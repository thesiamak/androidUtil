package ir.drax.modal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.CycleInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.Observable;
import java.util.Observer;

import ir.drax.modal.model.Direction;
import ir.drax.modal.model.MoButton;
import ir.drax.modal.model.ModalObj;

public class ModalBuilder extends RelativeLayout implements Observer, androidx.lifecycle.Observer<ModalObj> {
    private ViewGroup root;

    private ModalObj modalObj;
    private FrameLayout bg;

    ModalBuilder(ModalObj data) {
        super(data.getRoot().getContext());

        this.modalObj = data;
        this.root = modalObj.getRoot();

        addView(modalObj.getModal(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (modalObj.isBlurEnabled())
            initBlurEffect();
    }

    public void show(){
        buildModal();
        modalObj.observeForever( this);
    }

    public void hide(){
        if (bg != null)
            closeModal(bg);
    }

    private void buildModal(){

        if (bg == null) {
            bg = new FrameLayout(getContext());
            bg.setTag(modalObj.getViewIdTag());

            setTag(modalObj.getDirection());
            setViewDirection();

            setVisibility(View.INVISIBLE);

            setHeader(bg);

            if (modalObj.getType().equals(Modal.Type.Progress))
                setProgress();

            setReaction();


            if (modalObj.getType().equals(Modal.Type.List))
                setList();

            bg.setBackgroundDrawable(getResources().getDrawable(R.drawable.modal_root_transition));

            bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeModal(bg);
                }
            });

            bg.addView(this);
        }

        root.addView(bg,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = root.getViewTreeObserver();

                FrameLayout.LayoutParams fLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
                if (((int)getTag())== Direction.FromBottom) {
                    fLayoutParams.gravity = Gravity.BOTTOM;
                    fLayoutParams.bottomMargin = getHeight() * -1;

                }else{
                    fLayoutParams.gravity = Gravity.TOP;
                    fLayoutParams.topMargin = getHeight() * -1;
                }
                setLayoutParams(fLayoutParams);
                animate()
                        .setStartDelay(250)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                setVisibility(View.VISIBLE);

                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                                    TransitionDrawable transitionDrawable = (TransitionDrawable) bg.getBackground();
                                    transitionDrawable.startTransition(250);
                                }else
                                    bg.setBackgroundColor(getResources().getColor(R.color.modal_bg_dark_transparent));

                                super.onAnimationStart(animation);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (modalObj.getListener() != null)modalObj.getListener().onShow();

                                if (!modalObj.getType().equals(Modal.Type.Custom))
                                    findViewById(R.id.ok)
                                            .animate()
                                            .translationY(
                                                    ((int)getTag())==Direction.FromBottom?
                                                            -16:0)
                                            .setDuration(400)
                                            .setInterpolator(new CycleInterpolator(0.1f))
                                            .start();


                                super.onAnimationEnd(animation);
                                blurEffect(true);
                            }
                        })
                        .translationY(
                                ((int)getTag())==Direction.FromBottom?
                                        -getHeight() : getHeight())
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

    private void setProgress() {
        if (modalObj.getType().equals(Modal.Type.Custom))return;

        ProgressBar progress = findViewById(R.id.progress);
        ((TextView)findViewById(R.id.percentage)).setText(modalObj.getProgress()+"%");

        progress.setIndeterminate(modalObj.getProgress()==0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progress.setProgress(modalObj.getProgress(),true);

        }else progress.setProgress(modalObj.getProgress());
    }

    private void setList() {
        if (modalObj.getType().equals(Modal.Type.Custom))return;

        TextView doneBtnView = findViewById(R.id.ok);
        ViewGroup listHolder =  findViewById(R.id.listItems);
        LinearLayout.LayoutParams itemLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        for (final MoButton listItem : modalObj.getList()) {
            TextView textView = new TextView(getContext());
            textView.setText(listItem.getText());
            //textView.setText(Html.fromHtml(""));
            textView.setTextColor(getResources().getColor(R.color.black_faded));
            textView.setTypeface(doneBtnView.getTypeface());
            textView.setCompoundDrawablesWithIntrinsicBounds(0,0,listItem.getIcon(),0);
            textView.setCompoundDrawablePadding(8);
            //textView.tint(activity.getResources().getColorStateList();
            //textView.getCompoundDrawables()[0].setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), R.color.grey), PorterDuff.Mode.SRC_IN));

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listItem.getClickListener()!=null)listItem.getClickListener().onClick(v);
                }
            });
            //textView.setBackgroundColor(activity.getResources().getColor(listHolder.getChildCount()%2==0?R.color.modal_list_item_odd:R.color.modal_list_item_even));
            //textView.setBackgroundResource(R.drawable.list_item_bg);
            textView.setPadding(32,16,32,16);

            listHolder.addView(textView,itemLP);

            View divider = new View(getContext());
            divider.setBackgroundColor(getResources().getColor(R.color.black_faded));
            listHolder.addView(divider,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,5));
        }
    }

    private void setReaction() {
        if (modalObj.getType().equals(Modal.Type.Custom))return;

        TextView doneBtnView = findViewById(R.id.ok);
        if (modalObj.getReAction() != null) {
            doneBtnView.setText(modalObj.getReAction().getText());
            doneBtnView.setCompoundDrawablesWithIntrinsicBounds(modalObj.getReAction().getIcon(),0,0,0);
            doneBtnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (modalObj.getReAction().getClickListener()!=null)
                        if(modalObj.getReAction().getClickListener().onClick(v))
                            closeModal(bg);
                }
            });
            //doneBtnView.getCompoundDrawables()[0].setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(doneBtnView.getContext(), R.color.grey), PorterDuff.Mode.SRC_IN));
        }
    }

    private void setHeader(final FrameLayout bg) {
        if (modalObj.getType().equals(Modal.Type.Custom))return;

        if (!modalObj.getMessage().getText().toString().isEmpty()) {
            final TextView summary = findViewById(R.id.text);
            summary.setText(modalObj.getMessage().getText());
            summary.setCompoundDrawablesWithIntrinsicBounds(modalObj.getMessage().getIcon(),0,0,0);
            summary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (modalObj.getMessage().getClickListener()!=null)
                        if (modalObj.getMessage().getClickListener().onClick(v))
                            closeModal(bg);
                }
            });
        }


        if (!modalObj.getTitle().toString().isEmpty())
            ((TextView)findViewById(R.id.title)).setText(modalObj.getTitle());

        if (modalObj.getIcon() > 0)
            ((ImageView)findViewById(R.id.icon)).setImageResource(modalObj.getIcon());
        else
            findViewById(R.id.icon).setVisibility(View.GONE);

    }

    void setViewDirection(){
        if (((int)getTag())==Direction.FromBottom)
            setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.top_curved_header,null));
        else
            setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.bottom_curved_header,null));

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
        if (!modalObj.isBlurEnabled())return;
        /*if (set) {
            blurView.setBlurEnabled(true);

        } else
            blurView.setBlurEnabled(false);*/

    }

    boolean closeModal( final View bg) {
        if (modalObj.isLockVisibility())return false;

        animate()
                .translationY(
                        ((int)getTag())==Direction.FromBottom?
                                getHeight() : -getHeight())
                .setDuration(250)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                            TransitionDrawable transitionDrawable = (TransitionDrawable) bg.getBackground();
                            transitionDrawable.reverseTransition(250);
                        }
                        modalObj.removeObserver(ModalBuilder.this);
                        root.removeView(bg);
                        blurEffect(false);

                        if (modalObj.getListener()!=null)
                            modalObj.getListener().onDismiss();

                        super.onAnimationEnd(animation);
                    }
                })
                .start();
        return true;
    }

    public ModalObj state(){
        return modalObj;
    }

    @Override
    public void onChanged(ModalObj modalObj) {
        switch (modalObj.getChangedIndex()){
            case 0: case 1: case 2:
                setHeader(bg);break;

            case 3:
                setReaction();break;

            case 4:
                setList();break;

            case 5:
                setProgress();break;

        }
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}
