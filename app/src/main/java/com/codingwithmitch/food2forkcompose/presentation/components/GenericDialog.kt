package com.codingwithmitch.food2forkcompose.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GenericDialog(
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit,
  title: String,
  description: String? = null,
  positiveAction: PositiveAction?,
  negativeAction: NegativeAction?,
) {
  AlertDialog(
    modifier = modifier,
    onDismissRequest = onDismiss,
    title = { Text(title) },
    text = {
      if (description != null) {
        Text(text = description)
      }
    },
    buttons = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
        horizontalArrangement = Arrangement.End,
      ) {
        if(negativeAction != null){
          Button(
            modifier = Modifier.padding(end = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onError),
            onClick = negativeAction.onNegativeAction
          ) {
            Text(text = negativeAction.negativeBtnTxt)
          }
        }
        if(positiveAction != null){
          Button(
            modifier = Modifier.padding(end = 8.dp),
            onClick = positiveAction.onPositiveAction,
          ) {
            Text(text = positiveAction.positiveBtnTxt)
          }
        }
      }
    }
  )
}

data class PositiveAction(
  val positiveBtnTxt: String,
  val onPositiveAction: () -> Unit,
)

data class NegativeAction(
  val negativeBtnTxt: String,
  val onNegativeAction: () -> Unit,
)













