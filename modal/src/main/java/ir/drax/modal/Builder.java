package ir.drax.modal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.CycleInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class Builder {
    private static Builder instance;
    private AppCompatActivity activity;

    private ViewGroup root;
    private int direction = Direction.FromBottom;
    private BlurView blurView;
    private String ViewIdTag=getClass().getCanonicalName();
    private boolean blurEnabled = false;

    static Builder getInstance(AppCompatActivity activity){
        if (instance==null){
            instance = new Builder(activity);
        }

        else if (activity.getClass() != instance.activity.getClass())
            instance = new Builder(activity);

        else
            instance.resetDefaults();

        return instance;
    }

    private void resetDefaults() {
        direction = Direction.FromBottom ;
        blurEnabled=false;
    }

    private Builder(AppCompatActivity activity) {
        try {
            this.activity = activity;
            root= activity.findViewById(android.R.id.content);
            if (blurEnabled)
                initBlurEffect();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void show(String message){
        buildModal(message);
    }
    public void show(int message){
        buildModal(activity.getString(message));
    }

    private void buildModal(String message){

        final View modal = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.modal_alert_layout,null,false);
        modal.setTag(direction);
        if (((int)modal.getTag())==Direction.FromBottom)
            modal.findViewById(R.id.modal_root).setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.top_curved_header));
        else
            modal.findViewById(R.id.modal_root).setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.bottom_curved_header));

        modal.setVisibility(View.INVISIBLE);

        ((TextView)modal.findViewById(R.id.text)).setText(message);

        final FrameLayout bg = new FrameLayout(activity);
        bg.setTag(ViewIdTag);

        bg.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.modal_root_transition));

        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeModal(modal,bg);
            }
        });

        bg.addView(modal);

        root.addView(bg,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = root.getViewTreeObserver();

                FrameLayout.LayoutParams fLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
                if (((int)modal.getTag())==Direction.FromBottom) {
                    fLayoutParams.gravity = Gravity.BOTTOM;
                    fLayoutParams.bottomMargin = modal.getHeight() * -1;

                }else{
                    fLayoutParams.gravity = Gravity.TOP;
                    fLayoutParams.topMargin = modal.getHeight() * -1;
                }
                modal.setLayoutParams(fLayoutParams);
                modal.animate()
                        .setStartDelay(250)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                modal.setVisibility(View.VISIBLE);
                                TransitionDrawable transitionDrawable= (TransitionDrawable) bg.getBackground();
                                transitionDrawable.startTransition(250);
                                super.onAnimationStart(animation);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                View ok = modal.findViewById(R.id.ok);
                                ok.animate()
                                        .translationY(
                                                ((int)modal.getTag())==Direction.FromBottom?
                                                        -16:0)
                                        .setDuration(400)
                                        .setInterpolator(new CycleInterpolator(0.1f))
                                        .start();
                                super.onAnimationEnd(animation);
                                blurEffect(true);
                            }
                        })
                        .translationY(
                                ((int)modal.getTag())==Direction.FromBottom?
                                        -modal.getHeight() : modal.getHeight())
                        .setDuration(500)
                        .start();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    private void initBlurEffect() {
        blurView=new BlurView(activity);
        blurView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            blurView.setupWith(root)
                    //.windowBackground(root.getBackground())
                    .blurAlgorithm(new RenderScriptBlur(activity))
                    .blurRadius(2)
                    .setHasFixedTransformationMatrix(true);
        }

        root.addView(blurView);
    }

    private void blurEffect(boolean set) {
        if (!blurEnabled)return;
        if (set) {
            blurView.setBlurEnabled(true);

        } else
            blurView.setBlurEnabled(false);

    }

    private void closeModal(View modal, final View bg) {
        modal.animate()
                .translationY(
                        ((int)modal.getTag())==Direction.FromBottom?
                                modal.getHeight() : -modal.getHeight())
                .setDuration(250)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        TransitionDrawable transitionDrawable= (TransitionDrawable) bg.getBackground();
                        transitionDrawable.reverseTransition(250);
                        root.removeView(bg);
                        blurEffect(false);
                        super.onAnimationEnd(animation);
                    }
                })
                .start();
    }

    public Builder setDirection(int direction){
        //if (direction==Direction.FromBottom)
        this.direction=direction;

        return this;
    }

    public boolean hide(){
        View bg = getLastView(root);
        if (bg!=null){
            View modal = bg.findViewById(R.id.modal_root);
            closeModal(modal,bg);
            return true;
        }
        return false;
    }

    private View getLastView(ViewGroup root) {
        View tmp =root.findViewWithTag(ViewIdTag);
        if (tmp!=null){
            tmp.setTag("tmp");
            View tmp2= getLastView(root);
            if (tmp2 != null){
                tmp.setTag(ViewIdTag);
                return tmp2;
            }else{
                tmp.setTag(ViewIdTag);
                return tmp;
            }
        }else
            return null;
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    public ViewGroup getRoot() {
        return root;
    }

    public int getDirection() {
        return direction;
    }

    public boolean isBlurEnabled() {
        return blurEnabled;
    }

    public Builder setBlurEnabled(boolean blurEnabled) {
        if (blurEnabled && blurView==null)initBlurEffect();
        this.blurEnabled = blurEnabled;
        return this;
    }
}
