package triican.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Entity
public class Localizacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotEmpty
    @Column(unique = true)
    private String nombre;
    @NotNull
    private Float x;
    @NotNull
    private Float y;
    
    @OneToMany(mappedBy = "localizacion", fetch = FetchType.EAGER)
    private Set<Personaje> personajes;
    
    @OneToMany(mappedBy = "localizacion")
    private Set<Elemento> elementos;
    
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<TipoRecurso> recursos;
    
    @Transient
    private Set<Localizacion> localizaciones;
    
    @ManyToMany(mappedBy = "localizacionesHijo", fetch = FetchType.EAGER)
    private Set<Localizacion> localizacionesPadre;
    
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Localizacion> localizacionesHijo;

    @OneToMany(mappedBy = "localizacion", fetch = FetchType.EAGER)
    private Set<Trabajo> trabajos;
    
    @Transient
    private Map<Localizacion, String> direcciones;
    
    
    
    public Localizacion() {
        localizaciones = new HashSet<Localizacion>(0);
        recursos = new HashSet<TipoRecurso>(0);
        localizacionesPadre = new HashSet<Localizacion>(0);
        localizacionesHijo = new HashSet<Localizacion>(0);
    }
    
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Set<Personaje> getPersonajes() {
        return personajes;
    }

    public void setPersonajes(Set<Personaje> personajes) {
        this.personajes = personajes;
    }

    public Set<Elemento> getElementos() {
        if(elementos == null) {
            elementos = new HashSet<Elemento>();
        }
        return elementos;
    }

    public void setElementos(Set<Elemento> elementos) {
        this.elementos = elementos;
    }

    public Set<TipoRecurso> getRecursos() {
        return recursos;
    }

    public void setRecursos(Set<TipoRecurso> recursos) {
        this.recursos = recursos;
    }

    public Set<Localizacion> getLocalizaciones() {
        localizaciones.clear();
        localizaciones.addAll(localizacionesPadre);
        localizaciones.addAll(localizacionesHijo);
        
        return localizaciones;
    }

    public Set<Localizacion> getLocalizacionesPadre() {
        return localizacionesPadre;
    }

    public Set<Localizacion> getLocalizacionesHijo() {
        return localizacionesHijo;
    }

    public Set<Trabajo> getTrabajos() {
        return trabajos;
    }

    public void setTrabajos(Set<Trabajo> trabajos) {
        this.trabajos = trabajos;
    }

    public enum Direccion {
        E(0), 
        E_NE(1), 
        NE(2), 
        N_NE(3), 
        N(4), 
        N_NO(5),
        NO(6), 
        O_NO(7), 
        O(8), 
        O_SO(9), 
        SO(10), 
        S_SO(11), 
        S(12), 
        S_SE(13), 
        SE(14), 
        E_SE(15), 
        ;
        private int valor;
        private Direccion(int valor) {
            this.valor = valor;
        }
    };
    public String getDireccionHacia(Localizacion camino) {
        final double paso = Math.PI / 16.;
        if(direcciones == null) {
            direcciones = new HashMap<Localizacion, String>(localizaciones.size());
        }
        if(direcciones.containsKey(camino)) {
            return direcciones.get(camino);
        } else if(localizaciones.contains(camino)) {
            // d en el intervalo (-180, 180]
            final double d = Math.atan2(camino.y - y, camino.x - x);
            // l es una transformación al intervalo discreto [0, 16)
            long l = Math.round(16. * (d + 2*Math.PI) / (2*Math.PI)) % 16;
            
            String direccion = null;
            for(Direccion dir : Direccion.values()) {
                if(l == dir.valor ) {
                    direccion = dir.toString();
                    break;
                }
            }
            direcciones.put(camino, direccion);
            
            return direcciones.get(camino);
        }
        
        return null;
        
    }
    
    
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Localizacion)) {
            return false;
        }
        Localizacion other = (Localizacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "triican.entity.Localizacion[ id=" + id + " ]";
    }
    
}
