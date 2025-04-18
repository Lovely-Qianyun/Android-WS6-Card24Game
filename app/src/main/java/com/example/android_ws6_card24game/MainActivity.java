package com.example.android_ws6_card24game;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.singularsys.jep.EvaluationException;
import com.singularsys.jep.Jep;
import com.singularsys.jep.ParseException;

public class MainActivity extends AppCompatActivity {

    private Button rePick;
    private Button checkInput;
    private Button clear;
    private Button left;
    private Button right;
    private Button plus;
    private Button minus;
    private Button multiply;
    private Button divide;
    private Button backspace;
    private TextView expression;
    private ImageButton[] cards;
    private final int[] data = {-1, -1, -1, -1};
    private final int[] card = {-1, -1, -1, -1};
    private final int[] imageCount = {-1, -1, -1, -1};
    private int gameRule = 24;
    private final String signs = "+-/*(";
    private int openBracketCount = 0;
    private final int[] startIndex = new int[4];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().hasExtra("number"))
            gameRule = getIntent().getIntExtra("number", 24);

        cards = new ImageButton[4];

        cards[0] = findViewById(R.id.card1);
        cards[1] = findViewById(R.id.card2);
        cards[2] = findViewById(R.id.card3);
        cards[3] = findViewById(R.id.card4);

        rePick = findViewById(R.id.repick);
        checkInput = findViewById(R.id.checkinput);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        plus = findViewById(R.id.plus);
        minus = findViewById(R.id.minus);
        multiply = findViewById(R.id.multiply);
        divide = findViewById(R.id.divide);
        clear = findViewById(R.id.clear);
        expression = findViewById(R.id.input);
        backspace = findViewById(R.id.backspace);

        expression.setHint("Please form an expression such that the result is " + gameRule);
        initCardImage();
        setListeners();
        pickCard();
    }

    private boolean checkInput(String input) {
        if (!checkAllCardsUsed()) {
            Toast.makeText(MainActivity.this,
                    "Please use all cards to complete expression", Toast.LENGTH_SHORT).show();
            return false;
        }
        Jep jep = new Jep();
        Object res;
        try {
            jep.parse(input);
            res = jep.evaluate();
        } catch (ParseException | EvaluationException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,
                    "Wrong expression", Toast.LENGTH_SHORT).show();
            return false;
        }
        Double ca = (Double) res;
        return Math.abs(ca - gameRule) < 1e-6;
    }

    private boolean checkAllCardsUsed() {
        String txt = expression.getText().toString();
        for (int i = 0; i < 4; i++) {
            String cardStr = String.valueOf(data[i]);
            int idx = txt.indexOf(cardStr);
            if (idx < 0) {
                return false;
            }
            txt = txt.substring(0, idx) + txt.substring(idx + cardStr.length());
        }
        return true;
    }

//    private void backspace() {
//        String txt = expression.getText().toString();
//        if (txt.isEmpty()) return;
//
//        boolean foundCard = false;
//        for (int i = 0; i < 4; i++) {
//            if (imageCount[i] == 1) {
//                String numStr = String.valueOf(data[i]);
//                if (txt.endsWith(numStr)) {
//                    int resId = getResources().getIdentifier("card" + card[i], "drawable", getPackageName());
//                    cards[i].setImageResource(resId);
//                    imageCount[i] = 0;
//                    expression.setText(txt.substring(0, txt.length() - numStr.length()));
//                    foundCard = true;
//                    break;
//                }
//            }
//        }
//        if (!foundCard) {
//            expression.setText(txt.substring(0, txt.length() - 1));
//        }
//    }

    private void setListeners() {
        cards[0].setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                clickCard(0);
            }
        });
        cards[1].setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                clickCard(1);
            }
        });
        cards[2].setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                clickCard(2);
            }
        });
        cards[3].setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View view) {
                clickCard(3);
            }
        });

        left.setOnClickListener(view -> {
            String txt = expression.getText().toString();
            if (txt.isEmpty() || signs.indexOf(txt.charAt(txt.length() - 1)) != -1) {
                expression.append("(");
                openBracketCount++;
            } else {
                Toast.makeText(MainActivity.this, "Cannot add bracket '('", Toast.LENGTH_SHORT).show();
            }
        });

        right.setOnClickListener(view -> {
            String txt = expression.getText().toString();
            if (openBracketCount > 0 && !txt.isEmpty()
                    && signs.indexOf(txt.charAt(txt.length() - 1)) == -1) {
                expression.append(")");
                openBracketCount--;
            } else {
                Toast.makeText(MainActivity.this, "Cannot add bracket ')'", Toast.LENGTH_SHORT).show();
            }
        });

        plus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                String txt = expression.getText().toString();
                if (!txt.isEmpty()) {
                    char lastChar = txt.charAt(txt.length() - 1);
                    if (signs.indexOf(lastChar) != -1) {
                        expression.setText(txt.substring(0, txt.length() - 1) + "+");
                    } else {
                        expression.append("+");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Cannot add sign '+'",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        minus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                String txt = expression.getText().toString();
                if (!txt.isEmpty()) {
                    char lastChar = txt.charAt(txt.length() - 1);
                    if (signs.indexOf(lastChar) != -1) {
                        expression.setText(txt.substring(0, txt.length() - 1) + "-");
                    } else {
                        expression.append("-");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Cannot add sign '-'",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        multiply.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                String txt = expression.getText().toString();
                if (!txt.isEmpty()) {
                    char lastChar = txt.charAt(txt.length() - 1);
                    if (signs.indexOf(lastChar) != -1) {
                        expression.setText(txt.substring(0, txt.length() - 1) + "*");
                    } else {
                        expression.append("*");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Cannot add sign '*'",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        divide.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                String txt = expression.getText().toString();
                if (!txt.isEmpty()) {
                    char lastChar = txt.charAt(txt.length() - 1);
                    if (signs.indexOf(lastChar) != -1) {
                        expression.setText(txt.substring(0, txt.length() - 1) + "/");
                    } else {
                        expression.append("/");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Cannot add sign '/'",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        clear.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                setClear();
            }
        });
        rePick.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                pickCard();
            }
        });
        checkInput.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                evaluateExpression();
            }
        });
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = expression.getText().toString();
                if (txt.isEmpty()) return;

                boolean foundCard = false;
                for (int i = 0; i < 4; i++) {
                    if (imageCount[i] == 1) {
                        String numStr = String.valueOf(data[i]);
                        if (txt.endsWith(numStr)) {
                            int resId = getResources().getIdentifier("card" + card[i], "drawable", getPackageName());
                            cards[i].setImageResource(resId);
                            imageCount[i] = 0;
                            expression.setText(txt.substring(0, txt.length() - numStr.length()));
                            foundCard = true;
                            break;
                        }
                    }
                }
                if (!foundCard) {
                    expression.setText(txt.substring(0, txt.length() - 1));
                }
            }
        });

    }

    private void evaluateExpression() {
        String inputStr = expression.getText().toString();
        if (checkInput(inputStr)) {
            Toast.makeText(MainActivity.this, "Correct answer",
                    Toast.LENGTH_SHORT).show();
            pickCard();
        } else {
            Toast.makeText(MainActivity.this, "Wrong answer",
                    Toast.LENGTH_SHORT).show();
            setClear();
        }
    }

    private void initCardImage() {
        for (int i = 0; i < 4; i++) {
            int resID = getResources().getIdentifier("back_0", "drawable", getPackageName());
            cards[i].setImageResource(resID);
        }
    }

    private void pickCard() {
        java.util.Set<Integer> usedCards = new java.util.HashSet<>();
        for (int i = 0; i < 4; i++) {
            int newCard;
            do {
                newCard = 1 + (int) (Math.random() * 52);
            } while (usedCards.contains(newCard));
            usedCards.add(newCard);
            card[i] = newCard;
            data[i] = card[i] % 13;
            if (data[i] == 0) {
                data[i] = 13;
            }
        }
        setClear();
    }

    private void setClear() {
        int resID;
        expression.setText("");
        for (int i = 0; i < 4; i++) {
            imageCount[i] = 0;
            resID = getResources().getIdentifier
                    ("card" + card[i], "drawable", getPackageName());
            cards[i].setImageResource(resID);
            cards[i].setClickable(true);
        }
    }


    private void clickCard(int i) {
        String txt = expression.getText().toString();
        if (imageCount[i] == 0) {
            if (!txt.isEmpty() && Character.isDigit(txt.charAt(txt.length() - 1))) {
                Toast.makeText(MainActivity.this, "Cannot add a number", Toast.LENGTH_SHORT).show();
                return;
            }
            startIndex[i] = txt.length();
            int resId = getResources().getIdentifier("back_0", "drawable", getPackageName());
            cards[i].setImageResource(resId);
            expression.append(String.valueOf(data[i]));
            imageCount[i] = 1;
        } else {
            if (startIndex[i] >= 0 && startIndex[i] <= txt.length()) {
                String removed = txt.substring(startIndex[i]);
                for (int j = 0; j < 4; j++) {
                    if (imageCount[j] == 1) {
                        String numStr = String.valueOf(data[j]);
                        if (removed.contains(numStr)) {
                            int resId2 = getResources().getIdentifier("card" + card[j], "drawable", getPackageName());
                            cards[j].setImageResource(resId2);
                            imageCount[j] = 0;
                        }
                    }
                }
                expression.setText(txt.substring(0, startIndex[i]));
                startIndex[i] = -1;
            }
        }
    }
}
