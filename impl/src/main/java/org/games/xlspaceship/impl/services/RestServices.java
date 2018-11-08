package org.games.xlspaceship.impl.services;

import org.games.xlspaceship.impl.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;

@Service
public class RestServices {

    private static final Logger log = LoggerFactory.getLogger(XLSpaceshipServices.class);

    private static final String NEW_GAME_REQUEST = "http://%s:%s/xl-spaceship/protocol/game/new";
    private static final String FIRE_REQUEST = "http://%s:%s/xl-spaceship/protocol/game/%s";
    private static final String FIRE_REQUEST_AI = "http://%s:%s/xl-spaceship/user/game/%s/fire";

    @Autowired
    private Environment environment;

    @Autowired
    private UserServices userServices;

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    public int getCurrentPort() {
        return Integer.parseInt(environment.getProperty("server.port"));
    }

    public String getCurrentHostname() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.error(e.getMessage());
            return "localhost";
        }
    }

    public NewGameResponse sendPostNewGameRequest(String remoteHost, int remotePort, SpaceshipProtocol spaceshipProtocol) {
        String url = String.format(NEW_GAME_REQUEST, remoteHost, remotePort);

        NewGameRequest newGameRequest = new NewGameRequest();
        newGameRequest.setUserId(userServices.getUserId());
        newGameRequest.setFullName(userServices.getFullName());
        newGameRequest.setSpaceshipProtocol(spaceshipProtocol);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NewGameRequest> httpEntity = new HttpEntity<>(newGameRequest, headers);
        return restTemplate.postForObject(url, httpEntity, NewGameResponse.class);
    }

    public FireResponse fireShot(String remoteHost, int remotePort, String gameId, FireRequest fireRequest) {
        String url = String.format(FIRE_REQUEST, remoteHost, remotePort, gameId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FireRequest> httpEntity = new HttpEntity<>(fireRequest, headers);
        return restTemplate.postForObject(url, httpEntity, FireResponse.class);
    }

    public FireResponse fireShotByAi(String localHost, int localPort, String gameId, FireRequest fireRequest) {
        String url = String.format(FIRE_REQUEST_AI, localHost, localPort, gameId);

        HttpEntity<FireRequest> entity = new HttpEntity<>(fireRequest);
        ResponseEntity<FireResponse> response = restTemplate.exchange(url, HttpMethod.PUT, entity, FireResponse.class);
        return response.getBody();
    }

}
