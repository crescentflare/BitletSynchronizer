package com.crescentflare.bitletsynchronizerexample.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crescentflare.bitletsynchronizerexample.R;

/**
 * A view for showing usage or server items
 */
public class ListItem extends LinearLayout
{
    // ---
    // Members
    // ---

    private TextView labelView;
    private TextView additionalView;
    private TextView valueView;


    // ---
    // Initialization
    // ---

    public ListItem(Context context)
    {
        this(context, null);
    }

    public ListItem(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public ListItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs);
    }

    public ListItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        this(context, attrs);
    }

    private void init(AttributeSet attrs)
    {
        // Inflate layout
        LayoutInflater.from(getContext()).inflate(R.layout.view_list_item, this, true);
        setOrientation(HORIZONTAL);
        labelView = (TextView)findViewById(R.id.view_list_item_label);
        additionalView = (TextView)findViewById(R.id.view_list_item_additional);
        valueView = (TextView)findViewById(R.id.view_list_item_value);

        // Set values based on attributes
        if (attrs != null)
        {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ListItem);
            setLabel(a.getString(R.styleable.ListItem_item_label));
            setAdditional(a.getString(R.styleable.ListItem_item_additional));
            setValue(a.getString(R.styleable.ListItem_item_value));
            a.recycle();
        }
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params)
    {
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        super.setLayoutParams(params);
    }


    // ---
    // Set values
    // ---

    public void setLabel(String label)
    {
        labelView.setText(label);
    }

    public void setAdditional(String additional)
    {
        additionalView.setText(additional);
        additionalView.setVisibility(TextUtils.isEmpty(additional) ? GONE : VISIBLE);
    }

    public void setValue(String value)
    {
        valueView.setText(value);
        valueView.setVisibility(TextUtils.isEmpty(value) ? GONE : VISIBLE);
    }
}
