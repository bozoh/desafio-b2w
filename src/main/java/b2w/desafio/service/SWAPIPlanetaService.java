package b2w.desafio.service;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import b2w.desafio.models.SWAPIPlaneta;
import b2w.desafio.service.utils.SWAPIPlanetaSearch;


@Service
public class SWAPIPlanetaService {
    public static final String SWAPI_BASE_URL = "https://swapi.co";
    public static final String SWAPI_PLANETS_RESOURCE = "/api/planets/";
    

    @Autowired
    private RestTemplate restTemplate;

    public List<SWAPIPlaneta> getSWAPIPlaneta(String nome) throws URISyntaxException {
        URIBuilder searchURL = new URIBuilder(SWAPIPlanetaService.SWAPI_BASE_URL)
            .setPath(SWAPIPlanetaService.SWAPI_PLANETS_RESOURCE)
            .addParameter("search", nome)
            .setCharset(Charset.forName("UTF-8"));
     
        SWAPIPlanetaSearch results = restTemplate.getForObject(searchURL.build(), SWAPIPlanetaSearch.class);
        
        if (results == null || results.getResults() == null)
            return new ArrayList<>();

        return Arrays.stream(results.getResults()).collect(Collectors.toList());
    }
}