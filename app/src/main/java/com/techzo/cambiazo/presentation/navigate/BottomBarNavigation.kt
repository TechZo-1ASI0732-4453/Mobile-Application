package com.techzo.cambiazo.presentation.navigate


import android.graphics.PointF
import androidx.collection.emptyLongSet
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.zIndex

@Composable
fun BottomBarNavigation(items: List<ItemsScreens>, currentScreen: String): Pair<@Composable () -> Unit, () -> Unit> {
    val withPx = LocalContext.current.resources.displayMetrics.widthPixels

    val shapeCurve = BarShape(
        offset = withPx / 2f,
        circleRadius = 30.dp,
        cornerRadius = 0.dp,
        circleGap = 5.dp,
    )

    val action = items[2].navigate

    val bottomBarContent: @Composable () -> Unit = {
        Row(
            modifier = Modifier
                .height(78.dp)
                .border(0.01.dp, Color.Gray.copy(0.4f), shapeCurve)
                .shadow(elevation = 20.dp, shape = shapeCurve)
                .background(Color.White, shapeCurve)
        ) {
            items.fastForEachIndexed { index, screen ->

                val isMiddle = index == 2

                val isCurrentScreen = currentScreen == screen.route
                val color = if (isCurrentScreen) Color(0xFFFFD146) else Color.Gray.copy(alpha = 1f)
                val icon = if (isCurrentScreen) screen.iconSelected else screen.icon

                if (isMiddle) {
                    Spacer(modifier = Modifier.size(40.dp))
                    return@fastForEachIndexed

                }

                NavigationBarItem(
                    modifier = Modifier
                        .zIndex(1f),
                    alwaysShowLabel = true,
                    selected = false,
                    onClick = { screen.navigate() },
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = screen.title,
                            tint = color,
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                    },
                    label = {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-7).dp),
                            text = screen.title,
                            fontSize = 11.sp,
                            maxLines = 1,
                            color = color,
                            letterSpacing = 0.001.sp,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )
                    }
                )
            }
        }
    }

    return Pair(bottomBarContent, action)
}


private const val CURVE_CIRCLE_RADIUS = 98

// the coordinates of the first curve
private val mFirstCurveStartPoint = PointF()
private val mFirstCurveControlPoint1 = PointF()
private val mFirstCurveControlPoint2 = PointF()
private val mFirstCurveEndPoint = PointF()


private val mSecondCurveControlPoint1 = PointF()
private val mSecondCurveControlPoint2 = PointF()
private var mSecondCurveStartPoint = PointF()
private var mSecondCurveEndPoint = PointF()

class BottomNavCurve : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(path = Path().apply {
            val curveDepth = CURVE_CIRCLE_RADIUS + (CURVE_CIRCLE_RADIUS / 5F)
            // the coordinates (x,y) of the start point before curve
            mFirstCurveStartPoint.set(
                (size.width / 2) - (CURVE_CIRCLE_RADIUS * 2) - (CURVE_CIRCLE_RADIUS / 3),
                curveDepth
            )

            // the coordinates (x,y) of the end point after curve
            mFirstCurveEndPoint.set(
                size.width / 2,
                0F
            )

            // same thing for the second curve
            mSecondCurveStartPoint = mFirstCurveEndPoint;
            mSecondCurveEndPoint.set(
                (size.width / 2) + (CURVE_CIRCLE_RADIUS * 2) + (CURVE_CIRCLE_RADIUS / 3),
                curveDepth
            )

            // the coordinates (x,y)  of the 1st control point on a cubic curve
            mFirstCurveControlPoint1.set(
                mFirstCurveStartPoint.x + curveDepth,
                mFirstCurveStartPoint.y
            )

            // the coordinates (x,y)  of the 2nd control point on a cubic curve
            mFirstCurveControlPoint2.set(
                mFirstCurveEndPoint.x - (CURVE_CIRCLE_RADIUS * 2) + CURVE_CIRCLE_RADIUS,
                mFirstCurveEndPoint.y
            )
            mSecondCurveControlPoint1.set(
                mSecondCurveStartPoint.x + (CURVE_CIRCLE_RADIUS * 2) - CURVE_CIRCLE_RADIUS,
                mSecondCurveStartPoint.y
            )
            mSecondCurveControlPoint2.set(
                mSecondCurveEndPoint.x - (curveDepth),
                mSecondCurveEndPoint.y
            )

            moveTo(0f, curveDepth)
            lineTo(mFirstCurveStartPoint.x, mFirstCurveStartPoint.y)
            cubicTo(
                mFirstCurveControlPoint1.x, mFirstCurveControlPoint1.y,
                mFirstCurveControlPoint2.x, mFirstCurveControlPoint2.y,
                mFirstCurveEndPoint.x, mFirstCurveEndPoint.y
            )
            cubicTo(
                mSecondCurveControlPoint1.x, mSecondCurveControlPoint1.y,
                mSecondCurveControlPoint2.x, mSecondCurveControlPoint2.y,
                mSecondCurveEndPoint.x, mSecondCurveEndPoint.y
            )
            lineTo(size.width, curveDepth)
            lineTo(size.width, size.height)
            lineTo(0F, size.height)
        })
    }
}

class BarShape(
    private val offset: Float,
    private val circleRadius: Dp,
    private val cornerRadius: Dp,
    private val circleGap: Dp = 5.dp,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline  {
        return Outline.Generic(getPath(size, density))
    }

    private fun getPath(size: Size, density: Density): Path {
        val cutoutCenterX = offset
        val cutoutRadius = density.run { (circleRadius + circleGap).toPx() }
        val cornerRadiusPx = density.run { cornerRadius.toPx() }
        val cornerDiameter = cornerRadiusPx * 2
        return Path().apply {
            val cutoutEdgeOffset = cutoutRadius * 1f
            val cutoutLeftX = cutoutCenterX - cutoutEdgeOffset
            val cutoutRightX = cutoutCenterX + cutoutEdgeOffset

            // bottom left
            moveTo(x = 0F, y = size.height)
            // top left
            if (cutoutLeftX > 0) {
                val realLeftCornerDiameter = if (cutoutLeftX >= cornerRadiusPx) {
                    // there is a space between rounded corner and cutout
                    cornerDiameter
                } else {
                    // rounded corner and cutout overlap
                    cutoutLeftX * 2
                }
                arcTo(
                    rect = Rect(
                        left = 0f,
                        top = 0f,
                        right = realLeftCornerDiameter,
                        bottom = realLeftCornerDiameter
                    ),
                    startAngleDegrees = 2.0f,
                    sweepAngleDegrees = 1.0f,
                    forceMoveTo = false
                )
            }
            lineTo(cutoutLeftX, 0f)
            // cutout
            cubicTo(
                x1 = cutoutCenterX - cutoutRadius,
                y1 = 0f,
                x2 = cutoutCenterX - cutoutRadius,
                y2 = cutoutRadius,
                x3 = cutoutCenterX,
                y3 = cutoutRadius,
            )
            cubicTo(
                x1 = cutoutCenterX + cutoutRadius,
                y1 = cutoutRadius,
                x2 = cutoutCenterX + cutoutRadius,
                y2 = 0f,
                x3 = cutoutRightX,
                y3 = 0f,
            )
            // top right
            if (cutoutRightX < size.width) {
                val realRightCornerDiameter = if (cutoutRightX <= size.width - cornerRadiusPx) {
                    cornerDiameter
                } else {
                    (size.width - cutoutRightX) * 2
                }
                arcTo(
                    rect = Rect(
                        left = size.width - realRightCornerDiameter,
                        top = 0f,
                        right = size.width,
                        bottom = realRightCornerDiameter
                    ),
                    startAngleDegrees = -180.0f,
                    sweepAngleDegrees = 90.0f,
                    forceMoveTo = false
                )
            }
            // bottom right
            lineTo(x = size.width, y = size.height)
            close()
        }
    }
}