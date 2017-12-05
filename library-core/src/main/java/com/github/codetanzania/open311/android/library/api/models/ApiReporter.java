package com.github.codetanzania.open311.android.library.api.models;

import android.support.annotation.NonNull;

import com.github.codetanzania.open311.android.library.models.Reporter;

/**
 * This is a reporter object as expected by the MajiFix server.
 *
 *  { "reporter":
 *      { "name": "Lally Elias",
 *        "phone": "255714095061"
 *      }
 *  }
 */

public class ApiReporter {
    private String name;
    private String phone;
    private String email;
    private String account;

    public ApiReporter(@NonNull Reporter reporter) {
        name = reporter.getName();
        phone = reporter.getPhone();
        email = reporter.getEmail();
        account = reporter.getAccount();
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAccount() {
        return account;
    }
}


