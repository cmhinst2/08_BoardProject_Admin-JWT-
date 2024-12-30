package edu.kh.admin.main.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Board {
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private String boardWriteDate;
	private String boardUpdateDate;
	private int readCount;
	private String boardDelFl;
	private int memberNo;
	private int boardCode;
	
	// BOARD_TYPE 테이블 조인
	private String boardName; // 게시판 종류 이름
	
	// MEMBER 테이블 조인
	private String memberNickname;
	
	// BOARD_LIKE 테이블 조인 좋아요 수
	private int likeCount;
	
	// COMMENT 테이블 조인 댓글 수
	private int commentCount;
}
