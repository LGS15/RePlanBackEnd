package com.replan.domain.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotePayload {
    private String noteId;
    private String content;
    private Long videoTimestamp;
    private String authorName;
}
