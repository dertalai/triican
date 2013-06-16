package triican.ejb;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import triican.entity.Localizacion;
import triican.entity.Trabajo;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Stateless
public class TrabajoFacade extends AbstractFacade<Trabajo> {
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;

    @EJB
    TipoTrabajoFacade tipoTrabajoFacade;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TrabajoFacade() {
        super(Trabajo.class);
    }

    public List<Trabajo> findByLocalizacion(Localizacion localizacion) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder(); 
        CriteriaQuery cq = builder.createQuery();
        Root from = cq.from(entityClass);
        cq.select(from);
        cq.where(builder.equal(from.get("localizacion"), localizacion)
//                , builder.notEqual(from.get("tipoTrabajo"), tipoTrabajoFacade.getTipoExtraerRecurso())
                , builder.notEqual(from.get("tipoTrabajo"), tipoTrabajoFacade.getTipoTomarCamino()) );
        
        Query q = getEntityManager().createQuery(cq);
        List<Trabajo> res = q.getResultList();
        
        return res;
    }

}
