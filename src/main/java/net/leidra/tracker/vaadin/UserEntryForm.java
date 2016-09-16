package net.leidra.tracker.vaadin;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.StringToEnumConverter;
import com.vaadin.ui.*;
import net.leidra.tracker.backend.Role;
import net.leidra.tracker.backend.User;
import org.vaadin.viritin.fields.MPasswordField;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.Arrays;
import java.util.EnumSet;

public class UserEntryForm extends AbstractForm<User> {

    TextField username = new MTextField("Nombre");
    MPasswordField password = new MPasswordField("Clave");
    CheckBox enabled = new CheckBox("Habilitado");
    ComboBox role = new ComboBox("Rol");

    UserEntryForm(User user) {
        super();
        setSizeUndefined();
        setEntity(user);
        setNestedProperties("role.name");

        role.setContainerDataSource(new BeanItemContainer<>(Role.class, Arrays.asList(
                Role.create(Role.RoleDefinition.CENTRO),
                Role.create(Role.RoleDefinition.DOMICILIO))));
        role.setNullSelectionAllowed(false);
        role.setItemCaptionPropertyId("name");
        role.setTextInputAllowed(false);
        role.setInvalidAllowed(false);
        role.setNewItemsAllowed(false);
        role.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        role.setValue((getEntity().getRole() != null) ? getEntity().getRole() : role.getContainerDataSource().getItemIds().iterator().next());
        role.select((getEntity().getRole() != null) ? getEntity().getRole() : role.getContainerDataSource().getItemIds().iterator().next());
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