package com.androdevlinux.percy.globaldcx.network.apis;
import com.androdevlinux.percy.globaldcx.utils.NativeUtils;

import okhttp3.ResponseBody;
import retrofit2.Callback;

/**
 * Created by percy on 18/11/17.
 */

public class BitfinexApiImpl extends AbstractBaseApi<BitfinexAPI> {

    private static BitfinexApiImpl bitfinexApiManager;
    private BitfinexAPI bitfinexAPI;
    private BitfinexApiImpl(){
        setBaseUrl(NativeUtils.getBitfinexBaseUrl());
        bitfinexAPI = getClient(BitfinexAPI.class);

    }

    public static BitfinexApiImpl getInstance(){
        if(bitfinexApiManager==null)
            bitfinexApiManager = new BitfinexApiImpl();
        return bitfinexApiManager;
    }

    public void getBitfinexData(String time, String symbol, Callback<ResponseBody> callback){
        bitfinexAPI.getBitfinexData(time, symbol).enqueue(callback);
    }
}
