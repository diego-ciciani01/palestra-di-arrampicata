package com.polimi.palestraarrampicata.security;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class JwtBlackListSingleton {
    /**
     * quest classe è utilizzata per invalidare il jwt token utilizzato dall'utente loggato
     */
    private List<String> jwtBlackList;
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public List<String> getJwtBlackList() {
        if(jwtBlackList == null)
            jwtBlackList = new ArrayList<>();
        return jwtBlackList;
    }

    public void addJwtBlackList(String token) {
        this.getJwtBlackList().add(token);
    }
}
