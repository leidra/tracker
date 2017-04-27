package net.leidra.tracker.backend;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * Created by afuentes on 4/09/16.
 */
@Entity
public class Assistance {

    public static final int HOUR_IN_MINUTES = 60;

    public enum Type {
        ASSISTANCE("Asistencia"), LOGIN("Acceso"), LOCATION("Ubicación");
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
    private LocalDateTime start;
    private LocalDateTime end;
    private Type type;

    public Assistance() {
    }

    public Assistance(String latitude, String longitude, Double accuracy, Type type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.type = type;
    }

    public Assistance(String latitude, String longitude, Double accuracy, Type type, LocalDateTime start) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.type = type;
        this.start = start;
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

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
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

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getDuration() {
        if(getStart() != null && getEnd() != null) {
            Duration duration = Duration.between(getStart(), getEnd());
            long hours = duration.toHours();
            long minutes = duration.toMinutes() > HOUR_IN_MINUTES ? (duration.toMinutes() - (HOUR_IN_MINUTES * hours)) : duration.toMinutes();
            return hours + " horas " + minutes + " minutos";
        }

        return Type.ASSISTANCE.equals(getType()) ? "En curso" : StringUtils.EMPTY;
    }
}