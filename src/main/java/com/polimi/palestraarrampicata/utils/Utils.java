package com.polimi.palestraarrampicata.utils;

import com.polimi.palestraarrampicata.model.Utente;
import com.polimi.palestraarrampicata.repository.UtenteRepo;
import com.polimi.palestraarrampicata.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static final String REGEX_EMAIL = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public static final String REGEX_PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
    public static final String SPECIALITA = "^(?i)(ski|snowboard)$";
    public static final String SESSO = "^(?i)(man|women)$";
    public static final String LOCALDATE = "^(0[1-9]|[1-2][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$";
    public static final String LOCALDATETIME = "^(\\d{2})/(\\d{2})/(\\d{4}) (\\d{2}):(\\d{2})$";
    public static final String LOCALTIME = "^([01][0-9]|2[0-3]):[0-5][0-9]$";
    public static final String ERROR_EMAIL = "Formato mail non valido";
    public static final String ERROR_PASSWORD = "La password deve contenere almeno 8 caratteri, una lettera e una cifra.";
    public static final String ERROR_LOCALDATE = "La data deve essere nel formato dd/MM/yyyy";
    public static final String ERROR_LOCALDATETIME = "La data deve essere nel formato dd/MM/yyyy HH:mm";
    public static final String ERROR_LOCALTIME = "L'orario deve essere nel formato HH:mm";

    public static String getErrori(BindingResult result) {
        StringBuilder errori = new StringBuilder();
        result.getFieldErrors().forEach(e -> errori.append(e.getDefaultMessage()).append("\n"));
        return errori.toString();
    }

    public static Utente getUserFromHeader(HttpServletRequest httpServletRequest, UtenteRepo userRepository, JwtUtils jwtUtils) {
        String userEmail = jwtUtils.findEmailUtenteByHttpServletRequest(httpServletRequest);
        return userRepository.findUserByEmail(userEmail).orElseThrow(()->new IllegalStateException("L'utente non esiste"));
    }

    public static LocalDate parseLocalDateFromString(String data){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return  LocalDate.parse(data, formatter);
    }
    public static LocalDateTime formatterDataTime(String data) throws DateTimeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return LocalDateTime.parse(data, formatter);
    }

    public static LocalDate formatterData(String data) throws DateTimeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(data, formatter);
    }
}
