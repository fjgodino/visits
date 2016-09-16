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
import com.registrodevisitas.model.ListaDePromociones;
import com.registrodevisitas.model.Medico;
import com.registrodevisitas.model.Promocion;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
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
public class PromocionesServices {

    private final Logger logger = Logger.getLogger(PromocionesServices.class.getName());
    private String cacheName = "";
    private File cacheFile;
    private GluonObservableList<Promocion> promociones;
    private boolean existeCache;
    private boolean cacheVencido;
    private final IntegerProperty retrieved = new SimpleIntegerProperty(0);

    @Inject
    private ConfiguradorGeneralService configuracion;

    @Inject
    private UserServices userService;

    public PromocionesServices() {

        promociones = new GluonObservableList();
        cacheVencido = true;

    }

    public IntegerProperty codeReturned() {
        return retrieved;
    }

    public GluonObservableList<Promocion> obtenerPromocionesSorted() {

        Comparator<? super Promocion> comparatorByDesc = new Comparator<Promocion>() {

            @Override
            public int compare(Promocion o1, Promocion o2) {
                return o1.getDescripcion().compareToIgnoreCase(o2.getDescripcion());
            }

        };

        promociones.sort(comparatorByDesc);

        return this.promociones;
    }

    public ObservableList<Promocion> obtenerPromociones() {

        return promociones;

    }

    public boolean generarListaPromociones(boolean userConnected) {

        logger.info("PromocionesServices:generarListaPromociones: BEGIN ");

        /* Si el tamanio de la lista es cero, no se actualizo */
        if (promociones.size() > 0) {
            return false;
        }

        /* Determinar si el cache existente no existe o debe ser actualizado
         pues fue modificado posterior a la fecha del cache
         */
        if (userConnected) {
            logger.info("PromocionesServices: Constructor: Validando Cache ...");
            validateCache(new Date(Long.parseLong(userService.getUser().get().getPromociones())));
            logger.info("PromocionesServices: Constructor: Actualizar Cache: " + cacheVencido);
        }

        /* Si no existe cache generarlo desde el server */
        if (userConnected && (!existeCache || cacheVencido)) {

            if (cacheVencido) {
                this.cacheFile.delete();
            }

            logger.info("PromocionesServices:generarListaPromociones: END True ");

            return true;
        } else {
            /* Si existe cache y no esta vencido, armar lista
             desde el cache */

            try {

                if (!userConnected) {
                    this.cacheName = PlatformFactory.getPlatform().getPrivateStorage() + File.separator + "promociones_visitas.json";

                    this.cacheFile = new File(cacheName);
                }

                generarListaDePromocionesDeCache();

            } catch (Exception ex) {

                this.cacheFile.delete();
                return true;

            }

            logger.info("PromocionesServices:generarListaPromociones: END False ");

            return false;

        }

    }

    public void generarListaDePromocionesDeCache() throws FileNotFoundException, IOException {

        logger.info("PromocionesServices:generarListaDePromocionesDeCache: BEGIN ");

        Gson gson = new Gson();

        JsonReader jsonReader = new JsonReader(new FileReader(this.cacheFile));

        final ListaDePromociones listaDepromociones = gson.fromJson(jsonReader, ListaDePromociones.class);

        jsonReader.close();

        listaDepromociones.sortPromociones();

        promociones.addAll(listaDepromociones.getPromociones());

        listaDepromociones.getPromociones().clear();

        logger.info("PromocionesServices:generarListaDePromocionesDeCache: END ");

    }

    public void generarListaDePromocionesDeServer() {

        logger.info("PromocionesServices:generarListaDePromocionesDeServer: BEGIN ");

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
                    .path("/servicePromociones")
                    .contentType("application/json")
                    .dataString("{ \"action\": \"G\", \"email\": \"" + userService.getUser().get().getEmail() + "\"}");

            GluonObservableList<Promocion> promocionesTmp = DataProvider.retrieveList(restClient.createListDataReader(Promocion.class));

            promocionesTmp.initializedProperty().addListener((observable, ov, nv) -> {

                if (nv) {

                    logger.info("PromocionesServices:generarListaDePromocionesDeServer: Promociones Recuperados");

                }
            });

            promocionesTmp.stateProperty().addListener((observable, oldState, newState) -> {

                /* Evento que se genera por cada estado del proceso */
                if (newState == ConnectState.FAILED) {

                    if (promocionesTmp.getException() != null) {
                        logger.info("PromocionesServices:generarListaDePromocionesDeServer:Error Recuperando Promociones  ");

                    }
                    retrieved.set(4);
                }

                if (newState == ConnectState.SUCCEEDED) {

                    ListaDePromociones lista = new ListaDePromociones();

                    lista.getPromociones().addAll(promocionesTmp.subList(0, promocionesTmp.size()));

                    lista.sortPromociones();

                    promociones.addAll(lista.getPromociones());

                    storeCache(lista);

                    retrieved.set(1);
                }

            });

        } catch (Exception e) {

            logger.log(Level.SEVERE, e.getMessage(), e);

            retrieved.set(5);

        }

        logger.info("PromocionesServices:generarListaDePromocionesDeServer: END ");

    }

    public void validateCache(Date lastUpdate) {

        logger.info("PromocionesServices:validateCache: BEGIN ");

        try {

            this.cacheName = PlatformFactory.getPlatform().getPrivateStorage() + File.separator + "promociones_visitas.json";

            this.cacheFile = new File(cacheName);

            Date cacheFileLastUpdate = null;

            if (cacheFile.exists()) {

                cacheFileLastUpdate = new Date(cacheFile.lastModified());

                existeCache = true;

            } else {

                logger.info("PromocionesServices:validateCache: No existe Cache ");

                existeCache = false;

                return;
            }

            cacheVencido = lastUpdate.after(cacheFileLastUpdate);

            logger.info("PromocionesServices:validateCache: Existe Cache y esta vencido? " + cacheVencido);

        } catch (IOException ex) {

            logger.log(Level.SEVERE, ex.getMessage(), ex);
            existeCache = false;

        }

        logger.info("PromocionesServices:validateCache: END ");

    }

    public void storeCache(ListaDePromociones lista) {

        logger.info("PromocionesServices:storeCache BEGIN ");

        try {

            Gson gson = new Gson();

            try (FileWriter writer = new FileWriter(this.cacheName)) {

                gson.toJson(lista, writer);

                writer.close();
            }

            logger.log(Level.INFO, "PromocionesServices:storeCache: folder: " + PlatformFactory.getPlatform().getPrivateStorage().getAbsolutePath());

        } catch (Exception ex) {
            Logger.getLogger(ConfiguradorGeneralService.class.getName()).log(Level.SEVERE, null, ex);
        }

        logger.info("PromocionesServices:storeCache END ");

    }

    public void clean() {
        this.cacheFile.delete();
    }
    
    public Promocion getPromocionesById(String id)
    {
        
        Iterator<Promocion> iterator = promociones.iterator();
        
        while(iterator.hasNext())
        {
            Promocion promocion = iterator.next();
            
            if(promocion.getCod_promocion().compareTo(id)== 0)
            {
                return promocion; 
            }
            
        }
         
        return null;
    }

}
