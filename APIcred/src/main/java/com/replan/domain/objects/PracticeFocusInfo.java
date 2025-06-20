package com.replan.domain.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticeFocusInfo {
    private String value;
    private String displayName;
    private String description;
}