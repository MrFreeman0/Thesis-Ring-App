package fi.delektre.ringa.ring_thesis.util;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class FirebasePersistence extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}