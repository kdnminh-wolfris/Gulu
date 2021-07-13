package com.example.gulu;

public class QHistoryItem {
    private int id;
    private String content;
    private byte[] image;

    public QHistoryItem(int id, String content,  byte[] image) {
        this.id = id;
        this.content = content;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getString(){return content;}

    public void setString(String content){this.content = content;}

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
