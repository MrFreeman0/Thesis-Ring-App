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
import android.widget.Toast;

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
import androidx.fragment.app.DialogFragment;
import fi.delektre.ringa.ring_thesis.R;
import fi.delektre.ringa.ring_thesis.data.RxBus;
import fi.delektre.ringa.ring_thesis.util.DataType;
import fi.delektre.ringa.ring_thesis.util.UserDataCollection;

@EFragment(R.layout.dialog_fragment_weight_chose)
public class WeightPickerDialog extends DialogFragment implements View.OnClickListener{


    // Firebase stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser currentUser;
    private DatabaseReference mReferecence;
    private FirebaseDatabase mFirebaseDatabase;


    int weightKilosMax = 300;
    int weightPoundMax = 700;
    int maxDecimal = 9;
    double userWeightMain = 0;
    double userDecimalWeight = 0;
    String measUnit = "KG";
    String userWeightString;


    @ViewById(R.id.weight_units)
    TextView weightUnits;

    @ViewById(R.id.weight_picker_main)
    NumberPicker mainPicker;

    @ViewById(R.id.weight_picker_decimals)
    NumberPicker decimalPicker;

    @ViewById(R.id.weight_accept)
    ImageView acceptButton;

    @ViewById(R.id.weight_decline)
    ImageView declineButton;

    @ViewById(R.id.weight_unit_switch)
    Switch unitSwitch;

    @Click(R.id.weight_decline)
    void declineChanges(){
        dismiss();
    }

    @Click(R.id.weight_accept)
    void acceptChanges(){

        String data = (String.valueOf(mainPicker.getValue()) + ":" + String.valueOf(decimalPicker.getValue()) + ":" + String.valueOf(measUnit));
        RxBus.publish(new UserDataCollection().new UserWeight(DataType.WEIGHT, data));
        //passData(data);
        dismiss();
    }

    @AfterViews
    public void pickerManipulations(){
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        userWeightString = getArguments().getString("weight");

        //Receiving string from the parent activity, splitting it, so that:
        //[0] = Weight before decimal point
        //[1] = Weight after decimal point
        //[2] = Weight unit, as chosen by user
        if (userWeightString != null) {
            String dataSplit[] = userWeightString.split(":");
            measUnit = dataSplit[2];
            weightUnits.setText(measUnit);
            unitSwitch.setText(measUnit);

            Toast toast = Toast.makeText(this.getContext(), userWeightString, Toast.LENGTH_LONG);
            toast.show();

            userWeightMain = Double.parseDouble(dataSplit[0]);
            userDecimalWeight = Double.parseDouble(dataSplit[1]);
        }

        mainPicker.setMinValue(0);
        mainPicker.setMaxValue(weightKilosMax);
        decimalPicker.setMinValue(0);
        decimalPicker.setMaxValue(maxDecimal);

        mainPicker.setValue((int) userWeightMain);
        decimalPicker.setValue((int) userDecimalWeight);
        newCheckListener();
        if (measUnit.equals("LBS")){
            unitSwitch.setOnCheckedChangeListener(null);
            unitSwitch.setChecked(true);
            newCheckListener();
        } else {
            unitSwitch.setOnCheckedChangeListener(null);
            unitSwitch.setChecked(false);
            newCheckListener();
        }
    }


    int weightKilos[];
    int weightPounds[];
    int wightGrams[];
    int weightOz[];




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
        //userWeightString = getArguments().getString("weight");


        //return super.onCreateView(inflater, container, savedInstanceState);

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
                if (isChecked){
                    measUnit = "LBS";
                    userWeightMain = (mainPicker.getValue() + (decimalPicker.getValue() * 0.1)) * 2.205;
                    userDecimalWeight = Math.round(userWeightMain % 1 * 10);
                    mainPicker.setMaxValue(weightPoundMax);
                    mainPicker.setValue((int) userWeightMain);
                    decimalPicker.setValue((int) userDecimalWeight);
                    //decimalPicker.setValue((int) userDecimalWeight);
                    weightUnits.setText(measUnit);
                    unitSwitch.setText("LBS");
                } else {
                    measUnit = "KG";
                    userWeightMain = (mainPicker.getValue() + (decimalPicker.getValue() * 0.1)) / 2.205;
                    //userDecimalWeight = userWeightMain % 1 * 10;
                    userDecimalWeight = Math.round(userWeightMain % 1 * 10);
                    mainPicker.setMaxValue(weightKilosMax);
                    mainPicker.setValue((int) userWeightMain);
                    decimalPicker.setValue((int) userDecimalWeight);
                    weightUnits.setText(measUnit);
                    unitSwitch.setText("KG");
                }
            }
        });
    }
}
