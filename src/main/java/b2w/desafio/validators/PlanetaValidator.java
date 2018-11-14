package b2w.desafio.validators;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import b2w.desafio.models.Planeta;
import b2w.desafio.service.PlanetasService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PlanetaValidator implements Validator {

    @Autowired
    PlanetasService pService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Planeta.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nome", "field.required", new String[] { "Nome" },
                "Nome não definido");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "clima", "field.required", new String[] { "Clima" },
                "Clima não definido");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "terreno", "field.required", new String[] { "Terreno" },
                "Terreno não definido");
        
        Planeta p = (Planeta) target;

        //Não deve entrar aqui, mas se entrar vai falhar na criação devido as anotações 
        //feitas na entity Planeta
        if (p.getNome() == null)
            return;

        if (pService.loadPorNome(p.getNome()) != null) {
            errors.rejectValue("nome", "field.already.exists", 
                new String[] { "nome", p.getNome() }, 
                String.format("O nome [%s] já existe", p.getNome()));
        }

        // Verificar se é um nome que pertence a franquia star wars
        // somente se não houver outros erros, já que faz uma chamada remota
        try {
            if (!errors.hasErrors() && !pService.isStarWars(p.getNome())) {
                errors.rejectValue("nome", "invalid.field.value", new String[] { "nome", p.getNome() },
                        String.format("O nome [%s] não pertence a franquia Start Wars", p.getNome()));
            }
        } catch (URISyntaxException e) {
            log.error(e.getLocalizedMessage());
            errors.reject(null, e.getLocalizedMessage());
		}

    }

}