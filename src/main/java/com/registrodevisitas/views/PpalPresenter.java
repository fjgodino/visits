package com.registrodevisitas.views;

import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.charm.glisten.visual.Swatch;
import com.registrodevisitas.RegistroDeVisitas;
import static com.registrodevisitas.RegistroDeVisitas.VISITAS_DERMATOLOGOS_VIEW;
import com.registrodevisitas.services.LugaresDeVisitasServices;
import com.registrodevisitas.services.UserServices;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javax.inject.Inject;

public class PpalPresenter {

    @FXML
    private View ppal;
    
    @FXML 
    private Button dermatologos;
    
    @FXML 
    private Button farmacias;
    
    @Inject
    private LugaresDeVisitasServices lugardevisitasservice;
    
    @Inject 
    private UserServices userService;

    public void initialize() {
        
        ppal.setShowTransitionFactory(BounceInRightTransition::new);
        
        ppal.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                
                AppBar appBar = MobileApplication.getInstance().getAppBar();
                if(!userService.isConnected())
                {
                    Swatch.TEAL.assignTo(appBar.getScene());
                }
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> 
                        MobileApplication.getInstance().showLayer(RegistroDeVisitas.MENU_LAYER)));
                appBar.setTitleText("Registro de Visitas");
                
            }
        });
        
        dermatologos.setOnAction((event) -> {
            processDermatologos();
        });
        
        farmacias.setOnAction((event) -> {
            processFarmacias();
        });
        
    }
    
    
    public void processDermatologos() {
        
        MobileApplication.getInstance().switchView(VISITAS_DERMATOLOGOS_VIEW);
       
    }
    
    
    public void processFarmacias() {
        
        
        
       
    }
    
}
