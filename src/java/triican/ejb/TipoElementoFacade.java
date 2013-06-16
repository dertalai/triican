package triican.ejb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import triican.entity.TipoElemento;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Stateless
public class TipoElementoFacade extends AbstractFacade<TipoElemento> {
    private static final String NOTA = "Nota";
    private static TipoElemento tipoNota;
    
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoElementoFacade() {
        super(TipoElemento.class);
    }
    
    @Override
    public List<TipoElemento> findAll() {
        List<TipoElemento> lista = super.findAll();
        lista.remove(getTipoNota());
        
        return lista;
    }

    public TipoElemento getTipoNota() {
        if(tipoNota == null) {
            TipoElemento porNombre = findByNombre(NOTA);
            
            if(porNombre == null) {
                tipoNota = new TipoElemento();
                tipoNota.setNombre(NOTA);
                create(tipoNota);
            } else {
                tipoNota = porNombre;
            }
        }
        
        return tipoNota;
    }

    private TipoElemento findByNombre(String nombre) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder(); 
        CriteriaQuery cq = builder.createQuery();
        Root from = cq.from(entityClass);
        cq.select(from);
        cq.where(builder.equal(from.get("nombre"), nombre));
        Query q = getEntityManager().createQuery(cq);

        TipoElemento res;
        try {
            res = (TipoElemento) q.getSingleResult();
        } catch (NoResultException e) {
            res = null;
        }
        
        return res;
    }
    
}
