package com.registrodevisitas.views;

import com.gluonhq.charm.glisten.animation.BounceInUpTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
 
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.registrodevisitas.model.ConfiguradorGeneral;
import com.registrodevisitas.services.ConfiguradorGeneralService;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javax.inject.Inject;

public class SettingsPresenter {
    
    private Logger log = Logger.getLogger(SettingsPresenter.class.getName());

    @FXML
    private View settings;
    
    @FXML
    private TextField urlconexion;

    @Inject
    private ConfiguradorGeneralService service;
    
    private ConfiguradorGeneral config;

    public void initialize() {

        log.info("SettingsPresenter:BEGIN");
        
        settings.setShowTransitionFactory(BounceInUpTransition::new);

        settings.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e
                        -> MobileApplication.getInstance().switchToPreviousView()));
                appBar.setTitleText("Configuración");
                appBar.getActionItems().add(MaterialDesignIcon.SAVE.button(e -> 
                {
                    service.storeSettings();
                    log.info("SettingsPresenter:END"); 
                    MobileApplication.getInstance().switchToPreviousView();
                
                }));
            }
            
            
        });
        
        service.retrieveSettings();
        
        urlconexion.textProperty().bindBidirectional(service.settingsProperty().get().urlServiciosProperty());
        
    }
     
}
