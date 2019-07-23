package ir.drax.modal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ir.drax.modal.ListAlert.ListItem;

public abstract class BaseBuilder{
    static BaseBuilder instance;
    AppCompatActivity activity;

    ViewGroup root;
    int direction = Direction.FromBottom;
    //private BlurView blurView;
    String ViewIdTag=getClass().getPackage().getName();
    boolean blurEnabled = false , lockVisibility;


    static BaseBuilder getInstance(AppCompatActivity activity){
        return instance;
    }


    void resetDefaults() {
        direction = Direction.FromBottom ;
        blurEnabled=false;
    }

    void initBlurEffect() {
        /*blurView=new BlurView(activity);
        blurView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            blurView.setupWith(root)
                    //.windowBackground(root.getBackground())
                    .blurAlgorithm(new RenderScriptBlur(activity))
                    .blurRadius(2)
                    .setHasFixedTransformationMatrix(true);
        }

        root.addView(blurView);*/
    }

    void blurEffect(boolean set) {
        if (!blurEnabled)return;
        /*if (set) {
            blurView.setBlurEnabled(true);

        } else
            blurView.setBlurEnabled(false);*/

    }

    void closeModal(View modal, final View bg) {
        modal.animate()
                .translationY(
                        ((int)modal.getTag())==Direction.FromBottom?
                                modal.getHeight() : -modal.getHeight())
                .setDuration(250)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                            TransitionDrawable transitionDrawable = (TransitionDrawable) bg.getBackground();
                            transitionDrawable.reverseTransition(250);
                        }
                        root.removeView(bg);
                        blurEffect(false);
                        super.onAnimationEnd(animation);
                    }
                })
                .start();
    }

    public BaseBuilder setDirection(int direction){
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

    public BaseBuilder setBlurEnabled(boolean blurEnabled) {
        //if (blurEnabled && blurView==null)initBlurEffect();
        this.blurEnabled = blurEnabled;
        return this;
    }

    public ListBuilder typeList(){
        return ListBuilder.getInstance(activity,false);
    }

    public AlertBuilder typeAlert(){
        return AlertBuilder.getInstance(activity,false);
    }

    void setViewDirection(View modal){
        if (((int)modal.getTag())==Direction.FromBottom)
            modal.findViewById(R.id.modal_root).setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.top_curved_header));
        else
            modal.findViewById(R.id.modal_root).setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.bottom_curved_header));

    }
}
