package com.strng.wordgame;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.strng.wordgame.databinding.ActivityGameBinding;

import java.util.Random;

import application.WGApplication;
import retrofit.model.RetroWord;

public class GameActivity extends AppCompatActivity {
    private GameViewModel gameViewModel;
    private ActivityGameBinding activityGameBinding;
    private RetroWord newQuestion;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGameBinding = ActivityGameBinding.inflate(getLayoutInflater());
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        setContentView(activityGameBinding.getRoot());
        showIntroUI();
        initObserver();
        initClickListeners();
        gameViewModel.getNewQuestions();
    }

    private void showIntroUI() {
        activityGameBinding.tvWg.setVisibility(View.VISIBLE);
        activityGameBinding.progressTimer.setVisibility(View.VISIBLE);
        activityGameBinding.progressTimer.setIndicatorColor(getResources().getColor(R.color.answerColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            activityGameBinding.progressTimer.setProgress(100, true);
        } else {
            activityGameBinding.progressTimer.setProgress(100);
        }
        activityGameBinding.tvWelcome.setVisibility(View.VISIBLE);
        activityGameBinding.tvGameIntro.setVisibility(View.VISIBLE);
        String sentenceLeft = "Please click";
        String sentenceMiddle = "Start";
        String sentenceRight = "to play the game";
        SpannableString spanLeft = new SpannableString(sentenceLeft);
        SpannableString spanMiddle = new SpannableString(sentenceMiddle);
        SpannableString spanRight = new SpannableString(sentenceRight);
        spanMiddle.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanMiddle.length(), 0);
        activityGameBinding.tvGameIntro.setText(TextUtils.concat(spanLeft, " ", spanMiddle, " ", spanRight));
        activityGameBinding.tvNextLevel.setVisibility(View.VISIBLE);
    }

    private void hideIntroUI() {
        activityGameBinding.tvWg.setVisibility(View.INVISIBLE);
        activityGameBinding.tvWelcome.setVisibility(View.INVISIBLE);
        activityGameBinding.tvGameIntro.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            activityGameBinding.progressTimer.setProgress(0, true);
        } else {
            activityGameBinding.progressTimer.setProgress(0);
        }
        activityGameBinding.tvNextLevel.setVisibility(View.INVISIBLE);
    }

    private void showGameUI() {
        activityGameBinding.tvLevelNumber.setVisibility(View.VISIBLE);
        String temp = "Level " + gameViewModel.levelNumber;
        activityGameBinding.tvLevelNumber.setText(temp);
        activityGameBinding.tvScoreHeading.setVisibility(View.VISIBLE);
        activityGameBinding.tvScoreNumber.setVisibility(View.VISIBLE);
        activityGameBinding.tvWord.setVisibility(View.VISIBLE);
        activityGameBinding.tvQuestion.setVisibility(View.VISIBLE);
        activityGameBinding.tvAnswerTwo.setVisibility(View.VISIBLE);
        activityGameBinding.tvAnswerOne.setVisibility(View.VISIBLE);
    }

    private void hideGameUI() {
        activityGameBinding.tvWord.setVisibility(View.INVISIBLE);
        activityGameBinding.tvQuestion.setVisibility(View.INVISIBLE);
        activityGameBinding.tvAnswerTwo.setVisibility(View.INVISIBLE);
        activityGameBinding.tvAnswerOne.setVisibility(View.INVISIBLE);
    }

    private void showSummaryUI() {
        activityGameBinding.tvLevelScore.setVisibility(View.VISIBLE);
        String temp = gameViewModel.correctQuestionCount + "/5";
        activityGameBinding.tvLevelScore.setText(temp);
        activityGameBinding.tvNextLevel.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            activityGameBinding.progressTimer.setProgress(gameViewModel.correctQuestionCount * 10 * 2, true);
        } else {
            activityGameBinding.progressTimer.setProgress(gameViewModel.correctQuestionCount * 10 * 2);
        }
        if (gameViewModel.correctQuestionCount >= 3) {
            gameViewModel.levelNumber++;
            activityGameBinding.progressTimer.setIndicatorColor(getResources().getColor(R.color.green));
            activityGameBinding.tvCongrats.setVisibility(View.VISIBLE);
            activityGameBinding.tvLevelResultPositive.setVisibility(View.VISIBLE);
            ////
            String wordLeft = "Please click";
            String wordMiddle = "Start";
            String wordRight = "to play next level";
            SpannableString spanLeft = new SpannableString(wordLeft);
            SpannableString spanMiddle = new SpannableString(wordMiddle);
            SpannableString spanRight = new SpannableString(wordRight);
            spanMiddle.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanMiddle.length(), 0);
            activityGameBinding.tvLevelResultPositive.setText(TextUtils.concat(spanLeft, " ", spanMiddle, " ", spanRight));
        } else {
            activityGameBinding.progressTimer.setIndicatorColor(getResources().getColor(R.color.red));
            activityGameBinding.tvSorry.setVisibility(View.VISIBLE);
            activityGameBinding.tvLevelResultNegative.setVisibility(View.VISIBLE);
        }
    }

    private void hideSummaryUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            activityGameBinding.progressGame.setProgress(0, true);
            activityGameBinding.progressTimer.setProgress(0, true);
        } else {
            activityGameBinding.progressTimer.setProgress(0);
            activityGameBinding.progressGame.setProgress(0);
        }
        activityGameBinding.tvLevelScore.setVisibility(View.INVISIBLE);
        activityGameBinding.tvCongrats.setVisibility(View.INVISIBLE);
        activityGameBinding.tvLevelResultPositive.setVisibility(View.INVISIBLE);
        activityGameBinding.tvSorry.setVisibility(View.INVISIBLE);
        activityGameBinding.tvLevelResultNegative.setVisibility(View.INVISIBLE);
        activityGameBinding.tvNextLevel.setVisibility(View.INVISIBLE);
    }

    private void initObserver() {
        gameViewModel.timer.observe(this, remainingSeconds -> {
            if (remainingSeconds * 2 > 80) {
                activityGameBinding.progressTimer.setIndicatorColor(getResources().getColor(R.color.red));
            } else {
                activityGameBinding.progressTimer.setIndicatorColor(getResources().getColor(R.color.green));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                activityGameBinding.progressTimer.setProgress(2 * remainingSeconds, true);
            } else {
                activityGameBinding.progressTimer.setProgress(2 * remainingSeconds);
            }
        });
        gameViewModel.loading.observe(this, isLoading -> {
            if (isLoading) {
                activityGameBinding.progressGame.setIndeterminate(true);
                activityGameBinding.tvGameIntro.setText(getString(R.string.trying_t0_fetch_questions_from_server));
                activityGameBinding.tvNextLevel.setText(getString(R.string.please_wait));
                activityGameBinding.tvRetry.setText(getString(R.string.please_wait));
            } else {
                activityGameBinding.progressGame.setIndeterminate(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    activityGameBinding.progressGame.setProgress(6, true);
                } else {
                    activityGameBinding.progressGame.setProgress(6);
                }
                activityGameBinding.tvNextLevel.setText(getString(R.string.start));
                activityGameBinding.tvRetry.setText(getString(R.string.retry));
            }
        });
        gameViewModel.isApiCallFailed.observe(this, isAPIFailed -> {
            if (isAPIFailed) {
                showErrorUI();
            } else {
                hideErrorUI();
            }
        });
    }

    private void showErrorUI() {
        activityGameBinding.tvWelcome.setText(getString(R.string.error));
        activityGameBinding.tvWelcome.setTextColor(getResources().getColor(R.color.red));
        activityGameBinding.tvGameIntro.setText(getString(R.string.something_went_wrong));
        activityGameBinding.tvNextLevel.setVisibility(View.INVISIBLE);
        activityGameBinding.tvRetry.setVisibility(View.VISIBLE);
    }

    private void hideErrorUI() {
        activityGameBinding.tvWelcome.setText(getString(R.string.welcome));
        activityGameBinding.tvWelcome.setTextColor(getResources().getColor(R.color.questionColor));
        String sentenceLeft = "Please click";
        String sentenceMiddle = "Start";
        String sentenceRight = "to play the game";
        SpannableString spanLeft = new SpannableString(sentenceLeft);
        SpannableString spanMiddle = new SpannableString(sentenceMiddle);
        SpannableString spanRight = new SpannableString(sentenceRight);
        spanMiddle.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanMiddle.length(), 0);
        activityGameBinding.tvGameIntro.setText(TextUtils.concat(spanLeft, " ", spanMiddle, " ", spanRight));
        activityGameBinding.tvNextLevel.setVisibility(View.VISIBLE);
        activityGameBinding.tvRetry.setVisibility(View.INVISIBLE);
    }

    private void initClickListeners() {
        activityGameBinding.tvAnswerOne.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - lastClickTime < 2000) {
                return;
            }
            if (newQuestion != null) {
                Boolean isCorrectAnswer = activityGameBinding.tvAnswerOne.getText().toString().equals(newQuestion.correctAnswer);
                animateAnswerButtons(activityGameBinding.tvAnswerOne, isCorrectAnswer, activityGameBinding.ivCorrectAnswerOne, activityGameBinding.ivWrongAnswerOne);
            }
            lastClickTime = SystemClock.elapsedRealtime();
        });
        activityGameBinding.tvAnswerTwo.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - lastClickTime < 2000) {
                return;
            }
            if (newQuestion != null) {
                Boolean isCorrectAnswer = activityGameBinding.tvAnswerTwo.getText().toString().equals(newQuestion.correctAnswer);
                animateAnswerButtons(activityGameBinding.tvAnswerTwo, isCorrectAnswer, activityGameBinding.ivCorrectAnswerTwo, activityGameBinding.ivWrongAnswerTwo);
            }
            lastClickTime = SystemClock.elapsedRealtime();
        });
        activityGameBinding.tvNextLevel.setOnClickListener(view -> {
            if (!gameViewModel.isGameStarted && gameViewModel.loading.getValue() != null && !gameViewModel.loading.getValue()) {
                gameViewModel.isGameStarted = true;
                hideIntroUI();
                showGameUI();
                getQuestion();
            }else{
                if (gameViewModel.loading.getValue() != null && !gameViewModel.loading.getValue()) {
                    hideSummaryUI();
                    showGameUI();
                    getQuestion();
                }
            }
        });
        activityGameBinding.tvRetry.setOnClickListener(view -> {
            if (gameViewModel.loading.getValue() != null && gameViewModel.loading.getValue()) {
                return;
            }
            gameViewModel.getNewQuestions();
        });
    }

    private void getQuestion() {
        newQuestion = gameViewModel.getQuestion();
        if (newQuestion == null) {
            hideGameUI();
            gameViewModel.getNewQuestions();
            showSummaryUI();
        } else {
            setQuestionType(0);
            int random = new Random().nextInt(1000);
            activityGameBinding.tvWord.setText(newQuestion.word);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                activityGameBinding.progressGame.setProgress(gameViewModel.currentLevelCount++, true);
            } else {
                activityGameBinding.progressGame.setProgress(gameViewModel.currentLevelCount++);
            }
            if (random % 2 == 0) {
                activityGameBinding.tvAnswerOne.setText(newQuestion.notRelatedWord);
                activityGameBinding.tvAnswerTwo.setText(newQuestion.relatedWord);
            } else {
                activityGameBinding.tvAnswerOne.setText(newQuestion.relatedWord);
                activityGameBinding.tvAnswerTwo.setText(newQuestion.notRelatedWord);
            }
            gameViewModel.startTimer();
        }
    }

    private void setQuestionType(int type) {
        if (type == 0) {
            String questionLeft = "Which word is";
            String questionMiddle = "similar";
            String questionRight = "to above?";
            SpannableString spanLeft = new SpannableString(questionLeft);
            SpannableString spanMiddle = new SpannableString(questionMiddle);
            SpannableString spanRight = new SpannableString(questionRight);
            spanMiddle.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanMiddle.length(), 0);
            activityGameBinding.tvQuestion.setText(TextUtils.concat(spanLeft, " ", spanMiddle, " ", spanRight));
        } else {
            String questionLeft = "Which one is the";
            String questionMiddle = "opposite";
            String questionRight = "of above word?";
            SpannableString spanLeft = new SpannableString(questionLeft);
            SpannableString spanMiddle = new SpannableString(questionMiddle);
            SpannableString spanRight = new SpannableString(questionRight);
            spanMiddle.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanMiddle.length(), 0);
            activityGameBinding.tvQuestion.setText(TextUtils.concat(spanLeft, " ", spanMiddle, " ", spanRight));
        }
    }

    private void animateAnswerButtons(TextView view, Boolean isCorrectAnswer, ImageView correctAnswer, ImageView wrongAnswer) {
        gameViewModel.stopTimer();
        int previousScore = gameViewModel.currentScore;
        view.setTextColor(getResources().getColor(R.color.white));
        int randomCurrentScore = new Random().nextInt(10) * 10;
        if(randomCurrentScore == 0) randomCurrentScore = 20;
        if (isCorrectAnswer) {
            if (gameViewModel.timer.getValue() != null && gameViewModel.timer.getValue() < 50) {
                ++gameViewModel.correctQuestionCount;
                gameViewModel.currentScore += randomCurrentScore;
            }
            view.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_correct_answer, null));
            correctAnswer.setVisibility(View.VISIBLE);
        } else {
            if (gameViewModel.timer.getValue() != null && !(gameViewModel.timer.getValue() >= 50)) {
                gameViewModel.currentScore -= randomCurrentScore;
            }
            view.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_wrong_answer, null));
            wrongAnswer.setVisibility(View.VISIBLE);
        }
        activityGameBinding.tvScoreNumber.setText(String.valueOf(gameViewModel.currentScore));
        if (previousScore > gameViewModel.currentScore) {
            activityGameBinding.tvScoreNumber.setTextColor(getResources().getColor(R.color.red));
            activityGameBinding.ivUp.setVisibility(View.INVISIBLE);
            activityGameBinding.ivDown.setVisibility(View.VISIBLE);
        } else {
            activityGameBinding.tvScoreNumber.setTextColor(getResources().getColor(R.color.green));
            activityGameBinding.ivUp.setVisibility(View.VISIBLE);
            activityGameBinding.ivDown.setVisibility(View.INVISIBLE);
        }
        final CountDownTimer waitTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                correctAnswer.setVisibility(View.INVISIBLE);
                wrongAnswer.setVisibility(View.INVISIBLE);
                view.setTextColor(getResources().getColor(R.color.answerColor));
                view.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_answers, null));
                getQuestion();
            }
        };
        waitTimer.start();
    }
}