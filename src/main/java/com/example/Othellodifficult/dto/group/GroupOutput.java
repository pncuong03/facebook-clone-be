package com.example.Othellodifficult.dto.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupOutput {
    private Long id;
    private String name;
    private Integer memberCount;
//    private Boolean isMember;
//    private Boolean isRequested;
}
