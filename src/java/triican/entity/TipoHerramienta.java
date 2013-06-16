package triican.entity;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import triican.entityInterface.SubtipoElementoInterface;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Entity
public class TipoHerramienta implements Serializable, SubtipoElementoInterface {
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;

    @MapsId
    @OneToOne
    private TipoElemento tipoElemento;

    
    @OneToMany(mappedBy = "tipoHerramienta")
    private Set<Extraccion> extracciones;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoElemento getTipoElemento() {
        return tipoElemento;
    }

    public void setTipoElemento(TipoElemento tipoElemento) {
        this.tipoElemento = tipoElemento;
    }

    public Set<Extraccion> getExtracciones() {
        return extracciones;
    }

    public void setExtracciones(Set<Extraccion> extracciones) {
        this.extracciones = extracciones;
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
        if (!(object instanceof TipoHerramienta)) {
            return false;
        }
        TipoHerramienta other = (TipoHerramienta) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "triican.entity.TipoHerramienta[ id=" + id + " ]";
    }
    
}
