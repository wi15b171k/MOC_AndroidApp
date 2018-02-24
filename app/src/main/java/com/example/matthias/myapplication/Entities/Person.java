package com.example.matthias.myapplication.Entities;

import android.support.annotation.NonNull;

/**
 * Created by Matthias on 28.01.2018.
 */

public class Person implements Comparable<Person>{
    public String userId;
    public String name;

    @Override
    public int compareTo(@NonNull Person person) {
        return userId.compareTo(person.userId);
    }

    @Override
    public boolean equals(Object obj) {
        Person p = (Person) obj;
        return this.userId.equals(p.userId);
    }
}
