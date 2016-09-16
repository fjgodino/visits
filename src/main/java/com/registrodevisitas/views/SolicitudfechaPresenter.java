package com.registrodevisitas.views;

import com.gluonhq.charm.glisten.animation.BounceInLeftTransition;
import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.Alert;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.DatePicker;
import com.gluonhq.charm.glisten.layout.layer.FloatingActionButton;
 
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_DERMATOLOGOS_VIEW;
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_SOL_MEDICOS;
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_SOL_TURNO;
import com.registrodevisitas.services.ConfiguradorGeneralService;
import com.registrodevisitas.services.UserServices;
import com.registrodevisitas.services.VisitasServices;
import java.time.LocalDate;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
 
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javax.inject.Inject;

public class SolicitudfechaPresenter {

    @FXML
    private View visitasSolicitudFecha;
    
    @FXML
    private Hyperlink linkFecha ;

    @Inject 
    private UserServices userService;
    
    @Inject 
    private VisitasServices visitaService;
    
    @Inject
    private ConfiguradorGeneralService configuracion;
    
    private final BooleanProperty fechaValida = new SimpleBooleanProperty(false);
    
    
    public void initialize() {
        
                
        visitasSolicitudFecha.setShowTransitionFactory(BounceInRightTransition::new);
        
        visitasSolicitudFecha.showingProperty().addListener((obs, oldValue, newValue) -> {
            
            if (newValue) {
                
                AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> {
                        
                        MobileApplication.getInstance().switchView(VISITAS_SOL_MEDICOS);}));
                appBar.setTitleText("Fecha de la Visita");
              
                linkFecha.setText(visitaService.getVisita().get().obtenerFechaFormateada());
                
                appBar.getActionItems().add(MaterialDesignIcon.CANCEL.button(e -> {

                    Alert alert = new Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                    alert.setTitleText("Confirme la cancelación");
                    alert.setContentText("");

                    Button bYes = new Button("Si");
                    bYes.setOnAction(ev -> {
                        alert.setResult(ButtonType.YES);
                        alert.hide();
                    });
                    
                    bYes.setDefaultButton(true);

                    Button bNo = new Button("No");
                    bNo.setCancelButton(true);
                    bNo.setOnAction(ev -> {
                        alert.setResult(ButtonType.NO);
                        alert.hide();
                    });
                    alert.getButtons().setAll(bYes, bNo);

                    Optional result = alert.showAndWait();

                    if (result.isPresent() && result.get().equals(ButtonType.YES)) {
 
                        MobileApplication.getInstance().switchView(VISITAS_DERMATOLOGOS_VIEW);
                    }

                }));
                
            }
            
        }); 

        linkFecha.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String eventLatLong = "";
                Object source = event.getSource();
                if (source instanceof Hyperlink) {

                    Platform.runLater(() -> {

                        DatePicker datePicker = new DatePicker(visitaService.getVisita().get().getFechaDeLaVisita());

                        datePicker.setAutoHide(false);

                        datePicker.setTitleText("Fecha de la Visita");

                        Optional<LocalDate> dateReceived = datePicker.showAndWait();

                        if (dateReceived.isPresent()) {

                            visitaService.getVisita().get().setFechaDeLaVisita(dateReceived.get());
                            linkFecha.setText(visitaService.getVisita().get().obtenerFechaFormateada());
                            
                            LocalDate fechaSeleccionada = dateReceived.get();
                            LocalDate fechaHoy = LocalDate.now();
                            
                            
                            if (fechaSeleccionada.compareTo(fechaHoy)>0) {
                                
                                fechaValida.set(false);
                                linkFecha.setStyle(""
                                        + "-fx-font-size:21;"
                                        + "-fx-font-weight: bold;"
                                        + "-fx-text-fill: red;");
                            } else {
                                fechaValida.set(true);
                                linkFecha.setStyle(""
                                        + "-fx-font-size:21;"
                                        + "-fx-font-weight: bold;"
                                        + "-fx-text-fill: blue;");
                            }

                        }
                    });

                }

            }
        });
        
        FloatingActionButton flButtonNext = new FloatingActionButton(MaterialDesignIcon.ARROW_FORWARD.text,
            e -> {   
                    
                    MobileApplication.getInstance().switchView(VISITAS_SOL_TURNO);
                });
        
       
        flButtonNext.visibleProperty().bind(fechaValida);
        
        visitasSolicitudFecha.getLayers().add(flButtonNext);

        fechaValida.set(true);
        
        linkFecha.setStyle("" 
                                            + "-fx-font-size:21;"
                                            + "-fx-font-weight: bold;"
                                            + "-fx-text-fill: blue;");
        
    }
    
}
