package com.yaramobile.YaraDemo.Ui.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yaramobile.YaraDemo.DataBase.SearchSqlDao;
import com.yaramobile.YaraDemo.DataServices.FilmApi;
import com.yaramobile.YaraDemo.Models.FilmListModel;
import com.yaramobile.YaraDemo.Models.Search;
import com.yaramobile.YaraDemo.R;
import com.yaramobile.YaraDemo.Tools.OnItemClickListener;
import com.yaramobile.YaraDemo.Ui.Adapters.FilmListAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {

    private RecyclerView mRecyclerFilm;
    private FilmListAdapter filmListAdapter;
    private TextView textError;
    private FilmListModel filmListModel;
    private Context mContext;
    private SearchSqlDao searchSqlDao = new SearchSqlDao();
    private List<Search> mSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        mSearch = new ArrayList<>();

        mRecyclerFilm = findViewById(R.id.recycler_film);
        textError = findViewById(R.id.text_error);

        textError.setVisibility(View.GONE);
        mRecyclerFilm.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //region Check Online & Offline Data
        if (hasInternetConnection()) {

            loadList();

        } else if (searchSqlDao != null && searchSqlDao.findAll().size() > 0) {

            mSearch = searchSqlDao.findAll();

            setRecycler(mSearch);

        } else {
            textError.setVisibility(View.VISIBLE);
            mRecyclerFilm.setVisibility(View.GONE);

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
    private void loadList() {

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

        Call<FilmListModel> call = filmApi.getFilmList("3e974fca", "batman");

        call.enqueue(new Callback<FilmListModel>() {
            @Override
            public void onResponse(Call<FilmListModel> call, Response<FilmListModel> response) {
                if (response.isSuccessful()) {
                    filmListModel = response.body();

                    assert filmListModel != null;
                    mSearch = filmListModel.getSearch();

                    setRecycler(mSearch);
                    setSql(mSearch);

                } else {
                    assert response.errorBody() != null;
                    try {
                        Toast.makeText(MainActivity.this, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<FilmListModel> call, Throwable t) {

            }


        });

    }
    //endregion


    //region Set data in RecyclerView
    private void setRecycler(List<Search> search) {

        int gridNum = getResources().getInteger(R.integer.num_gridL);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), gridNum/*3*/);

        mRecyclerFilm.setLayoutManager(gridLayoutManager);

        filmListAdapter = new FilmListAdapter(getApplicationContext(), search);
        mRecyclerFilm.hasFixedSize();
        mRecyclerFilm.setAdapter(filmListAdapter);
        mRecyclerFilm.setItemAnimator(new DefaultItemAnimator());

        filmListAdapter.setOnItemClickListener(this);

    }
    //endregion


    //region Cache to DataBase
    private void setSql(List<Search> searches) {
        for (int i = 0; i < searches.size(); i++) {
            searchSqlDao.update(searches.get(i));
        }

    }
    //endregion


    @Override
    protected void onDestroy() {
        super.onDestroy();

        //region Close Db
        searchSqlDao.close();
        //endregion
    }

    //region Onclick for select Item
    @Override
    public void onItemClick(View view, int position) {
        String id = filmListAdapter.getItem(position).getImdbID();
        Intent intent = new Intent(this, DetailsFilmActivity.class);
        intent.putExtra("Id", id);
        startActivity(intent);
    }
    //endregion
}
