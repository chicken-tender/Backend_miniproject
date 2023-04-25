package com.kh.Backend_miniproject.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class BoardVO {
    private int boardNum;
    private String boardName;

    public BoardVO(String boardName) {
        this.boardName = boardName;
    }
}
