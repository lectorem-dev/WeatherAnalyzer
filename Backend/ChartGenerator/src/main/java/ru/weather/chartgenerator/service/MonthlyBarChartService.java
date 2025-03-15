package ru.weather.chartgenerator.service;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.awt.*;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@Service
public class MonthlyBarChartService {
    private final JdbcTemplate jdbcTemplate;

    public MonthlyBarChartService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String generateMonthlyBarChart(String columnName) {
        // Датасет для графика
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int recordsPerMonth = 25; // Ожидаемое количество записей в месяц

        // Запрашиваем данные по месяцам
        for (int month = 1; month <= 12; month++) {
            int offset = (month - 1) * recordsPerMonth; // Рассчитываем смещение

            // Запрос для получения данных по выбранному столбцу
            String sql = "SELECT " + columnName + " FROM observations LIMIT ? OFFSET ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, recordsPerMonth, offset);

            // Вычисляем среднее значение для текущего месяца
            if (!results.isEmpty()) {
                double avgValue = results.stream()
                        .mapToDouble(row -> ((Number) row.get(columnName)).doubleValue())
                        .average()
                        .orElse(0);

                // Добавляем значение в датасет
                dataset.addValue(avgValue, columnName, String.valueOf(month));
            }
        }

        // Создаём диаграмму
        JFreeChart chart = createChart(dataset);
        return convertChartToSVG(chart, 800, 600);
    }

    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        // Используем столбчатую диаграмму для отображения данных
        JFreeChart chart = ChartFactory.createBarChart(
                "Average Monthly " + dataset.getRowKey(0), // Заголовок графика
                "Month", // Ось X
                "Value", // Ось Y
                dataset
        );

        // Настройка осей графика
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainAxis(new CategoryAxis("Month"));
        plot.setRangeAxis(new NumberAxis("Value"));

        // Настройка цвета для каждой серии
        BarRenderer renderer = new BarRenderer();
        renderer.setSeriesPaint(0, new Color(79, 129, 189)); // Цвет столбцов
        plot.setRenderer(renderer);

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