package net.leidra.tracker.vaadin;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import net.leidra.tracker.backend.*;
import net.leidra.tracker.vaadin.geolocation.Location;
import net.leidra.tracker.vaadin.geolocation.LocationError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.time.LocalDateTime;
import java.util.Set;

@Title("Gestión de asistencias")
@Theme("tracker")
@SpringUI(path = "/user")
@Push(transport = Transport.LONG_POLLING)
public class UserUI extends UI implements Broadcaster.BroadcastListener {
	private static final long serialVersionUID = 1L;

    private MTextField patientName = new MTextField();
    private Button locationButton = new Button();
    private Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    private Location locationExtension;
    private boolean askingForLocation = false;

    @Autowired
    private UserRepository userRepository;
    private User user;

    @Override
    protected void init(VaadinRequest request) {
        Broadcaster.register(this);
        createUI();
    }

    private void createUI() {
        createLocator();
        this.user = userRepository.findByUserName(authentication.getName());
        Assistance lastAssistance = adjustFormState();

        Panel container = new Panel();
        container.addStyleName(ValoTheme.PANEL_BORDERLESS);
        container.setSizeUndefined();
        patientName.setCaption("Paciente");
        if(lastAssistance.getType() == Assistance.Type.START) {
            patientName.setValue(lastAssistance.getPatientName());
        }
        container.setContent(new VerticalLayout(patientName, locationButton));
        MVerticalLayout layout = createContainer(container);
        locationButton.addClickListener(e -> {
            locationExtension.requestLocation();
        });
        setContent(layout);
    }

    private Assistance adjustFormState() {
        Set<Assistance> assistanceSet = user.getAssistances();
        Assistance lastAssistance = assistanceSet.iterator().next();
        locationButton.setCaption(lastAssistance.getType() == Assistance.Type.START ? "Finalizar"
                : lastAssistance.getType() == Assistance.Type.END ? "Comenzar" : "Comenzar");
        patientName.setVisible(Role.RoleDefinition.DOMICILIO.equals(user.getRole().getRol()) && !Assistance.Type.START.equals(lastAssistance.getType()));
        return lastAssistance;
    }

    private void createLocator() {
        locationExtension = new Location(getCurrent());
        locationExtension.addLocationListener(new Location.LocationListener() {
            @Override
            public void onLocationFound(double latitude, double longitude, double accuracy) {
                boolean refresh = !askingForLocation;
                user = save(new LocationVO(latitude, longitude, accuracy));
                if(refresh) {
                    getPage().setLocation(getPage().getLocation());
                }
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
    }

    @Override
    public void detach() {
        Broadcaster.unregister(this);
        super.detach();
    }

    private MVerticalLayout createContainer(Panel container) {
        MVerticalLayout layout = new MVerticalLayout(container);
        layout.setComponentAlignment(container, Alignment.MIDDLE_CENTER);
        layout.setHeight("70%");
        setContent(layout);
        return layout;
    }

    private void logout() {
        authentication.setAuthenticated(false);

        getPage().setLocation("/login");
    }

    private User save(LocationVO locationVO) {
        if(Role.RoleDefinition.CENTRO.equals(user.getRole().getRol())
        || (askingForLocation || (Role.RoleDefinition.DOMICILIO.equals(user.getRole().getRol())
        && StringUtils.isNotBlank(patientName.getValue())))) {
            Assistance assistance = createAssistance(locationVO, user);
            user.getAssistances().add(assistance);

            return userRepository.save(user);
        } else {
            Notification.show("Debe proporcionar el nombre del paciente", Notification.Type.ERROR_MESSAGE);
        }
        return user;
    }

    private Assistance createAssistance(LocationVO locationVO, User user) {
        Assistance assistance = new Assistance();
        if(askingForLocation) {
            askingForLocation = false;
            assistance.setType(Assistance.Type.LOCATION);
        } else if(user.getAssistances().isEmpty()) {
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

    @Override
    public void receiveBroadcast() {
        access(() -> {
            askingForLocation = true;
            locationExtension.requestLocation();
        });
    }

    public String getUserName() {
        return user.getUsername();
    }
}
