import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom'; 

const MainPage = () => {
  const [svgLineContent, setSvgLineContent] = useState(null);
  const [svgBarContent, setSvgBarContent] = useState(null);
  const [svgParameterContent, setSvgParameterContent] = useState(null);
  const [svgDailyParameterContent, setSvgDailyParameterContent] = useState(null);
  const [svgSunshineMonthlyBarContent, setSvgSunshineMonthlyBarContent] = useState(null);
  const [selectedParameter, setSelectedParameter] = useState('rainfall');
  const [selectedDailyParameter, setSelectedDailyParameter] = useState('rainfall');
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  // Получаем токен из localStorage (предполагается, что мы сохранили его после логина)
  const token = localStorage.getItem('token');

  useEffect(() => {
    if (!token) {
      // Если токен отсутствует, перенаправляем на страницу входа
      navigate('/login'); 
    } else {
      fetchSvg(token);
    }
  }, [token, navigate]);

  const fetchSvg = async (token) => {
    try {
      // Загружаем первый график (line)
      const lineResponse = await fetch('http://localhost:8000/charts/temperature/line', {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (lineResponse.ok) {
        const svgLineText = await lineResponse.text();
        setSvgLineContent(svgLineText); // Устанавливаем SVG для line
      } else {
        setError('Ошибка при загрузке графика Line');
      }

      // Загружаем второй график (bar)
      const barResponse = await fetch('http://localhost:8000/charts/temperature/bar', {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (barResponse.ok) {
        const svgBarText = await barResponse.text();
        setSvgBarContent(svgBarText); // Устанавливаем SVG для bar
      } else {
        setError('Ошибка при загрузке графика Bar');
      }

      // Загружаем график для выбранного параметра
      const parameterResponse = await fetch(
        `http://localhost:8000/charts/parameter/${selectedParameter}/monthly/line`, 
        {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        }
      );

      if (parameterResponse.ok) {
        const svgParameterText = await parameterResponse.text();
        setSvgParameterContent(svgParameterText); // Устанавливаем SVG для выбранного параметра
      } else {
        setError('Ошибка при загрузке графика параметра');
      }

      // Загружаем график для дневного выбранного параметра
      const dailyParameterResponse = await fetch(
        `http://localhost:8000/charts/parameter/${selectedDailyParameter}/daily/line`, 
        {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        }
      );

      if (dailyParameterResponse.ok) {
        const svgDailyParameterText = await dailyParameterResponse.text();
        setSvgDailyParameterContent(svgDailyParameterText); // Устанавливаем SVG для дневного параметра
      } else {
        setError('Ошибка при загрузке графика дневного параметра');
      }

      // Загружаем график для sunshine monthly bar
      const sunshineMonthlyBarResponse = await fetch(
        `http://localhost:8000/charts/parameter/sunshine/monthly/bar`, 
        {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        }
      );

      if (sunshineMonthlyBarResponse.ok) {
        const svgSunshineMonthlyBarText = await sunshineMonthlyBarResponse.text();
        setSvgSunshineMonthlyBarContent(svgSunshineMonthlyBarText); // Устанавливаем SVG для sunshine monthly bar
      } else {
        setError('Ошибка при загрузке графика Sunshine Monthly Bar');
      }
    } catch (error) {
      setError('Ошибка при запросе SVG');
    }
  };

  const handleParameterChange = (event) => {
    const selected = event.target.value;
    setSelectedParameter(selected); // Обновляем выбранный параметр
    fetchSvg(localStorage.getItem('token')); // Загружаем новый график для выбранного параметра
  };

  const handleDailyParameterChange = (event) => {
    const selected = event.target.value;
    setSelectedDailyParameter(selected); // Обновляем выбранный дневной параметр
    fetchSvg(localStorage.getItem('token')); // Загружаем новый график для дневного параметра
  };

  return (
    <div>
      <h1>График температуры</h1>
      {error && <p style={{ color: 'red' }}>{error}</p>}

      {svgLineContent ? (
        <div
          style={{
            width: '100%',  // Устанавливаем ширину контейнера на 100%
            maxWidth: '800px',  // Ограничиваем максимальную ширину
            height: '600px', // Устанавливаем фиксированную высоту
            overflow: 'hidden', // Обрезаем все, что выходит за пределы
          }}
          dangerouslySetInnerHTML={{
            __html: svgLineContent.replace(
              /<svg([^>]*)>/,
              '<svg$1 width="100%" height="100%" viewBox="0 0 800 600">'
            ), 
          }} 
        />
      ) : (
        <p>Загружается график Line...</p>
      )}

      <h1>График температур (Bar)</h1>
      {svgBarContent ? (
        <div
          style={{
            width: '100%',  // Устанавливаем ширину контейнера на 100%
            maxWidth: '800px',  // Ограничиваем максимальную ширину
            height: '600px', // Устанавливаем фиксированную высоту
            overflow: 'hidden', // Обрезаем все, что выходит за пределы
          }}
          dangerouslySetInnerHTML={{
            __html: svgBarContent.replace(
              /<svg([^>]*)>/,
              '<svg$1 width="100%" height="100%" viewBox="0 0 800 600">'
            ), 
          }} 
        />
      ) : (
        <p>Загружается график Bar...</p>
      )}

      <h1>Выберите параметр</h1>
      <select onChange={handleParameterChange} value={selectedParameter}>
        <option value="rainfall">Rainfall</option>
        <option value="evaporation">Evaporation</option>
        <option value="sunshine">Sunshine</option>
        <option value="windgustspeed">Wind Gust Speed</option>
      </select>

      <h1>График выбранного параметра (Monthly)</h1>
      {svgParameterContent ? (
        <div
          style={{
            width: '100%',  // Устанавливаем ширину контейнера на 100%
            maxWidth: '800px',  // Ограничиваем максимальную ширину
            height: '600px', // Устанавливаем фиксированную высоту
            overflow: 'hidden', // Обрезаем все, что выходит за пределы
          }}
          dangerouslySetInnerHTML={{
            __html: svgParameterContent.replace(
              /<svg([^>]*)>/,
              '<svg$1 width="100%" height="100%" viewBox="0 0 800 600">'
            ), 
          }} 
        />
      ) : (
        <p>Загружается график параметра (Monthly)...</p>
      )}

      <h1>Выберите дневной параметр</h1>
      <select onChange={handleDailyParameterChange} value={selectedDailyParameter}>
        <option value="rainfall">Rainfall</option>
        <option value="evaporation">Evaporation</option>
        <option value="sunshine">Sunshine</option>
        <option value="windgustspeed">Wind Gust Speed</option>
      </select>

      <h1>График дневного параметра</h1>
      {svgDailyParameterContent ? (
        <div
          style={{
            width: '100%',  // Устанавливаем ширину контейнера на 100%
            maxWidth: '800px',  // Ограничиваем максимальную ширину
            height: '600px', // Устанавливаем фиксированную высоту
            overflow: 'hidden', // Обрезаем все, что выходит за пределы
          }}
          dangerouslySetInnerHTML={{
            __html: svgDailyParameterContent.replace(
              /<svg([^>]*)>/,
              '<svg$1 width="100%" height="100%" viewBox="0 0 800 600">'
            ), 
          }} 
        />
      ) : (
        <p>Загружается график дневного параметра...</p>
      )}

      <h1>График Sunshine Monthly (Bar)</h1>
      {svgSunshineMonthlyBarContent ? (
        <div
          style={{
            width: '100%',  // Устанавливаем ширину контейнера на 100%
            maxWidth: '800px',  // Ограничиваем максимальную ширину
            height: '600px', // Устанавливаем фиксированную высоту
            overflow: 'hidden', // Обрезаем все, что выходит за пределы
          }}
          dangerouslySetInnerHTML={{
            __html: svgSunshineMonthlyBarContent.replace(
              /<svg([^>]*)>/,
              '<svg$1 width="100%" height="100%" viewBox="0 0 800 600">'
            ), 
          }} 
        />
      ) : (
        <p>Загружается график Sunshine Monthly Bar...</p>
      )}
    </div>
  );
};

export default MainPage;
