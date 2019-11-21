package fi.delektre.ringa.ring_thesis.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.NumberPicker;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import fi.delektre.ringa.ring_thesis.R;
import fi.delektre.ringa.ring_thesis.data.RxBus;
import fi.delektre.ringa.ring_thesis.util.DataType;
import fi.delektre.ringa.ring_thesis.util.UserDataCollection;

@EFragment(R.layout.dialog_fragment_date_picker)
public class DatePickerDialog extends DialogFragment implements View.OnClickListener{

    // Firebase stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser currentUser;
    private DatabaseReference mReferecence;
    private FirebaseDatabase mFirebaseDatabase;

    // Interface for data pass


    //User vars
    String userBirthdayString;
    String newUserBirthdayString;
    Context mContext;


    Calendar mCalendar;
    int day;
    int month;
    int year;
    Date dateInstance;
    DateFormat displayDateFormat;
    String displayDateString;
    String displayYearString;


    @ViewById(R.id.date_dialog_year)
    TextView dateYear;

    @ViewById(R.id.date_dialog_date)
    TextView dateMonth;

    @ViewById(R.id.date_picker_year)
    NumberPicker pickerYear;

    @ViewById(R.id.date_accept)
    ImageView acceptButton;

    @ViewById(R.id.date_decline)
    ImageView acceptDecline;

    @ViewById(R.id.calendarView)
    CalendarView calendarView;

    @Click(R.id.date_dialog_year)
    void changeYear() {
        dateYear.setText(String.valueOf(pickerYear.getValue()));
        calendarView.setVisibility(View.GONE);
        pickerYear.setVisibility(View.VISIBLE);

    }

    @Click(R.id.date_dialog_date)
    void changeMonth(){
        SimpleDateFormat sdf =  new SimpleDateFormat("dd:MM:yyyy");
            try {
                if (newUserBirthdayString == null){
                    newUserBirthdayString = "01:01";
                }
                dateInstance = sdf.parse(newUserBirthdayString + ":" + year);
            } catch (ParseException e) {


                e.printStackTrace();
            }
        mCalendar.setTime(dateInstance);
        mCalendar.set(Calendar.YEAR, pickerYear.getValue());

        dateInstance = mCalendar.getTime();
        calendarView.setDate(dateInstance.getTime());
        dateYear.setText(Integer.toString(pickerYear.getValue()));
        calendarView.setVisibility(View.VISIBLE);
        pickerYear.setVisibility(View.GONE);
    }

    @Click(R.id.date_decline)
    void declineChanges(){
        dismiss();
    }

    @Click(R.id.date_accept)
    void acceptChanges(){
        if (newUserBirthdayString != null){
            String data = newUserBirthdayString + ":" + Integer.toString(year);
            RxBus.publish(new UserDataCollection().new UserBirthday(DataType.BIRTHDAY, data));
        } else if (userBirthdayString != null){
            String dataSplit[] = userBirthdayString.split(":");
            String data = dataSplit[0] + ":" + dataSplit[1] + ":" + year;
            RxBus.publish(new UserDataCollection().new UserBirthday(DataType.BIRTHDAY, data));
        }
        dismiss();
    }



    @AfterViews
    void initialization(){
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        mContext = this.getContext();
        displayDateFormat = new SimpleDateFormat("dd MMMM", Locale.getDefault());
        mCalendar = Calendar.getInstance();
        userBirthdayString = getArguments().getString("birthday");
        pickerYear.setMinValue(1900);
        pickerYear.setMaxValue(mCalendar.get(Calendar.YEAR));

        if (userBirthdayString != null){
            String dataSplit[] = userBirthdayString.split(":");
            mCalendar.set(Integer.parseInt(dataSplit[2]), Integer.parseInt(dataSplit[1]), Integer.parseInt(dataSplit[0]));
            year = Integer.parseInt(dataSplit[2]);



            SimpleDateFormat sdf =  new SimpleDateFormat("dd:MM:yyyy");
            try {
                dateInstance = sdf.parse(userBirthdayString);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            calendarView.setDate(dateInstance.getTime());
            displayDateString = displayDateFormat.format(dateInstance);
            pickerYear.setValue(Integer.parseInt(dataSplit[2]));
            dateMonth.setText(displayDateString);
            dateYear.setText(dataSplit[2]);
        }


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                year = i;

                i1++;
                pickerYear.setValue(year);
                dateYear.setText(String.valueOf(year));
                newUserBirthdayString = i2 + ":" + i1;
                SimpleDateFormat sdf =  new SimpleDateFormat("dd:MM");
                try {
                    dateInstance = sdf.parse(newUserBirthdayString);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                displayDateString = displayDateFormat.format(dateInstance);
                dateMonth.setText(displayDateString);

                Toast toast = Toast.makeText(mContext, displayDateString, Toast.LENGTH_LONG);
                toast.show();


            }
        });

        pickerYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                year = i1;
                dateYear.setText(String.valueOf(i1));
            }
        });

    }



    @Override
    public void onClick(View view) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
