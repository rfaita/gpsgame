package com.game.exception;

public interface ExceptionMessageConstant {

    String PLAYER_ACCESS_NOT_PERMITTED_GAME = "Player trying to access a not permitted mini game, playerId: %s, miniGameId: %s";
    String MINI_GAME_NOT_FOUND = "MiniGame not found, miniGameId: %s";
    String RANDOM_PLACE_NOT_FOUND = "Random place not found";
    String RANDOM_SITUATION_NOT_FOUND = "Random situation not found, placeId: %s";

}
