package com.example.myshoppinglistapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class ShoppingItem(
    val id : Int,
    var name : String,
    var quantity : Int,
    var isEditing : Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyShoppingApp() {
    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var ItemName by remember { mutableStateOf("") }
    var ItemQuantity by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        if (showDialog) {
            AlertDialog(onDismissRequest = { showDialog = false },
                // here we will control the behaviour of control button for saving items.
                confirmButton = {
                    // creating buttons in horizontal direction 
                      Row(modifier= Modifier
                          .fillMaxWidth()
                          .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                         Button(onClick = {
                            if (ItemName.isNotBlank()){
                                val newItem = ShoppingItem(
                                    id = sItems.size + 1,
                                    name = ItemName,
                                    quantity = ItemQuantity.toInt()
                                )
                                sItems = sItems + newItem
                                showDialog = false
                                ItemName = ""
                            }
                         }) {
                             Text(text = "Add")
                         }
                          Button(onClick = { showDialog = false }) {
                              Text(text = "Cancel")
                          }
                      }          
                },
                title = { Text(text = "Add Shopping Item")},
                text = {
                    Column {
                        // Section to enter the item name
                        OutlinedTextField(value = ItemName ,
                            onValueChange = {ItemName = it},
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)

                        )
                        // Section to enter the item quantity
                        OutlinedTextField(value = ItemQuantity ,
                            onValueChange = {ItemQuantity = it},
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)

                        )
                    }
                }
            )
        }
        Button(
            onClick = { showDialog = true }, // Creating a button to add items
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Add Item")
        }


        // Lazy column
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(sItems) {
                //shoppinglistItem(it,{},{})
                item ->
                if (item.isEditing){
                    ShoppingItemEditor(item = item,
                        onEditComplete ={
                            editedName, editedQuantity ->
                            sItems = sItems.map { it.copy(isEditing = false) }
                            val editedItem = sItems.find { it.id == item.id }
                            editedItem ?. let {
                                it.name = editedName
                                it.quantity = editedQuantity
                            }
                        }

                    )
                } else{
                    shoppinglistItem(
                        item = item,
                        onEditClick = {
                            // finding out which item we are editing and changing is " isEditing boolean" to true
                            sItems = sItems.map { it.copy(isEditing = it.id == item.id) }
                        },onDeleteClick = {
                            sItems = sItems - item
                        }

                    )
                }

            }
        }
    }
}

@Composable
fun shoppinglistItem(
    item: ShoppingItem,
    onEditClick : () -> Unit,
    onDeleteClick : () -> Unit
){
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, SnackbarDefaults.color),
                shape = RoundedCornerShape(percent = 20)
            ), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.name, modifier = Modifier.padding(8.dp))
        Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(8.dp))
        Row (
            modifier = Modifier.padding(8.dp)
        ){
            IconButton(onClick = { onEditClick}) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
            IconButton(onClick = { onDeleteClick }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    }

}

@Composable
fun ShoppingItemEditor(
    item: ShoppingItem,
    onEditComplete : (String,Int) -> Unit
){
   var editedName by remember { mutableStateOf(item.name) }
   var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }
   var isEditing by remember{ mutableStateOf(item.isEditing) }

   Row(
       modifier = Modifier
           .fillMaxWidth()
           .background(Color.White)
           .padding(8.dp),
       horizontalArrangement = Arrangement.SpaceEvenly
   ) {
       Column {
           BasicTextField(value = editedName, //text field for edited name
               onValueChange = {editedName = it},
               singleLine = true,
               modifier = Modifier
                   .wrapContentSize()
                   .padding(8.dp)
           ) {

           }

           BasicTextField(value = editedQuantity, //text field for edited quantity
               onValueChange = {editedQuantity = it},
               singleLine = true,
               modifier = Modifier
                   .wrapContentSize()
                   .padding(8.dp)
           ) {

           }
       }
       Button(onClick = {
           isEditing = false
           onEditComplete(editedName,editedQuantity.toIntOrNull() ?: 1) // using elvis operator
       }) {
           Text(text = "Save")
       }
   }
}