package ir.drax.util;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import ir.drax.expandable.Expandable;
import ir.drax.expandable.WaterfallExpandable;
import ir.drax.modal.Listener;
import ir.drax.modal.Modal;
import ir.drax.modal.ModalBuilder;
import ir.drax.modal.model.MoButton;
import ir.drax.modal.model.OnClickListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //openModal(this);
        initExpandable();
        initListExpandable();
    }

    public void openModal(View view) {

        Modal.builder(this)
                .setListener(new Listener() {
                    @Override
                    public void onDismiss() {
                        Toast.makeText(MainActivity.this, "dismissed!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onShow() {
                        Toast.makeText(MainActivity.this, "showed!", Toast.LENGTH_SHORT).show();
                    }
                }).setType(Modal.Type.Alert)
                .setTitle("رند کردن قیمت در صفحه waitingpassenger در اپهای مسافر و راننده اعمال نمی شود.")
                .setMessage(getString(R.string.sample_text))
                .setIcon(R.drawable.ic_gesture_black_24dp)
                .setCallback(new MoButton("Got it !!!!", R.drawable.ic_mood_black_24dp, new OnClickListener() {
                    @Override
                    public boolean onClick(View v) {
                        Toast.makeText(MainActivity.this, "closed!", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }))
                .build()
                .show();
    }

    public void openListModal(View view) {
        List<MoButton> buttonList =new ArrayList<>();
        buttonList.add(new MoButton(Html.fromHtml("Repair service:  <i>$2506 Dollars</i>"),R.drawable.ic_build_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_content_cut_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_content_cut_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_content_cut_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_content_cut_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_content_cut_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_content_cut_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_content_cut_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_content_cut_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_content_cut_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_content_cut_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_content_cut_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_content_cut_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_content_cut_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_content_cut_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_content_cut_black_24dp,null));
        buttonList.add(new MoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"),R.drawable.ic_brush_black_24dp,(v)->{
            Toast.makeText(this, "sssss", Toast.LENGTH_SHORT).show();
            return true;
        }));

        buttonList.add(new MoButton(Html.fromHtml("Discount :  <u>$500 Dollars</u>"),R.drawable.ic_mood_black_24dp,null));

        Modal.builder(this)
                .setType(Modal.Type.List)
                .setTitle("Sample list modal")
                .setIcon(R.drawable.ic_gesture_black_24dp)
                .setList(buttonList)
                .setMessage(new MoButton("2706 Total",R.drawable.ic_attach_money_black_24dp,null))
                .setCallback(new MoButton("Share", R.drawable.ic_share_black_24dp, new OnClickListener() {
                    @Override
                    public boolean onClick(View v) {
                        Toast.makeText(MainActivity.this, "Throw an intent here ...", Toast.LENGTH_SHORT).show();

                        return true;//Hide modal..
                    }
                }))
                .build().show();
    }

    @Override
    public void onBackPressed() {
        if (!Modal.hide(this))

        super.onBackPressed();
    }

    public void openModal2(View view) {
        Modal.builder(view)
                .setDirection(Modal.Direction.TopToBottom)
                .setType(Modal.Type.Custom)
                .setContentView(R.layout.sample_layout)
        .build().show();
    }

    public void hideModal(View view) {
        onBackPressed();
    }

    private ModalBuilder progressBuilder;
    public void openProgressModal(View view) {
         if (progressBuilder==null) {
             progressBuilder = Modal.builder(view)
                     .setListener(new Listener() {
                         @Override
                         public void onDismiss() {

                         }

                         @Override
                         public void onShow() {
                             new Thread(new Runnable() {
                                 @Override
                                 public void run() {
                                     while (progressBuilder.getState().getProgress()<99){
                                         try {
                                             Thread.sleep(100);
                                             runOnUiThread(()->{
                                                 Log.e("ssss","ss");
                                                 progressBuilder.getState().setProgress(progressBuilder.getState().getProgress()+1);
                                             });
                                         } catch (InterruptedException e) {
                                             e.printStackTrace();
                                         }
                                     }

                                     runOnUiThread(()->{
                                         progressBuilder.hide();
                                     });
                                 }
                             }).start();
                         }
                     })
                     .setType(Modal.Type.Progress)
                     .setTitle("Uploading")
                     .setMessage("Uploading file: readme.txt")
                     .setIcon(R.drawable.ic_baseline_cloud_queue_24)
                     .setProgress(0)
                     .build();

         }
        progressBuilder.show();
    }

    private void initExpandable(){
        WaterfallExpandable expandable= findViewById(R.id.waterfallExpandableList);

        TextView textView=new TextView(this);
        textView.setText("Heyyy, I'm a Textview with a message.\nClick me to add more views...");
        textView.setTextColor(Color.WHITE);
        textView.setOnClickListener((v)->{
            TextView textView2=new TextView(this);
            textView2.setText("I'm a NEW Textview with a message.\nA second line is provided as well.");
            textView2.setTextColor(Color.WHITE);
            expandable.addChild(textView2);
        });


        ImageView imageView=new ImageView(this);
        imageView.setImageResource(R.drawable.ic_close_black_12dp);
        ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

        imageView.setOnClickListener(v->expandable.collapse());

        expandable.setTitle("My expandable list");
        expandable.setIcon(R.drawable.ic_build_black_24dp);
        expandable.addChild(textView,imageView);
    }


    private void initListExpandable(){
        Expandable expandable= findViewById(R.id.myExpandableList);

        ListView recyclerView=new ListView(this);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,new ArrayList<>());
        recyclerView.setAdapter(itemsAdapter);
        recyclerView.setBackgroundColor(Color.WHITE);

        TextView textView=new TextView(this);
        textView.setText("Click me to add more list items ...");
        textView.setTextColor(Color.WHITE);
        textView.setOnClickListener((v)->{
            itemsAdapter.add("New item!");
        });


        expandable.setTitle("My recyclerView expandable list");
        expandable.setIcon(R.drawable.ic_build_black_24dp);
        expandable.addChild(textView,recyclerView);
        expandable.getStateObservable().addObserver((observable, o) -> {
            if ((boolean)o)
                Toast.makeText(MainActivity.this, "Event observer catched expand status ", Toast.LENGTH_LONG).show();
        });

    }
}
