package com.polimi.palestraarrampicata.dto.response;

import lombok.Builder;

@Builder
public class AuthenticationResponse {
    private  String token;
    private String message;

}
