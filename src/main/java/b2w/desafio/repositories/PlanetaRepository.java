package b2w.desafio.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import b2w.desafio.models.Planeta;

public interface PlanetaRepository extends MongoRepository<Planeta, String>, PlanetaRepositoryCustomInterface {
	
	// Usando os métodos disponíveis pela MongoRepository

	/**
	 * Busca planetas por nome iguais ou parecidos, ignorando a case
	 * 
	 * @param nome Nome do planeta
	 * @param pageable (Opcional) Define a página e quantidade de resultados 
	 * @return Página contendo uma lista de planetas
	 */
	Page<Planeta> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

}
