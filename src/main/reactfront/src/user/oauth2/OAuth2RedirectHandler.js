import React, { Component } from 'react';
import { ACCESS_TOKEN, REFRESH_TOKEN } from '../../constants';
import { Navigate } from 'react-router-dom'

class OAuth2RedirectHandler extends Component {
    getUrlParameter(name) {
        name = name.replace(/[\\[]/, '\\[').replace(/[\]]/, '\\]');
        var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');

        var results = regex.exec(window.location.search); // Updated to use window.location.search
        return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
    };

    componentDidMount() {
        const token = this.getUrlParameter('token');
        const error = this.getUrlParameter('error');
        console.log('OAuth2RedirectHandler token:', token);
        console.log('OAuth2RedirectHandler error:', error);

        if (token) {
            localStorage.setItem(ACCESS_TOKEN, token);
            localStorage.setItem(REFRESH_TOKEN, null);
            this.props.onLoginSuccess();
        } else {
            this.props.onLoginFailure(error);
        }
    }

    render() {
        const token = this.getUrlParameter('token');
        const error = this.getUrlParameter('error');

        if (token) {
            return <Navigate to="/" />;
        } else {
            return <Navigate to={{
                pathname: "/login",
                state: { 
                    error: error 
                }
            }} />;
        }
    }
}

export default OAuth2RedirectHandler;