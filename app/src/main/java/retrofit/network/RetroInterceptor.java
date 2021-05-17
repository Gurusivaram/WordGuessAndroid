package retrofit.network;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import application.WGApplication;
import okhttp3.Interceptor;
import okhttp3.Response;

public class RetroInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        if (!WGApplication.isNetworkConnected())
            throw new IOException("Network Error");
        Response response = chain.proceed(chain.request().newBuilder().build());
        int retryCount = 0;
        while (retryCount++ < 3 && !response.isSuccessful()) {
            response = chain.proceed(chain.request().newBuilder().build());
        }
        return response;
    }
}
