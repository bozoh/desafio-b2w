package b2w.desafio.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanetaNotFoundException extends Exception {

    private static final long serialVersionUID = -9212762337031342684L;

    private static final String ERROR_MSG = "Planeta não encontrado %s";
    private String id;

    public PlanetaNotFoundException(String mensage, Throwable th) {
        super(mensage, th);
    }

    public PlanetaNotFoundException(String mensage) {
        super(mensage);
    }

    public PlanetaNotFoundException(Throwable th) {
        super(String.format(ERROR_MSG, ""));
    }

    public PlanetaNotFoundException() {
        super();
    }

    @Override
    public String getMessage() {
        if (id == null)
            return super.getMessage();

        return String.format(ERROR_MSG, String.format("ID:[%s]", id));
    }

} 