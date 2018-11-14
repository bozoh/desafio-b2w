package b2w.desafio;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import b2w.desafio.models.Planeta;
import b2w.desafio.models.SWAPIPlaneta;

public class BaseTest {

    /**
     * Gera um planeta com os atributos randomicos
     * 
     * @return Um planeta
     */
    protected Planeta getRndPlaneta() {
        Planeta p = new Planeta();

        p.setNome(RandomStringUtils.random(RandomUtils.nextInt(5, 10)));
        p.setTerreno(RandomStringUtils.random(RandomUtils.nextInt(5, 10)));
        p.setClima(RandomStringUtils.random(RandomUtils.nextInt(5, 10)));

        return p;
    }

    protected int getRandomNumber(int min, int max) {
        return RandomUtils.nextInt(min, max);
    }
    
    protected SWAPIPlaneta getRndSWAPIPlaneta() {
        SWAPIPlaneta p = new SWAPIPlaneta();

        p.setName(RandomStringUtils.random(RandomUtils.nextInt(5, 10)));

        int numFilmes = RandomUtils.nextInt(1, 5);

        if (numFilmes <= 0)
            return p;

        String[] filmes = new String[numFilmes];
        for (int i = 0; i < numFilmes; i++) {
            filmes[i] = RandomStringUtils.random(RandomUtils.nextInt(5, 10));
        }
        p.setFilms(filmes);
        return p;
    }

    protected SWAPIPlaneta getRndSWAPIPlaneta(String nome) {
        SWAPIPlaneta p = getRndSWAPIPlaneta();
        p.setName(nome);

        return p;
    }
    
}