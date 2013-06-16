package triican.ejb;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import triican.entity.TipoTrabajo;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Stateless
public class TipoTrabajoFacade extends AbstractFacade<TipoTrabajo> {
    private static final String TOMAR_CAMINO = "Tomar camino";
    private static final String EXTRAER_RECURSO = "Extraer recurso";
    private static TipoTrabajo tipoTomarCamino;
    private static TipoTrabajo tipoExtraerRecurso;
    
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;

    @EJB
    private ConjuntoHabilidadesFacade conjuntoHabilidadesFacade;
    @EJB
    private TipoElementoFacade tipoElementoFacade;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoTrabajoFacade() {
        super(TipoTrabajo.class);
    }
    
    @Override
    public List<TipoTrabajo> findAll() {
        List<TipoTrabajo> lista = super.findAll();
        lista.remove(getTipoTomarCamino());
        lista.remove(getTipoExtraerRecurso());
        
        return lista;
    }
    
    public TipoTrabajo getTipoTomarCamino() {
        if(tipoTomarCamino == null) {
            TipoTrabajo porNombre = findByNombre(TOMAR_CAMINO);
            
            if(porNombre == null) {
                tipoTomarCamino = new TipoTrabajo();
                tipoTomarCamino.setNombre(TOMAR_CAMINO);
                
                //TODO: Ponemos valores que no se usan en este caso, pero son
                //obligatorios o con restricciones
                tipoTomarCamino.setDuracion(0f);
                tipoTomarCamino.setCantidadSalida(1);
                tipoTomarCamino.setTipoElemento(tipoElementoFacade.getTipoNota());
                
                conjuntoHabilidadesFacade.create(tipoTomarCamino.getConjuntoHabilidades());
                create(tipoTomarCamino);
            } else {
                tipoTomarCamino = porNombre;
            }
        }
        
        return tipoTomarCamino;
    }

    public TipoTrabajo getTipoExtraerRecurso() {
        if(tipoExtraerRecurso == null) {
            TipoTrabajo porNombre = findByNombre(EXTRAER_RECURSO);
            
            if(porNombre == null) {
                tipoExtraerRecurso = new TipoTrabajo();
                tipoExtraerRecurso.setNombre(EXTRAER_RECURSO);
                
                //TODO: Ponemos valores que no se usan en este caso, pero son
                //obligatorios o con restricciones
                tipoExtraerRecurso.setDuracion(0f);
                tipoExtraerRecurso.setCantidadSalida(1);
                tipoExtraerRecurso.setTipoElemento(tipoElementoFacade.getTipoNota());

                conjuntoHabilidadesFacade.create(tipoExtraerRecurso.getConjuntoHabilidades());
                create(tipoExtraerRecurso);
            } else {
                tipoExtraerRecurso = porNombre;
            }
        }
        
        return tipoExtraerRecurso;
    }
    
    private TipoTrabajo findByNombre(String nombre) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder(); 
        CriteriaQuery cq = builder.createQuery();
        Root from = cq.from(entityClass);
        cq.select(from);
        cq.where(builder.equal(from.get("nombre"), nombre));
        Query q = getEntityManager().createQuery(cq);

        TipoTrabajo res;
        try {
            res = (TipoTrabajo) q.getSingleResult();
        } catch (NoResultException e) {
            res = null;
        }
        
        return res;
    }

}
