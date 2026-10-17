package com.sartori.rmnews.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sartori.rmnews.R

/** Stays outside the NavHost so the title remains continuous across destinations. */
@Composable
internal fun NewsTopBar(
    isDetail: Boolean,
    isSearchVisible: Boolean,
    onBack: () -> Unit,
    onToggleSearch: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = isDetail,
            enter = expandHorizontally(tween(300), expandFrom = Alignment.Start) +
                slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300)),
            exit = shrinkHorizontally(tween(300), shrinkTowards = Alignment.Start) +
                slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300))
        ) {
            IconButton(onClick = onBack, enabled = isDetail) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = stringResource(R.string.back)
                )
            }
        }
        Text(
            text = stringResource(R.string.news_feed_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        AnimatedVisibility(
            visible = !isDetail,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300))
        ) {
            IconButton(onClick = onToggleSearch, enabled = !isDetail) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = stringResource(
                        if (isSearchVisible) R.string.hide_search else R.string.show_search
                    )
                )
            }
        }
    }
}
