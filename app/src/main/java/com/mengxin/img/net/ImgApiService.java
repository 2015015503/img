package com.mengxin.img.net;

import com.alibaba.fastjson.JSONObject;
import com.mengxin.img.data.dto.Author;
import com.mengxin.img.data.dto.Img;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ImgApiService {
    @GET("图片/getIllList/{num}")
    Observable<ArrayList<Img>> fetchIllImg(
            @Path("num") int num
    );
    @GET("author/getAuthorDetail/{id}")
    Observable<Author> getAuthorDetail(
            @Path("id") long id
    );
    @GET("图片/getById/{id}")
    Observable<Img> getImgDetail(
            @Path("id") long id
    );

    @POST("author/loginByPassWord")
    Observable<String> loginByPassWord(
            @Body Author author
    );

    @POST("author/regist")
    Observable<Boolean> regist(
            @Body JSONObject object
    );

    @GET("author/isNameUse/{name}")
    Observable<Boolean> isNameUse(
            @Path("name") String name
    );

    @POST("author/getVcode")
    Observable<JSONObject> getVcode(
            @Body String phoneNumber
    );

    @GET("图片/getHeadImg/{id}")
    Observable<String> getHeadImg(
            @Path("id") Long id
    );

    @POST("author/likeImg")
    Observable<Boolean> likeImg(
            @Body JSONObject object
    );

    @POST("author/unLikeImg")
    Observable<Boolean> unLikeImg(
            @Body JSONObject object
    );

    @POST("author/isLikeImg")
    Observable<Boolean> isLikeImg(
            @Body JSONObject object
    );
}
