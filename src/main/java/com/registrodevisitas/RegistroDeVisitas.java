package com.registrodevisitas;

import com.gluonhq.charm.down.common.PlatformFactory;
import com.registrodevisitas.views.DermatologosView;
import com.registrodevisitas.views.PpalView;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.Avatar;
import com.gluonhq.charm.glisten.control.NavigationDrawer;
import com.gluonhq.charm.glisten.control.NavigationDrawer.Item;
import com.gluonhq.charm.glisten.layout.layer.SidePopupView;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.charm.glisten.visual.Swatch;
import com.registrodevisitas.views.LoginView;
import com.registrodevisitas.views.SettingsView;
import com.registrodevisitas.views.SolicitudcausaView;
import com.registrodevisitas.views.SolicitudconfirmacionView;
import com.registrodevisitas.views.SolicitudfechaView;
import com.registrodevisitas.views.SolicitudturnoView;
import com.registrodevisitas.views.SolicitudmedicoView;
import com.registrodevisitas.views.SolicitudobservacionView;
import com.registrodevisitas.views.SolicitudpromocionView;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class RegistroDeVisitas extends MobileApplication {

    public static final String PRIMARY_VIEW = HOME_VIEW;
    public static final String MENU_VIEW = "Menu Principal";
    public static final String MENU_LAYER = "Side Menu";
    public static final String VISITAS_DERMATOLOGOS_VIEW = "Visitas Dermatologos";
    public static final String VISITAS_SOL_FECHA = "Solicitud De Fecha";
    public static final String VISITAS_SOL_TURNO = "Solicitud De Turno";
    public static final String VISITAS_SOL_MEDICOS = "Solicitud de Medicos";
    public static final String VISITAS_SOL_CAUSA = "Solicitud de Causa";
    public static final String VISITAS_SOL_PROMOCION = "Solicitud de Promocion";
    public static final String VISITAS_SOL_OBSERVACION = "Solicitud de Observacion";
    public static final String VISITAS_SOL_CONFIRMACION = "Solicitud de Confirmacion";
    public static final String SETTINGS_VIEW = "Settings View";

    private final BooleanProperty userLogged = new SimpleBooleanProperty();

    @Override
    public void init() {

        /* ===================== */
        /* Activación de Logging */
        /* ===================== */
        Logger log = LogManager.getLogManager().getLogger("");

        String fileName = "";
        try {
            String pattern = "yyyyMMdd"; //_hhmmss
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            fileName = PlatformFactory.getPlatform().getPrivateStorage() + File.separator + "visitas_" + format.format(new Date()) + ".log";
            FileHandler fh = new FileHandler(fileName, true);
            log.addHandler(fh);
            log.setLevel(Level.FINE);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException ex) {
            Logger.getLogger(RegistroDeVisitas.class.getName()).log(Level.SEVERE, null, ex);
        }

        /* ========================== */
        /* Lista de las Vistas        */
        /* ========================== */
        addViewFactory(PRIMARY_VIEW, () -> (View) new LoginView().getView());
        addViewFactory(MENU_VIEW, () -> (View) new PpalView().getView());
        addViewFactory(VISITAS_DERMATOLOGOS_VIEW, () -> (View) new DermatologosView().getView());
        addViewFactory(VISITAS_SOL_MEDICOS, () -> (View) new SolicitudmedicoView().getView());
        addViewFactory(VISITAS_SOL_FECHA, () -> (View) new SolicitudfechaView().getView());
        addViewFactory(VISITAS_SOL_TURNO, () -> (View) new SolicitudturnoView().getView());
        addViewFactory(VISITAS_SOL_CAUSA, () -> (View) new SolicitudcausaView().getView());
        addViewFactory(VISITAS_SOL_PROMOCION, () -> (View) new SolicitudpromocionView().getView());
        addViewFactory(VISITAS_SOL_OBSERVACION, () -> (View) new SolicitudobservacionView().getView());
        addViewFactory(VISITAS_SOL_CONFIRMACION, () -> (View) new SolicitudconfirmacionView().getView());
        addViewFactory(SETTINGS_VIEW, () -> (View) new SettingsView().getView());

        /* ========================== */
        /* Cabecera del Menu Lateral  */
        /* ========================== */
        NavigationDrawer drawer = new NavigationDrawer();

        NavigationDrawer.Header header = new NavigationDrawer.Header("Registro de Visitas",
                "Laboratorios Szama",
                new Avatar(21, new Image(RegistroDeVisitas.class.getResourceAsStream("/icon.png"))));
        drawer.setHeader(header);

        /* ========================== */
        /* Establecer el Menu Lateral */
        /* ========================== */
        final Item MenuPpalItem = new Item("Menu Ppal", MaterialDesignIcon.HOME.graphic());

        MenuPpalItem.visibleProperty().bind(userLogged);

        final Item DermatologosItem = new Item("Dermatologos", MaterialDesignIcon.FACE.graphic());

        DermatologosItem.visibleProperty().bind(userLogged);

        final Item FarmaciasItem = new Item("Farmacia", MaterialDesignIcon.BLOCK.graphic());

        FarmaciasItem.visibleProperty().bind(userLogged);

        final Item SalirItem = new Item("Salir", MaterialDesignIcon.EXIT_TO_APP.graphic());

        final Item ConfiguradorItem = new Item("Settings", MaterialDesignIcon.SETTINGS.graphic());

        drawer.getItems().addAll(MenuPpalItem, DermatologosItem, FarmaciasItem, ConfiguradorItem, SalirItem);

        drawer.selectedItemProperty().addListener((obs, oldItem, newItem) -> {

            hideLayer(MENU_LAYER);

            if (newItem.equals(MenuPpalItem)) {
                switchView(MENU_VIEW);
            }

            if (newItem.equals(DermatologosItem)) {
                switchView(VISITAS_DERMATOLOGOS_VIEW);
            }

            if (newItem.equals(FarmaciasItem)) {
                switchView(VISITAS_DERMATOLOGOS_VIEW);
            }

            if (newItem.equals(SalirItem)) {

                Platform.exit();

            }

            if (newItem.equals(ConfiguradorItem)) {
                switchView(SETTINGS_VIEW);
            }

        });

        /* ========================== */
        /* Creacion del Menu          */
        /* ========================== */
        addLayerFactory(MENU_LAYER, () -> new SidePopupView(drawer));

        this.viewProperty().addListener((obs, oldItem, newItem) -> {

            if (newItem.getName().compareTo(MENU_VIEW) == 0) {
                setUserLogged(true);
            }

        });

        setUserLogged(false);

    }

    @Override
    public void postInit(Scene scene) {

        Swatch.BLUE.assignTo(scene);

        scene.getStylesheets().add(RegistroDeVisitas.class.getResource("style.css").toExternalForm());
        ((Stage) scene.getWindow()).getIcons().add(new Image(RegistroDeVisitas.class.getResourceAsStream("/icon.png")));
    }

    public void setUserLogged(Boolean value) {
        userLogged.set(value);
    }

}
