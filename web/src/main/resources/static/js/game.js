// we wait till page is loaded
document.addEventListener("DOMContentLoaded", (event) => {

    updatePageAfterAiFirstTurn();

});

function updatePageAfterAiFirstTurn() {
    var opponentId = document.getElementById("opponentId").value;
    var playerTurnId = document.getElementById("playerTurnId").value;
    var gameId = document.getElementById("gameId").value;

    if (opponentId === playerTurnId) { // AI made first turn so we need to update our page
        updateMyGrid(gameId);
    }
}

function addShot(event) {
    var userId = document.getElementById("userId").value;
    var playerTurnId = document.getElementById("playerTurnId").value;

    if (userId == playerTurnId) { // ignore mouse clicks while it's not your turn
        var aliveShips = document.getElementById("aliveShips").value;

        var imgId = event.id;

        document.getElementById(imgId).removeAttribute("onclick");
        document.getElementById(imgId).setAttribute("class", "shot");

        // get salvo
        var salvo = document.getElementById("salvo").value;
        salvo = salvo + imgId;

        // get salvoCount
        var salvoCount = parseInt(document.getElementById("salvoCount").value);
        // update salvoCount
        salvoCount = salvoCount + 1;
        // save salvoCount
        document.getElementById("salvoCount").value = salvoCount;

        console.log("salvoCount = " + salvoCount + " aliveShips = " + aliveShips);
        if (salvoCount >= aliveShips) {
            // send required salvo to backend
            sendFireRequest(salvo);
            // reset salvoCount
            document.getElementById("salvoCount").value = 0;
        } else {
            // update salve
            salvo = salvo + ",";
        }
        // save salvo
        document.getElementById("salvo").value = salvo;
    }
}

function sendFireRequest(salvo) {
    var gameId = document.getElementById("gameId").value;

    var array = new Array();
    array = salvo.split(",");

    var fireRequest = {}
    fireRequest["salvo"] = array;

    var httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = function() {
        if (httpRequest.readyState == XMLHttpRequest.DONE) { // XMLHttpRequest.DONE == 4
            if (httpRequest.status == 200) {
                var fireResponse = JSON.parse(httpRequest.responseText);
                var game = fireResponse.game;
                var salvo = fireResponse.salvo;

                // update opponent grid
                for (const [key, value] of Object.entries(salvo)) {
                    if (value === "hit" || value === "kill") { // from backend
                        document.getElementById(key).setAttribute("class", "sunk");
                    }
                }

                updateGameTurn(game);

                // drop salvo value to empty
                document.getElementById("salvo").value = "";

                if (game.player_turn === 'AI') {
                    updateMyGrid(gameId);
                }
            } else {
                var jsonError = JSON.parse(httpRequest.responseText);
                alert(jsonError.error_message); // show message
                // very crude approach as we are trying to resolve it with page update
                window.location.href = "/gameId/" + gameId;
            }
        }
    };

    httpRequest.open("PUT", "/xl-spaceship/user/game/" + gameId + "/fire", true);
    httpRequest.setRequestHeader('Content-Type', 'application/json; charset=utf-8');
    httpRequest.send(JSON.stringify(fireRequest));
}

function updateMyGrid(gameId) {
    setTimeout(() => {
        var httpRequest = new XMLHttpRequest();
        httpRequest.onreadystatechange = function() {
            if (httpRequest.readyState == XMLHttpRequest.DONE) { // XMLHttpRequest.DONE == 4
                if (httpRequest.status == 200) {
                    var gameStatus = JSON.parse(httpRequest.responseText);

                    var myGrid = document.getElementById('myGrid');
                    myGrid.innerHTML = gameStatus.grid;

                    updateGameTurn(gameStatus.game);

                    if (gameStatus.aliveShips) { // check is not nullish
                        document.getElementById("aliveShips").value = gameStatus.aliveShips;
                    }
                } else {
                    var jsonError = JSON.parse(httpRequest.responseText);
                    alert(jsonError.error_message); // show message
                    // very crude approach as we are trying to resolve it with page update
                    window.location.href = "/gameId/" + gameId;
                }
            }
        };

        httpRequest.open("GET", "/xl-spaceship/user/game/" + gameId + "/status");
        httpRequest.setRequestHeader('Content-Type', 'application/json; charset=utf-8');
        httpRequest.send();
    }, 100); // update my grid after 100 ms delay
}

function updateGameTurn(gameTurn) {
    //  ╭─ nullish ──────╮ ╭─ not nullish ─────────────────────────────────╮
    // ┌───────────┬──────┬───────┬───┬────┬─────┬──────┬───┬─────────┬─────┐
    // │ undefined │ null │ false │ 0 │ "" │ ... │ true │ 1 │ "hello" │ ... │
    // └───────────┴──────┴───────┴───┴────┴─────┴──────┴───┴─────────┴─────┘
    //  ╰─ falsy ───────────────────────────────╯ ╰─ truthy ───────────────╯
    if (gameTurn.player_turn) { // check is not nullish
        document.getElementById("playerTurnId").value = gameTurn.player_turn;
    }

    if (gameTurn.won) { // check is not nullish
        document.getElementById("wonId").value = gameTurn.won;
    }

    checkGameOver();
}

function checkGameOver() {
    var userId = document.getElementById("userId").value;
    var opponentId = document.getElementById("opponentId").value;
    var wonId = document.getElementById("wonId").value;

    if (userId === wonId) {
        alert("You won!");
        proposeNewGame();
    } else if (opponentId === wonId) {
        alert("You lost!");
        proposeNewGame();
    }
}

function proposeNewGame() {
    if (confirm('Would you like to start a new game?')) {
        createNewGame();
    } else {
        // load index page
        window.location = "/";
    }
}

function createNewGame() {
    var gameId = document.getElementById("gameId").value;

    var httpRequest = new XMLHttpRequest();

    httpRequest.onreadystatechange = function() {
        if (httpRequest.readyState == XMLHttpRequest.DONE) { // XMLHttpRequest.DONE == 4
            if (httpRequest.status == 200) {
                var id = JSON.parse(httpRequest.responseText).game_id;
                window.location.href = "/gameId/" + id;
            } else {
                var jsonError = JSON.parse(httpRequest.responseText);
                alert(jsonError.error_message); // very crude approach
            }
        }
    };

    httpRequest.open("GET", "/xl-spaceship/user/game/" + gameId + "/new", true);
    httpRequest.setRequestHeader('Content-Type', 'application/json; charset=utf-8');
    httpRequest.send();
}