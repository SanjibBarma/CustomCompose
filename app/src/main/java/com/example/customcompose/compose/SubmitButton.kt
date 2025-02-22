package com.example.customcompose.compose

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel
import com.google.gson.Gson

@Composable
fun SubmitButton(blockListViewModel: BlockListViewModel) {
    Button(
        onClick = {
            val gson = Gson()
            val comboHistory = mutableListOf<SurveyHistoryModel>()
            for (surveyBlockHistory in blockListViewModel.surveyBlockListItem.value) {
                for (surveyHistory in surveyBlockHistory.surveyHistoryModel) {
                    if (surveyHistory != null) {
                        comboHistory.add(
                            SurveyHistoryModel(
                                question = surveyHistory.question,
                                answer = surveyHistory.answer,
                                id = surveyHistory.id
                            )
                        )
                    }
                }
            }
            val comboJson = gson.toJson(comboHistory)
            Log.d("SURVEY_COMBO_DATA", comboJson)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Submit")
    }
}