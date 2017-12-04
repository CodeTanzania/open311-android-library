package com.example.majifix311;

import android.graphics.Bitmap;
import android.os.Parcel;

import com.example.majifix311.api.ApiModelConverter;
import com.example.majifix311.api.models.ApiAttachment;
import com.example.majifix311.models.Attachment;
import com.example.majifix311.models.Problem;
import com.example.majifix311.utils.AttachmentUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * This tests the attachment objects.
 */


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AttachmentTest {
    private String name = "Kitty";
    private String caption = "is CUTE";
    private String mime = "image/png";
    private String content = "somelongstring123";

    @Test
    public void testParcelAttachment() {
        Attachment original = new Attachment(name, caption, mime, content);
        Parcel parcel = Parcel.obtain();
        original.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Attachment fromParcel = Attachment.CREATOR.createFromParcel(parcel);
        assertMatchesMock(fromParcel);
    }

    @Test
    public void convertFromApiAttachment() {
        ApiAttachment apiAttachment = new ApiAttachment(name, caption, content);
        Attachment converted = ApiModelConverter.convert(apiAttachment);
        assertMatchesMock(converted);
    }

    @Test
    public void convertToApiAttachment() {
        Attachment attachment = new Attachment(name, caption, mime, content);
        ApiAttachment converted = ApiModelConverter.convert(attachment);
        assertMatchesMock(converted);
    }

    @Test
    public void canSaveAttachmentToFile() {
        Attachment attachment = new Attachment(name, caption, mime, Mocks.mockAttachmentContent);
        String url = AttachmentUtils.saveAttachment(attachment);
        Bitmap bitmap = AttachmentUtils.getScaledBitmap(url, 125, 125);
        assertNotNull(bitmap);
    }

    @Test
    public void canCreateAttachmentFromFilepath() {
        Attachment before = new Attachment(name, caption, mime, Mocks.mockAttachmentContent);
        String url = AttachmentUtils.saveAttachment(before);
        Attachment after = AttachmentUtils.getPicAsAttachment(url);

        assertNotNull(after);
        assertNotNull(after.getName());
        assertNotNull(after.getCaption());
        assertNotNull(after.getMime());
        assertNotNull(after.getContent());

        Bitmap bitmap = AttachmentUtils.decodeFromBase64String(after.getContent());
        assertNotNull(bitmap);
    }

    @Test
    public void canCreateApiAttachmentFromFilepath() {
        Attachment before = new Attachment(name, caption, mime, Mocks.mockAttachmentContent);
        String url = AttachmentUtils.saveAttachment(before);
        List<String> urls = new ArrayList<>();
        urls.add(url);
        ApiAttachment[] after = ApiModelConverter.getFromFile(urls);

        assertNotNull(after);
        assertNotNull(after[0]);
        assertNotNull(after[0].getName());
        assertNotNull(after[0].getCaption());
        assertNotNull(after[0].getMime());
        assertNotNull(after[0].getContent());

        Bitmap bitmap = AttachmentUtils.decodeFromBase64String(after[0].getContent());
        assertNotNull(bitmap);
    }

    private void assertMatchesMock(Attachment attachment) {
        assertEquals("Name should be the same", name, attachment.getName());
        assertEquals("Caption should be the same", caption, attachment.getCaption());
        assertEquals("Mime should be the same", mime, attachment.getMime());
        assertEquals("Content should be the same", content, attachment.getContent());
    }

    private void assertMatchesMock(ApiAttachment apiAttachment) {
        assertEquals("Name should be the same", name, apiAttachment.getName());
        assertEquals("Caption should be the same", caption, apiAttachment.getCaption());
        assertEquals("Mime should be the same", mime, apiAttachment.getMime());
        assertEquals("Content should be the same", content, apiAttachment.getContent());
    }
}
