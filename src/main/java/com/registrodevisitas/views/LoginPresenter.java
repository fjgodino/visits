package com.registrodevisitas.views;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.Alert;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.ProgressIndicator;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.registrodevisitas.RegistroDeVisitas;
import static com.registrodevisitas.RegistroDeVisitas.MENU_VIEW;
import com.registrodevisitas.services.CausasServices;
import com.registrodevisitas.services.ConfiguradorGeneralService;
import com.registrodevisitas.services.MedicosServices;
import com.registrodevisitas.services.PromocionesServices;
import com.registrodevisitas.services.UserServices;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javax.inject.Inject;

public class LoginPresenter {

    private Logger log = Logger.getLogger(LoginPresenter.class.getName());

    @FXML
    private View login;

    @FXML
    private TextField usuario;

    @FXML
    private PasswordField clave;

    @FXML
    private Button ingreso;

    @FXML
    private ProgressIndicator progress;

    @Inject
    private UserServices userService;

    @Inject
    private ConfiguradorGeneralService configuracion;
    
    @Inject
    private MedicosServices medicosService;
    
    @Inject
    private PromocionesServices promocionesService;
    
    @Inject
    private CausasServices causasService;
    
    private int actividad = 0;

    public void initialize() {

        configuracion.retrieveSettings();

        login.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {

                AppBar appBar = MobileApplication.getInstance().getAppBar();

                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e
                        -> MobileApplication.getInstance().showLayer(RegistroDeVisitas.MENU_LAYER)));

                appBar.setTitleText("Ingreso");

                usuario.setText(configuracion.settingsProperty().get().getUltimoUser());

            }
        });

        ingreso.setOnAction((event) -> {
            buttonClick();
        });

        final ChangeListener changeListener = (ChangeListener) (ObservableValue observableValue, Object oldValue, Object newValue) -> {

            
            log.info("LoginPresenter:buttonClick: Validacion finalizada.." + observableValue.getValue().toString());
            
            String msg = "";

            switch (((Integer) newValue).intValue()) {

                case 1: /* OK */

                    if (this.actividad == 1) {

                        if (userService.isConnected()) {

                            log.info("LoginPresenter:ChangeListener: Usuario Valido");

                            configuracion.settingsProperty().get().setUltimoUser(usuario.getText());
                            configuracion.settingsProperty().get().setUltimaPassword(MD5(clave.getText()));
                            configuracion.storeSettings();

                            log.info("LoginPresenter:ChangeListener: Genera Medicos");

                        } else {
                            log.info("LoginPresenter:ChangeListener: Usuario Valido. Modo Desconectado");
                            MobileApplication.getInstance().showMessage("Modo desconectado");
                        }

                        Platform.runLater(() -> {

                            cargarCacheMedicos();

                        });

                    }
                    
                    if (this.actividad == 2) {
                       Platform.runLater(() -> {

                            cargarCachePromociones();

                        });
                    }
                    
                    if (this.actividad == 3) {
                        
                         Platform.runLater(() -> {

                            cargarCacheCausas();

                        });
                    }
                    
                    if (this.actividad == 4) {
                        
                        MobileApplication.getInstance().switchView(MENU_VIEW);
                    }

                    break;

                case 2: /* Clave INVALIDA */
                    
                    log.info("LoginPresenter:ChangeListener: Usuario con Clave Invalida");
                    
                    msg = "Clave Inválida";

                     
                    break;

                case 3: /* Usuario INEXISTENTE */

                    log.info("LoginPresenter:ChangeListener: Usuario Inexistente");
                    
                    msg = "Usuario Inexistente";

                    break;

                case 4: /* Error de Conexion */
 
                    log.info("LoginPresenter:ChangeListener: Error de Conexion");
                    
                    msg = "Error de Conexión";

                    break;

                case 5: /* Error Inexperado */

                    log.info("LoginPresenter:ChangeListener: Error Inéxperado de Conexión");
                    
                    msg = "Error Inéxperado de Conexión";

                    break;

            }
            
            progress.setVisible(false);

            if (((Integer) newValue).intValue() > 1 && ((Integer) newValue).intValue() < 6) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitleText("Error");
                alert.setContentText(msg);
                alert.showAndWait();
                userService.codeReturned().setValue(0);
            }

        };

        userService.codeReturned().addListener(changeListener);

        medicosService.codeReturned().addListener(changeListener);

        promocionesService.codeReturned().addListener(changeListener);

        causasService.codeReturned().addListener(changeListener);

    }

    void buttonClick() {

        actividad = 1;
                
        progress.setVisible(true);

        log.info("LoginPresenter:buttonClick: Validando Usuario...");

        
        userService.validateUser(usuario.getText(), MD5(clave.getText()));

        log.info("LoginPresenter:buttonClick: Usuario Validacion en proceso");

    }
    
    void cargarCacheMedicos() {
         
        actividad = 2;

        log.info("LoginPresenter:cargarCache: BEGIN Cargando Cache...");

        if (medicosService.generarListaMedicos(userService.isConnected())) {

            progress.setVisible(true);
            medicosService.generarListaDeMedicosDeServer();

        } else {
            medicosService.codeReturned().setValue(1);

        }
 
        log.info("LoginPresenter:cargarCache: END Cargando Cache");
        
    }
    
    void cargarCachePromociones() {
         
        actividad = 3;
        
        log.info("LoginPresenter:cargarCachePromociones: BEGIN Cargando Cache...");

        if (promocionesService.generarListaPromociones(userService.isConnected())) {

            progress.setVisible(true);
            promocionesService.generarListaDePromocionesDeServer();

        }
        else
        {
            promocionesService.codeReturned().setValue(1);
            
        }

        log.info("LoginPresenter:cargarCachePromociones: END Cargando Cache");
        
    }

    void cargarCacheCausas() {
        
        actividad = 4;
        
         log.info("LoginPresenter:cargarCachePromociones: BEGIN Cargando Cache...");

        if (causasService.generarListaCausas(userService.isConnected()) ) {

            progress.setVisible(true);
            causasService.generarListaDeCausasDeServer();

        }
        else
        {
            causasService.codeReturned().setValue(1);
            
        }

        
    }
    
    private String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

}
