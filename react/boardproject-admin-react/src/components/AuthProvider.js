import React, { createContext, useEffect, useState } from "react";
import axios from "axios";
import { axiosApi } from "../api/axiosAPI";

export const AuthContext = createContext();
// Context는 React에서 컴포넌트 계층 구조(트리)를 통해 데이터를 효율적으로 전달하기 위한 메커니즘
// 컴포넌트 간에 전역 상태(Global State)를 공유할수 있는 컨텍스트를 생성.

export let authUtils = {
  setUser: null,  // 초기값을 null로 설정
};

// 전역 상태 제공자(Provider) 정의
export const AuthProvider = ({ children }) => {
  const [email, setEmail] = useState(""); // 현재 입력하는 이메일
  const [password, setPassword] = useState(""); // 현재 입력하는 패스워드
  const [user, setUser] = useState(() => {
    const storedUser = localStorage.getItem("userData");
    return storedUser ? JSON.parse(storedUser) : null; // JSON.parse로 객체로 변환, 없으면 null
  }); // 로그인한 계정 정보를 전역으로 관리할 user

  // 컴포넌트가 마운트될 때 setUser를 authUtils에 할당
  useEffect(() => {
    authUtils.setUser = setUser;
  }, [setUser]);

  // 이메일 입력 핸들러
  const changeInputEmail = (e) => {
    setEmail(e.target.value);
  };

  // 비밀번호 입력 핸들러
  const changeInputPw = (e) => {
    setPassword(e.target.value);
  };

  // 로그인 처리
  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await axiosApi.post("/auth/login", {
        memberEmail: email,
        memberPw: password,
      });

      const { accessToken, member } = response.data;

      // Access Token을 localStorage에 저장
      localStorage.setItem("accessToken", accessToken);

      // Refresh Token은 httpOnly 쿠키에 저장되므로 서버에서 자동으로 관리
      // 따라서 쿠키에서 직접적으로 접근할 필요는 없으며, Refresh Token을 관리하려면
      // 서버에서 새 Access Token을 발급하는 로직을 구현해야 함.

      console.log(member);
      /*
      localStorage : 
      - 브라우저를 닫아도 데이터가 영구적으로 유지
      - 브라우저 전역에서 사용(모든 탭과 창에서 공유됨)

      sessionStorage : 
      - 브라우저 탭 또는 창을 닫으면 데이터가 즉시 삭제
      - 현재 탭 또는 창에만 데이터가 유지됨
      */

      localStorage.setItem("userData", JSON.stringify(member));

      // 상태에 세팅
      setUser(member);
    } catch (error) {
      console.error("로그인 실패:", error);
    }
  };

  // 로그아웃 처리
  const handleLogout = async () => {
    try {
      await axios.post("http://localhost:8080/auth/logout", {}, {
        withCredentials: true
      });

    } catch (error) {
      console.error("로그아웃 중 예외 발생 ", error);
    } finally {
      // 상태 정리
      setUser(null);
      localStorage.removeItem("accessToken");
      localStorage.removeItem("userData");
    }
  };

  // 전역 객체에 handleLogout 등록
  authUtils.handleLogout = handleLogout;

  // user라는 상태와, 여러가지 이벤트핸들러(함수)를 묶어서
  // Provider를 통해 하위(자식) 컴포넌트로 데이터를 전달함
  const globalState = {
    user,
    changeInputEmail,
    changeInputPw,
    handleSubmit,
    handleLogout,
  };

  return (
    // AuthContext.Provider : 데이터를 제공하는 역할
    // 하위(자식) 컴포넌트는 이 Provider가 제공하는 데이터를 사용(소비 == Consumer)할 수 있음.
    <AuthContext.Provider value={globalState}>
      {/* 공유할 데이터  */}
      {children}
    </AuthContext.Provider>
  );
};
