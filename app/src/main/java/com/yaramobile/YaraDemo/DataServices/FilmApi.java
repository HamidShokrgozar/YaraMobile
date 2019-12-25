package com.yaramobile.YaraDemo.DataServices;


import com.yaramobile.YaraDemo.Models.FilmDesModel;
import com.yaramobile.YaraDemo.Models.FilmListModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface  FilmApi {

    @GET(".")
    Call<FilmListModel> getFilmList(@Query("apikey")String apikey , @Query("s")String filmName);

    @GET(".")
    Call<FilmDesModel> getFilmDes(@Query("apikey")String apikey , @Query("i")String imdbID);
}

