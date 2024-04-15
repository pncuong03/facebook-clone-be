package com.example.Othellodifficult.dto.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GroupOutputAndTag {
    private Long idGroup;
    private String name;
    private Integer memberCount;
    private List<String> tagList;
}
