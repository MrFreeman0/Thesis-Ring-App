package fi.delektre.ringa.ring_thesis.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class AppBarTransparentScrollingViewBehavior extends AppBarLayout.ScrollingViewBehavior {
    Context ctx;

    public AppBarTransparentScrollingViewBehavior(Context context, AttributeSet attrs){
        super(context, attrs);
        ctx = context;
    }

    public AppBarTransparentScrollingViewBehavior(){
        super();
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child,
                                          View dependency) {
        updateOffset(parent, child, dependency);
        return false;
    }

    private boolean updateOffset(CoordinatorLayout parent, View child,
                                 View dependency) {
        final CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) dependency
                .getLayoutParams()).getBehavior();
        if (behavior instanceof CoordinatorLayout.Behavior) {
            // Offset the child so that it is below the app-bar (with any
            // overlap)

            final int offset = -(int)dpToPx(100);

            setTopAndBottomOffset(offset);
            return true;
        }
        return false;
    }

    public float dpToPx(float dp){
        float px;
        Resources r = ctx.getResources();
        px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
        return px;
    }

}

