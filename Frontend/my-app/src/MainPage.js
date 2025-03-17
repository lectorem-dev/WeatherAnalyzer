import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Select, Card, Typography } from 'antd';
import { fetchSvgCharts } from './api/fetchSvg';

const { Option } = Select;
const { Title } = Typography;

const MainPage = () => {
  const [svgComparisonContent, setSvgComparisonContent] = useState(null);
  const [svgDailyTrendContent, setSvgDailyTrendContent] = useState(null);
  const [svgMonthlyAverageBarContent, setSvgMonthlyAverageBarContent] = useState(null);
  const [svgTextFrequencyContent, setSvgTextFrequencyContent] = useState(null);

  const [selectedComparisonParameters, setSelectedComparisonParameters] = useState(['rainfall', 'sunshine']);
  const [selectedDailyTrendParameter, setSelectedDailyTrendParameter] = useState('rainfall');
  const [selectedMonthlyAverageParameter, setSelectedMonthlyAverageParameter] = useState('rainfall');
  const [selectedTextFrequencyParameter, setSelectedTextFrequencyParameter] = useState('windgustdir');

  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const token = localStorage.getItem('token');

  useEffect(() => {
    if (!token) {
        navigate('/');
    } else {
        fetchCharts(); 
    }
}, [token, selectedComparisonParameters, selectedDailyTrendParameter, selectedMonthlyAverageParameter, selectedTextFrequencyParameter]);

const [loading, setLoading] = useState(false);

const fetchCharts = async () => {
    if (loading) return; 
    setLoading(true);

    const results = await fetchSvgCharts(token, selectedComparisonParameters, selectedDailyTrendParameter, selectedMonthlyAverageParameter, selectedTextFrequencyParameter);

    if (results.error) {
        setError(results.error);
    } else {
        setSvgComparisonContent(results.comparison || null);
        setSvgDailyTrendContent(results.dailyTrend || null);
        setSvgMonthlyAverageBarContent(results.monthlyAverageBar || null);
        setSvgTextFrequencyContent(results.textFrequency || null);
    }

    setLoading(false);
};

const svgStyle = {
    width: '100%',
    height: '100%',
    display: 'block',
    objectFit: 'contain',
};

const replaceSvgContent = (svgContent) => {

    return svgContent
      ? svgContent.replace(
          /<svg([^>]*)>/,
          '<svg$1 width="100%" height="100%" viewBox="0 0 800 600">'
        )
      : ''; 
};

const weatherParameters = [
  { value: "mintemp", label: "минимальная температура" },
  { value: "maxtemp", label: "максимальная температура" },
  { value: "rainfall", label: "осадки" },
  { value: "evaporation", label: "испарение" },
  { value: "sunshine", label: "солнечное освещение" },
  { value: "windgustspeed", label: "скорость порывов ветра" },
  { value: "windspeed9am", label: "скорость ветра в 9:00" },
  { value: "windspeed3pm", label: "скорость ветра в 15:00" },
  { value: "humidity9am", label: "влажность в 9:00" },
  { value: "humidity3pm", label: "влажность в 15:00" },
  { value: "pressure9am", label: "давление в 9:00" },
  { value: "pressure3pm", label: "давление в 15:00" },
  { value: "cloud9am", label: "облачность в 9:00" },
  { value: "cloud3pm", label: "облачность в 15:00" },
  { value: "temp9am", label: "температура в 9:00" },
  { value: "temp3pm", label: "температура в 15:00" },
  { value: "risk_m", label: "риск осадков" },
];

const textParameters = [
  { value: "windgustdir", label: "направление порывов ветра" },
  { value: "winddir9am", label: "направление ветра в 9:00" },
  { value: "winddir3pm", label: "направление ветра в 15:00" },
  { value: "raintoday", label: "осадки за сегодня" },
  { value: "raintomorrow", label: "осадки за завтра" },
];

const WeatherSelect = ({ value, onChange }) => (
  <Select value={value} onChange={onChange} style={{ width: '300px', marginBottom: '10px' }}>
    {weatherParameters.map(({ value, label }) => (
      <Option key={value} value={value}>{label}</Option>
    ))}
  </Select>
);

const TextSelect = ({ value, onChange }) => (
  <Select value={value} onChange={onChange} style={{ width: '300px', marginBottom: '10px' }}>
    {textParameters.map(({ value, label }) => (
      <Option key={value} value={value}>{label}</Option>
    ))}
  </Select>
);

return (
  <div style={{ padding: '20px', maxWidth: '1000px', margin: '0 auto' }}>
    {error && <Title type="danger" level={4}>{error}</Title>}

    <Card title="О сайте" variant={false} style={{ marginBottom: '20px' }}>
      <p>Этот сайт предназначен для визуализации данных из погодного датасета. Он предоставляет четыре инструмента для анализа различных параметров погоды:</p>
      <ul>
        <li>График для сравнения двух погодных параметров – позволяет выбрать два параметра и сравнить их на одном графике.</li>
        <li>График тренда по дням – отображает изменение выбранного погодного параметра в течение времени.</li>
        <li>Столбчатая диаграмма с усреднёнными значениями по месяцам – показывает средние значения выбранного параметра за каждый месяц.</li>
        <li>График частоты текстовых параметров – анализирует частоту появления текстовых данных, таких как направление ветра или наличие осадков.</li>
      </ul>
      <p>Вы можете выбирать параметры для анализа с помощью выпадающих списков, а графики автоматически обновляются в соответствии с вашим выбором.</p>
    </Card>

    <Card title="График для сравнения двух погодных параметров" variant={false} style={{ marginBottom: '20px' }}>
      <WeatherSelect value={selectedComparisonParameters[0]} onChange={(value) => setSelectedComparisonParameters([value, selectedComparisonParameters[1]])} />
      <WeatherSelect value={selectedComparisonParameters[1]} onChange={(value) => setSelectedComparisonParameters([selectedComparisonParameters[0], value])} />
      <div style={svgStyle} dangerouslySetInnerHTML={{ __html: replaceSvgContent(svgComparisonContent) }} />
    </Card>

    <Card title="График тренда по дням" variant={false} style={{ marginBottom: '20px' }}>
      <WeatherSelect value={selectedDailyTrendParameter} onChange={setSelectedDailyTrendParameter} />
      <div style={svgStyle} dangerouslySetInnerHTML={{ __html: replaceSvgContent(svgDailyTrendContent) }} />
    </Card>

    <Card title="Столбчатая диаграмма с усреднёнными значениями по месяцам" variant={false} style={{ marginBottom: '20px' }}>
      <WeatherSelect value={selectedMonthlyAverageParameter} onChange={setSelectedMonthlyAverageParameter} />
      <div style={svgStyle} dangerouslySetInnerHTML={{ __html: replaceSvgContent(svgMonthlyAverageBarContent) }} />
    </Card>

    <Card title="График частоты текстовых параметров" variant={false}>
      <TextSelect value={selectedTextFrequencyParameter} onChange={setSelectedTextFrequencyParameter} />
      <div style={svgStyle} dangerouslySetInnerHTML={{ __html: replaceSvgContent(svgTextFrequencyContent) }} />
    </Card>

    <Card title="Техническая справка" variant={false} style={{ marginTop: '20px' }}>
      <p>Сайт построен на основе микросервисной архитектуры. Бэкенд реализован на Java Spring Boot и отвечает за обработку запросов, управление данными и авторизацию пользователей.</p>
      <p>В качестве базы данных используется PostgreSQL, которая разворачивается в контейнере и инициализируется через SQL-скрипт.</p>
      <p>Настроена система безопасности на основе Spring Security. Для аутентификации пользователей реализован механизм JWT-токенов: при входе в систему пользователю выдается токен, который используется для доступа к защищённым ресурсам. Фильтр безопасности автоматически проверяет каждый запрос на наличие и валидность токена.</p>
      <p>Исходный код доступен на <a href="https://github.com/ТВОЙ_GITHUB" target="_blank" rel="noopener noreferrer">GitHub</a>.</p>
    </Card>

  </div>
);
}

export default MainPage;
