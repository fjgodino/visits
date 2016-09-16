package com.registrodevisitas.views;

import com.gluonhq.charm.down.common.JavaFXPlatform;
import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.Alert;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.layout.layer.FloatingActionButton;

import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_DERMATOLOGOS_VIEW;
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_SOL_PROMOCION;
import com.registrodevisitas.services.ListaVisitasServices;
import com.registrodevisitas.services.VisitasServices;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javax.inject.Inject;

public class SolicitudconfirmacionPresenter {

    @FXML
    private View visitasSolicitudConfirmacion;

    @FXML
    private Label medico;

    @FXML
    private Label fecha;

    @FXML
    private Label visita;

    @FXML
    private Label lugar;

    @FXML
    private Label causa;

    @FXML
    private Label promocion;

    @FXML
    private TextArea observacion;

    @Inject
    private VisitasServices visitaService;

    private FloatingActionButton flButtonNext;

    public void initialize() {

        visitasSolicitudConfirmacion.setShowTransitionFactory(BounceInRightTransition::new);

        visitasSolicitudConfirmacion.showingProperty().addListener((obs, oldValue, newValue) -> {

            if (newValue) {

                AppBar appBar = MobileApplication.getInstance().getAppBar();

                appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e
                        -> {

                            MobileApplication.getInstance().switchView(VISITAS_SOL_PROMOCION);
                        }
                ));

                appBar.setTitleText("Confirmación");
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

                /* ========================== */
                /* Valores                     */
                /* ========================== */
                medico.setText(visitaService.getVisita().get().getMedico().getNombreMedico());
                fecha.setText(visitaService.getVisita().get().obtenerFechaFormateada());
                visita.setText(visitaService.getVisita().get().getTurnoVisita() + (visitaService.getVisita().get().getVisitaAcompanadaSN() ? " / ACOMPAÑADO" : ""));
                lugar.setText(visitaService.getVisita().get().getLugarVisita());
                causa.setText(visitaService.getVisita().get().getCausa().getDescripcion());
                promocion.setText(visitaService.getVisita().get().getPromocion().getDescripcion());
                observacion.setText(visitaService.getVisita().get().getObservacion());

                String icon = "";

                if (visitaService.getMode() == 1) {
                    icon = MaterialDesignIcon.SAVE.text;
                }

                if (visitaService.getMode() == 2) {
                    icon = MaterialDesignIcon.SAVE.text;
                }

                if (visitaService.getMode() == 3) {
                    icon = MaterialDesignIcon.DELETE.text;
                }

                flButtonNext.setText(icon);

            }

        });

        /* ========================== */
        /* BOTON CONFIRMACION         */
        /* ========================== */
        String icon = "";

        flButtonNext = new FloatingActionButton(icon,
                e -> {

                    if (JavaFXPlatform.isAndroid()) {
                        // Get Position
                    }

                    if (visitaService.getMode() == 1) {
                        visitaService.persistirVisita();
                    }

                    if (visitaService.getMode() == 2) {
                        visitaService.actualizarVisita();
                    }

                    if (visitaService.getMode() == 3) {
                        visitaService.eliminarVisita();
                    }

                    MobileApplication.getInstance().switchView(VISITAS_DERMATOLOGOS_VIEW);
                });

        visitasSolicitudConfirmacion.getLayers().add(flButtonNext);

        medico.setStyle(""
                + "-fx-font-size:16;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: blue;");

        fecha.setStyle(""
                + "-fx-font-size:16;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: blue;");

        visita.setStyle(""
                + "-fx-font-size:16;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: blue;");

        lugar.setStyle(""
                + "-fx-font-size:16;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: blue;");

        causa.setStyle(""
                + "-fx-font-size:16;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: blue;");

        promocion.setStyle(""
                + "-fx-font-size:16;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: blue;");

        observacion.setStyle(""
                + "-fx-font-size:16;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: blue;");

    }

}
