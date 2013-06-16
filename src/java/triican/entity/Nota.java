package triican.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Entity
public class Nota extends Elemento implements Serializable {
    public static final int TITULO_LENGTH = 128;
    public static final int TEXTO_LENGTH = 32 * 1024;
    @NotNull
    @Length(max = TITULO_LENGTH)
    private String titulo;
    
    @NotEmpty
    @Length(max = TEXTO_LENGTH)
    private String texto;

    public Nota() {
        cantidad = new Integer(1);
    }
    
    
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
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
        if (!(object instanceof Nota)) {
            return false;
        }
        Nota other = (Nota) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "triican.entity.Nota[ id=" + id + " ]";
    }
    
}
