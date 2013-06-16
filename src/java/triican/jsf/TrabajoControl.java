package triican.jsf;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.primefaces.model.DualListModel;
import triican.ejb.ConjuntoHabilidadesFacade;
import triican.ejb.TipoElementoFacade;
import triican.ejb.TipoHerramientaFacade;
import triican.ejb.TipoMaterialFacade;
import triican.ejb.TipoTrabajoFacade;
import triican.ejb.UsoMaterialFacade;
import triican.entity.ConjuntoHabilidades;
import triican.entity.TipoElemento;
import triican.entity.TipoHerramienta;
import triican.entity.TipoMaterial;
import triican.entity.TipoTrabajo;
import triican.entity.UsoMaterial;
import triican.jsf.util.JsfUtil;

/**
 * Clase encargada de controlar el CRUD de tipos de trabajo.
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Named(value = "trabajoControl")
@SessionScoped
public class TrabajoControl implements Serializable {
    private final static Logger log = Logger.getLogger(TrabajoControl.class.getName());
    private final ResourceBundle bundle = ResourceBundle.getBundle("/messages");

    @EJB
    private TipoTrabajoFacade trabajoFacade;
    @EJB
    private TipoMaterialFacade materialFacade;
    @EJB
    private TipoHerramientaFacade herramientaFacade;
    @EJB
    private ConjuntoHabilidadesFacade habilidadesFacade;
    @EJB
    private UsoMaterialFacade usoMaterialFacade;
    @EJB
    private TipoElementoFacade elementoFacade;
    
    private TipoTrabajo trabajo;
    private List<TipoTrabajo> trabajos;
    private ConjuntoHabilidades conjuntoHabilidades;
    
    private List<TipoHerramienta> herramientas;
    private DualListModel<TipoHerramienta> herramientasPick;
    
    private List<UsoMaterial> usosMaterial;
    private Set<TipoMaterial> materiales;
    private TipoMaterial seleccionMaterial;
    
    private TipoElemento elemento;
    private List<TipoElemento> elementos;
    
    
    /**
     * Creates a new instance of TrabajoControl
     */
    public TrabajoControl() {
    }

    
    
    public String guardaTrabajo() {
        log.info("guardaTrabajo()");
        
        if(getConjuntoHabilidades().getId() == null) {
            try {
                habilidadesFacade.create(conjuntoHabilidades);
            } catch (Exception e) {
                JsfUtil.addErrorMessage(e, bundle.getString("errorPersistencia"));
                return null;
            }
        } else {
            habilidadesFacade.edit(conjuntoHabilidades);
        }
        trabajo.setConjuntoHabilidades(conjuntoHabilidades);
        
        trabajo.setTipoElemento(elemento);
        
        trabajo.getHerramientas().clear();
        trabajo.getHerramientas().addAll(herramientasPick.getTarget());
        
        if(getTrabajo().getId() == null) {
            try {
                trabajoFacade.create(getTrabajo());
            } catch (Exception e) {
                JsfUtil.addErrorMessage(e, bundle.getString("errorPersistencia"));
                return null;
            }
        } else {
            trabajo = trabajoFacade.edit(trabajo);
        }

        // Borramos los usoMaterial que se pide eliminar. añadimos los nuevos
        Set<UsoMaterial> usosBorrar = new HashSet<UsoMaterial>();
        usosBorrar.addAll(usoMaterialFacade.findByTipoTrabajo(trabajo));
        usosBorrar.removeAll(getUsosMaterial());
        for(UsoMaterial u : usosBorrar) {
            try {
                usoMaterialFacade.remove(u);
            } catch(Throwable t) {
                JsfUtil.addErrorMessage("errorPersistencia");
            }
        }
        //Añadimos o editamos el resto
        for(UsoMaterial u : getUsosMaterial()) {
            u.setTipoTrabajo(trabajo);
            usoMaterialFacade.edit(u);
        }
        
        JsfUtil.addSuccessMessage("cambiosGuardados");
        
        return "trabajos";
    }

    public String cancela() {
        log.info("cancela()");
//        resetEntities();
        
        return "trabajos";
    }
    
    public String creaTrabajo() {
        resetEntities();
        return "trabajo";
    }
    
    public String editaTrabajo(TipoTrabajo tipo) {
        log.info("editaTrabajo()");
        
        resetEntities();
        
        trabajo = tipo;
        conjuntoHabilidades = tipo.getConjuntoHabilidades();
        elemento = tipo.getTipoElemento();
        usosMaterial = new ArrayList<UsoMaterial>(tipo.getUsosMaterial().size());
        usosMaterial.addAll(tipo.getUsosMaterial());
        
        return "trabajo";
    }
    
    public String borraTrabajo(TipoTrabajo tipo) {
        log.info("borraTrabajo(" + tipo + ")");
        try {
            trabajoFacade.remove(tipo);
            JsfUtil.addSuccessMessage("borradoCorrecto");
//        } catch(ViolaRestriccionExcepcion e) {
//            JsfUtil.addErrorMessage("tipoTrabajoEnUso");
        } catch (Throwable t) {
            JsfUtil.addErrorMessage("errorBorrar");
        }
        
        return null;
    }
    
    public String anadirMaterial() {
        log.info("anadirMaterial()");
        
        UsoMaterial usoMaterial = new UsoMaterial();
        usoMaterial.setCantidad(1);
        usoMaterial.setTipoMaterial(getSeleccionMaterial());
//        usoMaterial.setTipoTrabajo(getTrabajo());
        
        getUsosMaterial().add(usoMaterial);
        
        return null;
    }
    
    public String borraMaterial(UsoMaterial usoMaterial) {
        log.info("borraMaterial");
        getUsosMaterial().remove(usoMaterial);
        
        return null;
    }
    
    
    
    public TipoTrabajo getTrabajo() {
        if(trabajo == null) {
            trabajo = new TipoTrabajo();
        }
        
        return trabajo;
    }

    public void setTrabajo(TipoTrabajo trabajo) {
        this.trabajo = trabajo;
    }

    public List<TipoTrabajo> getTrabajos() {
        trabajos = trabajoFacade.findAll();
        
        return trabajos;
    }

    public void setTrabajos(List<TipoTrabajo> trabajos) {
        this.trabajos = trabajos;
    }

    public ConjuntoHabilidades getConjuntoHabilidades() {
        if(conjuntoHabilidades == null) {
            conjuntoHabilidades = new ConjuntoHabilidades();
        }
        
        return conjuntoHabilidades;
    }

    public void setConjuntoHabilidades(ConjuntoHabilidades conjuntoHabilidades) {
        this.conjuntoHabilidades = conjuntoHabilidades;
    }

    public List<TipoHerramienta> getHerramientas() {
        herramientas = herramientaFacade.findAll();
        
        if(getTrabajo().getId() != null) {
            herramientas.removeAll(getTrabajo().getHerramientas());
        }
        
        return herramientas;
    }

    public void setHerramientas(List<TipoHerramienta> herramientas) {
        this.herramientas = herramientas;
    }

    public DualListModel<TipoHerramienta> getHerramientasPick() {
        herramientasPick = new DualListModel<TipoHerramienta>(
                new ArrayList<TipoHerramienta>(), new ArrayList<TipoHerramienta>());
        
        herramientasPick.getSource().addAll(getHerramientas());
        herramientasPick.getTarget().addAll(getTrabajo().getHerramientas());
        return herramientasPick;
    }

    public void setHerramientasPick(DualListModel<TipoHerramienta> herramientasPick) {
        this.herramientasPick = herramientasPick;
    }

    public List<UsoMaterial> getUsosMaterial() {
        if(usosMaterial == null) {
            usosMaterial = new ArrayList<UsoMaterial>(0);
        }
        
        return usosMaterial;
    }

    public void setUsosMaterial(List<UsoMaterial> usosMaterial) {
        this.usosMaterial = usosMaterial;
    }

    public Set<TipoMaterial> getMateriales() {
        if(materiales == null) {
            materiales = new HashSet();
        }
        // Añadimos todos los materiales de la BBDD
        materiales.addAll(materialFacade.findAll());
        // Le quitamos los ya mostrados en el formulario
        for(UsoMaterial u : getUsosMaterial()) {
            materiales.remove(u.getTipoMaterial());
        }
        
        return materiales;
    }

    public void setMateriales(Set<TipoMaterial> materiales) {
        this.materiales = materiales;
    }

    public TipoMaterial getSeleccionMaterial() {
        return seleccionMaterial;
    }

    public void setSeleccionMaterial(TipoMaterial seleccionMaterial) {
        this.seleccionMaterial = seleccionMaterial;
    }

    public TipoElemento getElemento() {
        return elemento;
    }

    public void setElemento(TipoElemento elemento) {
        this.elemento = elemento;
    }

    public List<TipoElemento> getElementos() {
        elementos = elementoFacade.findAll();
        
        return elementos;
    }

    public void setElementos(List<TipoElemento> elementos) {
        this.elementos = elementos;
    }

    public void resetEntities() {
        trabajo = null;
        conjuntoHabilidades = null;
        
        elemento = null;

        usosMaterial = null;
        seleccionMaterial = null;
    }
    
    
}
