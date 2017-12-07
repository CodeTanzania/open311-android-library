package com.github.codetanzania.open311.android.library.api;

import android.support.annotation.VisibleForTesting;

import com.github.codetanzania.open311.android.library.api.models.ApiService;
import com.github.codetanzania.open311.android.library.models.Attachment;
import com.github.codetanzania.open311.android.library.models.Category;
import com.github.codetanzania.open311.android.library.models.Problem;
import com.github.codetanzania.open311.android.library.api.models.ApiAttachment;
import com.github.codetanzania.open311.android.library.api.models.ApiServiceRequest;
import com.github.codetanzania.open311.android.library.api.models.ApiLocation;
import com.github.codetanzania.open311.android.library.api.models.ApiReporter;
import com.github.codetanzania.open311.android.library.api.models.ApiServiceRequestGet;
import com.github.codetanzania.open311.android.library.api.models.ApiServiceRequestPost;
import com.github.codetanzania.open311.android.library.models.Reporter;
import com.github.codetanzania.open311.android.library.utils.AttachmentUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This is used to saveToFile between the objects from the server, and the objects
 * used within the MajiFix application.
 *
 * Note: Attachments are saved to file system, and accessed from there via a URL, so as
 * to ensure that the can easily be passed around via parcelables and intents.
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

    private static void convertShared(ApiServiceRequest request, Problem problem) {
        if (problem == null || request == null) {
            return;
        }
        request.setReporter(new ApiReporter(problem.getReporter()));
        if (problem.getLocation() != null) {
            request.setLocation(new ApiLocation(problem.getLocation().getLatitude(),
                            problem.getLocation().getLongitude()));
        }
        request.setAddress(problem.getAddress());
        request.setDescription(problem.getDescription());
        request.setAttachments(getFromFile(problem.getAttachments()));
    }

    public static List<String> saveToFile(ApiAttachment[] apiAttachments) {
        if (apiAttachments == null) {
            return null;
        }
        List<String> attachments = new ArrayList<>(apiAttachments.length);
        for (ApiAttachment apiAttachment : apiAttachments) {
            Attachment attachment = convert(apiAttachment);
            String path = AttachmentUtils.saveAttachment(attachment);
            attachments.add(path);
        }
        return attachments;
    }

    public static ApiAttachment[] getFromFile(List<String> attachmentUrls) {
        if (attachmentUrls == null || attachmentUrls.isEmpty()) {
            return null;
        }
        ApiAttachment[] apiAttachments = new ApiAttachment[attachmentUrls.size()];
        for (int i = 0; i < attachmentUrls.size(); i++) {
            Attachment attachment = AttachmentUtils.getPicAsAttachment(attachmentUrls.get(i));
            if (attachment != null) {
                apiAttachments[i] = convert(attachment);
            }
        }
        return apiAttachments;
    }

    @VisibleForTesting
    public static Attachment convert(ApiAttachment apiAttachment) {
        if (apiAttachment == null) {
            return null;
        }
        return new Attachment(apiAttachment.getName(), apiAttachment.getCaption(),
                apiAttachment.getMime(), apiAttachment.getContent());
    }

    @VisibleForTesting
    public static ApiAttachment convert(Attachment attachment) {
        if (attachment == null) {
            return null;
        }
        return new ApiAttachment(attachment.getName(), attachment.getCaption(),
                attachment.getMime(), attachment.getContent());
    }
}
