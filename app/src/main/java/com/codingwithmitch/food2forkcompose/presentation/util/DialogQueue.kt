package com.codingwithmitch.food2forkcompose.presentation.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.codingwithmitch.food2forkcompose.presentation.components.GenericDialogInfo
import com.codingwithmitch.food2forkcompose.presentation.components.PositiveAction
import java.util.*

class DialogQueue {

  // Queue for "First-In-First-Out" behavior
  val queue: MutableState<Queue<GenericDialogInfo>> = mutableStateOf(LinkedList())

  fun removeHeadMessage(){
    if (queue.value.isNotEmpty()) {
      val update = queue.value
      update.remove() // remove first (oldest message)
      queue.value = ArrayDeque() // force recompose (bug?)
      queue.value = update
    }
  }

  fun appendErrorMessage(title: String, description: String){
    queue.value.offer(
      GenericDialogInfo.Builder()
        .title(title)
        .onDismiss(this::removeHeadMessage)
        .description(description)
        .positive(
          PositiveAction(
            positiveBtnTxt = "Ok",
            onPositiveAction = this::removeHeadMessage,
          )
        )
        .build()
    )
  }
}

































