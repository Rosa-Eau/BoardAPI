package org.sparta.homework.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
public class BoardRequestDTO {

    private  String pw;
    private String name;
    private String title;
    private String content;
    private Date createdAt;

}
