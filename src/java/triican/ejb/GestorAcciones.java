package triican.ejb;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timer;
import org.apache.commons.lang3.StringUtils;
import triican.entity.Accion;
import triican.entity.Elemento;
import triican.entity.Localizacion;
import triican.entity.Nota;
import triican.entity.Personaje;
import triican.entity.Sistema;
import triican.entity.Trabajo;
import triican.entity.UsoMaterial;
import triican.exception.ExcepcionBBDD;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Singleton
@Lock(LockType.READ)
public class GestorAcciones {
    private final static Logger log = Logger.getLogger(GestorAcciones.class.getName());
    private final ResourceBundle bundle = ResourceBundle.getBundle("/messages");

    @EJB private AccionFacade accionFacade;
    @EJB private TipoElementoFacade tipoElementoFacade;
    @EJB private TipoTrabajoFacade tipoTrabajoFacade;
    @EJB private NotaFacade notaFacade;
    @EJB private PersonajeFacade personajeFacade;
    @EJB private ElementoFacade elementoFacade;
    @EJB private SistemaFacade sistemaFacade;
    @EJB private TrabajoFacade trabajoFacade;
    @EJB private PerfilFacade perfilFacade;
    
    private static Sistema sistema;
    
    @PostConstruct
    public void retomaTurno() {
        // Forzamos la creación de los tipos básicos si no lo están ya
        tipoElementoFacade.getTipoNota();
        tipoTrabajoFacade.getTipoTomarCamino();
        tipoTrabajoFacade.getTipoExtraerRecurso();
        perfilFacade.gestorMapa();
        perfilFacade.gestorTipos();
        
        sistema = sistemaFacade.findTurnoActual();
        
        if(sistema == null) {
            sistema = new Sistema();
            sistema.setEstado(Sistema.Estado.ACTUAL);
            sistema.setTurno(0L);
            sistema.setTurnosPorDia((short) 12);
            
            sistemaFacade.create(sistema);
        } else {
            sistema = sistemaFacade.edit(sistema);
        }
        
        log.info("Retomado turno " + sistema.getTurno());
    }
    
    
    @Schedule(hour = "*", minute = "*", second = "5", persistent = false)
    @Lock(LockType.WRITE)
    public void pasaTurno(Timer timer) {
        sistema.setEstado(Sistema.Estado.GUARDANDO);
        sistema = sistemaFacade.edit(sistema);
        
        List<Personaje> personajes = personajeFacade.findAll();
        Set<Trabajo> trabajosComprobar = new HashSet<Trabajo>();
        // Para cada personaje, procesa sólo su última acción lenta
        for(Personaje p : personajeFacade.findAll()) {
            Trabajo trabajo = p.getTrabajoParticipa();
            if(trabajo == null) {
                // Nada
            } else if(trabajo.getTipoTrabajo().equals(tipoTrabajoFacade.getTipoTomarCamino())) {
                cambiarLocalizacion(trabajo);
            } else if(trabajo.getTipoTrabajo().equals(tipoTrabajoFacade.getTipoExtraerRecurso())) {
                avanzarExtraccionRecurso(trabajo);
            } else {
                avanzarTrabajo(trabajo, p);
                trabajosComprobar.add(trabajo);
                trabajo = trabajoFacade.edit(trabajo);
            }
        }
        
        // Comprueba finalización trabajos
        for(Trabajo t : trabajosComprobar) {
            comprobarTerminadoTrabajo(t);
        }
        
        // Restaura cansancio de todos los personajes
        for(Personaje p : personajes) {
            p.restauraCansancio();
            p = personajeFacade.edit(p);
        }
        
        
        Sistema proximoSistema = new Sistema();
        proximoSistema.setEstado(Sistema.Estado.ACTUAL);
        proximoSistema.setTurno(sistema.getTurno() + 1);
        proximoSistema.setTurnosPorDia(sistema.getTurnosPorDia());
        
        sistema.setEstado(Sistema.Estado.PROCESADO);

        sistemaFacade.create(proximoSistema);
        
        sistema = proximoSistema;
        
        log.info("Timer: " + timer.getTimeRemaining() + " - Pasando al turno " + sistema.getTurno());
    }

    public Long getTurno() {
        return sistema.getTurno();
    }

    @Asynchronous
    public Future<Boolean> procesa(Accion accion) throws InterruptedException {
        if(accion == null) {
            return new AsyncResult<Boolean>(Boolean.FALSE);
        }
        
        if(accion.getActor() == null || accion.getActor().getLocalizacion() == null) {
            marcaErronea(accion);
            return new AsyncResult<Boolean>(Boolean.FALSE);
        }
        
        log.info("procesando acción...");
        switch(accion.getTipo()) {
            case CREAR_NOTA:
                gestionaCrearNota(accion);
                break;
            case MOVER_ELEMENTO:
                gestionaMoverElemento(accion);
                break;
            case HABLAR:
                gestionaHablar(accion);
                break;
            case ATACAR:
                gestionaAtacar(accion);
                break;
            case COMENZAR_TRABAJO:
                gestionarComenzarTrabajo(accion);
                break;
            case PARTICIPAR_TRABAJO:
                gestinarParticiparTrabajo(accion);
                break;
            case EXTRAER_RECURSO:
                gestionarExtraerRecurso(accion);
                break;
            case PARTIR_CAMINO:
                gestionarPartirCamino(accion);
                break;
            case USAR_ELEMENTO:
                break;
            default:
                log.info("default");
        }
        
        if(accion.getEstado() == Accion.Estado.ERROR) {
            accionFacade.edit(accion);
            return new AsyncResult<Boolean>(Boolean.FALSE);
        }
        
        log.info("procesada correctamente acción " + accion + " - " + accion.getTipo().toString() +
                " - " + accion.getCreado());
        
        return new AsyncResult<Boolean>(Boolean.TRUE);
    }
    
    private void gestionaHablar(Accion accion) {
        log.info("gestionaHablar()");
        
        /* validaciones */
        if(StringUtils.isBlank(accion.getTexto())) {
            log.warning("texto vacío");
            marcaErronea(accion);
            return;
        }
        if(accion.getReceptor() != null &&
                accion.getReceptor().getLocalizacion() != null &&
                ! accion.getReceptor().getLocalizacion().equals(accion.getActor().getLocalizacion())
                ) {
            log.warning("receptor falseado");
            marcaErronea(accion);
            return;
        }
        
        accion.setEstado(Accion.Estado.PROCESADA);

        if(accion.getReceptor() == null) {
            lanzarSuceso(accion, "suceso.hablarTodos", new Object[] {accion.getActor().getNombre(),
                accion.getTexto()});
        } else {
            lanzarSuceso(accion, "suceso.susurrar", new Object[] {accion.getActor().getNombre(),
                accion.getReceptor().getNombre(), accion.getTexto()});
        }
        
    }

    private void gestionaCrearNota(Accion accion) {
        log.info("gestionaCrearNota()");
        
        if(StringUtils.isBlank(accion.getTexto())) {
            marcaErronea(accion);
            return;
        }
        Nota nota = new Nota();
        nota.setTitulo(accion.getTitulo());
        nota.setTexto(accion.getTexto());
        nota.setContenedor(accion.getActor());
        nota.setTipoElemento(tipoElementoFacade.getTipoNota());
        
        notaFacade.create(nota);
        
        accion.setEstado(Accion.Estado.PROCESADA);
        
        lanzarSuceso(accion, "suceso.crearNota", null);
    }

    private void gestionaMoverElemento(Accion accion) {
        log.info("gestionaMoverElemento()");
        
        /* validaciones */
        if(accion.getCantidad() == null || accion.getElemento() == null) {
            log.warning("cantidad o elemento null");
            marcaErronea(accion);
            return;
        }
        if(accion.getCantidad() < 1 ||
                accion.getCantidad() > accion.getElemento().getCantidad()) {
            log.warning("cantidad incorrecta");
            marcaErronea(accion);
            return;
        }
        if(accion.getElemento().getPersonaje() != null &&
                ! accion.getElemento().getPersonaje().equals(accion.getActor()) )  {
            log.warning("actor falseado");
            marcaErronea(accion);
            return;
        }
        if(accion.getElemento().getLocalizacion() != null &&
                ! accion.getElemento().getLocalizacion().equals(accion.getActor().getLocalizacion()) ) {
            log.warning("localizacion falseada");
            marcaErronea(accion);
            return;
        }
        if(accion.getReceptor() != null && (
                accion.getElemento().getPersonaje() == null ||
                ! accion.getElemento().getPersonaje().getLocalizacion().equals(accion.getReceptor().getLocalizacion())
                )) {
            log.warning("receptor falseado");
            marcaErronea(accion);
            return;
        }
        
        /* Movimiento */
        if(accion.getElemento().getPersonaje() != null && accion.getReceptor() == null) {
            gestionaSoltarElemento(accion);
        } else if(accion.getElemento().getLocalizacion() != null &&
                accion.getElemento().getLocalizacion().equals(accion.getActor().getLocalizacion()) ) {
            gestionaCogerElemento(accion);
        } else if(accion.getElemento().getPersonaje() != null &&
                accion.getElemento().getPersonaje().getLocalizacion().equals(
                accion.getReceptor().getLocalizacion()) ) {
            gestionaDarElemento(accion);
        } else {
            marcaErronea(accion);
        }
    }

    private void gestionaSoltarElemento(Accion accion) {
        log.info("gestionaSoltarElemento()");
        mueveElementos(accion.getActor().getLocalizacion(), accion.getElemento(), accion.getCantidad());
        
        accion.setEstado(Accion.Estado.PROCESADA);

        lanzarSuceso(accion, "suceso.soltar", new Object[] {accion.getActor().getNombre(),
            accion.getElemento().getTipoElemento().getNombre(), accion.getCantidad()});
    }

    private void gestionaCogerElemento(Accion accion) {
        log.info("gestionaCogerElemento()");
        mueveElementos(accion.getActor(), accion.getElemento(), accion.getCantidad());

        accion.setEstado(Accion.Estado.PROCESADA);

        lanzarSuceso(accion, "suceso.coger", new Object[] {accion.getActor().getNombre(),
            accion.getElemento().getTipoElemento().getNombre(), accion.getCantidad()});
    }

    private void gestionaDarElemento(Accion accion) {
        log.info("gestionaDarElemento()");
        mueveElementos(accion.getReceptor(), accion.getElemento(), accion.getCantidad());

        accion.setEstado(Accion.Estado.PROCESADA);

        lanzarSuceso(accion, "suceso.dar", new Object[] {accion.getActor().getNombre(),
            accion.getReceptor().getNombre(), accion.getElemento().getTipoElemento().getNombre(), accion.getCantidad()});
    }
    
    private void gestionaAtacar(Accion accion) {
        log.info("gestionaAtacar()");
        /*validaciones*/
        if(accion.getReceptor() == null) {
            log.warning("sin receptor");
            marcaErronea(accion);
            return;
        }
        if( ! accion.getReceptor().getLocalizacion().equals(
                accion.getActor().getLocalizacion())) {
            log.warning("receptor falseado");
            marcaErronea(accion);
            return;
        }
        if(accion.getElemento() != null && ! accion.getElemento().getPersonaje().equals(
                accion.getActor())) {
            log.warning("arma falseada");
        }
        
        Float danno;
        if(accion.getElemento() == null) {
            danno = 0.01f;
        } else {
            danno = 0.01f * accion.getElemento().getTipoElemento().getTipoArma().getAtaque();
        }
        danno *= 1 - accion.getActor().getCansancio();
        
        accion.getReceptor().setSalud(accion.getReceptor().getSalud() - danno);
        if(accion.getReceptor().getSalud() < 0) {
            accion.getReceptor().setSalud(0f);
        }
        
        Float cansancio = 0.4f;
        accion.getActor().setCansancio(accion.getActor().getCansancio() + cansancio);
        if(accion.getActor().getCansancio() > 1) {
            accion.getActor().setCansancio(1f);
        }
        
        personajeFacade.edit(accion.getReceptor());
        personajeFacade.edit(accion.getActor());
        
        String stringArma = accion.getElemento() == null ?
                bundle.getString("punnos") :
                accion.getElemento().getTipoElemento().getNombre();

        accion.setEstado(Accion.Estado.PROCESADA);

        lanzarSuceso(accion, "suceso.atacar", new Object[] {accion.getActor().getNombre(), 
                accion.getReceptor().getNombre(), stringArma, danno * 100});
    }
    
    private void gestionarComenzarTrabajo(Accion accion) {
        log.info("gestionarComenzarTrabajo()");
        
        /* validaciones */
        if(accion.getTipoTrabajo() == null) {
            log.warning("tipo de trabajo nulo");
            marcaErronea(accion);
            return;
        }
        
        Trabajo trabajo = new Trabajo();
        trabajo.setTipoTrabajo(accion.getTipoTrabajo());
        trabajo.setCompletado(0F);
        trabajo.setPersonajeCreador(accion.getActor());
        trabajo.setLocalizacion(accion.getActor().getLocalizacion());
        
        trabajoFacade.create(trabajo);
        
        if( ! aportaMateriales(accion.getActor(), trabajo)) {
            trabajoFacade.remove(trabajo);
            marcaErronea(accion);
            return;
        }
        
        accion.setEstado(Accion.Estado.PROCESADA);

        lanzarSuceso(accion, "suceso.comenzarTrabajo", new Object[] {
            accion.getActor().getNombre(), accion.getTipoTrabajo().getNombre()});
    }
    
    private void gestinarParticiparTrabajo(Accion accion) {
        log.info("gestionarParticiparTrabajo(" + accion + ")");
        
        accion.getActor().setTrabajoParticipa(accion.getTrabajo());
        personajeFacade.edit(accion.getActor());
        
        accion.setEstado(Accion.Estado.PROCESADA);
        
        if(accion.getTrabajo() == null) {
            lanzarSuceso(accion, "suceso.dejarParticiparTrabajo", new Object[] {
               accion.getActor().getNombre() });
        } else {
            lanzarSuceso(accion, "suceso.participarTrabajo", new Object[] {
                accion.getActor().getNombre(), accion.getTrabajo().getTipoTrabajo().getNombre() });
        }
    }
    
    private void gestionarExtraerRecurso(Accion accion) {
        log.info("gestionarExtraerRecurso()");
        
        /* validaciones */
        if(accion.getTipoRecurso() == null) {
            log.warning("tipo de recurso nulo");
            marcaErronea(accion);
            return;
        }
        // TODO la localización tiene ese tipo de recurso
        
        Trabajo trabajo = new Trabajo();
        trabajo.setTipoTrabajo(tipoTrabajoFacade.getTipoExtraerRecurso());
        trabajo.setTipoRecurso(accion.getTipoRecurso());
        trabajo.setCompletado(0F);
        trabajo.setPersonajeCreador(accion.getActor());
        trabajo.setLocalizacion(accion.getActor().getLocalizacion());

        trabajoFacade.create(trabajo);
        
        if( ! aportaMateriales(accion.getActor(), trabajo)) {
            trabajoFacade.remove(trabajo);
            marcaErronea(accion);
            return;
        }
        
        accion.setEstado(Accion.Estado.PROCESADA);
        
        lanzarSuceso(accion, "suceso.comenzarExtraerRecurso", new Object[] {
            accion.getActor().getNombre(), accion.getTipoRecurso().getTipoElemento().getNombre()});

    }
    
    private void gestionarPartirCamino(Accion accion) {
        log.info("gestionarPartirCamino()");
        
        /*validaciones*/
        if( ! accion.getActor().getLocalizacion().getLocalizaciones()
                .contains(accion.getLocalizacion())) {
            log.warning("camino falseado");
            marcaErronea(accion);
            return;
        }
        
        Trabajo trabajo = new Trabajo();
        trabajo.setTipoTrabajo(tipoTrabajoFacade.getTipoTomarCamino());
        trabajo.setLocalizacionDestino(accion.getLocalizacion());
        trabajo.setCompletado(0F);
        trabajo.setPersonajeCreador(accion.getActor());
        trabajo.setLocalizacion(accion.getActor().getLocalizacion());

        trabajoFacade.create(trabajo);

        accion.getActor().setTrabajoParticipa(trabajo);
        personajeFacade.edit(accion.getActor());
        
        lanzarSuceso(accion, "suceso.dirigeACamino", new Object[] {
            accion.getActor().getNombre(), accion.getLocalizacion().getNombre()});
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private void marcaErronea(Accion accion) {
        log.info("marcarErronea()");
        
        accion.setEstado(Accion.Estado.ERROR);
    }

    private String geti18nMsg(String key, Object[] params) {
        String msg = bundle.getString(key);
        MessageFormat messageFormat = new MessageFormat(msg);
        return messageFormat.format(params == null ? new Object[0] : params);
    }
    
    private void lanzarSuceso(Accion accion, String key, Object[] params) {
        String texto;
        String keyActor = key + ".actor";
        String keyReceptor = key + ".receptor";
        
        if(bundle.containsKey(keyActor)) {
            texto = geti18nMsg(keyActor, params);
            annadirSuceso(accion.getActor(), texto);
        }
        
        if(bundle.containsKey(keyReceptor)) {
            texto = geti18nMsg(keyReceptor, params);
            annadirSuceso(accion.getReceptor(), texto);
        }
        
        if(bundle.containsKey(key)) {
            texto = geti18nMsg(key, params);
            for(Personaje p : accion.getActor().getLocalizacion().getPersonajes()) {
                if(! p.equals(accion.getActor()) && ! p.equals(accion.getReceptor())) {
                    annadirSuceso(p, texto);
                }
            }
        }
    }
    
    private void lanzarSuceso(Trabajo trabajo, String key, Object[] params) {
        String texto;
        String keyActor = key + ".actor";
        
        if(bundle.containsKey(keyActor)) {
            if(trabajo.getLocalizacion().equals(trabajo.getPersonajeCreador().getLocalizacion())) {
                texto = geti18nMsg(keyActor, params);
                annadirSuceso(trabajo.getPersonajeCreador(), texto);
            }
        }        

        Set<Personaje> personajesNotificar = new HashSet<Personaje>();
        personajesNotificar.addAll(personajeFacade.findByTrabajo(trabajo));
        personajesNotificar.remove(trabajo.getPersonajeCreador());
        
        if(bundle.containsKey(key)) {
            texto = geti18nMsg(key, params);
            for(Personaje p : personajesNotificar) {
                annadirSuceso(p, texto);
            }
        }
    }

    private void lanzarSuceso(Personaje actor, String key, Object[] params) {
        String texto;
        String keyActor = key + ".actor";
        
        if(bundle.containsKey(keyActor)) {
            texto = geti18nMsg(keyActor, params);
            annadirSuceso(actor, texto);
        }
        
        if(bundle.containsKey(key)) {
            texto = geti18nMsg(key, params);
            for(Personaje p : actor.getLocalizacion().getPersonajes()) {
                if(! p.equals(actor)) {
                    annadirSuceso(p, texto);
                }
            }
        }
    }

    private void annadirSuceso(Personaje personaje, String texto) {
        log.info("annadirSuceso("+ personaje.getNombre() + ", " + texto + ")");
        StringBuilder sucesos = new StringBuilder();
        sucesos.append(getTurno() / getTurnosDia())
                .append("-").append(getTurno() % getTurnosDia())
                .append(": ").append(texto).append("\n");
        if(StringUtils.isNotBlank(personaje.getSucesos())) {
            sucesos.append(personaje.getSucesos());
        }
        personaje.setSucesos(sucesos.toString());

        personajeFacade.edit(personaje);
    }

    public int getTurnosDia() {
        return sistema.getTurnosPorDia();
    }

    private boolean aportaMateriales(Personaje personaje, Trabajo trabajo) {
        List<Elemento> inventario = elementoFacade.findByPersonaje(personaje);
        
        //Bucle comprobación previa
        for(UsoMaterial u : trabajo.getTipoTrabajo().getUsosMaterial()) {
            boolean continuar = false;
            for(Elemento e : inventario) {
                if(e.getTipoElemento().equals(u.getTipoMaterial().getTipoElemento()) &&
                        e.getCantidad() >= u.getCantidad()) {
                    continuar = true;
                    break;
                }
            }
            if( ! continuar) {
                return false;
            }
        }
        
        //Bucle movimiento de materiales
        for(UsoMaterial u : trabajo.getTipoTrabajo().getUsosMaterial()) {
            for(Elemento e : inventario) {
                if(e.getTipoElemento().equals(u.getTipoMaterial().getTipoElemento()) &&
                        e.getCantidad() >= u.getCantidad()) {
                    mueveElementos(trabajo, e, u.getCantidad());
                    break;
                }
            }
        }
        
        return true;
    }

    private void mueveElementos(Object destino, Elemento elemento, Integer cantidad) {
        if(elemento.getCantidad() == cantidad) {
            elemento.setContenedor(destino);
            
            elementoFacade.edit(elemento);
        } else {
            elemento.setCantidad(elemento.getCantidad() - cantidad);
            
            Elemento nuevo = new Elemento();
            nuevo.setCantidad(cantidad);
            nuevo.setTipoElemento(elemento.getTipoElemento());
            nuevo.setContenedor(destino);
            
            elementoFacade.edit(elemento);
            elementoFacade.create(nuevo);
        }
        
        try {
            elementoFacade.amontona(destino, elemento.getTipoElemento());
        } catch (ExcepcionBBDD e) {
            log.log(Level.SEVERE, "error al amontonar elementos", e);
        }
    }

    
    
//    private Trabajo avanzarParticiparTrabajo(Accion accion) {
//        Float actual = accion.getTrabajo().getCompletado();
//        Float duracion = accion.getTrabajo().getTipoTrabajo().getDuracion();
//        
//        actual += 1f;
//        accion.getTrabajo().setCompletado((Math.min(actual, duracion)));
//        
//        return accion.getTrabajo();
//    }

    private void comprobarTerminadoTrabajo(Trabajo trabajo) {
        if(trabajo.getCompletado() < trabajo.getTipoTrabajo().getDuracion()) {
            return;
        }
        
        //Trabajo completado
        Elemento nuevo = new Elemento();
        nuevo.setTipoElemento(trabajo.getTipoTrabajo().getTipoElemento());
        nuevo.setCantidad(trabajo.getTipoTrabajo().getCantidadSalida());
        
        Object contenedor;
        if(trabajo.getLocalizacion().equals(trabajo.getPersonajeCreador().getLocalizacion())) {
            // si está presente, va al creador del trabajo
            contenedor = trabajo.getPersonajeCreador();
        } else {
            // cae al suelo
            contenedor = trabajo.getLocalizacion();
        }
        nuevo.setContenedor(contenedor);
        elementoFacade.create(nuevo);
        try {
            elementoFacade.amontona(contenedor, nuevo.getTipoElemento());
        } catch (ExcepcionBBDD ex) {
            Logger.getLogger(GestorAcciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        lanzarSuceso(trabajo, "suceso.trabajoTerminado", new Object[] {
            trabajo.getPersonajeCreador().getNombre(), trabajo.getTipoTrabajo().getNombre()
            });
        
        //Eliminar trabajo, sus referencias a los participantes y sus materiales
        for(Elemento e : elementoFacade.findByTrabajo(trabajo)) {
            elementoFacade.remove(e);
        }
        for(Personaje p : personajeFacade.findByTrabajo(trabajo)) {
            p.setTrabajoParticipa(null);
        }
        for(Accion a : accionFacade.findByTrabajo(trabajo)) {
            accionFacade.remove(a);
        }
        trabajoFacade.remove(trabajo);
        
    }

    private void avanzarTrabajo(Trabajo trabajo, Personaje personaje) {
        Float actual = trabajo.getCompletado();
        Float duracion = trabajo.getTipoTrabajo().getDuracion();
        
        actual += 1f;
        trabajo.setCompletado((Math.min(actual, duracion)));
    }

    private void avanzarExtraccionRecurso(Trabajo trabajo) {
        //TODO No se tienen en cuenta tasas de extracción 1 recurso por persona-turno
        final int CANTIDAD = 1;
        Elemento nuevo = new Elemento();
        nuevo.setTipoElemento(trabajo.getTipoRecurso().getTipoElemento());
        nuevo.setCantidad(CANTIDAD);
        
        Object contenedor;
        if(trabajo.getLocalizacion().equals(trabajo.getPersonajeCreador().getLocalizacion())) {
            // si está presente, va al creador del trabajo
            contenedor = trabajo.getPersonajeCreador();
        } else {
            // cae al suelo
            contenedor = trabajo.getLocalizacion();
        }
        nuevo.setContenedor(contenedor);
        elementoFacade.create(nuevo);

        try {
            elementoFacade.amontona(trabajo.getPersonajeCreador(), trabajo.getTipoRecurso().getTipoElemento());
        } catch (ExcepcionBBDD ex) {
            Logger.getLogger(GestorAcciones.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        lanzarSuceso(trabajo, "suceso.recursoExtraido", new Object[] {
            trabajo.getPersonajeCreador().getNombre(), CANTIDAD,
            trabajo.getTipoRecurso().getTipoElemento().getNombre()
        });

    }

    private void cambiarLocalizacion(Trabajo trabajo) {
        Personaje actor = personajeFacade.edit(trabajo.getPersonajeCreador());
        
        Localizacion origen = actor.getLocalizacion();
        
        lanzarSuceso(actor, "suceso.abandonaLugar", new Object[] {
            actor.getNombre(), origen.getNombre(),
            trabajo.getLocalizacionDestino().getNombre()});
        
        actor.setLocalizacion(trabajo.getLocalizacionDestino());
        
        lanzarSuceso(actor, "suceso.llegaALugar", new Object[] {
            actor.getNombre(), origen.getNombre(),
            trabajo.getLocalizacionDestino().getNombre() });
        
        actor.setTrabajoParticipa(null);
        actor.setTrabajosCreador(null);

        trabajo = trabajoFacade.edit(trabajo);
        actor = personajeFacade.edit(actor);
        trabajoFacade.remove(trabajo);
    }

}
