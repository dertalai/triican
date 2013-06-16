/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package triican.jsf.converters;

import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import triican.entity.TipoElemento;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@FacesConverter(forClass = TipoElemento.class, value = "tipoElementoConverter")
public class TipoElementoConverter extends AbstractConverter<TipoElemento> implements Converter {
    
}
