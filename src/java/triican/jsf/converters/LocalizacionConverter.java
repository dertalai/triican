/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package triican.jsf.converters;

import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import triican.entity.Localizacion;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@FacesConverter(forClass = Localizacion.class, value = "localizacionConverter")
public class LocalizacionConverter extends AbstractConverter<Localizacion> implements Converter {
    
}
