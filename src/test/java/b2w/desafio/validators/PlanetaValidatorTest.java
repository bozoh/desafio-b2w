package b2w.desafio.validators;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import b2w.desafio.BaseTest;
import b2w.desafio.models.Planeta;
import b2w.desafio.service.PlanetasService;

@RunWith(MockitoJUnitRunner.class)
public class PlanetaValidatorTest extends BaseTest{

    @Mock
    PlanetasService pService;


    @InjectMocks
    PlanetaValidator pValidator;

    @Test
    public void teste_supports() {
        assertThat(pValidator.supports(Planeta.class)).isTrue();

        assertThat(pValidator.supports(PlanetaValidatorTest.class)).isFalse();

    }
    

    @Test
    public void teste_se_validate_chama_loadPorNome() {
        Planeta p = getRndPlaneta();

        Errors errors = mock(Errors.class);
        when(errors.hasErrors()).thenReturn(false);

        // Situação 01, o nome não existe na base, então sem errors na validação
        when(pService.loadPorNome(anyString())).thenReturn(null);
        pValidator.validate(p, errors);
        verify(pService, times(1)).loadPorNome(p.getNome());
        verify(errors, never()).rejectValue("nome", "field.already.exists", 
            new String[] { "nome", p.getNome() }, 
            String.format("O nome [%s] já existe", p.getNome())
        );

        // Situação 02, o nome já existe na base, então erro de nome já existente
        reset(pService);
        when(pService.loadPorNome(anyString())).thenReturn(p);
        pValidator.validate(p, errors);
        verify(pService, times(1)).loadPorNome(p.getNome());
        verify(errors, times(1)).rejectValue("nome", "field.already.exists", 
            new String[] { "nome", p.getNome() }, 
            String.format("O nome [%s] já existe", p.getNome())
        );
    }

}