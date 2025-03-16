package ru.weather.chartgenerator.service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParameterPieChartService {
    private final JdbcTemplate jdbcTemplate;

    public ParameterPieChartService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String generatePieChart(String columnName) {
        // Запрашиваем данные для выбранного столбца
        String sql = "SELECT " + columnName + " FROM observations";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        // Создаём HashMap для подсчёта частоты уникальных значений
        Map<String, Integer> frequencyMap = new HashMap<>();

        // Пройдем по результатам и соберем частоту значений
        results.stream()
                .map(row -> row.get(columnName))
                .filter(value -> value != null && !value.toString().trim().isEmpty())
                .map(Object::toString)
                .forEach(value -> {
                    String key = value.trim();
                    frequencyMap.put(key, frequencyMap.getOrDefault(key, 0) + 1);
                });

        // Создаём датасет для круговой диаграммы
        DefaultPieDataset dataset = new DefaultPieDataset();

        // Добавляем все уникальные значения и их частоту в датасет
        frequencyMap.forEach((key, count) -> dataset.setValue(key, count));

        // Создаём диаграмму
        JFreeChart chart = createChart(dataset);

        // Конвертируем диаграмму в SVG
        return convertChartToSVG(chart, 800, 600); // Устанавливаем размер области для отображения
    }

    private JFreeChart createChart(DefaultPieDataset dataset) {
        // Создаём круговую диаграмму
        JFreeChart chart = ChartFactory.createPieChart(
                "Distribution of " + dataset.getKey(0), // Название диаграммы
                dataset, // Датасет
                true, // Легенда
                true, // Подсказки
                false // URL
        );

        // Настроим внешний вид диаграммы
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint(0, new Color(79, 129, 189)); // Цвет для первого сектора
        plot.setSectionPaint(1, new Color(192, 80, 77));  // Цвет для второго сектора
        plot.setSectionPaint(2, new Color(0, 176, 80));   // Цвет для третьего сектора
        plot.setBackgroundPaint(Color.white);             // Устанавливаем белый фон для диаграммы

        // Делаем диаграмму более читаемой
        plot.setCircular(true); // Круглая форма диаграммы
        // plot.setSectionDepth(0.25); // Толщина секторов диаграммы

        return chart;
    }

    private String convertChartToSVG(JFreeChart chart, int width, int height) {
        Document document = GenericDOMImplementation.getDOMImplementation()
                .createDocument(null, "svg", null);
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        chart.draw(svgGenerator, new java.awt.Rectangle(width, height));

        StringWriter writer = new StringWriter();
        try {
            svgGenerator.stream(writer, true);
        } catch (Exception e) {
            throw new RuntimeException("Error generating SVG", e);
        }

        return writer.toString();
    }
}
