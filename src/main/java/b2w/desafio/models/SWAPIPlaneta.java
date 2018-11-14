package b2w.desafio.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SWAPIPlaneta {
    String name;
    String[] films;
}