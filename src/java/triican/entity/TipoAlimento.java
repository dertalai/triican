package triican.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import triican.entityInterface.SubtipoElementoInterface;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Entity
public class TipoAlimento implements Serializable, SubtipoElementoInterface {
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;

    @MapsId
    @OneToOne
    private TipoElemento tipoElemento;

    @NotNull
    @Range(min = 0)
    private Float saciedad;
    @NotNull
    private Float curacion;
    

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

    public Float getSaciedad() {
        return saciedad;
    }

    public void setSaciedad(Float saciedad) {
        this.saciedad = saciedad;
    }

    public Float getCuracion() {
        return curacion;
    }

    public void setCuracion(Float curacion) {
        this.curacion = curacion;
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
        if (!(object instanceof TipoAlimento)) {
            return false;
        }
        TipoAlimento other = (TipoAlimento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "triican.entity.TipoAlimento[ id=" + id + " ]";
    }
    
}
