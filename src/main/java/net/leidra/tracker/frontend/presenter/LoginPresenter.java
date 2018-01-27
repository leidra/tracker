package net.leidra.tracker.frontend.presenter;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import net.leidra.tracker.backend.Assistance;
import net.leidra.tracker.backend.Role;
import net.leidra.tracker.backend.User;
import net.leidra.tracker.backend.UserRepository;
import net.leidra.tracker.frontend.dto.LocationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

/**
 * Created by afuentes on 21/04/2017.
 */
@org.springframework.stereotype.Component
public class LoginPresenter {
    @Autowired
    private DaoAuthenticationProvider daoAuthenticationProvider;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository repo;

    public LoginPresenter() {
        super();
    }

    public DaoAuthenticationProvider getDaoAuthenticationProvider() {
        return daoAuthenticationProvider;
    }

    public BCryptPasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public UserRepository getRepo() {
        return repo;
    }

    public void authenticateUser(LocationVO location, String userName, String password) throws DisabledException {
        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);

        authenticate(user);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated()) {
            user = getRepo().findByUserName(authentication.getName());
            String page = user.getRole().toString().toLowerCase();
            if(Role.RoleDefinition.DOMICILIO.toString().equals(user.getRole().getName())
            || Role.RoleDefinition.CENTRO.toString().equals(user.getRole().getName())) {
                user.getAssistances().add(new Assistance(location.getLatitude().toString(),
                                                        location.getLongitude().toString(),
                                                        location.getAccuracy(),
                                                        Assistance.Type.LOGIN,
                                                        LocalDateTime.now()));
                getRepo().save(user);
                page = "user";
            }

            VaadinSession.getCurrent().setAttribute("user", user.getUsername());
            //redirect to main application
            UI.getCurrent().getPage().setLocation("/" + page);
        }
    }

    private void authenticate(User user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        try {
            SecurityContextHolder.getContext().setAuthentication(getDaoAuthenticationProvider().authenticate(auth));
        } catch(AuthenticationException ex) {
            Notification.show("Acceso no permitido, compuebe sus credenciales.");
        }
    }
}
