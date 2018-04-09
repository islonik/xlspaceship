package org.games.xlspaceship.impl.services;

import org.games.xlspaceship.impl.game.Grid;
import org.games.xlspaceship.impl.game.ships.Spaceship;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GridServicesTest {

    private GridServices gridServices;
    private RandomServices randomServices;

    @Before
    public void before() {
        randomServices = new RandomServices();
        gridServices = new GridServices();
        gridServices.setRandomServices(randomServices);
    }

    @Test
    public void testNewGameCase01() {
        Grid grid = gridServices.newRandomGrid();
        System.out.println(grid.toString());

        Assert.assertNotNull(grid.toString());
        Assert.assertEquals(5, grid.getSpaceshipList().size());

        int count = 0;
        for (Spaceship spaceship : grid.getSpaceshipList()) {
            count += spaceship.getLives();
        }
        Assert.assertEquals(41, count);
    }

    @Test
    public void testNewGameCase02() {
        for (int t = 0; t < 50000; t++) {
            Grid grid = gridServices.newRandomGrid();
            String board = grid.toString();
            int count = 0;
            for (int i = 0; i < board.length(); i++) {
                if (Character.toString(board.charAt(i)).equals("*")) {
                    count++;
                }
            }
            if (count != 41) {
                System.out.println(grid.toString());
            }
            Assert.assertEquals(41, count);
        }
    }
}
