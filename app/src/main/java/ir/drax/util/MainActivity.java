package ir.drax.util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ir.drax.modal.Direction;
import ir.drax.modal.Modal;
import ir.drax.modal.model.MoButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //openModal(this);
    }

    public void openModal(View view) {
        Modal.builder(this)
                .show("",getString(R.string.sample_text),R.drawable.ic_gesture_black_24dp);
    }

    public void openListModal(View view) {
        List<MoButton> buttonList =new ArrayList<>();
        buttonList.add(new MoButton(Html.fromHtml("Repair service:  <i>$2506 Dollars</i>"),R.drawable.ic_build_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_content_cut_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_brush_black_24dp,null));

        buttonList.add(new MoButton(Html.fromHtml("Discount :  <u>$500 Dollars</u>"),R.drawable.ic_mood_black_24dp,null));


        Modal.builder(this)
                .typeList().show("Sample list modal",R.drawable.ic_gesture_black_24dp,
                buttonList
                ,new MoButton("2706 Total",R.drawable.ic_attach_money_black_24dp,null)
                ,new MoButton("Share", R.drawable.ic_share_black_24dp, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "build a share intent here ...", Toast.LENGTH_SHORT).show();
                        onBackPressed();//Hide the modal..
                    }
                }));
    }

    @Override
    public void onBackPressed() {
        if (!Modal.builder(this).hide())

            super.onBackPressed();
    }

    public void openModal2(View view) {
        Modal.builder(this).setDirection(Direction.FromTop).typeCustomView().show(R.layout.sample_layout);
    }

    public void hideModal(View view) {
        onBackPressed();
    }
}
