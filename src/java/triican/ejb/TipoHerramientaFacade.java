package triican.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import triican.entity.TipoHerramienta;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Stateless
public class TipoHerramientaFacade extends AbstractFacade<TipoHerramienta> {
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoHerramientaFacade() {
        super(TipoHerramienta.class);
    }
    
}
