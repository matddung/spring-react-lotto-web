import React, { useState, useEffect } from 'react';
import { Route, Routes, useNavigate } from 'react-router-dom';
import AppHeader from '../common/AppHeader';
import Home from '../home/Home';
import Login from '../user/login/Login';
import Signup from '../user/signUp/SignUp';
import Profile from '../user/profile/Profile';
import QuestionService from '../question/page/QuestionPage';
import OAuth2RedirectHandler from '../user/oauth2/OAuth2RedirectHandler';
import NotFound from '../common/NotFound';
import LoadingIndicator from '../common/LoadingIndicator';
import AdminPage from '../user/admin/page/AdminPage';
import WekaPage from '../user/weka/WekaPage'
import { getCurrentUser } from '../util/UserAPIUtils';
import { ACCESS_TOKEN, REFRESH_TOKEN } from '../constants';
import PrivateRoute from '../common/PrivateRoute';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './App.css';

const App = () => {
  const [authenticated, setAuthenticated] = useState(false);
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const loadCurrentlyLoggedInUser = () => {
    const token = localStorage.getItem(ACCESS_TOKEN);

    if (!token || token === 'null') {
      setLoading(false);
      return;
    }

    getCurrentUser()
      .then(response => {
        setCurrentUser(response);
        setAuthenticated(true);
        setLoading(false);
      }).catch(() => {
        localStorage.removeItem(ACCESS_TOKEN);
        localStorage.removeItem(REFRESH_TOKEN);
        setLoading(false);
      });
  };

  const handleLogout = () => {
    localStorage.removeItem(ACCESS_TOKEN);
    localStorage.removeItem(REFRESH_TOKEN);
    setAuthenticated(false);
    setCurrentUser(null);
    setLoading(false);

    navigate('/', { state: { fromLogout: true } });
  };

  const handleLoginSuccess = () => {
    loadCurrentlyLoggedInUser();
    toast.success("로그인에 성공하였습니다.");
  };

  const handleLoginFailure = (error) => {
    toast.error(<div>로그인에 실패하였습니다.<br />이메일 또는 비밀번호를 확인해주세요. </div>);
  };

  const updateCurrentUser = (newUser) => {
    setCurrentUser(newUser);
  };

  useEffect(() => {
    loadCurrentlyLoggedInUser();
  }, []);

  if (loading) {
    return <LoadingIndicator />;
  }

  return (
    <div className="app">
      <div className="app-top-box">
        <AppHeader authenticated={authenticated} onLogout={handleLogout} currentUser={currentUser} />
      </div>
      <div className="app-body">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/profile" element={
            <PrivateRoute authenticated={authenticated}>
              <Profile currentUser={currentUser} updateCurrentUser={updateCurrentUser} onLogout={handleLogout} />
            </PrivateRoute>
          } />
          <Route path='/question-service' element={
            <PrivateRoute authenticated={authenticated}>
              <QuestionService />
            </PrivateRoute>
          } />
          <Route path='/recommended-num' element={
            <PrivateRoute authenticated={authenticated}>
              <WekaPage />
            </PrivateRoute>
          } />
          <Route path="/login" element={<Login authenticated={authenticated} onLoginSuccess={handleLoginSuccess} onLoginFailure={handleLoginFailure} />} />
          <Route path="/signup" element={<Signup authenticated={authenticated} />} />
          <Route path="/oauth2/redirect" element={
            <OAuth2RedirectHandler onLoginSuccess={handleLoginSuccess} onLoginFailure={handleLoginFailure} />
          } />
          {currentUser && currentUser.data.role === 'ADMIN' && (
            <Route path="/admin" element={
              <PrivateRoute authenticated={authenticated}>
                <AdminPage />
              </PrivateRoute>
            } />
          )}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </div>
      <ToastContainer limit={3} autoClose={2000} position="top-right" />
    </div>
  );
};

export default App;