package net.leidra.tracker.frontend.ui;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;

/**
 * Created by afuentes on 26/04/2017.
 */
@Title("Gestión de asistencias")
@Theme("application")
@Push(value = PushMode.MANUAL, transport = Transport.LONG_POLLING)
@Widgetset("AppWidgetset")
public abstract class AbstractUI extends UI {
    @Value("#{'${net.leidra.tracker.features}'.split('\"')}")
    protected List<String> features;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    protected CssLayout createHeaderContainer() {
        CssLayout headerContainer = new CssLayout();
        Label title = new Label("<h1>Gestión de asistencias</h1>", ContentMode.HTML);
        title.addStyleName("title");
        headerContainer.addComponent(title);
        headerContainer.addStyleName("header");
        return headerContainer;
    }

    protected CssLayout createMainContainer(Layout... layouts) {
        CssLayout mainContainer = new CssLayout(layouts);
        mainContainer.addStyleName("main-container");
        return mainContainer;
    }

    protected CssLayout createToolbar() {
        CssLayout innerButtons = createToolbarButtons();
        Button logoutButton = createLogoutButton();
        CssLayout toolbar = new CssLayout(innerButtons, logoutButton);
        toolbar.addStyleName("toolbar");
        return toolbar;
    }

    protected Button createLogoutButton() {
        Button logoutButton = new Button("Salir");
        logoutButton.addStyleName("logout-button");
        logoutButton.addClickListener(e -> logout());

        return logoutButton;
    }

    protected void logout() {
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);

        getPage().setLocation("/login");
    }

    protected CssLayout createToolbarButtons() {
        return new CssLayout();
    }

    public List<String> getFeatures() {
        return features;
    }
}