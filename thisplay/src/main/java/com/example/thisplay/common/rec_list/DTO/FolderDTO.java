package com.example.thisplay.common.rec_list.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolderDTO {
    private Long folderId;
    private String folderName;
    private String userNickname;
}
