package es.uji.ei1027.sgovi.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class SgOviControllerAdvice {

    @ExceptionHandler(value = NoHandlerFoundException.class)
    public ModelAndView handleNotFound(NoHandlerFoundException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorName", "Página no encontrada (404)");
        mav.addObject("message", "La dirección \"" + ex.getRequestURL() + "\" no existe.");
        return mav;
    }

    @ExceptionHandler(value = Exception.class)
    public ModelAndView handleException(Exception ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorName", "Error inesperado");
        mav.addObject("message", ex.getMessage());
        return mav;
    }
}
