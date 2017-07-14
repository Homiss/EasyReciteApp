package com.adm.dictionary.http;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;


/**
 * 所有的网络请求方法
 * Created by Administrator on 2016/10/14.
 */
public interface ApiService {

    /**
     * 用户登录接口
     * @param phone
     * @param password
     * @return
     */
    @POST("/easyRecite/api/app/user/v1/login")
    Observable<ResponseBody> login(@Query("phone") String phone, @Query("password") String password);

    /**
     * 获取当日需要背诵题库列表
     * @param groupId
     * @return
     */
    @POST("/easyRecite/api/app/recite/v1/today/task")
    Observable<ResponseBody> todayReciteTask(@Query("userId") String userId, @Query("token") String token,
                                                  @Query("groupId") String groupId, @Query("rows") int rows);

    /**
     * 获取用户setting
     * @param userId
     * @return
     */
    @POST("/easyRecite/api/app/setting/v1")
    Observable<ResponseBody> getSetting(@Query("userId") String userId, @Query("token") String token);


    @POST("/easyRecite/api/app/setting/v1/modify/model")
    Observable<ResponseBody> modifyReciteModel(@Query("userId") String userId, @Query("token") String token, @Query("model") String model);

    /**
     * 获取题库列表
     * @param userId
     * @return
     */
    @POST("/easyRecite/api/app/question/v1/groups")
    Observable<ResponseBody> getQuestionGroups(@Query("userId") String userId, @Query("token") String token);

    /**
     * 获取题库题目列表
     * @param userId
     * @param token
     * @param groupId
     * @return
     */
    @POST("/easyRecite/api/app/question/v1/groups/list")
    Observable<ResponseBody> getQuestionListByGroupId(@Query("userId") String userId, @Query("token") String token, @Query("groupId") String groupId);

    /**
     * 获取我的题库列表
     * @param userId
     * @param token
     * @return
     */
    @POST("/easyRecite/api/app/question/v1/groups/mine")
    Observable<ResponseBody> getMineQuestionGroups(@Query("userId") String userId, @Query("token") String token);

    /**
     * 修改当前记录状态
     * @param userId
     * @param token
     * @param recordId
     * @param status
     * @return
     */
    @POST("/easyRecite/api/app/recite/v1/modify/record")
    Observable<ResponseBody> modifyReciteRecord(@Query("userId") String userId, @Query("token") String token,
                                                @Query("id") Long recordId, @Query("status") String status);

    /**
     * 修改题目答案
     * @param userId
     * @param token
     * @param id
     * @param answer
     * @return
     */
    @Headers("Content-type:application/x-www-form-urlencoded;charset=UTF-8")
    @FormUrlEncoded
    @POST("/easyRecite/api/app/question/v1/modify/answer")
    Observable<ResponseBody> modifyAnswer(@Field("userId") String userId, @Field("token") String token,
                                          @Field("id") Integer id, @Field("answer") String answer);

    /**
     * 当前题库添加到我的题库里
     * @param userId
     * @param token
     * @param groupId
     * @return
     */
    @POST("/easyRecite/api/app/question/v1/group/addToMine")
    Observable<ResponseBody> addGroupToMine(@Query("userId") String userId, @Query("token") String token,
                                            @Query("groupId") String groupId);

    /**
     * 将当前题库从我的题库里移除
     * @param userId
     * @param token
     * @param groupId
     * @return
     */
    @POST("/easyRecite/api/app/question/v1/group/removeFromMine")
    Observable<ResponseBody> removeGroupFromMine(@Query("userId") String userId, @Query("token") String token,
                                                 @Query("groupId") String groupId);

    /**
     * 修改默认题库
     * @param userId
     * @param token
     * @param groupId
     * @return
     */
    @POST("/easyRecite/api/app/setting/v1/modify/group")
    Observable<ResponseBody> modifyReciteGroup(@Query("userId") String userId,
                                               @Query("token") String token,
                                               @Query("groupId") Integer groupId);

    /**
     * 获取我的已背的题目列表
     * @param userId
     * @param token
     * @return
     */
    @POST("/easyRecite/api/app/recite/v1/finish/record")
    Observable<ResponseBody> getReciteQuestions(@Query("userId") String userId,
                                                @Query("token") String token);
}
