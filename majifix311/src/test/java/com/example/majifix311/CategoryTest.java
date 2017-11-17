package com.example.majifix311;

import android.os.Parcel;

import com.example.majifix311.models.Category;
import com.example.majifix311.models.Reporter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * This tests the Category class. Please see the DatabaseHelperTest as well.
 */

@RunWith(RobolectricTestRunner.class)
public class CategoryTest {
    private String mockName = "colors";
    private String mockId = "000000";
    private int mockPriority = 5;
    private String mockCode = "RGB";

    @Test
    public void category_isParcelable() {
        Category category = new Category(mockName, mockId, mockPriority, mockCode);
        assertMatchesMock(category);

        Parcel parcel = Parcel.obtain();
        category.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Category fromParcel = Category.CREATOR.createFromParcel(parcel);
        assertMatchesMock(fromParcel);
    }

    @Test
    public void category_isSortedByPriority() {
        List<Category> categories = new ArrayList<>(3);
        categories.add(new Category(mockName, "0", 3, mockCode));
        categories.add(new Category(mockName, "1", 5, mockCode));
        categories.add(new Category(mockName, "2", 1, mockCode));

        assertEquals("0", categories.get(0).getId());
        assertEquals("1", categories.get(1).getId());
        assertEquals("2", categories.get(2).getId());

        Collections.sort(categories);

        assertEquals("1", categories.get(0).getId());
        assertEquals("0", categories.get(1).getId());
        assertEquals("2", categories.get(2).getId());
    }

    private void assertMatchesMock(Category category) {
        assertEquals(mockName, category.getName());
        assertEquals(mockId, category.getId());
        assertEquals(mockPriority, category.getPriority());
        assertEquals(mockCode, category.getCode());
    }
}
