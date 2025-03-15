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
public class DailyTrendChartService {
    private final JdbcTemplate jdbcTemplate;

    public DailyTrendChartService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String generateLineChart(String columnName) {
        if (!isValidColumn(columnName)) {
            throw new IllegalArgumentException("Invalid column: " + columnName);
        }

        XYSeries series = new XYSeries(columnName);

        String sql = "SELECT " + columnName + " FROM observations";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        int index = 1; // Используем индексы для оси X
        for (Map<String, Object> row : results) {
            double value = ((Number) row.get(columnName)).doubleValue();
            series.add(index++, value);
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
                columnName + " Over Time",
                "Index",
                columnName,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        plot.setDomainAxis(new NumberAxis("Index"));
        plot.setRangeAxis(new NumberAxis(columnName));

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
        renderer.setSeriesPaint(0, new Color(0, 102, 255));  // Синяя линия
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
