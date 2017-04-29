package iqubal.ashraf.videoken;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private final String YOUTUBE_ID_FOR_INTENT = "iqubal.ashraf.videoken.YOUTUBE_ID";
    Button playYouTubeVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeAllViews();
        initializeOnClickListener();
    }
    private void initializeAllViews(){
        playYouTubeVideo = (Button)findViewById(R.id.playYouTubeVideo);
    }
    private void initializeOnClickListener(){
        playYouTubeVideo.setOnClickListener(playYouTubeVideoClicked);
    }
    View.OnClickListener playYouTubeVideoClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                displayDialogForYoutube();
            }else {
                Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
            }
        }
    };
    public void displayDialogForYoutube(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        final EditText input = new EditText(MainActivity.this);
        input.setHint("Enter Viedo URL");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setTitle("Youtube");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String title = input.getText().toString();
                        if(title.length()>22){
                            playViedoInAnotherActivity(getVideoId(title));
                        }else {
                            Toast.makeText(getApplicationContext(),"Invalid URL",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }
    private String getVideoId(String watchLink){
        return watchLink.substring(watchLink.length() - 11);
    }
    private void playViedoInAnotherActivity(String youtube_id){
        Intent intent = new Intent(this,YouTubePlayerActivity.class);
        intent.putExtra(YOUTUBE_ID_FOR_INTENT,youtube_id);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
