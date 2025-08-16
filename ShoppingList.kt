package com.example.myshoppinglistapp

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController

data class ShoppingItem(
    val Id:Int, var name:String, var quantity:Int, var isEditing:Boolean=false, var address:String=""
)

@Composable
fun ShoppingListApp(
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    navController: NavController,
    context: Context,
    address: String
){
    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember{ mutableStateOf(false)}
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }

    val requestPermissionLauncher= rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {permissions ->
            if(permissions[Manifest.permission.ACCESS_COARSE_LOCATION]==true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION]==true){
                //I have access to location

                locationUtils.requestLocationUpdates(viewModel=viewModel)

            }else{
                val rationaleRequired= ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )|| ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                if(rationaleRequired){
                    Toast.makeText(context,
                        "Location Permission is required !", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context,
                        "Location Permission is required! Please enable from android settings",
                        Toast.LENGTH_LONG).show()
                }

            }
        } )


    Column (
        modifier= Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ){
        Button(
            onClick = { showDialog=true },
            modifier= Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),

            ) {
            Text(text = "Add Item")
        }
        LazyColumn(
            modifier= Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            items(sItems){
                item ->
                if(item.isEditing){
                    ShoppingListEditor(item = item , onEditComplete = {
                        editedName,editedQuantity->
                        sItems=sItems.map { it.copy(isEditing = false) }
                        val editedItem= sItems.find { it.Id==item.Id }
                        editedItem?.let {
                            it.name=editedName
                            it.quantity=editedQuantity
                            it.address= address
                        }

                    })
                }else{
                    ShoppingItemList(item =item ,
                        onEditClick = {
                        sItems=sItems.map { it.copy(isEditing = it.Id==item.Id) }
                                      },
                        onDeleteClick = {
                        sItems=sItems-item
                        }
                    )
                }
            }
        }
    }
    
    if(showDialog){
        AlertDialog(onDismissRequest = {showDialog = false},
            confirmButton = {
                            Row(modifier= Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween)
                            {
                                Button(onClick =
                                {
                                    if(itemName.isNotBlank()){
                                        val newItem=ShoppingItem(
                                                Id=sItems.size+1,
                                            name=itemName,
                                            quantity=itemQuantity.toInt(),
                                            address=address
                                        )
                                        sItems+=newItem
                                        showDialog=false
                                        itemName=""
                                        itemQuantity=""
                                    }

                                }) {
                                    Text(text = "Add")
                                }
                                Button(onClick = { showDialog=false }) {
                                    Text(text = "Cancel")
                                }
                            }
            },
            title = {Text(text ="Add Shooping Item")},
            text={
                Column {
                    OutlinedTextField(
                        value =itemName,
                        onValueChange ={itemName=it},
                        singleLine=true,
                        modifier= Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = {Text("Item Name")}
                    )
                    OutlinedTextField(
                        value =itemQuantity,
                        onValueChange ={itemQuantity=it},
                        singleLine=true,
                        modifier= Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = {Text("Quantity")}
                    )

                    Button(onClick = {
                        if (locationUtils.hasLocationPermission(context)){
                            locationUtils.requestLocationUpdates(viewModel)
                            navController.navigate("locationscreen"){
                                this.launchSingleTop
                            }
                        }else{
                            requestPermissionLauncher.launch(arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ))
                        }
                    }) {
                        Text(text ="address")
                    }

                }
            }
        )
    }
}

@Composable
fun ShoppingListEditor(item: ShoppingItem, onEditComplete:(String,Int)->Unit )
{
    var editedName by remember{ mutableStateOf(item.name)}
    var editedQuantity by remember{ mutableStateOf(item.quantity.toString())}
    var isEditing by remember{ mutableStateOf(item.isEditing)}

    Row(modifier= Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .background(Color.White),
        horizontalArrangement = Arrangement.SpaceEvenly)
    {
        Column{
            BasicTextField(value = editedName,
                onValueChange = {editedName=it}, singleLine = true,
                modifier= Modifier
                    .wrapContentSize()
                    .padding(8.dp))
            BasicTextField(value = editedQuantity,
                onValueChange = {editedQuantity=it}, singleLine = true,
                modifier= Modifier
                    .wrapContentSize()
                    .padding(8.dp))
        }

        Button(
            onClick = {
                isEditing=false
                onEditComplete(editedName,editedQuantity.toIntOrNull() ?: 1)
            }
        )
        {
            Text(text = "Save")

        }
    }


}



@Composable
fun ShoppingItemList(
    item: ShoppingItem,
    //()->Unit - lambda function () means take nothing,-> means return,Unit means nothing
    onEditClick: ()->Unit,
    onDeleteClick:()->Unit
)
{
    Row (
        modifier= Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                BorderStroke(2.dp, Color(0xFF018786)),
                RoundedCornerShape(20)
            ), horizontalArrangement = Arrangement.SpaceBetween
    ){
        Column(modifier= Modifier
            .weight(1f)
            .padding(8.dp)) {
            Row {
                Text(text = item.name,Modifier.padding(8.dp))
                Text(text = "Qty: ${item.quantity}",Modifier.padding(8.dp))
            }
            Row (modifier=Modifier.fillMaxWidth()){
                Icon(imageVector = Icons.Default.LocationOn, contentDescription =null )
                Text(text = item.address)
            }
        }


        Row (modifier=Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly){
            IconButton(onClick =onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
            IconButton(onClick =onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }

        }
    }
}

/*@Preview
@Composable
fun ShoppingListPreview(){
    ShoppingListApp()
}*/
