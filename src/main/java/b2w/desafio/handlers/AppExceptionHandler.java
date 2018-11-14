package b2w.desafio.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import b2w.desafio.exceptions.PlanetaAlreadyExistsException;
import b2w.desafio.exceptions.PlanetaNotFoundException;
import b2w.desafio.handlers.utils.ApiErrorsView;
import b2w.desafio.handlers.utils.ApiFieldError;
import b2w.desafio.handlers.utils.ApiGlobalError;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class AppExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> defaultExceptionHandler(Exception ex, WebRequest request) {
        log.error("defaultExceptionHandler: {}", ex);
   
        List<ApiGlobalError> globalErrors = new ArrayList<>();
        globalErrors.add(ApiGlobalError.builder()
            .mensagem(ex.getLocalizedMessage())
            .build()
        );

        return buildResponseEntity(globalErrors, null, request, HttpStatus.INTERNAL_SERVER_ERROR); 
    }

    @ExceptionHandler(PlanetaNotFoundException.class)
    public final ResponseEntity<Object> planetaNotFoundExceptionHandler(PlanetaNotFoundException ex, WebRequest request) {
        log.error("planetaNotFoundException: {}", ex);
   
        List<ApiGlobalError> globalErrors = new ArrayList<>();
        globalErrors.add(ApiGlobalError.builder()
            .code(HttpStatus.NOT_FOUND.name())
            .mensagem(ex.getMessage())
            .build()
        );

        return buildResponseEntity(globalErrors, null, request, HttpStatus.NOT_FOUND); 
    }

    @ExceptionHandler(PlanetaAlreadyExistsException.class)
    public final ResponseEntity<Object> planetaAlreadyExistsExceptionHandler(PlanetaAlreadyExistsException ex, WebRequest request) {
        log.error("planetaAlreadyExistsException: {}", ex);
   
        List<ApiFieldError> errors = new ArrayList<>();
         errors.add(ApiFieldError.builder()
            .code("field.already.exists")
            .field("planeta")
            .rejectedValue(ex.getPlaneta())
            .mensagem(ex.getMessage())
            .build()
        );

        return buildResponseEntity(null, errors, request, HttpStatus.CONFLICT); 
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        

        log.error("handleMethodArgumentNotValid: {}", ex);

        BindingResult bindingResult = ex.getBindingResult();
        List<ApiFieldError> fieldErrors = bindingResult.getFieldErrors().stream()
            .map(fieldError -> ApiFieldError.builder()
                .field(fieldError.getField())
                .mensagem(fieldError.getDefaultMessage())
                .code(fieldError.getCode())
                .rejectedValue(fieldError.getRejectedValue())
                .build()
            ).collect(Collectors.toList());
        
        List<ApiGlobalError> globalErrors = bindingResult.getGlobalErrors().stream()
            .map(globalError -> ApiGlobalError.builder()
                .code(globalError.getCode())
                .mensagem(globalError.getDefaultMessage())
                .build()
            ).collect(Collectors.toList());

        return buildResponseEntity(globalErrors, fieldErrors, request, HttpStatus.UNPROCESSABLE_ENTITY); 
        
        
    }

    private ResponseEntity<Object> buildResponseEntity(List<ApiGlobalError> globalErrors,
            List<ApiFieldError> fieldErrors, WebRequest request, HttpStatus status) {
        
        return new ResponseEntity<>(ApiErrorsView.builder()
                .fieldErrors(fieldErrors)
                .globalErrors(globalErrors)
                .path(request.getDescription(false))
                .build(), 
            status
        );
    }

}







