package net.leidra.tracker.web;

/**
 * Created by afuentes on 21/04/2017.
 */
public class LocationVO {
    Double latitude, longitude, accuracy;

    public LocationVO(Double latitude, Double longitude, Double accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getAccuracy() {
        return accuracy;
    }

}
