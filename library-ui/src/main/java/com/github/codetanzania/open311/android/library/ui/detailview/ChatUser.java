package com.github.codetanzania.open311.android.library.ui.detailview;

import com.github.codetanzania.open311.android.library.models.Party;
import com.stfalcon.chatkit.commons.models.IUser;

/**
 * This is used to show changelogs.
 */

public class ChatUser implements IUser {
    private Party mParty;

    public ChatUser(Party party) {
        mParty = party;
    }

    @Override
    public String getId() {
        return mParty.getObjectId();
    }

    @Override
    public String getName() {
        return mParty.getName();
    }

    @Override
    public String getAvatar() {
//        return mParty.getAvatar();
        return "blue";
    }
}
