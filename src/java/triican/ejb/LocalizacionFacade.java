package triican.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import triican.entity.Localizacion;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Stateless
public class LocalizacionFacade extends AbstractFacade<Localizacion> {
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LocalizacionFacade() {
        super(Localizacion.class);
    }
    
}
