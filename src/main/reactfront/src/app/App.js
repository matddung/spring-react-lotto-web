import React, { Component } from 'react';
import { Route, Routes } from 'react-router-dom';
import AppHeader from '../common/AppHeader';
import Home from '../home/Home';
import Login from '../user/login/Login';
import Signup from '../user/signUp/SignUp';
import Profile from '../user/profile/Profile';
import QuestionService from '../question/QuestionService';
import OAuth2RedirectHandler from '../user/oauth2/OAuth2RedirectHandler';
import NotFound from '../common/NotFound';
import LoadingIndicator from '../common/LoadingIndicator';
import { getCurrentUser } from '../util/UserAPIUtils';
import { ACCESS_TOKEN, REFRESH_TOKEN } from '../constants';
import PrivateRoute from '../common/PrivateRoute';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './App.css';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      authenticated: false,
      currentUser: null,
      loading: true
    };

    this.loadCurrentlyLoggedInUser = this.loadCurrentlyLoggedInUser.bind(this);
    this.handleLogout = this.handleLogout.bind(this);
    this.handleLoginSuccess = this.handleLoginSuccess.bind(this);
    this.handleLoginFailure = this.handleLoginFailure.bind(this);
    this.updateCurrentUser = this.updateCurrentUser.bind(this);
  }

  loadCurrentlyLoggedInUser() {
    console.log('Fetching current user...');
    getCurrentUser()
      .then(response => {
        console.log('User fetched successfully:', response);
        this.setState({
          currentUser: response,
          authenticated: true,
          loading: false
        });
      }).catch(error => {
        console.log('Error fetching user:', error);
        this.setState({
          loading: false
        });
      });
  }

  handleLogout() {
    console.log('Logging out...');
    localStorage.removeItem(ACCESS_TOKEN);
    localStorage.removeItem(REFRESH_TOKEN);
    this.setState({
      authenticated: false,
      currentUser: null
    });
    toast.success("로그아웃 했습니다.");
  }

  handleLoginSuccess() {
    console.log('Login successful');
    this.loadCurrentlyLoggedInUser();
    toast.success("로그인에 성공하였습니다.");
  }

  handleLoginFailure(error) {
    console.log('Login failed:', error);
    toast.error(`로그인에 실패하였습니다: ${error}`);
  }

  updateCurrentUser(newUser) {
    this.setState({ currentUser: newUser });
  }

  componentDidMount() {
    this.loadCurrentlyLoggedInUser();
  }

  render() {
    console.log('Rendering App with state:', this.state);
    if (this.state.loading) {
      return <LoadingIndicator />
    }

    return (
      <div className="app">
        <div className="app-top-box">
          <AppHeader authenticated={this.state.authenticated} onLogout={this.handleLogout} />
        </div>
        <div className="app-body">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/profile" element={
              <PrivateRoute authenticated={this.state.authenticated}>
                <Profile currentUser={this.state.currentUser} updateCurrentUser={this.updateCurrentUser} onLogout={this.handleLogout} />
              </PrivateRoute>
            } />
            <Route path='/question-service' element={
              <PrivateRoute authenticated={this.state.authenticated}>
                <QuestionService />
              </PrivateRoute>
            } />
            <Route path="/login" element={<Login authenticated={this.state.authenticated} onLoginSuccess={this.handleLoginSuccess} onLoginFailure={this.handleLoginFailure} />} />
            <Route path="/signup" element={<Signup authenticated={this.state.authenticated} />} />
            <Route path="/oauth2/redirect" element={
              <OAuth2RedirectHandler onLoginSuccess={this.handleLoginSuccess} onLoginFailure={this.handleLoginFailure} />
            } />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </div>
        <ToastContainer limit={3} autoClose={3000} position="top-right" />
      </div>
    );
  }
}

export default App;