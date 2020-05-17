package com.yashsethi.chatroom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

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

    public void sortMessages()
    {
        Collections.sort(messageContainer, new SortbyTimeStamp());
    }
    class SortbyTimeStamp implements Comparator<MessageContainer>
    {
        @Override
        public int compare(MessageContainer a, MessageContainer b) {
            return a.timeStamp.compareTo(b.timeStamp);
        }
    }
}
