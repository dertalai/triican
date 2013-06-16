package triican.jsf;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;

import javax.inject.Named;
import triican.ejb.AbstractFacade;
import triican.ejb.ExtraccionFacade;
import triican.ejb.TipoAlimentoFacade;
import triican.ejb.TipoArmaFacade;
import triican.ejb.TipoElementoFacade;
import triican.ejb.TipoHerramientaFacade;
import triican.ejb.TipoMaterialFacade;
import triican.ejb.TipoRecursoFacade;
import triican.entity.Extraccion;
import triican.entity.TipoAlimento;
import triican.entity.TipoArma;
import triican.entity.TipoElemento;
import triican.entity.TipoHerramienta;
import triican.entity.TipoMaterial;
import triican.entity.TipoRecurso;
import triican.entityInterface.SubtipoElementoInterface;
import triican.jsf.util.JsfUtil;

/**
 * Clase encargada de controlar el CRUD de tipos de elemento.
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Named(value = "elementoControl")
@SessionScoped
public class ElementoControl implements Serializable {

    public enum Categoria {RECURSO, HERRAMIENTA, ALIMENTO, MATERIAL, ARMA};
    
    private final static Logger log = Logger.getLogger(ElementoControl.class.getName());
    private final ResourceBundle bundle = ResourceBundle.getBundle("/messages");
    
    @EJB
    private TipoElementoFacade tipoElementoFacade;
    @EJB
    private TipoRecursoFacade recursoFacade;
    @EJB
    private TipoHerramientaFacade herramientaFacade;
    @EJB
    private TipoAlimentoFacade alimentoFacade;
    @EJB
    private TipoMaterialFacade materialFacade;
    @EJB
    private TipoArmaFacade armaFacade;
    @EJB
    private ExtraccionFacade extraccionFacade;

    private TipoElemento elemento;
    private TipoRecurso recurso;
    private TipoHerramienta herramienta;
    private TipoAlimento alimento;
    private TipoMaterial material;
    private TipoArma arma;
    
    private List<TipoElemento> elementos;
    final private Set<Categoria> categorias;
    private Set<String> seleccionCategorias;
    
    //Como recurso
    private List<TipoHerramienta> herramientas;
    private TipoHerramienta seleccionHerramienta;
    private Set<Extraccion> extracciones;
    
    
    
    public ElementoControl() {
        this.categorias = EnumSet.allOf(Categoria.class);
    }
    
    public String guardaElemento() {
        log.info("guardaElemento()");
        if(getElemento().getId() == null) {
            try {
                tipoElementoFacade.create(getElemento());
            } catch (Exception e) {
                JsfUtil.addErrorMessage(e, bundle.getString("errorPersistencia"));
                return null;
            }
        }
        
        guardaSubtipo(Categoria.MATERIAL, materialFacade, getMaterial());
        guardaSubtipo(Categoria.RECURSO, recursoFacade, getRecurso());
        guardaSubtipo(Categoria.ALIMENTO, alimentoFacade, getAlimento());
        guardaSubtipo(Categoria.HERRAMIENTA, herramientaFacade, getHerramienta());
        guardaSubtipo(Categoria.ARMA, armaFacade, getArma());

        elemento = tipoElementoFacade.edit(getElemento());
        
        JsfUtil.addSuccessMessage("cambiosGuardados");
        
        return "elementos";
    }

    public String cancela() {
        log.info("cancela()");
//        resetEntities();
        
        return "elementos";
    }
    
    public String editaElemento(TipoElemento tipo) {
        log.info("editaElemento()");
        resetEntities();
        
        elemento = tipo;
        
        material = elemento.getTipoMaterial();
        recurso = elemento.getTipoRecurso();
        alimento = elemento.getTipoAlimento();
        herramienta = elemento.getTipoHerramienta();
        arma = elemento.getTipoArma();
        leeSubtipo(Categoria.MATERIAL, elemento.getTipoMaterial());
        leeSubtipo(Categoria.RECURSO, elemento.getTipoRecurso());
        leeSubtipo(Categoria.ALIMENTO, elemento.getTipoAlimento());
        leeSubtipo(Categoria.HERRAMIENTA, elemento.getTipoHerramienta());
        leeSubtipo(Categoria.ARMA, elemento.getTipoArma());
        
        return "elemento";
    }
    
    public String creaTipoElemento() {
        resetEntities();
        
        return "elemento";
    }
    
    public String borraElemento(TipoElemento tipo) {
        log.info("borraElemento(" + tipo + ")");
        try {
            tipoElementoFacade.remove(tipo);
            JsfUtil.addSuccessMessage("borradoCorrecto");
        } catch(Throwable t) {
//            JsfUtil.addErrorMessage("tipoElementoEnUso");
//        } catch (ExcepcionBBDD e) {
            JsfUtil.addErrorMessage("errorBorrar");
        }
        
        
        return "elementos";
    }
    
    public String anadirRecurso() {
        log.info("\nanadirRecurso()");
        
        // Si se intenta meter un valor que no corresponde, salimos sin añadir
        if( ! getHerramientas().contains(seleccionHerramienta)) {
            //TODO mostrar error?
            return null;
        }
        
        Extraccion extraccion = new Extraccion();
        
        extraccion.setTasa(1f);
        extraccion.setTipoHerramienta(seleccionHerramienta);
        if(getRecurso().getId() != null) {
            extraccion.setTipoRecurso(getRecurso());
        }
        log.info("\nsize antes: " + getExtracciones().size());
        getExtracciones().add(extraccion);
        log.info("\nsize después: " + getExtracciones().size());
        
        return null;
    }
    
    public String borraRecurso(Extraccion extraccion) {
        log.info("\nborraRecurso()");
        getExtracciones().remove(extraccion);
        
        return null;
    }   
    
    public void categoriaListener() {
        log.info("categoriaListener()");
        getElemento().setTipoArma(null);
        getElemento().setTipoAlimento(null);
        getElemento().setTipoRecurso(null);
    }
    
    
    public TipoElemento getElemento() {
        if(elemento == null) {
            elemento = new TipoElemento();
        }
        
        return elemento;
    }

    public void setElemento(TipoElemento elemento) {
        this.elemento = elemento;
    }

    public List<TipoElemento> getElementos() {
        elementos = tipoElementoFacade.findAll();
        
        return elementos;
    }

    public TipoRecurso getRecurso() {
        if(recurso == null) {
            recurso = new TipoRecurso();
        }
        
        return recurso;
    }

    public void setRecurso(TipoRecurso recurso) {
        this.recurso = recurso;
    }

    public TipoHerramienta getHerramienta() {
        if(herramienta == null) {
            herramienta = new TipoHerramienta();
        }
        
        return herramienta;
    }

    public void setHerramienta(TipoHerramienta herramienta) {
        this.herramienta = herramienta;
    }

    public TipoAlimento getAlimento() {
        if(alimento == null) {
            alimento = new TipoAlimento();
        }
        
        return alimento;
    }

    public void setAlimento(TipoAlimento alimento) {
        this.alimento = alimento;
    }

    public TipoArma getArma() {
        if(arma == null) {
            arma = new TipoArma();
        }
        
        return arma;
    }

    public void setArma(TipoArma arma) {
        this.arma = arma;
    }

    public Set<Categoria> getCategorias() {
        return categorias;
    }

    public Set<String> getSeleccionCategorias() {
        if(seleccionCategorias == null) {
            seleccionCategorias = new HashSet(5);
        }
        return seleccionCategorias;
    }

    public void setSeleccionCategorias(Set<String> seleccionCategorias) {
        this.seleccionCategorias = seleccionCategorias;
    }

    public List<TipoHerramienta> getHerramientas() {
        herramientas = herramientaFacade.findAll();
        //Añadimos la posibilidad de que no se use herramienta
        herramientas.add(0, null);
        
        // Si el propio elemento es una herramienta, lo quitamos
        if(getElemento().getTipoHerramienta() != null) {
            herramientas.remove(getElemento().getTipoHerramienta());
        }
        
        // Si ya se usa en una de las Extraccion, lo quitamos
        for(Extraccion e : getExtracciones()) {
            herramientas.remove(e.getTipoHerramienta());
        }
        
        return herramientas;
    }

    public void setHerramientas(List<TipoHerramienta> herramientas) {
        this.herramientas = herramientas;
    }

    public TipoHerramienta getSeleccionHerramienta() {
        return seleccionHerramienta;
    }

    public void setSeleccionHerramienta(TipoHerramienta seleccionHerramienta) {
        this.seleccionHerramienta = seleccionHerramienta;
    }

    public Set<Extraccion> getExtracciones() {
        if(extracciones == null) {
            extracciones = new HashSet<Extraccion>(0);
        }
        
        return extracciones;
    }

    public void setExtracciones(Set<Extraccion> extracciones) {
        this.extracciones = extracciones;
    }

    public TipoMaterial getMaterial() {
        if(material == null) {
            material = new TipoMaterial();
        }
        
        return material;
    }

    public void setMaterial(TipoMaterial material) {
        this.material = material;
    }

    public void resetEntities() {
        elemento = null;
        material = null;
        arma = null;
        recurso = null;
        alimento = null;
        herramienta = null;
        
        elementos = null;
        seleccionCategorias = null;
        
        herramientas = null;
        seleccionHerramienta = null;
        extracciones = null;
    }

    private void guardaSubtipo(Categoria categoria, AbstractFacade facade,
            SubtipoElementoInterface entity) {
        log.info("\nguardaSubtipo()" + categoria.toString());
        if(getSeleccionCategorias().contains(categoria.toString())) {
            log.info("\nok");
            entity.setTipoElemento(getElemento());
            entity = (SubtipoElementoInterface) facade.edit(entity);

            
            if(categoria == Categoria.RECURSO) {
                TipoRecurso tRecurso = (TipoRecurso) entity;
                
                Set<Extraccion> extraccionesBorrar = new HashSet<Extraccion>(0);
                extraccionesBorrar.addAll(extraccionFacade.findByTipoRecurso(tRecurso));
                extraccionesBorrar.removeAll(getExtracciones());
                for(Extraccion e : extraccionesBorrar) {
                    try {
                        extraccionFacade.remove(e);
                    } catch (Throwable t) {
                        JsfUtil.addErrorMessage("errorPersistencia");
                    }
                }
                
                for(Extraccion e : getExtracciones()) {
                    e.setTipoRecurso(tRecurso);
                    extraccionFacade.edit(e);
                }
            }
        } else if(entity.getId() != null) {
            try {
                facade.remove(entity);
            } catch (Throwable t) {
                JsfUtil.addErrorMessage("errorPersistencia");
            }
        }
    }

    private void leeSubtipo(Categoria categoria, SubtipoElementoInterface entity) {
        if(entity != null) {
            getSeleccionCategorias().add(categoria.toString());
            if(categoria == Categoria.RECURSO) {
                List<Extraccion> lista = extraccionFacade.findByTipoRecurso((TipoRecurso) entity);
                log.info("\nleyendo subtipo RECURSO. Lista:");
                getExtracciones().clear();
                getExtracciones().addAll(lista);
                
                for(Extraccion e : getExtracciones()) {
                    log.info("\n" + e.toString());
                }
            }
        } else {
            getSeleccionCategorias().remove(categoria.toString());
        }
    }
    
    
    
}
