package fi.delektre.ringa.ring_thesis;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.graphics.SurfaceTexture;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity{

    @ViewById(R.id.login_edit_email)
    TextInputEditText E_Email;

    @ViewById(R.id.login_edit_password)
    TextInputEditText E_Password;

    @ViewById(R.id.login_screen_texture)
    TextureView T1_View;

    @ViewById(R.id.login_enter)
    MaterialButton loginButton;

    @ViewById(R.id.login_create)
    MaterialButton createButton;

    @Click(R.id.login_create)
    void createPressed(){
        if((E_Email != null) && E_Password != null) {
            create(E_Email.getText().toString(), E_Password.getText().toString());
        }
    }

    @Click(R.id.login_enter)
    void loginPressed(){
        if((E_Email != null) && E_Password != null) {
            signing(E_Email.getText().toString(), E_Password.getText().toString());
        }
    }

    private String userName;

    //Firebase Vars
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mReference;

    //View vars
    private Surface S1_Surface;
    private static final String TAG = "MainActivity";
    Uri mediaPlayerUri;
    MediaPlayer M1_Player;

    @AfterViews
    void initialization(){

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference();
        mediaPlayerUri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.clock_spin);
        M1_Player = new MediaPlayer();
        M1_Player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setVolume(0, 0);
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
        });
        T1_View.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                S1_Surface = new Surface(surfaceTexture);

                try {
                    M1_Player.setDataSource(LoginActivity.this, mediaPlayerUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                M1_Player.setSurface(S1_Surface);
                M1_Player.setVideoScalingMode(2);
                scaleVideo(M1_Player);
                M1_Player.prepareAsync();

            }
            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
            }
            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }
            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }
        });





        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userName = ((String) dataSnapshot.child("Name").getValue());
                    Toast.makeText(LoginActivity.this, userName, Toast.LENGTH_SHORT).show();
                    if (userName != null){
                        Intent intent = new Intent(LoginActivity.this, MainActivity_.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayer M1_Player = new MediaPlayer();
        M1_Player.pause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void scaleVideo(MediaPlayer mediaPlayer) {

        LayoutParams videoParams = (LayoutParams) T1_View
                .getLayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        LoginActivity.this.getWindowManager().getDefaultDisplay()
                .getMetrics(dm);

        final int height = dm.heightPixels;
        final int width = dm.widthPixels;
        int videoHeight = mediaPlayer.getVideoHeight();
        int videoWidth = mediaPlayer.getVideoWidth();
        double hRatio = 1;

        hRatio = (height * 1.0 / videoHeight) / (width * 1.0 / videoWidth);
        int videoParam_X = (int) (hRatio <= 1 ? 0 : Math.round((-(hRatio - 1) / 2)
                * width));
        int videoParam_Y = (int) (hRatio >= 1 ? 0 : Math
                .round((((-1 / hRatio) + 1) / 2) * height));
        videoParams.width = width - videoParam_X - videoParam_X;
        videoParams.height = height - videoParam_Y - videoParam_Y;
        Log.e(TAG, "x:" + videoParam_X + " y:" + videoParam_Y);
        T1_View.setScaleX(1.00001f);//<-- this line enables smoothing of the picture in TextureView.
        T1_View.requestLayout();
        T1_View.invalidate();
    }

    public void signing(String Email, String Password) {
        mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity_.class);
                                startActivity(intent);
                            }
                        } else
                            Toast.makeText(LoginActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();


                    }
                }
        );
    }

    public void create(String Email, String Password){
        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, "Signing In", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user!= null) {
                                String UserID = user.getUid();
                                mReference.child(UserID).setValue("true");
                                mReference.child(UserID).child("Name").setValue(null);
                                mReference.child(UserID).child("Age").setValue(null);
                                mReference.child(UserID).child("Height").setValue(null);
                                mReference.child(UserID).child("Weight").setValue(null);
                                Intent intent = new Intent(LoginActivity.this, UserData_.class);
                                startActivity(intent);
                            }
                        } else
                            Toast.makeText(LoginActivity.this, "Error, Account not created", Toast
                                    .LENGTH_SHORT).show();
                    }
                }
        );
    }

}
