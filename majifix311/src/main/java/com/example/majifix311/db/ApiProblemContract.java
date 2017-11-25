package com.example.majifix311.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.provider.BaseColumns;

import com.example.majifix311.api.models.ApiAttachment;
import com.example.majifix311.api.models.ApiLocation;
import com.example.majifix311.api.models.ApiReporter;
import com.example.majifix311.api.models.ApiService;
import com.example.majifix311.api.models.ApiServiceRequestGet;
import com.example.majifix311.api.models.ApiStatus;
import com.example.majifix311.models.Attachment;
import com.example.majifix311.models.Category;
import com.example.majifix311.models.Problem;
import com.example.majifix311.models.Status;
import com.example.majifix311.utils.DateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.majifix311.db.ApiProblemContract.Entry.TABLE_NAME;


/**
 * This is used to store reported Problems in the database.
 */

class ApiProblemContract {
    static final String CREATE_PROBLEM_TABLE = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +"("+
            Entry.COLUMN_TICKET_ID +" TEXT PRIMARY KEY, "+
            Entry.COLUMN_REPORTER_NAME +" TEXT, "+
            Entry.COLUMN_REPORTER_PHONE +" TEXT, "+
            Entry.COLUMN_REPORTER_EMAIL +" TEXT, "+
            Entry.COLUMN_REPORTER_ACCOUNT +" TEXT, "+
            Entry.COLUMN_CATEGORY_NAME +" TEXT, "+
            Entry.COLUMN_CATEGORY_ID +" TEXT, "+
            Entry.COLUMN_CATEGORY_PRIORITY +" INTEGER, "+
            Entry.COLUMN_CATEGORY_CODE +" STRING, "+
            Entry.COLUMN_LATITUDE +" DECIMAL, "+
            Entry.COLUMN_LONGITUDE +" DECIMAL, "+
            Entry.COLUMN_ADDRESS +" TEXT, "+
            Entry.COLUMN_DESCRIPTION +" TEXT, "+
            Entry.COLUMN_ATTACHMENT_JSON +" TEXT, "+
            Entry.COLUMN_STATUS_IS_OPEN + " INTEGER,"+
            Entry.COLUMN_STATUS_NAME + " TEXT,"+
            Entry.COLUMN_STATUS_COLOR + " TEXT,"+
            Entry.COLUMN_CREATED_AT + " INTEGER,"+
            Entry.COLUMN_UPDATED_AT + " INTEGER,"+
            Entry.COLUMN_RESOLVED_AT + " INTEGER,"+
            Entry.COLUMN_COMMENT_JSON + " TEXT,"+
            Entry.COLUMN_POSTED + " INTEGER)";

    static final String DELETE_PROBLEM_TABLE = "DROP TABLE IF EXISTS "+ TABLE_NAME;
    private static final String CLEAR_PROBLEM_TABLE = "DELETE FROM " + TABLE_NAME; //TODO only drop posted

    private ApiProblemContract() {}

    static void writeProblems(DatabaseHelper helper, List<ApiServiceRequestGet> problems) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(CLEAR_PROBLEM_TABLE); // clear necessary?

        for (ApiServiceRequestGet problem : problems) {
            ContentValues values = new ContentValues();
            values.put(Entry.COLUMN_TICKET_ID, problem.getTicketId());
            ApiReporter reporter = problem.getReporter();
            if (reporter != null) {
                values.put(Entry.COLUMN_REPORTER_NAME, reporter.getName());
                values.put(Entry.COLUMN_REPORTER_PHONE, reporter.getPhone());
                values.put(Entry.COLUMN_REPORTER_EMAIL, reporter.getEmail());
                values.put(Entry.COLUMN_REPORTER_ACCOUNT, reporter.getAccount());
            }
            ApiService category = problem.getService();
            if (category != null) {
                values.put(Entry.COLUMN_CATEGORY_NAME, category.getName());
                values.put(Entry.COLUMN_CATEGORY_ID, category.getId());
                values.put(Entry.COLUMN_CATEGORY_PRIORITY, category.getPriority());
                values.put(Entry.COLUMN_CATEGORY_CODE, category.getCode());
            }
            ApiLocation location = problem.getLocation();
            if (location != null) {
                values.put(Entry.COLUMN_LATITUDE, location.getLatitude());
                values.put(Entry.COLUMN_LONGITUDE, location.getLongitude());
            }
            values.put(Entry.COLUMN_ADDRESS, problem.getAddress());
            values.put(Entry.COLUMN_DESCRIPTION, problem.getDescription());
            //TODO implement
            ApiAttachment[] attachments = problem.getAttachments();
            if (attachments != null && attachments.length > 0) {
                String json = new Gson().toJson(attachments);
                values.put(Entry.COLUMN_ATTACHMENT_JSON, json);
            }
            values.put(Entry.COLUMN_STATUS_IS_OPEN, problem.isOpen() ? 0 : 1);
            ApiStatus status = problem.getStatus();
            if (status != null) {
                values.put(Entry.COLUMN_STATUS_NAME, status.getName());
                values.put(Entry.COLUMN_STATUS_COLOR, status.getColor());
            }
            values.put(Entry.COLUMN_CREATED_AT, problem.getCreatedAtMills());
            values.put(Entry.COLUMN_UPDATED_AT, problem.getUpdatedAtMills());
            values.put(Entry.COLUMN_RESOLVED_AT, problem.getResolvedAtMills());
            //TODO implement
            //values.put(Entry.COLUMN_COMMENT_JSON, problem.getTicketNumber());
            values.put(Entry.COLUMN_POSTED, 1);

            long newRowId = db.insert(TABLE_NAME, null, values);
            System.out.println("New row inserted: "+ newRowId);
        }
    }

    static ArrayList<Problem> readProblems(DatabaseHelper helper) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                Entry.COLUMN_TICKET_ID,
                Entry.COLUMN_REPORTER_NAME,
                Entry.COLUMN_REPORTER_PHONE,
                Entry.COLUMN_REPORTER_EMAIL,
                Entry.COLUMN_REPORTER_ACCOUNT,
                Entry.COLUMN_CATEGORY_NAME,
                Entry.COLUMN_CATEGORY_ID,
                Entry.COLUMN_CATEGORY_PRIORITY,
                Entry.COLUMN_CATEGORY_CODE,
                Entry.COLUMN_LATITUDE,
                Entry.COLUMN_LONGITUDE,
                Entry.COLUMN_ADDRESS,
                Entry.COLUMN_DESCRIPTION,
                Entry.COLUMN_ATTACHMENT_JSON,
                Entry.COLUMN_STATUS_IS_OPEN,
                Entry.COLUMN_STATUS_NAME,
                Entry.COLUMN_STATUS_COLOR,
                Entry.COLUMN_CREATED_AT,
                Entry.COLUMN_UPDATED_AT,
                Entry.COLUMN_RESOLVED_AT,
                Entry.COLUMN_COMMENT_JSON,
                Entry.COLUMN_POSTED
        };

        Cursor cursor = db.query(TABLE_NAME, projection,null,null,null,null,null);
        int ticketIdIndex = cursor.getColumnIndexOrThrow(Entry.COLUMN_TICKET_ID);
        int nameIndex = cursor.getColumnIndexOrThrow(Entry.COLUMN_REPORTER_NAME);
        int phoneIndex = cursor.getColumnIndexOrThrow(Entry.COLUMN_REPORTER_PHONE);

        ArrayList<Problem> problems = new ArrayList<>();
        while (cursor.moveToNext()) {
            Problem.Builder builder = new Problem.Builder(null);

            // ticket id
            String ticketNumber = cursor.getString(ticketIdIndex);

            // reporter info
            String username = cursor.getString(nameIndex);
            String phone = cursor.getString(phoneIndex);
            String email = cursor.getString(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_REPORTER_EMAIL));
            String accountNumber = cursor.getString(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_REPORTER_ACCOUNT));

            // category info
            String categoryName = cursor.getString(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_CATEGORY_NAME));
            String categoryId = cursor.getString(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_CATEGORY_ID));
            int categoryPriority = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_CATEGORY_PRIORITY));
            String categoryCode = cursor.getString(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_CATEGORY_CODE));

            // location info
            Location location = new Location("");
            location.setLatitude(cursor.getDouble(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_LATITUDE)));
            location.setLongitude(cursor.getDouble(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_LONGITUDE)));

            String address = cursor.getString(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_ADDRESS));

            // other
            String description = cursor.getString(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_DESCRIPTION));

            //TODO Implement attachments
            String attachmentJson = cursor.getString(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_ATTACHMENT_JSON));

            Type listType = new TypeToken<ArrayList<Attachment>>(){}.getType();
            List<Attachment> attachments = new Gson().fromJson(attachmentJson, listType);

            // status
            boolean isOpen = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_STATUS_IS_OPEN)) > 0;
            String statusName = cursor.getString(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_STATUS_NAME));
            String statusColor = cursor.getString(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_STATUS_COLOR));

            // dates
            Calendar createdAt = DateUtils.getCalendarFromDbMills(cursor.getLong(
                    cursor.getColumnIndexOrThrow(Entry.COLUMN_CREATED_AT)));
            Calendar updatedAt = DateUtils.getCalendarFromDbMills(cursor.getLong(
                            cursor.getColumnIndexOrThrow(Entry.COLUMN_UPDATED_AT)));
            Calendar resolvedAt = DateUtils.getCalendarFromDbMills(cursor.getLong(
                            cursor.getColumnIndexOrThrow(Entry.COLUMN_RESOLVED_AT)));

            // TODO Implement comments
            //String commentIds = cursor.getString(
            //        cursor.getColumnIndexOrThrow(Entry.COLUMN_COMMENT_JSON));

            Problem problem = builder.buildWithoutValidation(username, phone, email, accountNumber,
                    new Category(categoryName, categoryId, categoryPriority, categoryCode),
                    location, address, description, ticketNumber,
                    new Status(isOpen, statusName, statusColor),
                    createdAt, updatedAt, resolvedAt, attachments);

            problems.add(problem);
        }
        cursor.close();

        // todo: sort?

        return problems;
    }

    static class Entry implements BaseColumns {
        static final String TABLE_NAME = "problems";

        static final String COLUMN_TICKET_ID = "_id";
        static final String COLUMN_REPORTER_NAME = "name";
        static final String COLUMN_REPORTER_PHONE = "phone";
        static final String COLUMN_REPORTER_EMAIL = "email";
        static final String COLUMN_REPORTER_ACCOUNT = "account";
        static final String COLUMN_CATEGORY_NAME = "category_name";
        static final String COLUMN_CATEGORY_ID = "category_id";
        static final String COLUMN_CATEGORY_PRIORITY = "category_priority";
        static final String COLUMN_CATEGORY_CODE = "category_code";
        static final String COLUMN_LATITUDE = "latitude";
        static final String COLUMN_LONGITUDE = "longitude";
        static final String COLUMN_ADDRESS = "address";
        static final String COLUMN_DESCRIPTION = "description";
        static final String COLUMN_ATTACHMENT_JSON = "attachment_ids";
        static final String COLUMN_STATUS_IS_OPEN = "status_is_open";
        static final String COLUMN_STATUS_NAME = "status_name";
        static final String COLUMN_STATUS_COLOR = "status_color";
        static final String COLUMN_CREATED_AT = "created_at";
        static final String COLUMN_UPDATED_AT = "updated_at";
        static final String COLUMN_RESOLVED_AT = "resolved_at";
        static final String COLUMN_COMMENT_JSON = "comment_ids";

        static final String COLUMN_POSTED = "is_posted";
    }
}
