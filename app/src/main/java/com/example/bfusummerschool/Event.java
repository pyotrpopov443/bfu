package com.example.bfusummerschool;

public class Event {

    private String time;
    private String place;
    private String event;
    private String professor;
    private String assistant;

    public Event() {}

    public Event(String time, String place, String what, String professor, String assistant) {
        this.time = time;
        this.place = place;
        this.event = what;
        this.professor = professor;
        this.assistant = assistant;
    }

    public String getTime() {
        return time;
    }

    public String getPlace() {
        return place;
    }

    public String getEvent() {
        return event;
    }

    public String getProfessor() {
        return professor;
    }

    public String getAssistant() {
        return assistant;
    }

}
