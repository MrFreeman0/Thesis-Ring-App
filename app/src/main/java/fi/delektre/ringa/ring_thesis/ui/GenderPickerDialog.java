package fi.delektre.ringa.ring_thesis.ui;

import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import androidx.fragment.app.DialogFragment;
import fi.delektre.ringa.ring_thesis.R;
import fi.delektre.ringa.ring_thesis.data.RxBus;
import fi.delektre.ringa.ring_thesis.util.DataType;
import fi.delektre.ringa.ring_thesis.util.UserDataCollection;

@EFragment(R.layout.dialog_fragment_gender_picker)
public class GenderPickerDialog extends DialogFragment {


    String userGenderString;
    String genderPickerVars[];

    @ViewById(R.id.gender_picker)
    NumberPicker genderPicker;

    @ViewById(R.id.gender_accept)
    ImageView acceptButton;

    @ViewById(R.id.gender_decline)
    ImageView declineButton;

    @Click(R.id.gender_decline)
    void declineChanges(){
        dismiss();
    }

    @Click(R.id.gender_accept)
    void acceptChanges(){
        int i = genderPicker.getValue();
        String data = genderPickerVars[i];
        RxBus.publish(new UserDataCollection().new UserGender(DataType.GENDER, data));
        dismiss();
    }

    @AfterViews
    void initialization(){
        userGenderString = getArguments().getString("gender");

        genderPickerVars = new String[]{"Male", "Female"};
        genderPicker.setValue(0);

        genderPicker.setMinValue(0);
        genderPicker.setMaxValue(genderPickerVars.length-1);
        genderPicker.setDisplayedValues(genderPickerVars);

        if(userGenderString != null){
            int i = 0;
            for (String s : genderPickerVars){
                if (s.equals(userGenderString)){
                    Toast.makeText(this.getContext(), s, Toast.LENGTH_SHORT).show();
                    genderPicker.setValue(i);
                }
                i++;
            }
        }
    }

}
