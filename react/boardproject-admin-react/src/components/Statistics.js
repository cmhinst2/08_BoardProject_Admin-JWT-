import { useEffect, useState } from "react";
import { axiosApi } from "../api/axiosAPI";

export default function Statistics() {
  const [readCountData, setReadCountData] = useState(null);
  const [likeCountData, setLikeCountData] = useState(null);
  const [commentCountData, setCommentCountData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  // 최대 조회 수 게시글 조회
  const getMaxReadCount = async () => {
    try {
      const response = await axiosApi.get("/admin/maxReadCount");
      setReadCountData(response.data); // 서버에서 받은 데이터로 상태 업데이트
    } catch (error) {
      console.error("최대 조회 수 게시글 조회 중 에러:", error);
    }
  };

  // 최대 좋아요 수 게시글 조회
  const getMaxLikeCount = async () => {
    try {
      const response = await axiosApi.get("/admin/maxLikeCount");
      setLikeCountData(response.data); // 서버에서 받은 데이터로 상태 업데이트
    } catch (error) {
      console.error("최대 좋아요 수 게시글 조회 중 에러:", error);
    }
  };

  // 최대 댓글 수 게시글 조회
  const getCommentCount = async () => {
    try {
      const response = await axiosApi.get("/admin/maxCommentCount");
      setCommentCountData(response.data); // 서버에서 받은 데이터로 상태 업데이트
    } catch (error) {
      console.error("최대 댓글 수 게시글 조회 중 에러:", error);
    }
  };


  // - Statistics 컴포넌트가 처음 렌더링될 때 콜백함수 내용 수행 
  // -> getMaxReadCount,getMaxLikeCount,getCommentCount 함수 실행
  useEffect(() => {
    getMaxReadCount();
    getMaxLikeCount();
    getCommentCount();
  }, []);

  // readCountData, likeCountData, commentCountData에 변화가 감지될 때 콜백함수 내용 수행
  useEffect(() => {
    if (
      readCountData != null &&
      likeCountData != null &&
      commentCountData != null
    ) {
      setIsLoading(false);
    }
  }, [readCountData, likeCountData, commentCountData]);

  if (isLoading) {
    return <h1>Loading...</h1>;

  } else {

    return (
      <div>
        <NewMembers />
        <section className="statistics-section">
          <h2>가장 조회수 많은 게시글</h2>
          <p>게시판 종류 : {readCountData.boardName}</p>
          <p>게시글 번호/제목 : No.{readCountData.boardNo} / {readCountData.boardTitle}</p>
          <p>게시글 조회 수 : {readCountData.readCount}</p>
          <p>작성자 닉네임 : {readCountData.memberNickname}</p>
        </section>

        <section className="statistics-section">
          <h2>가장 좋아요 많은 게시글</h2>
          <p>게시판 종류 : {likeCountData.boardName}</p>
          <p>게시글 번호/제목 : No.{likeCountData.boardNo} / {likeCountData.boardTitle}</p>
          <p>게시글 좋아요 수 : {likeCountData.likeCount}</p>
          <p>작성자 닉네임 : {likeCountData.memberNickname}</p>
        </section>

        <section className="statistics-section">
          <h2>가장 댓글 많은 게시글</h2>
          <p>게시판 종류 : {commentCountData.boardName}</p>
          <p>게시글 번호/제목 : No.{commentCountData.boardNo} / {commentCountData.boardTitle}</p>
          <p>게시글 댓글 수 : {commentCountData.commentCount}</p>
          <p>작성자 닉네임 : {commentCountData.memberNickname}</p>
        </section>
      </div>
    );
  }
}

// 신규 회원 조회
const NewMembers = () => {
  const [newMembers, setNewMembers] = useState([]);

  useEffect(() => {
    const fetchNewMembers = async () => {
      try {
        const response = await axiosApi.get("/admin/newMember");

        if(response.status === 200) {
          setNewMembers(response.data);
        }

      } catch (error) {
        console.error("신규 가입 회원 조회 중 에러 발생:", error);
      } 
    };

    fetchNewMembers();
  }, []);

  return (
    <div className="new-members">
      <h2>신규 가입 회원 ({newMembers.length}명)</h2>
      <h3>[ 7일 이내 가입회원 ]</h3>
      <table border={1}>
        <thead style={{backgroundColor : 'greenyellow'}}>
          <tr>
            <th>회원번호</th>
            <th>이메일</th>
            <th>닉네임</th>
            <th>가입일</th>
          </tr>
        </thead>
        <tbody>
          {newMembers?.map((member) => (
            <tr key={member.memberNo}>
              <td>{member.memberNo}</td>
              <td>{member.memberEmail}</td>
              <td>{member.memberNickname}</td>
              <td>{member.enrollDate}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};
