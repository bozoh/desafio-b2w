package b2w.desafio.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import b2w.desafio.BaseTest;
import b2w.desafio.models.Planeta;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PlanetaRepositoryTest extends BaseTest{

    @Autowired
    private PlanetaRepository planetaRepository;

    
    @Before
    public void beforeEach() {
        planetaRepository.deleteAll();

        //Verificando se está realmente vazio
        assertThat(planetaRepository.count()).isEqualTo(0l);
    }

    @Test
    public void teste_create() {
        Planeta expected = getRndPlaneta();

        Planeta returned = planetaRepository.save(expected);
        assertThat(returned.getId()).isNotNull();
        assertThat(returned).isEqualTo(expected);


        Planeta mesmoNome = getRndPlaneta();
        mesmoNome.setNome(expected.getNome());

        // Deve lançar erro se tentar salvar o planeta como mesmo nome
        assertThat(mesmoNome.getId()).isNull();
        assertThat(mesmoNome.getNome()).isEqualTo(expected.getNome());
        try {
            planetaRepository.save(mesmoNome);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(DuplicateKeyException.class);
            // Como esperado
            return;
        }
        //Não deve chegar aqui
        Assert.fail("Nenhuma exceção foi lançada");
    }

    @Test
    public void teste_findAll() {
        // Testando se retorna vazio se não tiver nenhum planeta
        assertThat(planetaRepository.findAll()).isEmpty();

        planetaRepository.save(getRndPlaneta());
        planetaRepository.save(getRndPlaneta());
        planetaRepository.save(getRndPlaneta());
        planetaRepository.save(getRndPlaneta());
        planetaRepository.save(getRndPlaneta());

        List<Planeta> returned = planetaRepository.findAll();

        assertThat(returned).hasSize(5);
    }

    @Test
    public void teste_sort_por_nome_findAll() {
        // Testando se retorna vazio se não tiver nenhum planeta
        assertThat(planetaRepository.findAll()).isEmpty();

        Sort ascendente = new Sort(Sort.Direction.ASC, "nome");
        Sort inversa = new Sort(Sort.Direction.DESC, "nome");

        // Criando 100 Planetas como nome Planeta 001, Planeta 002, ...
        LinkedList<Planeta> listaOrdenada = new LinkedList<>();
        LinkedList<Planeta> listaInversa = new LinkedList<>();
        for(int i =0; i < 100; i++) {
            Planeta p = this.getRndPlaneta();
            p.setNome(String.format("Planeta %03d", i+1));
            planetaRepository.save(p);
            listaOrdenada.add(i, p);
            listaInversa.addFirst(p);
        }

        //Testando Sort
        List<Planeta> result = planetaRepository.findAll(ascendente);
        assertThat(result).hasSize(100);
        assertThat(result).containsExactlyElementsOf(listaOrdenada);

        result = planetaRepository.findAll(inversa);
        assertThat(result).hasSize(100);
        assertThat(result).containsExactlyElementsOf(listaInversa);
    }

    @Test
    public void teste_paginacao_findAll() {
        // Testando se retorna vazio se não tiver nenhum planeta
        assertThat(planetaRepository.findAll()).isEmpty();

        Sort ascendente = new Sort(Sort.Direction.ASC, "nome");
        Sort inversa = new Sort(Sort.Direction.DESC, "nome");

        // Criando 100 Planetas como nome Planeta 001, Planeta 002, ...
        LinkedList<Planeta> listaOrdenada = new LinkedList<>();
        LinkedList<Planeta> listaInversa = new LinkedList<>();
        for(int i =0; i < 100; i++) {
            Planeta p = this.getRndPlaneta();
            p.setNome(String.format("Planeta %03d", i+1));
            planetaRepository.save(p);
            listaOrdenada.add(i, p);
            listaInversa.addFirst(p);
        }

         //Testando Paginação
        // 4 páginas (0~3) com 25 elementos cada, com ordenação ascendente
        for(int i=0; i<4; i++) {
            Page<Planeta> pResult = planetaRepository.findAll(PageRequest.of(i, 25, ascendente));
            assertThat(pResult).hasSize(25);
            assertThat(pResult.getContent()).containsExactlyElementsOf(listaOrdenada.subList(i*25, (i*25)+25));
        }

        // 4 páginas (0~3) com 25 elementos cada, com ordenação inversa
        for(int i=0; i<4; i++) {
            Page<Planeta> pResult = planetaRepository.findAll(PageRequest.of(i, 25, inversa));
            assertThat(pResult).hasSize(25);
            assertThat(pResult.getContent()).containsExactlyElementsOf(listaInversa.subList(i*25, (i*25)+25));
        }
    }

   
    @Test
    public void teste_ordenacao_por_nome_e_paginacao_findByNomeContainingIgnoreCase() {
        Sort ascendente = new Sort(Sort.Direction.ASC, "nome");
        Sort inversa = new Sort(Sort.Direction.DESC, "nome");

        // Criando 100 Planetas como nome Planeta 001, Planeta 002, ...
        LinkedList<Planeta> listaOrdenada = new LinkedList<>();
        LinkedList<Planeta> listaInversa = new LinkedList<>();
        for(int i =0; i < 100; i++) {
            Planeta p = this.getRndPlaneta();
            p.setNome(String.format("Planeta %03d", i+1));
            planetaRepository.save(p);
            listaOrdenada.add(i, p);
            listaInversa.addFirst(p);
        }

        //Testando Paginação
        // 4 páginas (0~3) com 25 elementos cada, com ordenação ascendente
        for(int i=0; i<4; i++) {
            Page<Planeta> pResult = planetaRepository.findByNomeContainingIgnoreCase("PlaN", PageRequest.of(i, 25, ascendente));
            assertThat(pResult).hasSize(25);
            assertThat(pResult.getContent()).containsExactlyElementsOf(listaOrdenada.subList(i*25, (i*25)+25));
        }

        // 4 páginas (0~3) com 25 elementos cada, com ordenação inversa
        for(int i=0; i<4; i++) {
            Page<Planeta> pResult = planetaRepository.findByNomeContainingIgnoreCase("lanE", PageRequest.of(i, 25, inversa));
            assertThat(pResult).hasSize(25);
            assertThat(pResult.getContent()).containsExactlyElementsOf(listaInversa.subList(i*25, (i*25)+25));
        }
    }

    @Test
    public void teste_nomes_semelhantes_e_case_findByNomeContainingIgnoreCase() {
        Planeta p1 = getRndPlaneta();
        p1.setNome("Teste01");
        p1 = planetaRepository.save(p1);
        Planeta p2 = getRndPlaneta();
        p2.setNome("Teste02");
        p2 = planetaRepository.save(p2);
        Planeta p3 = getRndPlaneta();
        p3.setNome("Teste03");
        p3 = planetaRepository.save(p3);

        planetaRepository.save(getRndPlaneta());
        planetaRepository.save(getRndPlaneta());
        planetaRepository.save(getRndPlaneta());
        planetaRepository.save(getRndPlaneta());
        planetaRepository.save(getRndPlaneta());

        assertThat(planetaRepository.count()).isEqualTo(8l);

        // Testando se retorna por nome completo
        Page<Planeta> returned = planetaRepository.findByNomeContainingIgnoreCase("Teste01", null);
        assertThat(returned).size().isEqualTo(1);
        assertThat(returned.getContent()).containsOnly(p1);

        returned = planetaRepository.findByNomeContainingIgnoreCase("teste02", null);
        assertThat(returned).size().isEqualTo(1);
        assertThat(returned).containsOnly(p2);

        returned = planetaRepository.findByNomeContainingIgnoreCase("tesTe03", null);
        assertThat(returned).size().isEqualTo(1);
        assertThat(returned).containsOnly(p3);

        // Testando se retorna por nome parcial
        returned = planetaRepository.findByNomeContainingIgnoreCase("Teste", null);
        doAssertions(returned.getContent(), p1, p2, p3);

        returned = planetaRepository.findByNomeContainingIgnoreCase("teste", null);
        doAssertions(returned.getContent(), p1, p2, p3);

        returned = planetaRepository.findByNomeContainingIgnoreCase("tESte", null);
        doAssertions(returned.getContent(), p1, p2, p3);

        returned = planetaRepository.findByNomeContainingIgnoreCase("test", null);
        doAssertions(returned.getContent(), p1, p2, p3);

        // Testando nome inexistente retorna vazio

        returned = planetaRepository.findByNomeContainingIgnoreCase("TesteTesteTesteTesteTeste", null);
        assertThat(returned.getContent()).isEmpty();

    }

    @Test
    public void teste_findOneByNomeIgnoreCase(){
        //Deve retorna nulo se o nome não existir
        assertThat(planetaRepository.findOneByNomeIgnoreCase("teste")).isNull();

        Planeta p1 = getRndPlaneta();
        p1.setNome("Teste 01");
        planetaRepository.save(p1);

        Planeta p2 = getRndPlaneta();
        p2.setNome("Teste 02");
        planetaRepository.save(p2);

        Planeta p3 = getRndPlaneta();
        p3.setNome("Teste 03");
        planetaRepository.save(p3);
        
        //Deve somente ter 3 planetas
        assertThat(planetaRepository.count()).isEqualTo(3l);

        //Busca por nome Exato
        assertThat(planetaRepository.findOneByNomeIgnoreCase("Teste 01")).isEqualTo(p1);

        //Busca por nome Teste de case
        assertThat(planetaRepository.findOneByNomeIgnoreCase("teste 01")).isEqualTo(p1);
        assertThat(planetaRepository.findOneByNomeIgnoreCase("teSte 02")).isEqualTo(p2);
        assertThat(planetaRepository.findOneByNomeIgnoreCase("testE 03")).isEqualTo(p3);
        assertThat(planetaRepository.findOneByNomeIgnoreCase("TESTE 01")).isEqualTo(p1);

        //Retorna nulo se o nome for Parecido
        assertThat(planetaRepository.findOneByNomeIgnoreCase("Teste 0")).isNull();
        assertThat(planetaRepository.findOneByNomeIgnoreCase("teste 0")).isNull();
        assertThat(planetaRepository.findOneByNomeIgnoreCase("testi 0")).isNull();
        assertThat(planetaRepository.findOneByNomeIgnoreCase("TESTE")).isNull();
        assertThat(planetaRepository.findOneByNomeIgnoreCase("TESTE 0")).isNull();
        assertThat(planetaRepository.findOneByNomeIgnoreCase("TESTE  01")).isNull();
        assertThat(planetaRepository.findOneByNomeIgnoreCase("test")).isNull();
        assertThat(planetaRepository.findOneByNomeIgnoreCase(" ")).isNull();
        assertThat(planetaRepository.findOneByNomeIgnoreCase("   Teste 01")).isNull();
        assertThat(planetaRepository.findOneByNomeIgnoreCase("Teste  01")).isNull();
    }

    @Test
    public void teste_findOne(){
        //Deve retorna nulo se o nome não existir
        assertThat(planetaRepository.findOne("teste")).isNull();

        Planeta p1 = getRndPlaneta();
        p1.setId("Teste01");
        planetaRepository.save(p1);

        Planeta p2 = getRndPlaneta();
        p2.setId("Teste02");
        planetaRepository.save(p2);

        Planeta p3 = getRndPlaneta();
        p3.setId("Teste03");
        planetaRepository.save(p3);
        
        //Deve somente ter 3 planetas
        assertThat(planetaRepository.count()).isEqualTo(3l);

        //Busca por nome Exato
        assertThat(planetaRepository.findOne("Teste01")).isEqualTo(p1);
        assertThat(planetaRepository.findOne("Teste02")).isEqualTo(p2);
        assertThat(planetaRepository.findOne("Teste03")).isEqualTo(p3);

        //Busca por nome Teste de case
        assertThat(planetaRepository.findOne("teste01")).isEqualTo(p1);
        assertThat(planetaRepository.findOne("teSte02")).isEqualTo(p2);
        assertThat(planetaRepository.findOne("testE03")).isEqualTo(p3);
        assertThat(planetaRepository.findOne("TESTE01")).isEqualTo(p1);

        //Retorna nulo se o nome for Parecido
        assertThat(planetaRepository.findOne("Teste0")).isNull();
        assertThat(planetaRepository.findOne("teste0")).isNull();
        assertThat(planetaRepository.findOne("testi0")).isNull();
        assertThat(planetaRepository.findOne("TESTE")).isNull();
        assertThat(planetaRepository.findOne("TESTE0")).isNull();
        assertThat(planetaRepository.findOne("TESTE 01")).isNull();
        assertThat(planetaRepository.findOne("test")).isNull();
        assertThat(planetaRepository.findOne(" ")).isNull();
        assertThat(planetaRepository.findOne("   Teste_01")).isNull();
        assertThat(planetaRepository.findOne("Teste_01")).isNull();

    }


    @Test
    public void teste_deleteById() {
        Planeta p1 = getRndPlaneta();
        p1.setNome("Teste 1");
        p1 = planetaRepository.save(p1);
        Planeta p2 = getRndPlaneta();
        p2.setNome("Teste 2");
        p2 = planetaRepository.save(p2);

        planetaRepository.save(getRndPlaneta());

        assertThat(planetaRepository.findAll()).hasSize(3);

        Optional<Planeta> returned = planetaRepository.findById(p1.getId());
        assertThat(returned.isPresent()).isTrue();
        assertThat(returned.get()).isEqualTo(p1);
        planetaRepository.deleteById(p1.getId());

        returned = planetaRepository.findById(p1.getId());
        assertThat(returned.isPresent()).isFalse();
        assertThat(planetaRepository.findAll()).hasSize(2);

    }

    private void doAssertions(List<Planeta> returned, Planeta p1, Planeta p2, Planeta p3) {
        assertThat(returned).size().isGreaterThanOrEqualTo(3);
        assertThat(returned).contains(p1, p2, p3);
    }
}
