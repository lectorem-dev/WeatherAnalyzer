package ru.weather.chartgenerator.service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@Service
public class TemperatureBarChartService {
    private final JdbcTemplate jdbcTemplate;

    public TemperatureBarChartService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String generateTemperatureBarChart() {
        // Датасет для графика
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int recordsPerMonth = 25; // Ожидаемое количество записей в месяц

        for (int month = 0; month < 12; month++) {
            int offset = month * recordsPerMonth;

            // Запрашиваем min и max температуры для текущего месяца
            String sql = "SELECT mintemp, maxtemp FROM observations LIMIT ? OFFSET ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, recordsPerMonth, offset);

            if (!results.isEmpty()) {
                double avgMinTemp = results.stream()
                        .mapToDouble(row -> ((Number) row.get("mintemp")).doubleValue())
                        .average()
                        .orElse(0);

                double avgMaxTemp = results.stream()
                        .mapToDouble(row -> ((Number) row.get("maxtemp")).doubleValue())
                        .average()
                        .orElse(0);

                dataset.addValue(avgMinTemp, "Min Temp", String.valueOf(month + 1));
                dataset.addValue(avgMaxTemp, "Max Temp", String.valueOf(month + 1));
            }
        }

        // Создаём диаграмму
        JFreeChart chart = createChart(dataset);
        return convertChartToSVG(chart, 800, 600);
    }

    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Average Monthly Temperatures",
                "Month",
                "Temperature (°C)",
                dataset
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainAxis(new CategoryAxis("Month"));
        plot.setRangeAxis(new NumberAxis("Temperature (°C)"));

        BarRenderer renderer = new BarRenderer();
        renderer.setSeriesPaint(0, new Color(79, 129, 189)); // Min Temp (синий)
        renderer.setSeriesPaint(1, new Color(192, 80, 77));  // Max Temp (красный)
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
