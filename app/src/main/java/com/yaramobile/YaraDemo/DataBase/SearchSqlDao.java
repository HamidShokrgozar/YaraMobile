package com.yaramobile.YaraDemo.DataBase;


import android.util.Log;

import com.yaramobile.YaraDemo.Models.Search;

import io.realm.Realm;
import io.realm.RealmResults;

public class SearchSqlDao {

    private static String tag = "REAL_TAG";

    private Realm realm;

    public SearchSqlDao() {
        this.realm = Realm.getDefaultInstance();
    }

    public void update(final Search searchSql) {
        realm.executeTransaction(realm -> realm.copyToRealmOrUpdate(searchSql));
    }

    public RealmResults<Search> findAll() {
        RealmResults<Search> realmResults = realm.where(Search.class).findAll();

        for (Search searchSql : realmResults) {
            try {
                Log.i(tag, searchSql.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return realmResults;
    }

    public Search findById(String id) {
        Search search = realm.where(Search.class)
                .equalTo("imdbID", id)
                .findFirst();

        try {
            assert search != null;
            Log.i(tag, search.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return search;
    }


    public void close() {
        realm.close();

    }

}
