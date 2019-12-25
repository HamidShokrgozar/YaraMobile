package com.yaramobile.YaraDemo.DataBase;

import android.util.Log;

import com.yaramobile.YaraDemo.Models.FilmDesModel;

import io.realm.Realm;
import io.realm.RealmResults;

public class DetailsFilmDao {

    private static String tag = "REAL_TAG";

    private Realm realm;

    public DetailsFilmDao(){
        this.realm=Realm.getDefaultInstance();
    }




    public void update(final FilmDesModel filmDesModel) {
        realm.executeTransaction(realm -> realm.copyToRealmOrUpdate(filmDesModel));
    }


    public RealmResults<FilmDesModel> findAll() {
        RealmResults<FilmDesModel> realmResults = realm.where(FilmDesModel.class).findAll();

        for (FilmDesModel filmDesModel : realmResults) {
            try {
                Log.i(tag, filmDesModel.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return realmResults;
    }

    public FilmDesModel findById(String id) {
        FilmDesModel filmDesModel = realm.where(FilmDesModel.class)
                .equalTo("imdbID", id)
                .findFirst();

        try {
            assert filmDesModel != null;
            Log.i(tag, filmDesModel.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filmDesModel;
    }


    public void close() {
        realm.close();

    }
}
