package org.games.xlspaceship.impl.services;

import org.games.xlspaceship.impl.game.Grid;
import org.games.xlspaceship.impl.game.GridStatus;
import org.games.xlspaceship.impl.model.FireRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AIServices {

    private static final Logger log = LoggerFactory.getLogger(AIServices.class);

    private static final ExecutorService executors = Executors.newFixedThreadPool(10);

    @Autowired
    private UserServices userServices;

    @Autowired
    private RandomServices randomServices;

    @Autowired
    private RestServices restServices;

    public void setRandomServices(RandomServices randomServices) {
        this.randomServices = randomServices;
    }

    public void asyncFireRequest(final String playerTurn, final String gameId, final int aliveShips, final GridStatus opponent) {
        if (userServices.isAI() && userServices.getUserId().equalsIgnoreCase(playerTurn) && aliveShips > 0) {
            executors.execute(() -> {
                try {
                    Thread.sleep(500L);

                    List<String> salvo = new ArrayList<>();
                    FireRequest fireRequest = new FireRequest();
                    for (int i = 1; i <= aliveShips; i++) {
                        String shotId = getShotId(opponent);
                        salvo.add(shotId);
                    }

                    fireRequest.setSalvo(salvo);

                    String localHost = restServices.getCurrentHostname();
                    int localPort = restServices.getCurrentPort();

                    log.debug("host = " + localHost + " port = " + localPort + " gameId = " + gameId + " aliveShips = " + aliveShips + " request = " + fireRequest.toString());

                    restServices.fireShotByAi(localHost, localPort, gameId, fireRequest);
                } catch (InterruptedException ie) {
                    log.error(ie.getMessage());
                }
            });
        }
    }

    private String getShotId(final GridStatus opponent) {
        int count = 0;
        while (true) {
            int x = randomServices.generateUp16();
            int y = randomServices.generateUp16();

            String shotId = String.format("%sx%s", Integer.toString(x, 16), Integer.toString(y, 16));
            String status = opponent.getGrid().getValue(x, y);

            if (status.equalsIgnoreCase(".")) {
                return shotId;
            }
            if (count++ >=30) {
                break;
            }
        }
        return getIterativeStatus(opponent);
    }

    private String getIterativeStatus(final GridStatus opponent) {
        for (int x = 0; x < Grid.SIZE; x++) {
            for (int y = 0; y < Grid.SIZE; y++) {
                String shotId = String.format("%sx%s", Integer.toString(x, 16), Integer.toString(y, 16));
                String status = opponent.getGrid().getValue(x, y);

                if (status.equalsIgnoreCase(".")) {
                    return shotId;
                }
            }
        }
        return "0x0";
    }


}
