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
import android.widget.ImageView;
import android.widget.TextView;

public class AlertBuilder extends BaseBuilder {


    static AlertBuilder getInstance(AppCompatActivity activity,boolean resetDefaults) {

        if (!(instance instanceof AlertBuilder)){
            instance = new AlertBuilder(activity);
        }

        else if (activity.getClass() != instance.activity.getClass())
            instance = new AlertBuilder(activity);

        else if (resetDefaults)
            instance.resetDefaults();

        return (AlertBuilder) instance;

    }

    AlertBuilder(AppCompatActivity activity) {

        try {
            this.activity = activity;
            this.root = activity.findViewById(android.R.id.content);
            if (blurEnabled)
                initBlurEffect();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void show(CharSequence title ,CharSequence message, int icon){
        buildModal(title,message,icon);
    }
    public void show(CharSequence title ,CharSequence message){
        buildModal(title,message,0);
    }
    public void show(int title , int message){
        buildModal(activity.getString(title) , activity.getString(message),0);
    }
    public void show(CharSequence message){
        buildModal("",message,0);
    }
    public void show(int message){
        buildModal("",activity.getString(message),0);
    }


    void buildModal(CharSequence title, CharSequence message , int icon){

        final View modal = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.modal_alert_layout,null,false);
        modal.setTag(direction);
        setViewDirection(modal);

        modal.setVisibility(View.INVISIBLE);

        ((TextView)modal.findViewById(R.id.text)).setText(message);

        if (!title.toString().isEmpty())
            ((TextView)modal.findViewById(R.id.title)).setText(title);

        if (icon > 0)
            ((ImageView)modal.findViewById(R.id.icon)).setImageResource(icon);
        else
            modal.findViewById(R.id.icon).setVisibility(View.GONE);

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

                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                                    TransitionDrawable transitionDrawable = (TransitionDrawable) bg.getBackground();
                                    transitionDrawable.startTransition(250);
                                }else
                                    bg.setBackgroundColor(getActivity().getResources().getColor(R.color.modal_bg_dark_transparent));

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
}