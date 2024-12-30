package edu.kh.admin.main.model.service;

import java.util.List;

import edu.kh.admin.main.model.dto.Board;
import edu.kh.admin.main.model.dto.Member;

public interface AdminService {

	/** 관리자 로그인
	 * @param inputMember
	 * @return
	 */
	Member login(Member inputMember);
	
	/** 새로운 가입 회원 조회
	 * @return
	 */
	List<Member> getNewMember();

	/** 탈퇴 회원 조회
	 * @return
	 */
	List<Member> selectWithdrawnMemberList();

	/** 탈퇴 회원 복구
	 * @param memberNo 
	 * @return
	 */
	int restoreMember(int memberNo);

	/** 삭제된 게시글 목록 조회
	 * @return
	 */
	List<Board> selectDeleteBoardList();

	/** 삭제 게시글 복구
	 * @param boardNo
	 * @return
	 */
	int restoreBoard(int boardNo);

	/** 게시글 최대 조회 수
	 * @return
	 */
	Board maxReadCount();

	/** 게시글 최대 좋아요 수
	 * @return
	 */
	Board maxLikeCount();

	/** 게시글 최대 댓글 수
	 * @return
	 */
	Board maxCommentCount();

	/** 관리자 계정 발급
	 * @param member
	 * @return
	 */
	String createAdminAccount(Member member);

	/** 관리자 계정 목록
	 * @return
	 */
	List<Member> adminAccountList();


}
