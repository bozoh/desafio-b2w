package b2w.desafio.repositories;

import java.util.List;

import b2w.desafio.models.Planeta;
import org.springframework.data.domain.Sort;

public interface PlanetaRepositoryCustomInterface {

    /**
     * Retorna um planeta por nome (EXATO), ignorando o case 
     * @param nome Nome do planeta
     * @return O planeta ou nulo se o nome não existir
     */
    public Planeta findOneByNomeIgnoreCase(String nome);

    /**
     * Retorna um planeta por id (EXATO), ignorando o case 
     * @param id Id do planeta
     * @return O planeta ou nulo se o id não existir
     */
    public Planeta findOne(String id);


    // List<Planeta> findByNomeContainingIgnoreCaseSort(String nome, Sort sort);
}
