import React from 'react';
import { Navigate } from 'react-router-dom';

const PrivateRoute = ({ children, authenticated, ...rest }) => {
    return (
        authenticated ? (
            React.cloneElement(children, { ...rest })
        ) : (
            <Navigate to="/login" />
        )
    );
};

export default PrivateRoute;