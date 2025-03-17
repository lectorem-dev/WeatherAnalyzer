import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Select, Card, Typography } from 'antd';
import { fetchSvgCharts } from './api/fetchSvg';

const { Option } = Select;
const { Title } = Typography;

const MainPage = () => {
  const [svgLineContent, setSvgLineContent] = useState(null);
  const [svgBarContent, setSvgBarContent] = useState(null);
  const [svgParameterContent, setSvgParameterContent] = useState(null);
  const [svgDailyParameterContent, setSvgDailyParameterContent] = useState(null);
  const [svgSunshineMonthlyBarContent, setSvgSunshineMonthlyBarContent] = useState(null);
  // const [svgPieContent, setSvgPieContent] = useState(null);

  const [selectedParameter, setSelectedParameter] = useState('rainfall');
  const [selectedDailyParameter, setSelectedDailyParameter] = useState('rainfall');
  const [selectedSunshineParameter, setSelectedSunshineParameter] = useState('sunshine');
  // const [selectedPieParameter, setSelectedPieParameter] = useState('windgustdir');

  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const token = localStorage.getItem('token');

  useEffect(() => {
    if (!token) {
        navigate('/');
    } else {
        fetchCharts(); // Этот метод вызывается при изменении зависимостей
    }
}, [token, selectedParameter, selectedDailyParameter, selectedSunshineParameter]);


const [loading, setLoading] = useState(false);

const fetchCharts = async () => {
    if (loading) return; // Если запрос уже в процессе, не выполняем его снова
    setLoading(true);

    const results = await fetchSvgCharts(token, selectedParameter, selectedDailyParameter, selectedSunshineParameter);

    if (results.error) {
        setError(results.error);
    } else {
        setSvgLineContent(results.line || null);
        setSvgBarContent(results.bar || null);
        setSvgParameterContent(results.parameter || null);
        setSvgDailyParameterContent(results.dailyParameter || null);
        setSvgSunshineMonthlyBarContent(results.sunshineMonthlyBar || null);
        // setSvgPieContent(results.pieChart || null);
    }

    setLoading(false);
};

  const svgStyle = {
    width: '100%', // Ширина 100% контейнера
    height: '100%', // Высота 100% контейнера
    display: 'block', // Убирает лишние отступы
    objectFit: 'contain', // Сохраняет пропорции, растягивает по максимуму
  };  

  const replaceSvgContent = (svgContent) => {
    return svgContent
      ? svgContent.replace(
          /<svg([^>]*)>/,
          '<svg$1 width="100%" height="100%" viewBox="0 0 800 600">'
        )
      : ''; // Возвращаем пустую строку, если контент не доступен
  };
  
  return (
    <div style={{ padding: '20px', maxWidth: '1000px', margin: '0 auto' }}>
      {error && <Title type="danger" level={4}>{error}</Title>}
  
      <Card title="Графики максимальной и минимальной температуры по дням" variant={false} style={{ marginBottom: '20px' }}>
        <div
          style={svgStyle}
          dangerouslySetInnerHTML={{
            __html: replaceSvgContent(svgLineContent),
          }}
        />
      </Card>

      <Card title="Графики максимальной и минимальной температуры, усреднённые по месяцам" variant={false} style={{ marginBottom: '20px' }}>
        <div
          style={svgStyle}
          dangerouslySetInnerHTML={{
            __html: replaceSvgContent(svgBarContent),
          }}
        />
      </Card>
  
      <Card title="Средние значения по месяцам" variant={false} style={{ marginBottom: '20px' }}>
        <Select
          value={selectedParameter}
          onChange={setSelectedParameter}
          style={{ width: '200px', marginBottom: '10px' }}
        >
          <Option value="rainfall">Ливень</Option>
          <Option value="evaporation">Испарение</Option>
          <Option value="sunshine">Солнечный свет</Option>
          <Option value="windgustspeed">Скорость порыва ветра</Option>
        </Select>
        <div
          style={svgStyle}
          dangerouslySetInnerHTML={{
            __html: replaceSvgContent(svgParameterContent),
          }}
        />
      </Card>
  
      <Card title="Подрорбные значения по дням" variant={false} style={{ marginBottom: '20px' }}>
        <Select
          value={selectedDailyParameter}
          onChange={setSelectedDailyParameter}
          style={{ width: '200px', marginBottom: '10px' }}
        >
          <Option value="rainfall">Ливень</Option>
          <Option value="evaporation">Испарение</Option>
          <Option value="sunshine">Солнечный свет</Option>
          <Option value="windgustspeed">Скорость порыва ветра</Option>
        </Select>
        <div
          style={svgStyle}
          dangerouslySetInnerHTML={{
            __html: replaceSvgContent(svgDailyParameterContent),
          }}
        />
      </Card>
  
      <Card title="Средние значения по месяцам, столбчатая диаграмма" variant={false}>
        <Select
          value={selectedSunshineParameter}
          onChange={setSelectedSunshineParameter}
          style={{ width: '200px', marginBottom: '10px' }}
        >
          <Option value="rainfall">Ливень</Option>
          <Option value="evaporation">Испарение</Option>
          <Option value="sunshine">Солнечный свет</Option>
          <Option value="windgustspeed">Скорость порыва ветра</Option>
        </Select>
        <div
          style={svgStyle}
          dangerouslySetInnerHTML={{
            __html: replaceSvgContent(svgSunshineMonthlyBarContent),
          }}
        />
      </Card>
    </div>
  );
}  

export default MainPage;

/*
<Card title="Пончик" variant={false} style={{ marginBottom: '20px' }}>
        <Select
          value={selectedPieParameter}
          onChange={setSelectedPieParameter}
          style={{ width: '200px', marginBottom: '10px' }}
        >
          <Option value="windgustdir">Направление порыва ветра</Option>
          <Option value="winddir9am">Направление ветра в 9 утра</Option>
        </Select>
        <div
          style={svgStyle}
          dangerouslySetInnerHTML={{
            __html: replaceSvgContent(svgPieContent),
          }}
        />
      </Card>
*/