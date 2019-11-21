package fi.delektre.ringa.ring_thesis;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import fi.delektre.ringa.ring_thesis.data.RxBus;
import fi.delektre.ringa.ring_thesis.ui.DatePickerDialog;
import fi.delektre.ringa.ring_thesis.ui.DatePickerDialog_;
import fi.delektre.ringa.ring_thesis.ui.GenderPickerDialog;
import fi.delektre.ringa.ring_thesis.ui.GenderPickerDialog_;
import fi.delektre.ringa.ring_thesis.ui.HeightPickerDialog;
import fi.delektre.ringa.ring_thesis.ui.HeightPickerDialog_;
import fi.delektre.ringa.ring_thesis.ui.WeightPickerDialog;
import fi.delektre.ringa.ring_thesis.ui.WeightPickerDialog_;
import fi.delektre.ringa.ring_thesis.util.DataType;
import fi.delektre.ringa.ring_thesis.util.UserDataCollection;

@EActivity(R.layout.activity_profile_layout)
public class ProfileActivity extends AppCompatActivity  {
    //Camera feature request
    static final int REQUEST_IMAGE_CAPTURE = 1;




    // Firebase stuff

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mReference;
    private FirebaseDatabase mFirebaseDatabase;
    private String UserID;
    private UploadTask uploadTask;
    private StorageReference storageRef;
    private StorageReference userFolder;



    // User data-related variables
    String userName;
    String userFirstname;
    String userSurname;
    static String userHeight;
    static String userWeight;
    static String userBirthday;
    static String userGender;
    String picturePath;
    Uri photoURI;
    String imageFileName;


    private File profilePictureFile;
    private File profilePicturePath;




    @ViewById(R.id.profileLayout)
    ConstraintLayout profileLayout;

    @ViewById(R.id.profile_surname)
    TextInputLayout profileSurnameLayout;

    @ViewById(R.id.profile_name)
    TextInputLayout profileNameLayout;

    @ViewById(R.id.profile_height)
    TextInputLayout profileHeightLayout;

    @ViewById(R.id.profile_weight)
    TextInputLayout profileWeightLayout;

    @ViewById(R.id.profile_birthday_edit)
    TextInputEditText profileBirthday;

    @ViewById(R.id.profile_weight_edit)
    TextInputEditText profileWeight;

    @ViewById(R.id.profile_height_edit)
    TextInputEditText profileHeight;

    @ViewById(R.id.profile_picture)
    ImageView profilePicture;

    @ViewById(R.id.edit_photo_button)
    ImageView editProfileButton;

    @ViewById(R.id.profile_surname_edit)
    TextInputEditText profileSurname;

    @ViewById(R.id.profile_name_edit)
    TextInputEditText profileName;

    @ViewById(R.id.profile_gender_edit)
    TextInputEditText profileGender;

    @Click(R.id.edit_photo_button)
    public void clickEditButton() {
        dispatchTakePictureIntent();
    }
    @Click(R.id.profile_weight_edit)
    public void clickProfileWeight(){
        showWeightPickerDialog();
    }

    @Click(R.id.profile_birthday_edit)
    public void clickProfileBirthday(){
        showDatePickerDialog();
    }

    @Click(R.id.profile_height_edit)
    public void clickProfileHeight() {
        showHeightPickerDialog();
    }

    @Click(R.id.profile_gender_edit)
    public void clickProfileGender() {showGenderPickerDialog(); }



    @AfterViews
    void initialization(){

        profileGender.setFocusable(false);
        profileGender.setClickable(true);

        profileWeight.setFocusable(false);
        profileWeight.setClickable(true);


        profileBirthday.setFocusable(false);
        profileBirthday.setClickable(true);
        profileHeight.setFocusable(false);
        profileHeight.setClickable(true);
        profileWeightLayout.setFocusable(false);
        profileHeightLayout.setFocusable(false);

        //Profile picture related init
        profilePicturePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imageFileName = "profile_pic.jpg";


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        StorageReference pictureRef = storageRef.child("default-files/default_ph.jpg");

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //Firebase UserID and user storage

        UserID = currentUser.getUid();
        userFolder = storageRef.child("user/" + UserID + "/profile-pic.jpg");

        //Firebase Database Reference
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference();

        //Data communication between activity and the fragment vars


        mReference.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userGender = ((String)dataSnapshot.child("Gender").getValue());
                userBirthday = ((String)dataSnapshot.child("Birthday").getValue());
                userHeight = ((String)dataSnapshot.child("Height").getValue());
                userName = dataSnapshot.child("Name").getValue().toString();
                userWeight = ((String)dataSnapshot.child("Weight").getValue());
                String receivedHeight[] = userHeight.split(":");
                if (receivedHeight.length == 3) {
                    String user_height = receivedHeight[0] + "' " + receivedHeight[1] + "'' ";
                    profileHeight.setText(user_height);
                } else if (receivedHeight.length == 2){
                    String user_height = receivedHeight[0] + " " + receivedHeight[1];
                    profileHeight.setText(user_height);
                }
                String receivedWeight[] = userWeight.split(":");
                String user_weight = receivedWeight[0] + "." + receivedWeight[1] + " " + receivedWeight[2];
                String receivedName[] = userName.split(":");
                userFirstname = receivedName[0];
                userSurname = receivedName[1];
                profileGender.setText(userGender);
                profileName.setText(userFirstname);
                profileSurname.setText(userSurname);
                profileWeight.setText(user_weight);
                profileBirthday.setText(userBirthdayConversion(userBirthday));

                profileSurname.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        userSurname = String.valueOf(editable);
                        mReference.child(UserID).child("Name").setValue(userFirstname + ":" + userSurname);
                    }
                });

                profileName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        userFirstname = String.valueOf(editable);
                        mReference.child(UserID).child("Name").setValue(userFirstname + ":" + userSurname);
                        Toast toast = Toast.makeText(ProfileActivity.this, userFirstname, Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        File image_file = new File(profilePicturePath, imageFileName);
        if (image_file.exists()){
            Picasso.get()
                    .load(image_file)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(profilePicture, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory
                                    .create(getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            profilePicture.setImageDrawable(imageDrawable);
                        }
                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }
        editPicture(userFolder);

        profileName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    profileName.clearFocus();
                }
                return false;
            }
        });

        profileSurname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    profileSurname.clearFocus();
                }
                return false;
            }
        });


        RxBus.subscribe((userDataCollection) -> {
            if (userDataCollection instanceof UserDataCollection) {
                UserDataCollection userData = (UserDataCollection) userDataCollection;
                mReference.child(UserID).child(DataType.DataLabel[userData.getDataType()]).setValue(userData.getUserData());
                switch (((UserDataCollection) userDataCollection).getDataType()){
                    case DataType.HEIGHT: {
                        UserDataCollection.UserHeight newUserDataCollection = (UserDataCollection.UserHeight) userDataCollection;
                        userHeight = newUserDataCollection.getUserData();
                        profileHeight.setText(newUserDataCollection.getDisplayString());
                        break;
                    }
                    case DataType.WEIGHT: {
                        UserDataCollection.UserWeight newUserDataCollection = (UserDataCollection.UserWeight) userDataCollection;
                        userWeight = newUserDataCollection.getUserData();
                        profileWeight.setText(newUserDataCollection.getDisplayString());
                        break;
                    }
                    case DataType.BIRTHDAY: {
                        UserDataCollection.UserBirthday newUserDataCollection = (UserDataCollection.UserBirthday) userDataCollection;
                        userBirthday = newUserDataCollection.getUserData();
                        profileBirthday.setText(newUserDataCollection.getDisplayString());
                        break;
                    }
                    case DataType.GENDER: {
                        UserDataCollection.UserGender newUserDataCollection = (UserDataCollection.UserGender) userDataCollection;
                        userGender = newUserDataCollection.getUserData();
                        profileGender.setText(newUserDataCollection.getDisplayString());
                        break;
                    }
                }
            }
        });
    }




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }




    private void showDatePickerDialog(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.addToBackStack(null);

        DatePickerDialog datePicker = DatePickerDialog_.builder().build();
        Bundle dateBundle = new Bundle();

        dateBundle.putString("birthday", userBirthday);
        datePicker.setArguments(dateBundle);
        datePicker.show(ft, "date_picker_name");
    }


    private void showWeightPickerDialog(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.addToBackStack(null);

        WeightPickerDialog weightPicker = WeightPickerDialog_.builder().build();
        Bundle weightBundle = new Bundle();
        weightBundle.putString("weight", userWeight);

        weightPicker.setArguments(weightBundle);
        weightPicker.show(ft, "weight_picker_name");

    }


    private void showHeightPickerDialog(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.addToBackStack(null);

        HeightPickerDialog heightPicker = HeightPickerDialog_.builder().build();
        Bundle heightBundle = new Bundle();
        heightBundle.putString("height", userHeight);
        heightPicker.setArguments(heightBundle);
        heightPicker.show(ft, "height_picker_name");
    }

    private void showGenderPickerDialog(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.addToBackStack(null);

        GenderPickerDialog genderPicker = GenderPickerDialog_.builder().build();
        Bundle genderBundle = new Bundle();
        genderBundle.putString("gender", userGender);
        genderPicker.setArguments(genderBundle);
        genderPicker.show(ft, "gender_picker_name");
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                photoFile = createImageFile();
                if (photoFile != null){
                    photoURI = FileProvider.getUriForFile(this,
                            "fi.delektre.ringa.ring_thesis.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }

    private void editPicture(StorageReference pictureReference){
        pictureReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString()).into(profilePicture, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory
                                .create(getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        profilePicture.setImageDrawable(imageDrawable);
                    }
                    @Override
                    public void onError(Exception e) {
                    }
                });
            }
        });
    }
    private File createImageFile(){
        if (!profilePicturePath.exists()){
            profilePicturePath.mkdirs();
        }
        File image_file;

        image_file = new File(profilePicturePath, imageFileName);
        if (image_file.exists()){
            ProfileActivity.this.deleteFile(imageFileName);
            image_file = new File(profilePicturePath, imageFileName);
        }
        picturePath = image_file.getAbsolutePath();
        return image_file;
    }

    String userBirthdayConversion(String date){
        SimpleDateFormat sdf =  new SimpleDateFormat("dd:MM:yyyy");
        Date birthdayDate = null;
        try {
            birthdayDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String user_birthday = DateFormat.getDateInstance(DateFormat.SHORT).format(birthdayDate);
        return user_birthday;
    }


    /* The following method takes photo from the camera, saves it on the phone and, using Picasso library,
     transforms it to reduce the size for cloud storage and puts it inside profilePicture image view.
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            if (resultCode == Activity.RESULT_OK){
                profilePictureFile = new File(picturePath);

                Toast toast = Toast.makeText(ProfileActivity.this, picturePath, Toast.LENGTH_LONG);
                toast.show();
                /*Following block tries to upload profile picture to the Firebase Database.
                If it is not possible, the user is notified.
                 */
                Picasso.get()
                        .load(profilePictureFile)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .resize(300, 300)
                        .centerCrop()
                        .into(profilePicture, new Callback() {
                            @Override
                            public void onSuccess() {
                                    OutputStream fOut = null;
                                    Bitmap imageBitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
                                    RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                    imageDrawable.setCircular(true);
                                    try {
                                        ProfileActivity.this.deleteFile(imageFileName);
                                        profilePictureFile = new File(picturePath);
                                        try {
                                            InputStream stream = new FileInputStream(profilePictureFile);
                                            uploadTask = userFolder.putStream(stream);
                                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast toast = Toast.makeText(ProfileActivity.this,
                                                            "Picture not uploaded", Toast.LENGTH_LONG);
                                                    toast.show();
                                                }
                                            });
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        fOut = new FileOutputStream(profilePictureFile);
                                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                                        fOut.flush();
                                        fOut.close();
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                profilePicture.setImageDrawable(imageDrawable);

                            }

                            @Override
                            public void onError(Exception e) {

                            }

                });
            }
        }
    }
}
