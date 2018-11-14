package b2w.desafio.handlers.utils;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiGlobalError {
    private String code;
    private String mensagem;
}