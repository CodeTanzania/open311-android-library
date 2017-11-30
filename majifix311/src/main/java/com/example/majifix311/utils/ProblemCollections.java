package com.example.majifix311.utils;

import com.example.majifix311.models.Problem;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * This is used for funtions related to managing problem objects.
 */

public class ProblemCollections {
    public static void sortByDate(List<Problem> problems) {
        Collections.sort(problems, newestFirstComparator);
    }

    private static Comparator<Problem> newestFirstComparator
            = new Comparator<Problem>() {
        @Override
        public int compare(Problem request1, Problem request2) {
            if (request1 == null || request2 == null) {
                return -1;
            }
            Calendar firstDate = request1.getCreatedAt();
            Calendar secondDate = request2.getCreatedAt();

            return secondDate.compareTo(firstDate);        }
    };
}
