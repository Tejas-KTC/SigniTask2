package com.example.signitask2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(UserData user);  // ✅ Ensure this method exists

    @Query("SELECT * FROM users")
    LiveData<List<UserData>> getAllUsers();
}



