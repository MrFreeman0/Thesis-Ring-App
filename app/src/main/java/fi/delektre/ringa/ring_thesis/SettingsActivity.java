package fi.delektre.ringa.ring_thesis;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.PreferenceScreen;


@PreferenceScreen(R.xml.preferences)
@EActivity
public class SettingsActivity extends PreferenceActivity{
    private final String TAG = "SettingsActivity";
    public static String PREF_NAME = "RingPrefs";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    public void clickLogoutButton (View v){
        mAuth.signOut();
        currentUser = mAuth.getCurrentUser();
        Toast.makeText(SettingsActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
        if (currentUser == null){
            Intent intent = new Intent(SettingsActivity.this, LoginActivity_.class);
            startActivity(intent);
        }
    }

}
