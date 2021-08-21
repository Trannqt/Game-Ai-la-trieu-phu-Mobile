package com.sire.ailatrieuphu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {
    RadioButton optionA, optionB, optionC, optionD;
    RadioGroup optionGroup;
    TextView tvTimer, tvQuestion, tvScore;
    Button btnSwap, btnAsk, btnConfirm, btn50;
    User user;
    CountDownTimer timer;

    MediaPlayer uncleSam;
    MediaPlayer beginPlaySound;
    int[] reward = {0, 200, 400, 600, 1000, 2000, 3000, 6000, 10000, 14000, 22000, 30000, 40000, 60000, 85000, 100000};
    ArrayList<String> questionsId = new ArrayList<>();
    int correctAnswer = 0;

    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    private long sumQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        beginPlaySound = MediaPlayer.create(PlayActivity.this, R.raw.play_begin);
        beginPlaySound.setVolume(100, 100);
        beginPlaySound.start();

        optionGroup = findViewById(R.id.rdGroup);
        optionA = findViewById(R.id.rdbA);
        optionB = findViewById(R.id.rdbB);
        optionC = findViewById(R.id.rdbC);
        optionD = findViewById(R.id.rdbD);
        tvTimer = findViewById(R.id.tvTimer);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore);
        btnSwap = findViewById(R.id.btnSwap);
        btnAsk = findViewById(R.id.btnAsk);
        btn50 = findViewById(R.id.btn50);
        btnConfirm = findViewById(R.id.btnConfirm);

        user = new User();
        Bundle bd = getIntent().getExtras();
        if(bd != null) {
            String  strUsername = bd.getString("strUsername");
            user.setUser(strUsername);
        }
        startTimer();
        // Lấy câu hỏi
        getQuestionFromDatabase();
    }

    // Lấy câu hỏi
    private void getQuestionFromDatabase() {
        final DatabaseReference questionRef = databaseRef.child("Questions");
        questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sumQuestion = dataSnapshot.getChildrenCount();
                Log.d("ALLQUESTIONS", String.valueOf(sumQuestion));
                // Lấy 15 câu hỏi + 1 câu Swap Random lưu vào questionsId
                getRandomIds(0, sumQuestion - 1);
                Log.d("ALLQUESTIONS", String.valueOf(questionsId));
                showQuestion(questionRef, questionsId.get(correctAnswer));

                // Đổi câu hỏi
                btnSwap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timer.cancel();
                        btnSwap.setEnabled(false);
                        showQuestion(questionRef, questionsId.get(15));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    // Lấy 15 câu hỏi + 1 câu Swap Random lưu vào questionsId
    private void getRandomIds(long min, long max) {
        HashSet hs = new HashSet();
        while(hs.size() < 16) {
            String result = String.valueOf((long)(Math.random() * max) + min);
            hs.add(result);
        }

        Iterator it = hs.iterator();
        while(it.hasNext()){
            questionsId.add(it.next().toString());
        }
    }

    // Hiển thị và cập nhật câu hỏi
    private void showQuestion(final DatabaseReference questionRef, String id) {
        optionA.setEnabled(true);
        optionB.setEnabled(true);
        optionC.setEnabled(true);
        optionD.setEnabled(true);
        optionGroup.clearCheck();
        DatabaseReference idRef = questionRef.child(id);
        idRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Question question = dataSnapshot.getValue(Question.class);
                tvQuestion.setText(question.getQuestion());
                optionA.setText(question.getOption1());
                optionB.setText(question.getOption2());
                optionC.setText(question.getOption3());
                optionD.setText(question.getOption4());
                tvScore.setText("Số Điểm Hiện Tại: " + reward[correctAnswer]);

                timer.cancel();
                startTimer();

                // Kiểm tra đáp án
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkAnswer(questionRef, question);
                    }
                });

                // 50:50
                btn50.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn50.setEnabled(false);
                        int temp = 0;
                        boolean isHiddenA = false, isHiddenB = false, isHiddenC = false, isHiddenD = false;
                        Random random = new Random();
                        while(temp < 2) {
                            char randomChar = (char)(random.nextInt(4) + 'A');
                            switch (randomChar) {
                                case 'A':
                                    if(isHiddenA == false && !(optionA.getText().toString().equals(question.getAnswer()))) {
                                        temp++;
                                        isHiddenA = true;
                                        optionA.setText("");
                                        optionA.setEnabled(false);
                                    }
                                    break;
                                case 'B':
                                    if(isHiddenB == false && !(optionB.getText().toString().equals(question.getAnswer()))) {
                                        temp++;
                                        isHiddenB = true;
                                        optionB.setText("");
                                        optionB.setEnabled(false);
                                    }
                                    break;
                                case 'C':
                                    if(isHiddenC == false && !(optionC.getText().toString().equals(question.getAnswer()))) {
                                        temp++;
                                        isHiddenC = true;
                                        optionC.setText("");
                                        optionC.setEnabled(false);
                                    }
                                    break;
                                case 'D':
                                    if(isHiddenD == false && !(optionD.getText().toString().equals(question.getAnswer()))) {
                                        temp++;
                                        isHiddenD = true;
                                        optionD.setText("");
                                        optionD.setEnabled(false);
                                    }
                                    break;
                            }
                        }
                    }
                });

                // Hỏi ý kiến
                btnAsk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnAsk.setEnabled(false);
                        ProgressBar A, B, C, D;
                        TextView opinionA, opinionB, opinionC, opinionD;
                        int tempOptionA, tempOptionB, tempOptionC, tempOptionD;
                        Random random = new Random();
                        tempOptionA = random.nextInt(70);
                        tempOptionB = random.nextInt(70 - tempOptionA);
                        tempOptionC = random.nextInt(70 - tempOptionA - tempOptionB);
                        tempOptionD = random.nextInt(70 - tempOptionA - tempOptionB - tempOptionC);

                        if(optionA.getText().toString().equals(question.getAnswer())) {
                            tempOptionA += 30;
                        }
                        else if (optionB.getText().toString().equals(question.getAnswer())) {
                            tempOptionB += 30;
                        }
                        else if (optionC.getText().toString().equals(question.getAnswer())) {
                            tempOptionC += 30;
                        }
                        else if (optionD.getText().toString().equals(question.getAnswer())) {
                            tempOptionD += 30;
                        }
                        Log.d("OPINION A", String.valueOf(tempOptionA));
                        Log.d("OPINION B", String.valueOf(tempOptionB));
                        Log.d("OPINION C", String.valueOf(tempOptionC));
                        Log.d("OPINION D", String.valueOf(tempOptionD));

                        AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.ask, null);
                        dialog.setView(view);
                        dialog.setTitle("");

                        A = view.findViewById(R.id.pbOptionA);
                        B = view.findViewById(R.id.pbOptionB);
                        C = view.findViewById(R.id.pbOptionC);
                        D = view.findViewById(R.id.pbOptionD);
                        opinionA = view.findViewById(R.id.tvOpinionA);
                        opinionB = view.findViewById(R.id.tvOpinionB);
                        opinionC = view.findViewById(R.id.tvOpinionC);
                        opinionD = view.findViewById(R.id.tvOpinionD);

                        A.setProgress(tempOptionA);
                        B.setProgress(tempOptionB);
                        C.setProgress(tempOptionC);
                        D.setProgress(tempOptionD);
                        opinionA.setText(tempOptionA + "%");
                        opinionB.setText(tempOptionB + "%");
                        opinionC.setText(tempOptionC + "%");
                        opinionD.setText(tempOptionD + "%");
                        dialog.show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("LoadQuestionFailed", databaseError.toException());
            }
        });
    }
    // Kiểm tra đáp án
    private void checkAnswer(DatabaseReference questionRef, Question question) {
        RadioButton selectedRadioButton = findViewById(optionGroup.getCheckedRadioButtonId());
        String answer = selectedRadioButton.getText().toString();
        if(answer.equals(question.getAnswer())) {
            // Xử lý khi đúng
            isCorrect(questionRef);
        }
        else {
            // Xử lý khi sai
            //isWrong();
        }
    }

    // Xử lý khi đúng
    private void isCorrect(final DatabaseReference questionRef) {
        correctAnswer++;
        Log.d("CORRECT_ANSWER", String.valueOf(correctAnswer));

        if(correctAnswer < 15) {
            uncleSam = MediaPlayer.create(PlayActivity.this, R.raw.correct);
            uncleSam.start();
            timer.cancel();

            AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Chúc mừng");
            builder.setMessage("Một Câu Trả Lời Chính Xác");
            AlertDialog dialog = builder.create();

            // Lấy câu hỏi tiếp theo
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Câu Tiếp Theo", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tvScore.setText("Số Điểm Đạt Được: " + reward[correctAnswer]);
                    showQuestion(questionRef, questionsId.get(correctAnswer));
                    dialog.cancel();
                }
            });
            dialog.show();
        }
        else {
            AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Chiến Thắng");
            builder.setMessage("Bạn Đã Hoàn Thành 15 Câu Hỏi Và Kiếm Được: " + reward[correctAnswer]);
            AlertDialog dialog = builder.create();
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    timer.cancel();
                    // Xử lý lưu điểm
                    user.setScore(reward[correctAnswer]);
                    saveHighscore();

                    // Trở lại menu
                    timer.cancel();
                    uncleSam.stop();
                    finish();
                }
            });
            dialog.show();
        }
    }

    // Xử lý khi sai
    private void isWrong() {
        uncleSam = MediaPlayer.create(PlayActivity.this, R.raw.wrong);
        uncleSam.start();
        timer.cancel();

        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Thật Đáng Tiếc!");
        builder.setMessage("Số Điểm Đạt Được: " + reward[correctAnswer]);
        AlertDialog dialog = builder.create();
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Trở lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý lưu điểm
                user.setScore(reward[correctAnswer]);
                saveHighscore();

                // Trở lại menu
                timer.cancel();
                uncleSam.stop();
                finish();
            }
        });
        dialog.show();
    }

    // Chạy timer
    private void startTimer() {
        timer = new CountDownTimer(31000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Thời gian: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                uncleSam = MediaPlayer.create(PlayActivity.this, R.raw.time_out);
                uncleSam.start();

                AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
                builder.setTitle("Hết Giờ");
                builder.setMessage("Đã Hết Thời Gian!");
                builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.cancel();
                    }
                });
                AlertDialog dialogTimeOut = builder.create();

                user.setScore(reward[correctAnswer]);
                // Xử lý lưu điểm
                saveHighscore();
                dialogTimeOut.show();
            }
        };
        timer.start();
    }

    // Xử lý lưu điểm
    private void saveHighscore() {
        if(user.getScore() != 0) {
            final DatabaseReference scoreRef = databaseRef.child("Scores");
            String key = scoreRef.push().getKey();
            scoreRef.child(key).setValue(user);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
