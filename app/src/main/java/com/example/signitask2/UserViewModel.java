package com.example.signitask2;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final LiveData<List<UserData>> allUsers;

    public UserViewModel(Application application) {
        super(application);
        repository = new UserRepository(application);
        allUsers = repository.getAllUsers();
    }

    public LiveData<List<UserData>> getAllUsers() {
        return allUsers;
    }

    public void insert(UserData user) {
        repository.insert(user);  // ✅ Now you can call insert()
    }

    public void delete(UserData user) {
        repository.delete(user);  // ✅ Now you can call insert()
    }
}

