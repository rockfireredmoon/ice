package org.icenet.client;

public class ForumPost {

    private String text;
    private int edits;
    private long date;
    private long lastEdit;
    private String author;
    private int number;

    public long getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(long lastEdit) {
        this.lastEdit = lastEdit;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    public int getEdits() {
	return edits;
    }

    public void setEdits(int edits) {
	this.edits = edits;
    }

    public long getDate() {
	return date;
    }

    public void setDate(long date) {
	this.date = date;
    }

    public String getAuthor() {
	return author;
    }

    public void setAuthor(String author) {
	this.author = author;
    }

}
