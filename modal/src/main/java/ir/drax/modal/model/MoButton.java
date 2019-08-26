package ir.drax.modal.model;

import android.view.View;

public class MoButton {
    private CharSequence title;
    private int icon;
    private OnClickListener clickListener;

    public MoButton(CharSequence title, int icon, OnClickListener clickListener) {
        this.title = title;
        this.icon = icon;
        this.clickListener = clickListener;
    }

    public CharSequence getTitle() {
        return title;
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
