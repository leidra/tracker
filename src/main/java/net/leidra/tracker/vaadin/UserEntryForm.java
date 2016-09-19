package net.leidra.tracker.vaadin;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import net.leidra.tracker.backend.Role;
import net.leidra.tracker.backend.User;
import org.vaadin.viritin.fields.MPasswordField;
import org.vaadin.viritin.fields.MTextField;

import java.util.Arrays;
import java.util.function.Consumer;

public class UserEntryForm extends CustomComponent {
    private TextField username = new MTextField("Nombre");
    private MPasswordField password = new MPasswordField("Clave");
    private CheckBox enabled = new CheckBox("Habilitado");
    private ComboBox role = new ComboBox("Rol");

    private User user;

    private Consumer<User> saveHandler;
    private Consumer<User> resetHandler;

    UserEntryForm(User user) {
        super();
        this.user = user;
        VerticalLayout container = new VerticalLayout();
        container.addStyleName("form-container");
        createContent(container);
        setCompositionRoot(container);
    }

    protected void createContent(VerticalLayout container) {
        role.setContainerDataSource(new BeanItemContainer<>(Role.class, Arrays.asList(
                Role.create(Role.RoleDefinition.ADMIN),
                Role.create(Role.RoleDefinition.CENTRO),
                Role.create(Role.RoleDefinition.DOMICILIO))));
        role.setNullSelectionAllowed(false);
        role.setNewItemsAllowed(false);
        role.setTextInputAllowed(false);

        final BeanFieldGroup<User> fieldGroup = new BeanFieldGroup<>(User.class);
        fieldGroup.setItemDataSource(user);
        fieldGroup.bindMemberFields(this);
        fieldGroup.setBuffered(true);

        HorizontalLayout usernameContainer = new HorizontalLayout(username);
        usernameContainer.addStyleName("username-container");
        HorizontalLayout passwordContainer = new HorizontalLayout(password);
        passwordContainer.addStyleName("password-container");
        HorizontalLayout enabledContainer = new HorizontalLayout(enabled);
        enabledContainer.addStyleName("enabled-container");
        HorizontalLayout roleContainer = new HorizontalLayout(role);
        roleContainer.addStyleName("role-container");

        container.addComponent(usernameContainer);
        container.addComponent(passwordContainer);
        container.addComponent(enabledContainer);
        container.addComponent(roleContainer);

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