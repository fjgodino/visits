package com.registrodevisitas.views;

import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.Alert;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.layout.layer.FloatingActionButton;

import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_DERMATOLOGOS_VIEW;
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_SOL_FECHA;
import com.registrodevisitas.model.Medico;
import com.registrodevisitas.model.Visita;
import com.registrodevisitas.services.ConfiguradorGeneralService;
import com.registrodevisitas.services.MedicosServices;
import com.registrodevisitas.services.UserServices;
import com.registrodevisitas.services.VisitasServices;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javax.inject.Inject;

public class SolicitudmedicoPresenter {

    private final Logger logger = Logger.getLogger(SolicitudmedicoPresenter.class.getName());

    @FXML
    private View visitasSolicitudMedicos;

    @FXML
    private ListView listaDeMedicos;

    @FXML
    private TextField filterMedicos;

    @Inject
    private UserServices userService;

    @Inject
    private VisitasServices visitaService;

    @Inject
    private ConfiguradorGeneralService configuracion;

    @Inject
    private MedicosServices medicosService;

    private final BooleanProperty medicoSelected = new SimpleBooleanProperty(false);
    
    FilteredList<Medico>  filteredData;

    public void initialize() {

        visitasSolicitudMedicos.setShowTransitionFactory(BounceInRightTransition::new);

        visitasSolicitudMedicos.showingProperty().addListener((obs, oldValue, newValue) -> {

            if (newValue) {

                AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e
                        -> {
                            MobileApplication.getInstance().switchView(VISITAS_DERMATOLOGOS_VIEW);
                        }));
                appBar.setTitleText("Selección de Médicos");
                appBar.getActionItems().add(MaterialDesignIcon.CANCEL.button(e -> {

                    Alert alert = new Alert(AlertType.CONFIRMATION);
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

                Platform.runLater(() -> {
              
                    Visita visita = visitaService.getVisita().get();
                    if (visita.getMedico() != null) {
                        filterMedicos.setText(visitaService.getVisita().get().getMedico().getNombreMedico());
                    } else {
                        filterMedicos.setText("");
                    }
                    
                    logger.info("DEMO" +filteredData.isEmpty() );

                });

            }

        }
        );
        
        /* =========================================================================== */
        /* 1. Wrap the ObservableList in a FilteredList (initially display all data)   */
        /* =========================================================================== */
        
        logger.log(Level.INFO, "Medicos: {0}", medicosService.obtenerMedicos().size());
 
        filteredData = new FilteredList<>(medicosService.obtenerMedicos(), s -> true);
          
        /* =========================================================================== */
        /* 2. Set the filter Predicate whenever the filter changes.                   */
        /* =========================================================================== */
        
        filterMedicos.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredData.setPredicate(medico -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (medico.getNombreMedico().toLowerCase().contains(lowerCaseFilter)) {

                    return true;
                }

                return false; // Does not match.
            });

            if (filteredData.size() == 1) {
                filterMedicos.setStyle(""
                        + "-fx-font-size:21;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: blue;");

                Platform.runLater(() -> {
                    // listaDeMedicos.setVisible(false);
                    medicoSelected.set(true);
                    if (oldValue.length() < newValue.length()) {
                        filterMedicos.setText(filteredData.get(0).getNombreMedico());
                        filterMedicos.positionCaret(filteredData.get(0).getNombreMedico().length());
                    }
                });

               

            } else if (filteredData.size() > 1) {
                filterMedicos.setStyle(""
                        + "-fx-font-size:21;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: green;");

                if (!listaDeMedicos.isVisible()) {
                    Platform.runLater(() -> {
                        listaDeMedicos.setVisible(true);
                        medicoSelected.set(false);
                    });
                }

                

            } else {
                filterMedicos.setStyle(""
                        + "-fx-font-size:21;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: red;");

                medicoSelected.set(false);
            }

        });

        /* =========================================================================== */
        /* Wrap the FilteredList in a SortedList.                                      */
        /* =========================================================================== */
         
        SortedList<Medico> sortedData = new SortedList<>(filteredData);
         
        listaDeMedicos.setItems(sortedData);
        

        // Handle ListView selection changes.
        listaDeMedicos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue != null) {

                Platform.runLater(() -> {
                    filterMedicos.setText(((Medico) newValue).getNombreMedico());
                });

            }

        });

        FloatingActionButton flButtonNext = new FloatingActionButton(MaterialDesignIcon.ARROW_FORWARD.text,
                e -> {
                    
                    Medico medicoSeleccionado = null;
                    
                    try {
                        medicoSeleccionado = (Medico) ((Medico) filteredData.get(0)).clone();
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(SolicitudmedicoPresenter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   
                    visitaService.getVisita().get().setMedico(medicoSeleccionado);
                    MobileApplication.getInstance().switchView(VISITAS_SOL_FECHA);
                });

        flButtonNext.visibleProperty().bind(medicoSelected);

        visitasSolicitudMedicos.getLayers().add(flButtonNext);

        filterMedicos.setStyle(""
                + "-fx-font-size:21;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: blue;");

    }

}
