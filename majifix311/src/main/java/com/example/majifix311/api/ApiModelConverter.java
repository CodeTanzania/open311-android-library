package com.example.majifix311.api;

import com.example.majifix311.Problem;
import com.example.majifix311.api.models.ApiAttachment;
import com.example.majifix311.api.models.ApiServiceRequest;
import com.example.majifix311.api.models.ApiLocation;
import com.example.majifix311.api.models.ApiReporter;
import com.example.majifix311.api.models.ApiServiceRequestGet;
import com.example.majifix311.api.models.ApiServiceRequestPost;

import java.util.Date;

/**
 * This is used to convert between the objects from the server, and the objects
 * used within the MajiFix application.
 */

public class ApiModelConverter {
    public static ApiServiceRequestPost convert(Problem problem) {
        if (problem == null) {
            return null;
        }

        ApiServiceRequestPost request = new ApiServiceRequestPost();
        convertShared(request, problem);
//        request.setService(problem.getCategory()); // TODO: with id
        request.setService("5968b64148dfc224bb47748d"); // hardcoded to lack of water for testing purposes
        return request;
    }

    public static Problem convert(ApiServiceRequestGet response) {
        if (response == null) {
            return null;
        }
        return new Problem.Builder(null).build(response);
    }

    private static ApiServiceRequest convertShared(ApiServiceRequest request, Problem problem) {
        if (problem == null || request == null) {
            return null;
        }
        request.setReporter(new ApiReporter(problem.getUsername(), problem.getPhoneNumber()));
        if (problem.getLocation() != null) {
            request.setLocation(new ApiLocation(problem.getLocation().getLatitude(),
                            problem.getLocation().getLongitude()));
        }
        request.setAddress(problem.getAddress());
        request.setDescription(problem.getDescription());
        request.setAttachments(new ApiAttachment[] {
                new ApiAttachment("Issue_" + (new Date()).getTime(),
                        problem.getDescription(), "bytes") // TODO: implement this
        });
        return request;
    }
}
