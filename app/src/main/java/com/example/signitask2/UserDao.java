package com.example.signitask2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(UserData user);

    @Query("SELECT * FROM users")
    LiveData<List<UserData>> getAllUsers();

    @Delete
    void delete(UserData user);
}



