package com.example.mycorrectapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.sql.SQLOutput;

public class StudentRegisterSetLearningStyleActivity extends AppCompatActivity {

    String learningStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_register_set_learning_style);

        RadioGroup q1 = findViewById(R.id.q1);
        RadioGroup q2 = findViewById(R.id.q2);
        RadioGroup q3 = findViewById(R.id.q3);
        RadioGroup q4 = findViewById(R.id.q4);
        RadioGroup q5 = findViewById(R.id.q5);
        RadioGroup q6 = findViewById(R.id.q6);
        RadioGroup q7 = findViewById(R.id.q7);
        RadioGroup q8 = findViewById(R.id.q8);
        RadioGroup q9 = findViewById(R.id.q9);
        RadioGroup q10 = findViewById(R.id.q10);
        RadioGroup q11 = findViewById(R.id.q11);
        RadioGroup q12 = findViewById(R.id.q12);
        RadioGroup q13 = findViewById(R.id.q13);
        RadioGroup q14 = findViewById(R.id.q14);
        RadioGroup q15 = findViewById(R.id.q15);
        RadioGroup q16 = findViewById(R.id.q16);

        Button btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int[] jsonArray = new int[16];
                    jsonArray[0] = q1.getCheckedRadioButtonId();
                    jsonArray[1] = q2.getCheckedRadioButtonId();
                    jsonArray[2] = q3.getCheckedRadioButtonId();
                    jsonArray[3] = q4.getCheckedRadioButtonId();
                    jsonArray[4] = q5.getCheckedRadioButtonId();
                    jsonArray[5] = q6.getCheckedRadioButtonId();
                    jsonArray[6] = q7.getCheckedRadioButtonId();
                    jsonArray[7] = q8.getCheckedRadioButtonId();
                    jsonArray[8] = q9.getCheckedRadioButtonId();
                    jsonArray[9] = q10.getCheckedRadioButtonId();
                    jsonArray[10] = q11.getCheckedRadioButtonId();
                    jsonArray[11] = q12.getCheckedRadioButtonId();
                    jsonArray[12] = q13.getCheckedRadioButtonId();
                    jsonArray[13] = q14.getCheckedRadioButtonId();
                    jsonArray[14] = q15.getCheckedRadioButtonId();
                    jsonArray[15] = q16.getCheckedRadioButtonId();

                    learningStyle = calculateLearningStyle(jsonArray);
                    if (learningStyle != null) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("learningStyle", learningStyle);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please answer all questions", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String calculateLearningStyle(int[] jsonArray) throws JSONException {
        RadioButton radio;
        String radioId;
        boolean hasNull = false;
        int visual = 0;
        int aural = 0;
        int readWrite = 0;
        int kinesthetic = 0;
        for (int id : jsonArray) {
            if (id > -1) {
                radio = findViewById(id);
                radioId = getResources().getResourceEntryName(radio.getId());
                switch (radioId.charAt(radioId.length() - 1)) {
                    case 'v':
                        visual++;
                        break;
                    case 'a':
                        aural++;
                        break;
                    case 'r':
                        readWrite++;
                        break;
                    case 'k':
                        kinesthetic++;
                        break;
                }
            } else {
                hasNull = true;
            }
        }

        if (hasNull != true) {
            int max = visual;
            String lStyle = "Visual";
            if (aural > max) {
                max = aural;
                lStyle = "Aural";
            }
            if (readWrite > max) {
                max = readWrite;
                lStyle = "Read/Write";
            }
            if (kinesthetic > max) {
                max = kinesthetic;
                lStyle = "Kinesthetic";
            }
            return lStyle;
        } else {
            return null;
        }
    }
}
