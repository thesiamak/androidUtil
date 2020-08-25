package ir.drax.expandable;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class Expandable extends ConstraintLayout implements Observer {
    private StateObservable observableState=new StateObservable(false);
    private final int COLLAPSE_DURATION=250, EXPAND_DURATION=320;
    private final int MARGIN_START=16;
    private int expandedHeight=0, collapsedHeight=0;
    private List<View> childs=new ArrayList<>();
    private int[] drawables=new int[2];


    public Expandable(Context context) {
        super(context);
    }

    public Expandable(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Expandable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init(){
        Log.e("exxxxxxxxx","init");
        LayoutInflater.from(getContext()).inflate(R.layout.expandable_layout,this,true);
        observableState.addObserver(this);
        findViewById(R.id.header).setOnClickListener(v->{
            observableState.setState(!observableState.getState());
        });


        addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int top, int i2, int bottom, int i4, int i5, int i6, int oldBottom) {
                int newHeight= bottom-top;
                Log.e("exxxxxxxxx","changed:"+newHeight);

                if (collapsedHeight == 0) {
                    collapsedHeight = newHeight;
                    expandedHeight = newHeight;

                    for (View v : childs)
                        v.setVisibility(VISIBLE);


                } else if (newHeight > expandedHeight) {
                    expandedHeight = newHeight;
                    removeOnLayoutChangeListener(this);
                    collapse();
                }

            }
        });
    }

    private void expand(){
        findViewById(R.id.header_icon).setBackground(ResourcesCompat.getDrawable(getResources(),drawables[0],null));

        ResizeAnimation resizeAnimation = new ResizeAnimation(this, expandedHeight, collapsedHeight,interpolate -> {
//            Log.e("SS",interpolate+"");
//            for (int i = 1; i < root.getChildCount(); i++) {
//                root.getChildAt(i).setAlpha(interpolate);;
//            }
        });
        resizeAnimation.setDuration(EXPAND_DURATION);
        resizeAnimation.setStartOffset(100);
        startAnimation(resizeAnimation);

    }

    private void collapse(){
        findViewById(R.id.header_icon).setBackground(ResourcesCompat.getDrawable(getResources(),drawables[1],null));

        if (collapsedHeight > 0) {
            ResizeAnimation resizeAnimation = new ResizeAnimation(this, collapsedHeight, expandedHeight,
                    interpolate -> {
                    });
            resizeAnimation.setDuration(COLLAPSE_DURATION);
            startAnimation(resizeAnimation);
        }

    }


    @Override
    public void update(Observable observable, Object o) {
        if((Boolean) o)
            expand();
        else
            collapse();

    }


    public Expandable addChild(@NonNull View...  views){
        Log.e("exxxxxxxxx","addchild");

        Collections.addAll(childs, views);
        drawChild();
        return this;
    }

    private void drawChild(){
        for (View view : childs) {
            LayoutParams params=new Constraints.LayoutParams(0,LayoutParams.WRAP_CONTENT);
            params.setMarginStart(MARGIN_START);
            params.startToStart=LayoutParams.PARENT_ID;
            params.endToEnd=LayoutParams.PARENT_ID;
            if (getChildCount() > 1)
                params.topMargin=MARGIN_START;

            params.topToBottom=getChildAt(getChildCount()-1).getId();


            view.setVisibility(GONE);
            if (view.getId() == -1)
                view.setId((int) System.currentTimeMillis()+getChildCount());

            addView(view,params);
        }
    }

    public void setDrawables(int enabledDrawable,int disabledDrawable) {
        this.drawables =new int[] {enabledDrawable,disabledDrawable};
    }
}
