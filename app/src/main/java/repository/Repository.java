package repository;

import java.util.List;

import io.reactivex.Observable;
import retrofit.model.RetroWord;
import retrofit.network.RetroService;
import retrofit.network.RetrofitClientInstance;

public class Repository {
    private static final RetroService service = RetrofitClientInstance.getRetrofitInstance().create(RetroService.class);

    public static Observable<List<RetroWord>> getWords() {
        return service.getWords().toObservable();
    }
}
