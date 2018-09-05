package com.crescentflare.bitletsynchronizerexample.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crescentflare.bitletsynchronizerexample.R;

/**
 * Extends scrollview with a listener for scroll position changes on all supported API levels
 */
public class NotifyingScrollView extends ScrollView
{
    // ---
    // Members
    // ---

    private ScrollPositionChangedListener scrollPositionChangedListener;


    // ---
    // Initialization
    // ---

    public NotifyingScrollView(Context context)
    {
        this(context, null);
    }

    public NotifyingScrollView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NotifyingScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs);
    }

    public NotifyingScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        this(context, attrs);
    }


    // ---
    // Hook into scroll position changes
    // ---

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY)
    {
        super.onScrollChanged(x, y, oldX, oldY);
        if (scrollPositionChangedListener != null)
        {
            scrollPositionChangedListener.onScrollPositionChanged(x, y);
        }
    }


    // ---
    // Scroll position listener
    // ---

    public interface ScrollPositionChangedListener
    {
        void onScrollPositionChanged(int x, int y);
    }

    public ScrollPositionChangedListener getScrollPositionChangedListener()
    {
        return scrollPositionChangedListener;
    }

    public void setScrollPositionChangedListener(ScrollPositionChangedListener scrollPositionChangedListener)
    {
        this.scrollPositionChangedListener = scrollPositionChangedListener;
    }
}
