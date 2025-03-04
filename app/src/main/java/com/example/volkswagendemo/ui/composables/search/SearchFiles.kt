package com.example.volkswagendemo.ui.composables.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.volkswagendemo.R
import com.example.volkswagendemo.ui.composables.general.SelectFileItem
import com.example.volkswagendemo.ui.composables.general.RfidBottomBar
import com.example.volkswagendemo.viewmodel.SearchViewModel

@Composable
fun SearchFiles(
    searchViewModel: SearchViewModel
) {
    val searchUiState = searchViewModel.searchUiState
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(searchUiState.filesList) { file ->
                SelectFileItem(
                    fileName = file,
                    selected = file == searchUiState.selectedFileName,
                    onClickListener = { searchViewModel.selectFile(file) }
                )
            }
        }
        RfidBottomBar(
            isDualMode = false,
            title = stringResource(R.string.inventory_button_start),
            onClickListener = { searchViewModel.setupSearch() },
            isButtonEnable = searchUiState.selectedFileName.isNotEmpty(),
            title2 = "",
            onClickListener2 = { }
        )
    }
}