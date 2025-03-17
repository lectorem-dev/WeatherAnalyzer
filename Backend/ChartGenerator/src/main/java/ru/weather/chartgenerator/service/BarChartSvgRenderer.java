package ru.weather.chartgenerator.service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.weather.chartgenerator.util.SvgChartExporter;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class BarChartSvgRenderer {
    private final JdbcTemplate jdbcTemplate;
    private final SvgChartExporter svgChartExporter;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int DAYS_IN_MONTH = 29;

    private static final Set<String> ALLOWED_COLUMNS = Set.of(
            "mintemp",
            "maxtemp",
            "rainfall",
            "evaporation",
            "sunshine",
            "windgustspeed",
            "windspeed9am",
            "windspeed3pm",
            "humidity9am",
            "humidity3pm",
            "pressure9am",
            "pressure3pm",
            "cloud9am",
            "cloud3pm",
            "temp9am",
            "temp3pm",
            "risk_mm"
    );

    public BarChartSvgRenderer(JdbcTemplate jdbcTemplate, SvgChartExporter svgChartExporter) {
        this.jdbcTemplate = jdbcTemplate;
        this.svgChartExporter = svgChartExporter;
    }

    public String generateBarChart(String columnName) {
        if (!ALLOWED_COLUMNS.contains(columnName)) {
            throw new IllegalArgumentException("Invalid column: " + columnName);
        }

        XYSeries series = new XYSeries(columnName);

        for (int month = 0; month < 12; month++) {
            int offset = month * DAYS_IN_MONTH;

            String sql = "SELECT " + columnName + " FROM observations LIMIT ? OFFSET ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, DAYS_IN_MONTH, offset);

            if (!results.isEmpty()) {
                double avgValue = results.stream()
                        .mapToDouble(row -> ((Number) row.get(columnName)).doubleValue())
                        .average()
                        .orElse(0);

                series.add(month + 1, avgValue);  // X - месяц, Y - среднее значение
            }
        }

        JFreeChart chart = createChart(series, columnName);

        return svgChartExporter.convertChartToSVG(chart, WIDTH, HEIGHT);
    }

    private JFreeChart createChart(XYSeries series, String columnName) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Average " + columnName + " per Month",
                "Month",
                columnName,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        plot.setDomainAxis(new NumberAxis("Month"));
        plot.setRangeAxis(new NumberAxis(columnName));

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
        renderer.setSeriesPaint(0, new Color(255, 0, 0));  // Красная линия
        plot.setRenderer(renderer);

        return chart;
    }
}