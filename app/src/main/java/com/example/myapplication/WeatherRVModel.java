package com.example.myapplication;

public class WeatherRVModel {
    private String time;
    private String temperature;
    private String iconUrl;
    private String windSpeed;

    public WeatherRVModel(String time, String temperature, String iconUrl, String windSpeed) {
        this.time = time;
        this.temperature = temperature;
        this.iconUrl = iconUrl;
        this.windSpeed = windSpeed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
}




