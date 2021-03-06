package com.game.exception;

public interface ExceptionMessageConstant {

    String PLAYER_ACCESS_NOT_PERMITTED_GAME = "Player trying to access a not permitted mini game, playerId: %s, miniGameId: %s";
    String PLAYER_ACCESS_NOT_IN_RADIUS_OF_GAME = "Player not in radius of mini game, playerId: %s, miniGameId: %s";
    String MINI_GAME_NOT_FOUND = "MiniGame not found, miniGameId: %s";
    String MINI_GAME_ENDED = "MiniGame already end, miniGameId: %s";
    String RANDOM_PLACE_NOT_FOUND = "Random place not found";
    String RANDOM_SITUATION_NOT_FOUND = "Random situation not found, placeId: %s";
    String ACTION_NOT_FOUND = "Action not found, actionId: %s";
    String EVENT_NOT_FOUND = "Event not found, eventId: %s";

}
