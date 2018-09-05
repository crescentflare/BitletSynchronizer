package com.crescentflare.bitletsynchronizerexample.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crescentflare.bitletsynchronizer.bitlet.BitletResultObserver;
import com.crescentflare.bitletsynchronizer.synchronizer.BitletSynchronizer;
import com.crescentflare.bitletsynchronizerexample.R;
import com.crescentflare.bitletsynchronizerexample.Settings;
import com.crescentflare.bitletsynchronizerexample.model.session.Session;
import com.crescentflare.bitletsynchronizerexample.network.Api;

/**
 * The login activity shows a demo of a user authentication screen with a field to configure the server address
 */
public class LoginActivity extends AppCompatActivity
{
    // ---
    // Members
    // ---

    private boolean continueEnabled = true;


    // ---
    // Initialization
    // ---

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Set layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.login_title));

        // Show popup with more information when clicking on the link
        findViewById(R.id.activity_login_more_info).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(R.string.login_more_info_title)
                        .setMessage(R.string.login_more_info_text)
                        .setPositiveButton(R.string.login_more_info_button, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int button)
                            {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        });

        // Listen for changes on the credential text fields to enable or disable the continue button
        final EditText serverAddressField = (EditText)findViewById(R.id.activity_login_server_address);
        final EditText usernameField = (EditText)findViewById(R.id.activity_login_username);
        final EditText passwordField = (EditText)findViewById(R.id.activity_login_password);
        usernameField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                // No implementation
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                // No implementation
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                checkEnteredCredentials();
            }
        });
        passwordField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                // No implementation
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                // No implementation
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                checkEnteredCredentials();
            }
        });

        // Apply pre-filled fields
        serverAddressField.setText(Settings.instance.getServerAddress());
        usernameField.setText(Settings.instance.getLastLoggedInUser());
        checkEnteredCredentials();

        // Log in when pressing the done button on the keyboard when editing the password field
        passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int action, KeyEvent keyEvent)
            {
                if (action == EditorInfo.IME_ACTION_DONE)
                {
                    login(usernameField.getText(), passwordField.getText());
                    return true;
                }
                return false;
            }
        });
    }


    // ---
    // State handling
    // ---

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        ((EditText)findViewById(R.id.activity_login_password)).setText("");
        enableViews(true);
        checkEnteredCredentials();
    }


    // ---
    // Menu handling
    // ---

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.menu_login_continue).setEnabled(continueEnabled);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.menu_login_continue)
        {
            login(((EditText)findViewById(R.id.activity_login_username)).getText(), ((EditText)findViewById(R.id.activity_login_password)).getText());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // ---
    // Data handling
    // ---

    private void login(final CharSequence username, CharSequence password)
    {
        Settings.instance.setServerAddress(((EditText)findViewById(R.id.activity_login_server_address)).getText().toString());
        enableViews(false);
        BitletSynchronizer.instance.load(Session.bitletInstance(username.toString(), password.toString()), new BitletResultObserver.SimpleCompletionListener<Session>()
        {
            @Override
            public void onFinish(Session session, Throwable exception)
            {
                if (exception != null)
                {
                    Toast.makeText(LoginActivity.this, getString(R.string.error_generic_title) + ":\n" + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    enableViews(true);
                }
                else if (session != null)
                {
                    // Store user
                    Api.getInstance(Settings.instance.getServerAddress()).setCookie(session.getCookie());
                    Settings.instance.setLastLoggedInUser(username.toString());

                    // Launch overview screen
                    Intent intent = new Intent(LoginActivity.this, OverviewActivity.class);
                    startActivityForResult(intent, 0); // Request code ignored
                }
            }
        });
    }


    // ---
    // View helper
    // ---

    private void enableViews(boolean enabled)
    {
        findViewById(R.id.activity_login_more_info).setEnabled(enabled);
        findViewById(R.id.activity_login_server_address).setEnabled(enabled);
        findViewById(R.id.activity_login_username).setEnabled(enabled);
        findViewById(R.id.activity_login_password).setEnabled(enabled);
        continueEnabled = enabled;
        invalidateOptionsMenu();
    }

    private void checkEnteredCredentials()
    {
        final EditText usernameField = (EditText)findViewById(R.id.activity_login_username);
        final EditText passwordField = (EditText)findViewById(R.id.activity_login_password);
        boolean enabled = usernameField.getText().length() > 0 && passwordField.getText().length() > 0;
        if (enabled != continueEnabled)
        {
            continueEnabled = enabled;
            invalidateOptionsMenu();
        }
    }
}
