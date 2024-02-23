package org.sparta.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.sparta.homework.dto.BoardRequestDTO;
import org.sparta.homework.dto.BoardResponseDTO;
import org.sparta.homework.entity.Board;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
@Slf4j
public class BoardController {

    private final JdbcTemplate jdbcTemplate;

    public BoardController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 게시글 작성 기능
    @PostMapping("/board")
    public BoardResponseDTO createBoard(@RequestBody BoardRequestDTO requestDTO) {
        // RequestDTO -> Entity
        Board board = new Board(requestDTO);

        // DB 저장
        KeyHolder keyHolder = new GeneratedKeyHolder(); // 기본 키를 반환받기 위한 객체

        String sql = "INSERT INTO BOARD (name, password, title, content, created_at) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update( con -> {
                    PreparedStatement preparedStatement = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);

                    preparedStatement.setString(1, board.getName());
                    preparedStatement.setString(2, board.getPw());
                    preparedStatement.setString(3, board.getTitle());
                    preparedStatement.setString(4, board.getContent());
                    preparedStatement.setDate(5, board.getCreatedAt());
                    return preparedStatement;
                },
                keyHolder);

        // DB Insert 후 받아온 기본키 확인
        int no = Objects.requireNonNull(keyHolder.getKey()).intValue();

        board.setNo(no);

        // Entity -> ResponseDTO
        return new BoardResponseDTO(board);
    }

    // 선택한 게시글 조회 기능
    @GetMapping("/board/{no}")
    public BoardResponseDTO getBoardNo(@PathVariable int no) {
        // 해당 게시글이 DB에 존재하는지 확인
        Board board = findByNo(no);

        if(board != null) {
            // BoardResponseDTO에 PK 값으로 불러온 board를 담아서 반환
            return new BoardResponseDTO(board);
        } else {
            throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다.");
        }
    }

    // 게시글 목록 조회 기능
    @GetMapping("/boards")
    public List<BoardResponseDTO> getBoards() {
        // DB 조회
        String sql = "SELECT * FROM BOARD";

        return jdbcTemplate.query(sql, new RowMapper<BoardResponseDTO>() {
            @Override
            public BoardResponseDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                // SQL 의 결과로 받아온 Board 데이터들을 BoardResponseDTO 타입으로 변환해줄 메서드
                int no = rs.getInt("NO");
                String name = rs.getString("NAME");
                String title = rs.getString("TITLE");
                String content = rs.getString("CONTENT");
                Date createdAt = rs.getDate("CREATED_AT");
                return new BoardResponseDTO(no, name, title, content, createdAt);
            }
        });
    }

    // 선택한 게시글 수정 기능
    @PutMapping("/board/{no}")
    public BoardResponseDTO updateBoard(@PathVariable int no, @RequestBody BoardRequestDTO requestDTO) {
        // 해당 게시글이 DB에 존재하는지 확인
        Board board = findByNo(no);
        if(board != null) {
            boolean checkPw = checkPw(no, requestDTO);
            // 패스워드 맞는지 확인
            if (checkPw) {
                // 맞으면 board 내용 수정
                String sql = "UPDATE BOARD SET NAME = ?, TITLE = ?, CONTENT = ? WHERE NO = ?";
                jdbcTemplate.update(sql, requestDTO.getName(), requestDTO.getTitle(), requestDTO.getContent(), no);

                board = findByNo(no);
                return new BoardResponseDTO(board);
            } else {
                throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
            }

        } else {
            throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다.");
        }
    }

    // 선택한 게시글 삭제 기능
    @DeleteMapping("/board/{no}")
    public int deleteBoard(@PathVariable int no, @RequestBody BoardRequestDTO requestDTO) {
        // 해당 게시글이 DB에 존재하는지 확인
        Board board = findByNo(no);
        if(board != null) {
            boolean checkPw = checkPw(no, requestDTO);
            // 비밀번호 맞는지 확인
            if (checkPw) {
                // 맞으면 board 삭제
                String sql = "DELETE FROM BOARD WHERE NO = ?";
                jdbcTemplate.update(sql, no);
                return no;
            } else {
                throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
            }
        } else {
            throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다.");
        }
    }

    // PK로 조회하기
    private Board findByNo(int no) {
        // DB 조회
        String sql = "SELECT * FROM BOARD WHERE NO = ?";

        // 패스워드 제외하고 가져오기
        return jdbcTemplate.query(sql, resultSet -> {
            if(resultSet.next()) {
                Board board = new Board();
                board.setNo(resultSet.getInt("NO"));
                board.setName(resultSet.getString("NAME"));
                board.setTitle(resultSet.getString("TITLE"));
                board.setContent(resultSet.getString("CONTENT"));
                board.setCreatedAt(resultSet.getDate("CREATED_AT"));
                return board;
            } else {
                return null;
            }
        }, no);
    }

    // 비밀번호 맞는지 체크
    private boolean checkPw(int no, BoardRequestDTO requestDTO) {
        boolean result = false;

        String sql = "SELECT COUNT(*) FROM BOARD WHERE NO = ? AND PASSWORD = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, no, requestDTO.getPw());

        if (count.equals(1)) {
            result = true;
        }

        return result;
    }
}