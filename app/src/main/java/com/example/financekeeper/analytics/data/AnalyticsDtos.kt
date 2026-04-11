package com.example.financekeeper.analytics.data

import com.google.gson.annotations.SerializedName

data class AnalyticsDto(
    @SerializedName("kpi") val kpi: KpiDto,
    @SerializedName("insights") val insights: List<InsightDto>,
    @SerializedName("forecast") val forecast: ForecastDto
)

data class KpiDto(
    @SerializedName("stabilityScore") val stabilityScore: MetricDto,
    @SerializedName("savingsRate") val savingsRate: MetricDto,
    @SerializedName("expenseRatio") val expenseRatio: MetricDto,
    @SerializedName("spendingConcentration") val spendingConcentration: MetricDto,
    @SerializedName("weekendRatio") val weekendRatio: MetricDto,
    @SerializedName("incomeCoverageRatio") val incomeCoverageRatio: MetricDto,
    @SerializedName("driftIndex") val driftIndex: MetricDto,
    @SerializedName("incomeStability") val incomeStability: MetricDto
)

data class MetricDto(
    @SerializedName("value") val value: Double?,
    @SerializedName("available") val available: Boolean,
    @SerializedName("unavailableReason") val unavailableReason: String?
)

data class InsightDto(
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String
)

data class ForecastDto(
    @SerializedName("yearOutcome") val yearOutcome: YearOutcomeDto,
    @SerializedName("breakEven") val breakEven: BreakEvenDto?,
    @SerializedName("stabilityBuffer") val stabilityBuffer: Double?,
    @SerializedName("habitCost") val habitCost: HabitCostDto?,
    @SerializedName("monthEndForecast") val monthEndForecast: MonthEndForecastDto?
)

data class YearOutcomeDto(
    @SerializedName("available") val available: Boolean,
    @SerializedName("isSurplus") val isSurplus: Boolean,
    @SerializedName("amount") val amount: Double?,
    @SerializedName("unavailableReason") val unavailableReason: String?
)

data class BreakEvenDto(
    @SerializedName("requiredIncomeGrowthPercent") val requiredIncomeGrowthPercent: Double,
    @SerializedName("requiredExpenseReductionPercent") val requiredExpenseReductionPercent: Double
)

data class HabitCostDto(
    @SerializedName("categoryName") val categoryName: String,
    @SerializedName("avgCheck") val avgCheck: Double,
    @SerializedName("intervalDays") val intervalDays: Double,
    @SerializedName("yearSum") val yearSum: Double
)

data class MonthEndForecastDto(
    @SerializedName("forecastAmount") val forecastAmount: Double,
    @SerializedName("spentSoFar") val spentSoFar: Double,
    @SerializedName("daysElapsed") val daysElapsed: Int,
    @SerializedName("daysInMonth") val daysInMonth: Int
)