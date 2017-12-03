package com.example.majifix311.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dave - Work on 11/30/2017.
 */

public class TaggedProblemList extends ArrayList<Problem> {
    public final boolean mPreliminary;

    public TaggedProblemList(List<Problem> list, boolean isPreliminary){
        mPreliminary = isPreliminary;
        this.addAll(list);
    }
}
