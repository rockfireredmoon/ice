package org.icenet.client;

public class ForumItem {

    private String title;
    private long date;

    public ForumItem(String title, long date) {
	super();
	this.title = title;
	this.date = date;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public long getDate() {
	return date;
    }

    public void setDate(long date) {
	this.date = date;
    }

}
