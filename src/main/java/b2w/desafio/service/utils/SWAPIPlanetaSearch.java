package b2w.desafio.service.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import b2w.desafio.models.SWAPIPlaneta;
import lombok.Data;

/**
 * Classe utilitária para buscar planetas pela API
 * https://swapi.co/api
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SWAPIPlanetaSearch {
    
    SWAPIPlaneta[] results;

   
}
