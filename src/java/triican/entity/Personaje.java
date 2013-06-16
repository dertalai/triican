package triican.entity;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Entity
public class Personaje implements Serializable {
    private static final long serialVersionUID = 178592268L;
    private static final int SUCESOS_LENGTH = 128 * 1024;
    private static final float TASA_RECUPERACION_CANSANCIO = 0.1f;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotEmpty
    private String nombre;
    @NotNull
    @Range(min = 0)
    private Integer diaNacimiento;
    @NotNull
    @Range(min = 0, max = 1)
    private Float salud;
    @NotNull
    @Range(min = 0, max = 1)
    private Float cansancio;
    
    @ManyToOne(optional = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    private Localizacion localizacion;
    
    @OneToMany(mappedBy = "personaje")
    private Set<Elemento> elementos;
    
    @OneToOne(optional = false)
    private ConjuntoHabilidades conjuntoHabilidades;
    
    @ManyToOne
    private Trabajo trabajoParticipa;
    
    @OneToMany(mappedBy = "personajeCreador")
    private Set<Trabajo> trabajosCreador;
    
    @Length(max = SUCESOS_LENGTH)
    private String sucesos;
    
    @OneToMany(mappedBy = "actor")
    private Set<Accion> acciones;
    
//    @Version
//    Integer version;
    
    @Transient
    String gravatarUrl;

    
    
    
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

    public Integer getDiaNacimiento() {
        return diaNacimiento;
    }

    public void setDiaNacimiento(Integer diaNacimiento) {
        this.diaNacimiento = diaNacimiento;
    }

    public Float getSalud() {
        return salud;
    }

    public void setSalud(Float salud) {
        this.salud = salud;
    }

    public Float getCansancio() {
        return cansancio;
    }

    public void setCansancio(Float cansancio) {
        this.cansancio = cansancio;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Localizacion getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(Localizacion localizacion) {
        this.localizacion = localizacion;
    }

    public Set<Elemento> getElementos() {
        return elementos;
    }

    public void setElementos(Set<Elemento> elementos) {
        this.elementos = elementos;
    }

    public ConjuntoHabilidades getConjuntoHabilidades() {
        return conjuntoHabilidades;
    }

    public void setConjuntoHabilidades(ConjuntoHabilidades conjuntoHabilidades) {
        this.conjuntoHabilidades = conjuntoHabilidades;
    }

    public Trabajo getTrabajoParticipa() {
        return trabajoParticipa;
    }

    public void setTrabajoParticipa(Trabajo trabajoParticipa) {
        this.trabajoParticipa = trabajoParticipa;
    }

    public Set<Trabajo> getTrabajosCreador() {
        return trabajosCreador;
    }

    public void setTrabajosCreador(Set<Trabajo> trabajosCreador) {
        this.trabajosCreador = trabajosCreador;
    }

    public String getSucesos() {
        return sucesos;
    }

    public void setSucesos(String sucesos) {
        this.sucesos = sucesos
                .substring(0, Math.min(SUCESOS_LENGTH, sucesos.length()));
    }

    public Set<Accion> getAcciones() {
        return acciones;
    }

    public void setAcciones(Set<Accion> acciones) {
        this.acciones = acciones;
    }

    
    
    
    
    public String getGravatarUrl() throws NoSuchAlgorithmException {
        if(gravatarUrl == null) {
            byte[] bytes = ByteBuffer.allocate(8).putLong(serialVersionUID * id).array();
            byte[] md5 = MessageDigest.getInstance("MD5").digest(bytes);
            
            StringBuilder aux = new StringBuilder();
            aux.append("http://www.gravatar.com/avatar/");
            for(int i=0; i<md5.length; i++) {
                String digito = Integer.toHexString(0xFF & md5[i]);
                if(digito.length() == 1) {
                    aux.append(0).append(digito);
                } else {
                    aux.append(digito);
                }
            }
            aux.append(".png");
            
            gravatarUrl = aux.toString();
        }
        
        return gravatarUrl;
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
        if (!(object instanceof Personaje)) {
            return false;
        }
        Personaje other = (Personaje) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "triican.entity.Personaje[ id=" + id + " ]";
    }

    public void restauraCansancio() {
        if(cansancio != 0f) {
            cansancio -= TASA_RECUPERACION_CANSANCIO;
            cansancio = Math.max(cansancio, 0f);
        }
    }
    
}
