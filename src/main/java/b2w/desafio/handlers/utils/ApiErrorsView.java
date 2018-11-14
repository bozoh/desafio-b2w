package b2w.desafio.handlers.utils;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiErrorsView {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private final LocalDateTime date = LocalDateTime.now(); 
    private List<ApiFieldError> fieldErrors;
    private List<ApiGlobalError> globalErrors;
    private String path;

}