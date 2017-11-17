package com.example.majifix311.api;

import com.example.majifix311.api.models.ApiService;
import com.example.majifix311.models.Attachment;
import com.example.majifix311.models.Category;
import com.example.majifix311.models.Problem;
import com.example.majifix311.api.models.ApiAttachment;
import com.example.majifix311.api.models.ApiServiceRequest;
import com.example.majifix311.api.models.ApiLocation;
import com.example.majifix311.api.models.ApiReporter;
import com.example.majifix311.api.models.ApiServiceRequestGet;
import com.example.majifix311.api.models.ApiServiceRequestPost;
import com.example.majifix311.models.Reporter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        request.setService(problem.getCategory().getId());
        return request;
    }

    public static Problem convert(ApiServiceRequestGet response) {
        if (response == null) {
            return null;
        }
        // This logic is in the builder because the Problem constructor is protected
        return new Problem.Builder(null).build(response);
    }

    public static Reporter convert(ApiReporter apiReporter) {
        Reporter reporter = new Reporter();
        reporter.setName(apiReporter.getName());
        reporter.setPhone(apiReporter.getPhone());
        reporter.setEmail(apiReporter.getEmail());
        reporter.setAccount(apiReporter.getAccount());
        return reporter;
    }

    public static Category convert(ApiService apiCategory) {
        return new Category(apiCategory.getName(),
                apiCategory.getId(),
                apiCategory.getPriority(),
                apiCategory.getCode());
    }

    private static ApiServiceRequest convertShared(ApiServiceRequest request, Problem problem) {
        if (problem == null || request == null) {
            return null;
        }
        request.setReporter(new ApiReporter(problem.getReporter()));
        if (problem.getLocation() != null) {
            request.setLocation(new ApiLocation(problem.getLocation().getLatitude(),
                            problem.getLocation().getLongitude()));
        }
        request.setAddress(problem.getAddress());
        request.setDescription(problem.getDescription());
//        request.setAttachments(new ApiAttachment[] {
//                new ApiAttachment("Issue_" + (new Date()).getTime(),
//                        problem.getDescription(), "bytes") // TODO: implement this
//        });
        return request;
    }

    public static Attachment convert(ApiAttachment apiAttachment) {
        if (apiAttachment == null) {
            return null;
        }
        return new Attachment(apiAttachment.getName(), apiAttachment.getCaption(),
                apiAttachment.getMime(), apiAttachment.getContent());
    }

    public static List<Attachment> convert(ApiAttachment[] apiAttachments) {
        if (apiAttachments == null) {
            return null;
        }
        List<Attachment> attachments = new ArrayList<>(apiAttachments.length);
        for (ApiAttachment attachment : apiAttachments) {
            attachments.add(convert(attachment));
        }
        return attachments;
    }
}
