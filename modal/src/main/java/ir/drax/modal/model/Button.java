package ir.drax.modal.model;

import android.view.View;

public class Button {
    private CharSequence title;
    private int icon;
    private View.OnClickListener clickListener;

    public Button(CharSequence title, int icon, View.OnClickListener clickListener) {
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

    public View.OnClickListener getClickListener() {
        return clickListener;
    }
}
