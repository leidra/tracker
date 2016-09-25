package net.leidra.tracker.backend;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Created by afuentes on 4/09/16.
 */
@Entity
public class Assistance {
    public enum Type {
        START("Entrada"), END("Salida"), LOGIN("Acceso"), LOCATION("ubicación");
        private String name;

        Type(String name) {
            this.name = name;
        }


        @Override
        public String toString() {
            return name;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotNull
    private String latitude;
    @NotNull
    private String longitude;
    private String patientName;
    private double accuracy;
    private LocalDateTime time;
    private Type type;

    public Assistance() {
    }

    public Assistance(String latitude, String longitude, Double accuracy, Type type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
}
