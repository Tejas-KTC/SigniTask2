package com.example.signitask2;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao userDao;
    private final LiveData<List<UserData>> allUsers;
    private final ExecutorService executorService;

    public UserRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        userDao = database.userDao();
        allUsers = userDao.getAllUsers();
        executorService = Executors.newSingleThreadExecutor(); // Background thread
    }

    public LiveData<List<UserData>> getAllUsers() {
        return allUsers;
    }

    public void insert(UserData user) {
        executorService.execute(() -> userDao.insert(user));  // ✅ Insert in background
    }
}
