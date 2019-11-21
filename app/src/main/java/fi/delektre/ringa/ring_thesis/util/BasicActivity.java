package fi.delektre.ringa.ring_thesis.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import fi.delektre.ringa.ring_thesis.MainActivity_;
import fi.delektre.ringa.ring_thesis.ProfileActivity_;
import fi.delektre.ringa.ring_thesis.R;
import fi.delektre.ringa.ring_thesis.SettingsActivity_;
import fi.delektre.ringa.ring_thesis.UserHistory_;

@EActivity()
public abstract class BasicActivity extends AppCompatActivity {

    public NavigationView drawerList;
    public ActionBarDrawerToggle drawerToggle;
    public DrawerLayout drawerLayout;
    public FrameLayout frameLayout;
    public Toolbar toolbar;
    public String currentClassName;

    @AfterViews
    protected void onCreateDrawer() {
        // R.id.drawer_layout should be in every activity with exactly the same id.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (NavigationView) findViewById(R.id.nav_view);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerToggle = new ActionBarDrawerToggle((Activity) this, drawerLayout, 0, 0)
        {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float moveFactor = (drawerList.getWidth() * slideOffset);
                frameLayout.setTranslationX(moveFactor);
            }

            public void onDrawerClosed(View view)
            {
                getSupportActionBar().setTitle(R.string.app_name);
            }

            public void onDrawerOpened(View drawerView)
            {
                getSupportActionBar().setTitle(R.string.app_name);
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerList.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.nav_history:
                        if (currentClassName.equals("UserHistory_")){
                            drawerLayout.closeDrawers();
                            break;
                        } else {
                            Intent intent = new Intent(getApplicationContext(), UserHistory_.class);
                            startActivity(intent);
                            break;
                        }
                    case R.id.nav_dashboard:
                        Toast.makeText(getApplicationContext(), currentClassName,
                                Toast.LENGTH_LONG).show();
                        if (currentClassName.equals("MainActivity_")){
                            drawerLayout.closeDrawers();
                            break;
                        } else {
                            Intent intent2 = new Intent(getApplicationContext(), MainActivity_.class);
                            startActivity(intent2);
                            break;
                        }
                    case R.id.nav_settings:
                        if (currentClassName.equals("SettingsActivity_")){
                            drawerLayout.closeDrawers();
                            break;
                        } else {
                            Intent intent3 = new Intent(getApplicationContext(), SettingsActivity_.class);
                            startActivity(intent3);
                            break;
                        }
                    case R.id.nav_profile:
                        if (currentClassName.equals("ProfileActivity_")){
                            drawerLayout.closeDrawers();
                            break;
                        } else {
                            Intent intent4 = new Intent(getApplicationContext(), ProfileActivity_.class);
                            startActivity(intent4);
                            break;
                        }
                }

                return true;
            }
        });
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_settings:
                Intent intent = new Intent(this, SettingsActivity_.class);
                startActivity(intent);

        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStart() {
        super.onStart();
        currentClassName = getClass().getSimpleName();
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentClassName = null;
    }
}
