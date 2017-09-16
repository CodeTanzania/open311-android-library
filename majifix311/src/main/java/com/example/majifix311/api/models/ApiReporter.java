package com.example.majifix311.api.models;

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

    public ApiReporter(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}


