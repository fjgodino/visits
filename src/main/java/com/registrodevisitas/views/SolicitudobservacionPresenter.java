package com.registrodevisitas.views;

import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.Alert;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.layout.layer.FloatingActionButton;

import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_DERMATOLOGOS_VIEW;
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_SOL_CAUSA;
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_SOL_CONFIRMACION;
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_SOL_MEDICOS;
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_SOL_OBSERVACION;
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_SOL_PROMOCION;
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_SOL_TURNO;
import com.registrodevisitas.services.VisitasServices;
import java.util.Optional;
import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javax.inject.Inject;

public class SolicitudobservacionPresenter {

    @FXML
    private View visitasSolicitudObservacion;

    @FXML
    private TextArea observacion;

    @Inject
    private VisitasServices visitaService;

    public void initialize() {

        visitasSolicitudObservacion.setShowTransitionFactory(BounceInRightTransition::new);

        visitasSolicitudObservacion.showingProperty().addListener((obs, oldValue, newValue) -> {

            if (newValue) {

                AppBar appBar = MobileApplication.getInstance().getAppBar();

                appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e
                        -> {
                            visitaService.getVisita().get().observacionProperty().unbindBidirectional(observacion.textProperty());

                            MobileApplication.getInstance().switchView(VISITAS_SOL_PROMOCION);
                        }
                ));

                appBar.setTitleText("Observacion");
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

                        visitaService.getVisita().get().observacionProperty().unbindBidirectional(observacion.textProperty());
 
                        MobileApplication.getInstance().switchView(VISITAS_DERMATOLOGOS_VIEW);
                    }

                }));
                
                if (visitaService.getVisita().get().getObservacion() != null &&
                        visitaService.getVisita().get().getObservacion().isEmpty()) {
                    observacion.clear();
                }

                Platform.runLater(() -> {                     
                    visitaService.getVisita().get().observacionProperty().bindBidirectional(observacion.textProperty());
                });
                
                

            }

        });

        /* ========================== */
        /* BOTON NEXT                 */
        /* ========================== */
        FloatingActionButton flButtonNext = new FloatingActionButton(MaterialDesignIcon.ARROW_FORWARD.text,
                e -> {

                    MobileApplication.getInstance().switchView(VISITAS_SOL_CONFIRMACION);
                });

        visitasSolicitudObservacion.getLayers().add(flButtonNext);

        observacion.setStyle(""
                + "-fx-font-size:16;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: blue;");

    }

}
