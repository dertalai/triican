/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package triican.jsf.converters;

import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import triican.entity.TipoRecurso;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@FacesConverter(forClass = TipoRecurso.class, value = "recursoConverter")
public class RecursoConverter extends AbstractConverter<TipoRecurso> implements Converter {
    
}
