package org.icenet.client;

public class ForumTopic extends ForumItem {

    private int id;

    public ForumTopic(String title, long date) {
	super(title, date);
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

}
