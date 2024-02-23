package org.sparta.homework.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sparta.homework.dto.BoardRequestDTO;
import org.sparta.homework.dto.BoardResponseDTO;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
public class Board {

    private int no;
    private String name;
    private  String title;
    private String pw;
    private String content;
    private Date createdAt;

    public Board(BoardRequestDTO requestDTO) {
        this.name = requestDTO.getName();
        this.pw = requestDTO.getPw();
        this.title = requestDTO.getTitle();
        this.content = requestDTO.getContent();
        this.createdAt = requestDTO.getCreatedAt();
    }

    public Board(BoardResponseDTO responseDTO) {
        this.no = responseDTO.getNo();
        this.name = responseDTO.getName();
        this.pw = responseDTO.getPw();
        this.title = responseDTO.getTitle();
        this.content = responseDTO.getContent();
        this.createdAt = responseDTO.getCreatedAt();
    }
}
