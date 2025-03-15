package ru.weather.chartgenerator.service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
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
public class MonthlyAverageChartService {
    private final JdbcTemplate jdbcTemplate;

    public MonthlyAverageChartService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String generateLineChart(String columnName) {
        if (!isValidColumn(columnName)) {
            throw new IllegalArgumentException("Invalid column: " + columnName);
        }

        XYSeries series = new XYSeries(columnName);
        int recordsPerMonth = 25;  // Приблизительное число записей в месяц

        for (int month = 0; month < 12; month++) {
            int offset = month * recordsPerMonth;

            String sql = "SELECT " + columnName + " FROM observations LIMIT ? OFFSET ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, recordsPerMonth, offset);

            if (!results.isEmpty()) {
                double avgValue = results.stream()
                        .mapToDouble(row -> ((Number) row.get(columnName)).doubleValue())
                        .average()
                        .orElse(0);

                series.add(month + 1, avgValue);  // X - месяц, Y - среднее значение
            }
        }

        JFreeChart chart = createChart(series, columnName);
        return convertChartToSVG(chart, 800, 600);
    }

    private boolean isValidColumn(String column) {
        return column.equals("rainfall") || column.equals("evaporation") ||
                column.equals("sunshine") || column.equals("windgustspeed");
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
