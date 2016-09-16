/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.registrodevisitas.services;

import com.gluonhq.charm.down.common.PlatformFactory;
import com.gluonhq.connect.ConnectState;
import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.connect.provider.RestClient;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.registrodevisitas.model.ConfiguradorGeneral;
import com.registrodevisitas.model.ListaDeMedicos;
import com.registrodevisitas.model.Medico;
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
public class MedicosServices {

    private final Logger logger = Logger.getLogger(MedicosServices.class.getName());
    private String cacheName = "";
    private File cacheFile;
    private GluonObservableList<Medico> medicos;
    private boolean existeCache;
    private boolean cacheVencido;
    private final IntegerProperty retrieved = new SimpleIntegerProperty(0);

    @Inject
    private ConfiguradorGeneralService configuracion;

    @Inject
    private UserServices userService;

    public MedicosServices() {

        medicos = new GluonObservableList();
        cacheVencido = true;

    }

    public IntegerProperty codeReturned() {
        return retrieved;
    }

    public ObservableList<Medico> obtenerMedicos() {

        return medicos;

    }

    public boolean generarListaMedicos(boolean userConnected) {

        logger.info("MedicosServices:generarListaMedicos: BEGIN ");

        /* Si el tamanio de la lista es cero, no se actualizo */
        if (medicos.size() > 0) {
            return false;
        }

        /* Determinar si el cache existente no existe o debe ser actualizado
         pues fue modificado posterior a la fecha del cache
         */
        if (userConnected) {
            logger.info("MedicosServices: Constructor: Validando Cache ...");
            validateCache(new Date(Long.parseLong(userService.getUser().get().getMedicos())));
            logger.info("MedicosServices: Constructor: Actualizar Cache: " + cacheVencido);
        }

        /* Si no existe cache generarlo desde el server */
        if (userConnected && (!existeCache || cacheVencido)) {

            if (cacheVencido) {
                this.cacheFile.delete();
            }

            logger.info("MedicosServices:generarListaMedicos: END True ");

            return true;
        } else {
            /* Si existe cache y no esta vencido, armar lista
             desde el cache */

            try {

                if (!userConnected) {
                    this.cacheName = PlatformFactory.getPlatform().getPrivateStorage() + File.separator + "medicos_visitas.json";

                    this.cacheFile = new File(cacheName);
                }

                generarListaDeMedicosDeCache();

            } catch (Exception ex) {

                this.cacheFile.delete();
                return true;

            }

            logger.info("MedicosServices:generarListaMedicos: END False ");

            return false;

        }

    }

    public void generarListaDeMedicosDeCache() throws FileNotFoundException, IOException {

        logger.info("MedicosServices:generarListaDeMedicosDeCache: BEGIN ");

        Gson gson = new Gson();

        JsonReader jsonReader = new JsonReader(new FileReader(this.cacheFile));

        final ListaDeMedicos listaDemedicos = gson.fromJson(jsonReader, ListaDeMedicos.class);

        jsonReader.close();

        medicos.addAll(listaDemedicos.getMedicos());

        listaDemedicos.getMedicos().clear();

        logger.info("MedicosServices:generarListaDeMedicosDeCache: END ");

    }

    public void generarListaDeMedicosDeServer() {

        logger.info("MedicosServices:generarListaDeMedicosDeServer: BEGIN ");

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
                    .path("/serviceMedicos")
                    .contentType("application/json")
                    .dataString("{ \"action\": \"G\", \"email\": \"" + userService.getUser().get().getEmail() + "\"}");

            medicos = DataProvider.retrieveList(restClient.createListDataReader(Medico.class));

            medicos.initializedProperty().addListener((observable, ov, nv) -> {

                if (nv) {

                    logger.info("MedicosServices:generarListaDeMedicosDeServer: Medicos Recuperados");

                }
            });

            medicos.stateProperty().addListener((observable, oldState, newState) -> {

                /* Evento que se genera por cada estado del proceso */
                if (newState == ConnectState.FAILED) {

                    if (medicos.getException() != null) {
                        logger.info("MedicosServices:generarListaDeMedicosDeServer:Error Recuperando Medicos  ");

                    }
                    retrieved.set(4);
                }

                if (newState == ConnectState.SUCCEEDED) {

                    storeCache();

                    retrieved.set(1);
                }

            });

        } catch (Exception e) {

            logger.log(Level.SEVERE, e.getMessage(), e);

            retrieved.set(5);

        }

        logger.info("MedicosServices:generarListaDeMedicosDeServer: END ");

    }

    public void validateCache(Date lastUpdate) {

        logger.info("MedicosServices:validateCache: BEGIN ");

        try {

            this.cacheName = PlatformFactory.getPlatform().getPrivateStorage() + File.separator + "medicos_visitas.json";

            this.cacheFile = new File(cacheName);

            Date cacheFileLastUpdate = null;

            if (cacheFile.exists()) {

                cacheFileLastUpdate = new Date(cacheFile.lastModified());

                existeCache = true;

            } else {

                logger.info("MedicosServices:validateCache: No existe Cache ");

                existeCache = false;

                return;
            }

            cacheVencido = lastUpdate.after(cacheFileLastUpdate);

            logger.info("MedicosServices:validateCache: Existe Cache y esta vencido? " + cacheVencido);

        } catch (IOException ex) {

            logger.log(Level.SEVERE, ex.getMessage(), ex);
            existeCache = false;

        }

        logger.info("MedicosServices:validateCache: END ");

    }

    public void storeCache() {

        logger.info("MedicosServices:storeCache BEGIN ");

        ListaDeMedicos lista = new ListaDeMedicos();

        lista.getMedicos().addAll(medicos.subList(0, medicos.size()));

        try {

            Gson gson = new Gson();

            try (FileWriter writer = new FileWriter(this.cacheName)) {

                gson.toJson(lista, writer);

                writer.close();
            }

            logger.log(Level.INFO, "MedicosServices:storeCache: folder: " + PlatformFactory.getPlatform().getPrivateStorage().getAbsolutePath());

        } catch (Exception ex) {
            Logger.getLogger(ConfiguradorGeneralService.class.getName()).log(Level.SEVERE, null, ex);
        }

        logger.info("MedicosServices:storeCache END ");

    }

    public void clean() {
        this.cacheFile.delete();
    }
    
    public Medico getMedicoById(Integer id)
    {
        
        Iterator<Medico> iterator = medicos.iterator();
        
        while(iterator.hasNext())
        {
            Medico medico = iterator.next();
            
            if(medico.getCodigoMedico() == id)
            {
                return medico; 
            }
            
        }
         
        return null;
    }
    

}
