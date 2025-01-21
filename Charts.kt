package com.serhio.homeaccountingapp

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun IncomeExpenseChart(
    incomes: Map<String, Double>,
    expenses: Map<String, Double>,
    totalIncomes: Double,
    totalExpenses: Double
) {
    BoxWithConstraints {
        val screenWidth = maxWidth
        val chartSize = if (screenWidth < 360.dp) 100.dp else 140.dp
        val strokeWidth = if (screenWidth < 360.dp) 50f else 110f
        val maxLegendHeight = if (screenWidth < 360.dp) 100.dp else 150.dp // Зменшення висоти легенди для маленьких екранів

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            val incomeColors = generateColors(incomes.size.takeIf { it > 0 } ?: 1)
            val expenseColors = generateColors(expenses.size.takeIf { it > 0 } ?: 1, isExpense = true)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DonutChart(
                    values = incomes.values.toList(),
                    maxAmount = totalIncomes,
                    chartSize = chartSize,
                    colors = incomeColors,
                    strokeWidth = strokeWidth,
                    chartLabel = "Доходи",
                    emptyChartColor = Color(0x8032CD32).copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(24.dp))
                DonutChart(
                    values = expenses.values.toList(),
                    maxAmount = totalExpenses,
                    chartSize = chartSize,
                    colors = expenseColors,
                    strokeWidth = strokeWidth,
                    chartLabel = "Витрати",
                    emptyChartColor = Color(0x80B22222).copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxLegendHeight) // Використання змінної для обмеження висоти легенди
                    .verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                ) {
                    incomes.keys.forEachIndexed { index, category ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(color = incomeColors[index], shape = CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = category,
                                color = Color.White,
                                fontSize = if (screenWidth < 360.dp) 10.sp else 14.sp
                            )
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(start = 35.dp, end = 35.dp)
                        .weight(1f)
                ) {
                    expenses.keys.forEachIndexed { index, category ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(color = expenseColors[index], shape = CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = category,
                                color = Color.White,
                                fontSize = if (screenWidth < 360.dp) 10.sp else 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DonutChart(
    values: List<Double>,
    maxAmount: Double,
    chartSize: Dp,
    colors: List<Color>,
    strokeWidth: Float,
    chartLabel: String,
    emptyChartColor: Color
) {
    Canvas(modifier = Modifier.size(chartSize)) {
        val chartRadius = size.minDimension / 2f
        val innerRadius = chartRadius - strokeWidth / 2f
        var currentAngle = 0f

        if (values.isEmpty() || maxAmount == 0.0) {
            drawArc(
                color = emptyChartColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(0f, 0f),
                size = Size(chartRadius * 2, chartRadius * 2),
                style = Stroke(width = strokeWidth)
            )
        } else {
            values.forEachIndexed { index, value ->
                val sweepAngle = (value / maxAmount * 360).toFloat()
                val color = colors[index % colors.size]

                drawArc(
                    color = color,
                    startAngle = currentAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(0f, 0f),
                    size = Size(chartRadius * 2, chartRadius * 2),
                    style = Stroke(width = strokeWidth)
                )
                currentAngle += sweepAngle
            }
        }

        drawCircle(
            color = Color.Transparent,
            radius = innerRadius,
            style = Stroke(width = 1f)
        )

        drawContext.canvas.nativeCanvas.apply {
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = chartRadius / 4
                isFakeBoldText = true
            }

            drawText(
                chartLabel,
                size.width / 2,
                size.height / 2 + (textPaint.textSize / 4),
                textPaint
            )
        }
    }
}

// Функція форматування чисел із пробілами між тисячами
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ExpandableButtonWithAmount(
    text: String,
    amount: Double,
    gradientColors: List<Color>,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    BoxWithConstraints {
        val screenWidth = maxWidth
        val fontSize = if (screenWidth < 360.dp) 14.sp else 18.sp
        val padding = if (screenWidth < 360.dp) 8.dp else 16.dp

        val gradient = Brush.horizontalGradient(
            colors = listOf(
                gradientColors[0],
                gradientColors[1]
            )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(gradient)
                .clickable(onClick = onClick)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSize
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${"%.2f".format(amount)} грн",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = fontSize
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}
@Composable
fun IncomeList(
    incomes: Map<String, Double>,
    onCategoryClick: (String) -> Unit // Додаємо параметр для обробки переходу
) {
    // Сортування категорій за сумою у спадному порядку
    val sortedIncomes = incomes.toList().sortedByDescending { (_, amount) -> amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp) // Обмеження висоти списку
    ) {
        items(sortedIncomes) { (category, amount) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0x99000000), Color(0x66000000)),
                            start = Offset(0f, 0f),
                            end = Offset(1f, 0f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onCategoryClick(category) } // Додаємо обробку натискання
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                    )
                    Text(
                        text = "${"%.2f".format(amount)} грн",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                    )
                }
            }
        }
    }
}

@Composable
fun ExpensesList(
    expenses: Map<String, Double>,
    onCategoryClick: (String) -> Unit // Додаємо параметр для обробки переходу
) {
    // Сортування категорій за сумою у зростаючому порядку
    val sortedExpenses = expenses.toList().sortedBy { (_, amount) -> amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp) // Обмеження висоти списку
    ) {
        items(sortedExpenses) { (category, amount) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0x99000000), Color(0x66000000)),
                            start = Offset(0f, 0f),
                            end = Offset(1f, 0f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onCategoryClick(category) } // Додаємо обробку натискання
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                    )
                    Text(
                        text = "${"%.2f".format(amount)} грн",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                    )
                }
            }
        }
    }
}
@Composable
fun CategoryItem(
    text: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    gradientColors: List<Color>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = gradientColors,
                    startX = 0f,
                    endX = Float.POSITIVE_INFINITY
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            icon() // Іконка зліва
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White
                )
            )
        }
    }
}

fun generateColors(size: Int, isExpense: Boolean = false): List<Color> {
    val expenseColors = listOf(
        Color(0xFFD32F2F).copy(alpha = 0.5f), // Яскраво-червоний з прозорістю 50%
        Color(0xFFFFC107).copy(alpha = 0.5f), // Яскраво-жовтий з прозорістю 50%
        Color(0xFF4CAF50).copy(alpha = 0.5f), // Яскраво-зелений з прозорістю 50%
        Color(0xFF2196F3).copy(alpha = 0.5f), // Яскраво-синій з прозорістю 50%
        Color(0xFFFF5722).copy(alpha = 0.5f), // Яскраво-оранжевий з прозорістю 50%
        Color(0xFF9C27B0).copy(alpha = 0.5f), // Яскраво-фіолетовий з прозорістю 50%
        Color(0xFFE91E63).copy(alpha = 0.5f), // Яскраво-рожевий з прозорістю 50%
        Color(0xFF00BCD4).copy(alpha = 0.5f), // Яскраво-бірюзовий з прозорістю 50%
        Color(0xFF673AB7).copy(alpha = 0.5f)  // Насичено-фіолетовий з прозорістю 50%
    )

    val incomeColors = listOf(
        Color(0xFF1E88E5).copy(alpha = 0.5f), // Синій з прозорістю 50%
        Color(0xFF43A047).copy(alpha = 0.5f), // Зелений з прозорістю 50%
        Color(0xFFF4511E).copy(alpha = 0.5f), // Помаранчевий з прозорістю 50%
        Color(0xFFFB8C00).copy(alpha = 0.5f), // Жовтогарячий з прозорістю 50%
        Color(0xFF8E24AA).copy(alpha = 0.5f), // Фіолетовий з прозорістю 50%
        Color(0xFF26C6DA).copy(alpha = 0.5f)  // Бірюзовий з прозорістю 50%
    )

    // Вибираємо кольори залежно від типу
    val baseColors = if (isExpense) expenseColors else incomeColors

    // Генеруємо кольори з урахуванням кількості
    return List(size) { index -> baseColors[index % baseColors.size] }
}
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun BalanceDisplay(balance: Double, modifier: Modifier = Modifier) {
    val formattedBalance = "%,.2f".format(balance).replace(",", " ")

    Column(
        modifier = modifier
            .padding(16.dp)
            .wrapContentWidth(Alignment.Start)
    ) {
        Text(
            text = "Залишок:",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 30.sp, // Менший шрифт для заголовка
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "$formattedBalance грн",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 25.sp, // Менший шрифт для суми
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}