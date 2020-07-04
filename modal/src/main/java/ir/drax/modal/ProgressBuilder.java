package ir.drax.modal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.CycleInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ir.drax.modal.model.ProgressModel;

public class ProgressBuilder extends BaseBuilder {

    private ProgressModel model;

    static ProgressBuilder getInstance(AppCompatActivity activity, boolean resetDefaults) {

        if (!(instance instanceof ProgressBuilder)){
            instance = new ProgressBuilder(activity,instance);
        }

        else if (activity.getClass() != instance.activity.getClass())
            instance = new ProgressBuilder(activity,instance);

        if (resetDefaults)
            instance.resetDefaults(null);

        return (ProgressBuilder) instance;

    }

    ProgressBuilder(AppCompatActivity activity, BaseBuilder settings) {

        try {
            this.activity = activity;
            this.root = activity.findViewById(android.R.id.content);
            this.lockVisibility = true;
            resetDefaults(settings);
            if (blurEnabled)
                initBlurEffect();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public ProgressBuilder show(CharSequence title ,CharSequence message, int icon,int progress){
        model=new ProgressModel(title,message,icon,progress);
        return buildModal();
    }
    public ProgressBuilder show(CharSequence title ,CharSequence message, int icon){
        model=new ProgressModel(title,message,icon,0);
        return buildModal();
    }
    public ProgressBuilder show(CharSequence title ,CharSequence message){
        model=new ProgressModel(title , message,0,0);
        return buildModal();
    }
    public ProgressBuilder show(int title , int message){
        model=new ProgressModel(activity.getString(title) , activity.getString(message),0,0);
        return buildModal();
    }
    public ProgressBuilder show(CharSequence message){
        model=new ProgressModel("",message,0,0);
        return buildModal();
    }
    public ProgressBuilder show(int message){
        model=new ProgressModel("",activity.getString(message),0,0);
        return buildModal();
    }
    public ProgressBuilder show(){
        if (model!=null)
            return buildModal();

        else return null;
    }

    ProgressBuilder buildModal(){

        final View modal = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.modal_progress_layout,null,false);
        modal.setTag(direction);
        setViewDirection(modal);

        modal.setVisibility(View.INVISIBLE);

        preset(modal);

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
                if (((int)modal.getTag())== Direction.FromBottom) {
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
                                ProgressBar progress = modal.findViewById(R.id.progress);
                                /*progress.animate()
                                        .translationY(
                                                ((int)modal.getTag())== Direction.FromBottom?
                                                        -16:0)
                                        .setDuration(400)
                                        .setInterpolator(new AccelerateDecelerateInterpolator())
                                        .start();*/
                                if (listener != null)listener.onShow();

                                super.onAnimationEnd(animation);
                                blurEffect(true);
                            }
                        })
                        .translationY(
                                ((int)modal.getTag())== Direction.FromBottom?
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

        model.observe(activity, new Observer<ProgressModel>() {
            @Override
            public void onChanged(@Nullable ProgressModel progressModel) {
                preset(modal);
            }
        });

        return this;
    }
    private void preset(View modal){
        ((TextView)modal.findViewById(R.id.text)).setText(model.getMessage());
        ((TextView)modal.findViewById(R.id.percentage)).setText(model.getProgress()+"%");

        if (!model.getTitle().toString().isEmpty())
            ((TextView)modal.findViewById(R.id.title)).setText(model.getTitle());

        if (model.getIcon() > 0)
            ((ImageView)modal.findViewById(R.id.icon)).setImageResource(model.getIcon());
        else
            modal.findViewById(R.id.icon).setVisibility(View.GONE);

        ProgressBar progress = modal.findViewById(R.id.progress);

        progress.setIndeterminate(model.getProgress()==0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progress.setProgress(model.getProgress(),true);

        }else progress.setProgress(model.getProgress());
    }

    public ProgressBuilder setProgress(int progress){
        model.setProgress(progress);
        return this;
    }
    public int getProgress(){
        return model.getProgress();
    }

}
