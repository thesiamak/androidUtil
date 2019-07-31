package ir.drax.util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
        Modal.builder(this).show("",getString(R.string.sample_text),R.drawable.ic_gesture_black_24dp);

    }


    @Override
    public void onBackPressed() {
        if (!Modal.builder(this).hide())
        super.onBackPressed();
    }

    public void openModal2(View view) {
        Modal.builder(this).setDirection(Direction.FromTop).typeCustomView().show(R.layout.reserve_start_trip);
    }

    public void hideModal(View view) {
        onBackPressed();
    }
}
