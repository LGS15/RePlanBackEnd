package com.replan.domain.websocket;

public enum MessageType {
    PLAY,
    PAUSE,
    SEEK,
    NOTE_ADDED,
    NOTE_UPDATED,
    NOTE_DELETED,
    USER_JOINED,
    USER_LEFT,
    SYNC_REQUEST,
    SYNC_RESPONSE
}
