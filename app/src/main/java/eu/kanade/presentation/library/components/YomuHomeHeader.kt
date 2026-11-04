package eu.kanade.presentation.library.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.kanade.presentation.manga.components.MangaCover
import eu.kanade.presentation.util.formatChapterNumber
import mihon.icons.materialsymbols.MaterialSymbols
import mihon.icons.materialsymbols.roundedfilled.PlayArrow
import tachiyomi.domain.history.model.HistoryWithRelations
import java.util.Calendar

@Composable
fun YomuHomeHeader(
    lastRead: HistoryWithRelations?,
    onContinueReading: (HistoryWithRelations) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        // --- 1. Top Greeting + Avatar Fateh ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
                val greeting = if (hour in 5..17) "Bonjour," else "Good evening,"
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium,
                    ),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Fateh",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                        ),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "✦",
                        color = Color(0xFFA78BFA),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Avatar avec anneau dégradé violet / indigo
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF8B5CF6), Color(0xFF6366F1)),
                        ),
                        shape = CircleShape,
                    )
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F111A)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "F",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }
        }

        // --- 2. Hero Card : Reprendre la lecture ---
        if (lastRead != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onContinueReading(lastRead) },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF141624),
                ),
                shape = RoundedCornerShape(20.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                listOf(Color(0x338B5CF6), Color(0x05FFFFFF)),
                            ),
                            shape = RoundedCornerShape(20.dp),
                        )
                        .padding(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Couverture du manga (Format 2:3, coins 14dp)
                        MangaCover.Book(
                            data = lastRead.coverData,
                            contentDescription = lastRead.title,
                            modifier = Modifier
                                .width(64.dp)
                                .height(92.dp),
                            shape = RoundedCornerShape(14.dp),
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            // Badge violet pulsé
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFFA78BFA), CircleShape),
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "REPRENDRE LA LECTURE",
                                    color = Color(0xFFA78BFA),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = lastRead.title,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            val chapterLabel = if (lastRead.chapterNumber > -1) {
                                "Chapitre ${formatChapterNumber(lastRead.chapterNumber)}"
                            } else {
                                ""
                            }
                            if (chapterLabel.isNotEmpty()) {
                                Text(
                                    text = chapterLabel,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Bouton pilule violet "Reprendre"
                            Button(
                                onClick = { onContinueReading(lastRead) },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .height(32.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF7C3AED),
                                    contentColor = Color.White,
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Icon(
                                    imageVector = MaterialSymbols.RoundedFilled.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Reprendre",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
