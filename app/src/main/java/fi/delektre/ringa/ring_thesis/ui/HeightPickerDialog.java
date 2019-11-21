package fi.delektre.ringa.ring_thesis.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.DialogFragment;
import fi.delektre.ringa.ring_thesis.R;
import fi.delektre.ringa.ring_thesis.data.RxBus;
import fi.delektre.ringa.ring_thesis.util.DataType;
import fi.delektre.ringa.ring_thesis.util.UserDataCollection;

@EFragment(R.layout.dialog_fragment_height_chose)
public class HeightPickerDialog extends DialogFragment implements View.OnClickListener{


    // Firebase stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser currentUser;
    private DatabaseReference mReferecence;
    private FirebaseDatabase mFirebaseDatabase;

    int heightCmMax = 272;
    int heightFeetMax = 8;
    int maxDecimalInches = 11;
    double userHeightMain = 0;
    double userDecimalHeight = 0;
    String measUnit = "CM";
    String userHeightString;

    //Constraint set for further view manipulation
    ConstraintSet constraintSet = new ConstraintSet();


    @ViewById(R.id.height_units)
    TextView heightUnits;

    @ViewById(R.id.height_picker_main)
    NumberPicker mainPicker;

    @ViewById(R.id.height_picker_decimals)
    NumberPicker decimalPicker;

    @ViewById(R.id.height_accept)
    ImageView acceptButton;

    @ViewById(R.id.height_decline)
    ImageView declineButton;

    @ViewById(R.id.height_unit_switch)
    Switch unitSwitch;

    @ViewById(R.id.dialog_height_constraint)
    ConstraintLayout heightConstraintLayout;

    @ViewById(R.id.guideline)
    Guideline verticalGuideline;

    @Click(R.id.height_decline)
    void declineChanges(){
        dismiss();
    }

    @Click(R.id.height_accept)
    void acceptChanges(){
        if (measUnit.equals("CM")) {
            String data = (String.valueOf(mainPicker.getValue()) + ":" + String.valueOf(measUnit));
            RxBus.publish(new UserDataCollection().new UserHeight(DataType.HEIGHT, data));
        } else {
            String data = (String.valueOf(mainPicker.getValue()) + ":" + String.valueOf(decimalPicker
                    .getValue()) + ":" + String.valueOf(measUnit));
            RxBus.publish(new UserDataCollection().new UserHeight(DataType.HEIGHT, data));
        }
        dismiss();
    }

    @AfterViews
    public void viewManipulations() {

        //Necessary to make round corners
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        constraintSet.clone(heightConstraintLayout);



        userHeightString = getArguments().getString("height");

        /*Receiving string from the parent activity, splitting it.
        Then, if the array size is equal to 3, it assumes that user's height is received in imperial units
        Otherwise, it is recieved in SI. The correct initialization is applied.
         */
        if (userHeightString != null) {
            String dataSplit[] = userHeightString.split(":");
            if (dataSplit.length == 2) {
                measUnit = dataSplit[1];
                heightUnits.setText(measUnit);
                unitSwitch.setText(measUnit);
                mainPicker.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        return String.format("%d CM", value);
                    }
                });
                decimalPicker.setVisibility(View.GONE);
            } else if (dataSplit.length == 3){
                measUnit = dataSplit[2];
                heightUnits.setText(measUnit);
                unitSwitch.setText(measUnit);
                userDecimalHeight = Double.parseDouble(dataSplit[1]);
                mainPicker.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        return String.format("%d ft", value);
                    }
                });
                View firstItem = mainPicker.getChildAt(0);
                if (firstItem != null) {
                    firstItem.setVisibility(View.INVISIBLE);
                }
                decimalPicker.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        return String.format("%d in", value);
                    }
                });
            }
            viewFix(mainPicker);
            viewFix(decimalPicker);
            movingNumberPicker(measUnit);
            userHeightMain = Double.parseDouble(dataSplit[0]);
        }

        mainPicker.setMinValue(0);
        mainPicker.setMaxValue(heightCmMax);
        decimalPicker.setMinValue(0);
        decimalPicker.setMaxValue(maxDecimalInches);
        mainPicker.setValue((int) userHeightMain);
        decimalPicker.setValue((int) userDecimalHeight);

        newCheckListener();

        if (measUnit.equals("FT")){
            unitSwitch.setOnCheckedChangeListener(null);
            unitSwitch.setChecked(true);
            newCheckListener();
        } else {
            unitSwitch.setOnCheckedChangeListener(null);
            unitSwitch.setChecked(false);
            newCheckListener();
        }

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    public void newCheckListener(){
        unitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    decimalPicker.setVisibility(View.VISIBLE);
                    measUnit = "FT";
                    userHeightMain = (mainPicker.getValue() / 2.54) / 12;
                    userDecimalHeight = Math.round((userHeightMain % 1 )/0.0833);
                    if (userDecimalHeight > 11) {
                        userHeightMain++;
                    }

                    mainPicker.setMaxValue(heightFeetMax);
                    mainPicker.setFormatter(new NumberPicker.Formatter() {
                        @Override
                        public String format(int value) {
                            return String.format("%d FT", value);
                        }
                    });

                    //workaround for a nasty visual bug
                    decimalPicker.setFormatter(new NumberPicker.Formatter() {
                        @Override
                        public String format(int value) {
                            return String.format("%d INCH", value);
                        }
                    });
                    mainPicker.setValue((int) userHeightMain);
                    decimalPicker.setValue((int) userDecimalHeight);
                    heightUnits.setText(measUnit);
                    unitSwitch.setText("FT");
                    constraintSet.clone(heightConstraintLayout);
                    movingNumberPicker(measUnit);
                } else {
                    measUnit = "CM";
                    userHeightMain = (mainPicker.getValue()*30.48 + (decimalPicker.getValue() * 2.54));
                    //userDecimalHeight = Math.round(userHeightMain % 1 * 10);
                    mainPicker.setMaxValue(heightCmMax);
                    mainPicker.setFormatter(new NumberPicker.Formatter() {
                        @Override
                        public String format(int value) {
                            return String.format("%d CM", value);
                        }
                    });
                    mainPicker.setValue((int) userHeightMain);
                    decimalPicker.setVisibility(View.GONE);
                    decimalPicker.setValue(0);
                    heightUnits.setText(measUnit);
                    unitSwitch.setText("CM");
                    constraintSet.clone(heightConstraintLayout);
                    movingNumberPicker(measUnit);
                }
            }
        });

    }
    void viewFix(NumberPicker picker){
        View firstItem = picker.getChildAt(0);
        if (firstItem != null) {
            firstItem.setVisibility(View.INVISIBLE);
        }
    }
    void movingNumberPicker(String measUnit){
        constraintSet.clone(heightConstraintLayout);
        if(measUnit.equals("CM")) {
            constraintSet.connect(mainPicker.getId(), ConstraintSet.END, heightConstraintLayout.getId(), ConstraintSet.END);
            constraintSet.applyTo(heightConstraintLayout);
        } else if(measUnit.equals("FT")){
            constraintSet.connect(mainPicker.getId(), ConstraintSet.END, verticalGuideline.getId(), ConstraintSet.START);
            constraintSet.applyTo(heightConstraintLayout);
        }
    }
}
