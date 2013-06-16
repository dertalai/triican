package triican.entity;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Entity
public class TipoElemento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @NotEmpty
    @Column(unique = true)
    protected String nombre;
    
    @OneToMany(mappedBy = "tipoElemento")
    protected Set<Elemento> elementos;
    
    @OneToOne(mappedBy = "tipoElemento")
    private TipoMaterial tipoMaterial;
    
    @OneToOne(mappedBy = "tipoElemento")
    private TipoHerramienta tipoHerramienta;
    
    @OneToOne(mappedBy = "tipoElemento")
    private TipoRecurso tipoRecurso;
    
    @OneToOne(mappedBy = "tipoElemento")
    private TipoAlimento tipoAlimento;
    
    @OneToOne(mappedBy = "tipoElemento")
    private TipoArma tipoArma;

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

    public Set<Elemento> getElementos() {
        return elementos;
    }

    public TipoMaterial getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(TipoMaterial tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

    public TipoRecurso getTipoRecurso() {
        return tipoRecurso;
    }

    public void setTipoRecurso(TipoRecurso tipoRecurso) {
        this.tipoRecurso = tipoRecurso;
    }

    public TipoHerramienta getTipoHerramienta() {
        return tipoHerramienta;
    }

    public void setTipoHerramienta(TipoHerramienta tipoHerramienta) {
        this.tipoHerramienta = tipoHerramienta;
    }

    public TipoAlimento getTipoAlimento() {
        return tipoAlimento;
    }

    public void setTipoAlimento(TipoAlimento tipoAlimento) {
        this.tipoAlimento = tipoAlimento;
    }

    public TipoArma getTipoArma() {
        return tipoArma;
    }

    public void setTipoArma(TipoArma tipoArma) {
        this.tipoArma = tipoArma;
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
        if (!(object instanceof TipoElemento)) {
            return false;
        }
        TipoElemento other = (TipoElemento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "triican.entity.TipoElemento[ id=" + id + " ]";
    }
    
}
