package com.example.bfusummerschool;

public class Event {

    private String when;
    private String where;
    private String which;
    private String who;

    public Event() {}

    public Event(String when, String where, String which, String who) {
        this.when = when;
        this.where = where;
        this.which = which;
        this.who = who;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getWhich() {
        return which;
    }

    public void setWhich(String which) {
        this.which = which;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }
}
