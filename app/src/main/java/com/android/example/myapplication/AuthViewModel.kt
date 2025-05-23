import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storageRef: StorageReference = FirebaseStorage.getInstance().reference
    fun signupWithEmailPassword(email: String, password: String,
                                onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }
    fun loginWithEmailPassword(email: String, password: String,
                               onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }
    fun uploadProfilePicture(imageUri: Uri, onResult: (Boolean,
                                                       String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false, "User
                not logged in")
            val profilePicRef =
        storageRef.child("profile_pictures/$uid.jpg") // ✅ Correct here!
        profilePicRef.putFile(imageUri)
            .addOnSuccessListener {
                profilePicRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileUpdates =
                        UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri)
                            .build()
                    auth.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                onResult(true, null)
                            } else {
                                onResult(false,
                                    updateTask.exception?.message)
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun deleteProfilePicture(onResult: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false, "User
                not logged in")
            val profilePicRef =
        storageRef.child("profile_pictures/$uid.jpg")
        profilePicRef.delete()
            .addOnSuccessListener {
                // After successful delete, clear user's photoUrl
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(null)
                    .build()
                auth.currentUser?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            onResult(true, null)
                        } else {
                            onResult(false,
                                updateTask.exception?.message)
                        }
                    }
            }
            .addOnFailureListener { e ->
                if (e.message?.contains("Object does not exist") ==
                    true) {
                    // If the object didn't exist, consider it
                    successful
                    val profileUpdates =
                        UserProfileChangeRequest.Builder()
                            .setPhotoUri(null)
                            .build()
                    auth.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                onResult(true, null)
                            } else {
                                onResult(false,
                                    updateTask.exception?.message)
                            }
                        }
                } else {
                    onResult(false, e.message)
                }
            }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    fun signOut() {
        auth.signOut()
    }
}
