package com.kh.Backend_miniproject.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BoardVO {
    private int boardNum;
    private String boardName;

    public BoardVO(int boardNum, String boardName) {
        this.boardNum = boardNum;
        this.boardName = boardName;
    }
}
