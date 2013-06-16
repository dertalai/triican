/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package triican.jsf.converters;

import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import triican.entity.Elemento;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@FacesConverter(forClass = Elemento.class, value = "elementoConverter")
public class ElementoConverter extends AbstractConverter<Elemento> implements Converter {
    
}
