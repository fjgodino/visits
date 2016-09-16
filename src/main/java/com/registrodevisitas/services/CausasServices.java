/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.registrodevisitas.services;

import com.registrodevisitas.model.Causa;
import com.gluonhq.charm.down.common.PlatformFactory;
import com.gluonhq.connect.ConnectState;
import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.connect.provider.RestClient;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.registrodevisitas.model.ConfiguradorGeneral;
import com.registrodevisitas.model.ListaDeCausas;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javax.inject.Inject;

/**
 *
 * @author jgodino
 */
public class CausasServices {

    private final Logger logger = Logger.getLogger(CausasServices.class.getName());
    private String cacheName = "";
    private File cacheFile;
    private GluonObservableList<Causa> causas;
    private boolean existeCache;
    private boolean cacheVencido;
    private final IntegerProperty retrieved = new SimpleIntegerProperty(0);

    @Inject
    private ConfiguradorGeneralService configuracion;

    @Inject
    private UserServices userService;

    public CausasServices() {

        causas = new GluonObservableList();
        cacheVencido = true;

    }

    public GluonObservableList<Causa> obtenerCausasDeVisitas() {
        return this.causas;
    }

    public IntegerProperty codeReturned() {
        return retrieved;
    }

    public ObservableList<Causa> obtenerCausas() {

        return causas;

    }

    public boolean generarListaCausas(boolean userConnected) {

        logger.info("CausasServices:generarListaCausas: BEGIN ");

        /* Si el tamanio de la lista es cero, no se actualizo */
        if (causas.size() > 0) {
            return false;
        }

        /* Determinar si el cache existente no existe o debe ser actualizado
         pues fue modificado posterior a la fecha del cache
         */
        if (userConnected) {
            logger.info("CausasServices: Constructor: Validando Cache ...");
            validateCache(new Date(Long.parseLong(userService.getUser().get().getCausas())));
            logger.info("CausasServices: Constructor: Actualizar Cache: " + cacheVencido);
        }
        /* Si no existe cache generarlo desde el server */

        if (userConnected && (!existeCache || cacheVencido)) {

            if (cacheVencido) {
                this.cacheFile.delete();
            }

            logger.info("CausasServices:generarListaCausas: END True ");

            return true;
        } else {
            /* Si existe cache y no esta vencido, armar lista
             desde el cache */

            try {

                if (!userConnected) {
                    this.cacheName = PlatformFactory.getPlatform().getPrivateStorage() + File.separator + "causas_visitas.json";

                    this.cacheFile = new File(cacheName);
                }

                generarListaDeCausasDeCache();

            } catch (Exception ex) {

                this.cacheFile.delete();
                return true;

            }

            logger.info("CausasServices:generarListaCausas: END False ");

            return false;

        }

    }

    public void generarListaDeCausasDeCache() throws FileNotFoundException, IOException {

        logger.info("CausasServices:generarListaDeCausasDeCache: BEGIN ");

        Gson gson = new Gson();

        JsonReader jsonReader = new JsonReader(new FileReader(this.cacheFile));

        final ListaDeCausas listaDecausas = gson.fromJson(jsonReader, ListaDeCausas.class);

        jsonReader.close();

        listaDecausas.sortCauses();

        causas.addAll(listaDecausas.getCausas());

        listaDecausas.getCausas().clear();

        logger.info("CausasServices:generarListaDeCausasDeCache: END ");

    }

    public void generarListaDeCausasDeServer() {

        logger.info("CausasServices:generarListaDeCausasDeServer: BEGIN ");

        try {

            /* ------------------ */
            /* String de Conexión */
            /* ------------------ */
            ConfiguradorGeneral configGral = configuracion.settingsProperty().get();

            if (configGral.getUrlServicios() == null) {
                configuracion.retrieveSettings();
            }

            String urlToConnect = configGral.getUrlServicios();

            logger.log(Level.INFO, "UserServices:validateUser: URL" + urlToConnect);

            /* ========================================================= */
            /* create a RestClient to the specific URL                   */
            /* ========================================================= */
            RestClient restClient = RestClient.create()
                    .method("POST")
                    .host(urlToConnect)
                    .path("/serviceCausas")
                    .contentType("application/json")
                    .dataString("{ \"action\": \"G\", \"email\": \"" + userService.getUser().get().getEmail() + "\"}");

            GluonObservableList<Causa> causasTmp = DataProvider.retrieveList(restClient.createListDataReader(Causa.class));

            causasTmp.initializedProperty().addListener((observable, ov, nv) -> {

                if (nv) {

                    logger.info("CausasServices:generarListaDeCausasDeServer: Causas Recuperados");

                }
            });

            causasTmp.stateProperty().addListener((observable, oldState, newState) -> {

                /* Evento que se genera por cada estado del proceso */
                if (newState == ConnectState.FAILED) {

                    if (causasTmp.getException() != null) {
                        logger.info("CausasServices:generarListaDeCausasDeServer:Error Recuperando Causas  ");

                    }
                    retrieved.set(4);
                }

                if (newState == ConnectState.SUCCEEDED) {

                    ListaDeCausas lista = new ListaDeCausas();

                    lista.getCausas().addAll(causasTmp.subList(0, causasTmp.size()));

                    lista.sortCauses();

                    causas.addAll(lista.getCausas());

                    storeCache(lista);

                    retrieved.set(1);
                }

            });

        } catch (Exception e) {

            logger.log(Level.SEVERE, e.getMessage(), e);

            retrieved.set(5);

        }

        logger.info("CausasServices:generarListaDeCausasDeServer: END ");

    }

    public void validateCache(Date lastUpdate) {

        logger.info("CausasServices:validateCache: BEGIN ");

        try {

            this.cacheName = PlatformFactory.getPlatform().getPrivateStorage() + File.separator + "causas_visitas.json";

            this.cacheFile = new File(cacheName);

            Date cacheFileLastUpdate = null;

            if (cacheFile.exists()) {

                cacheFileLastUpdate = new Date(cacheFile.lastModified());

                existeCache = true;

            } else {

                logger.info("CausasServices:validateCache: No existe Cache ");

                existeCache = false;

                return;
            }

            cacheVencido = lastUpdate.after(cacheFileLastUpdate);

            logger.info("CausasServices:validateCache: Existe Cache y esta vencido? " + cacheVencido);

        } catch (IOException ex) {

            logger.log(Level.SEVERE, ex.getMessage(), ex);
            existeCache = false;

        }

        logger.info("CausasServices:validateCache: END ");

    }

    public void storeCache(ListaDeCausas lista) {

        logger.info("CausasServices:storeCache BEGIN ");

        try {

            Gson gson = new Gson();

            try (FileWriter writer = new FileWriter(this.cacheName)) {

                gson.toJson(lista, writer);

                writer.close();
            }

            logger.log(Level.INFO, "CausasServices:storeCache: folder: " + PlatformFactory.getPlatform().getPrivateStorage().getAbsolutePath());

        } catch (Exception ex) {
            Logger.getLogger(ConfiguradorGeneralService.class.getName()).log(Level.SEVERE, null, ex);
        }

        logger.info("CausasServices:storeCache END ");

    }

    public void clean() {
        this.cacheFile.delete();
    }
    public Causa getCausaById(Integer id)
    {
        
        Iterator<Causa> iterator = causas.iterator();
        
        while(iterator.hasNext())
        {
            Causa causa = iterator.next();
            
            if(causa.getCod_causa() == id)
            {
                return causa; 
            }
            
        }
         
        return null;
    }
    

}
