package ir.drax.modal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.CycleInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ir.drax.modal.model.MoButton;

public class ListBuilder extends BaseBuilder{

    static ListBuilder getInstance(AppCompatActivity activity,boolean resetDefaults) {

        if (!(instance instanceof ListBuilder)){
            instance = new ListBuilder(activity,instance);

        } else if (activity.getClass() != instance.activity.getClass())
            instance = new ListBuilder(activity,instance);

        if (resetDefaults)
            instance.resetDefaults(null);

        return (ListBuilder) instance;

    }

    ListBuilder(AppCompatActivity activity, BaseBuilder settings) {

        try {
            this.activity = activity;
            this.root = activity.findViewById(android.R.id.content);
            resetDefaults(settings);
            if (blurEnabled)
                initBlurEffect();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void show(String title, int icon , List<MoButton> list, MoButton summaryMessage , MoButton doneBtn){

        buildModal(title,icon,list,summaryMessage,doneBtn);
    }

    void buildModal(CharSequence title, int icon , List<MoButton> list, MoButton summaryMessage , final MoButton doneBtn){

        final View modal = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.modal_list_layout,null,false);
        modal.setTag(direction);
        setViewDirection(modal);
        modal.setVisibility(View.INVISIBLE);

        if (!summaryMessage.toString().isEmpty()) {
            TextView summary = modal.findViewById(R.id.text);
            summary.setText(summaryMessage.getTitle());
            summary.setCompoundDrawablesWithIntrinsicBounds(summaryMessage.getIcon(),0,0,0);
            //summary.getCompoundDrawables()[0].setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(summary.getContext(), R.color.grey), PorterDuff.Mode.SRC_IN));

        }
        if (!title.toString().isEmpty())
            ((TextView)modal.findViewById(R.id.title)).setText(title);

        if (icon!=0)
            ((ImageView)modal.findViewById(R.id.icon)).setImageResource(icon);

        TextView doneBtnView = modal.findViewById(R.id.action);
        if (doneBtn != null) {
            doneBtnView.setText(doneBtn.getTitle());
            doneBtnView.setCompoundDrawablesWithIntrinsicBounds(doneBtn.getIcon(),0,0,0);
            doneBtnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doneBtn.getClickListener().onClick(v);
                }
            });
            //doneBtnView.getCompoundDrawables()[0].setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(doneBtnView.getContext(), R.color.grey), PorterDuff.Mode.SRC_IN));
        }

        ViewGroup listHolder =  modal.findViewById(R.id.listItems);
        LinearLayout.LayoutParams itemLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        for (final MoButton listItem : list) {
            TextView textView = new TextView(activity);
            textView.setText(listItem.getTitle());
            //textView.setText(Html.fromHtml(""));
            textView.setTextColor(activity.getResources().getColor(R.color.black_faded));
            textView.setTypeface(doneBtnView.getTypeface());
            textView.setCompoundDrawablesWithIntrinsicBounds(0,0,listItem.getIcon(),0);
            textView.setCompoundDrawablePadding(8);
            //textView.tint(activity.getResources().getColorStateList();
            //textView.getCompoundDrawables()[0].setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), R.color.grey), PorterDuff.Mode.SRC_IN));

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listItem.getClickListener().onClick(v);
                }
            });
            //textView.setBackgroundColor(activity.getResources().getColor(listHolder.getChildCount()%2==0?R.color.modal_list_item_odd:R.color.modal_list_item_even));
            //textView.setBackgroundResource(R.drawable.list_item_bg);
            textView.setPadding(32,16,32,16);

            listHolder.addView(textView,itemLP);

            View divider = new View(activity);
            divider.setBackgroundColor(getActivity().getResources().getColor(R.color.black_faded));
            listHolder.addView(divider,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,5));
        }


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
                                View ok = modal.findViewById(R.id.action);
                                ok.animate()
                                        .translationY(
                                                ((int)modal.getTag())==Direction.FromBottom?
                                                        -16:0)
                                        .setDuration(400)
                                        .setInterpolator(new CycleInterpolator(0.1f))
                                        .start();

                                if (listener != null)listener.onShow();

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
