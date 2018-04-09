package org.games.xlspaceship.impl.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class GameServices {

    private static final AtomicLong sequenceId = new AtomicLong();

    @Autowired
    private RandomServices randomServices;

    public void setRandomServices(RandomServices randomServices) {
        this.randomServices = randomServices;
    }

    public String nextUniqueGameId() {
        return String.format("match-%s-%s-%s",
                sequenceId.incrementAndGet(),
                randomServices.generateUp10(),
                randomServices.generateUp10()
        );
    }

    /**
     * 1 - challenger
     * 2 - defender
     */
    public String chooseStartingPlayer(String challenger, String defender) {
        int p1 = 1;
        int randNumber = randomServices.generatePlayer();
        return (p1 == randNumber) ? challenger : defender;
    }

}
