package ir.drax.modal;

import android.support.v7.app.AppCompatActivity;

public class Modal {
    public static AlertBuilder builder(AppCompatActivity activity){
        return AlertBuilder.getInstance(activity,true);
    }
}
