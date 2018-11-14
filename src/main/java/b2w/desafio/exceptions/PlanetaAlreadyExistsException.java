package b2w.desafio.exceptions;

import b2w.desafio.models.Planeta;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanetaAlreadyExistsException extends Exception {

    private static final long serialVersionUID = 8188426372687168668L;
    private static final String ERROR_MSG = "O Planeta já existe %s";
    private Planeta planeta;

    public PlanetaAlreadyExistsException(String mensage, Throwable th) {
        super(mensage, th);
    }

    public PlanetaAlreadyExistsException(String mensage) {
        super(mensage);
    }

    public PlanetaAlreadyExistsException(Throwable th) {
        super(String.format(ERROR_MSG, ""));
    }

    public PlanetaAlreadyExistsException() {
        super();
    }

    @Override
    public String getMessage() {
        if (planeta == null)
            return super.getMessage();

        return String.format(ERROR_MSG, String.format("planeta: [%s]", planeta.toString()));
    }

} 