package org.games.xlspaceship.impl.game;

import org.games.xlspaceship.impl.game.ships.Winger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GridTest {

    @Test
    public void testToStringCase01() {
        Grid grid = new Grid();
        Assertions.assertEquals("................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n",
                grid.toString()
        );
    }

    @Test
    public void testSetSpaceshipCase01() {

        Grid grid = new Grid();
        Winger winger = new Winger(1);

        grid.printSpaceship(0, 0, winger);

        Assertions.assertEquals(
                "*.*.............\n" +
                        "*.*.............\n" +
                        ".*..............\n" +
                        "*.*.............\n" +
                        "*.*.............\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n",
                grid.toString()
        );
    }

    @Test
    public void testSetSpaceshipCase02() {

        Grid grid = new Grid();
        Winger winger = new Winger( 2);

        grid.printSpaceship(0, 0, winger);

        Assertions.assertEquals(
                "**.**...........\n" +
                        "..*.............\n" +
                        "**.**...........\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n",
                grid.toString()
        );
    }

    @Test
    public void testSetSpaceshipCase03() {
        Grid grid = new Grid();
        Winger winger = new Winger( 1);

        int x = 16 - winger.getWidth();
        int y = 16 - winger.getHeight();

        Assertions.assertTrue(grid.isSettable(x, y, winger));
        grid.printSpaceship(x, y, winger);

        Assertions.assertEquals(
                "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        ".............*.*\n" +
                        ".............*.*\n" +
                        "..............*.\n" +
                        ".............*.*\n" +
                        ".............*.*\n",
                grid.toString()
        );
    }

    @Test
    public void testIsSettableCase01() {
        Grid grid = new Grid();
        Winger winger1 = new Winger( 1);
        Winger winger2 = new Winger( 2);

        Assertions.assertTrue(grid.isSettable(0, 0, winger1));
        grid.printSpaceship(0, 0, winger1);

        Assertions.assertFalse(grid.isSettable(0, 0, winger2));
        Assertions.assertTrue(grid.isSettable(5, 5, winger2));
        grid.printSpaceship(5, 5, winger2);

        Assertions.assertEquals(
                "*.*.............\n" +
                        "*.*.............\n" +
                        ".*..............\n" +
                        "*.*.............\n" +
                        "*.*.............\n" +
                        ".....**.**......\n" +
                        ".......*........\n" +
                        ".....**.**......\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n",
                grid.toString()
        );
    }

    @Test
    public void testIsSettableCase02() {
        Grid grid = new Grid();
        Winger winger1 = new Winger( 1);
        Winger winger2 = new Winger( 2);

        Assertions.assertTrue(grid.isSettable(16 - winger1.getWidth(), 16 - winger1.getHeight(), winger1));
        grid.printSpaceship(0, 0, winger1);

        Assertions.assertFalse(grid.isSettable(0, 0, winger2));
        Assertions.assertTrue(grid.isSettable(5, 5, winger2));
        grid.printSpaceship(5, 5, winger2);

        Assertions.assertEquals(
                "*.*.............\n" +
                        "*.*.............\n" +
                        ".*..............\n" +
                        "*.*.............\n" +
                        "*.*.............\n" +
                        ".....**.**......\n" +
                        ".......*........\n" +
                        ".....**.**......\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n" +
                        "................\n",
                grid.toString()
        );
    }

    @Test
    public void testHex() {
        int x = 0;
        for (int i = 0; i < 15; i++) {
            x++;
        }
        //Assertions.assertEquals(16, x);
        String value = Integer.toString(x, 16);
        Assertions.assertEquals("f", value);
    }

}
