package net.leidra.tracker.web;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.data.sort.Sort;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.converter.StringToBooleanConverter;
import com.vaadin.v7.data.util.filter.Between;
import com.vaadin.v7.data.util.filter.SimpleStringFilter;
import com.vaadin.v7.event.FieldEvents;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.TextField;
import net.leidra.tracker.backend.*;
import net.leidra.tracker.web.forms.UserEntryForm;
import net.leidra.tracker.web.utils.String2LocalDateTimeConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.dialogs.ConfirmDialog;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@SpringUI(path = "/admin")
public class AdminUI extends AbstractUI {
	private static final long serialVersionUID = 1L;

    @Autowired
    private UserRepository repo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User editingUser;

    private Grid list = new Grid(new BeanItemContainer<>(User.class));

    private Button addNew = new Button("Añadir", this::add);
    private Button edit = new Button("Editar", this::edit);
    private Button delete = new Button("Borrar", this::remove);
    private Button refresh = new Button("Actualizar", this::refresh);
    private Button location = new Button("Solicitar ubicación", this::location);

    @Override
    protected void init(VaadinRequest request) {
        CssLayout toolbar = createToolbar();
        CssLayout headerContainer = createHeaderContainer();
        CssLayout listContainer = createListContainer();

        CssLayout mainContainer = createMainContainer(headerContainer, toolbar, listContainer);

        setContent(mainContainer);

        listEntities();
    }

    @Override
    protected CssLayout createToolbarButtons() {
        CssLayout innerButtons = new CssLayout(addNew, edit, delete, refresh, location);
        innerButtons.addStyleName("action-buttons");
        return innerButtons;
    }

    private CssLayout createListContainer() {
        createUsersGrid();

        CssLayout listContainer = new CssLayout();
        listContainer.addStyleName("list-container");
        listContainer.addComponent(list);
        return listContainer;
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
            if(!itemId.getAssistances().isEmpty() && !Role.RoleDefinition.ADMIN.equals(itemId.getRole().getRol())) {
                list.setDetailsVisible(itemId, !list.isDetailsVisible(itemId));
            }
        }
    }

    private Grid createAssitancesGrid(User user) {
        Set<Assistance> assistanceSet = user.getAssistances().stream().collect(Collectors.toSet());

        Grid assistances = new Grid(new BeanItemContainer<>(Assistance.class, assistanceSet));
        assistances.setColumns("patientName", "type", "start", "end", "duration");
        assistances.setSortOrder(Sort.by("start", SortDirection.DESCENDING).build());
        assistances.getColumn("patientName").setHeaderCaption("Paciente");
        assistances.getColumn("type").setHeaderCaption("Tipo").setConverter(retrieveAssistanceTypeConvert());
        assistances.setWidth("100%");
        assistances.getColumn("start").setHeaderCaption("Entrada").setConverter(new String2LocalDateTimeConverter());
        assistances.getColumn("end").setHeaderCaption("Salida").setConverter(new String2LocalDateTimeConverter());
        assistances.getColumn("duration").setHeaderCaption("Duración");
        assistances.setHeightByRows(assistances.getContainerDataSource().size() > 0 ? assistances.getContainerDataSource().size() : 5);

        createAssistancesFilter(assistances);
        assistances.addItemClickListener(itemClickEvent -> assistanceClick(itemClickEvent));

        return assistances;
    }

    private void createAssistancesFilter(Grid assistances) {
        Grid.HeaderRow headerRow = assistances.getDefaultHeaderRow();
        headerRow.setStyleName("assistances-header");

        headerRow.getCell("start").setComponent(createDateFieldFilter(assistances, "start"));
        headerRow.getCell("end").setComponent(createDateFieldFilter(assistances, "end"));
    }

    private DateField createDateFieldFilter(Grid assistances, String fieldKey) {
        DateField dateField = new DateField();
        dateField.addStyleName("column-filter");
        dateField.setImmediate(true);
        dateField.addValueChangeListener(valueChangeEvent -> filterDateTime(assistances, valueChangeEvent.getProperty().getValue(), fieldKey));
        return dateField;
    }

    private Converter<String, Assistance.Type> retrieveAssistanceTypeConvert() {
        return new Converter<String, Assistance.Type>() {
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
        };
    }

    private void filterDateTime(Grid assistances, Object value, String fieldKey) {
        BeanItemContainer<User> container = ((BeanItemContainer<User>) assistances.getContainerDataSource());
        container.removeContainerFilters(fieldKey);
        if (null != value) {
            container.addContainerFilter(new Between(fieldKey, LocalDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault()), LocalDateTime.now()));
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
        return event -> {
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
        };
    }

    protected void adjustActionButtonState() {
        boolean hasSelection = list.getSelectedRow() != null;
        edit.setEnabled(hasSelection);
        delete.setEnabled(hasSelection);
        location.setEnabled(hasSelection && isDomicilio());
    }

    private Boolean isDomicilio() {
        return Role.RoleDefinition.DOMICILIO.equals(((User)list.getSelectedRow()).getRole().getRol());
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