import React, { useState, useEffect } from 'react';

const App = () => {
  const [token, setToken] = useState(null);
  const [temperatureData, setTemperatureData] = useState(null);
  const [error, setError] = useState(null);

  // Функция для авторизации
  const login = async () => {
    try {
      const response = await fetch('http://localhost:8000/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          login: 'ivanovii',
          password: 'qwerty123',
        }),
      });

      if (!response.ok) {
        throw new Error('Ошибка авторизации');
      }

      const data = await response.json();
      setToken(data.token); // Предположим, что токен приходит в поле 'token'
      console.log('Token:', data.token);
    } catch (error) {
      setError(error.message);
      console.error('Login error:', error);
    }
  };

  // Функция для получения данных температуры
  const fetchTemperatureData = async () => {
    if (!token) {
      console.log('Нет токена, не могу выполнить запрос');
      return;
    }

    try {
      const response = await fetch('http://localhost:8000/charts/temperature/line', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error('Ошибка получения данных о температуре');
      }

      const data = await response.text(); // Если это SVG или другие данные
      setTemperatureData(data);
      console.log('Temperature Data:', data);
    } catch (error) {
      setError(error.message);
      console.error('Error fetching temperature data:', error);
    }
  };

  // Вызов login при монтировании компонента
  useEffect(() => {
    login();
  }, []);

  // Вызов fetchTemperatureData при наличии токена
  useEffect(() => {
    if (token) {
      fetchTemperatureData();
    }
  }, [token]);

  return (
    <div>
      <h1>React App</h1>
      {error && <p style={{ color: 'red' }}>Error: {error}</p>}
      <div>
        <h2>Temperature Data</h2>
        {temperatureData ? (
          <div dangerouslySetInnerHTML={{ __html: temperatureData }} />
        ) : (
          <p>No temperature data yet.</p>
        )}
      </div>
    </div>
  );
};

export default App;
