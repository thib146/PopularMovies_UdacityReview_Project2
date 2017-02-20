package com.example.android.popularmovies.utilities;

import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by thib146 on 15/02/2017.
 */

/**
 * This class is used to store all the reviews data in one variable
 */
public class ReviewArrays {

    public ArrayList<String> id;
    public ArrayList<String> author;
    public ArrayList<String> content;
    public ArrayList<String> url;

    // Used to store the strings containing "Read More" and "Read Less" for the long reviews
    public String readMoreText;
    public String readLessText;
}