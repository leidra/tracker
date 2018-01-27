package net.leidra.tracker.frontend.forms;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.*;
import com.vaadin.v7.ui.PasswordField;
import com.vaadin.v7.ui.TextField;
import net.leidra.tracker.frontend.dto.LocationVO;
import net.leidra.tracker.frontend.geolocation.Location;
import net.leidra.tracker.frontend.geolocation.LocationError;

import java.util.function.Consumer;

/**
 * Created by afuentes on 21/04/2017.
 */
public class LoginForm extends CssLayout {
    private TextField username = new TextField("Nombre");
    private PasswordField password = new PasswordField("Clave");
    private Button loginButton = new Button("Acceder");
    private Location locationExtension;
    private Consumer loginHandler;

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

    public TextField getUsername() {
        return username;
    }

    public PasswordField getPassword() {
        return password;
    }

    protected void createContent() {
        username.focus();
        VerticalLayout containerLayout = new VerticalLayout();
        containerLayout.addComponentsAndExpand(username, password, loginButton);
        addComponent(containerLayout);
    }

    public void setLoginHandler(Consumer handler) {
        this.loginHandler = handler;
    }

}