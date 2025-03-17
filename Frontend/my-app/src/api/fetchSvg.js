export const fetchSvgCharts = async (token, selectedParameter, selectedDailyParameter, selectedSunshineParameter) => {
    const endpoints = {
        line: 'http://localhost:8000/charts/temperature/line',
        bar: 'http://localhost:8000/charts/temperature/bar',
        parameter: `http://localhost:8000/charts/parameter/${selectedParameter}/monthly/line`,
        dailyParameter: `http://localhost:8000/charts/parameter/${selectedDailyParameter}/daily/line`,
        sunshineMonthlyBar: `http://localhost:8000/charts/parameter/${selectedSunshineParameter}/monthly/bar`,
        // pieChart: `http://localhost:8000/charts/parameter/${selectedPieParameter}/pie`,
    };

    try {
        const requests = Object.entries(endpoints).map(async ([key, url]) => {
            const response = await fetch(url, {
                headers: { 'Authorization': `Bearer ${token}` },
            });

            if (!response.ok) {
                console.error(`Error fetching ${key} from ${url}:`, response.statusText);
                return { [key]: `Ошибка при загрузке ${key}` };
            }

            return { [key]: await response.text() };
        });

        const results = await Promise.all(requests);

        return results.reduce((acc, curr) => ({ ...acc, ...curr }), {});
    } catch (error) {
        console.error("Error fetching charts:", error);
        return { error: 'Ошибка при запросе SVG' };
    }
};
