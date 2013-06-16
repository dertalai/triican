/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package triican.jsf.converters;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
public abstract class AbstractConverter<T> {
    private final static Logger log = Logger.getLogger(AbstractConverter.class.getName());
    private Method getId;

    public T getAsObject(FacesContext context, UIComponent component, String valor) {
        if (StringUtils.isBlank(valor)) {
            return null;
        }
        if (component instanceof PickList) {
            DualListModel<T> dl = (DualListModel) ((PickList) component).getValue();
            for (T t : dl.getTarget()) {
                String id = obtenerId(t);
                if (valor.equals(id)) {
                    return t;
                }
            }
            for (T t : dl.getSource()) {
                String id = obtenerId(t);
                if (valor.equals(id)) {
                    return t;
                }
            }

        } else if (component.getChildren().size() == 1 &&
                component.getChildren().get(0) instanceof UISelectItems) {
            UISelectItems select = (UISelectItems) component.getChildren().get(0);
            Collection<T> lista = (Collection<T>) select.getValue();
            for(T t : lista) {
                String id = obtenerId(t);
                if (valor.equals(id)) {
                    return t;
                }
            }
        }
            
        return null;
    }
    
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        return obtenerId((T) value);
    }

    
    private String obtenerId(T t) {
        if(t == null) {
            return null;
        }
        try {
            if(getId == null) {
                getId = t.getClass().getMethod("getId");
            }
            return (String) getId.invoke(t).toString();
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
            return null;
        }
    }

}
