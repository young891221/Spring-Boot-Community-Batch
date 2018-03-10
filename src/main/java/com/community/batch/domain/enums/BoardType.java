package com.community.batch.domain.enums;

/**
 * Created by KimYJ on 2017-07-12.
 */
public enum BoardType {
    NOTICE("공지사항"),
    FREE("자유게시판");

    private String value;

    BoardType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
