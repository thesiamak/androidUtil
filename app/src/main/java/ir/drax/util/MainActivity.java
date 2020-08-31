package ir.drax.util;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;

import java.util.ArrayList;
import java.util.List;

import ir.drax.expandable.Expandable;
import ir.drax.modal.BaseBuilder;
import ir.drax.modal.Modal;
import ir.drax.modal.ProgressBuilder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //openModal(this);
        initExpandable();
    }

    public void openModal(View view) {
        Modal.builder(this)
                .setListener(new BaseBuilder.Listener() {
                    @Override
                    public void onDismiss() {
                        Toast.makeText(MainActivity.this, "dismissed!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onShow() {
                        Toast.makeText(MainActivity.this, "showed!", Toast.LENGTH_SHORT).show();
                    }
                }).typeAlert()
                .show(" کردن قیمت در صفحه waitingpassenger در اپهای مسافر و راننده اعمال نمی شود.",getString(R.string.sample_text),R.drawable.ic_gesture_black_24dp
                ,new MoButton("Got it !!!!", R.drawable.ic_mood_black_24dp, new MoButton.OnClickListener() {
                            @Override
                            public boolean onClick(View v) {
                                Toast.makeText(MainActivity.this, "closed!", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        }));
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
                ,new MoButton("Share", R.drawable.ic_share_black_24dp, new MoButton.OnClickListener() {
                    @Override
                    public boolean onClick(View v) {
                        Toast.makeText(MainActivity.this, "build a share intent here ...", Toast.LENGTH_SHORT).show();
                        onBackPressed();//Hide the modal..
                        return true;
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

    private ProgressBuilder progressBuilder;
    public void openProgressModal(View view) {
         if (progressBuilder==null)
             progressBuilder = Modal.builder(this)
                     .setListener(new BaseBuilder.Listener() {
                         @Override
                         public void onDismiss() {

                         }

                         @Override
                         public void onShow() {
                             new Thread(new Runnable() {
                                 @Override
                                 public void run() {
                                     while (progressBuilder.getProgress()<99){
                                         try {
                                             Thread.sleep(100);
                                             runOnUiThread(()->{
                                                 progressBuilder.setProgress(progressBuilder.getProgress()+1);
                                             });
                                         } catch (InterruptedException e) {
                                             e.printStackTrace();
                                         }
                                     }
                                 }
                             }).start();
                         }
                     }).typeProgress().show("Uploading","Uploading file: readme.txt",R.drawable.ic_baseline_cloud_queue_24,0);

         else progressBuilder.show();
    }

    private void initExpandable(){
        Expandable expandable= findViewById(R.id.myExpandable);

        TextView textView=new TextView(this);
        textView.setText("Heyyy, I'm a Textview with a message.\nA second line is provided as well.");
        textView.setTextColor(Color.WHITE);

        ImageView imageView=new ImageView(this);
        imageView.setImageResource(R.drawable.ic_close_black_12dp);
        ImageViewCompat.setImageTintList(imageView,ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

        imageView.setOnClickListener(v->expandable.collapse());

        expandable.setTitle("My expandable list");
        expandable.setIcon(R.drawable.ic_build_black_24dp);
        expandable.addChild(textView,imageView);
    }
}
