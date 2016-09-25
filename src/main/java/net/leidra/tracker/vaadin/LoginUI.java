package net.leidra.tracker.vaadin;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import net.leidra.tracker.backend.*;
import net.leidra.tracker.vaadin.geolocation.Location;
import net.leidra.tracker.vaadin.geolocation.LocationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MPasswordField;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.function.Consumer;

/**
 * Created by afuentes on 3/09/16.
 */
@SpringUI(path = "/login")
@Title("LoginPage")
@Theme("valo")
public class LoginUI extends UI {
    private LoginForm loginForm;
    @Autowired
    private DaoAuthenticationProvider daoAuthenticationProvider;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository repo;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        loginForm = new LoginForm();
        loginForm.setLoginHandler((l) -> this.login((LocationVO) l));

        setContent(loginForm);
    }

    private void login(LocationVO location) {
        try {
            authenticateUser(location);
        } catch(DisabledException e) {
            Notification.show("Este usuario está deshabilitado. Contacte con el administrador");
        }
    }

    private void authenticateUser(LocationVO location) throws DisabledException {
        User user = new User();
        user.setUsername(loginForm.username.getValue());
        user.setPassword(loginForm.password.getValue());
        Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        try {
            SecurityContextHolder.getContext().setAuthentication(daoAuthenticationProvider.authenticate(auth));
        } catch(AuthenticationException ex) {
            Notification.show("Acceso no permitido, compuebe sus credenciales.");
        }

        if(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            user = repo.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName());
            String page = user.getRole().toString().toLowerCase();
            if(Role.RoleDefinition.DOMICILIO.toString().equals(user.getRole().getName())
            || Role.RoleDefinition.CENTRO.toString().equals(user.getRole().getName())) {
                user.getAssistances().add(new Assistance(location.getLatitude().toString(), location.getLongitude().toString(), location.getAccuracy(), Assistance.Type.LOGIN));
                repo.save(user);
                page = "user";
            }

            VaadinSession.getCurrent().setAttribute("user", user.getUsername());
            //redirect to main application
            getPage().setLocation("/" + page);
        }
    }

}

class LoginForm extends CssLayout {
    TextField username = new MTextField("Nombre");
    MPasswordField password = new MPasswordField("Clave");
    MButton loginButton = new MButton("Acceder");
    Location locationExtension;
    Consumer loginHandler;

    public LoginForm() {
        locationExtension = new Location(UI.getCurrent());
        locationExtension.addLocationListener(new Location.LocationListener() {
            @Override
            public void onLocationFound(double latitude, double longitude, double accuracy) {
                loginHandler.accept(new LocationVO(latitude, longitude, accuracy));
            }

            @Override
            public void onLocationError(LocationError error) {
                Notification.show("Debe habilitar la geolocalización en el navegador para poder acceder");
            }

            @Override
            public void onLocationNotSupported() {
                Notification.show("El navegador no soporta geolocalización. No puede utilizar la aplicación");
            }
        });
        username.addShortcutListener(new ShortcutListener("", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object o, Object o1) {
                loginButton.click();
            }
        });
        password.addShortcutListener(new ShortcutListener("", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object o, Object o1) {
                loginButton.click();
            }
        });
        loginButton.addClickListener(e -> locationExtension.requestLocation());
        setSizeUndefined();
        createContent();
    }

    protected void createContent() {
        username.focus();
        addComponent(new MVerticalLayout(
                new MFormLayout(
                        username,
                        password,
                        loginButton
                ).withWidth("")).withWidth(""));
    }

    public void setLoginHandler(Consumer handler) {
        this.loginHandler = handler;
    }

}

class LocationVO {
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