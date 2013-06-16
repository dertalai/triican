package triican.ejb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import triican.entity.Extraccion;
import triican.entity.TipoRecurso;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Stateless
public class ExtraccionFacade extends AbstractFacade<Extraccion> {
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExtraccionFacade() {
        super(Extraccion.class);
    }

    public List<Extraccion> findByTipoRecurso(TipoRecurso tipoRecurso) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder(); 
        CriteriaQuery cq = builder.createQuery();
        Root from = cq.from(entityClass);
        cq.select(from);
        cq.where(builder.equal(from.get("tipoRecurso"), tipoRecurso));
        Query q = getEntityManager().createQuery(cq);
        
        List<Extraccion> res = q.getResultList();
        
        return res;
    }
    
}
