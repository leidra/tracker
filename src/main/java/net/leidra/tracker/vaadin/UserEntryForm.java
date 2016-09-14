package net.leidra.tracker.vaadin;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import net.leidra.tracker.backend.Role;
import net.leidra.tracker.backend.User;
import org.vaadin.viritin.fields.MPasswordField;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

public class UserEntryForm extends AbstractForm<User> {

    TextField username = new MTextField("Nombre");
    MPasswordField password = new MPasswordField("Clave");
    CheckBox enabled = new CheckBox("Habilitado");
    ComboBox role = new ComboBox("Rol");

    UserEntryForm(User user) {
        setSizeUndefined();
        setEntity(user);
        setNestedProperties("role.name");

        role.setNullSelectionAllowed(false);
        role.addItem(Role.RoleDefinition.CENTRO);
        role.addItem(Role.RoleDefinition.DOMICILIO);
        role.setValue(user.getRole().getRol());
    }

    @Override
    protected Component createContent() {
        return new MVerticalLayout(
                new MFormLayout(
                        username,
                        password,
                        enabled,
                        role
                ).withWidth(""),
                getToolbar()
        ).withWidth("");
    }

}