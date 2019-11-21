package fi.delektre.ringa.ring_thesis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.viewpager.widget.ViewPager;
import fi.delektre.ringa.ring_thesis.data.RxBus;
import fi.delektre.ringa.ring_thesis.ui.CreateProfileDataFragment_;
import fi.delektre.ringa.ring_thesis.ui.CreateProfileNameFragment_;
import fi.delektre.ringa.ring_thesis.util.DataType;
import fi.delektre.ringa.ring_thesis.adapters.SectionStatePagerAdapter;
import fi.delektre.ringa.ring_thesis.util.UserDataCollection;

import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_user_data)
public class UserData extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;


    @ViewById(R.id.user_data_fragment_holder)
    ViewPager viewPager;

    @ViewById(R.id.set_profile_picture)
    TextView setProfilePicture;


    @ViewById(R.id.user_data_layout)
    ConstraintLayout userDataLayout;

    @ViewById(R.id.profile_pic_set_data)
    ImageView profilePicSetData;

    //Adapter
    private SectionStatePagerAdapter mSectionStateAdapter;

    //Widgets
    private Button mSubmit;
    private TextInputEditText mName;
    private TextInputEditText mAge;
    private TextInputEditText mWeight;
    private TextInputEditText mHeight;

    //User Vars
    protected String userFirstname;

    //User Data
    String UserID;
    String userHeight;
    String userWeight;
    String userBirthday;
    String userName;
    String userGender;
    UserDataCollection.UserHeight userHeightCollection;
    UserDataCollection.UserWeight userWeighthCollection;
    UserDataCollection.UserBirthday userBirthdayCollection;
    UserDataCollection.UserGender userGenderCollection;
    UserDataCollection.UserName userNameCollection;
    UserDataCollection[] dataCollectionArray;

    //Firebase Vars
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mReference;
    private FirebaseUser currentUser;

    @AfterViews
    void initialization() {

        //Setting up the adapter for fragments
        setupAdapter(viewPager);

        //Declare Firebase Database Reference:
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        UserID = currentUser.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference();
        dataCollectionArray = new UserDataCollection[5];
        subscribeToUserData();

    }


    void setupAdapter(ViewPager viewPager) {
        SectionStatePagerAdapter adapter = new SectionStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CreateProfileNameFragment_(), "ProfileNameFragment");
        adapter.addFragment(new CreateProfileDataFragment_(), "ProfileDataFragment");
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber) {
        viewPager.setCurrentItem(fragmentNumber);
    }

    private void subscribeToUserData() {
        RxBus.subscribe((userDataCollection) -> {
            if (userDataCollection instanceof UserDataCollection) {
                UserDataCollection userData = (UserDataCollection) userDataCollection;
                mReference.child(UserID).child(DataType.DataLabel[userData.getDataType()]).setValue(userData.getUserData());
                switch (((UserDataCollection) userDataCollection).getDataType()) {
                    case DataType.HEIGHT: {
                        userHeightCollection = (UserDataCollection.UserHeight) userDataCollection;
                        userHeight = userHeightCollection.getUserData();
                        dataCollectionArray[DataType.HEIGHT] = userHeightCollection;
                        break;
                    }
                    case DataType.WEIGHT: {
                        userWeighthCollection = (UserDataCollection.UserWeight) userDataCollection;
                        userWeight = userWeighthCollection.getUserData();
                        dataCollectionArray[DataType.WEIGHT] = userWeighthCollection;
                        break;
                    }
                    case DataType.BIRTHDAY: {
                        userBirthdayCollection = (UserDataCollection.UserBirthday) userDataCollection;
                        userBirthday = userBirthdayCollection.getUserData();
                        dataCollectionArray[DataType.BIRTHDAY] = userBirthdayCollection;
                        break;
                    }
                    case DataType.GENDER: {
                        userGenderCollection = (UserDataCollection.UserGender) userDataCollection;
                        userGender = userGenderCollection.getUserData();
                        dataCollectionArray[DataType.GENDER] = userGenderCollection;
                        break;
                    }
                    case DataType.NAME: {
                        userNameCollection = (UserDataCollection.UserName) userDataCollection;
                        userName = userNameCollection.getUserData();
                        dataCollectionArray[DataType.NAME] = userNameCollection;
                        break;
                    }
                }
            }
        });
    }
    public void submitAndFinish(){
        for (int i=0; i < dataCollectionArray.length; i++){
            if (dataCollectionArray[i] == null){
                break;
            }
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity_.class);
        startActivity(intent);
    }


}