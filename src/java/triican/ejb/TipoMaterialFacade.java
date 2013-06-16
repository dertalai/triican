package triican.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import triican.entity.TipoMaterial;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Stateless
public class TipoMaterialFacade extends AbstractFacade<TipoMaterial> {
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoMaterialFacade() {
        super(TipoMaterial.class);
    }
    
}
