package net.leidra.tracker.web.forms;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.*;
import net.leidra.tracker.backend.Role;
import net.leidra.tracker.backend.User;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.function.Consumer;

public class UserEntryForm extends CustomComponent {
    private TextField username = new TextField("Nombre");
    private PasswordField password = new PasswordField("Clave");
    private CheckBox enabled = new CheckBox("Habilitado");
    private ComboBox role = new ComboBox("Rol");

    private User user;

    private Consumer<User> saveHandler;
    private Consumer<User> resetHandler;

    public UserEntryForm(User user) {
        super();
        this.user = user;
        VerticalLayout container = new VerticalLayout();
        container.addStyleName("form-container");
        createContent(container);
        setCompositionRoot(container);
    }

    protected void createContent(VerticalLayout container) {
        final BeanFieldGroup<User> fieldGroup = new BeanFieldGroup<>(User.class);
        fieldGroup.setItemDataSource(user);
        fieldGroup.bindMemberFields(this);
        fieldGroup.setBuffered(true);

        createUserNameField(container);
        createPasswordField(container);
        createEnabledField(container);
        createRoleField(container);
        createButtons(container, fieldGroup);
    }

    private void createButtons(VerticalLayout container, BeanFieldGroup<User> fieldGroup) {
        Button save = new Button("Guardar");
        save.addClickListener(e -> {
            try {
                fieldGroup.commit();
                saveHandler.accept(fieldGroup.getItemDataSource().getBean());
            } catch (FieldGroup.CommitException e1) {
                Notification.show("No se pudo guardar el usuario", Notification.Type.ERROR_MESSAGE);
            }
        });

        Button reset = new Button("Resetear");
        save.addClickListener(e -> fieldGroup.discard());

        container.addComponent(new HorizontalLayout(save, reset));
    }

    private HorizontalLayout createUserNameField(VerticalLayout container) {
        HorizontalLayout usernameContainer = new HorizontalLayout(username);
        usernameContainer.addStyleName("username-container");
        container.addComponent(usernameContainer);
        username.setNullRepresentation(StringUtils.EMPTY);
        return usernameContainer;
    }

    private HorizontalLayout createPasswordField(VerticalLayout container) {
        HorizontalLayout passwordContainer = new HorizontalLayout(password);
        passwordContainer.addStyleName("password-container");
        container.addComponent(passwordContainer);
        password.setNullRepresentation(StringUtils.EMPTY);
        return passwordContainer;
    }

    private HorizontalLayout createEnabledField(VerticalLayout container) {
        HorizontalLayout enabledContainer = new HorizontalLayout(enabled);
        enabledContainer.addStyleName("enabled-container");
        container.addComponent(enabledContainer);
        return enabledContainer;
    }

    private void createRoleField(VerticalLayout container) {
        role.setContainerDataSource(new BeanItemContainer<>(Role.class, Arrays.asList(
                Role.create(Role.RoleDefinition.ADMIN),
                Role.create(Role.RoleDefinition.CENTRO),
                Role.create(Role.RoleDefinition.DOMICILIO))));
        role.setNullSelectionAllowed(false);
        role.setNewItemsAllowed(false);
        role.setTextInputAllowed(false);
        role.select(role.getContainerDataSource().getItemIds().iterator().next());

        role.setValue(Role.create(Role.RoleDefinition.CENTRO));
        HorizontalLayout roleContainer = new HorizontalLayout(role);
        roleContainer.addStyleName("role-container");
        container.addComponent(roleContainer);
    }

    public Consumer<User> getSaveHandler() {
        return saveHandler;
    }

    public void setSaveHandler(Consumer<User> saveHandler) {
        this.saveHandler = saveHandler;
    }

    public Consumer<User> getResetHandler() {
        return resetHandler;
    }

    public void setResetHandler(Consumer<User> resetHandler) {
        this.resetHandler = resetHandler;
    }

    public User getUser() {
        return user;
    }
}