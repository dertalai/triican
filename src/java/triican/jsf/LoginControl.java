package triican.jsf;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.math3.distribution.NormalDistribution;
import triican.ejb.ConjuntoHabilidadesFacade;
import triican.ejb.GestorAcciones;
import triican.ejb.LocalizacionFacade;
import triican.ejb.PerfilFacade;
import triican.ejb.PersonajeFacade;
import triican.ejb.UsuarioFacade;
import triican.entity.ConjuntoHabilidades;
import triican.entity.Localizacion;
import triican.entity.Personaje;
import triican.entity.Usuario;
import triican.jsf.util.JsfUtil;

/**
 * Clase encargada de controlar la autenticación del usuario.
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@ManagedBean
@SessionScoped
public class LoginControl implements Serializable {
    private final ResourceBundle bundle = ResourceBundle.getBundle("/messages");
    private final static Logger log = Logger.getLogger(LoginControl.class.getName()); 

    @EJB private PerfilFacade perfilFacade;
    @EJB private UsuarioFacade usuarioFacade;
    @EJB private PersonajeFacade personajeFacade;
    @EJB private LocalizacionFacade localizacionFacade;
    @EJB private ConjuntoHabilidadesFacade habilidadesFacade;
    @EJB private GestorAcciones gestor;

    /**
     * Número máximo de personajes que puede tener un usuario.
     */
    static public int LIMITE_PERSONAJES = 10;
    
    private Usuario usuario;
    private String nombrePersonaje;
    private Boolean usuarioEsGestorMapa;
    private Boolean usuarioEsGestorTipos;
    

    /**
     * Creates a new instance of LoginControl
     */
    public LoginControl() {
    }

    public boolean estaAutenticado() {
        return usuario != null && usuario.getId() != null;
    }
    
//    public String creaUsuarioPrueba() {
//        Perfil perfil = new Perfil();
//        perfil.setNombre("admin");
//        perfilFacade.create(perfil);
//
//        getUsuario().setNombre("prueba");
//        getUsuario().setContrasena("prueba");
//        getUsuario().setPerfiles(new HashSet<Perfil>(0));
//        getUsuario().getPerfiles().procesa(perfil);
//        usuarioFacade.create(getUsuario());
//        
//        return null;
//    }
    
    public String entrar() {
        log.info("entrar(): " + getUsuario().getNombre());
        usuario = usuarioFacade.findByUsuario(getUsuario());

        if(usuario == null) {
            JsfUtil.addErrorMessage(bundle.getString("usuarioContrasennaIncorrectos"));
            return null;
        } else {
            return "menu";
        }
    }
    
    public String creaPersonaje() {
        log.info("crearPersonaje()");
        /*validaciones*/
        if(usuario.getPersonajes().size() >= LIMITE_PERSONAJES) {
            JsfUtil.addErrorMessage(bundle.getString("demasiadosPersonajes"));
            return null;
        }
        List<Localizacion> listaLocalizaciones = localizacionFacade.findAll();
        if(listaLocalizaciones.isEmpty()) {
            JsfUtil.addErrorMessage(bundle.getString("noHayLocalizaciones"));
            return null;
        }

        final double MEDIA = 50;
        final double DESVIACION = 18;
        NormalDistribution distr = new NormalDistribution(50, 18);
        
        ConjuntoHabilidades nuevoConjunto = new ConjuntoHabilidades();
        nuevoConjunto.setFuerza(new Float(distr.sample()));
        nuevoConjunto.setAgilidad(new Float(distr.sample()));
        nuevoConjunto.setDestreza(new Float(distr.sample()));
        nuevoConjunto.setPercepcion(new Float(distr.sample()));
        habilidadesFacade.create(nuevoConjunto);
        
        Personaje nuevoPersonaje = new Personaje();
        nuevoPersonaje.setNombre(getNombrePersonaje());
        nuevoPersonaje.setDiaNacimiento(getDia());
        nuevoPersonaje.setSalud(new Float(1));
        nuevoPersonaje.setCansancio(new Float(0));
        nuevoPersonaje.setUsuario(usuario);
        nuevoPersonaje.setLocalizacion(listaLocalizaciones.get(0));
        nuevoPersonaje.setConjuntoHabilidades(nuevoConjunto);
        personajeFacade.create(nuevoPersonaje);
        
        usuario = usuarioFacade.find(usuario.getId());
        
        return "menu";
    }
            
            
    
    
    public Usuario getUsuario() {
        if(usuario == null) {
            usuario = new Usuario();
        }
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNombrePersonaje() {
        return nombrePersonaje;
    }

    public void setNombrePersonaje(String nombrePersonaje) {
        this.nombrePersonaje = nombrePersonaje;
    }

    
    
    public Short getTurno() {
        return (short) (gestor.getTurno() % gestor.getTurnosDia());
    }

    public Integer getDia() {
        return gestor.getTurno().intValue() / gestor.getTurnosDia();
    }

    public void resetEntities() {
        // Invalida la sesión.
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        
        usuario = null;
        nombrePersonaje = null;
        usuarioEsGestorMapa = null;
        usuarioEsGestorTipos = null;
    }

    public Boolean getUsuarioEsGestorMapa() {
        if(usuarioEsGestorMapa == null) {
            usuarioEsGestorMapa = estaAutenticado() ?
                    usuario.getPerfiles().contains(perfilFacade.gestorMapa()) :
                    false;
        }
        return usuarioEsGestorMapa;
    }

    public Boolean getUsuarioEsGestorTipos() {
        if(usuarioEsGestorTipos == null) {
            usuarioEsGestorTipos = estaAutenticado() ?
                    usuario.getPerfiles().contains(perfilFacade.gestorTipos()) :
                    false;
        }
        return usuarioEsGestorTipos;
    }

}
