package ir.drax.modal.model;

import android.arch.lifecycle.LiveData;


public class ProgressModel extends LiveData<ProgressModel> {
    private CharSequence title="";
    private CharSequence message ="";
    private int icon;
    private int progress;

    public ProgressModel(CharSequence title, CharSequence message, int icon, int progress) {
        this.title = title;
        this.message = message;
        this.icon = icon;
        this.progress = progress;
    }

    public CharSequence getTitle() {
        return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
        postValue(this);
    }

    public CharSequence getMessage() {
        return message;
    }

    public void setMessage(CharSequence message) {
        this.message = message;
        postValue(this);
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
        postValue(this);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        postValue(this);
    }
}