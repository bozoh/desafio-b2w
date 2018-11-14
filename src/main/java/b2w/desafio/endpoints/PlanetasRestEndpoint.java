package b2w.desafio.endpoints;

import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import b2w.desafio.exceptions.PlanetaAlreadyExistsException;
import b2w.desafio.exceptions.PlanetaNotFoundException;
import b2w.desafio.models.Planeta;
import b2w.desafio.service.PlanetasService;
import b2w.desafio.validators.PlanetaValidator;

@RestController()
@RequestMapping("/api/planetas")
public class PlanetasRestEndpoint {

    
    @Autowired
    private PlanetasService planetasService;
    
    @Autowired
    private PlanetaValidator pValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(pValidator);
    }

    @PostMapping(consumes=MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Planeta criarPlaneta(@Valid @RequestBody Planeta p) throws PlanetaAlreadyExistsException, URISyntaxException, PlanetaNotFoundException {
        return planetasService.adicionar(p);
    }

        
    @GetMapping(value="/nome/{nome}/filmes", 
    
    produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Integer getAparicoesPlanetaPorNome(@PathVariable(name="nome") String nome) throws URISyntaxException, PlanetaNotFoundException {
        return planetasService.quantidadeFilmesPorNome(nome);
    }
    
    @GetMapping(value="/nome/{nome}",
    produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Page<Planeta> getPlanetasPorNome(
        @PathVariable(name="nome") String nome,
        @RequestParam(name="pagina", required=false) Integer pagina,
        @RequestParam(name="tamanho", required=false) Integer tamanho
    ) {
        return planetasService.procurarPorNome(nome, pagina, tamanho);
    }
    
    @GetMapping(value="/{id}/filmes", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Integer getAparicoesPlanetaPorId(@PathVariable String id) throws URISyntaxException, PlanetaNotFoundException {
        return planetasService.quantidadeFilmesPorId(id);
    }
    
    @GetMapping(value="/{id}", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Planeta getPlanetaPorId(@PathVariable String id) throws PlanetaNotFoundException {
        return planetasService.load(id);
    }
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Page<Planeta> listarPlanetas(
        @RequestParam(name="pagina", required=false) Integer pagina,
        @RequestParam(name="tamanho", required=false) Integer tamanho
    ) {
        return planetasService.listar(pagina, tamanho);
    }

    @DeleteMapping(value="/{id}")
    public void removerPlaneta(@PathVariable String id) {
        planetasService.remover(id);
    }


}