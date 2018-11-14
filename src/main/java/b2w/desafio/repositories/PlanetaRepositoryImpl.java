package b2w.desafio.repositories;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import b2w.desafio.models.Planeta;

public class PlanetaRepositoryImpl implements PlanetaRepositoryCustomInterface {

    @Autowired
    private MongoTemplate mongoTemplate;
    private Collation ignoreCaseCollation;

    public PlanetaRepositoryImpl() {
        this.ignoreCaseCollation = Collation.of(Locale.ENGLISH).strength(Collation.ComparisonLevel.secondary());
    }

    @Override
    public Planeta findOneByNomeIgnoreCase(String nome) {
        Query query = new Query(Criteria.where("nome").is(nome));
        query.collation(this.ignoreCaseCollation);

        return mongoTemplate.findOne(query, Planeta.class);
    }

    @Override
    public Planeta findOne(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        query.collation(this.ignoreCaseCollation);

        return mongoTemplate.findOne(query, Planeta.class);
    }

    // @Override
    // public List<Planeta> findByNomeContainingIgnoreCaseSort(String nome, Sort sort) {
    //     Example<Planeta> example = Example.of(new Planeta(null, nome, null, null, null),
    //             ExampleMatcher.matching().withIgnorePaths("id", "terreno", "clima", "filmes").withMatcher("nome",
    //                     matcher -> matcher.contains().ignoreCase()));
    //     Query query = new Query(Criteria.byExample(example));
    //     query.with(sort);
    //     return mongoTemplate.find(query, Planeta.class);
    // }

}
