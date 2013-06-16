package triican.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import triican.entity.Perfil;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@Stateless
public class PerfilFacade extends AbstractFacade<Perfil> {
    @PersistenceContext(unitName = "TriicanPU")
    private EntityManager em;

    private static Perfil gestorMapa;
    private static Perfil gestorTipos;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PerfilFacade() {
        super(Perfil.class);
    }
    
    private Perfil findByNombre(String nombre) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder(); 
        CriteriaQuery cq = builder.createQuery();
        Root from = cq.from(entityClass);
        cq.select(from);
        cq.where(builder.equal(from.get("nombre"), nombre));
        Query q = getEntityManager().createQuery(cq);

        Perfil res;
        try {
            res = (Perfil) q.getSingleResult();
        } catch (NoResultException e) {
            res = null;
        }
        
        return res;
    }
    
    public Perfil gestorMapa() {
        if(gestorMapa == null) {
            Perfil porNombre = findByNombre(Perfil.PERFIL_GESTOR_MAPA);
            
            if(porNombre == null) {
                gestorMapa = new Perfil();
                gestorMapa.setNombre(Perfil.PERFIL_GESTOR_MAPA);
                create(gestorMapa);
            } else {
                gestorMapa = porNombre;
            }
        }
        
        return gestorMapa;
    }

    public Perfil gestorTipos() {
        if(gestorTipos == null) {
            Perfil porNombre = findByNombre(Perfil.PERFIL_GESTOR_TIPOS);

            if(porNombre == null) {
                gestorTipos = new Perfil();
                gestorTipos.setNombre(Perfil.PERFIL_GESTOR_TIPOS);
                create(gestorTipos);
            } else {
                gestorTipos = porNombre;
            }
        }
        
        return gestorTipos;
    }
    
}
