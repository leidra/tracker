package net.leidra.tracker.web;

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
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by afuentes on 26/04/2017.
 */
@Title("Gestión de asistencias")
@Theme("acufade")
@Push(value = PushMode.MANUAL, transport = Transport.LONG_POLLING)
//@Widgetset("com.vaadin.v7.Vaadin7WidgetSet")
public abstract class AbstractUI extends UI {

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

}
