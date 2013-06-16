/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package triican.jsf.converters;

import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import triican.entity.TipoHerramienta;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@FacesConverter(forClass = TipoHerramienta.class, value = "tipoHerramientaConverter")
public class TipoHerramientaConverter extends AbstractConverter<TipoHerramienta> implements Converter {
    
}
