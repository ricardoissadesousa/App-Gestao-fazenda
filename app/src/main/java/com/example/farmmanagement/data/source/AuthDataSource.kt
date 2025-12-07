package com.example.farmmanagement.data.source

import com.example.farmmanagement.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthDataSource {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("usuarios")

    suspend fun login(email: String, senha: String): Result<Usuario> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, senha).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(Exception("Usuário não encontrado"))

            val userDoc = collection.document(uid).get().await()

            val usuario = userDoc.toObject(Usuario::class.java)?.copy(id = uid)
                ?: return Result.failure(Exception("Dados do usuário não encontrados"))

            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registrar(email: String, senha: String, nome: String, tipo: String): Result<Usuario> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, senha).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(Exception("Erro ao criar usuário"))

            val usuario = Usuario(
                id = uid,
                email = email,
                nome = nome,
                tipo = if (tipo == "GESTOR") com.example.farmmanagement.data.model.TipoUsuario.GESTOR
                       else com.example.farmmanagement.data.model.TipoUsuario.FUNCIONARIO
            )

            collection.document(uid).set(usuario).await()

            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun obterUsuarioAtual(): Flow<Usuario?> = callbackFlow {
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid
            if (uid != null) {
                collection.document(uid).get()
                    .addOnSuccessListener { doc ->
                        trySend(doc.toObject(Usuario::class.java)?.copy(id = uid))
                    }
                    .addOnFailureListener {
                        trySend(null)
                    }
            } else {
                trySend(null)
            }
        }
        auth.addAuthStateListener(authListener)
        awaitClose { auth.removeAuthStateListener(authListener) }
    }

    fun logout() {
        auth.signOut()
    }

    fun isUsuarioLogado(): Boolean {
        return auth.currentUser != null
    }
}

