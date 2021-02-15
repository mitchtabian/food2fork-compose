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
      title = {
        Text(
            text = title,
        )
      },
      text = {
        if (description != null) {
          Text(
              text = description,
          )
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

data class GenericDialogInfo(
  val title: String,
  val onDismiss: () -> Unit,
  val description: String?,
  val positiveAction: PositiveAction?,
  val negativeAction: NegativeAction?
){
  class Builder(
    var title: String,
    val onDismiss: () -> Unit,
  ) {

    private var description: String? = null
    private var positiveAction: PositiveAction? = null
    private var negativeAction: NegativeAction? = null

    fun description(
      description: String
    ): Builder{
      this.description = description
      return this
    }

    fun positive(
      positiveAction: PositiveAction?,
    ) : Builder {
      this.positiveAction = positiveAction
      return this
    }

    fun negative(
      negativeAction: NegativeAction
    ) : Builder {
      this.negativeAction = negativeAction
      return this
    }

    fun build(): GenericDialogInfo {
      return GenericDialogInfo(
        title = this.title,
        onDismiss = this.onDismiss,
        description = this.description,
        positiveAction = this.positiveAction,
        negativeAction = this.negativeAction
      )
    }
  }
}




















