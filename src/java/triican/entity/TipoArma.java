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
public class TipoArma implements Serializable, SubtipoElementoInterface {
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;

    @MapsId
    @OneToOne
    private TipoElemento tipoElemento;
    
    @NotNull
    @Range(min = 0)
    private Float ataque;
    @NotNull
    @Range(min = 0)
    private Float defensa;

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

    public Float getAtaque() {
        return ataque;
    }

    public void setAtaque(Float ataque) {
        this.ataque = ataque;
    }

    public Float getDefensa() {
        return defensa;
    }

    public void setDefensa(Float defensa) {
        this.defensa = defensa;
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
        if (!(object instanceof TipoArma)) {
            return false;
        }
        TipoArma other = (TipoArma) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "triican.entity.TipoArma[ id=" + id + " ]";
    }
    
}
