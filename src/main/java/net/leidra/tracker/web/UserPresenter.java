package net.leidra.tracker.web;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import net.leidra.tracker.backend.Assistance;
import net.leidra.tracker.backend.Role;
import net.leidra.tracker.backend.User;
import net.leidra.tracker.backend.UserRepository;
import net.leidra.tracker.web.geolocation.Location;
import net.leidra.tracker.web.geolocation.LocationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;

/**
 * Created by afuentes on 26/04/2017.
 */
@org.springframework.stereotype.Component
public class UserPresenter {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByUserName() {
        if(retrieveAuthentication().isPresent()) {
            return Optional.ofNullable(userRepository.findByUserName(retrieveAuthentication().get().getName()));
        }
        return Optional.empty();
    }

    public Optional<Authentication> retrieveAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    public Location createLocator(UserUI userUI) {
        Location location = new Location(userUI);
        location.addLocationListener(new Location.LocationListener() {
            @Override
            public void onLocationFound(double latitude, double longitude, double accuracy) {
                handleLocationFound(latitude, longitude, accuracy, userUI);
            }

            @Override
            public void onLocationError(LocationError error) {
                Notification.show(error.toString());
            }

            @Override
            public void onLocationNotSupported() {
                Notification.show("Geolocalización no soportada");
            }
        });

        return location;
    }

    public User save(LocationVO locationVO, UserUI userUi) {
        if(isCentroRoleSaving(userUi.getUser()) || userUi.isAskingForLocation() || isDomicilioRoleSaving(userUi)) {
            Assistance assistance = createAssistance(locationVO, userUi);
            userUi.getUser().getAssistances().add(assistance);

            return userRepository.save(userUi.getUser());
        } else {
            Notification.show("Debe proporcionar el nombre del usuario", Notification.Type.ERROR_MESSAGE);
        }
        return userUi.getUser();
    }

    public void adjustFormState(UserUI components) {
        establishStartState(components);
        establishFinishState(components);
    }

    private void establishFinishState(UserUI userUI) {
        Assistance lastAssistance = retrieveLastAssistance(userUI);
        if(null != lastAssistance) {
            boolean hasStarted = lastAssistance.getStart() != null;
            boolean hasFinished = lastAssistance.getEnd() != null;

            if(!hasFinished && hasStarted) {
                userUI.getPatientName().setValue(lastAssistance.getPatientName());
                buildAssistanceDescription(lastAssistance, userUI);
                userUI.getSubmitButton().setCaption("Finalizar");
                userUI.getPatientName().setVisible(false);
            }
        }
    }

    private void establishStartState(UserUI userUi) {
        userUi.getAssistanceDescription().setVisible(false);
        userUi.getPatientName().clear();
        userUi.getPatientName().setVisible(false);
        userUi.getSubmitButton().setCaption("Comenzar");

        Component patientName = userUi.getPatientName();
        patientName.setVisible(Role.RoleDefinition.DOMICILIO.equals(userUi.getUser().getRole().getRol()));
        Optional.ofNullable(patientName.getParent()).ifPresent(cc -> cc.setVisible(patientName.isVisible()));
    }

    public Assistance retrieveLastAssistance(UserUI userUI) {
        return userUI.getUser().getAssistances()
                .parallelStream()
                .filter(a -> Assistance.Type.ASSISTANCE.equals(a.getType()))
                .sorted(Comparator.comparing(Assistance::getStart).reversed())
                .findFirst().orElse(null);
    }

    public Assistance createAssistance(LocationVO locationVO, UserUI userUI) {
        Assistance assistance = new Assistance();
        if(userUI.isAskingForLocation()) {
            userUI.setAskingForLocation(false);
            assistance.setType(Assistance.Type.LOCATION);
            assistance.setStart(LocalDateTime.now());
        } else if(!hasAssistances(userUI.getUser()) || !isEndAssistance(userUI)) {
            // Comienza una asistencia cuando no hay ninguna o la última ha finalizado
            assistance.setType(Assistance.Type.ASSISTANCE);
            assistance.setStart(LocalDateTime.now());
        } else {
            // Finaliza la última asistencia
            Assistance a = retrieveLastAssistance(userUI);
            if(a != null) {
                a.setType(Assistance.Type.ASSISTANCE);
                a.setEnd(LocalDateTime.now());
            }
            assistance = a;
        }

        if(isDomicilio(userUI.getUser())) {
            assistance.setPatientName(userUI.getPatientName().getValue());
        }
        assistance.setAccuracy(locationVO.getAccuracy());
        assistance.setLatitude("" + locationVO.getLatitude());
        assistance.setLongitude("" + locationVO.getLongitude());

        return assistance;
    }

    public void handleLocationFound(double latitude, double longitude, double accuracy, UserUI userUI) {
        userUI.setUser(save(new LocationVO(latitude, longitude, accuracy), userUI));

        adjustFormState(userUI);
    }

    private boolean isEndAssistance(UserUI userUI) {
        Assistance a = retrieveLastAssistance(userUI);
        if(a != null) {
            return a.getStart() != null && !isFinished(userUI);
        }
        return false;
    }

    private boolean isFinished(UserUI userUI) {
        Assistance a = retrieveLastAssistance(userUI);
        if(a != null) {
            return a.getEnd() != null;
        }
        return false;
    }

    public boolean isDomicilio(User user) {
        return Role.RoleDefinition.DOMICILIO.equals(user.getRole().getRol());
    }

    private boolean isCentroRoleSaving(User user) {
        return Role.RoleDefinition.CENTRO.equals(user.getRole().getRol());
    }

    private boolean isDomicilioRoleSaving(UserUI userUI) {
        return isDomicilio(userUI.getUser()) && StringUtils.hasText(userUI.getPatientName().getValue());
    }

    private boolean hasAssistances(User user) {
        return !user.getAssistances().isEmpty();
    }

    public void buildAssistanceDescription(Assistance lastAssistance, UserUI userUI) {
        DateTimeFormatter startTimeFormat = DateTimeFormatter.ofPattern("HH:mm");
        StringBuilder description = new StringBuilder();
        if(userUI.getUserPresenter().isDomicilio(userUI.getUser())) {
            description.append(MessageFormat.format(UserUI.DOMICILIO_DESCRIPTION, lastAssistance.getPatientName()));
        }
        description.append(MessageFormat.format(UserUI.START_DESCRIPTION, lastAssistance.getStart().format(startTimeFormat)));

        userUI.getAssistanceDescription().setValue(description.toString());
        Optional.ofNullable(userUI.getAssistanceDescription().getParent()).ifPresent(c -> c.setVisible(true));
        userUI.getAssistanceDescription().setVisible(true);
    }

}