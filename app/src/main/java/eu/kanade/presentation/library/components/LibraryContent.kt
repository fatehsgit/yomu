package eu.kanade.presentation.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import eu.kanade.core.preference.PreferenceMutableState
import eu.kanade.tachiyomi.ui.library.LibraryItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tachiyomi.domain.category.model.Category
import tachiyomi.domain.history.model.HistoryWithRelations
import tachiyomi.domain.library.model.LibraryDisplayMode
import tachiyomi.domain.library.model.LibraryManga
import tachiyomi.presentation.core.components.material.PullRefresh
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@Composable
fun LibraryContent(
    categories: List<Category>,
    searchQuery: String?,
    selection: Set<Long>,
    contentPadding: PaddingValues,
    currentPage: Int,
    hasActiveFilters: Boolean,
    showPageTabs: Boolean,
    onChangeCurrentPage: (Int) -> Unit,
    onClickManga: (Long) -> Unit,
    onContinueReadingClicked: ((LibraryManga) -> Unit)?,
    onToggleSelection: (Category, LibraryManga) -> Unit,
    onToggleRangeSelection: (Category, LibraryManga) -> Unit,
    onRefresh: suspend () -> Boolean,
    onGlobalSearchClicked: () -> Unit,
    getItemCountForCategory: (Category) -> Int?,
    getDisplayMode: (Int) -> PreferenceMutableState<LibraryDisplayMode>,
    getColumnsForOrientation: (Boolean) -> PreferenceMutableState<Int>,
    getItemsForCategory: (Category) -> List<LibraryItem>,
    lastReadItem: HistoryWithRelations? = null,
    onContinueReadingHistory: ((HistoryWithRelations) -> Unit)? = null,
    onSearchQueryChange: (String?) -> Unit = {},
    onClickFilter: () -> Unit = {},
    onClickRefreshCategory: () -> Unit = {},
    onClickGlobalUpdate: () -> Unit = {},
    onClickOpenRandomManga: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(currentPage) { categories.size }
    var isRefreshing by remember(pagerState.currentPage) { mutableStateOf(false) }

    var headerHeightPx by remember { mutableFloatStateOf(0f) }
    var headerOffsetPx by remember { mutableFloatStateOf(0f) }

    // Reset offset when changing page
    LaunchedEffect(currentPage) {
        headerOffsetPx = 0f
    }

    // Reset offset when selecting or searching
    LaunchedEffect(selection.isNotEmpty(), searchQuery) {
        if (selection.isNotEmpty() || !searchQuery.isNullOrEmpty()) {
            headerOffsetPx = 0f
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                // Scrolling down (delta < 0): collapse header towards top
                if (delta < 0f && headerOffsetPx > -headerHeightPx && headerHeightPx > 0f) {
                    val newOffset = (headerOffsetPx + delta).coerceIn(-headerHeightPx, 0f)
                    val consumed = newOffset - headerOffsetPx
                    headerOffsetPx = newOffset
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                // Scrolling up (available.y > 0) when grid reaches top: expand header
                if (available.y > 0f && headerOffsetPx < 0f) {
                    val newOffset = (headerOffsetPx + available.y).coerceIn(-headerHeightPx, 0f)
                    val consumed = newOffset - headerOffsetPx
                    headerOffsetPx = newOffset
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .padding(
                top = contentPadding.calculateTopPadding(),
                start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
            )
            .nestedScroll(nestedScrollConnection),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .layout { measurable, constraints ->
                    val headerH = headerHeightPx.roundToInt()
                    val placeable = measurable.measure(
                        constraints.copy(
                            maxHeight = constraints.maxHeight + headerH,
                        ),
                    )
                    layout(placeable.width, constraints.maxHeight) {
                        placeable.place(0, headerOffsetPx.roundToInt())
                    }
                },
        ) {
            // Header d'accueil Yomu (Bonjour Fateh, Recherche capsule, Hero Card)
            if (selection.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { headerHeightPx = it.height.toFloat() },
                ) {
                    YomuHomeHeader(
                        lastRead = lastReadItem,
                        onContinueReading = { onContinueReadingHistory?.invoke(it) },
                        onExploreClick = null,
                        onSearchClick = onGlobalSearchClicked,
                        searchQuery = searchQuery,
                        onSearchQueryChange = onSearchQueryChange,
                        hasActiveFilters = hasActiveFilters,
                        onClickFilter = onClickFilter,
                        onClickRefresh = onClickRefreshCategory,
                        onClickGlobalUpdate = onClickGlobalUpdate,
                        onClickOpenRandomManga = onClickOpenRandomManga,
                    )
                }
            }

            // Onglets de catégories sous forme de capsules arrondies (fixés en haut dès le repli)
            if (showPageTabs && categories.isNotEmpty() && (categories.size > 1 || !categories.first().isSystemCategory)) {
                LaunchedEffect(categories) {
                    if (categories.size <= pagerState.currentPage) {
                        pagerState.scrollToPage(categories.size - 1)
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF08090E)),
                ) {
                    LibraryTabs(
                        categories = categories,
                        pagerState = pagerState,
                        getItemCountForCategory = getItemCountForCategory,
                        onTabItemClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(it)
                            }
                        },
                    )
                }
            }

            // Grille de mangas avec pull-to-refresh
            PullRefresh(
                refreshing = isRefreshing,
                enabled = selection.isEmpty(),
                onRefresh = {
                    scope.launch {
                        if (!onRefresh()) return@launch
                        // Fake refresh status but hide it after a second as it's a long running task
                        isRefreshing = true
                        delay(1.seconds)
                        isRefreshing = false
                    }
                },
                modifier = Modifier.fillMaxSize(),
            ) {
                LibraryPager(
                    state = pagerState,
                    contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding() + 96.dp),
                    hasActiveFilters = hasActiveFilters,
                    selection = selection,
                    searchQuery = searchQuery,
                    onGlobalSearchClicked = onGlobalSearchClicked,
                    getCategoryForPage = { page -> categories[page] },
                    getDisplayMode = getDisplayMode,
                    getColumnsForOrientation = getColumnsForOrientation,
                    getItemsForCategory = getItemsForCategory,
                    onClickManga = { category, manga ->
                        if (selection.isNotEmpty()) {
                            onToggleSelection(category, manga)
                        } else {
                            onClickManga(manga.manga.id)
                        }
                    },
                    onLongClickManga = onToggleRangeSelection,
                    onClickContinueReading = onContinueReadingClicked,
                )
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        onChangeCurrentPage(pagerState.currentPage)
    }
}
