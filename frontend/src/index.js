import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './app/App';
// import * as serviceWorkerRegistration from './serviceWorkerRegistration';
import { BrowserRouter as Router } from 'react-router-dom';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <Router>
        <App />
    </Router>
);

// serviceWorkerRegistration.register();