package net.leidra.tracker.vaadin;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import net.leidra.tracker.backend.Assistance;
import net.leidra.tracker.backend.Role;
import net.leidra.tracker.backend.User;
import net.leidra.tracker.backend.UserRepository;
import net.leidra.tracker.vaadin.geolocation.Location;
import net.leidra.tracker.vaadin.geolocation.LocationError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.time.LocalDateTime;
import java.util.Set;

@Title("Gestión de asistencias")
@Theme("tracker")
@SpringUI(path = "/user")
public class UserUI extends UI {
	private static final long serialVersionUID = 1L;

    MTextField patientName = new MTextField();
    Button locationButton = new Button();
    Button logoutButton;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void init(VaadinRequest request) {
        User user = userRepository.findByUserName(authentication.getName());
        Set<Assistance> assistanceSet = user.getAssistances();
        Assistance lastAssistance = assistanceSet.iterator().next();
        locationButton.setCaption(lastAssistance.getType() == Assistance.Type.START ? "Finalizar"
                            : lastAssistance.getType() == Assistance.Type.END ? "Comenzar" : "Comenzar");

        Button logoutButton = createLogoutButton();
        MHorizontalLayout toolbar = createToolbar(logoutButton);

        Panel container = new Panel();
        container.addStyleName(ValoTheme.PANEL_BORDERLESS);
        container.setSizeUndefined();
        patientName.setCaption("Paciente");
        patientName.setVisible(Role.RoleDefinition.DOMICILIO.equals(user.getRole().getRol()) && !Assistance.Type.START.equals(lastAssistance.getType()));
        if(lastAssistance.getType() == Assistance.Type.START) {
            patientName.setValue(lastAssistance.getPatientName());
        }
        container.setContent(new VerticalLayout(patientName, locationButton));
        MVerticalLayout layout = createContainer(toolbar, container);

        Location locationExtension = new Location(UI.getCurrent());
        locationExtension.addLocationListener(new Location.LocationListener() {
            @Override
            public void onLocationFound(double latitude, double longitude, double accuracy) {
                save(new LocationVO(latitude, longitude, accuracy));
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
        locationButton.addClickListener(e -> locationExtension.requestLocation());
        setContent(layout);
    }

    private MVerticalLayout createContainer(MHorizontalLayout toolbar, Panel container) {
        MVerticalLayout layout = new MVerticalLayout(toolbar, container);
        layout.setComponentAlignment(container, Alignment.MIDDLE_CENTER);
        layout.setHeight("70%");
        setContent(layout);
        return layout;
    }

    private MHorizontalLayout createToolbar(Button logoutButton) {
        MHorizontalLayout toolbar = new MHorizontalLayout(logoutButton);
        toolbar.setWidth("100%");
        toolbar.setComponentAlignment(logoutButton, Alignment.MIDDLE_RIGHT);
        return toolbar;
    }

    private Button createLogoutButton() {
        logoutButton = new Button("Salir");
        logoutButton.addClickListener(e -> logout());
        return logoutButton;
    }

    private void logout() {
        authentication.setAuthenticated(false);

        getPage().setLocation("/login");
    }

    private void save(LocationVO locationVO) {
        User user = userRepository.findByUserName(authentication.getName());
        if(Role.RoleDefinition.CENTRO.equals(user.getRole().getRol())
        || (Role.RoleDefinition.DOMICILIO.equals(user.getRole().getRol())
        && StringUtils.isNotBlank(patientName.getValue()))) {
            Assistance assistance = createAssistance(locationVO, user);
            user.getAssistances().add(assistance);

            userRepository.save(user);
            logout();
        } else {
            Notification.show("Debe proporcionar el nombre del paciente", Notification.Type.ERROR_MESSAGE);
        }
    }

    private Assistance createAssistance(LocationVO locationVO, User user) {
        Assistance assistance = new Assistance();
        if(user.getAssistances().isEmpty()) {
            assistance.setType(Assistance.Type.START);
        } else if(user.getAssistances().iterator().next().getType() == Assistance.Type.START) {
            assistance.setType(Assistance.Type.END);
        } else {
            assistance.setType(Assistance.Type.START);
        }
        if(Role.RoleDefinition.DOMICILIO.equals(user.getRole().getRol())) {
            assistance.setPatientName(patientName.getValue());
        }
        assistance.setAccuracy(locationVO.getAccuracy());
        assistance.setLatitude("" + locationVO.getLatitude());
        assistance.setLongitude("" + locationVO.getLongitude());
        assistance.setTime(LocalDateTime.now());
        return assistance;
    }
}
