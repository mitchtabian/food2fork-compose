package com.codingwithmitch.food2forkcompose.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.lang.Exception

@Composable
fun GenericDialog(
        onDismiss: () -> Unit,
        title: String,
        description: String? = null,
        positiveBtnTxt: String? = null,
        onPositiveAction: () -> Unit,
        negativeBtnTxt: String? = null,
        onNegativeAction: () -> Unit,
        modifier: Modifier? = null,

        ){
    if(positiveBtnTxt == null && negativeBtnTxt == null){
        throw Exception("There must be at least one button on a dialog.")
    }
    AlertDialog(
            modifier = modifier?:Modifier.padding(8.dp)
            ,
            onDismissRequest = onDismiss,
            title = {
                Text(
                        text = title,
                )
            },
            text = {
                if(description != null){
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

                ){
                    if(positiveBtnTxt != null){
                        Button(
                                modifier = if(negativeBtnTxt != null) Modifier.padding(end=8.dp) else Modifier,
                                onClick = onPositiveAction,
                        ) {
                            Text(text = positiveBtnTxt)
                        }
                    }
                    if(negativeBtnTxt != null){
                        Button(
                                onClick = onNegativeAction
                        ) {
                            Text(text = negativeBtnTxt)
                        }
                    }
                }
            }
    )
}
