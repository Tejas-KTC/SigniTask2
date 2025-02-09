package com.example.signitask2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {
    private UserViewModel userViewModel;
    private UserAdapter adapter;
    private ImageButton btnDelete;
    private TextView tvSelectedCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(0); // Remove light mode flag
            getWindow().setStatusBarColor(Color.parseColor("#FF0000")); // Custom status bar color
        }
        getWindow().setNavigationBarColor(Color.parseColor("#FF0000")); // Custom navigation bar color



        RecyclerView recyclerView = findViewById(R.id.recList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        btnDelete = findViewById(R.id.btnDelete);
        tvSelectedCount = findViewById(R.id.tvSelectedCount);

        btnDelete.setVisibility(View.GONE);

        adapter = new UserAdapter(new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserData user) {
                // Open MainActivity2 when clicking on an item
                Intent intent = new Intent(MainActivity3.this, MainActivity2.class);
                intent.putExtra("user_name", user.getName());
                startActivity(intent);
            }

            @Override
            public void onItemSelectionChanged(int selectedCount) {
                if (selectedCount > 0) {
                    btnDelete.setVisibility(View.VISIBLE);
                    tvSelectedCount.setText(selectedCount + " selected");
                } else {
                    btnDelete.setVisibility(View.GONE);
                    tvSelectedCount.setText("List of Users");
                }
            }
        });

        recyclerView.setAdapter(adapter);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, new Observer<List<UserData>>() {
            @Override
            public void onChanged(List<UserData> users) {
                adapter.setUsers(users);
            }
        });

        btnDelete.setOnClickListener(v -> {
            List<UserData> selectedUsers = adapter.getSelectedUsers();

            for (UserData users : selectedUsers) {
                UserData user = new UserData();
                user.setId(users.getId());
                user.setName(users.getName());
                user.setEmail(users.getEmail());
                user.setNumber(users.getNumber());
                user.setGender(users.getGender());
                user.setHobbies(users.getHobbies());
                user.setDate(users.getDate());
                user.setTime(users.getTime());
                user.setImagePath(users.getImagePath());

                userViewModel.delete(user);

                Log.d("DeleteUsers", "User ID to delete: " + users.getId() + users.getName());
            }

            adapter.clearSelection();
            btnDelete.setVisibility(View.GONE);
            tvSelectedCount.setText("List of Users");
        });

    }
}
