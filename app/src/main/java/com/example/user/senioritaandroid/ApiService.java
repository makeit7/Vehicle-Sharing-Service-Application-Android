package com.example.user.senioritaandroid;

import com.example.user.senioritaandroid.Client.Request;
import com.example.user.senioritaandroid.Driver.Offer;
import com.example.user.senioritaandroid.User.Token;
import com.example.user.senioritaandroid.User.User;

import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("/oauth/token")
    @FormUrlEncoded
    @Headers({
            "Accept: application/json"
    })
    Single<Token> loginAccount(@Field("grant_type") String grantType,
                               @Field("username") String username,
                               @Field("password") String password,
                               @Header("Authorization") String authKey,
                               @Header("Content-Type") String contentType,
                               @Header("noToken") Boolean noToken);

    @GET("/getUser")
    @Headers({
            "Accept: application/json"
    })
    Single<User> getUser();

    @GET("/bake/images/{location}")
    Single<ResponseBody> getImage(@Path("location") String location);

    @GET("/getRequests")
    @Headers({
            "Accept: application/json"
    })
    Single<List<Request>> getRequests();

    @POST("/putOffer")
    Single<String> putOffer(@Body Offer offer);

    @POST("/updateUser")
    Single<String> updateUser(@Body User user);
}
