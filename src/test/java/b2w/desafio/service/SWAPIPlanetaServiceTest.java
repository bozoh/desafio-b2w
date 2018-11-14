package b2w.desafio.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import b2w.desafio.BaseTest;
import b2w.desafio.models.SWAPIPlaneta;
import b2w.desafio.service.utils.SWAPIPlanetaSearch;


@RunWith(MockitoJUnitRunner.class)
public class SWAPIPlanetaServiceTest extends BaseTest {
    public static final String SWAPI_BASE_URL = "https://swapi.co";
    public static final String SWAPI_PLANETS_RESOURCE = "/api/planets/";
    

    @Mock
    private RestTemplate mockRestTemplate;

    @InjectMocks
    private SWAPIPlanetaService swService;


    @Test
    public void test_se_getSWAPIPlaneta_chama_restTemplate_getForObject() throws URISyntaxException {
        //Criando os objetos utilizados nos mock
        String nome = "Planeta SW 01";
        SWAPIPlaneta p = getRndSWAPIPlaneta();

        //Criando a URI que deve ser chamada
        URIBuilder searchURL = new URIBuilder("https://swapi.co")
            .setPath("/api/planets/")
            .addParameter("search", nome)
            .setCharset(Charset.forName("UTF-8"));


        SWAPIPlanetaSearch mockSearch = mock(SWAPIPlanetaSearch.class);
        
        //Caso 01 - O planeta existe no swApi

        //Configurando os mocks
        when(mockSearch.getResults()).thenReturn(new SWAPIPlaneta[]{p});
        when(mockRestTemplate.getForObject(any(URI.class), any()))
            .thenReturn(mockSearch);

        
        List<SWAPIPlaneta> result = swService.getSWAPIPlaneta(nome);
        assertThat(result).hasSize(1);
        assertThat(result).contains(p);
  
        verify(mockRestTemplate, times(1)).getForObject(searchURL.build(), SWAPIPlanetaSearch.class);
        verify(mockSearch, times(2)).getResults();

        //Caso 02 - O planeta não existe no swApi
        //// SWAPIPlanetaSearch.getResult rentorna nulo
        reset(mockSearch, mockRestTemplate);

        when(mockSearch.getResults()).thenReturn(null);
        when(mockRestTemplate.getForObject(any(URI.class), any()))
            .thenReturn(mockSearch);

        result = swService.getSWAPIPlaneta(nome);
        assertThat(result).isEmpty();
    
        verify(mockRestTemplate, times(1)).getForObject(searchURL.build(), SWAPIPlanetaSearch.class);
        verify(mockSearch, times(1)).getResults();

        //// SWAPIPlanetaSearch.getResult rentorna um array vazio
        reset(mockSearch, mockRestTemplate);

        when(mockSearch.getResults()).thenReturn(new SWAPIPlaneta[0]);
        when(mockRestTemplate.getForObject(any(URI.class), any()))
            .thenReturn(mockSearch);
        
        result = swService.getSWAPIPlaneta(nome);
        assertThat(result).isEmpty();
    
        verify(mockRestTemplate, times(1)).getForObject(searchURL.build(), SWAPIPlanetaSearch.class);
        verify(mockSearch, times(2)).getResults();

        //Caso 03 RestTemplate retorna nulo
        reset(mockSearch, mockRestTemplate);
        when(mockRestTemplate.getForObject(any(URI.class), any()))
            .thenReturn(null);
    
        result = swService.getSWAPIPlaneta(nome);
        assertThat(result).isEmpty();
    
        verify(mockRestTemplate, times(1)).getForObject(searchURL.build(), SWAPIPlanetaSearch.class);
        verify(mockSearch, never()).getResults();
    }

   
}