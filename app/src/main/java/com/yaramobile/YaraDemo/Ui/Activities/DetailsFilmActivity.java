package com.yaramobile.YaraDemo.Ui.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yaramobile.YaraDemo.DataBase.DetailsFilmDao;
import com.yaramobile.YaraDemo.DataServices.FilmApi;
import com.yaramobile.YaraDemo.Models.FilmDesModel;
import com.yaramobile.YaraDemo.R;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsFilmActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private Context mContext;
    private FilmDesModel filmDesModel;
    private String imdbID;
    private DetailsFilmDao detailsFilmDao = new DetailsFilmDao();

    private LinearLayout consView;
    private ImageView imagePosterFilm;
    private TextView typeFilm;
    private TextView ratFilm;
    private TextView yearsFilm;
    private TextView timeFilm;
    private TextView showFilm;
    private TextView consFilm;
    private TextView lngFilm;
    private TextView counFilm;
    private TextView rangFilm;
    private TextView prodFilm;
    private TextView writFilm;
    private TextView actFilm;
    private TextView plotFilm;
    private TextView awaFilm;
    private TextView boxFilm;
    private TextView comFilm;
    private TextView textError;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_film);

        mContext = getApplicationContext();

        if (getIntent() != null)
            imdbID = getIntent().getStringExtra("Id");

        //region load ActionBar
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");

            setBind();

        }
        //endregion
    }

    @Override
    protected void onResume() {
        super.onResume();

        //region Check Online & Offline Data
        if (hasInternetConnection()) {

            loadItem();

        } else if (detailsFilmDao != null && detailsFilmDao.findById(imdbID) != null) {

            filmDesModel = detailsFilmDao.findById(imdbID);

            setItemView(filmDesModel);
//
        } else {
            textError.setVisibility(View.VISIBLE);
            consView.setVisibility(View.GONE);

        }
        //endregion
    }

    //region Check Connect to Internet
    public boolean hasInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }
    //endregion

    //region get Data in Online
    private void loadItem() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request originalRequest = chain.request();

            Request.Builder builder = originalRequest.newBuilder().header("Content-Type", "application/json");

            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.omdbapi.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FilmApi filmApi = retrofit.create(FilmApi.class);

        Call<FilmDesModel> call = filmApi.getFilmDes("3e974fca", imdbID);

        call.enqueue(new Callback<FilmDesModel>() {
            @Override
            public void onResponse(Call<FilmDesModel> call, Response<FilmDesModel> response) {
                if (response.isSuccessful()) {
                    filmDesModel = response.body();


                    assert filmDesModel != null;
                    setItemView(filmDesModel);
                    setSql(filmDesModel);

                } else {
                    assert response.errorBody() != null;
                    try {
                        Toast.makeText(DetailsFilmActivity.this, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<FilmDesModel> call, Throwable t) {

            }

        });

    }
    //endregion

    //region Cache to DataBase
    private void setSql(FilmDesModel filmDesModel) {
        detailsFilmDao.update(filmDesModel);
    }
    //endregion

    //region set Data to View
    private void setItemView(FilmDesModel filmDesModel) {

        actionBar.setTitle(filmDesModel.getTitle());

        Glide.with(mContext)
                .load(filmDesModel.getPoster())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .placeholder(mContext.getResources().getDrawable(R.drawable.sample))
                .into(imagePosterFilm);

        typeFilm.setText(filmDesModel.getType());
        ratFilm.setText(filmDesModel.getImdbRating());
        yearsFilm.setText(filmDesModel.getYear());
        timeFilm.setText(filmDesModel.getRuntime());
        showFilm.setText(filmDesModel.getReleased());
        consFilm.setText(filmDesModel.getGenre());
        lngFilm.setText(filmDesModel.getLanguage());
        counFilm.setText(filmDesModel.getCountry());
        rangFilm.setText(filmDesModel.getRated());
        prodFilm.setText(filmDesModel.getDirector());
        writFilm.setText(filmDesModel.getWriter());
        actFilm.setText(filmDesModel.getActors());
        plotFilm.setText(filmDesModel.getPlot());
        awaFilm.setText(filmDesModel.getAwards());
        boxFilm.setText(filmDesModel.getBoxOffice());
        comFilm.setText(filmDesModel.getProduction());

    }
    //endregion

    //region Bind All View
    private void setBind() {

        consView = findViewById(R.id.cons_view);
        imagePosterFilm = findViewById(R.id.image_poster_film);
        typeFilm = findViewById(R.id.type_film);
        ratFilm = findViewById(R.id.rat_film);
        yearsFilm = findViewById(R.id.years_film);
        timeFilm = findViewById(R.id.time_film);
        showFilm = findViewById(R.id.show_film);
        consFilm = findViewById(R.id.cons_film);
        lngFilm = findViewById(R.id.lng_film);
        counFilm = findViewById(R.id.coun_film);
        rangFilm = findViewById(R.id.rang_film);
        prodFilm = findViewById(R.id.prod_film);
        writFilm = findViewById(R.id.writ_film);
        actFilm = findViewById(R.id.act_film);
        plotFilm = findViewById(R.id.plot_film);
        awaFilm = findViewById(R.id.awa_film);
        boxFilm = findViewById(R.id.box_film);
        comFilm = findViewById(R.id.com_film);
        textError = findViewById(R.id.text_error);

        textError.setVisibility(View.GONE);
        consView.setVisibility(View.VISIBLE);

    }
    //endregion

    //region load Menu to back Button
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    //endregion


    @Override
    protected void onDestroy() {
        super.onDestroy();

        //region Close Db
        detailsFilmDao.close();
        //endregion
    }
}
