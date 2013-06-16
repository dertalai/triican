/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package triican.jsf.converters;

import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import triican.entity.TipoTrabajo;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@FacesConverter(forClass = TipoTrabajo.class, value = "tipoTrabajoConverter")
public class TipoTrabajoConverter extends AbstractConverter<TipoTrabajo> implements Converter {
    
}
