package ir.drax.modal.model;

import android.view.View;

public class MoButton {
    private CharSequence text;
    private int icon;
    private OnClickListener clickListener;

    public MoButton(CharSequence text, int icon, OnClickListener clickListener) {
        this.text = text;
        this.icon = icon;
        this.clickListener = clickListener;
    }

    public CharSequence getText() {
        return text;
    }

    public int getIcon() {
        return icon;
    }

    public OnClickListener getClickListener() {
        return clickListener;
    }

    public interface  OnClickListener{
        public boolean onClick(View v);
    }
}
