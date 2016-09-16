/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.registrodevisitas.services;

import com.gluonhq.charm.down.common.PlatformFactory;
import com.gluonhq.connect.ConnectState;
import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.connect.provider.RestClient;
import com.registrodevisitas.model.ConfiguradorGeneral;
import com.registrodevisitas.model.User;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author jgodino
 */
public class UserServices {

    private Logger log = Logger.getLogger(UserServices.class.getName());

    private GluonObservableObject<User> userLogeado;

    @Inject
    private ConfiguradorGeneralService configuracion;

    private final IntegerProperty retrieved = new SimpleIntegerProperty(0);
    private final BooleanProperty connected = new SimpleBooleanProperty();

    public UserServices() {

    }

    public boolean isConnected() {
        return connected.get();
    }

    public IntegerProperty codeReturned() {
        return retrieved;
    }

    public void validateUser(String user, String password) {

        log.info("UserServices:validateUser: BEGIN user/pass: " + user + "/" + password);

        if (user.compareTo("DEMO") == 0 ) {
            
            userLogeado = new GluonObservableObject<User>();
            User tmpUser = new User();
            tmpUser.setNombre("");
            tmpUser.setEmail(user);
            tmpUser.setApm("DEMO");
            userLogeado.set(tmpUser);
            connected.set(false);
            try { 
                ExportResource("medicos_visitas.json");
                ExportResource("promociones_visitas.json");
                ExportResource("causas_visitas.json");
            } catch (Exception ex) {
                log.log(Level.SEVERE, null, ex);
            }
            
            retrieved.set(1);
            
            return;

        }

        /* ------------------ */
        /* String de Conexión */
        /* ------------------ */
        try {

            ConfiguradorGeneral configGral = configuracion.settingsProperty().get();

            if (configGral.getUrlServicios() == null) {
                configuracion.retrieveSettings();
            }

            String urlToConnect = configGral.getUrlServicios();

            log.log(Level.INFO, "UserServices:validateUser: URL" + urlToConnect);

            /* ========================================================= */
            /* create a RestClient to the specific URL                   */
            /* ========================================================= */
            RestClient restClient = RestClient.create()
                    .method("POST")
                    .host(urlToConnect)
                    .path("/serviceLogin")
                    .contentType("application/json")
                    .dataString("{ \"action\": \"V\", \"email\": \"" + user + "\", \"password\": \"" + password + "\"}");

            userLogeado = DataProvider.retrieveObject(restClient.createObjectDataReader(User.class));

            userLogeado.initializedProperty().addListener((observable, ov, nv) -> {

                if (nv) {

                    log.info("UserServices:validateUser: RestFull finalizado");

                }
            });

            userLogeado.stateProperty().addListener((observable, oldState, newState) -> {

                /* Evento que se genera por cada estado del proceso */
                if (newState == ConnectState.FAILED) {

                    if (userLogeado.getException() != null) {
                        log.info("UserServices:validateUser ERROR: " + userLogeado.getException().getMessage());
                    }

                    if (user.compareTo(configuracion.settingsProperty().get().getUltimoUser()) == 0
                            && password.compareTo(configuracion.settingsProperty().get().getUltimaPassword()) == 0) {
                        User tmpUser = new User();
                        tmpUser.setNombre("");
                        tmpUser.setEmail(user);
                        tmpUser.setApm("Desconectado");

                        userLogeado.set(tmpUser);

                        connected.set(false);
                        retrieved.set(1);
                    } else {
                        retrieved.set(4);
                    }

                } else if (newState == ConnectState.SUCCEEDED) {

                    log.info("UserServices:validateUser Resultado: " + (userLogeado.get()).getAction());

                    if ((userLogeado.get()).getAction().compareTo("OK") == 0) {

                        Date date = new Date(Long.parseLong(userLogeado.get().getMedicos()));

                        connected.set(true);
                        retrieved.set(1);
                    }

                    if ((userLogeado.get()).getAction().compareTo("INVALIDO") == 0) {
                        retrieved.set(2);
                    }

                    if ((userLogeado.get()).getAction().compareTo("INEXISTENTE") == 0) {
                        retrieved.set(3);
                    }

                } else if (newState == ConnectState.READY) {

                    log.info("UserServices:validateUser ConnectState.READY ");

                } else if (newState == ConnectState.REMOVED) {

                    log.info("UserServices:validateUser ConnectState.REMOVED ");

                }

            });

        } catch (Exception e) {

            log.log(Level.SEVERE, e.getMessage(), e);

            retrieved.set(5);

        }

        log.info("UserServices:validateUser END");

    }

    public GluonObservableObject<User> getUser() {

        return this.userLogeado;

    }
    
    static private void ExportResource(String resourceName) throws Exception {//todo move to Utils
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            stream = UserServices.class.getResourceAsStream("/" +  resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree"
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            String fileName = PlatformFactory.getPlatform().getPrivateStorage() + File.separator + resourceName;       
            resStreamOut = new FileOutputStream(fileName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }
 
    }

}
