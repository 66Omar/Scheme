package com.scheme.models;

import java.util.List;

public class LectureResponse {
    private List<List<String>> lectures;

    public LectureResponse(List<List<String>> lectures) {
        this.lectures = lectures;
    }

    public List<List<String>> getLectures() {
        return lectures;
    }

    public void setLectures(List<List<String>> lectures) {
        this.lectures = lectures;
    }
}
