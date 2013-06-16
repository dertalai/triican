package triican.jsf;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.primefaces.model.DualListModel;
import triican.ejb.LocalizacionFacade;
import triican.ejb.TipoRecursoFacade;
import triican.entity.Localizacion;
import triican.entity.TipoRecurso;
import triican.jsf.util.JsfUtil;

/**
 * Clase encargada de controlar el CRUD de localizaciones.
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Named(value = "localizacionControl")
@SessionScoped
public class LocalizacionControl implements Serializable {
    private final ResourceBundle bundle = ResourceBundle.getBundle("/messages");
    private final static Logger log = Logger.getLogger(LocalizacionControl.class.getName()); 
    @EJB
    private LocalizacionFacade localizacionFacade;
    @EJB
    private TipoRecursoFacade recursoFacade;
    
    private Localizacion localizacion;
    private List<Localizacion> localizaciones;
    private List<TipoRecurso> recursos;
    private DualListModel<Localizacion> localizacionesPick;
    private DualListModel<TipoRecurso> recursosPick;
    /**
     * Creates a new instance of LocalizacionControl
     */
    public LocalizacionControl() {
    }
    
    public String guardaLocalizacion() {
        log.info("\nguardaLocalizacion() " + localizacionesPick.getTarget().size());
        
        if(getLocalizacion().getId() == null) {
            localizacionFacade.create(localizacion);
        }

        List<Localizacion> conexiones = localizacionesPick.getTarget();
        localizacion.getLocalizacionesHijo().retainAll(conexiones);
        localizacion.getLocalizacionesHijo().addAll(conexiones);
        // Recorremos los padres antiguos para borrar como hijo
        for(Localizacion lPadre : localizacion.getLocalizacionesPadre()) {
            lPadre.getLocalizacionesHijo().remove(localizacion);
            localizacionFacade.edit(lPadre);
        }
        
        // Guardamos los recursos
        List<TipoRecurso> tRecursos = recursosPick.getTarget();
        localizacion.getRecursos().retainAll(tRecursos);
        localizacion.getRecursos().addAll(tRecursos);

        localizacionFacade.edit(localizacion);
        
        JsfUtil.addSuccessMessage("cambiosGuardados");
        
        return "localizaciones";
    }

    public String creaLocalizacion() {
        resetEntities();
        return "localizacion";
    }
    
    public String editaLocalizacion(Localizacion loc) {
        log.info("editaLocalizacion(" + loc + ")");
        localizacion = loc;
        
        return "localizacion";
    }
    
    public String borraLocalizacion(Localizacion loc) {
        log.info("borraLocalizacion(" + loc + ")");
        if(loc != null && loc.getId() != null) {
            try {
                localizacionFacade.remove(loc);
                JsfUtil.addSuccessMessage("borradoCorrecto");
//            } catch(ViolaRestriccionExcepcion e) {
//                JsfUtil.addErrorMessage("localizacionEnUso");
            } catch(Throwable t) {
                JsfUtil.addErrorMessage("errorBorrar");
            }
        }

        return "localizaciones";
    }
    
    public List<Localizacion> getLocalizaciones() {
        localizaciones = localizacionFacade.findAll();
        
        // No queremos que aparezca la propia localización ni sus conexiones
        if(getLocalizacion() != null) {
            localizaciones.remove(localizacion);
            localizaciones.removeAll(localizacion.getLocalizaciones());
        }
        
        return localizaciones;
    }

    public List<TipoRecurso> getRecursos() {
        recursos = recursoFacade.findAll();
        
        if(localizacion != null) {
            recursos.removeAll(localizacion.getRecursos());
        }
        return recursos;
    }

    public String cancela() {
        log.info("cancela()");
        
        return "localizaciones";
    }
    
    public Localizacion getLocalizacion() {
        if(localizacion == null) {
            localizacion = new Localizacion();
        }
        return localizacion;
    }

    public void setLocalizacion(Localizacion localizacion) {
        this.localizacion = localizacion;
    }

    public DualListModel<Localizacion> getLocalizacionesPick() {
        if(localizacionesPick == null) {
            localizacionesPick = new DualListModel<Localizacion>(
                    new ArrayList<Localizacion>(), new ArrayList<Localizacion>());
            localizacionesPick.getSource().addAll(getLocalizaciones());
            localizacionesPick.getTarget().addAll(getLocalizacion().getLocalizaciones());            
        }
        
        return localizacionesPick;
    }

    public void show() {
        log.info("\nshow()");
        for(Localizacion l : localizacionesPick.getTarget()) {
            log.info("\nTarget: " + l + "-" + l.getNombre());
        }
        for(Localizacion l : localizacionesPick.getSource()) {
            log.info("\nSource: " + l + "-" + l.getNombre());
        }
        
    }
    public void setLocalizacionesPick(DualListModel<Localizacion> localizacionesPick) {
        this.localizacionesPick = localizacionesPick;
    }
    
    public DualListModel<TipoRecurso> getRecursosPick() {
        recursosPick = new DualListModel<TipoRecurso>(
                new ArrayList(), new ArrayList());
        recursosPick.getSource().addAll(getRecursos());
        recursosPick.getTarget().addAll(getLocalizacion().getRecursos());
        
        return recursosPick;
    }

    public void setRecursosPick(DualListModel<TipoRecurso> recursosPick) {
        this.recursosPick = recursosPick;
    }

    public void resetEntities() {
        localizacion = null;
        localizacionesPick = null;
    }
    
}
