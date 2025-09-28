package com.example.thisplay.common.rec_list.DTO;

import com.example.thisplay.common.moviepage.DTO.MovieDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViewFolderDTO {
    private Long FolderId;
    private String FolderName;
    private List<MovieDTO> movies;
}
