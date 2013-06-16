package triican.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import triican.entity.Usuario;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }

    public Usuario findByUsuario(Usuario usuario) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder(); 
        CriteriaQuery cq = builder.createQuery();
        Root from = cq.from(entityClass);
        cq.select(from);
        cq.where(builder.equal(from.get("nombre"), usuario.getNombre()));
        cq.where(builder.equal(from.get("contrasena"), usuario.getContrasena()));
        Query q = getEntityManager().createQuery(cq);

        Usuario res;
        try {
            res = (Usuario) q.getSingleResult();
        } catch (NoResultException e) {
            res = null;
        }
        
        return res;
    }
    
}
