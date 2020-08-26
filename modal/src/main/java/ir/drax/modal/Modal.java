package ir.drax.modal;


import androidx.appcompat.app.AppCompatActivity;

public class Modal {
    public static AlertBuilder builder(AppCompatActivity activity){
        return AlertBuilder.getInstance(activity,true);
    }
}
