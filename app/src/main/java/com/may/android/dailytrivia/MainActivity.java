package com.may.android.dailytrivia;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;

public class MainActivity extends Activity {

    int[] questions;
    int[] answers;
    Random r = new Random();
    int points = 0;
    String name = "";
    Boolean nameChanged = false;

    static int[] addElement(int[] a, int e) {
        if (a == null) {
            a = new int[1];
        } else {
            a = Arrays.copyOf(a, a.length + 1);
        }
        a[a.length - 1] = e;
        return a;
    }

    static int[] removeElement(int[] a, int e) {
        int m = 0;
        for (int i = 0; i < a.length; i++) {
            m = i;
            if (a[i] == e) {
                break;
            }
        }
        int[] b = Arrays.copyOf(a, a.length - 1);
        for (int j = m; j < (a.length - 1); j++) {
            b[j] = a[j + 1];
        }
        return b;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepare();
        EditText bedit = (EditText) findViewById(R.id.name);
        bedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetName();
            }
        });
        Button bresult = (Button) findViewById(R.id.buttonResults);
        bresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getresult();
            }
        });
        Button breset = (Button) findViewById(R.id.buttonRetry);
        breset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    void prepare() {
        int[] questionsSort = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        for (int i = 0; i < 5; i++) {
            int i1 = r.nextInt(questionsSort.length);
            int actq = questionsSort[i1];
            questionsSort = removeElement(questionsSort, actq);
            questions = addElement(questions, actq);
            int[] answersSort = new int[]{1, 2, 3, 4};
                    /*Define questionsSort and answers here*/
            for (int j = 0; j < 4; j++) {
                int i2 = r.nextInt(answersSort.length);
                int acta = answersSort[i2];
                answersSort = removeElement(answersSort, acta);
                answers = addElement(answers, acta);
            }

        }
        String[] questionStrings = getResources().getStringArray(R.array.questions);
        String[] answerStrings = getResources().getStringArray(R.array.answers);
        for (int i = 1; i < 6; i++) {
            int qID = this.getResources().getIdentifier("question" + i, "id", this.getPackageName());
            TextView questionView = (TextView) findViewById(qID);
            questionView.setText(questionStrings[questions[i - 1] - 1]);
            for (int j = 1; j < 5; j++) {
                int aID = getResources().getIdentifier("answer" + i + j, "id", getPackageName());
                RadioButton answerView = (RadioButton) findViewById(aID);
                answerView.setText(answerStrings[(questions[i - 1] - 1) * 4 + (answers[j - 1] - 1)]);
                answerView.setTag(answers[j - 1]);
            }
        }
    }


    void getresult() {
        EditText nameView = (EditText) findViewById(R.id.name);
        name = nameView.getText().toString();
        if (name.equals("Cheatword")){
            //Cheat adds 20 points extra
            points=points+20;
        }

        RadioButton[] useranswers = new RadioButton[5];
        boolean done = true;
        for (int i = 1; i < 6; i++) {
            int gID = this.getResources().getIdentifier("groupAnswers" + i, "id", this.getPackageName());
            RadioGroup answerGroup = (RadioGroup) findViewById(gID);
            int selectedId = answerGroup.getCheckedRadioButtonId();
            useranswers[i - 1] = (RadioButton) findViewById(selectedId);
            if (selectedId == -1) {
                Context context = getApplicationContext();
                CharSequence text = "Please answer all the questions!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                done = false;
                break;
            }
        }

        if (done) {
            boolean[] checked = new boolean[4];
            for (int i = 1; i < 5; i++) {
                int cID = this.getResources().getIdentifier("checkBox" + i, "id", this.getPackageName());
                CheckBox check = (CheckBox) findViewById(cID);
                if (check.isChecked()) {
                    checked[i-1]=true;
                }
            }
            String bonus_s = "";
            if (!(checked[0])&&checked[1]&&checked[2]&&checked[3]) {
                points = points + 10;
                bonus_s = "\nGood, nobody likes facebook. Have a bonus!";
            }
            for (int i = 1; i < 6; i++) {
                RadioButton answerButton = useranswers[i - 1];
                int userAnswer = (int) answerButton.getTag();
                if (userAnswer == 1) {
                    points = points + 10;
                    answerButton.setTextColor(ContextCompat.getColor(this, R.color.right));
                } else {
                    answerButton.setTextColor(getResources().getColor(R.color.wrong));
                    for (int j = 1; j < 5; j++) {
                        int aID = getResources().getIdentifier("answer" + i + j, "id", getPackageName());
                        RadioButton correctAnswer = (RadioButton) findViewById(aID);
                        if ((int) correctAnswer.getTag() == 1) {
                            correctAnswer.setTextColor(getResources().getColor(R.color.right));
                        }
                    }
                }
            }
            TextView restext = (TextView) findViewById(R.id.endresults);
            String resstring = "Congratulations " + name + ", you scored " + points + " points." + bonus_s + "\nClick Retry for a new game!";
            restext.setText(resstring);

            Toast toastDone = Toast.makeText(getApplicationContext(), resstring, Toast.LENGTH_LONG);
            toastDone.show();

            LinearLayout resultLayout = (LinearLayout) findViewById(R.id.resultsLayout);
            resultLayout.setVisibility(View.VISIBLE);

            Button getres = (Button) findViewById(R.id.buttonResults);
            getres.setClickable(false);
        }
    }

    void reset() {
        points = 0;
        Button getres = (Button) findViewById(R.id.buttonResults);
        getres.setClickable(true);
        nameChanged = false;
        questions = new int[0];
        answers = new int[0];

        for (int i = 1; i < 6; i++) {
            int gID = this.getResources().getIdentifier("groupAnswers" + i, "id", this.getPackageName());
            RadioGroup answerGroup = (RadioGroup) findViewById(gID);
            answerGroup.clearCheck();
            for (int j = 1; j < 5; j++) {
                int aID = getResources().getIdentifier("answer" + i + j, "id", getPackageName());
                RadioButton answerView = (RadioButton) findViewById(aID);
                answerView.setTag(0);
                answerView.setTextColor(getResources().getColor(R.color.white));
            }
        }

        prepare();
        LinearLayout resultLayout = (LinearLayout) findViewById(R.id.resultsLayout);
        resultLayout.setVisibility(View.GONE);

    }

    void resetName() {
        EditText nameView = (EditText) findViewById(R.id.name);
        if (!nameChanged) {
            nameView.setText("");
            nameChanged = true;
        }
    }


}
