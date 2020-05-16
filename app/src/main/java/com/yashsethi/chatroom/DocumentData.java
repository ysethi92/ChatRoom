package com.yashsethi.chatroom;

import java.util.ArrayList;

public class DocumentData {

    ArrayList<MessageContainer> messageContainer;

    public ArrayList<MessageContainer> getMessage() {
        return messageContainer;
    }

    public void setMessage(ArrayList<MessageContainer> messageContainer) {
        this.messageContainer = messageContainer;
    }

    public void clearData()
    {
        this.messageContainer.clear();
    }

    public void addMessage(MessageContainer message)
    {
        this.messageContainer.add(message);
    }
}
