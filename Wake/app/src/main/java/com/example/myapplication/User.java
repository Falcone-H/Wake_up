package com.example.myapplication;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    public int heartbeat;

    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.
}