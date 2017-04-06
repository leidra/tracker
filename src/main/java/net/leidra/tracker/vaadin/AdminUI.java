package net.leidra.tracker.vaadin;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.ui.*;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.sort.Sort;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.converter.StringToBooleanConverter;
import com.vaadin.v7.data.util.filter.Between;
import com.vaadin.v7.data.util.filter.SimpleStringFilter;
import com.vaadin.v7.event.FieldEvents;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.server.*;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.TextField;
import net.leidra.tracker.backend.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.dialogs.ConfirmDialog;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Title("Gestión de asistencias")
@Theme("tracker")
@SpringUI(path = "/admin")
@Push(value = PushMode.MANUAL, transport = Transport.LONG_POLLING)
public class AdminUI extends UI {
	private static final long serialVersionUID = 1L;

    @Autowired
    private UserRepository repo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User editingUser;

    private Grid list = new Grid(new BeanItemContainer<User>(User.class));

    private Button addNew = new Button("Añadir", this::add);
    private Button edit = new Button("Editar", this::edit);
    private Button delete = new Button("Borrar", this::remove);
    private Button refresh = new Button("Actualizar", this::refresh);
    private Button location = new Button("Solicitar ubicación", this::location);

    @Override
    protected void init(VaadinRequest request) {
        createUsersGrid();
        Button logoutButton = new Button("Salir");
        logoutButton.addStyleName("logout-button");
        logoutButton.addClickListener(e -> {
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);

            getPage().setLocation("/login");
        });

        CssLayout innerButtons = new CssLayout(addNew, edit, delete, refresh, location);
        innerButtons.addStyleName("action-buttons");
        CssLayout toolbar = new CssLayout(innerButtons, logoutButton);
        toolbar.addStyleName("toolbar");

        CssLayout headerContainer = new CssLayout();
        Label title = new Label("<h1>Gestión de asistencias</h1>", ContentMode.HTML);
        title.addStyleName("title");
        headerContainer.addComponent(title);
        headerContainer.addStyleName("header");
        CssLayout listContainer = new CssLayout();
        listContainer.addStyleName("list-container");
        listContainer.addComponent(list);
        CssLayout layout = new CssLayout(headerContainer, toolbar, listContainer);
        layout.addStyleName("main-container");
        listEntities();

        setContent(layout);
    }

    private void createUsersGrid() {
        list.setColumns("username", "role", "enabled");

        createUsernameFilter();

        list.setDetailsGenerator(rowReference -> {
            User user = (User)rowReference.getItemId();
            return createAssitancesGrid(user);
        });
        list.addItemClickListener((ItemClickEvent.ItemClickListener) event -> userClick(event));
        list.setColumnOrder("username", "role", "enabled");
        list.getColumn("role").setHeaderCaption("Rol");
        list.getColumn("enabled").setHeaderCaption("Habilitado").setConverter(new StringToBooleanConverter("Sí", "No"));
        list.setSizeFull();
        list.setSelectionMode(Grid.SelectionMode.SINGLE);
        list.addSelectionListener(e -> adjustActionButtonState());
        list.setHeightMode(HeightMode.ROW);
    }

    private void createUsernameFilter() {
        Grid.HeaderRow filterRow = list .getDefaultHeaderRow();
        TextField textField = new TextField();
        textField.addStyleName("column-filter");
        textField.setImmediate(true);

        textField.addTextChangeListener(getUserFilterListener());
        textField.setValue("Usuario");
        textField.addFocusListener(e -> textField.clear());
        filterRow.getCell("username").setComponent(textField);
    }

    private void userClick(ItemClickEvent event) {
        if (event.isDoubleClick()) {
            User itemId = (User)event.getItemId();
            if(!itemId.getAssistances().isEmpty()
            && !Role.RoleDefinition.ADMIN.equals(itemId.getRole().getRol())) {
                list.setDetailsVisible(itemId, !list.isDetailsVisible(itemId));
            }
        }
    }

    private Grid createAssitancesGrid(User user) {
        Set<Assistance> assistanceSet = user.getAssistances().stream()
                .filter(a -> null != a.getTime())
                .sorted((o1, o2) -> o1.getTime().compareTo(o2.getTime()))
                .collect(Collectors.toSet());
        Grid assistances = new Grid(new BeanItemContainer<Assistance>(Assistance.class, assistanceSet));
        assistances.setColumns("patientName", "type", "time");
        assistances.setSortOrder(Sort.by("time", SortDirection.DESCENDING).build());
        assistances.getColumn("patientName").setHeaderCaption("Paciente");
        assistances.getColumn("type").setHeaderCaption("Tipo").setConverter(new Converter<String, Assistance.Type>() {
            @Override
            public Assistance.Type convertToModel(String s, Class<? extends Assistance.Type> aClass, Locale locale) throws ConversionException {
                return Assistance.Type.valueOf(s);
            }

            @Override
            public String convertToPresentation(Assistance.Type type, Class<? extends String> aClass, Locale locale) throws ConversionException {
                return type.toString();
            }

            @Override
            public Class<Assistance.Type> getModelType() {
                return Assistance.Type.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }
        });
        assistances.getColumn("time").setHeaderCaption("Hora");
        assistances.setWidth("100%");
        assistances.getColumn("time").setConverter(new String2LocalDateTimeConverter());
        assistances.setHeightByRows(assistances.getContainerDataSource().size() > 0 ? assistances.getContainerDataSource().size() : 5);

        Grid.HeaderRow headerRow = assistances.getDefaultHeaderRow();
        headerRow.setStyleName("assistances-header");
        DateField dateField = new DateField();
        dateField.addStyleName("column-filter");
        dateField.setImmediate(true);
        dateField.addValueChangeListener(valueChangeEvent -> assistanceValueChange(assistances, valueChangeEvent));
        headerRow.getCell("time").setComponent(dateField);
        assistances.addItemClickListener(itemClickEvent -> assistanceClick(itemClickEvent));
        return assistances;
    }

    private void assistanceValueChange(Grid assistances, Property.ValueChangeEvent valueChangeEvent) {
        BeanItemContainer<User> container = ((BeanItemContainer<User>) assistances.getContainerDataSource());
        container.removeContainerFilters("time");
        if (null != valueChangeEvent.getProperty().getValue()) {
            container.addContainerFilter(new Between("time", LocalDateTime.ofInstant(((Date)valueChangeEvent.getProperty().getValue()).toInstant(), ZoneId.systemDefault()), LocalDateTime.now()));
        }
        assistances.recalculateColumnWidths();
    }

    private void assistanceClick(ItemClickEvent itemClickEvent) {
        if(itemClickEvent.isDoubleClick()) {
            Assistance itemId = (Assistance) itemClickEvent.getItemId();
            Page.getCurrent().open("https://www.google.com/maps/@" + itemId.getLatitude() + "," + itemId.getLongitude() + ",25z", "_blank");
        }
    }

    private FieldEvents.TextChangeListener getUserFilterListener() {
        return new FieldEvents.TextChangeListener() {
            /**
             *
             */
            private static final long serialVersionUID = -2368474286053602744L;

            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                String newValue = (String) event.getText();
                @SuppressWarnings("unchecked")
                BeanItemContainer<User> container = ((BeanItemContainer<User>) list.getContainerDataSource());
                // This is important, this removes the previous filter
                // that was used to filter the container
                container.removeContainerFilters("username");
                if (null != newValue && !newValue.isEmpty()) {
                    container.addContainerFilter(new SimpleStringFilter("username", newValue, true, false));
                }
                list.recalculateColumnWidths();
            }
        };
    }

    protected void adjustActionButtonState() {
        boolean hasSelection = list.getSelectedRow() != null;
        edit.setEnabled(hasSelection);
        delete.setEnabled(hasSelection);
        location.setEnabled(hasSelection);
    }

    private void listEntities() {
        list.getContainerDataSource().removeAllItems();
        repo.findAll().stream().forEach(list.getContainerDataSource()::addItem);
        adjustActionButtonState();
        list.clearSortOrder();
    }

    public void add(ClickEvent clickEvent) {
        edit(new User());
    }

    public void refresh(ClickEvent clickEvent) {
        listEntities();
        Notification.show("Lista de usuarios actualizada");
    }

    public void edit(ClickEvent e) {
        edit((User)list.getSelectedRow());
    }

    public void remove(ClickEvent e) {
        ConfirmDialog.show(getUI(), "Borrar usuario", "¿Está seguro de borrar este usuario?",
                "Sí", "No", ()-> {
            repo.delete((User)list.getSelectedRow());
            list.select(null);
            listEntities();
        });
    }

    protected void edit(final User user) {
        if(UI.getCurrent().getWindows().isEmpty()) {
            editingUser = new User();
            BeanUtils.copyProperties(user, editingUser);
            UserEntryForm userForm = new UserEntryForm(user);
            userForm.setSaveHandler(this::saveEntry);
            userForm.setResetHandler(this::resetEntry);

            Window popup = new Window("", userForm);
            popup.setModal(true);
            UI.getCurrent().addWindow(popup);
        }
    }

    public void saveEntry(User user) {
        User entry = user;
        if(entry.getId() == null && repo.findByUserName(entry.getUsername()) != null) {
            Notification.show("El usuario ya existe. Introduzca un nombre de usuario distinto", Notification.Type.ERROR_MESSAGE);
        } else {
            if(entry.getId() == null
            || (editingUser != null
                && !editingUser.getPassword().equals(entry.getPassword()))) {
                entry.setPassword(passwordEncoder.encode(entry.getPassword()));
                editingUser = null;
            }
            repo.save(entry);
            listEntities();
            closeWindow();
        }
    }

    public void resetEntry(User entry) {
        closeWindow();
    }

    protected void closeWindow() {
        getWindows().stream().forEach(w -> {
            User user = ((UserEntryForm) w.getContent()).getUser();
            if(list.getContainerDataSource().containsId(user)) {
                list.select(user);
            }
            removeWindow(w);
        });
        editingUser = null;
    }

    private void location(ClickEvent clickEvent) {
        User user = (User)list.getSelectedRow();
        if(user != null && Role.RoleDefinition.DOMICILIO.equals(user.getRole().getRol())) {
            access(() -> {
                Broadcaster.broadcast(user);
                push();
                Notification.show("Ubicación solicitada");
            });
        }
    }
}

class String2LocalDateTimeConverter implements Converter<String, LocalDateTime> {
    @Override
    public LocalDateTime convertToModel(String s, Class<? extends LocalDateTime> aClass, Locale locale) throws ConversionException {
        return LocalDateTime.parse(s);
    }

    @Override
    public String convertToPresentation(LocalDateTime LocalDateTime, Class<? extends String> aClass, Locale locale) throws ConversionException {
        return LocalDateTime.format(DateTimeFormatter.ofPattern("dd/MM/YYYY hh:mm:ss")).toString();
    }

    @Override
    public Class<LocalDateTime> getModelType() {
        return LocalDateTime.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}