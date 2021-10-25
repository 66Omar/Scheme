package com.scheme.models;

import java.util.List;

public class OptionsResponse {
    private List<String> options;

    public OptionsResponse(List<String> options) {
        this.options = options;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
