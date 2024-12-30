import React, { useContext } from "react";
import Login from "./components/Login";
import DashBoard, { DashBoardRouter } from "./components/DashBoard";
import "./css/App.css";
import { AuthContext, AuthProvider } from "./components/AuthProvider";
import { BrowserRouter } from "react-router";

// ContextAPI 사용하는 방법 2가지
// 1. <AuthProvider> 안에서  <AuthContext.Consumer> 이용하는 방법
// -> <AuthContext.Consumer> 안에서 익명함수 형태로 user 꺼내어 사용
// function App() {

//   return (
//     <AuthProvider>
//       <AuthContext.Consumer>
//         {({ user }) =>
//           user ? (
//             <div className="body-container">
//               <DashBoard />
//             </div>
//           ) : (
//             <div className="login-section">
//               <Login />
//             </div>
//           )
//         }
//       </AuthContext.Consumer>
//     </AuthProvider>
//   );
// }

// 2. 컴포넌트 분리하여 하위 컴포넌트에서 useContext이용하는 방법
function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

// 별도의 컴포넌트로 분리
function AppContent() {
  // userContext이용하여 AuthContext 의 user 꺼내어 사용
  const { user } = useContext(AuthContext);

  return (
    <>
      {user ? (
        <div className="body-container">
          
          <BrowserRouter>
            <DashBoardRouter />
          </BrowserRouter>
         
          {/* <DashBoard /> */}
        </div>
      ) : (
        <div className="login-section">
          <Login />
        </div>
      )}
    </>
  );
}

export default App;
