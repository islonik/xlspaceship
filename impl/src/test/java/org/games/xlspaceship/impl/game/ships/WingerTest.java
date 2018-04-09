package org.games.xlspaceship.impl.game.ships;

import org.games.xlspaceship.impl.game.Cell;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class WingerTest {

    @Test
    public void testCreationCase01() {
        Winger winger = new Winger(1);

        List<Cell> shapeList = winger.shape();

        Assert.assertEquals(15, shapeList.size());
        Assert.assertEquals("[*, ., *, *, ., *, ., *, ., *, ., *, *, ., *]", shapeList.toString());
    }

    @Test
    public void testCreationCase02() {
        Winger winger = new Winger(2);

        List<Cell> shapeList = winger.shape();

        Assert.assertEquals(15, shapeList.size());
        Assert.assertEquals("[*, *, ., *, *, ., ., *, ., ., *, *, ., *, *]", shapeList.toString());
    }
}
