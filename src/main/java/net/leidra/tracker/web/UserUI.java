package net.leidra.tracker.web;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.v7.ui.TextField;
import net.leidra.tracker.backend.Broadcaster;
import net.leidra.tracker.backend.User;
import net.leidra.tracker.web.geolocation.Location;
import org.springframework.beans.factory.annotation.Autowired;

@SpringUI(path = "/user")
@Push(transport = Transport.LONG_POLLING)
public class UserUI extends AbstractUI implements Broadcaster.BroadcastListener {
	private static final long serialVersionUID = 1L;
    public final static String DOMICILIO_DESCRIPTION = "Está atendiendo al paciente {0}.<br/>";
    public final static String START_DESCRIPTION = "Inicio a las {0} ";

    private Label assistanceDescription = new Label();
    private TextField patientName = new TextField();
    private Button submitButton = new Button();
    private Location locationManager;
    private boolean askingForLocation = false;

    private UserPresenter userPresenter;
    private User user;

    public boolean isAskingForLocation() {
        return askingForLocation;
    }

    public void setAskingForLocation(boolean askingForLocation) {
        this.askingForLocation = askingForLocation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserPresenter getUserPresenter() {
        return userPresenter;
    }

    @Autowired
    public void setUserPresenter(UserPresenter userPresenter) {
        this.userPresenter = userPresenter;
    }

    public Label getAssistanceDescription() {
        return assistanceDescription;
    }

    public TextField getPatientName() {
        return patientName;
    }

    public Button getSubmitButton() {
        return submitButton;
    }

    @Override
    protected void init(VaadinRequest request) {
        Broadcaster.register(this);
        this.user = getUserPresenter().findByUserName().orElseThrow(() -> new RuntimeException());

        patientName.setCaption("Paciente");
        assistanceDescription.setContentMode(ContentMode.HTML);
        assistanceDescription.setVisible(false);
        locationManager = getUserPresenter().createLocator(this);
        submitButton.addClickListener(e -> {
            locationManager.requestLocation();
        });

        getUserPresenter().adjustFormState(this);
        CssLayout headerContainer = createHeaderContainer();
        CssLayout toolbar = createToolbar();
        Layout formContainer = createFormContainer();
        CssLayout mainContainer = createMainContainer(headerContainer, toolbar, formContainer);

        setContent(mainContainer);
    }

    private Layout createFormContainer() {
        VerticalLayout formContainer = new VerticalLayout();
        formContainer.addStyleName("form-container");

        createFormRow(formContainer, assistanceDescription);
        createFormRow(formContainer, patientName);
        createFormRow(formContainer, submitButton);

        return formContainer;
    }

    private void createFormRow(VerticalLayout formContainer, Component component) {
        HorizontalLayout formRow = new HorizontalLayout(component);
        formRow.addStyleName("row");
        formRow.setVisible(component.isVisible());
        formRow.setComponentAlignment(component, Alignment.MIDDLE_CENTER);

        formContainer.addComponent(formRow);
        formContainer.setComponentAlignment(formRow, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void detach() {
        Broadcaster.unregister(this);
        super.detach();
    }

    @Override
    public void receiveBroadcast() {
        access(() -> {
            askingForLocation = true;
            locationManager.requestLocation();
        });
    }

    public String getUserName() {
        return user.getUsername();
    }
}