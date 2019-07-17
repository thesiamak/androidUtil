package ir.drax.modal;

import android.support.v7.app.AppCompatActivity;

public class Modal {
    public static Builder builder(AppCompatActivity activity){
        return Builder.getInstance(activity);
    }


}
