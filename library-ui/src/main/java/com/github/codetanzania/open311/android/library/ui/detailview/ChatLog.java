package com.github.codetanzania.open311.android.library.ui.detailview;

import com.github.codetanzania.open311.android.library.auth.Auth;
import com.github.codetanzania.open311.android.library.models.ChangeLog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is used to display a changelog.
 */

public class ChatLog implements IMessage {
    private ChangeLog mLog;
    private ChatUser mUser;

    public static List<ChatLog> createLogs(List<ChangeLog> changeLogs) {
        if (changeLogs == null) {
            return null;
        }
        List<ChatLog> chatBubbles = new ArrayList<>(changeLogs.size());
        for (ChangeLog change : changeLogs) {
            // TODO is this logic?
            // if logged in, show all changes
            if (Auth.getInstance().isLogin()) {
                chatBubbles.add(new ChatLog(change));
            }
            // else show only public changes
            else if (change.isPublic()) {
                chatBubbles.add(new ChatLog(change));
            }
        }
        return chatBubbles;
    }

    public ChatLog(ChangeLog changeLog) {
        mLog = changeLog;
        if (mLog.getChanger() != null) {
            mUser = new ChatUser(mLog.getChanger());
        }
    }

    @Override
    public String getId() {
        return mLog.getCreatedAtString();
    }

    @Override
    public String getText() {
        return mLog.getLog();
    }

    @Override
    public ChatUser getUser() {
        return mUser;
    }

    @Override
    public Date getCreatedAt() {
        return mLog.getCreatedAt().getTime();
    }
}
