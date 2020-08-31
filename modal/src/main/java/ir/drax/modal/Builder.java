package ir.drax.modal;

import android.app.Activity;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import ir.drax.modal.model.Listener;
import ir.drax.modal.model.MoButton;
import ir.drax.modal.model.ModalObj;
import ir.drax.modal.model.UnsatisfiedParametersException;

public class Builder {
    private ModalObj modalObj=new ModalObj();
    private Activity activity;
    public Builder(Activity activity) {
        this.activity=activity;
    }

    public Builder setDirection(int direction) {
        modalObj.setDirection(direction);
        return this;
    }

    public Builder setBlurEnabled(boolean blurEnabled) {
        modalObj.setBlurEnabled(blurEnabled);
        return this;
    }

    public Builder setLockVisibility(boolean lockVisibility) {
        modalObj.setLockVisibility(lockVisibility);
        return this;
    }

    public Builder setListener(Listener listener) {
        modalObj.setListener(listener);
        return this;
    }

    public Builder setType(Modal.Type type) {
        modalObj.setType(type);
        return this;
    }

    public Builder setCallback(MoButton reAction) {
        modalObj.setReAction(reAction);
        return this;
    }


    public Builder setTitle(CharSequence title) {
        modalObj.setTitle(title);
        return this;
    }

    public Builder setIcon(int icon) {
        modalObj.setIcon(icon);
        return this;
    }

    public Builder setContentView(int view) {
        modalObj.setContentView(view);
        return this;
    }

    public Builder setProgress(int progress) {
        modalObj.setProgress(progress);
        return this;
    }

    public Builder setMessage(CharSequence message) {
        modalObj.setMessage(new MoButton(message,0,null));
        return this;
    }

    public Builder setMessage(MoButton message) {
        modalObj.setMessage(message);
        return this;
    }

    public Builder setList(List<MoButton> list) {
        modalObj.setList(list);
        return  this;
    }
    public ModalBuilder build(){
        try {
            modalObj.setRoot((ViewGroup) activity.findViewById(android.R.id.content));

            int view=0;
            if (modalObj.getType().equals(Modal.Type.Alert))
                view=R.layout.modal_alert_layout;
            else if (modalObj.getType().equals(Modal.Type.List))
                view=R.layout.modal_list_layout;
            else if (modalObj.getType().equals(Modal.Type.Progress))
                view=R.layout.modal_progress_layout;
            else if (modalObj.getType().equals(Modal.Type.Custom) && modalObj.getContentView()>0)
                view=modalObj.getContentView();

            else {
                throw new UnsatisfiedParametersException();
            }

            View inflated=((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(view,null,false);

            if (modalObj.getType().equals(Modal.Type.Custom)){
                final RelativeLayout modal = new RelativeLayout(new ContextThemeWrapper(activity, R.style.modal_root));
                ViewGroup.LayoutParams modalLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                modal.addView(inflated,modalLayoutParams);
                modalObj.setModal(modal);

            }else
                modalObj.setModal(inflated);

            ModalBuilder builder= new ModalBuilder(modalObj);
            return builder;

        } catch (UnsatisfiedParametersException e) {
            e.printStackTrace();
            return null;
        }
    }
}
