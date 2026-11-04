package eu.kanade.presentation.more

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.kanade.tachiyomi.ui.more.DownloadQueueState
import mihon.icons.materialsymbols.MaterialSymbols
import mihon.icons.materialsymbols.automirroredrounded.ArrowForward
import mihon.icons.materialsymbols.automirroredrounded.Help
import mihon.icons.materialsymbols.automirroredrounded.Label
import mihon.icons.materialsymbols.rounded.CloudOff
import mihon.icons.materialsymbols.rounded.Download
import mihon.icons.materialsymbols.rounded.Eyeglasses2
import mihon.icons.materialsymbols.rounded.Info
import mihon.icons.materialsymbols.rounded.Palette
import mihon.icons.materialsymbols.rounded.QueryStats
import mihon.icons.materialsymbols.rounded.Settings
import mihon.icons.materialsymbols.rounded.Storage
import tachiyomi.core.common.Constants
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.ScrollbarLazyColumn
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.i18n.pluralStringResource
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.plus

@Composable
fun MoreScreen(
    downloadQueueStateProvider: () -> DownloadQueueState,
    downloadedOnly: Boolean,
    onDownloadedOnlyChange: (Boolean) -> Unit,
    incognitoMode: Boolean,
    onIncognitoModeChange: (Boolean) -> Unit,
    onClickDownloadQueue: () -> Unit,
    onClickCategories: () -> Unit,
    onClickStats: () -> Unit,
    onClickDataAndStorage: () -> Unit,
    onClickSettings: () -> Unit,
    onClickSupport: () -> Unit,
    onClickAbout: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    Scaffold { contentPadding ->
        ScrollbarLazyColumn(
            contentPadding = contentPadding + PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // 1. En-tête Profil Fateh & Aperçu NAS
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF141221),
                    border = BorderStroke(1.dp, Color(0xFF2A243D)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        // Avatar dégradé violet / indigo avec lettre F
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF8B5CF6), Color(0xFF6366F1)),
                                    ),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "F",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = "Fateh",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                )
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = Color(0x338B5CF6),
                                ) {
                                    Text(
                                        text = "Admin",
                                        color = Color(0xFFC084FC),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    )
                                }
                            }
                            Text(
                                text = "NAS « nasgroot » (DS720+)",
                                color = Color(0xFFC084FC),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                            )
                            Text(
                                text = "46 titres • 3 240 chapitres synchronisés",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                            )
                        }
                    }
                }
            }

            // 2. Ligne Mode Incognito
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF141221),
                    border = BorderStroke(1.dp, Color(0xFF2A243D)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x228B5CF6)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = MaterialSymbols.Rounded.Eyeglasses2,
                                    contentDescription = null,
                                    tint = Color(0xFFC084FC),
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                            Column {
                                Text(
                                    text = stringResource(MR.strings.pref_incognito_mode),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                )
                                Text(
                                    text = stringResource(MR.strings.pref_incognito_mode_summary),
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                )
                            }
                        }

                        Switch(
                            checked = incognitoMode,
                            onCheckedChange = onIncognitoModeChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF8B5CF6),
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF2A243D),
                            ),
                        )
                    }
                }
            }

            // 3. File de téléchargements
            item {
                val downloadQueueState = downloadQueueStateProvider()
                val queueSummary = when (downloadQueueState) {
                    DownloadQueueState.Stopped -> null
                    is DownloadQueueState.Paused -> {
                        val pending = downloadQueueState.pending
                        if (pending == 0) {
                            stringResource(MR.strings.paused)
                        } else {
                            "${stringResource(MR.strings.paused)} • ${
                                pluralStringResource(
                                    MR.plurals.download_queue_summary,
                                    count = pending,
                                    pending,
                                )
                            }"
                        }
                    }
                    is DownloadQueueState.Downloading -> {
                        val pending = downloadQueueState.pending
                        pluralStringResource(MR.plurals.download_queue_summary, count = pending, pending)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF141221),
                    border = BorderStroke(1.dp, Color(0xFF2A243D)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = onClickDownloadQueue),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x2210B981)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = MaterialSymbols.Rounded.Download,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                            Column {
                                Text(
                                    text = stringResource(MR.strings.label_download_queue),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                )
                                Text(
                                    text = queueSummary ?: "Aucun téléchargement actif",
                                    color = if (queueSummary != null) Color(0xFF10B981) else Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                )
                            }
                        }

                        Icon(
                            imageVector = MaterialSymbols.AutoMirroredRounded.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }

            // 4. Carte modulaire : Apparence & Bibliothèque
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF141221),
                    border = BorderStroke(1.dp, Color(0xFF2A243D)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column {
                        MoreClickableRow(
                            title = "Apparence & Thèmes",
                            subtitle = "Thème OLED Noir Pur • Violet Mihon",
                            icon = MaterialSymbols.Rounded.Palette,
                            iconColor = Color(0xFFC084FC),
                            iconBgColor = Color(0x228B5CF6),
                            onClick = onClickSettings,
                        )
                        HorizontalDivider(color = Color(0xFF2A243D), thickness = 1.dp)
                        MoreClickableRow(
                            title = stringResource(MR.strings.categories),
                            subtitle = "En cours, Manhwa, Favoris, Non lus",
                            icon = MaterialSymbols.AutoMirroredRounded.Label,
                            iconColor = Color(0xFF60A5FA),
                            iconBgColor = Color(0x223B82F6),
                            onClick = onClickCategories,
                        )
                        HorizontalDivider(color = Color(0xFF2A243D), thickness = 1.dp)
                        MoreSwitchRow(
                            title = stringResource(MR.strings.label_downloaded_only),
                            subtitle = stringResource(MR.strings.downloaded_only_summary),
                            icon = MaterialSymbols.Rounded.CloudOff,
                            iconColor = Color(0xFFFBBF24),
                            iconBgColor = Color(0x22F59E0B),
                            checked = downloadedOnly,
                            onCheckedChanged = onDownloadedOnlyChange,
                        )
                    }
                }
            }

            // 5. Carte modulaire : Suivi & Données
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF141221),
                    border = BorderStroke(1.dp, Color(0xFF2A243D)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column {
                        MoreClickableRow(
                            title = stringResource(MR.strings.label_stats),
                            subtitle = "Temps de lecture, graphiques et records",
                            icon = MaterialSymbols.Rounded.QueryStats,
                            iconColor = Color(0xFFF472B6),
                            iconBgColor = Color(0x22EC4899),
                            onClick = onClickStats,
                        )
                        HorizontalDivider(color = Color(0xFF2A243D), thickness = 1.dp)
                        MoreClickableRow(
                            title = stringResource(MR.strings.label_data_storage),
                            subtitle = "Sauvegarder ou restaurer la bibliothèque",
                            icon = MaterialSymbols.Rounded.Storage,
                            iconColor = Color(0xFF34D399),
                            iconBgColor = Color(0x2210B981),
                            onClick = onClickDataAndStorage,
                        )
                    }
                }
            }

            // 6. Carte modulaire : Paramètres & NAS
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF141221),
                    border = BorderStroke(1.dp, Color(0xFF2A243D)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column {
                        MoreClickableRow(
                            title = stringResource(MR.strings.label_settings),
                            subtitle = "Lecteur, sécurité, téléchargements et réseau",
                            icon = MaterialSymbols.Rounded.Settings,
                            iconColor = Color(0xFF94A3B8),
                            iconBgColor = Color(0x2264748B),
                            onClick = onClickSettings,
                        )
                        HorizontalDivider(color = Color(0xFF2A243D), thickness = 1.dp)
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0x228B5CF6)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = MaterialSymbols.Rounded.Storage,
                                        contentDescription = null,
                                        tint = Color(0xFFC084FC),
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Serveur NAS « nasgroot »",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                    )
                                    Text(
                                        text = "Synology DS720+ • DSM 7.4.1",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = Color(0x2210B981),
                            ) {
                                Text(
                                    text = "Opérationnel",
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                )
                            }
                        }
                        HorizontalDivider(color = Color(0xFF2A243D), thickness = 1.dp)
                        MoreClickableRow(
                            title = stringResource(MR.strings.pref_category_about),
                            subtitle = "Yomu v0.21.9 • Moteur Mihon",
                            icon = MaterialSymbols.Rounded.Info,
                            iconColor = Color(0xFFC084FC),
                            iconBgColor = Color(0x228B5CF6),
                            onClick = onClickAbout,
                        )
                        HorizontalDivider(color = Color(0xFF2A243D), thickness = 1.dp)
                        MoreClickableRow(
                            title = stringResource(MR.strings.label_help),
                            subtitle = "Documentation et guides",
                            icon = MaterialSymbols.AutoMirroredRounded.Help,
                            iconColor = Color(0xFF818CF8),
                            iconBgColor = Color(0x226366F1),
                            onClick = { uriHandler.openUri(Constants.URL_HELP) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoreClickableRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f),
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                )
            }
        }
        Icon(
            imageVector = MaterialSymbols.AutoMirroredRounded.ArrowForward,
            contentDescription = null,
            tint = Color(0xFF64748B),
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun MoreSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f),
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChanged,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF8B5CF6),
                uncheckedThumbColor = Color(0xFF94A3B8),
                uncheckedTrackColor = Color(0xFF2A243D),
            ),
        )
    }
}
