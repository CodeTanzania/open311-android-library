package com.example.majifix311;

import android.os.Parcel;

import com.example.majifix311.api.ApiModelConverter;
import com.example.majifix311.api.models.ApiAttachment;
import com.example.majifix311.models.Attachment;
import com.example.majifix311.models.Problem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;

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
        ApiAttachment attachment = new ApiAttachment(name, caption, content);
        Attachment converted = ApiModelConverter.convert(attachment);
        assertMatchesMock(converted);
    }
    private void assertMatchesMock(Attachment attachment) {
        assertEquals("Name should be the same", name, attachment.getName());
        assertEquals("Caption should be the same", caption, attachment.getCaption());
        assertEquals("Mime should be the same", mime, attachment.getMime());
        assertEquals("Content should be the same", content, attachment.getContent());
    }
}
