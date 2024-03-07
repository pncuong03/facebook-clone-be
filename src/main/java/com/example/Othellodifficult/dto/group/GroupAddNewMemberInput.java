package com.example.Othellodifficult.dto.group;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupAddNewMemberInput {
    @NonNull
    private Long groupId;
    @Size(min = 1)
    private List<Long> userIds;
}
