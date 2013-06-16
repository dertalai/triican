package triican.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Entity
public class Accion implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final Tipo[] LENTAS = {
        Tipo.PARTIR_CAMINO, 
        Tipo.PARTICIPAR_TRABAJO, 
        Tipo.EXTRAER_RECURSO,
    };
    static {
        Arrays.sort(LENTAS);
    }
    
    public enum Tipo {
        HABLAR,
        MOVER_ELEMENTO,
        ATACAR,
        PARTIR_CAMINO, // lento (bloquea)
        COMENZAR_TRABAJO,
        PARTICIPAR_TRABAJO, // lento (bloquea)
        EXTRAER_RECURSO, // lento (bloquea)
        USAR_ELEMENTO,
        CREAR_NOTA,
    }
    
    public enum Estado {
        PENDIENTE,
        PROCESADA,
        ERROR,
    }
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Tipo tipo;
    @NotNull
    private Estado estado;
    @Length(max = Nota.TEXTO_LENGTH)
    private String texto;
    @Length(max = Nota.TITULO_LENGTH)
    private String titulo;
    @Range(min = 1)
    private Integer cantidad;
    
    @NotNull
    @ManyToOne
    private Personaje actor;
    
    @ManyToOne
    private Personaje receptor;
    
    @ManyToOne
    private Elemento elemento;
    
    @ManyToOne
    private Localizacion localizacion;
    
    @ManyToOne
    private TipoTrabajo tipoTrabajo;
    
    @ManyToOne
    private TipoRecurso tipoRecurso;

    @ManyToOne
    private Trabajo trabajo;
    
    @Version
    private Timestamp creado;
    
    
    
    public Accion() {
    }
    
    public Accion(Personaje actor) {
        this.actor = actor;
        this.estado = Estado.PENDIENTE;
    }
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Personaje getActor() {
        return actor;
    }
    
    public Personaje getReceptor() {
        return receptor;
    }

    public void setReceptor(Personaje receptor) {
        this.receptor = receptor;
    }

    public Elemento getElemento() {
        return elemento;
    }

    public void setElemento(Elemento elemento) {
        this.elemento = elemento;
    }

    public Localizacion getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(Localizacion localizacion) {
        this.localizacion = localizacion;
    }

    public TipoTrabajo getTipoTrabajo() {
        return tipoTrabajo;
    }

    public void setTipoTrabajo(TipoTrabajo tipoTrabajo) {
        this.tipoTrabajo = tipoTrabajo;
    }

    public TipoRecurso getTipoRecurso() {
        return tipoRecurso;
    }

    public void setTipoRecurso(TipoRecurso tipoRecurso) {
        this.tipoRecurso = tipoRecurso;
    }

    public Trabajo getTrabajo() {
        return trabajo;
    }

    public void setTrabajo(Trabajo trabajo) {
        this.trabajo = trabajo;
    }

    public Timestamp getCreado() {
        return creado;
    }
    
    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
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
        if (!(object instanceof Accion)) {
            return false;
        }
        Accion other = (Accion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "triican.entity.Accion[ id=" + id + " ]";
    }
    
}
