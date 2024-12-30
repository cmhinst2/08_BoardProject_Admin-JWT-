import axios from "axios";
import { authUtils } from "../components/AuthProvider";

export const axiosApi = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true, // 쿠키 포함 설정
  // 이 설정이 되어있으면 서버에서도 credential을 허용하겠다는 설정이 필요함
  headers: {
    "Content-Type": "application/json",
  },
});

const REFRESH_URL = "http://localhost:8080/auth/refresh";

// access token 재발급
const getRefreshToken = async () => {
  // const { handleLogout } = useContext(AuthContext); // useContext는 컴포넌트에서만 사용가능!!
  try {
    const res = await axios.post(REFRESH_URL, {}, { withCredentials: true });
    const accessToken = res.data?.accessToken;
    return accessToken;
  } catch (e) {
    // access token 재발급 중 예외 발생한 경우
    // -> 리프레시 토큰에 문제가 있는 경우..
    // -> 로그아웃 유도 후 새로 로그인하도록 해야함
    console.log("여기까진옴", e);

    // handleLogout 호출 - 이제 일반 axios를 사용하므로 순환 호출 없음
    await authUtils.handleLogout();

    alert("세션이 만료되었습니다. 다시 로그인해주세요.");
    return null;
  }
};

// 요청 인터셉터
axiosApi.interceptors.request.use(
  (config) => {
    const accessToken = localStorage.getItem("accessToken");
    // 요청 보내기 전에 localStorage에 있는 accessToken을 꺼내어 요청 헤더에 담아줌
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    // 인증이 필요하지 않은 요청일 수 있음.
    // 인증이 필요한 요청이라면, 서버에서 이를 처리하기 위해 추가적인 로직 구현하면 됨.
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 대기 중인 요청을 저장할 배열
let isRefreshing = false;
let refreshSubscribers = [];

// 대기 중인 모든 요청들을 처리하는 함수(토큰을 갱신한 후에 호출되어, 대기 중이던 모든 요청들을 한번에 처리)
// 성공/실패 모두 처리할 수 있도록 구현
const processQueue = (token = null, error = null) => { 
          // 매개변수 token: 새로 받은 토큰 (성공했을 때) , error: 발생한 에러 (실패했을 때)
  refreshSubscribers.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });

  refreshSubscribers = [];
};

/* 토큰 갱신 로직 정리
1. 첫 번째 401 요청이 토큰 갱신을 시작
2. 갱신 중에 들어오는 모든 401 요청은 큐에 추가
3. 토큰 갱신 완료 후:
  - 첫 번째 실패했던 요청을 재시도
  - 큐에 있던 모든 요청들도 새 토큰으로 재시도

ex)  

/admin/maxReadCount가 401을 받고 토큰 갱신(/auth/refresh) 시작
그 동안 들어온 /admin/maxLikeCount와 /admin/maxCommentCount 요청은 큐에 대기

토큰 갱신 완료되면 /admin/maxReadCount 재요청
큐에 있던 나머지 두 요청도 새 토큰으로 자동 재요청

모든 요청이 순차적으로 처리되면서도, 불필요한 토큰 갱신은 방지함
*/

// 응답 인터셉터
axiosApi.interceptors.response.use(
  (response) => response, // 정상 응답인 경우 그대로 반환
  async (error) => {
    // 에러 발생한 경우의 처리
    const originalRequest = error.config; // 실패한 요청의 설정을 저장

    // 401(인증) 에러가 아닌 경우는 그대로 에러 반환
    if (error.response?.status !== 401) {
      return Promise.reject(error);
    }

    // _retry flag가 있다면 이미 재시도된 요청이므로 에러 반환
    // 무한 재시도 방지
    if (originalRequest._retry) {
      return Promise.reject(error);
    }

    // 현재 토큰 갱신이 진행 중인 경우
    if (isRefreshing) {
      // 맨 처음은 false라 if문 안거치고 아래로 내려감.
      // 새로운 Promise를 만들어 현재 요청을 대기열에 추가

      // Promise는 비동기 작업의 최종 완료(또는 실패)를 나타내는 객체
      return new Promise((resolve, reject) => {
        // Promise 객체는 두 개의 함수를 매개변수로 받음: resolve와 reject
        // resolve: 작업이 성공했을 때 호출하는 함수
        // reject: 작업이 실패했을 때 호출하는 함수
        refreshSubscribers.push({ // refreshSubscribers 배열에 이 Promise를 추가함
          resolve: (token) => { // 토큰 갱신이 성공하면 resolve 함수가 호출되어 새 토큰으로 요청을 재시도
            originalRequest.headers.Authorization = `Bearer ${token}`;
            resolve(axiosApi(originalRequest));
          },
          reject,  // 토큰 갱신이 실패하면 이 reject 함수가 호출되어 에러를 전달.
        });
      });
    }

    // 토큰 갱신이 진행중이 아닌 경우
    originalRequest._retry = true; // 재시도 표시
    isRefreshing = true;  // 토큰 갱신 진행중 표시

    try {
       // 새로운 토큰 요청
       const newToken = await getRefreshToken();
       if (newToken) {
        // 새 토큰을 로컬 스토리지에 저장
        localStorage.setItem("accessToken", newToken);
        // 실패했던 요청의 헤더를 새 토큰으로 업데이트
        originalRequest.headers.Authorization = `Bearer ${newToken}`;

        // 대기 중인 다른 요청들도 새 토큰으로 처리
        processQueue(newToken);

        // 처음 실패한 요청을 새 토큰으로 재시도
        return axiosApi(originalRequest);

      } else {

        // 토큰 갱신 실패시 대기중인 모든 요청에 에러 전달
        processQueue(null, new Error("토큰 갱신 실패!"));
        return Promise.reject(error);
      }

    } catch (refreshError) {
      // 토큰 갱신 중 에러 발생시 대기중인 모든 요청에 에러 전달
      processQueue(null, refreshError);
      return Promise.reject(refreshError);
    } finally {
      // 토큰 갱신 작업 완료 표시
      isRefreshing = false;
    }
  }
);