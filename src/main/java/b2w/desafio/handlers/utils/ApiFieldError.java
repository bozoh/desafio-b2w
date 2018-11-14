package b2w.desafio.handlers.utils;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiFieldError {
    private String field;
    private String code;
    private String mensagem;
    private Object rejectedValue;   
}