package ru.weather.chartgenerator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.weather.chartgenerator.service.*;

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
    private LineChartSvgRenderer dailyTrendChartService;
    @Autowired
    private WordFrequencyChartSvgRenderer monthlyBarChartService;
    @Autowired
    private ParameterPieChartService parameterPieChartService;

    @Operation(
            summary = "Линейный график температуры",
            description = "Отображает максимальную и минимальную температуру на одном линейном графике.")
    @GetMapping(value = "/temperature/line", produces = "image/svg+xml")
    public String getTemperatureLineChart() { return temperatureLineChartService.generateTemperatureLineChart(); }

    @Operation(
            summary = "Столбчатая диаграмма температуры",
            description = "Отображает максимальную и минимальную температуру в виде столбчатой диаграммы.")
    @GetMapping(value = "/temperature/bar", produces = "image/svg+xml")
    public String getTemperatureBarChart() {
        return temperatureBarChartService.generateTemperatureBarChart();
    }

    @Operation(
            summary = "Линейный график параметра по месяцам",
            description = "Отображает выбранный параметр в виде сглаженного линейного графика, усредненного по месяцам. Доступные значения columnName: [ rainfall evaporation sunshine windgustspeed ]")
    @GetMapping(value = "/parameter/{columnName}/monthly/line", produces = "image/svg+xml")
    public String getMonthlyAverageLineChart(@PathVariable String columnName) {
        return monthlyAverageChartService.generateLineChart(columnName);
    }

    @Operation(
            summary = "Линейный график параметра по дням",
            description = "Отображает выбранный параметр линейным графиком без сглаживания, данные представлены по дням. Доступные значения columnName: [ rainfall evaporation sunshine windgustspeed ]")
    @GetMapping(value = "/{columnName}/line", produces = "image/svg+xml")
    public String getDailyTrendLineChart(@PathVariable String columnName) {
        return dailyTrendChartService.generateLineChart(columnName);
    }

    @Operation(
            summary = "Столбчатая диаграмма параметра по месяцам",
            description = "Отображает выбранный параметр в виде столбчатой диаграммы, усредненного по месяцам. Доступные значения columnName: [ rainfall evaporation sunshine windgustspeed ]")
    @GetMapping(value = "/{columnName}/monthly-bar", produces = "image/svg+xml")
    public String getMonthlyBarChart(@PathVariable String columnName) {
        return monthlyBarChartService.generateWordFrequencyChart(columnName);
    }

    @Operation(
            summary = "Круговая диаграмма параметров",
            description = "Отображает выбранный параметр в виде круговой диаграммы. Доступные значения columnName: [ windgustdir winddir9am winddir3am ]")
    @GetMapping(value = "/parameter/{columnName}/pie", produces = "image/svg+xml")
    public String getPieChart(@PathVariable String columnName) {
        return parameterPieChartService.generatePieChart(columnName);
    }
}