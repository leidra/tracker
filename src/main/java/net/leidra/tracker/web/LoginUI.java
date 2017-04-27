package net.leidra.tracker.web;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import net.leidra.tracker.web.forms.LoginForm;
import net.leidra.tracker.web.presenter.LoginPresenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;

/**
 * Created by afuentes on 3/09/16.
 */
@SpringUI(path = "/login")
@Title("LoginPage")
@Theme("acufade")
//@Widgetset("com.vaadin.v7.Vaadin7WidgetSet")
public class LoginUI extends UI {
    private LoginForm loginForm;

    private LoginPresenter loginPresenter;

    public LoginPresenter getLoginPresenter() {
        return loginPresenter;
    }

    @Autowired
    public void setLoginPresenter(LoginPresenter loginPresenter) {
        this.loginPresenter = loginPresenter;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        loginForm = new LoginForm();
        loginForm.setLoginHandler((l) -> this.login((LocationVO) l));

        setContent(loginForm);
    }

    private void login(LocationVO location) {
        try {
            getLoginPresenter().authenticateUser(location, loginForm.getUsername().getValue(), loginForm.getPassword().getValue());
        } catch(DisabledException e) {
            Notification.show("Este usuario está deshabilitado. Contacte con el administrador");
        }
    }

}

