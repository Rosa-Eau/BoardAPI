package org.sparta.homework.dto;

import lombok.Getter;
import org.sparta.homework.entity.Board;

import java.sql.Date;

@Getter
public class BoardResponseDTO {

    private int no;
    private String name;
    private String pw;
    private  String title;
    private String content;
    private Date createdAt;

    public BoardResponseDTO(Board board) {
        this.no = board.getNo();
        this.name = board.getName();
        this.pw = board.getPw();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.createdAt = board.getCreatedAt();
    }

    public BoardResponseDTO(int no, String name, String title, String content, Date createdAt) {
        this.no = no;
        this.name = name;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }
}
