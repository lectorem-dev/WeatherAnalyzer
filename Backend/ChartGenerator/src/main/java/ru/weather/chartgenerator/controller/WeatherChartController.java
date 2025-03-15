package ru.weather.chartgenerator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import ru.weather.chartgenerator.service.TemperatureBarChartService;
import ru.weather.chartgenerator.service.MonthlyAverageChartService;
import ru.weather.chartgenerator.service.DailyTrendChartService;
import ru.weather.chartgenerator.service.MonthlyBarChartService;
import ru.weather.chartgenerator.service.ParameterPieChartService;
import ru.weather.chartgenerator.service.TemperatureLineChartService;


@RestController
@RequestMapping("/charts")
@Tag(name = "Weather Charts", description = "API для генерации графиков погоды")
public class WeatherChartController {
    @Autowired
    private TemperatureLineChartService temperatureLineChartService;
    @Autowired
    private TemperatureBarChartService temperatureBarChartService;
    @Autowired
    private MonthlyAverageChartService monthlyAverageChartService;
    @Autowired
    private DailyTrendChartService dailyTrendChartService;
    @Autowired
    private MonthlyBarChartService monthlyBarChartService;
    @Autowired
    private ParameterPieChartService parameterPieChartService;

    @Operation(
            summary = "Линейный график температуры",
            description = "Отображает максимальную и минимальную температуру на одном линейном графике.")
    @GetMapping(value = "/temperature/line", produces = "image/svg+xml")
    public String getTemperatureLineChart() {
        return temperatureLineChartService.generateTemperatureLineChart();
    }

    @Operation(
            summary = "Столбчатая диаграмма температуры",
            description = "Отображает максимальную и минимальную температуру в виде столбчатой диаграммы.")
    @GetMapping(value = "/temperature/bar", produces = "image/svg+xml")
    public String getTemperatureBarChart() {
        return temperatureBarChartService.generateTemperatureBarChart();
    }

    @Operation(
            summary = "Линейный график параметра по месяцам",
            description = "Отображает выбранный параметр в виде сглаженного линейного графика, усредненного по месяцам. Доступные значения columnName: [укажите доступные параметры]")
    @GetMapping(value = "/parameter/{columnName}/monthly/line", produces = "image/svg+xml")
    public String getMonthlyAverageLineChart(@PathVariable String columnName) {
        return monthlyAverageChartService.generateLineChart(columnName);
    }

    @Operation(
            summary = "Линейный график параметра по дням",
            description = "Отображает выбранный параметр линейным графиком без сглаживания, данные представлены по дням. Доступные значения columnName: [укажите доступные параметры]")
    @GetMapping(value = "/parameter/{columnName}/daily/line", produces = "image/svg+xml")
    public String getDailyTrendLineChart(@PathVariable String columnName) {
        return dailyTrendChartService.generateLineChart(columnName);
    }

    @Operation(
            summary = "Столбчатая диаграмма параметра по месяцам",
            description = "Отображает выбранный параметр в виде столбчатой диаграммы, усредненного по месяцам. Доступные значения columnName: [укажите доступные параметры]")
    @GetMapping(value = "/parameter/{columnName}/monthly/bar", produces = "image/svg+xml")
    public String getMonthlyBarChart(@PathVariable String columnName) {
        return monthlyBarChartService.generateMonthlyBarChart(columnName);
    }

    @Operation(
            summary = "Круговая диаграмма параметров",
            description = "Отображает выбранный параметр в виде круговой диаграммы. Доступные значения columnName: [укажите доступные параметры]")
    @GetMapping(value = "/parameter/{columnName}/pie", produces = "image/svg+xml")
    public String getPieChart(@PathVariable String columnName) {
        return parameterPieChartService.generatePieChart(columnName);
    }
}


