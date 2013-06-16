package triican.ejb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import triican.entity.Sistema;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Singleton
public class SistemaFacade extends AbstractFacade<Sistema> {
    private final static Logger log = Logger.getLogger(SistemaFacade.class.getName(), "/messages");
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade() {
        super(Sistema.class);
    }
    
    public Sistema findTurnoActual() {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder(); 
        CriteriaQuery cq = builder.createQuery();
        Root from = cq.from(entityClass);
        cq.select(from);
        cq.where(builder.equal(from.get("estado"), Sistema.Estado.ACTUAL));
        cq.orderBy(builder.desc(from.get("turno")));
        Query q = getEntityManager().createQuery(cq);

        Sistema res;
        try {
            res = (Sistema) q.getSingleResult();
        } catch (NoResultException e) {
            log.log(Level.SEVERE, "findTurnoActual(): no se ha encontrado el turno actual");
            res = null;
        } catch (NonUniqueResultException e) {
            log.log(Level.SEVERE, "findTurnoActual(): se ha encontrado más de un candidato a turno actual");
            List<Sistema> lista = (List<Sistema>) q.getResultList();
            res = lista.get(0);
            for(Sistema s : lista.subList(1, lista.size()) ) {
                s.setEstado(Sistema.Estado.ERROR);
                edit(s);
            }
        }
        
        return res;
    }

}
