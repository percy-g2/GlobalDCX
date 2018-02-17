package com.androdevlinux.percy.globaldcx.network;

import com.androdevlinux.percy.globaldcx.network.apis.BitfinexApiImpl;

import okhttp3.ResponseBody;
import retrofit2.Callback;

/**
 * Created by percy on 17/2/18.
 */

public class ApiManager {
    private static ApiManager apiManager;
    private BitfinexApiImpl bitfinexApiImpl;

    private ApiManager() {
        bitfinexApiImpl = BitfinexApiImpl.getInstance();
    }

    public static ApiManager getInstance() {
        if (apiManager == null) {
            apiManager = new ApiManager();
        }
        return apiManager;
    }

    public void getBitfinexData(String time, String symbol, Callback<ResponseBody> callback) {
        bitfinexApiImpl.getBitfinexData(time, symbol, callback);
    }
}
