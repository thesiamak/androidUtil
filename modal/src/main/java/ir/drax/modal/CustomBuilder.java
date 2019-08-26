package ir.drax.modal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class CustomBuilder extends BaseBuilder {


    static CustomBuilder getInstance(AppCompatActivity activity, boolean resetDefaults) {

        if (!(instance instanceof CustomBuilder)){
            instance = new CustomBuilder(activity,instance);
        }

        else if (activity.getClass() != instance.activity.getClass())
            instance = new CustomBuilder(activity, instance);

        if (resetDefaults)
            instance.resetDefaults(null);

        return (CustomBuilder) instance;

    }

    CustomBuilder(AppCompatActivity activity, BaseBuilder instance) {
        try {
            this.activity = activity;
            this.root = activity.findViewById(android.R.id.content);
            resetDefaults(instance);
            if (blurEnabled)
                initBlurEffect();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void show(View layout){
        buildModal(layout);
    }
    public void show(int view){
        try {
            View layout = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(view,null,false);
            buildModal(layout);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    void buildModal(View layout){
        try {

            final RelativeLayout modal = new RelativeLayout(new ContextThemeWrapper(activity, R.style.modal_root));
            ViewGroup.LayoutParams modalLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            modal.addView(layout,modalLayoutParams);

            modal.setTag(direction);
            setViewDirection(modal);

            modal.setVisibility(View.INVISIBLE);

            final FrameLayout bg = new FrameLayout(activity);
            bg.setTag(ViewIdTag);

            bg.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.modal_root_transition));

            bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeModal(modal, bg);
                }
            });
            bg.addView(modal,modalLayoutParams);

            root.addView(bg, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewTreeObserver obs = root.getViewTreeObserver();

                    FrameLayout.LayoutParams fLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    if (((int) modal.getTag()) == Direction.FromBottom) {
                        fLayoutParams.gravity = Gravity.BOTTOM;
                        fLayoutParams.bottomMargin = modal.getHeight() * -1;

                    } else {
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

                                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                                        TransitionDrawable transitionDrawable = (TransitionDrawable) bg.getBackground();
                                        transitionDrawable.startTransition(250);
                                    } else
                                        bg.setBackgroundColor(getActivity().getResources().getColor(R.color.modal_bg_dark_transparent));

                                    super.onAnimationStart(animation);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    blurEffect(true);
                                    if (listener != null)listener.onShow();
                                }
                            })
                            .translationY(
                                    ((int) modal.getTag()) == Direction.FromBottom ?
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
