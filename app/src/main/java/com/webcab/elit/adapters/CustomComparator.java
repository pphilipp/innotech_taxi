package com.webcab.elit.adapters;

import java.util.Comparator;

import com.webcab.elit.data.templ;

public class CustomComparator implements Comparator<templ> {
    @Override
    public int compare(templ o1, templ o2) {
        return o1.getTitle().compareTo(o2.getTitle());
    }
}