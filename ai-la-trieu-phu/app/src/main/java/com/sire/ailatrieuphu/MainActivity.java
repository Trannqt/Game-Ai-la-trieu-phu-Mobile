package com.sire.ailatrieuphu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button btnPlay, btnScore, btnExit, btnHelp;
    ImageView imgSound;
    User user;
    Boolean hasSound = true;
    static MediaPlayer soundPlayer;
    static Boolean keepPlayingSound = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay = findViewById(R.id.btnPlay);
        btnHelp = findViewById(R.id.btnHelp);
        btnScore = findViewById(R.id.btnScore);
        btnExit = findViewById(R.id.btnExit);
        user = new User();
        imgSound = findViewById(R.id.imgSound);

        // Âm thanh
        setSound(soundPlayer, true);
        imgSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasSound == true) {
                    hasSound = false;
                    setSound(soundPlayer, false);
                }
                else {
                    hasSound = true;
                    setSound(soundPlayer, true);
                }
            }
        });

        // Nhập tên
        final AlertDialog userInput = buildDialog();
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.username_input, null);
        userInput.setView(view);
        userInput.setTitle("Mời bạn nhập tên");
        final EditText txtUsername = view.findViewById(R.id.txtUsername);
        Button btnOk = view.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(txtUsername.getText())) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập tên", Toast.LENGTH_LONG).show();
                }
                else {
                    user.setUser(txtUsername.getText().toString());
                    userInput.cancel();
                }
            }
        });
        userInput.show();

        // Bắt đầu
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getUser() == null)
                    userInput.show();
                else {
                    Intent play = new Intent(MainActivity.this, PlayActivity.class);
                    play.putExtra("strUsername", user.getUser());
                    keepPlayingSound = false;
                    startActivity(play);
                }
            }
        });

        // Hướng dẫn
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Hướng Dẫn");
                builder.setMessage("Người chơi phải trả lời 15 câu hỏi. Mỗi câu hỏi đều được gắn với mức điểm thưởng quy định. Người chơi sẽ có 3 quyền trợ giúp:" +
                        "\n + 50:50 loại bỏ 2 phương án sai" +
                        "\n + Đổi câu hỏi" +
                        "\n + Hỏi ý kiến khán giả ");
                builder.setPositiveButton("Đã Hiểu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Điểm cao
        btnScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent highScore = new Intent(MainActivity.this, Highscore.class);
                MainActivity.this.startActivity(highScore);
                keepPlayingSound = true;
            }
        });

        // Thoát
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }

    // Ngắt âm thanh nếu FALSE, ngược lại giữ âm thanh khi chuyển activity
    @Override
    protected void onStop() {
        super.onStop();
        if(keepPlayingSound == false) {
            soundPlayer.stop();
        }
    }

    // Tắt âm thanh khi bấm back
    @Override
    public void onBackPressed() {
        soundPlayer.stop();
        super.onBackPressed();
    }

    // Âm thanh
    private void setSound(MediaPlayer mediaPlayer, Boolean isOn) {
        if(isOn == false) {
            imgSound.setImageResource(R.drawable.nosound);
            soundPlayer.stop();
        }
        else {
            imgSound.setImageResource(R.drawable.sound);
            soundPlayer = MediaPlayer.create(MainActivity.this, R.raw.main_menu);
            soundPlayer.setVolume(10, 100);
            soundPlayer.start();
            soundPlayer.setLooping(true);
        }
    }

    // Nhập tên
    private AlertDialog buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage("");

        AlertDialog dialog = builder.create();
        return dialog;
    }
}
