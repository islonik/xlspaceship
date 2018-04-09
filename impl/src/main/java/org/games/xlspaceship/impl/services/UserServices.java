package org.games.xlspaceship.impl.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServices {

    @Autowired
    private RandomServices randomServices;

    public void setRandomServices(RandomServices randomServices) {
        this.randomServices = randomServices;
    }

    private String userId;
    private String fullName;
    private boolean isAI = false;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setUpAI() {
        isAI = true;
        userId = "AI";
        fullName = String.format("AI-%s", randomServices.generateAI());
    }

    public boolean isAI() {
        return isAI;
    }

}
