/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package triican.jsf.converters;

import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import triican.entity.Personaje;

/**
 *
 * @author Dertalai <dertalai@gmail.com>
 */
@FacesConverter(forClass = Personaje.class, value = "personajeConverter")
public class PersonajeConverter extends AbstractConverter<Personaje> implements Converter {
    
}
