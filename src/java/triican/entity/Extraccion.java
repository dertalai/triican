package triican.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Entity
@Table(uniqueConstraints = 
        @UniqueConstraint(columnNames = {"tipoHerramienta_id", "tipoRecurso_id"}))
public class Extraccion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    @Range(min = 0)
    private Float tasa;
    
    @ManyToOne
    @JoinColumn(name = "tipoHerramienta_id")
    private TipoHerramienta tipoHerramienta;
        
    @ManyToOne(optional = false)
    @JoinColumn(name = "tipoRecurso_id")
    private TipoRecurso tipoRecurso;


    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getTasa() {
        return tasa;
    }

    public void setTasa(Float tasa) {
        this.tasa = tasa;
    }

    public TipoHerramienta getTipoHerramienta() {
        return tipoHerramienta;
    }

    public void setTipoHerramienta(TipoHerramienta tipoHerramienta) {
        this.tipoHerramienta = tipoHerramienta;
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

//    @Override
//    public boolean equals(Object object) {
//        // TODO: Warning - this method won't work in the case the id fields are not set
//        if (!(object instanceof Extraccion)) {
//            return false;
//        }
//        Extraccion other = (Extraccion) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Extraccion)) {
            return false;
        }
        Extraccion other = (Extraccion) object;
        if(this.tipoRecurso == null && other.tipoRecurso != null || 
                this.tipoRecurso != null && !this.tipoRecurso.equals(other.tipoRecurso)) {
            return false;
        }
        if(this.tipoHerramienta == null && other.tipoHerramienta != null || 
                this.tipoHerramienta != null && !this.tipoHerramienta.equals(other.tipoHerramienta)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "triican.entity.Extraccion[ id=" + id + " tasa=" + tasa + " "
                + tipoHerramienta + " " + tipoRecurso + " ]";
    }

}
