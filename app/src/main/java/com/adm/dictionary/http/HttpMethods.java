package com.adm.dictionary.http;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by Administrator on 2016/10/14.
 */
public class HttpMethods {

    private static HttpMethods instance;


    private ApiService service4;


    private HttpMethods() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Retrofit retrofit4 = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://192.168.0.151:9090") // "http://192.168.0.151:9090" http://wyx.gege5.cn
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service4 = retrofit4.create(ApiService.class);
    }

    public static HttpMethods getInstance() {
        if (instance == null) {
            synchronized (HttpMethods.class) {
                if (instance == null) {
                    instance = new HttpMethods();
                }
            }
        }
        return instance;
    }

    /**
     * 用户登录接口
     * @param phone
     * @param password
     * @return
     */
    public Observable<ResponseBody> login(String phone, String password) {
        return service4.login(phone, password);
    }

    public Observable<ResponseBody> todayReciteTask(String userId, String token, String groupId, int rows){
        return service4.todayReciteTask(userId, token, groupId, rows);
    }

    public Observable<ResponseBody> getSetting(String userId, String token) {
        return service4.getSetting(userId, token);
    }

    public Observable<ResponseBody> modifyReciteModel(String userId, String token, Integer reciteModel) {
        return service4.modifyReciteModel(userId, token, String.valueOf(reciteModel));
    }

    public Observable<ResponseBody> getQuestionGroups(String userId, String token) {
        return service4.getQuestionGroups(userId, token);
    }


    public Observable<ResponseBody> getQuestionListByGroupId(String userId, String token, String groupId) {
        return service4.getQuestionListByGroupId(userId, token, groupId);
    }

    public Observable<ResponseBody> modifyReciteRecord(String userId, String token, Long recordId, String status) {
        return service4.modifyReciteRecord(userId, token, recordId, status);
    }

    public Observable<ResponseBody> modifyAnswer(String userId, String token, Integer id, String answer) {
        return service4.modifyAnswer(userId, token, id, answer);
    }

    public Observable<ResponseBody> getMineQuestionGroups(String userId, String token) {
        return service4.getMineQuestionGroups(userId, token);
    }

    public Observable<ResponseBody> addGroupToMine(String userId, String token, String groupId) {
        return service4.addGroupToMine(userId, token, groupId);
    }

    public Observable<ResponseBody> removeGroupFromMine(String userId, String token, String groupId) {
        return service4.removeGroupFromMine(userId, token, groupId);
    }
}
