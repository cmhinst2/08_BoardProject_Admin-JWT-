import { useEffect, useState } from "react";
import { axiosApi } from "../api/axiosAPI";

export default function Manager() {
  const [email, setEmail] = useState("");
  const [nickname, setNickname] = useState("");
  const [tel, setTel] = useState("");
  const [accountList, setAccountList] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  // 관리자 계정 발급
  const createAdminAccount = async () => {
    if (email.length === 0 || nickname.length === 0 || tel.length === 0) {
      alert("모든 필드를 입력해주세요");
      return;
    }

    try {
      const response = await axiosApi.post("/admin/createAdminAccount", {
        memberEmail: email,
        memberNickname: nickname,
        memberTel: tel,
      });

      console.log(response);

      if (response.status === 201) {
        const result = response.data; // 서버 응답 데이터
        alert(
          `발급된 비밀번호는 ${result} 입니다. 다시 확인할 수 없으니 저장해주시기 바랍니다.`
        );
        console.log(result); // 조관리 : aTA5UL

        initInputValue(); // 입력 필드 초기화
        getAdminAccountList(); // 계정 목록 새로고침
      }
    } catch (error) {
      console.error("관리자 계정 등록 중 에러 발생:", error);
    }
  };

  // gWyKZV

  // 인풋창 값 비우기
  const initInputValue = () => {
    setEmail("");
    setNickname("");
    setTel("");
  };

  // 관리자 계정 목록 조회
  const getAdminAccountList = async () => {
    try {
      const response = await axiosApi.get("/admin/adminAccountList");

      if (response.status === 200) {
        setAccountList(response.data);
      }
    } catch (error) {
      console.error("관리자 계정 목록 조회 중 에러 발생:", error);
    }
  };

  useEffect(() => {
    getAdminAccountList();
  }, []);

  useEffect(() => {
    if (accountList != null) {
      setIsLoading(false);
    }
  }, [accountList]);

  if (isLoading) {
    return <h1>Loading...</h1>;
  } else {
    return (
      <>
        <div className="manager-div">
          <section className="manager-section">
            <h2>관리자 계정 발급</h2>
            <table>
              <tbody>
                <tr>
                  <td>사용할 이메일 : </td>
                  <td>
                    <input
                      id="email"
                      type="email"
                      placeholder="ex) admin2@kh.or.kr"
                      onChange={(e) => setEmail(e.target.value)}
                    />
                  </td>
                </tr>
                <tr>
                  <td>사용할 이름 : </td>
                  <td>
                    <input
                      id="nickname"
                      type="text"
                      placeholder="ex) 관리자2"
                      onChange={(e) => setNickname(e.target.value)}
                    />
                  </td>
                </tr>
                <tr>
                  <td>사용할 전화번호 : </td>
                  <td>
                    <input
                      id="tel"
                      type="text"
                      placeholder="ex) 01012341234"
                      onChange={(e) => setTel(e.target.value)}
                    />
                  </td>
                </tr>
              </tbody>
            </table>
            <button className="issueBtn" onClick={createAdminAccount}>
              발급
            </button>
          </section>

          <section className="manager-section">
            <h2>관리자 계정 목록</h2>
            <table className="manager-list-table" border={1}>
              <thead>
                <tr>
                  <th>번호</th>
                  <th>이메일</th>
                  <th>관리자명</th>
                  <th>관리자 연락처</th>
                </tr>
              </thead>
              <tbody>
                {accountList.map((member, index) => {
                  return (
                    <tr key={index}>
                      <td>{member.memberNo}</td>
                      <td>{member.memberEmail}</td>
                      <td>{member.memberNickname}</td>
                      <td>{member.memberTel}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </section>
        </div>
      </>
    );
  }
}
