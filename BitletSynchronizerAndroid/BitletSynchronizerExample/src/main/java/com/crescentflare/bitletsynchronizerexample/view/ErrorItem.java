package com.crescentflare.bitletsynchronizerexample.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crescentflare.bitletsynchronizerexample.R;

/**
 * A view for showing that an error occurred while loading a section
 */
public class ErrorItem extends LinearLayout
{
    // ---
    // Members
    // ---

    private TextView labelView;


    // ---
    // Initialization
    // ---

    public ErrorItem(Context context)
    {
        this(context, null);
    }

    public ErrorItem(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public ErrorItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs);
    }

    public ErrorItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        this(context, attrs);
    }

    private void init(AttributeSet attrs)
    {
        // Inflate layout
        LayoutInflater.from(getContext()).inflate(R.layout.view_error_item, this, true);
        setOrientation(HORIZONTAL);
        labelView = (TextView)findViewById(R.id.view_error_item_label);

        // Set values based on attributes
        if (attrs != null)
        {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ErrorItem);
            setLabel(a.getString(R.styleable.ErrorItem_error_label));
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
}
