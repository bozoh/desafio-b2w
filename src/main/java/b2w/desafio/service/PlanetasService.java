package b2w.desafio.service;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import b2w.desafio.exceptions.PlanetaAlreadyExistsException;
import b2w.desafio.exceptions.PlanetaNotFoundException;
import b2w.desafio.models.Planeta;
import b2w.desafio.models.SWAPIPlaneta;
import b2w.desafio.repositories.PlanetaRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PlanetasService {

   

    @Autowired
    private PlanetaRepository planetaRepository;

    @Autowired
    private SWAPIPlanetaService swApiPlanetaService;
    

    public Planeta adicionar(Planeta p) throws PlanetaAlreadyExistsException, URISyntaxException, PlanetaNotFoundException {
        if (planetaRepository.findOneByNomeIgnoreCase(p.getNome()) != null) {
            PlanetaAlreadyExistsException ex = new PlanetaAlreadyExistsException();
            ex.setPlaneta(p);
            throw ex;
        }

        Integer filmes = this.quantidadeFilmesPorNome(p.getNome());
        p.setFilmes(filmes);
        
        return planetaRepository.save(p);
    }

    /**
     * Busca um planeta por id
     * 
     * @param id Id do planeta
     * @return Retorna o planeta com o id ou nulo caso não ache
     * @throws PlanetaNotFoundException
     */
    public Planeta load(String id) throws PlanetaNotFoundException {
        Planeta p = planetaRepository.findOne(id);
        if (p == null) {
            PlanetaNotFoundException ex = new PlanetaNotFoundException();
            ex.setId(id);
            throw ex;
        }
        return p;
    }


     /**
     * Busca um planeta por nome, exato, ignorando o case
     * @param nome Nome do planeta
     * @return Retorna o planeta com o nome ou nulo caso não ache
     */
    public Planeta loadPorNome(String nome) {
        return planetaRepository.findOneByNomeIgnoreCase(nome);
    }

    /**
     *  
     * Procura planetas por nomes iguais ou semelhantes
     * ordenado por nome por padrão
     * 
     * @param nome Nome do planeta
     * @param pagina Número da Página
     * @param porPagina Quantidade de valores por página
     * @param sort Campo de ordenação (nome, clima, terreno, id)
     * @param reverse true se for para fazer ordenação inversa
     * @return Lista de planetas com nomes iguais ou semelhantes
     */
    public Page<Planeta> procurarPorNome(String nome, Integer pagina, Integer porPagina, String sort, Boolean reverse) {
        // Pagination
        Pageable pageable = getPageable(pagina, porPagina, sort, reverse);
        
        return planetaRepository.findByNomeContainingIgnoreCase(nome, pageable);
        
    }

    public Page<Planeta> procurarPorNome(String nome, Integer pagina, Integer porPagina, String sort) {
        return this.procurarPorNome(nome, pagina, porPagina, sort, false);
    }
    public Page<Planeta> procurarPorNome(String nome, Integer pagina, Integer porPagina) {
        return this.procurarPorNome(nome, pagina, porPagina, "nome");
    }

    public Page<Planeta> procurarPorNome(String nome) {
        return this.procurarPorNome(nome, null, null);
    }



    /**
     *  
     * Lista todos os planetas, ordenado por nome por padrão
     * 
     * @param pagina Número da Página
     * @param porPagina Quantidade de valores por página
     * @param sort Campo de ordenação (nome, clima, terreno, id)
     * @param reverse true se for para fazer ordenação inversa
     * @return Lista de planetas 
     */
    public Page<Planeta> listar(Integer pagina, Integer porPagina, String sort, Boolean reverse) {
        
        Pageable pageable = getPageable(pagina, porPagina, sort, reverse);
           
        return planetaRepository.findAll(pageable);
    }

    public Page<Planeta> listar(Integer pagina, Integer porPagina, String sort) {
        return this.listar(pagina, porPagina, sort, false);
    }

    public Page<Planeta> listar(Integer pagina, Integer porPagina) {
        return this.listar(pagina, porPagina, "nome");
    }

    public Page<Planeta> listar() {
        return this.listar(null, null);
    }

    /**
     * Remove o planeta com o id especificado
     * 
     * @param id Id do planeta
     * @throws PlanetaNotFoundException
     */
    public void remover(String id) throws PlanetaNotFoundException {
        Planeta p = planetaRepository.findOne(id);
        if (p == null) {
            PlanetaNotFoundException ex = new PlanetaNotFoundException();
            ex.setId(id);
            throw ex;
        }
        planetaRepository.delete(p);
    }

    public Integer quantidadeFilmesPorId(String id) throws URISyntaxException, PlanetaNotFoundException {
        Planeta p = planetaRepository.findOne(id);

        if (p == null) {
            PlanetaNotFoundException ex = new PlanetaNotFoundException();
            ex.setId(id);
            throw ex;
        }
        return p.getFilmes();
    }

    public Integer quantidadeFilmesPorNome(String nome) throws URISyntaxException, PlanetaNotFoundException {
        //Se o planeta já estiver na base, não preciso usar a SWAPI
        Planeta planeta = planetaRepository.findOneByNomeIgnoreCase(nome);
        if (planeta != null) {
            return planeta.getFilmes();
        }
        //Não está na base, então uso o SWAPI
        List<SWAPIPlaneta> swAPIPlanetas = swApiPlanetaService.getSWAPIPlaneta(nome);
        if (swAPIPlanetas==null || swAPIPlanetas.isEmpty()) {
            PlanetaNotFoundException ex = new PlanetaNotFoundException();
            ex.setId(nome);
            throw ex;
        }


        return swAPIPlanetas.stream()
            .filter(p -> p.getName().equalsIgnoreCase(nome))
            .map(p->{
                if(p.getFilms() == null)
                    return 0;
                return p.getFilms().length;
            }).mapToInt(Integer::intValue)
            .sum();
        
    }

    /**
     * Verifica se o nome pertence a algum planeta da franquia starwars, usando 
     * o SWAPI (https://swapi.co)
     * @param nome Nome do Planeta
     * @return true se o planeta fizer parte da franquia star wars
     * @throws URISyntaxException
     */
    public boolean isStarWars(String nome) throws URISyntaxException {
        List<SWAPIPlaneta> swAPIPlanetas = swApiPlanetaService.getSWAPIPlaneta(nome);
        if (swAPIPlanetas == null || swAPIPlanetas.isEmpty()) {
            return false;
        }

        // Filtrando planetas, retronando só os que tem exatamente 
        // o mesmo nome (ignorando case)
        return (swAPIPlanetas.stream()
            .filter(p -> p.getName().equalsIgnoreCase(nome))
            .count()) > 0;

    }
    

    

    private Pageable getPageable(Integer pagina, Integer porPagina, String sort, Boolean reverse) {
        if (pagina == null) {
            pagina = 0;
            porPagina = Long.valueOf(planetaRepository.count()).intValue();
        }

        if (porPagina == null)
            porPagina = 10;

        Direction ordDirection = getDirection(reverse);
        return PageRequest.of(pagina, porPagina, ordDirection, sort);
    }

    private Direction getDirection(Boolean reverse) {
        return (reverse != null && reverse.equals(Boolean.TRUE)) ? Direction.DESC : Direction.ASC;
    }

    private Sort getSort(String sort, Boolean reverse) {
        return new Sort(getDirection(reverse), sort);
    }

}

