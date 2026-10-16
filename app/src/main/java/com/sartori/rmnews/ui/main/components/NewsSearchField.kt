package com.sartori.rmnews.ui.main.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sartori.rmnews.R

@Composable
internal fun NewsSearchField(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier,
        label = { Text(stringResource(R.string.search_headlines)) },
        singleLine = true,
        trailingIcon = {
            if (query.isNotEmpty()) {
                TextButton(onClick = { onQueryChanged("") }) {
                    Text(stringResource(R.string.clear_search))
                }
            }
        }
    )
}
