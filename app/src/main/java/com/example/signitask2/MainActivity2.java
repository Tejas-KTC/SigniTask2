package com.example.signitask2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;

public class MainActivity2 extends AppCompatActivity {
    private EditText etName, etEmail, etNumber;
    private RadioGroup radioGroup;
    private CheckBox checkBoxHobby1, checkBoxHobby2, checkBoxHobby3;
    private TextView tvDate, tvTime;
    private ImageView ivProfile;
    private Button btnSave;
    private UserViewModel userViewModel;
    private boolean isReadOnly = false;
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Initialize UI elements
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etNumber = findViewById(R.id.etNumber);
        radioGroup = findViewById(R.id.radioGroup);
        checkBoxHobby1 = findViewById(R.id.checkBoxHobby1);
        checkBoxHobby2 = findViewById(R.id.checkBoxHobby2);
        checkBoxHobby3 = findViewById(R.id.checkBoxHobby3);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        ivProfile = findViewById(R.id.imageView);
        btnSave = findViewById(R.id.btnSave);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Open gallery when ImageView is clicked
        ivProfile.setOnClickListener(v -> openGallery());

        // Check if we are in read-only mode
        Intent intent = getIntent();
        if (intent.hasExtra("USER_ID")) {
            isReadOnly = true;
            int userId = intent.getIntExtra("USER_ID", -1);
            if (userId != -1) {
                loadUserData(userId);
            }
        }

        if (isReadOnly) {
            disableEditing();
        } else {
            btnSave.setOnClickListener(v -> saveUser());
        }
    }

    // Open image gallery
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    // Handle image selection result
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivProfile.setImageURI(selectedImageUri);
                }
            });

    private void loadUserData(int userId) {
        userViewModel.getAllUsers().observe(this, users -> {
            for (UserData user : users) {
                if (user.getId() == userId) {
                    etName.setText(user.getName());
                    etEmail.setText(user.getEmail());
                    etNumber.setText(user.getNumber());

                    if (user.getGender().equals("Male")) {
                        radioGroup.check(R.id.radioMale);
                    } else {
                        radioGroup.check(R.id.radioFemale);
                    }

                    if (user.getHobbies().contains("Hobby 1")) checkBoxHobby1.setChecked(true);
                    if (user.getHobbies().contains("Hobby 2")) checkBoxHobby2.setChecked(true);
                    if (user.getHobbies().contains("Hobby 3")) checkBoxHobby3.setChecked(true);

                    tvDate.setText(user.getDate());
                    tvTime.setText(user.getTime());

                    // Load stored image
                    if (user.getImagePath() != null && !user.getImagePath().isEmpty()) {
                        Uri imageUri = Uri.parse(user.getImagePath());
                        String realPath = getRealPathFromURI(imageUri);

                        if (realPath != null) {
                            Glide.with(this).load(realPath).into(ivProfile);
                        } else {
                            Glide.with(this).load(imageUri).into(ivProfile);
                        }
                    }

                    break;
                }
            }
        });
    }

    public String getRealPathFromURI(Uri contentUri) {
        String result = null;
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                if (idx != -1) {
                    result = cursor.getString(idx);
                }
            }
            cursor.close();
        }
        return result;
    }


    private void saveUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String number = etNumber.getText().toString().trim();

        // Ensure a gender is selected
        int selectedGenderId = radioGroup.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return;
        }
        String gender = ((RadioButton) findViewById(selectedGenderId)).getText().toString();

        // Collect selected hobbies
        StringBuilder hobbies = new StringBuilder();
        if (checkBoxHobby1.isChecked()) hobbies.append("Hobby 1, ");
        if (checkBoxHobby2.isChecked()) hobbies.append("Hobby 2, ");
        if (checkBoxHobby3.isChecked()) hobbies.append("Hobby 3");

        String date = tvDate.getText().toString();
        String time = tvTime.getText().toString();

        // Ensure all fields are filled
        if (name.isEmpty() || email.isEmpty() || number.isEmpty() || hobbies.toString().isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        UserData user = new UserData();
        user.setName(name);
        user.setEmail(email);
        user.setNumber(number);
        user.setGender(gender);
        user.setHobbies(hobbies.toString().trim());
        user.setDate(date);
        user.setTime(time);
        user.setImagePath(selectedImageUri != null ? selectedImageUri.toString() : "");

        userViewModel.insert(user);

        Toast.makeText(this, "User saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void disableEditing() {
        etName.setEnabled(false);
        etEmail.setEnabled(false);
        etNumber.setEnabled(false);

        // Disable all RadioButtons in RadioGroup
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(false);
        }

        checkBoxHobby1.setEnabled(false);
        checkBoxHobby2.setEnabled(false);
        checkBoxHobby3.setEnabled(false);
        tvDate.setEnabled(false);
        tvTime.setEnabled(false);
        ivProfile.setEnabled(false);
        btnSave.setVisibility(View.GONE); // Hide save button
    }

    public void showDatePicker(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view1, year1, month1, dayOfMonth) -> tvDate.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                year, month, day);
        datePickerDialog.show();
    }

    public void showTimePicker(View view) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view1, hourOfDay, minute1) -> tvTime.setText(hourOfDay + ":" + minute1),
                hour, minute, true);
        timePickerDialog.show();
    }
}
