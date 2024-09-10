// Sales Report by Date
let data;
let chartOptions;
let totalGrossSales;
let totalNetSales;
let totalOrders;
let startDateField;
let endDateField;

const MILLISECONDS_A_DAY = 24 * 60 * 60 * 1000;

$(document).ready(function() {
	const divCustomDateRange = $("#divCustomDateRange");
	startDateField = document.getElementById('startDate');
	endDateField = document.getElementById('endDate');
		
	$(".button-sales-by-date").on("click", function() {
		$(".button-sales-by-date").each(function(e) {
			$(this).removeClass('btn-primary').addClass('btn-light');
		});
		
		$(this).removeClass('btn-light').addClass('btn-primary');
		
		const period = $(this).attr("period");
		if (period) {
			loadSalesReportByDate(period);
			divCustomDateRange.addClass("d-none");
		} else {
			divCustomDateRange.removeClass("d-none");
		}		
	});
	
	initCustomDateRange();
	
	$("#buttonViewReportByDateRange").on("click", function(e) {
		validateDateRange();
	});
});
	
function validateDateRange() {
	const days = calculateDays();
	
	startDateField.setCustomValidity("");
	
	if (days >= 7 && days <= 30) {
		loadSalesReportByDate("custom");
	} else {
		startDateField.setCustomValidity("Dates must be in the range of 7..30 days");
		startDateField.reportValidity();
	}
}

function calculateDays() {
	const startDate = startDateField.valueAsDate;
	const endDate = endDateField.valueAsDate;
	
	const differenceInMilliseconds = endDate - startDate;
	return differenceInMilliseconds / MILLISECONDS_A_DAY;
}
	
function initCustomDateRange() {
	const toDate = new Date();
	endDateField.valueAsDate = toDate;
	
	const fromDate = new Date();
	fromDate.setDate(toDate.getDate() - 30);
	startDateField.valueAsDate = fromDate;
}	

function loadSalesReportByDate(period) {
	let requestURL;
	if (!period) {
		period = 'last_7_days';
	}
	if (period === "custom") {
		const startDate = $("#startDate").val();
		const endDate = $("#endDate").val();

		requestURL = contextPath + "reports/" + startDate + "/" + endDate;
	} else {
		requestURL = contextPath + "reports/" + period;
	}
	
	$.get(requestURL, function(responseJSON) {
		prepareChartData(responseJSON);
		customizeChart(period);
		drawChart(period);
	});
}

function prepareChartData(responseJSON) {
	data = new google.visualization.DataTable();
	data.addColumn('string', 'Date');
	data.addColumn('number', 'Gross Sales');
	data.addColumn('number', 'Net Sales');
	data.addColumn('number', 'Orders');
	
	totalGrossSales = 0.0;
	totalNetSales = 0.0;
	totalOrders = 0;
	
	$.each(responseJSON, function(index, reportItem) {
		data.addRows([[reportItem.identifier, reportItem.grossSales, reportItem.netSales, reportItem.ordersCount]]);
		totalGrossSales += parseFloat(reportItem.grossSales);
		totalNetSales += parseFloat(reportItem.netSales);
		totalOrders += parseInt(reportItem.ordersCount);
	});
}

function customizeChart(period) {
	chartOptions = {
		title: getChartTitle(period),
		'height': 360,
		legend: {position: 'top'},
		
		series: {
			0: {targetAxisIndex: 0},
			1: {targetAxisIndex: 0},
		},
		
		vAxes: {
			0: {title: 'Sales Amount'},
		}
	};
	
	const formatter = new google.visualization.NumberFormat({
		prefix: prefixCurrencySymbol,
		suffix: suffixCurrencySymbol,
		decimalSymbol: decimalPointType,
		groupingSymbol: thousandsPointType,
		fractionDigits: decimalDigits
	});
	
	formatter.format(data, 1);
	formatter.format(data, 2);
}

function drawChart(period) {
	const salesChart = new google.visualization.ColumnChart(document.getElementById('chart_sales_by_date'));
	salesChart.draw(data, chartOptions);
	
	$("#textTotalGrossSales").text(formatCurrency(totalGrossSales));
	$("#textTotalNetSales").text(formatCurrency(totalNetSales));
	
	const denominator = getDenominator(period);
	
	$("#textAvgGrossSales").text(formatCurrency(totalGrossSales / denominator));
	$("#textAvgNetSales").text(formatCurrency(totalNetSales / denominator));
	$("#textTotalOrders").text(totalOrders);
}

function formatCurrency(amount) {
	const formattedAmount = $.number(amount, decimalDigits, decimalPointType, thousandsPointType);
	return prefixCurrencySymbol + formattedAmount + suffixCurrencySymbol;
}

function getChartTitle(period) {
	if (period === "last_7_days") return "Sales in Last 7 Days";
	if (period === "last_28_days") return "Sales in Last 28 Days";
	if (period === "last_6_months") return "Sales in Last 6 Months";
	if (period === "last_year") return "Sales in Last Year";
	if (period === "custom") return "Custom Date Range";
	
	return "";
}

function getDenominator(period) {
	if (period === "last_7_days") return 7;
	if (period === "last_28_days") return 28;
	if (period === "last_6_months") return 180;
	if (period === "last_year") return 365;
	if (period === "custom") return calculateDays();
	
	return 7;
}