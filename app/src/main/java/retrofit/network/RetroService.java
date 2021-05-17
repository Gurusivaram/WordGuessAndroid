package retrofit.network;

import java.util.List;

import io.reactivex.Single;
import retrofit.model.RetroWord;
import retrofit2.http.GET;

public interface RetroService {
    @GET("/get_round")
    Single<List<RetroWord>> getWords();
}
