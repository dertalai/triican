package triican.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import triican.entity.TipoRecurso;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Stateless
public class TipoRecursoFacade extends AbstractFacade<TipoRecurso> {
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoRecursoFacade() {
        super(TipoRecurso.class);
    }
    
}
