/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package triican.ejb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import triican.entity.Personaje;
import triican.entity.Trabajo;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Stateless
public class PersonajeFacade extends AbstractFacade<Personaje> {
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PersonajeFacade() {
        super(Personaje.class);
    }

    public List<Personaje> findByTrabajo(Trabajo trabajo) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder(); 
        CriteriaQuery cq = builder.createQuery();
        Root from = cq.from(entityClass);
        cq.select(from);
        cq.where(builder.equal(from.get("trabajoParticipa"), trabajo));

        Query q = getEntityManager().createQuery(cq);
        List<Personaje> res = q.getResultList();
        
        return res;
    }
    
}
