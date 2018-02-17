package com.androdevlinux.percy.globaldcx.network.apis;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by percy on 22/11/17.
 */

public interface BitfinexAPI {
    @GET("/v2/candles/trade:{time}:{symbol}/hist/")
    Call<ResponseBody> getBitfinexData(@Path(value = "time", encoded = true) String time, @Path(value = "symbol", encoded = true) String symbol);
}
