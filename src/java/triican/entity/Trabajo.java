package triican.entity;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Entity
public class Trabajo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotNull
    @Range(min = 0)
    private Float completado;
    
    @ManyToOne(optional = false)
    private TipoTrabajo tipoTrabajo;

    @ManyToOne(optional = false)
    private Personaje personajeCreador;
    
    @OneToMany(mappedBy = "trabajoParticipa")
    private Set<Personaje> personajesParticipantes;
    
    @ManyToOne(optional = false)
    Localizacion localizacion;
    
    @OneToMany(mappedBy = "trabajo")
    private Set<Elemento> elementos;

    //Tipos especiales tomar camino y extraer recurso
    @ManyToOne
    Localizacion localizacionDestino;

    @ManyToOne
    TipoRecurso tipoRecurso;
    
    
    
    
    
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getCompletado() {
        return completado;
    }

    public void setCompletado(Float completado) {
        this.completado = completado;
    }

    public TipoTrabajo getTipoTrabajo() {
        return tipoTrabajo;
    }

    public void setTipoTrabajo(TipoTrabajo tipoTrabajo) {
        this.tipoTrabajo = tipoTrabajo;
    }

    public Personaje getPersonajeCreador() {
        return personajeCreador;
    }

    public void setPersonajeCreador(Personaje personajeCreador) {
        this.personajeCreador = personajeCreador;
    }

    public Set<Personaje> getPersonajesParticipantes() {
        return personajesParticipantes;
    }

    public void setPersonajesParticipantes(Set<Personaje> personajesParticipantes) {
        this.personajesParticipantes = personajesParticipantes;
    }

    public Localizacion getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(Localizacion localizacion) {
        this.localizacion = localizacion;
    }

    public Set<Elemento> getElementos() {
        return elementos;
    }

    public void setElementos(Set<Elemento> elementos) {
        this.elementos = elementos;
    }

    public Localizacion getLocalizacionDestino() {
        return localizacionDestino;
    }

    public void setLocalizacionDestino(Localizacion localizacionDestino) {
        this.localizacionDestino = localizacionDestino;
    }

    public TipoRecurso getTipoRecurso() {
        return tipoRecurso;
    }

    public void setTipoRecurso(TipoRecurso tipoRecurso) {
        this.tipoRecurso = tipoRecurso;
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
        if (!(object instanceof Trabajo)) {
            return false;
        }
        Trabajo other = (Trabajo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "triican.entity.Trabajo[ id=" + id + " ]";
    }
    
}
