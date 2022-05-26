package dev.cmedina.desafiomeli.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

   public record Error (String message) {}	
	
	
   @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
   public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
	   ex.printStackTrace();
     Error error = new Error(ex.getMessage());
     return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
   }
 
}