package eu.kanade.presentation.library.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.kanade.presentation.manga.components.MangaCover
import eu.kanade.presentation.util.formatChapterNumber
import mihon.icons.materialsymbols.MaterialSymbols
import mihon.icons.materialsymbols.rounded.Close
import mihon.icons.materialsymbols.rounded.FilterList
import mihon.icons.materialsymbols.rounded.MoreVert
import mihon.icons.materialsymbols.rounded.Public
import mihon.icons.materialsymbols.rounded.Refresh
import mihon.icons.materialsymbols.rounded.Search
import mihon.icons.materialsymbols.rounded.Sync
import mihon.icons.materialsymbols.roundedfilled.PlayArrow
import tachiyomi.domain.history.model.HistoryWithRelations
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import java.util.Calendar

@Composable
fun YomuHomeHeader(
    lastRead: HistoryWithRelations?,
    onContinueReading: (HistoryWithRelations) -> Unit,
    onExploreClick: (() -> Unit)? = null,
    onSearchClick: (() -> Unit)? = null,
    searchQuery: String? = null,
    onSearchQueryChange: ((String?) -> Unit)? = null,
    hasActiveFilters: Boolean = false,
    onClickFilter: (() -> Unit)? = null,
    onClickRefresh: (() -> Unit)? = null,
    onClickGlobalUpdate: (() -> Unit)? = null,
    onClickOpenRandomManga: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        // --- 1. Top Greeting + Actions (Filter, Menu, Avatar) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Greeting & Name
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

            // Action buttons: Filter, More Menu, Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Filter button with glowing badge if active
                if (onClickFilter != null) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF161426))
                            .border(BorderStroke(1.dp, Color(0xFF2A243D)), CircleShape)
                            .clickable(onClick = onClickFilter),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = MaterialSymbols.Rounded.FilterList,
                            contentDescription = "Filtres",
                            tint = if (hasActiveFilters) Color(0xFF8B5CF6) else Color(0xFFA78BFA),
                            modifier = Modifier.size(19.dp),
                        )
                        if (hasActiveFilters) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 4.dp)
                                    .background(Color(0xFF8B5CF6), CircleShape),
                            )
                        }
                    }
                }

                // More Menu button (Refresh, Global update, Random)
                if (onClickGlobalUpdate != null || onClickRefresh != null || onClickOpenRandomManga != null) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF161426))
                                .border(BorderStroke(1.dp, Color(0xFF2A243D)), CircleShape)
                                .clickable { menuExpanded = true },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = MaterialSymbols.Rounded.MoreVert,
                                contentDescription = "Menu",
                                tint = Color(0xFFA78BFA),
                                modifier = Modifier.size(19.dp),
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(Color(0xFF141221)),
                        ) {
                            if (onClickGlobalUpdate != null) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(MR.strings.action_update_library),
                                            color = Color.White,
                                            fontSize = 13.sp,
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onClickGlobalUpdate()
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = MaterialSymbols.Rounded.Refresh,
                                            contentDescription = null,
                                            tint = Color(0xFFA78BFA),
                                            modifier = Modifier.size(18.dp),
                                        )
                                    },
                                )
                            }
                            if (onClickRefresh != null) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(MR.strings.action_update_category),
                                            color = Color.White,
                                            fontSize = 13.sp,
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onClickRefresh()
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = MaterialSymbols.Rounded.Sync,
                                            contentDescription = null,
                                            tint = Color(0xFFA78BFA),
                                            modifier = Modifier.size(18.dp),
                                        )
                                    },
                                )
                            }
                            if (onClickOpenRandomManga != null) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(MR.strings.action_open_random_manga),
                                            color = Color.White,
                                            fontSize = 13.sp,
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onClickOpenRandomManga()
                                    },
                                )
                            }
                        }
                    }
                }

                // Avatar Fateh avec anneau dégradé violet / indigo
                Box(
                    modifier = Modifier
                        .size(40.dp)
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
                        fontSize = 17.sp,
                    )
                }
            }
        }

        // --- 2. Barre de recherche moderne en capsule (Search Bar) ---
        if (onSearchQueryChange != null) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = Color(0xFF161426),
                border = BorderStroke(1.dp, Color(0xFF2A243D)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = MaterialSymbols.Rounded.Search,
                        contentDescription = null,
                        tint = Color(0xFFA78BFA),
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (searchQuery.isNullOrEmpty()) {
                            Text(
                                text = "Rechercher dans la bibliothèque...",
                                color = Color(0xFF71717A),
                                fontSize = 13.5.sp,
                                maxLines = 1,
                            )
                        }
                        BasicTextField(
                            value = searchQuery ?: "",
                            onValueChange = onSearchQueryChange,
                            singleLine = true,
                            cursorBrush = SolidColor(Color(0xFF8B5CF6)),
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    if (!searchQuery.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = MaterialSymbols.Rounded.Close,
                            contentDescription = "Effacer",
                            tint = Color(0xFFA78BFA),
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .clickable { onSearchQueryChange(null) },
                        )
                    }
                }
            }
        }

        // --- 3. Hero Card : Reprendre la lecture OU Découverte Yomu ---
        // Masquée quand une recherche est en cours pour afficher directement les résultats
        if (searchQuery.isNullOrEmpty()) {
            if (lastRead != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onContinueReading(lastRead) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141221)),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Color(0xFF2A243D)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Couverture du manga (Format 2:3, coins 14dp)
                        MangaCover.Book(
                            data = lastRead.coverData,
                            contentDescription = lastRead.title,
                            modifier = Modifier
                                .width(64.dp)
                                .height(96.dp),
                            shape = RoundedCornerShape(14.dp),
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            // Badge violet pulsé + Pourcentage
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
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
                                Text(
                                    text = "78%",
                                    color = Color(0xFFA78BFA),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

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
                                "Dernier chapitre"
                            }
                            Text(
                                text = chapterLabel,
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            Spacer(modifier = Modifier.height(7.dp))

                            // Fine barre de progression violette
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFF2A243D)),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.78f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color(0xFF8B5CF6)),
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Bouton capsule violet "Resume"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                Button(
                                    onClick = { onContinueReading(lastRead) },
                                    modifier = Modifier.height(32.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF8B5CF6),
                                        contentColor = Color.White,
                                    ),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                                    shape = RoundedCornerShape(999.dp),
                                ) {
                                    Icon(
                                        imageVector = MaterialSymbols.RoundedFilled.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp),
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Resume",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Hero Card Découverte / Accueil (visible si aucun manga lu)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141221)),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Color(0xFF2A243D)),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFFA78BFA), CircleShape),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "YOMU POUR GALAXY Z FOLD",
                                color = Color(0xFFA78BFA),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Prêt pour de nouvelles lectures ?",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Le catalogue Keiyoushi est prêt. Explore les extensions ou lance une recherche pour remplir ta bibliothèque.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            if (onExploreClick != null) {
                                Button(
                                    onClick = onExploreClick,
                                    modifier = Modifier.height(34.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF8B5CF6),
                                        contentColor = Color.White,
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                    shape = RoundedCornerShape(999.dp),
                                ) {
                                    Icon(
                                        imageVector = MaterialSymbols.Rounded.Public,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Explorer les sources",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }

                            if (onSearchClick != null) {
                                OutlinedButton(
                                    onClick = onSearchClick,
                                    modifier = Modifier.height(34.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFA78BFA),
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFF2A243D)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                    shape = RoundedCornerShape(999.dp),
                                ) {
                                    Icon(
                                        imageVector = MaterialSymbols.Rounded.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Rechercher",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
