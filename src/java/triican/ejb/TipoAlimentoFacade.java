package triican.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import triican.entity.TipoAlimento;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Stateless
public class TipoAlimentoFacade extends AbstractFacade<TipoAlimento> {
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoAlimentoFacade() {
        super(TipoAlimento.class);
    }
    
}
