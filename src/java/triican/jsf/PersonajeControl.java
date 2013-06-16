package triican.jsf;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import javax.ejb.EJB;
import triican.ejb.ElementoFacade;
import triican.ejb.GestorAcciones;
import triican.ejb.PersonajeFacade;
import triican.ejb.TipoTrabajoFacade;
import triican.ejb.TrabajoFacade;
import triican.entity.Accion;
import triican.entity.Elemento;
import triican.entity.Localizacion;
import triican.entity.Nota;
import triican.entity.Personaje;
import triican.entity.TipoElemento;
import triican.entity.TipoRecurso;
import triican.entity.TipoTrabajo;
import triican.entity.Trabajo;
import triican.jsf.util.JsfUtil;

/**
 * Clase encargada de recoger las acciones de los jugadores y llevarles la
 * información.
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Named(value = "personajeControl")
@SessionScoped
public class PersonajeControl implements Serializable {
    private final static Logger log = Logger.getLogger(PersonajeControl.class.getName(), "/messages");

    @EJB
    PersonajeFacade personajeFacade;
    @EJB
    GestorAcciones gestorAcciones;
    @EJB
    ElementoFacade elementoFacade;
    @EJB
    TipoTrabajoFacade tipoTrabajoFacade;
    @EJB
    TrabajoFacade trabajoFacade;
    
    private String textoHablar;
    private String textoNota;
    private String tituloNota;
    private Integer cantidad = 1;
    private Personaje personaje;
    private Personaje receptor;
    private Set<Personaje> personajes = new HashSet<Personaje>(0);
    private List<Elemento> inventario;
    private Set<Elemento> armas;
    private Elemento armaSeleccionada;
    private Set<Elemento> elementosSuelo = new HashSet<Elemento>(0);
    private Set<Personaje> participantes;
    private TipoTrabajo tipoTrabajo;
    private List<TipoTrabajo> tiposTrabajo;
    private List<Trabajo> trabajos;
    
    
    public PersonajeControl() {
    }
    
    public String seleccionaPersonaje(Personaje personaje) {
        resetEntities();
        this.personaje = personaje;
        
        return "juego";
    }

    public String habla(Personaje persona) {
        log.info("habla(" + persona + ", " + textoHablar + ")");
        
        Accion accion = new Accion(personaje);
        accion.setTipo(Accion.Tipo.HABLAR);
        accion.setTexto(textoHablar);
        accion.setReceptor(persona);
        
        enviarAccion(accion);
        
        textoHablar = null;
        
        return null;
    }
    
    public String extraeRecurso(TipoRecurso recurso) {
        log.info("extraeRecurso(" + recurso.getTipoElemento().getNombre() + ")");
        
        Accion accion = new Accion(personaje);
        accion.setTipo(Accion.Tipo.EXTRAER_RECURSO);
        accion.setTipoRecurso(recurso);
        accion.setCantidad(cantidad);
        
        enviarAccion(accion);
        
        cantidad = 1;
        
        return null;
    }
    
    public String tomaCamino(Localizacion localizacion) {
        log.info("tomaCamino(" + localizacion.getNombre() + ")");
        
        Accion accion = new Accion(personaje);
        accion.setTipo(Accion.Tipo.PARTIR_CAMINO);
        accion.setLocalizacion(localizacion);
        
        enviarAccion(accion);
        
        return null;
    }
    
    public String ataca(Personaje persona) {
        log.info("ataca(" + persona + ", " + armaSeleccionada + ")");
        
        Accion accion = new Accion(personaje);
        accion.setTipo(Accion.Tipo.ATACAR);
        accion.setReceptor(persona);
        accion.setElemento(armaSeleccionada);
        
        enviarAccion(accion);
        
        return null;
    }
    
    public String sueltaElemento(Elemento elemento) {
        log.info("sueltaElemento(" + elemento.getTipoElemento().getNombre() + "," + cantidad + ")");
        
        Accion accion = new Accion(personaje);
        accion.setTipo(Accion.Tipo.MOVER_ELEMENTO);
        accion.setReceptor(null);
        accion.setElemento(elemento);
        accion.setCantidad(elemento instanceof Nota ? 1 : cantidad);
        
        enviarAccion(accion);
        
        cantidad = 1;
        resetInventario();
        
        return null;
    }
    
    public String daElemento(Elemento elemento) {
        log.info(elemento + ", " + receptor);
        log.info("daElemento(" + receptor.getNombre() + ", " + elemento.getTipoElemento().getNombre() + ""
                + ", " + cantidad + ")");
        
        Accion accion = new Accion(personaje);
        accion.setTipo(Accion.Tipo.MOVER_ELEMENTO);
        accion.setReceptor(receptor);
        accion.setElemento(elemento);
        accion.setCantidad(cantidad);
        
        enviarAccion(accion);

        cantidad = 1;
        resetInventario();        
        return null;
    }
    
    public String cogeElemento(Elemento elemento) {
        log.info("cogeElemento(" + elemento.getTipoElemento().getNombre() + ", " + cantidad + ")");
        
        Accion accion = new Accion(personaje);
        accion.setTipo(Accion.Tipo.MOVER_ELEMENTO);
        accion.setElemento(elemento);
        accion.setCantidad(cantidad);
        
        enviarAccion(accion);
        
        cantidad = 1;
        resetInventario();
        
        return null;
    }
    
    public String participa(Trabajo trabajo) {
        log.info("participa(" + trabajo + ")");
        
        Accion accion = new Accion(personaje);
        accion.setTipo(Accion.Tipo.PARTICIPAR_TRABAJO);
        accion.setTrabajo(trabajo);
        
        enviarAccion(accion);
        
        return null;
    }
    
    public String creaNota() {
        log.info("creaNota(" + textoNota + ")");
        
        Accion accion = new Accion(personaje);
        accion.setTipo(Accion.Tipo.CREAR_NOTA);
        accion.setTitulo(tituloNota);
        accion.setTexto(textoNota);
        
        enviarAccion(accion);
        
        resetInventario();

        return null;
    }
    
    public String creaTrabajo() {
        log.info("creaTrabajo(" + tipoTrabajo + ")");
        
        Accion accion = new Accion(personaje);
        accion.setTipo(Accion.Tipo.COMENZAR_TRABAJO);
        accion.setTipoTrabajo(tipoTrabajo);
        
        enviarAccion(accion);
        
        resetInventario();

        return null;
    }
    
    public void resetEntities() {
        textoHablar = null;
        textoNota = null;
        tituloNota = null;
        cantidad = 1;
        personaje = null;
        personajes.clear();
        inventario = null;
        armas = null;
        armaSeleccionada = null;
    }
    
    public void resetInventario() {
        inventario = null;
        armas = null;
        armaSeleccionada = null;
    }
    
    
    public String getTextoHablar() {
        return textoHablar;
    }

    public void setTextoHablar(String textoHablar) {
        this.textoHablar = textoHablar;
    }

    public String getTextoNota() {
        return textoNota;
    }

    public void setTextoNota(String textoNota) {
        this.textoNota = textoNota;
    }

    public String getTituloNota() {
        return tituloNota;
    }

    public void setTituloNota(String tituloNota) {
        this.tituloNota = tituloNota;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    
    
    public Personaje getPersonaje() {
//        if(personaje != null) { // refrescar
//            personaje = personajeFacade.edit(personaje);
//        }
        return personaje;
    }

    public void setPersonaje(Personaje personaje) {
        this.personaje = personaje;
    }

    public Personaje getReceptor() {
        return receptor;
    }

    public void setReceptor(Personaje receptor) {
        this.receptor = receptor;
    }
    
    public Set<Personaje> getPersonajes() {
        personajes.clear();
        personajes.addAll(personaje.getLocalizacion().getPersonajes());
        personajes.remove(personaje);
        
        return personajes;
    }
    
    public Set<Elemento> getArmas() {
        log.info("getArmas()");
        if(armas == null) {
            armas = new HashSet<Elemento>();

            // Para que aparezca "ninguna"
            armas.add(null);

            for(Elemento e : getInventario()) {
                if(e.getTipoElemento().getTipoArma() != null) {
                    armas.add(e);
                }
            }
        }
        
        return armas;
    }

    public Elemento getArmaSeleccionada() {
        return armaSeleccionada;
    }

    public void setArmaSeleccionada(Elemento armaSeleccionada) {
        this.armaSeleccionada = armaSeleccionada;
    }

    public List<Elemento> getInventario() {
        if(inventario == null) {
            inventario = elementoFacade.findByPersonaje(personaje);
        }

        return inventario;
    }

    public Set<Elemento> getElementosSuelo() {
        elementosSuelo.clear();
        elementosSuelo.addAll(elementoFacade
                .findByLocalizacion(personaje.getLocalizacion()));
        
        return elementosSuelo;
    }
    
    public Set<Personaje> getParticipantes(Trabajo trabajo) {
        if(participantes == null) {
            participantes = new HashSet<Personaje>(0);
        }
        participantes.clear();
        participantes.addAll(personajeFacade.findByTrabajo(trabajo));
        
        return participantes;
    }

    public TipoTrabajo getTipoTrabajo() {
        if(tipoTrabajo == null && ! getTiposTrabajo().isEmpty()) {
            tipoTrabajo = getTiposTrabajo().get(0);
        }
        return tipoTrabajo;
    }

    public void setTipoTrabajo(TipoTrabajo tipoTrabajo) {
        this.tipoTrabajo = tipoTrabajo;
    }

    public List<TipoTrabajo> getTiposTrabajo() {
        if(tiposTrabajo == null) {
            tiposTrabajo = tipoTrabajoFacade.findAll();
        }
        return tiposTrabajo;
    }

    public List<Trabajo> getTrabajos() {
        trabajos = trabajoFacade.findByLocalizacion(personaje.getLocalizacion());
        return trabajos;
    }

    private void enviarAccion(Accion accion) {
        log.info("enviarAccion()");
        Boolean res;
        try {
            Future<Boolean> futureRes = gestorAcciones.procesa(accion);
            
            res = futureRes.get();
        } catch (Throwable t) {
            log.severe(t.getMessage());
            res = null;
        }
        
        if(res == Boolean.TRUE) {
            JsfUtil.addSuccessMessage("accionCorrecta");
        } else {
            JsfUtil.addErrorMessage("errorRealizarAccion");
        }
        log.info("FIN enviarAccion()");
        
    }

    public String muestraDisponible(TipoElemento tipo) {
        for(Elemento e : getInventario()) {
            if(e.getTipoElemento().equals(tipo)) {
                return e.getCantidad().toString();
            }
        }
        return "-";
    }

    
}
