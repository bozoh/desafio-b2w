package b2w.desafio.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import b2w.desafio.BaseTest;
import b2w.desafio.exceptions.PlanetaAlreadyExistsException;
import b2w.desafio.exceptions.PlanetaNotFoundException;
import b2w.desafio.models.Planeta;
import b2w.desafio.models.SWAPIPlaneta;
import b2w.desafio.repositories.PlanetaRepository;

@RunWith(MockitoJUnitRunner.class)
public class PlanetasServiceTest extends BaseTest {

    @Mock
    private PlanetaRepository mockPlanetaRepository;

    @Mock
    private SWAPIPlanetaService mockSWApiPlanetaService;

    @InjectMocks
    private PlanetasService service;

    // @Before
    // public void beforeEach() {
    // mockRepository = mock(PlanetaRepository.class);
    // service = new PlanetasService(mockRepository);
    // }

    @Test
    public void teste_adicionar_planeta() throws Exception {

        final Planeta p = getRndPlaneta();
        SWAPIPlaneta pSW = getRndSWAPIPlaneta();
        List<SWAPIPlaneta> search = new ArrayList<>();

        // Teste 01 - O planeta não existe ainda, a verificação por nome retorna nulo, e o planeta existe no SWAPI
        pSW.setName(p.getNome());
        search.add(pSW);
        when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(search);
        when(mockPlanetaRepository.findOneByNomeIgnoreCase(anyString())).thenReturn(null);

        service.adicionar(p);

        verify(mockPlanetaRepository, atLeastOnce()).findOneByNomeIgnoreCase(p.getNome());
        verify(mockSWApiPlanetaService, times(1)).getSWAPIPlaneta(p.getNome());
        verify(mockPlanetaRepository, times(1)).save(p);

        // Limpando o mock do repositório
        reset(mockPlanetaRepository, mockSWApiPlanetaService);
        search.clear();
        // Teste 02 - O planeta existe ainda, a verificação por nome retorna um planeta
        when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(search);
        when(mockPlanetaRepository.findOneByNomeIgnoreCase(anyString())).thenReturn(p);

        assertThatExceptionOfType(PlanetaAlreadyExistsException.class).isThrownBy(
            ()->service.adicionar(p)
        ).withMessageContaining(p.toString());

        verify(mockPlanetaRepository, atLeastOnce()).findOneByNomeIgnoreCase(p.getNome());
        verify(mockSWApiPlanetaService, never()).getSWAPIPlaneta(p.getNome());
        verify(mockPlanetaRepository, never()).save(any(Planeta.class));

         // Teste 03 - O planeta nao existe ainda, ou seja a verificação por nome não
        // retorna um planeta, mas
        // o planeta não existe na SWAPI
        reset(mockPlanetaRepository, mockSWApiPlanetaService);
        search.clear();
        ///// Retorna nulo
        when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(null);
        when(mockPlanetaRepository.findOneByNomeIgnoreCase(anyString())).thenReturn(null);

        assertThatExceptionOfType(PlanetaNotFoundException.class).isThrownBy(() -> service.adicionar(p))
                .withMessageContaining(p.getNome());

        verify(mockPlanetaRepository, atLeastOnce()).findOneByNomeIgnoreCase(p.getNome());
        verify(mockSWApiPlanetaService, times(1)).getSWAPIPlaneta(p.getNome());
        verify(mockPlanetaRepository, never()).save(any(Planeta.class));

         //// Retona vazio
        reset(mockPlanetaRepository, mockSWApiPlanetaService);
        search.clear();

        when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(search);
        when(mockPlanetaRepository.findOneByNomeIgnoreCase(anyString())).thenReturn(null);

        assertThatExceptionOfType(PlanetaNotFoundException.class).isThrownBy(() -> service.adicionar(p))
                .withMessageContaining(p.getNome());

        verify(mockPlanetaRepository, atLeastOnce()).findOneByNomeIgnoreCase(p.getNome());
        verify(mockSWApiPlanetaService, times(1)).getSWAPIPlaneta(p.getNome());
        verify(mockPlanetaRepository, never()).save(any(Planeta.class));

    }

    
    @Test
    public void teste_load_chama_o_metodo_findOne_no_repositorio() throws PlanetaNotFoundException {
        String id = "id_do_Planeta";
        Planeta p = getRndPlaneta();

        // Teste 01 - O planeta existe
        when(mockPlanetaRepository.findOne(anyString())).thenReturn(p);
        service.load(id);

        verify(mockPlanetaRepository, times(1)).findOne(id);

        // Teste 02 - O planeta não existe
        reset(mockPlanetaRepository);
        when(mockPlanetaRepository.findOne(anyString())).thenReturn(null);
        assertThatExceptionOfType(PlanetaNotFoundException.class).isThrownBy(
            ()->service.load(id)
        ).withMessageContaining(id);
    }


    @Test
    public void teste_isStarWars() throws URISyntaxException {
            String nome = "Planeta SW 01";
            List<SWAPIPlaneta> result = new ArrayList<>();
    
            //Situação 01:  Não encontra planeta algum
            ////Retorna vazio
            when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(result);
        
            assertThat(service.isStarWars(nome)).isFalse();
            verify(mockSWApiPlanetaService, times(1)).getSWAPIPlaneta(nome);
    
            ////Retorna nulo
            reset(mockSWApiPlanetaService);
            when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(null);
        
            assertThat(service.isStarWars(nome)).isFalse();
            verify(mockSWApiPlanetaService, times(1)).getSWAPIPlaneta(nome);
    
            //Situação 02:  Encontra planetas, mas nenhum com o nome exatamente igual (ignorando case)
            reset(mockSWApiPlanetaService);
            int testSize = getRandomNumber(5, 50);
            while(result.size() < testSize) {
                result.add(getRndSWAPIPlaneta());
            }
            
            when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(result);
    
            assertThat(service.isStarWars(nome)).isFalse();
            verify(mockSWApiPlanetaService, times(1)).getSWAPIPlaneta(nome);
    
            //Situação 03:  Encontra planetas, alguns são exatamente iguais (ignorando case)
            ////Filmes nulo
            reset(mockSWApiPlanetaService);
            result.clear();

            testSize = getRandomNumber(5, 50);
            while(result.size() < testSize) {
                result.add(getRndSWAPIPlaneta());
            }
            result.add(getRndSWAPIPlaneta(nome));
            result.add(getRndSWAPIPlaneta(nome.toLowerCase()));
            result.add(getRndSWAPIPlaneta(nome.toUpperCase()));

            when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(result);

            
            assertThat(service.isStarWars(nome)).isTrue();
            verify(mockSWApiPlanetaService, times(1)).getSWAPIPlaneta(nome);
        }


    @Test
    public void teste_loadPorNome_chama_o_metodo_findOneByNomeIgnoreCase_no_repositorio() {
        String nome = "nome_do_Planeta";
        service.loadPorNome(nome);

        verify(mockPlanetaRepository, times(1)).findOneByNomeIgnoreCase(nome);
    }

    @Test
    public void teste_remover_planeta_chama_metodo_findOne_no_repositorio() {
        Planeta p = getRndPlaneta();
        p.setId("id_do_p");

        // Teste 01 - O planeta não existe ainda, a busca por id retorna nulo
        when(mockPlanetaRepository.findOne(anyString())).thenReturn(null);

        service.remover(p.getId());

        verify(mockPlanetaRepository, times(1)).findOne(p.getId());
        verify(mockPlanetaRepository, never()).delete(any(Planeta.class));

        // Limpando o mock do repositório
        reset(mockPlanetaRepository);

        // Teste 02 - O planeta existe ainda, a busca por id retorna um planeta
        when(mockPlanetaRepository.findOne(anyString())).thenReturn(p);

        service.remover(p.getId());

        verify(mockPlanetaRepository, times(1)).findOne(p.getId());
        verify(mockPlanetaRepository, times(1)).delete(p);

    }

    @Test
    public void teste_procurar_por_nome_sem_paginacao_ou_ordem() {
        // Quando chamo o método procurarPorNome só com o parâmetro nome
        // deve chamar uma procura como o nome e com ordenação por nome

        when(mockPlanetaRepository.count()).thenReturn(1l);
        String nome = "Teste";
        Pageable p = PageRequest.of(0, 1, Sort.Direction.ASC, "nome");

        service.procurarPorNome(nome);

        verify(mockPlanetaRepository, times(1)).findByNomeContainingIgnoreCase(nome, p);

    }

    @Test
    public void teste_procurar_por_nome_sem_paginacao_com_ordem_reversa_no_id() {

        when(mockPlanetaRepository.count()).thenReturn(1l);
        String nome = "Teste";
        Pageable p = PageRequest.of(0, 1, Sort.Direction.DESC, "id");


        service.procurarPorNome(nome, null, null, "id", true);

        verify(mockPlanetaRepository, times(1)).findByNomeContainingIgnoreCase(nome, p);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void teste_procurar_por_nome_com_paginacao() {
        
        // Por padrão a ordem é por nome e direta

        String nome = "Teste";

        Pageable p = PageRequest.of(12, 50, Sort.Direction.ASC, "nome");
        
        service.procurarPorNome(nome, 12, 50);
        verify(mockPlanetaRepository, times(1)).findByNomeContainingIgnoreCase(nome, p);
        
    }


    @Test
    public void teste_listar_sem_paginacao_ou_ordem() {
        // Quando chamo o método listar sem parâmetros
        // deve chamar listAll com ordenação por nome
        when(mockPlanetaRepository.count()).thenReturn(1l);

        Pageable p = PageRequest.of(0, 1, Sort.Direction.ASC, "nome");

        service.listar();

        verify(mockPlanetaRepository, times(1)).findAll(p);

    }

    @Test
    public void teste_listar_sem_paginacao_com_ordem_por_id_inversa() {

        when(mockPlanetaRepository.count()).thenReturn(1l);
        Pageable p = PageRequest.of(0, 1, Sort.Direction.DESC, "id");

        service.listar(null, null, "id", true);

        verify(mockPlanetaRepository, times(1)).findAll(p);

    }

    @Test
    public void teste_listar_com_paginacao() {
        // Por padrão a ordem é por nome e direta
        Pageable p = PageRequest.of(12, 50, Sort.Direction.ASC, "nome");
        
        
        service.listar(12, 50);
        verify(mockPlanetaRepository, times(1)).findAll(p);
        
    }

    @Test
    public void teste_quantidadeFilmesPorNome() throws URISyntaxException, PlanetaNotFoundException {
        String nome = "Planeta SW 01";
        List<SWAPIPlaneta> result = new ArrayList<>();

        //Situação:  Planeta não está na base de dados e SWAPI Não encontra planeta algum
        when(mockPlanetaRepository.findOneByNomeIgnoreCase(anyString())).thenReturn(null);
        ////Retorna vazio
        when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(result);

    
        assertThatExceptionOfType(PlanetaNotFoundException.class).isThrownBy(
            ()->service.quantidadeFilmesPorNome(nome)
        ).withMessageContaining(nome);
        verify(mockPlanetaRepository, times(1)).findOneByNomeIgnoreCase(nome);
        verify(mockSWApiPlanetaService, times(1)).getSWAPIPlaneta(nome);

        reset(mockSWApiPlanetaService, mockPlanetaRepository);
        when(mockPlanetaRepository.findOneByNomeIgnoreCase(anyString())).thenReturn(null);
        ////Retorna nulo
        when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(null);

    
        assertThatExceptionOfType(PlanetaNotFoundException.class).isThrownBy(
            ()->service.quantidadeFilmesPorNome(nome)
        ).withMessageContaining(nome);
        verify(mockPlanetaRepository, times(1)).findOneByNomeIgnoreCase(nome);
        verify(mockSWApiPlanetaService, times(1)).getSWAPIPlaneta(nome);


        //Situação: Planeta está na base de dados, então não é necessário usar a SWAPI
        reset(mockSWApiPlanetaService, mockPlanetaRepository);
        result.clear();
        Planeta pnt = getRndPlaneta();
        when(mockPlanetaRepository.findOneByNomeIgnoreCase(anyString())).thenReturn(pnt);
        when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(null);

        assertThat(service.quantidadeFilmesPorNome(nome)).isEqualTo(pnt.getFilmes());
        verify(mockPlanetaRepository, times(1)).findOneByNomeIgnoreCase(nome);
        verify(mockSWApiPlanetaService, never()).getSWAPIPlaneta(nome);

        //Situação: Planeta não está na base de dados, e SWAPI Encontra apenas um planeta, e esse possui filmes
        reset(mockSWApiPlanetaService, mockPlanetaRepository);
        SWAPIPlaneta p = getRndSWAPIPlaneta();
        p.setName(nome);
        result.add(p);

        when(mockPlanetaRepository.findOneByNomeIgnoreCase(anyString())).thenReturn(null);
        when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(result);

        assertThat(service.quantidadeFilmesPorNome(nome)).isEqualTo(p.getFilms().length);
        verify(mockPlanetaRepository, times(1)).findOneByNomeIgnoreCase(nome);
        verify(mockSWApiPlanetaService, times(1)).getSWAPIPlaneta(nome);

        //Situação: Planeta não está na base de dados, e SWAPI Encontra apenas um planeta, e esse não possui filmes
        reset(mockSWApiPlanetaService, mockPlanetaRepository);
        result.clear();
        p = getRndSWAPIPlaneta(nome);
        ////Filmes nulo
        p.setFilms(null);
        result.add(p);
        when(mockPlanetaRepository.findOneByNomeIgnoreCase(anyString())).thenReturn(null);
        when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(result);

        assertThat(service.quantidadeFilmesPorNome(nome)).isEqualTo(0);
        verify(mockPlanetaRepository, times(1)).findOneByNomeIgnoreCase(nome);
        verify(mockSWApiPlanetaService, times(1)).getSWAPIPlaneta(nome);

        reset(mockSWApiPlanetaService, mockPlanetaRepository);
        result.clear();
        p = getRndSWAPIPlaneta(nome);
        ////Filmes Vazio
        p.setFilms(new String[0]);
        result.add(p);
        when(mockPlanetaRepository.findOneByNomeIgnoreCase(anyString())).thenReturn(null);
        when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(result);

        assertThat(service.quantidadeFilmesPorNome(nome)).isEqualTo(0);
        verify(mockPlanetaRepository, times(1)).findOneByNomeIgnoreCase(nome);
        verify(mockSWApiPlanetaService, times(1)).getSWAPIPlaneta(nome);


        //Situação: Planeta não está na base de dados, e SWAPI Encontra mais de um planeta, e só um possui o mesmo nome e possui filmes
        reset(mockSWApiPlanetaService, mockPlanetaRepository);
        result.clear();
        p = getRndSWAPIPlaneta(nome);
        result.add(p);
        result.add(getRndSWAPIPlaneta());
        result.add(getRndSWAPIPlaneta());
        result.add(getRndSWAPIPlaneta());
        when(mockPlanetaRepository.findOneByNomeIgnoreCase(anyString())).thenReturn(null);
        when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(result);

        assertThat(service.quantidadeFilmesPorNome(nome)).isEqualTo(p.getFilms().length);
        verify(mockPlanetaRepository, times(1)).findOneByNomeIgnoreCase(nome);
        verify(mockSWApiPlanetaService, times(1)).getSWAPIPlaneta(nome);

        //Situação: Planeta não está na base de dados, e SWAPI Encontra mais de um planeta, e só um possui o mesmo nome e não possui filmes
        reset(mockSWApiPlanetaService, mockPlanetaRepository);
        result.clear();
        p = getRndSWAPIPlaneta(nome);
        //getFilms retorna nulo
        p.setFilms(null);
        result.add(p);
        result.add(getRndSWAPIPlaneta());
        result.add(getRndSWAPIPlaneta());
        result.add(getRndSWAPIPlaneta());
        when(mockPlanetaRepository.findOneByNomeIgnoreCase(anyString())).thenReturn(null);
        when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(result);

        assertThat(service.quantidadeFilmesPorNome(nome)).isEqualTo(0);
        verify(mockPlanetaRepository, times(1)).findOneByNomeIgnoreCase(nome);
        verify(mockSWApiPlanetaService, times(1)).getSWAPIPlaneta(nome);

        reset(mockSWApiPlanetaService, mockPlanetaRepository);
        result.clear();
        p = getRndSWAPIPlaneta(nome);
        //getFilms retorna vazio
        p.setFilms(new String[0]);
        result.add(p);
        result.add(getRndSWAPIPlaneta());
        result.add(getRndSWAPIPlaneta());
        result.add(getRndSWAPIPlaneta());
        when(mockPlanetaRepository.findOneByNomeIgnoreCase(anyString())).thenReturn(null);
        when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(result);

        assertThat(service.quantidadeFilmesPorNome(nome)).isEqualTo(0);
        verify(mockPlanetaRepository, times(1)).findOneByNomeIgnoreCase(nome);
        verify(mockSWApiPlanetaService, times(1)).getSWAPIPlaneta(nome);

        //Situação: Planeta não está na base de dados, e SWAPI Encontra mais de um planeta, mas nenhum possui o mesmo nome
        reset(mockSWApiPlanetaService, mockPlanetaRepository);
        result.clear();
        result.add(getRndSWAPIPlaneta());
        result.add(getRndSWAPIPlaneta());
        result.add(getRndSWAPIPlaneta());
        result.add(getRndSWAPIPlaneta());
        when(mockPlanetaRepository.findOneByNomeIgnoreCase(anyString())).thenReturn(null);
        when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(result);

        assertThat(service.quantidadeFilmesPorNome(nome)).isEqualTo(0);
        verify(mockPlanetaRepository, times(1)).findOneByNomeIgnoreCase(nome);
        verify(mockSWApiPlanetaService, times(1)).getSWAPIPlaneta(nome);

        //Situação: Planeta não está na base de dados, e SWAPI Encontra mais de um planeta, e mais de um possui o mesmo nome e alguns com filmes
        reset(mockSWApiPlanetaService, mockPlanetaRepository);
        result.clear();
        p = getRndSWAPIPlaneta(nome);
        SWAPIPlaneta p2 = getRndSWAPIPlaneta(nome);
        SWAPIPlaneta p3 = getRndSWAPIPlaneta(nome);
        p3.setFilms(null);
        SWAPIPlaneta p4 = getRndSWAPIPlaneta(nome);
        p4.setFilms(new String[0]);
        result.add(p);
        result.add(p2);
        result.add(p3);
        result.add(getRndSWAPIPlaneta());
        result.add(getRndSWAPIPlaneta());
        when(mockPlanetaRepository.findOneByNomeIgnoreCase(anyString())).thenReturn(null);
        when(mockSWApiPlanetaService.getSWAPIPlaneta(anyString())).thenReturn(result);

        assertThat(service.quantidadeFilmesPorNome(nome)).isEqualTo(p.getFilms().length + p2.getFilms().length);
        verify(mockPlanetaRepository, times(1)).findOneByNomeIgnoreCase(nome);
        verify(mockSWApiPlanetaService, times(1)).getSWAPIPlaneta(nome);

    }

    @Test
    public void teste_quantidadeFilmesPorId_nao_chama_getSWAPIPlaneta() throws URISyntaxException, PlanetaNotFoundException {
        Planeta p = getRndPlaneta();
        p.setId("Planeta-id");
     

        //Situação:  O id planeta não é encontrado
        when(mockPlanetaRepository.findOne(anyString())).thenReturn(null);
        
        assertThatExceptionOfType(PlanetaNotFoundException.class).isThrownBy(
            ()->service.quantidadeFilmesPorId(p.getId())
        ).withMessageContaining(p.getId());

        verify(mockPlanetaRepository, times(1)).findOne(p.getId());
        verify(mockSWApiPlanetaService, never()).getSWAPIPlaneta(p.getNome());

        

        //Situação :  O id planeta é encontrado
        reset(mockPlanetaRepository, mockSWApiPlanetaService);
        
        when(mockPlanetaRepository.findOne(anyString())).thenReturn(p);

        assertThat(service.quantidadeFilmesPorId(p.getId())).isEqualTo(p.getFilmes());
        verify(mockPlanetaRepository, times(1)).findOne(p.getId());
        verify(mockSWApiPlanetaService, never()).getSWAPIPlaneta(p.getNome());

    }
    
}