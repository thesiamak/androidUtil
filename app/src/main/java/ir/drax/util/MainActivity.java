package ir.drax.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import ir.drax.modal.Direction;
import ir.drax.modal.Modal;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //openModal(this);
    }

    public void openModal(View view) {
        Modal.builder(this).show(R.string.sample_text);
        Modal.builder(this).setDirection(Direction.FromBottom).show("87sa87sa48s7!");
        Modal.builder(this).setDirection(Direction.FromTop).show("Heeyyy!");
        //openModal(this);
    }


    @Override
    public void onBackPressed() {
        if (!Modal.builder(this).hide())
        super.onBackPressed();
    }
}
