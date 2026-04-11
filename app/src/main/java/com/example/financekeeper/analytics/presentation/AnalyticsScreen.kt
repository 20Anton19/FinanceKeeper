package com.example.financekeeper.analytics.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financekeeper.analytics.data.AnalyticsDto
import com.example.financekeeper.analytics.data.ForecastDto
import com.example.financekeeper.analytics.data.InsightDto
import com.example.financekeeper.analytics.data.KpiDto
import com.example.financekeeper.analytics.data.MetricDto
import com.example.financekeeper.navigation.BottomNavBar
import com.example.financekeeper.navigation.BottomNavItem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onNavigateToTransactions: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    viewModel: AnalyticsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Аналитика") }) },
        bottomBar = {
            BottomNavBar(
                current = BottomNavItem.Analytics,
                onTransactions = onNavigateToTransactions,
                onAnalytics = onNavigateToAnalytics,
                onCategories = onNavigateToCategories,
                onProfile = onNavigateToProfile
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is AnalyticsState.Loading ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is AnalyticsState.Error ->
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(s.message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.load() }) { Text("Повторить") }
                    }
                is AnalyticsState.Success ->
                    AnalyticsContent(data = s.data)
            }
        }
    }
}

@Composable
fun AnalyticsContent(data: AnalyticsDto) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { SectionTitle("KPI") }
        item { KpiGrid(data.kpi) }

        item { SectionTitle("Прогнозы") }
        item { ForecastSection(data.forecast) }

        if (data.insights.isNotEmpty()) {
            item { SectionTitle("Инсайты") }
            items(data.insights) { insight ->
                InsightCard(insight)
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        "Недостаточно данных для формирования инсайтов. Добавьте больше операций.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium
    )
}

@Composable
fun KpiGrid(kpi: KpiDto) {
    val metrics = listOf(
        "Стабильность" to kpi.stabilityScore,
        "Норма сбережений" to kpi.savingsRate,
        "Расходы / Доход" to kpi.expenseRatio,
        "Концентрация трат" to kpi.spendingConcentration,
        "Коэф. выходных" to kpi.weekendRatio,
        "Покрытие дохода" to kpi.incomeCoverageRatio,
        "Дрейф трат" to kpi.driftIndex,
        "Стаб. доходов" to kpi.incomeStability
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        metrics.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { (label, metric) ->
                    KpiCard(
                        label = label,
                        metric = metric,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

enum class MetricLevel { Great, Good, Neutral, Warning, Bad }

private fun evaluateMetric(label: String, value: Double): MetricLevel {
    return when (label) {
        "Стабильность" -> when {
            value >= 80 -> MetricLevel.Great
            value >= 60 -> MetricLevel.Good
            value >= 40 -> MetricLevel.Neutral
            value >= 20 -> MetricLevel.Warning
            else -> MetricLevel.Bad
        }
        "Норма сбережений" -> when {
            value >= 20 -> MetricLevel.Great
            value >= 10 -> MetricLevel.Good
            value >= 0 -> MetricLevel.Neutral
            value >= -10 -> MetricLevel.Warning
            else -> MetricLevel.Bad
        }
        "Расходы / Доход" -> when {
            value <= 70 -> MetricLevel.Great
            value <= 90 -> MetricLevel.Good
            value <= 100 -> MetricLevel.Neutral
            value <= 120 -> MetricLevel.Warning
            else -> MetricLevel.Bad
        }
        "Концентрация трат" -> when {
            value <= 40 -> MetricLevel.Great
            value <= 55 -> MetricLevel.Good
            value <= 70 -> MetricLevel.Neutral
            value <= 85 -> MetricLevel.Warning
            else -> MetricLevel.Bad
        }
        "Коэф. выходных" -> when {
            value <= 120 -> MetricLevel.Great
            value <= 160 -> MetricLevel.Good
            value <= 200 -> MetricLevel.Neutral
            value <= 280 -> MetricLevel.Warning
            else -> MetricLevel.Bad
        }
        "Покрытие дохода" -> when {
            value >= 30 -> MetricLevel.Great
            value >= 15 -> MetricLevel.Good
            value >= 8 -> MetricLevel.Neutral
            value >= 4 -> MetricLevel.Warning
            else -> MetricLevel.Bad
        }
        "Дрейф трат" -> when {
            value <= 10 -> MetricLevel.Great
            value <= 20 -> MetricLevel.Good
            value <= 35 -> MetricLevel.Neutral
            value <= 50 -> MetricLevel.Warning
            else -> MetricLevel.Bad
        }
        "Стаб. доходов" -> when {
            value >= 0.8 -> MetricLevel.Great
            value >= 0.6 -> MetricLevel.Good
            value >= 0.4 -> MetricLevel.Neutral
            value >= 0.2 -> MetricLevel.Warning
            else -> MetricLevel.Bad
        }
        else -> MetricLevel.Neutral
    }
}

@Composable
private fun levelColors(level: MetricLevel): Pair<Color, Color> {
    return when (level) {
        MetricLevel.Great -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        MetricLevel.Good -> Color(0xFFF1F8E9) to Color(0xFF558B2F)
        MetricLevel.Neutral -> MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface
        MetricLevel.Warning -> Color(0xFFFFF8E1) to Color(0xFFE65100)
        MetricLevel.Bad -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    }
}


private fun metricDescription(label: String): Pair<String, String> {
    return when (label) {
        "Стабильность" -> "Коэффициент стабильности" to
                "Показывает насколько ваши ежедневные траты предсказуемы.\n\n" +
                "Считается как доля дней за последние 30 дней, когда траты были в пределах ±30% от вашей обычной нормы.\n\n" +
                "80–100% — отлично, траты очень предсказуемы\n" +
                "60–80% — хорошо\n" +
                "40–60% — есть нестабильность\n" +
                "< 40% — траты хаотичны"

        "Норма сбережений" -> "Savings Rate" to
                "Показывает какую долю дохода вы сберегаете.\n\n" +
                "Формула: (доход − расходы) / доход × 100%\n\n" +
                "> 20% — отлично\n" +
                "10–20% — хорошо\n" +
                "0–10% — мало откладываете\n" +
                "< 0% — живёте в долг"

        "Расходы / Доход" -> "Expense Ratio" to
                "Показывает какую долю дохода составляют ваши расходы.\n\n" +
                "Формула: расходы / доход × 100%\n\n" +
                "Это обратная сторона нормы сбережений — чем ниже, тем лучше.\n\n" +
                "< 70% — отлично\n" +
                "70–90% — хорошо\n" +
                "90–100% — на грани\n" +
                "> 100% — расходы превышают доходы"

        "Концентрация трат" -> "Spending Concentration Index" to
                "Показывает насколько ваши траты сосредоточены в нескольких категориях.\n\n" +
                "Формула: сумма топ-3 категорий / общие траты × 100%\n\n" +
                "< 40% — траты разнообразны\n" +
                "40–55% — умеренная концентрация\n" +
                "55–70% — высокая концентрация\n" +
                "> 85% — вся жизнь в 3 категориях"

        "Коэф. выходных" -> "Weekend Ratio" to
                "Показывает во сколько раз выходные дороже будних дней.\n\n" +
                "Формула: средний чек в выходной / средний чек в будний × 100%\n\n" +
                "100% — нет разницы между днями\n" +
                "120–160% — лёгкое превышение, норма\n" +
                "160–200% — заметная разница\n" +
                "> 280% — выходные обходятся значительно дороже"

        "Покрытие дохода" -> "Income Coverage Ratio" to
                "Показывает на сколько дней хватит месячного дохода, если каждый день тратить как в самый дорогой день месяца.\n\n" +
                "Формула: оценка дохода / максимальная дневная трата\n\n" +
                "> 30 дней — хороший запас прочности\n" +
                "15–30 — норма\n" +
                "8–15 — уязвимы к импульсным покупкам\n" +
                "< 8 — одна крупная трата ставит под удар весь месяц"

        "Дрейф трат" -> "Drift Index" to
                "Показывает насколько сильно изменилась структура ваших трат по сравнению с прошлым месяцем.\n\n" +
                "Считается как сумма абсолютных изменений долей всех категорий.\n\n" +
                "< 10% — структура трат стабильна\n" +
                "10–20% — небольшие изменения\n" +
                "20–35% — заметный сдвиг приоритетов\n" +
                "> 50% — серьёзная перестройка трат"

        "Стаб. доходов" -> "Income Stability" to
                "Показывает насколько стабильны ваши доходы. Особенно важно для фрилансеров.\n\n" +
                "Формула: 1 − (стандартное отклонение / среднее значение доходов)\n\n" +
                "0.8–1.0 — доходы очень стабильны\n" +
                "0.6–0.8 — умеренная стабильность\n" +
                "0.4–0.6 — заметные колебания\n" +
                "< 0.2 — доходы сильно непредсказуемы"

        else -> label to "Описание недоступно"
    }
}


@Composable
fun KpiCard(
    label: String,
    metric: MetricDto,
    modifier: Modifier = Modifier
) {
    val isAvailable = metric.available && metric.value != null
    var showDialog by remember { mutableStateOf(false) }

    val (bgColor, contentColor) = if (isAvailable) {
        val level = evaluateMetric(label, metric.value!!)
        levelColors(level)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) to
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
    }

    Card(
        modifier = modifier.clickable { showDialog = true },
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isAvailable) contentColor.copy(alpha = 0.7f)
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
            )
            Spacer(Modifier.height(6.dp))
            if (isAvailable) {
                Text(
                    text = formatMetricValue(metric.value!!),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = contentColor
                )
                Spacer(Modifier.height(4.dp))
                val level = evaluateMetric(label, metric.value)
                LevelBar(level = level, color = contentColor)
            } else {
                Text(
                    text = "Недостаточно\nданных",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    lineHeight = 16.sp
                )
                metric.unavailableReason?.let { reason ->
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = reason,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }

    if (showDialog) {
        val (title, description) = metricDescription(label)
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(title) },
            text = {
                Column {
                    if (isAvailable) {
                        val level = evaluateMetric(label, metric.value!!)
                        val levelText = when (level) {
                            MetricLevel.Great -> "Отлично"
                            MetricLevel.Good -> "Хорошо"
                            MetricLevel.Neutral -> "Нейтрально"
                            MetricLevel.Warning -> "Внимание"
                            MetricLevel.Bad -> "Плохо"
                        }
                        val (levelBg, levelFg) = levelColors(level)
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = levelBg,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = "Ваш результат: ${formatMetricValue(metric.value!!)} — $levelText",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = levelFg
                            )
                        }
                    }
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("Понятно") }
            }
        )
    }
}

@Composable
fun LevelBar(level: MetricLevel, color: Color) {
    val filledDots = when (level) {
        MetricLevel.Great -> 5
        MetricLevel.Good -> 4
        MetricLevel.Neutral -> 3
        MetricLevel.Warning -> 2
        MetricLevel.Bad -> 1
    }
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(5) { i ->
            Box(
                modifier = Modifier
                    .size(width = 14.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (i < filledDots) color
                        else color.copy(alpha = 0.2f)
                    )
            )
        }
    }
}

private fun formatMetricValue(value: Double): String {
    return if (value >= 100 || value == value.toLong().toDouble()) {
        "${value.toLong()}"
    } else {
        "${Math.round(value * 10) / 10.0}"
    }
}

@Composable
fun ForecastSection(forecast: ForecastDto) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        // К концу месяца
        forecast.monthEndForecast?.let { mef ->
            ForecastCard(
                title = "К концу месяца",
                body = "Уже потрачено ${mef.spentSoFar.toInt()}₽ за ${mef.daysElapsed} дней. " +
                        "При текущем темпе к концу месяца — ~${mef.forecastAmount.toInt()}₽."
            )
        } ?: UnavailableForecastCard("К концу месяца", "Нужно минимум 3 расхода в этом месяце")

        // Годовой прогноз
        if (forecast.yearOutcome.available) {
            val yo = forecast.yearOutcome
            ForecastCard(
                title = "К чему приведёт текущее поведение",
                body = if (yo.isSurplus)
                    "При сохранении текущего темпа за год разница между доходами и расходами составит ~${yo.amount?.toInt()}₽."
                else
                    "При сохранении текущего темпа за год накопится дефицит ~${yo.amount?.toInt()}₽.",
                isWarning = !yo.isSurplus
            )
        } else {
            UnavailableForecastCard(
                "К чему приведёт текущее поведение",
                forecast.yearOutcome.unavailableReason ?: "Недостаточно данных"
            )
        }

        // Точка безубыточности
        forecast.breakEven?.let { be ->
            ForecastCard(
                title = "Точка безубыточности",
                body = "Чтобы выйти в ноль, нужно увеличить доходы на ${be.requiredIncomeGrowthPercent.toInt()}% " +
                        "или сократить траты на ${be.requiredExpenseReductionPercent.toInt()}%.",
                isWarning = true
            )
        }

        // Буфер стабильности
        forecast.stabilityBuffer?.let { buf ->
            ForecastCard(
                title = "Буфер стабильности",
                body = if (buf >= 0)
                    "При росте расходов более чем на ${buf.toInt()}% текущий баланс станет отрицательным."
                else
                    "Расходы уже превышают доходы на ${(-buf).toInt()}%.",
                isWarning = buf < 0
            )
        }

        // Стоимость привычки
        forecast.habitCost?.let { hc ->
            ForecastCard(
                title = "Стоимость привычки на год",
                body = "Категория \"${hc.categoryName}\" каждые ${hc.intervalDays.toInt()} дней по ${hc.avgCheck.toInt()}₽ — " +
                        "за год ~${hc.yearSum.toInt()}₽."
            )
        }
    }
}

@Composable
fun ForecastCard(
    title: String,
    body: String,
    isWarning: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isWarning)
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = if (isWarning) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun UnavailableForecastCard(title: String, reason: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = reason,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
            )
        }
    }
}

@Composable
fun InsightCard(insight: InsightDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = insight.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = insight.body,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}