package fi.delektre.ringa.ring_thesis.ui;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioButton;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import androidx.fragment.app.Fragment;
import fi.delektre.ringa.ring_thesis.LoginActivity_;
import fi.delektre.ringa.ring_thesis.R;
import fi.delektre.ringa.ring_thesis.UserData_;
import fi.delektre.ringa.ring_thesis.data.RxBus;
import fi.delektre.ringa.ring_thesis.util.DataType;
import fi.delektre.ringa.ring_thesis.util.UserDataCollection;

@EFragment(R.layout.fragment_create_username)
public class CreateProfileNameFragment extends Fragment {

    //User Variables
    String userFirstname;
    String userSurname;
    String userFullname;
    String userGender;

    @ViewById(R.id.create_name_edit)
    TextInputEditText createFirstName;

    @ViewById(R.id.create_surname_edit)
    TextInputEditText createSurname;

    @ViewById(R.id.create_back)
    MaterialButton backButton;

    @ViewById(R.id.create_next)
    MaterialButton nextButton;

    @ViewById(R.id.create_radio_female)
    RadioButton femaleButton;

    @ViewById(R.id.create_radio_male)
    RadioButton maleButton;

    @Click(R.id.create_radio_female)
    void femaleButtonClicked(){
        onRadioButtonClicked(femaleButton);
    }

    @Click(R.id.create_radio_male)
    void maleButtonClicked(){
        onRadioButtonClicked(maleButton);
    }

    @Click(R.id.create_back)
    void createBackButton(){
        startActivity(new Intent(getActivity(), LoginActivity_.class));
        ((UserData_)getActivity()).overridePendingTransition(0, 0);
    }

    @Click(R.id.create_next)
    void createNextButton(){
        if (userFirstname != null && userSurname != null && userGender!= null){
            userFullname = userFirstname + ":" + userSurname;
            RxBus.publish(new UserDataCollection().new UserName(DataType.NAME, userFullname));
            RxBus.publish(new UserDataCollection().new UserGender(DataType.GENDER, userGender));
            ((UserData_)getActivity()).setViewPager(1);
        }
    }



    @AfterViews
    void initialize(){


        createFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                userFirstname = String.valueOf(editable);
            }
        });

        createSurname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                userSurname = String.valueOf(editable);
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.create_radio_male:
                if (checked)
                    userGender="Male";
                    break;
            case R.id.create_radio_female:
                if (checked)
                    userGender="Female";
                    break;
        }
    }
}
