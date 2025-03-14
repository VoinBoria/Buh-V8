package com.serhio.homeaccountingapp

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.ExperimentalPagerApi
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.unit.TextUnit
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
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
        val chartSize = if (screenWidth < 360.dp) 120.dp else 160.dp  // Зменшення розміру діаграми
        val strokeWidth = if (screenWidth < 360.dp) 30f else 50f  // Зменшення товщини обводки
        val maxLegendHeight = chartSize  // Висота легенди тепер дорівнює висоті діаграми

        val scope = rememberCoroutineScope()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            val incomeColors = generateDistinctColors(incomes.size.takeIf { it > 0 } ?: 1, excludeRed = true)
            val expenseColors = generateDistinctColors(expenses.size.takeIf { it > 0 } ?: 1, excludeGreen = true)
            val pagerState = rememberPagerState()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(
                            onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = if (pagerState.currentPage == 0) Color.Gray else Color.Transparent
                            )
                        ) {
                            Text("Доходи", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = if (pagerState.currentPage == 1) Color.Gray else Color.Transparent
                            )
                        ) {
                            Text("Витрати", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    }

                    HorizontalPager(state = pagerState, count = 2, modifier = Modifier.fillMaxWidth()) { page ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(max = maxLegendHeight)
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.Start
                            ) {
                                if (page == 0) {
                                    incomes.keys.forEachIndexed { index, category ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)  // Додавання відступів
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .background(
                                                        color = incomeColors[index],
                                                        shape = CircleShape
                                                    )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = category,
                                                color = Color.White,
                                                fontSize = if (screenWidth < 360.dp) 12.sp else 14.sp  // Збільшення шрифту
                                            )
                                        }
                                    }
                                } else {
                                    expenses.keys.forEachIndexed { index, category ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)  // Додавання відступів
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .background(
                                                        color = expenseColors[index],
                                                        shape = CircleShape
                                                    )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = category,
                                                color = Color.White,
                                                fontSize = if (screenWidth < 360.dp) 12.sp else 14.sp  // Збільшення шрифту
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            if (page == 0) {
                                GradientDonutChart(
                                    values = incomes.values.toList(),
                                    maxAmount = totalIncomes,
                                    chartSize = chartSize,
                                    colors = incomeColors,
                                    strokeWidth = strokeWidth,
                                    emptyChartColor = Color(0x8032CD32).copy(alpha = 0.5f)
                                )
                            } else {
                                GradientDonutChart(
                                    values = expenses.values.toList(),
                                    maxAmount = totalExpenses,
                                    chartSize = chartSize,
                                    colors = expenseColors,
                                    strokeWidth = strokeWidth,
                                    emptyChartColor = Color(0x80B22222).copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }

            HorizontalPagerIndicator(
                pagerState = pagerState,
                activeColor = Color.White,
                inactiveColor = Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun GradientDonutChart(
    values: List<Double>,
    maxAmount: Double,
    chartSize: Dp,
    colors: List<Color>,
    strokeWidth: Float,
    emptyChartColor: Color
) {
    Box(
        modifier = Modifier
            .size(chartSize)
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.size(chartSize)) {
            val sweepAngles = values.map { value ->
                (value / maxAmount * 360).toFloat()
            }
            var startAngle = -90f
            for (i in values.indices) {
                drawArc(
                    color = colors[i],
                    startAngle = startAngle,
                    sweepAngle = sweepAngles[i],
                    useCenter = false,
                    style = Stroke(strokeWidth)
                )
                drawArc(
                    color = Color.Black.copy(alpha = 0.1f),
                    startAngle = startAngle,
                    sweepAngle = sweepAngles[i],
                    useCenter = false,
                    style = Stroke(strokeWidth + 4)
                )
                startAngle += sweepAngles[i]
            }
            if (values.isEmpty()) {
                drawArc(
                    color = emptyChartColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(strokeWidth)
                )
            }
        }
    }
}

fun generateDistinctColors(count: Int, excludeRed: Boolean = false, excludeGreen: Boolean = false): List<Color> {
    val predefinedColors = listOf(
        Color(0xFFe6194B), // red
        Color(0xFF3cb44b), // green
        Color(0xFFffe119), // yellow
        Color(0xFF4363d8), // blue
        Color(0xFFf58231), // orange
        Color(0xFF911eb4), // purple
        Color(0xFF42d4f4), // cyan
        Color(0xFFf032e6), // magenta
        Color(0xFFbfef45), // lime
        Color(0xFFfabebe), // pink
        Color(0xFF469990), // teal
        Color(0xFFe6beff), // lavender
        Color(0xFF9A6324), // brown
        Color(0xFFfffac8), // beige
        Color(0xFF800000), // maroon
        Color(0xFFaaffc3), // mint
        Color(0xFF808000), // olive
        Color(0xFFffd8b1), // coral
        Color(0xFF000075), // navy
        Color(0xFFa9a9a9)  // gray
    )

    val filteredColors = predefinedColors.filter {
        (!excludeRed || it != Color(0xFFe6194B)) && (!excludeGreen || it != Color(0xFF3cb44b))
    }

    return List(count) { filteredColors[it % filteredColors.size] }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ExpandableButtonWithAmount(
    text: String,
    amount: Double,
    gradientColors: List<Color>,
    isExpanded: Boolean,
    onClick: () -> Unit,
    textColor: Color = Color.White, // Додаємо параметр для кольору тексту
    fontWeight: FontWeight = FontWeight.Bold, // Додаємо параметр для жирності шрифту
    fontSize: TextUnit = 24.sp // Збільшення шрифту
) {
    BoxWithConstraints {
        val screenWidth = maxWidth
        val padding = if (screenWidth < 360.dp) 8.dp else 16.dp

        val gradient = Brush.horizontalGradient(
            colors = listOf(
                gradientColors[0],
                gradientColors[1]
            )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f) // Зменшення ширини фону
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = textColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = text,
                        color = textColor,
                        fontWeight = fontWeight,
                        fontSize = fontSize
                    )
                }
                Text(
                    text = "${"%.2f".format(amount)} грн",
                    color = textColor,
                    fontWeight = fontWeight,
                    fontSize = fontSize
                )
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

@Composable
fun BalanceDisplay(balance: Double, modifier: Modifier = Modifier) {
    val formattedBalance = "%,.2f".format(balance).replace(",", " ")
    val balanceColor = when {
        balance < 0 -> Color.Red
        balance > 0 -> Color.Green
        else -> Color.White
    }

    Column(
        modifier = modifier
            .padding(start = 16.dp, bottom = 25.dp)
            .width(IntrinsicSize.Max)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.height(56.dp) // Встановлюємо висоту контейнера для вирівнювання з кнопками
        ) {
            Text(
                text = "Залишок:",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 24.sp, // Збільшуємо шрифт для заголовка
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 4.dp) // Налаштовуємо відступ для вирівнювання
            )
            Text(
                text = "$formattedBalance грн",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 22.sp, // Збільшуємо шрифт для суми
                    color = balanceColor, // Колір тексту залежить від суми
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.align(Alignment.Start) // Вирівнюємо текст по лівому краю
            )
        }
    }
}
