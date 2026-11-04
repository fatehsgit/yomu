package eu.kanade.tachiyomi.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import eu.kanade.presentation.util.Screen
import eu.kanade.presentation.util.isTabletUi
import eu.kanade.tachiyomi.ui.browse.BrowseTab
import eu.kanade.tachiyomi.ui.download.DownloadQueueScreen
import eu.kanade.tachiyomi.ui.history.HistoryTab
import eu.kanade.tachiyomi.ui.library.LibraryTab
import eu.kanade.tachiyomi.ui.manga.MangaScreen
import eu.kanade.tachiyomi.ui.more.MoreTab
import eu.kanade.tachiyomi.ui.updates.UpdatesTab
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import mihon.app.di.appGraph
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.pluralStringResource

object HomeScreen : Screen() {

    private val librarySearchEvent = Channel<String>()
    private val openTabEvent = Channel<Tab>()
    private val showBottomNavEvent = Channel<Boolean>()

    @Suppress("ConstPropertyName")
    private const val TabFadeDuration = 200

    @Suppress("ConstPropertyName")
    private const val TabNavigatorKey = "HomeTabs"

    private val TABS = listOf(
        LibraryTab,
        UpdatesTab,
        HistoryTab,
        BrowseTab,
        MoreTab,
    )

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        TabNavigator(
            tab = LibraryTab,
            key = TabNavigatorKey,
        ) { tabNavigator ->
            // Provide usable navigator to content screen
            CompositionLocalProvider(LocalNavigator provides navigator) {
                val tabletUi = isTabletUi()
                var isDockVisible by remember { mutableStateOf(true) }
                val scope = rememberCoroutineScope()

                LaunchedEffect(tabletUi) {
                    if (tabletUi) isDockVisible = true
                    showBottomNavEvent.receiveAsFlow().collectLatest { show ->
                        isDockVisible = if (tabletUi) true else show
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0F111A)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (tabletUi) {
                                    Modifier.padding(end = if (isDockVisible) 84.dp else 0.dp)
                                } else {
                                    Modifier.padding(bottom = if (isDockVisible) 76.dp else 0.dp)
                                }
                            ),
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            AnimatedContent(
                                targetState = tabNavigator.current,
                                transitionSpec = {
                                    materialFadeThroughIn(
                                        initialScale = 1f,
                                        durationMillis = TabFadeDuration,
                                    ) togetherWith materialFadeThroughOut(durationMillis = TabFadeDuration)
                                },
                                label = "tabContent",
                            ) {
                                tabNavigator.saveableState(key = "currentTab", it) {
                                    it.Content()
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = isDockVisible,
                        enter = fadeIn() + if (tabletUi) slideInHorizontally { it } else slideInVertically { it },
                        exit = fadeOut() + if (tabletUi) slideOutHorizontally { it } else slideOutVertically { it },
                        modifier = if (tabletUi) {
                            Modifier.align(Alignment.CenterEnd)
                        } else {
                            Modifier.align(Alignment.BottomCenter)
                        },
                    ) {
                        YomuFloatingDock(
                            tabs = TABS,
                            currentTab = tabNavigator.current,
                            isTablet = tabletUi,
                            onTabSelected = { tab ->
                                if (tabNavigator.current::class != tab::class) {
                                    tabNavigator.current = tab
                                } else {
                                    scope.launch { tab.onReselect(navigator) }
                                }
                            },
                            badgeProvider = { tab -> tabBadge(tab) },
                        )
                    }
                }
            }

            val goToLibraryTab = { tabNavigator.current = LibraryTab }

            BackHandler(enabled = tabNavigator.current != LibraryTab, onBack = goToLibraryTab)

            LaunchedEffect(Unit) {
                launch {
                    librarySearchEvent.receiveAsFlow().collectLatest {
                        goToLibraryTab()
                        LibraryTab.search(it)
                    }
                }
                launch {
                    openTabEvent.receiveAsFlow().collectLatest {
                        tabNavigator.current = when (it) {
                            is Tab.Library -> LibraryTab
                            Tab.Updates -> UpdatesTab
                            Tab.History -> HistoryTab
                            is Tab.Browse -> {
                                if (it.toExtensions) {
                                    BrowseTab.showExtension()
                                }
                                BrowseTab
                            }
                            is Tab.More -> MoreTab
                        }

                        if (it is Tab.Library && it.mangaIdToOpen != null) {
                            navigator.push(MangaScreen(it.mangaIdToOpen))
                        }
                        if (it is Tab.More && it.toDownloads) {
                            navigator.push(DownloadQueueScreen)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun YomuFloatingDock(
        tabs: List<eu.kanade.presentation.util.Tab>,
        currentTab: eu.kanade.presentation.util.Tab,
        isTablet: Boolean,
        onTabSelected: (eu.kanade.presentation.util.Tab) -> Unit,
        badgeProvider: @Composable (eu.kanade.presentation.util.Tab) -> (@Composable () -> Unit)?,
        modifier: Modifier = Modifier,
    ) {
        val dockBgColor = Color(0xEE161426)
        val dockBorderColor = Color(0x33A78BFA)
        val primaryAccent = Color(0xFFA78BFA)
        val unselectedColor = Color(0xFF94A3B8)

        if (isTablet) {
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = dockBgColor,
                border = BorderStroke(1.dp, dockBorderColor),
                shadowElevation = 16.dp,
                modifier = modifier
                    .navigationBarsPadding()
                    .padding(end = 16.dp)
                    .wrapContentHeight(),
            ) {
                Column(
                    modifier = Modifier
                        .width(76.dp)
                        .padding(vertical = 16.dp, horizontal = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    tabs.fastForEach { tab ->
                        val selected = currentTab::class == tab::class
                        val activeBgColor = if (selected) Color(0x26A78BFA) else Color.Transparent

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(activeBgColor)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = false, radius = 28.dp),
                                ) { onTabSelected(tab) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                        ) {
                            val badge = badgeProvider(tab)
                            if (badge != null) {
                                BadgedBox(badge = { badge() }) {
                                    Icon(
                                        painter = tab.options.icon!!,
                                        contentDescription = tab.options.title,
                                        tint = if (selected) primaryAccent else unselectedColor,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                            } else {
                                Icon(
                                    painter = tab.options.icon!!,
                                    contentDescription = tab.options.title,
                                    tint = if (selected) primaryAccent else unselectedColor,
                                    modifier = Modifier.size(24.dp),
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = tab.options.title,
                                color = if (selected) primaryAccent else unselectedColor,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 10.sp,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        } else {
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = dockBgColor,
                border = BorderStroke(1.dp, dockBorderColor),
                shadowElevation = 16.dp,
                modifier = modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, bottom = 12.dp)
                    .widthIn(max = 440.dp)
                    .fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    tabs.fastForEach { tab ->
                        val selected = currentTab::class == tab::class
                        val activeBgColor = if (selected) Color(0x26A78BFA) else Color.Transparent

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(activeBgColor)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = false, radius = 28.dp),
                                ) { onTabSelected(tab) }
                                .padding(vertical = 6.dp),
                        ) {
                            val badge = badgeProvider(tab)
                            if (badge != null) {
                                BadgedBox(badge = { badge() }) {
                                    Icon(
                                        painter = tab.options.icon!!,
                                        contentDescription = tab.options.title,
                                        tint = if (selected) primaryAccent else unselectedColor,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                            } else {
                                Icon(
                                    painter = tab.options.icon!!,
                                    contentDescription = tab.options.title,
                                    tint = if (selected) primaryAccent else unselectedColor,
                                    modifier = Modifier.size(24.dp),
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = tab.options.title,
                                color = if (selected) primaryAccent else unselectedColor,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 10.5.sp,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun tabBadge(tab: eu.kanade.presentation.util.Tab): (@Composable () -> Unit)? {
        val context = LocalContext.current
        val count by produceState(initialValue = 0, tab) {
            val graph = context.appGraph
            when (tab) {
                is UpdatesTab -> {
                    combine(
                        graph.libraryPreferences.newShowUpdatesCount.changes(),
                        graph.libraryPreferences.newUpdatesCount.changes(),
                    ) { show, count ->
                        if (show) count else 0
                    }
                        .collectLatest { value = it }
                }

                is BrowseTab -> {
                    graph.sourcePreferences.extensionUpdatesCount.changes()
                        .collectLatest { value = it }
                }

                else -> value = 0
            }
        }
        if (count <= 0) return null
        return {
            Badge(
                containerColor = Color(0xFFA855F7),
                contentColor = Color.White,
            ) {
                val desc = when (tab) {
                    is UpdatesTab -> pluralStringResource(
                        MR.plurals.notification_chapters_generic,
                        count = count,
                        count,
                    )

                    is BrowseTab -> pluralStringResource(
                        MR.plurals.update_check_notification_ext_updates,
                        count = count,
                        count,
                    )

                    else -> null
                }
                Text(
                    text = count.toString(),
                    modifier = Modifier.semantics {
                        if (desc != null) contentDescription = desc
                    },
                )
            }
        }
    }

    suspend fun search(query: String) {
        librarySearchEvent.send(query)
    }

    suspend fun openTab(tab: Tab) {
        openTabEvent.send(tab)
    }

    suspend fun showBottomNav(show: Boolean) {
        showBottomNavEvent.send(show)
    }

    sealed interface Tab {
        data class Library(val mangaIdToOpen: Long? = null) : Tab
        data object Updates : Tab
        data object History : Tab
        data class Browse(val toExtensions: Boolean = false) : Tab
        data class More(val toDownloads: Boolean) : Tab
    }
}
