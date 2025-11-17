package com.example.thisplay.common.rec_list.DTO;

import com.example.thisplay.common.moviepage.DTO.MovieDTO;
import com.example.thisplay.common.rec_list.entity.FolderVisibility;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViewFolderDTO {
    private Long FolderId;
    private String FolderName;
    private FolderVisibility visibility;
    private List<MovieDTO> movies;
}
