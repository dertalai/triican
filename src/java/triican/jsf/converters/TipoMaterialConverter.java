/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package triican.jsf.converters;

import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import triican.entity.TipoMaterial;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@FacesConverter(forClass = TipoMaterial.class, value = "tipoMaterialConverter")
public class TipoMaterialConverter extends AbstractConverter<TipoMaterial> implements Converter {
    
}
