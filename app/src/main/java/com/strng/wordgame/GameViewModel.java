package com.strng.wordgame;

import android.app.Application;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import repository.Repository;
import retrofit.model.RetroWord;

public class GameViewModel extends AndroidViewModel {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final String TAG = "GameViewModel";
    public int levelNumber = 1;
    public int currentScore = 0;
    public int currentLevelCount = 1;
    public int correctQuestionCount = 0;
    private int currentQuestionIndex = 0;
    public boolean isGameStarted = false;
    public MutableLiveData<Integer> timer = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();
    public MutableLiveData<Boolean> isApiCallFailed = new MutableLiveData<>();
    public MutableLiveData<List<RetroWord>> wordList = new MutableLiveData<>();
    private final CountDownTimer countDownTimer = new CountDownTimer(5000, 100) {
        public void onTick(long millisUntilFinished) {
            timer.setValue(50 - (int) (millisUntilFinished / 100));
        }

        public void onFinish() {
            timer.setValue(50);
        }
    };

    public GameViewModel(@NonNull Application application) {
        super(application);
    }

    public void getNewQuestions() {
        loading.setValue(true);
        try {
            Disposable disposable = Repository.getWords().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::success, this::failure);
            compositeDisposable.add(disposable);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void failure(Throwable throwable) {
        Log.v(TAG, "API Call Failed");
        correctQuestionCount = 0;
        isApiCallFailed.setValue(true);
        loading.setValue(false);
    }

    private void success(List<RetroWord> retroWords) {
        Log.v(TAG, "API Call Success");
        correctQuestionCount = 0;
        wordList.setValue(retroWords);
        isApiCallFailed.setValue(false);
        loading.setValue(false);
    }

    public RetroWord getQuestion() {
        if (currentQuestionIndex < Objects.requireNonNull(wordList.getValue()).size()) {
            return wordList.getValue().get(currentQuestionIndex++);
        }
        currentQuestionIndex = 0;
        currentLevelCount = 1;
        return null;
    }

    public void startTimer() {
        countDownTimer.start();
    }

    public void stopTimer() {
        countDownTimer.cancel();
    }

//    private void disposeComposite() {
//        compositeDisposable.dispose();
//    }
}
