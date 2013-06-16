package triican.ejb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import triican.entity.TipoTrabajo;
import triican.entity.UsoMaterial;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Stateless
public class UsoMaterialFacade extends AbstractFacade<UsoMaterial> {
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsoMaterialFacade() {
        super(UsoMaterial.class);
    }

    public List<UsoMaterial> findByTipoTrabajo(TipoTrabajo tipoTrabajo) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder(); 
        CriteriaQuery cq = builder.createQuery();
        Root from = cq.from(entityClass);
        cq.select(from);
        cq.where(builder.equal(from.get("tipoTrabajo"), tipoTrabajo));
        Query q = getEntityManager().createQuery(cq);
        
        List<UsoMaterial> res = q.getResultList();
        
        return res;
    }
    
}
