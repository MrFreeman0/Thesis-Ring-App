package fi.delektre.ringa.ring_thesis.ui;

import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import fi.delektre.ringa.ring_thesis.R;
import fi.delektre.ringa.ring_thesis.UserData_;
import fi.delektre.ringa.ring_thesis.data.RxBus;
import fi.delektre.ringa.ring_thesis.util.DataType;
import fi.delektre.ringa.ring_thesis.util.UserDataCollection;

@EFragment(R.layout.fragment_create_height_weight)
public class CreateProfileDataFragment extends Fragment {
    String userHeight;
    String userWeight;
    String userBirthday;
    UserDataCollection.UserHeight userHeightCollection;
    UserDataCollection.UserWeight userWeigthCollection;
    UserDataCollection.UserBirthday userBirthdayCollection;

    @ViewById(R.id.create_birthday_edit)
    TextInputEditText createBirthday;

    @ViewById(R.id.create_weight_edit)
    TextInputEditText createWeight;

    @ViewById(R.id.create_height_edit)
    TextInputEditText createHeight;

    @ViewById(R.id.create_finish)
    MaterialButton finishButton;

    @ViewById(R.id.create_back)
    MaterialButton backButton;

    @Click(R.id.create_back)
    void createBackButton(){
        ((UserData_)getActivity()).setViewPager(0);
    }


    @Click(R.id.create_height_edit)
    void pressHeight(){
        showHeightPickerDialog();
    }

    @Click(R.id.create_weight_edit)
    void pressWeight(){
        showWeightPickerDialog();

    }

    @Click(R.id.create_birthday_edit)
    void pressBirthday(){
        showBirthdayPickerDialog();
    }



    @Click(R.id.create_finish)
    void createFinishButton(){
        finishSignup();
    }


    @AfterViews
    void initialization(){
        RxBus.subscribe((userDataCollection) -> {
            if (userDataCollection instanceof UserDataCollection) {
                UserDataCollection userData = (UserDataCollection) userDataCollection;
                switch (((UserDataCollection) userDataCollection).getDataType()){
                    case DataType.HEIGHT: {
                        userHeightCollection = (UserDataCollection.UserHeight) userDataCollection;
                        userHeight = userHeightCollection.getUserData();
                        createHeight.setText(userHeightCollection.getDisplayString());
                        break;
                    }
                    case DataType.WEIGHT: {
                        userWeigthCollection = (UserDataCollection.UserWeight) userDataCollection;
                        userWeight = userWeigthCollection.getUserData();
                        createWeight.setText(userWeigthCollection.getDisplayString());
                        break;
                    }
                    case DataType.BIRTHDAY: {
                        userBirthdayCollection = (UserDataCollection.UserBirthday) userDataCollection;
                        userBirthday = userBirthdayCollection.getUserData();
                        createBirthday.setText(userBirthdayCollection.getDisplayString());
                        break;
                    }
                }
            }
        });
    }


    private void showHeightPickerDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.addToBackStack(null);
        HeightPickerDialog heightPicker = HeightPickerDialog_.builder().build();
        Bundle heightBundle = new Bundle();
        heightBundle.putString("height", userHeight);
        heightPicker.setArguments(heightBundle);
        heightPicker.show(ft, "height_picker_name");
    }

    private void showWeightPickerDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.addToBackStack(null);

        WeightPickerDialog weightPicker = WeightPickerDialog_.builder().build();
        Bundle weightBundle = new Bundle();
        //weightBundle.putInt("weight_main", userWeight);
        //weightBundle.putInt("weight_decimal", userWeight);
        weightBundle.putString("weight", userWeight);

        weightPicker.setArguments(weightBundle);
        weightPicker.show(ft, "weight_picker_name");
    }

    private void showBirthdayPickerDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.addToBackStack(null);

        DatePickerDialog datePicker = DatePickerDialog_.builder().build();
        Bundle dateBundle = new Bundle();

        dateBundle.putString("birthday", userBirthday);
        datePicker.setArguments(dateBundle);
        datePicker.show(ft, "date_picker_name");
    }

    private void finishSignup(){
        RxBus.publish(userBirthdayCollection);
        RxBus.publish(userWeigthCollection);
        RxBus.publish(userBirthdayCollection);
        ((UserData_)getActivity()).submitAndFinish();
    }
}
