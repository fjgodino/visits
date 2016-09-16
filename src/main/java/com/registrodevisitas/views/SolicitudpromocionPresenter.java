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
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_SOL_OBSERVACION;
import com.registrodevisitas.model.Causa;
import com.registrodevisitas.model.Promocion;
import com.registrodevisitas.services.ConfiguradorGeneralService;
import com.registrodevisitas.services.PromocionesServices;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.layout.VBox;
import javax.inject.Inject;

public class SolicitudpromocionPresenter {

    @FXML
    private View visitasSolicitudPromocion;

    @FXML
    private VBox vbListaOpciones;

    @Inject
    private UserServices userService;

    @Inject
    private VisitasServices visitaService;

    @Inject
    private ConfiguradorGeneralService configuracion;

    @Inject
    private PromocionesServices promociones;

    private final ToggleGroupValue tgCGroup = new ToggleGroupValue();

    private final BooleanProperty causaSelected = new SimpleBooleanProperty(false);

    private ChangeListener clListenerLista = null;

    public void initialize() {

        visitasSolicitudPromocion.setShowTransitionFactory(BounceInRightTransition::new);

        visitasSolicitudPromocion.showingProperty().addListener((obs, oldValue, newValue) -> {

            if (newValue) {

                AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e
                        -> {
                            visitaService.getVisita().get().promocionProperty().unbindBidirectional(tgCGroup.valueProperty());

                            tgCGroup.selectedToggleProperty().removeListener(clListenerLista);
                            MobileApplication.getInstance().switchView(VISITAS_SOL_CAUSA);
                        }
                ));

                appBar.setTitleText("Promocion");

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

                        visitaService.getVisita().get().causaProperty().unbindBidirectional(tgCGroup.valueProperty());

                        tgCGroup.selectedToggleProperty().removeListener(clListenerLista);

                        MobileApplication.getInstance().switchView(VISITAS_DERMATOLOGOS_VIEW);
                    }

                }));

                if (visitaService.getVisita().get().getPromocion() != null
                        && visitaService.getVisita().get().getPromocion().getDescripcion().isEmpty()) {
                    tgCGroup.clean();
                }

                Platform.runLater(() -> {
                    
                    tgCGroup.selectedToggleProperty().addListener(clListenerLista);
                    
                    for(Toggle toggle: tgCGroup.getToggles())
                    {
                        if(((Promocion) toggle.getUserData()).getCod_promocion().compareTo(visitaService.getVisita().get().getPromocion().getCod_promocion()) == 0)
                        {
                            toggle.setSelected(true);
                        }
                    }
                    
                    visitaService.getVisita().get().promocionProperty().bindBidirectional(tgCGroup.valueProperty());
                });

            }

        });

        /* ===================================== */
        /* PROMOCIONES                           */
        /* ===================================== */
        for (Promocion promocion : promociones.obtenerPromociones()) {
            RadioButton rb = new RadioButton(promocion.getDescripcion());
            tgCGroup.add(rb, promocion);
            vbListaOpciones.getChildren().add(rb);

        }

        clListenerLista = new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
                causaSelected.set(true);

            }
        };

        /* ========================== */
        /* BOTON NEXT                 */
        /* ========================== */
        FloatingActionButton flButtonNext = new FloatingActionButton(MaterialDesignIcon.ARROW_FORWARD.text,
                e -> {

                    visitaService.getVisita().get().causaProperty().unbindBidirectional(tgCGroup.valueProperty());
                    tgCGroup.selectedToggleProperty().removeListener(clListenerLista);
                    MobileApplication.getInstance().switchView(VISITAS_SOL_OBSERVACION);
                });

        flButtonNext.visibleProperty().bind(causaSelected);

        visitasSolicitudPromocion.getLayers().add(flButtonNext);

    }

}
