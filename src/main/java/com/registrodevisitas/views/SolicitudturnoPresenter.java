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
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_SOL_FECHA;
import com.registrodevisitas.services.ConfiguradorGeneralService;
import com.registrodevisitas.services.UserServices;
import com.registrodevisitas.services.VisitasServices;
import com.registrodevisitas.tools.ToggleGroupValue;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javax.inject.Inject;

public class SolicitudturnoPresenter {

    @FXML
    private View visitasSolicitudTurno;

    @FXML
    private RadioButton rbTurnoManiana;

    @FXML
    private RadioButton rbTurnoTarde;

    @FXML
    private CheckBox cbAcompaniado;

    @FXML
    private RadioButton rbConsultorio;

    @FXML
    private RadioButton rbHostpital;

    @FXML
    private RadioButton rbInstituto;

    @Inject
    private UserServices userService;

    @Inject
    private VisitasServices visitaService;

    @Inject
    private ConfiguradorGeneralService configuracion;

    private final ToggleGroupValue tgTurnoGroup = new ToggleGroupValue();
    private final ToggleGroupValue tgLugarGroup = new ToggleGroupValue();

    private ChangeListener clListenerListaTurno = null;
    private ChangeListener clListenerListaLugares = null;

    private final BooleanProperty opcionesSelected = new SimpleBooleanProperty(false);

    public void initialize() {

        visitasSolicitudTurno.setShowTransitionFactory(BounceInRightTransition::new);

        visitasSolicitudTurno.showingProperty().addListener((obs, oldValue, newValue) -> {

            if (newValue) {

                AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e
                        -> {
                            MobileApplication.getInstance().switchView(VISITAS_SOL_FECHA);
                        }
                ));
                appBar.setTitleText("Turno:");
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

                Platform.runLater(() -> {

                    /* SET TURNO */
                    
                    if (visitaService.getVisita().get().getTurnoVisita() != null
                            && !visitaService.getVisita().get().getTurnoVisita().isEmpty()) {
                        rbTurnoManiana.setSelected(visitaService.getVisita().get().getTurnoVisita().compareTo("MAÑANA") == 0);
                        rbTurnoTarde.setSelected(visitaService.getVisita().get().getTurnoVisita().compareTo("TARDE") == 0);
                    } else {
                        rbTurnoManiana.setSelected(false);
                        rbTurnoTarde.setSelected(false);
                    }

                    /* ACOMPANIADO */
                    
                    cbAcompaniado.setSelected(visitaService.getVisita().get().getVisitaAcompanadaSN());
                    
                    /* SET LUGAR */
                    
                    if (visitaService.getVisita().get().getLugarVisita() != null) {

                        if (visitaService.getVisita().get().getLugarVisita().compareTo("HOSPITAL") == 0) {
                            rbHostpital.setSelected(true);
                            rbConsultorio.setSelected(false);
                            rbInstituto.setSelected(false);
                        } else if (visitaService.getVisita().get().getLugarVisita().compareTo("CONSULTORIO") == 0) {
                            rbHostpital.setSelected(false);
                            rbConsultorio.setSelected(true);
                            rbInstituto.setSelected(false);
                        } else if (visitaService.getVisita().get().getLugarVisita().compareTo("INSTITUTO") == 0) {
                            rbHostpital.setSelected(false);
                            rbConsultorio.setSelected(false);
                            rbInstituto.setSelected(true);
                        } else {
                            rbHostpital.setSelected(false);
                            rbConsultorio.setSelected(false);
                            rbInstituto.setSelected(false);
                        }

                    }
                });

            }

        });

        /* ========================== */
        /* TURNO                      */
        /* ========================== */
        tgTurnoGroup.add(rbTurnoManiana, "MAÑANA");
        tgTurnoGroup.add(rbTurnoTarde, "TARDE");

        clListenerListaTurno = new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
                opcionesSelected.set(tgLugarGroup.getSelectedToggle() != null && true);
            }
        };

        tgTurnoGroup.selectedToggleProperty().addListener(clListenerListaTurno);

        /* ========================== */
        /* LUGAR                      */
        /* ========================== */
        tgLugarGroup.add(rbHostpital, "HOSPITAL");
        tgLugarGroup.add(rbConsultorio, "CONSULTORIO");
        tgLugarGroup.add(rbInstituto, "INSTITUTO");

        clListenerListaLugares = new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
                opcionesSelected.set(tgTurnoGroup.getSelectedToggle() != null && true);
            }
        };

        tgLugarGroup.selectedToggleProperty().addListener(clListenerListaLugares);


        /* ========================== */
        /* BOTON NEXT                 */
        /* ========================== */
        FloatingActionButton flButtonNext = new FloatingActionButton(MaterialDesignIcon.ARROW_FORWARD.text,
                e -> {

                    visitaService.getVisita().get().setTurnoVisita((String) tgTurnoGroup.getValue());
                    visitaService.getVisita().get().setLugarVisita((String) tgLugarGroup.getValue());
                    visitaService.getVisita().get().setVisitaAcompanadaSN(cbAcompaniado.isSelected());
                    MobileApplication.getInstance().switchView(VISITAS_SOL_CAUSA);

                });

        flButtonNext.visibleProperty().bind(opcionesSelected);

        visitasSolicitudTurno.getLayers().add(flButtonNext);

    }

}
