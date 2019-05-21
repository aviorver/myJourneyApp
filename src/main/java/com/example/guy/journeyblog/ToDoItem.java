package com.example.guy.journeyblog;

public class ToDoItem {
    private String id,title,description,currentid;
    public ToDoItem()
    {

    }

    public ToDoItem(String id, String title, String description, String currentid) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.currentid= currentid;
    }

    public String getCurrentid() {
        return currentid;
    }

    public void setCurrentid(String currentid) {
        this.currentid = currentid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
