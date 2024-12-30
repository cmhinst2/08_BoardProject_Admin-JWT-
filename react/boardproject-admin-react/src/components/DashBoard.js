import React, { useState, useContext } from "react";
import "../css/DashBoard.css";
import Restore from "./Restore.js";
import Manager from "./Manager.js";
import Statistics from "./Statistics.js";
import { AuthContext } from "./AuthProvider.js";
import { NavLink, Route, Routes } from "react-router";

// 상태값에 따라 렌더링할 컴포넌트를 변경하는 방법
export default function DashBoard() {
  const globalState = useContext(AuthContext); // 전역 상태

  const [menu, setMenu] = useState(1); // 1: 통계, 2: 복구 , 3:관리자메뉴

  // 메뉴탭 버튼 이벤트 함수
  const handleMenu = (e, number) => {
    // 모든 li 요소의 클래스를 초기화
    const allTabs = document.querySelectorAll(".tab-box li");
    allTabs.forEach((tab) => tab.classList.remove("active"));

    // 클릭된 li 요소에 클래스를 추가하여 스타일 변경
    e.target.classList.add("active");

    setMenu(number);
  };

  return (
    <div className="dash-board-container">
      <h1>관리자 페이지</h1>

      <div className="admin-info">
        <p>현재 접속 관리자 : {globalState.user.memberNickname}</p>
        <button onClick={globalState.handleLogout}>로그아웃</button>
      </div>

      <ul className="tab-box">
        <li className="active" onClick={(e) => handleMenu(e, 1)}>
          통계
        </li>
        <li onClick={(e) => handleMenu(e, 2)}>복구</li>
        <li onClick={(e) => handleMenu(e, 3)}>관리자 메뉴</li>
      </ul>

      {menu === 1 && <Statistics />}
      {menu === 2 && <Restore />}
      {menu === 3 && <Manager />}
    </div>
  );
}

// react-router-dom 이용한 라우팅 방법
// react-router-dom : React 애플리케이션에서 라우팅을 구현하기 위해 사용하는 라이브러리
export function DashBoardRouter() {
  const globalState = useContext(AuthContext); // 전역 상태

  return (
    <div className="dash-board-container">
      <h1>관리자 페이지</h1>

      <div className="admin-info">
        <p>현재 접속 관리자 : {globalState.user.memberNickname}</p>
        <button onClick={globalState.handleLogout}>로그아웃</button>
      </div>

      {/* react-router-dom 설치 */}
      <div className="router-tab-box">
       
          <NavLink to="/restore">복구</NavLink>
      
          <NavLink to="/statistics">통계</NavLink>
        
          <NavLink to="/manager">관리자 메뉴</NavLink>
        
      </div>

      {/* Route를 이용하여 각 컴포넌트 연결 */}
      <Routes>
        <Route path="/" element={<h1>DashBoard 메인</h1>}/>
        <Route path="/restore" element={<Restore />} />
        <Route path="/statistics" element={<Statistics />} />
        <Route path="/manager" element={<Manager />} />
      </Routes>
    </div>
  );
}
