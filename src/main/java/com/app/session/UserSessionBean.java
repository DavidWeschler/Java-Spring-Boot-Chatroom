package com.app.session;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class UserSessionBean {

    private boolean loggedIn;

    @PostConstruct
    public void init() {
        loggedIn = false;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}

