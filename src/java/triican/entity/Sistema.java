package triican.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Entity
public class Sistema implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum Estado {ACTUAL, GUARDANDO, PROCESADO, ERROR};
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotNull
    private Estado estado;
    @NotNull
    @Column(unique = true)
    private Long turno;
    @NotNull
    private Short turnosPorDia;
    
    @NotNull
    @Version
    private Timestamp creado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    
    public Long getTurno() {
        return turno;
    }

    public void setTurno(Long turno) {
        this.turno = turno;
    }

    public Short getTurnosPorDia() {
        return turnosPorDia;
    }

    public void setTurnosPorDia(Short turnosPorDia) {
        this.turnosPorDia = turnosPorDia;
    }

    public Timestamp getCreado() {
        return creado;
    }

    public void setCreado(Timestamp creado) {
        this.creado = creado;
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
        if (!(object instanceof Sistema)) {
            return false;
        }
        Sistema other = (Sistema) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "triican.entity.Sistema[ id=" + id + " ]";
    }
    
}
