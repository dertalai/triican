package triican.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;


/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Entity
public class TipoTrabajo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Column(unique = true)
    private String nombre;
    @NotNull
    @Range(min = 0)
    private Float duracion;
    @NotNull
    @Range(min = 1)
    private Integer cantidadSalida;
    
    
    @OneToOne(optional = false)
    private ConjuntoHabilidades conjuntoHabilidades;

    @OneToMany(fetch = FetchType.EAGER,
            mappedBy = "tipoTrabajo")
    private Set<UsoMaterial> usosMaterial;
    
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<TipoHerramienta> herramientas;
    
    @ManyToOne(optional = false)
    private TipoElemento tipoElemento;
    

    
    
    public TipoTrabajo() {
        duracion = new Float(0);
        cantidadSalida = 0;
        
        usosMaterial = new HashSet<UsoMaterial>(0);
        herramientas = new HashSet<TipoHerramienta>(0);
        
        conjuntoHabilidades = new ConjuntoHabilidades();
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

    public Float getDuracion() {
        return duracion;
    }

    public void setDuracion(Float duracion) {
        this.duracion = duracion;
    }

    public Integer getCantidadSalida() {
        return cantidadSalida;
    }

    public void setCantidadSalida(Integer cantidadSalida) {
        this.cantidadSalida = cantidadSalida;
    }

    public Set<UsoMaterial> getUsosMaterial() {
        return usosMaterial;
    }

    public void setUsosMaterial(Set<UsoMaterial> usosMaterial) {
        this.usosMaterial = usosMaterial;
    }


    public Set<TipoHerramienta> getHerramientas() {
        return herramientas;
    }

    public void setHerramientas(Set<TipoHerramienta> herramientas) {
        this.herramientas = herramientas;
    }

    public TipoElemento getTipoElemento() {
        return tipoElemento;
    }

    public void setTipoElemento(TipoElemento tipoElemento) {
        this.tipoElemento = tipoElemento;
    }

    public ConjuntoHabilidades getConjuntoHabilidades() {
        return conjuntoHabilidades;
    }

    public void setConjuntoHabilidades(ConjuntoHabilidades conjuntoHabilidades) {
        this.conjuntoHabilidades = conjuntoHabilidades;
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
        if (!(object instanceof TipoTrabajo)) {
            return false;
        }
        TipoTrabajo other = (TipoTrabajo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "triican.entity.TipoTrabajo[ id=" + id + " ]";
    }
    
}
