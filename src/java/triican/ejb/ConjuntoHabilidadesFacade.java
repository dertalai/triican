package triican.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import triican.entity.ConjuntoHabilidades;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Stateless
public class ConjuntoHabilidadesFacade extends AbstractFacade<ConjuntoHabilidades> {
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConjuntoHabilidadesFacade() {
        super(ConjuntoHabilidades.class);
    }
    
}
