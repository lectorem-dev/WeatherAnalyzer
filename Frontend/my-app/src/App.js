import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom'; // Используем Routes вместо Switch
import LoginPage from './LoginPage';
import MainPage from './MainPage';

const App = () => {
  return (
    <Router>
      <Routes> {/* Заменили Switch на Routes */}
        <Route path="/" element={<LoginPage />} />
        <Route path="/main" element={<MainPage />} />
      </Routes>
    </Router>
  );
};

export default App;
