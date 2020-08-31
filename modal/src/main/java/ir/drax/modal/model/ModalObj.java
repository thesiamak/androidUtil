package ir.drax.modal.model;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ModalObj extends LiveData<ModalObj> {
    private CharSequence title="";
    private MoButton message=new MoButton("",0,null);
    private int icon;
    private MoButton reAction;
    private int direction = Direction.BottomToTop;
    private ViewGroup root;
    public static String ViewIdTag= ModalObj.class.getSimpleName();
    private boolean blurEnabled = false;
    private boolean lockVisibility=false;
    private Listener listener;
    private Enum<Modal.Type> type=Modal.Type.Alert;
    private View modal;
    private int contentView;
    private List<MoButton> list;
    private int progress = 0;
    private int changedIndex=-1;


    public void setTitle(CharSequence title) {
        this.title = title;
        postValue(setChangedIndex(0));
    }

    public void setMessage(MoButton message) {
        this.message = message;
        postValue(setChangedIndex(1));
    }

    public void setIcon(int icon) {
        this.icon = icon;
        postValue(setChangedIndex(2));
    }

    public void setReAction(MoButton reAction) {
        this.reAction = reAction;
        postValue(setChangedIndex(3));
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setRoot(ViewGroup root) {
        this.root = root;
    }

    public void setBlurEnabled(boolean blurEnabled) {
        this.blurEnabled = blurEnabled;
    }

    public void setLockVisibility(boolean lockVisibility) {
        this.lockVisibility = lockVisibility;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setType(Enum<Modal.Type> type) {
        this.type = type;
    }

    public void setModal(View modal) {
        this.modal = modal;
    }

    public CharSequence getTitle() {
        return title;
    }

    public MoButton getMessage() {
        return message;
    }

    public int getIcon() {
        return icon;
    }

    public MoButton getReAction() {
        return reAction;
    }

    public int getDirection() {
        return direction;
    }

    public ViewGroup getRoot() {
        return root;
    }

    public String getViewIdTag() {
        return ViewIdTag;
    }

    public boolean isBlurEnabled() {
        return blurEnabled;
    }

    public boolean isLockVisibility() {
        return lockVisibility;
    }

    public Listener getListener() {
        return listener;
    }

    public Enum<Modal.Type> getType() {
        return type;
    }

    public View getModal() {
        return modal;
    }

    public List<MoButton> getList() {
        return list;
    }

    public void setList(List<MoButton> list) {
        this.list = list;
        postValue(setChangedIndex(4));

    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        postValue(setChangedIndex(5));

    }

    private ModalObj setChangedIndex(int i){
        changedIndex=i;
        return this;
    }

    public int getChangedIndex() {
        return changedIndex;
    }

    @Override
    protected void onActive() {
        super.onActive();
        Log.e("onActive",""+hasActiveObservers());
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        Log.e("onInactive",""+hasActiveObservers());
    }

    public int getContentView() {
        return contentView;
    }

    public void setContentView(int contentView) {
        this.contentView = contentView;
    }
}
