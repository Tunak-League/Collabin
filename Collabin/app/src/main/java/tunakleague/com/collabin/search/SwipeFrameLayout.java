package tunakleague.com.collabin.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;


/*Custom FrameLayout that consumes all touch events (children of this view do not get any)*/
public class SwipeFrameLayout extends FrameLayout {


    public SwipeFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
