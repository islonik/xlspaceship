package org.games.xlspaceship.impl.services;

import org.games.xlspaceship.impl.game.*;
import org.games.xlspaceship.impl.game.ships.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GridServices {

    @Autowired
    private RandomServices randomServices;

    public void setRandomServices(RandomServices randomServices) {
        this.randomServices = randomServices;
    }

    public Grid newUnknownGrid() {
        return new Grid();
    }

    public Grid newRandomGrid() {
        Grid grid = new Grid();

        List<Spaceship> spaceshipList = new ArrayList<>();
        spaceshipList.add(new BClass(randomServices.generateForm())); // 10 lives
        spaceshipList.add(new Winger(randomServices.generateForm())); // 9 lives
        spaceshipList.add(new SClass(randomServices.generateForm())); // 8 lives
        spaceshipList.add(new AClass(randomServices.generateForm())); // 8 lives
        spaceshipList.add(new Angle(randomServices.generateForm()));  // 6 lives

        while (true) {
            boolean isSet = false;
            for (Spaceship spaceship : spaceshipList) {
                isSet = grid.setSpaceship(randomServices, spaceship);
                if (!isSet) {
                    grid.clear();
                    break;
                }
            }
            if (isSet) {
                break;
            }
        }

        return grid;
    }

}
