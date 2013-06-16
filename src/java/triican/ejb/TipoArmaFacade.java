package triican.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import triican.entity.TipoArma;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Stateless
public class TipoArmaFacade extends AbstractFacade<TipoArma> {
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoArmaFacade() {
        super(TipoArma.class);
    }
    
}
