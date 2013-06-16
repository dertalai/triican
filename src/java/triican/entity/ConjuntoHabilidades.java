package triican.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Entity
public class ConjuntoHabilidades implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Range(min = 0, max = 100)
    private Float fuerza;
    @NotNull
    @Range(min = 0, max = 100)
    private Float agilidad;
    @NotNull
    @Range(min = 0, max = 100)
    private Float destreza;
    @NotNull
    @Range(min = 0, max = 100)
    private Float percepcion;
    
    @OneToOne(mappedBy = "conjuntoHabilidades")
    private Personaje personaje;
    
    @OneToOne(mappedBy = "conjuntoHabilidades")
    private TipoTrabajo tipoTrabajo;
    
    
    public ConjuntoHabilidades() {
        fuerza = new Float(0);
        agilidad = new Float(0);
        destreza = new Float(0);
        percepcion = new Float(0);
    }
    
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getFuerza() {
        return fuerza;
    }

    public void setFuerza(Float fuerza) {
        this.fuerza = fuerza;
    }

    public Float getAgilidad() {
        return agilidad;
    }

    public void setAgilidad(Float agilidad) {
        this.agilidad = agilidad;
    }

    public Float getDestreza() {
        return destreza;
    }

    public void setDestreza(Float destreza) {
        this.destreza = destreza;
    }

    public Float getPercepcion() {
        return percepcion;
    }

    public void setPercepcion(Float percepcion) {
        this.percepcion = percepcion;
    }

    public Personaje getPersonaje() {
        return personaje;
    }

    public void setPersonaje(Personaje personaje) {
        this.personaje = personaje;
    }

    public TipoTrabajo getTipoTrabajo() {
        return tipoTrabajo;
    }

    public void setTipoTrabajo(TipoTrabajo tipoTrabajo) {
        this.tipoTrabajo = tipoTrabajo;
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
        if (!(object instanceof ConjuntoHabilidades)) {
            return false;
        }
        ConjuntoHabilidades other = (ConjuntoHabilidades) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "triican.entity.ConjuntoHabilidades[ id=" + id + " ]";
    }
    
}
