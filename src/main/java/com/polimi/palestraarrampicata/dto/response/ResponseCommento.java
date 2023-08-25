package com.polimi.palestraarrampicata.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@ToString
public class ResponseCommento {
    private String id;
    private String testo;
    private String emailIstruttore;
    private String commentatore;

}
