package triican.entity;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Elemento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;
    
    @NotNull
    @Range(min = 1)
    protected Integer cantidad;
    
    @ManyToOne
    protected Localizacion localizacion;
    
    @ManyToOne
    protected Personaje personaje;
    
    @ManyToOne
    protected Trabajo trabajo;
    
    @ManyToOne(optional = false)
    protected TipoElemento tipoElemento;
    
    @OneToMany(mappedBy = "elemento")
    private Set<Accion> acciones;
    
    
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Localizacion getLocalizacion() {
        return localizacion;
    }

    public void setContenedor(Object contenedor) throws IllegalArgumentException {
        if(contenedor instanceof Localizacion) {
            this.localizacion = (Localizacion) contenedor;
            this.personaje = null;
            this.trabajo = null;
        } else if (contenedor instanceof Personaje) {
            this.localizacion = null;
            this.personaje = (Personaje) contenedor;
            this.trabajo = null;
        } else if (contenedor instanceof Trabajo) {
            this.localizacion = null;
            this.personaje = null;
            this.trabajo = (Trabajo) contenedor;
        } else {
            throw new IllegalArgumentException("El tipo del contenedor debe ser Localizacion, Personaje o Trabajo.");
        }
    }

    public Personaje getPersonaje() {
        return personaje;
    }


    public Trabajo getTrabajo() {
        return trabajo;
    }


    public TipoElemento getTipoElemento() {
        return tipoElemento;
    }

    public void setTipoElemento(TipoElemento tipoElemento) {
        this.tipoElemento = tipoElemento;
    }

    public Set<Accion> getAcciones() {
        return acciones;
    }

    public void setAcciones(Set<Accion> acciones) {
        this.acciones = acciones;
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
        if (!(object instanceof Elemento)) {
            return false;
        }
        Elemento other = (Elemento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "triican.entity.Elemento[ id=" + id + " ]";
    }

    private Exception IllegalArgumentException(String el_tipo_del_contenedor_debe_ser_Localizac) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
