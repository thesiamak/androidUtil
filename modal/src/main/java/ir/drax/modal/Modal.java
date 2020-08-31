package ir.drax.modal;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import ir.drax.modal.model.ModalObj;

public class Modal {
    public static enum Type {
        Alert,
        List,
        Progress,
        Custom
    }

    public static Builder builder(AppCompatActivity activity){
        return new Builder(activity);
    }

    public static boolean hide(Activity activity){
        return hide((ViewGroup) activity.findViewById(android.R.id.content));
    }

    static boolean hide(ViewGroup root){
        ViewGroup bg = (ViewGroup) getLastView(root);
        if (bg!=null){
            ModalBuilder modal = (ModalBuilder) bg.getChildAt(bg.getChildCount()-1);
            if (modal.closeModal(bg)) {
                return true;

            }else{
                return false;
            }
        }

        return false;
    }

    private static View getLastView(ViewGroup root) {
        View tmp =root.findViewWithTag(ModalObj.ViewIdTag);
        if (tmp!=null){
            tmp.setTag("tmp");
            View tmp2= getLastView(root);
            if (tmp2 != null){
                tmp.setTag(ModalObj.ViewIdTag);
                return tmp2;
            }else{
                tmp.setTag(ModalObj.ViewIdTag);
                return tmp;
            }
        }else
            return null;
    }

}
