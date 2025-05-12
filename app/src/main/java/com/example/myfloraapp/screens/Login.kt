package com.example.myfloraapp.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.myfloraapp.R
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth

import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.lang.IllegalStateException

@Composable
fun Login (navController: NavHostController, auth: FirebaseAuth) {
    // True=Login ; False=Create
    //var email by remember { mutableStateOf("") }
    val context = LocalContext.current // Obtén el contexto aquí
    val showLoginForm = rememberSaveable {mutableStateOf(true) }
    Surface(modifier=Modifier
                    .fillMaxSize(),
            color = Color(0xFFC8CFC8)
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter= painterResource(R.drawable.myflorasolologopng),
                contentDescription = "Logo MyFloraApp"
            )
            Spacer(Modifier.height(32.dp))
            if (showLoginForm.value) {
                Text("Inicia sesión",
                    style = MaterialTheme.typography.titleLarge)
                UserForm(
                    isCreateAccount = false
                ){
                    email, password ->
                    auth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener{ task->
                            if(task.isSuccessful){
                                Log.d("Javi","Logueando con $email y $password")
                                Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                                navController.navigate("Home")
                            }else{
                                Log.e("Login", "Error al crear la cuenta", task.exception)
                                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            else{
                Text("Crea una cuenta",
                    style = MaterialTheme.typography.titleLarge)
                UserForm(
                    isCreateAccount = true
                ){
                    email, password ->
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Cuenta Creada con Exito!!!!!!!!!!!",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                Log.d("Javi","Creando cuenta con $email y $password")
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Javi", "createUserWithEmail:failure", task.exception)
                                Toast.makeText(
                                    context,
                                    "Error: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                val text1 =
                        if(showLoginForm.value)"¿No tienes cuenta?"
                        else "¿Ya tienes cuenta?"
                val text2 =
                    if(showLoginForm.value)"Registrate"
                    else "Inicia sesión"
                Text(text = text1)
                Text(text = text2,
                        modifier = Modifier
                            .clickable {showLoginForm.value = !showLoginForm.value}
                            .padding(start = 5.dp),
                        color = MaterialTheme.colorScheme.secondary
                )
            }
        }

    }

}

@Composable
fun UserForm(
    isCreateAccount: Boolean=false,
    onDone: (String,String)-> Unit = {email,pwd ->}
){
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordvisible = rememberSaveable { mutableStateOf(false) }
    val valido = remember(email.value, password.value){
        email.value.trim().isNotEmpty() &&
                password.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        EmailInput(
            emailState= email
        )
        PaswordInput(
            passwordState= password,
            labelId = "Password",
            passwordVisible = passwordvisible
        )
        SubmitButton(
            textId = if (isCreateAccount) "Crear cuenta" else "Login",
            inputValido = valido
        ){
            onDone(email.value.trim(),password.value.trim())
            keyboardController?.hide()
        }
    }

}

@Composable
fun SubmitButton(
    textId: String,
    inputValido: Boolean,
    onClick: ()->Unit
) {
    Button( onClick = onClick,
            modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth(),
            shape = CircleShape,
            enabled = inputValido,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF333333), // Negro no muy oscuro
                contentColor = Color.White // Texto en blanco
            )
        ){
        Text(text = textId,
            modifier = Modifier
                .padding(5.dp)
                )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaswordInput(
    passwordState: MutableState<String>,
    labelId: String,
    passwordVisible: MutableState<Boolean>
) {
    val visualTransformation = if(passwordVisible.value)
        VisualTransformation.None
    else PasswordVisualTransformation()
    OutlinedTextField(
        value = passwordState.value,
        onValueChange = {passwordState.value = it},
        label = {Text(text=labelId)},
        singleLine= true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        modifier = Modifier
            .padding(bottom=10.dp,start=10.dp, end=10.dp)
            .fillMaxWidth(),
        visualTransformation = visualTransformation,
        trailingIcon = {
            if(passwordState.value.isNotBlank()){
                PasswordVisibleIcon(passwordVisible)
            }
            else null
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(0xFF333333), // Color del borde cuando está enfocado
            unfocusedBorderColor = Color(0xFF333333), // Color del borde cuando no está enfocado
            focusedLabelColor = Color(0xFF333333), // Color del label cuando está enfocado
            unfocusedLabelColor = Color(0xFF333333) // Color del label cuando no está enfocado
        )
    )
}

@Composable
fun PasswordVisibleIcon(
    passwordVisible: MutableState<Boolean>
) {
    val image =
        if (passwordVisible.value)
            Icons.Default.VisibilityOff
        else
            Icons.Default.Visibility
    IconButton(
        onClick = {
        passwordVisible.value = !passwordVisible.value }
    ) {
        Icon(
            imageVector = image,
            contentDescription=""
        )
    }
}

@Composable
fun EmailInput(
    emailState: MutableState<String>,
    labelId: String = "Email"
){
    InputField(
        valueState=emailState,
        labelId = labelId,
        Keyboard = KeyboardType.Email
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    valueState: MutableState<String>,
    labelId: String,
    Keyboard: KeyboardType,
    isSingleLine: Boolean = true
) {
    OutlinedTextField(
        value = valueState.value,
        onValueChange = {valueState.value = it},
        label = {Text(text=labelId)},
        singleLine=isSingleLine,
        modifier = Modifier
            .padding(bottom=10.dp,start=10.dp, end=10.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = Keyboard
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(0xFF333333), // Color del borde cuando está enfocado
            unfocusedBorderColor = Color(0xFF333333), // Color del borde cuando no está enfocado
            focusedLabelColor = Color(0xFF333333), // Color del label cuando está enfocado
            unfocusedLabelColor = Color(0xFF333333) // Color del label cuando no está enfocado
        )

    )
}
