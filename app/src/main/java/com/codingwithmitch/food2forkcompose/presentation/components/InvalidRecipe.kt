package com.codingwithmitch.food2forkcompose.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun InvalidRecipe(onNavigateBack: () -> Unit,){
  Box(
    modifier = Modifier.fillMaxSize()
  ){
    Column(modifier = Modifier.align(Alignment.Center)){
      Text(
        modifier = Modifier.padding(8.dp).align(Alignment.CenterHorizontally),
        text = "Sorry, we're having trouble finding this recipe.",
        style = MaterialTheme.typography.h6
      )
      Button(
        modifier = Modifier.padding(8.dp).align(Alignment.CenterHorizontally),
        onClick = {
          onNavigateBack()
        }
      ){
        Text(
          text = "Go back",
          style = MaterialTheme.typography.button
        )
      }
    }

  }
}