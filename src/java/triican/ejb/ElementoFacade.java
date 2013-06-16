package triican.ejb;

import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import triican.entity.Accion;
import triican.entity.Elemento;
import triican.entity.Localizacion;
import triican.entity.Personaje;
import triican.entity.TipoElemento;
import triican.entity.Trabajo;
import triican.exception.ExcepcionBBDD;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Stateless
public class ElementoFacade extends AbstractFacade<Elemento> {
    private final static Logger log = Logger.getLogger(ElementoFacade.class.getName());
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;
    
    @EJB TipoElementoFacade tipoElementoFacade;
    @EJB AccionFacade accionFacade;
            
            
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ElementoFacade() {
        super(Elemento.class);
    }

    public List<Elemento> findByPersonaje(Personaje personaje) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder(); 
        CriteriaQuery cq = builder.createQuery();
        Root from = cq.from(entityClass);
        cq.select(from);
        cq.where(builder.equal(from.get("personaje"), personaje));
        
        Query q = getEntityManager().createQuery(cq);
        List<Elemento> res = q.getResultList();
        
        return res;
    }

    public List<Elemento> findByLocalizacion(Localizacion localizacion) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder(); 
        CriteriaQuery cq = builder.createQuery();
        Root from = cq.from(entityClass);
        cq.select(from);
        cq.where(builder.equal(from.get("localizacion"), localizacion));
        
        Query q = getEntityManager().createQuery(cq);
        List<Elemento> res = q.getResultList();
        
        return res;
    }
    
    public List<Elemento> findByTrabajo(Trabajo trabajo) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder(); 
        CriteriaQuery cq = builder.createQuery();
        Root from = cq.from(entityClass);
        cq.select(from);
        cq.where(builder.equal(from.get("trabajo"), trabajo));
        
        Query q = getEntityManager().createQuery(cq);
        List<Elemento> res = q.getResultList();
        
        return res;
    }

    public void amontona(Object ubicacion, TipoElemento tipoElemento) throws ExcepcionBBDD {
        if(! (ubicacion instanceof Personaje) && 
                ! (ubicacion instanceof Localizacion) &&
                ! (ubicacion instanceof Trabajo)) {
            throw new IllegalArgumentException(
                    "amontona(): ubicacion debe ser de tipo Localizacion, Personaje o Trabajo");
        }

        // Las notas no se amontonan
        if(tipoElementoFacade.getTipoNota().equals(tipoElemento)) {
            return;
        }
        
        log.info("amontona " + tipoElemento + " en " + ubicacion);
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder(); 
        CriteriaQuery cq = builder.createQuery();
        Root from = cq.from(entityClass);
        cq.select(from);
        
        Predicate ubicacionPred = null;
        if(ubicacion instanceof Personaje) {
            ubicacionPred = builder.equal(from.get("personaje"), ubicacion);
        } else if(ubicacion instanceof Localizacion) {
            ubicacionPred = builder.equal(from.get("localizacion"), ubicacion);        
        } else if(ubicacion instanceof Trabajo) {
            ubicacionPred = builder.equal(from.get("trabajo"), ubicacion);
        }
        Predicate tipoPred = builder.equal(from.get("tipoElemento"), tipoElemento);
        cq.where(ubicacionPred, tipoPred);
        
        Query q = getEntityManager().createQuery(cq);
        List<Elemento> res = q.getResultList();

        log.info("hay " + res.size() + " para amontonar");
        if(res.size() > 1) {
            Elemento monton = res.get(0);
            monton = edit(monton);
            Integer cantidad = monton.getCantidad();
            for(int i=1; i<res.size(); i++) {
                Elemento e = res.get(i);
                e = edit(e);
                cantidad += e.getCantidad();
                
                List<Accion> acciones = accionFacade.findByElemento(e);
                for(Accion a : acciones) {
                    a.setElemento(monton);
                    if(a.getId() != null) {
                        accionFacade.edit(a);
                    }
                }
                
                remove(e);
            }
            monton.setCantidad(cantidad);
        }
    }


}
