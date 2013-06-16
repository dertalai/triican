package triican.ejb;

import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import triican.entity.Accion;
import triican.entity.Elemento;
import triican.entity.Personaje;
import triican.entity.Trabajo;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Singleton
public class AccionFacade extends AbstractFacade<Accion> {
    private final static Logger log = Logger.getLogger(AccionFacade.class.getName());
    
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;
    
    @EJB ElementoFacade elementoFacade;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AccionFacade() {
        super(Accion.class);
    }
    
    @Override
    @Asynchronous
    public void create(Accion accion) {
        log.info("AccionFacade#create() asíncrono.");
        super.create(accion);
        log.info("Fin create() asíncrono.");
    }
    
    @Override
    public Accion edit(Accion accion) {
        return super.edit(accion);
    }
    
    public List<Accion> findPendientes() {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder(); 
        CriteriaQuery cq = builder.createQuery();
        Root from = cq.from(entityClass);
        cq.select(from);
        cq.where(builder.equal(from.get("estado"), Accion.Estado.PENDIENTE));
        cq.orderBy(builder.asc(from.get("creado")));
                
        Query q = getEntityManager().createQuery(cq);

        return q.getResultList();
    }
    
    public List<Accion> findByElemento(Elemento elemento) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder(); 
        CriteriaQuery cq = builder.createQuery();
        Root from = cq.from(entityClass);
        cq.select(from);
        cq.where(builder.equal(from.get("elemento"), elemento));
                
        Query q = getEntityManager().createQuery(cq);

        return q.getResultList();
    }
    
    public Accion findUltimaByPersonaje(Personaje personaje) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder(); 
        CriteriaQuery cq = builder.createQuery();
        Root from = cq.from(entityClass);
        cq.select(from);
        cq.where(builder.equal(from.get("actor"), personaje));
        cq.orderBy(builder.desc(from.get("creado")));
        
        Query q = getEntityManager().createQuery(cq);

        List<Accion> lista = q.getResultList();
        Accion res = null;
        if( ! lista.isEmpty()) {
            res = lista.get(0);
        }
        
        return res;
    }

    void removeAll() {
        getEntityManager().createQuery("DELETE FROM Accion").executeUpdate();
    }

    public List<Accion> findByTrabajo(Trabajo trabajo) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder(); 
        CriteriaQuery cq = builder.createQuery();
        Root from = cq.from(entityClass);
        cq.select(from);
        cq.where(builder.equal(from.get("trabajo"), trabajo));
                
        Query q = getEntityManager().createQuery(cq);

        return q.getResultList();
    }
}
