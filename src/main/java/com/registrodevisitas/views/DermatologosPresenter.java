package com.registrodevisitas.views;

import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.CharmListView;
import com.gluonhq.charm.glisten.layout.layer.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.registrodevisitas.RegistroDeVisitas;
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_SOL_CONFIRMACION;
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_SOL_MEDICOS;
import com.registrodevisitas.model.Visita;
import com.registrodevisitas.services.ListaVisitasServices;
import com.registrodevisitas.services.UserServices;
import com.registrodevisitas.services.VisitasServices;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javax.inject.Inject;

public class DermatologosPresenter {

    private Logger log = Logger.getLogger(DermatologosPresenter.class.getName());

    @FXML
    private View visitasDermatologos;

    @FXML
    private CharmListView<Visita, String> listaVisitas;

    @Inject
    private UserServices userService;

    @Inject
    private VisitasServices visitaService;

    @Inject
    private ListaVisitasServices listaDeVisitas;

    public void initialize() {

        visitasDermatologos.setShowTransitionFactory(BounceInRightTransition::new);

        visitasDermatologos.showingProperty().addListener((obs, oldValue, newValue) -> {

            if (newValue) {

                AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e
                        -> MobileApplication.getInstance().showLayer(RegistroDeVisitas.MENU_LAYER)));
                appBar.setTitleText("Visitas APM:" + userService.getUser().get().getApm());
                
            }
        });
       
        listaVisitas.setHeadersFunction(n -> n.obtenerFechaFormateada());
        listaVisitas.setCellFactory(p -> new VisitaCell(this::edit, this::remove));
        listaVisitas.setHeaderCellFactory(p -> new HeaderVisitaCell());
        listaVisitas.setPlaceholder(new Label("No hay visitas registradas"));
        listaVisitas.setItems(listaDeVisitas.getListaDeVisitas());
        
        
        /* ========================== */
        /* BOTON NEXT                 */
        /* ========================== */
        
        FloatingActionButton flButtonNext = new FloatingActionButton(MaterialDesignIcon.ADD.text,
            e -> {  
                    visitaService.createVisita();
                    visitaService.setMode(1);
                    MobileApplication.getInstance().switchView(VISITAS_SOL_MEDICOS);
                });
           
        visitasDermatologos.getLayers().add(flButtonNext);
        
        /* =========================== */
        /* Actualizar Lista de Visitas */
        /* =========================== */
        
        Platform.runLater(() -> {
            listaDeVisitas.iniciarLista();
            
        });


    }

    private void edit(Visita visita) {

        visitaService.setVisita(visita);
        visitaService.setMode(2);
        MobileApplication.getInstance().switchView(VISITAS_SOL_MEDICOS);
    }

    private void remove(Visita visita) {
        visitaService.setVisita(visita);
        visitaService.setMode(3);
        MobileApplication.getInstance().switchView(VISITAS_SOL_CONFIRMACION);

    }

}
