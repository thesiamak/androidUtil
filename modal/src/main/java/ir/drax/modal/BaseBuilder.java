package ir.drax.modal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseBuilder{
    static BaseBuilder instance;
    AppCompatActivity activity;

    ViewGroup root;
    int direction = Direction.FromBottom;
    //private BlurView blurView;
    String ViewIdTag=getClass().getPackage().getName();
    boolean blurEnabled = false , lockVisibility=false;
    Listener listener;



    static BaseBuilder getInstance(AppCompatActivity activity){
        return instance;
    }


    void resetDefaults(BaseBuilder oldInstance) {
        if (oldInstance==null) {
            direction = Direction.FromBottom;
            blurEnabled = false;
            listener = null;

        }else{
            direction = oldInstance.getDirection();
            blurEnabled = oldInstance.blurEnabled;
            listener = oldInstance.listener;
        }
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

    boolean closeModal(View modal, final View bg) {
        if (lockVisibility)return false;

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

                        if (listener!=null)
                            listener.onDismiss();

                        super.onAnimationEnd(animation);
                    }
                })
                .start();
        return true;
    }

    public BaseBuilder setDirection(int direction){
        this.direction=direction;
        return this;
    }

    public boolean hide(){
        ViewGroup bg = (ViewGroup) getLastView(root);
        if (bg!=null){
            View modal = bg.getChildAt(bg.getChildCount()-1);
            if (closeModal(modal,bg)) {
                resetDefaults(null);
                return true;

            }else{
                return false;
            }
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

    public BaseBuilder setLockVisibility(boolean lockVisibility) {
        this.lockVisibility= lockVisibility;
        return this;
    }

    public ListBuilder typeList(){
        return ListBuilder.getInstance(activity,false);
    }

    public AlertBuilder typeAlert(){
        return AlertBuilder.getInstance(activity,false);
    }
    public ProgressBuilder typeProgress(){
        return ProgressBuilder.getInstance(activity,false);
    }

    public CustomBuilder typeCustomView(){
        return CustomBuilder.getInstance(activity,false);
    }

    void setViewDirection(View modal){
        if (((int)modal.getTag())==Direction.FromBottom)
            modal.getRootView().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.top_curved_header));
        else
            modal.getRootView().setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.bottom_curved_header));

    }

    public BaseBuilder setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public interface Listener{
        public void onDismiss();
        public void onShow();
    }
}
