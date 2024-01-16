package com.korea.project2_team4.Model.Entity;


import java.time.LocalDateTime;

public class SendMessage {
    private String content;
    private String receiver;
    private String createDate;



    public SendMessage() {
    }

    public SendMessage(String content, String receiver) {

        this.content = content;
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }


    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}