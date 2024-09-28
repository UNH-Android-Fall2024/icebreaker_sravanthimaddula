package com.example.demo


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.demo.databinding.ActivityMainBinding

// Main activity where my code starts
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val className = "android-fall24"
    private val db = Firebase.firestore
    private var TAG = "IcebreakerF24-TAG"
    private var questionBank: MutableList<questions>? = arrayListOf()

    // Override the onCreate Fucntion and do stuff
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getQuestionsFromFirebase()

        binding.btnSetRandomQuestion.setOnClickListener {
            binding.txtQuestion.text = questionBank!!.random().text
        }

        binding.btnSubmit.setOnClickListener {
            writeStudentToFirebase()
            binding.txtquestion.text = ""

        }
    }

    // Function to get question from database
    private fun getQuestionsFromFirebase(){
        db.collection("Questions")
            .get()
            .addOnSuccessListener { result ->
                questionBank = mutableListOf()
                for(document in result) {
                    val question = document.toObject(Questions::class.java)
                    questionBank!!.add(question) // Necessary
                    Log.d(TAG,"$question")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "error", e)
            }
    }

    private fun writeStudentToFirebase() {
        val firstName = binding.txtFirstName
        val lastName = binding.txtLastName
        val prefName = binding.txtPrefName
        val answer = binding.txtAnswer

        Log.d(TAG, "Variables: $firstName $lastName $prefName $answer")

        // Convert data to hasmap for firebase purposes
        val student = hashMapOf(
            "firstname"  to firstName.text.toString(),
            "lastname"  to lastName.text.toString(),
            "prefname"  to prefName.text.toString(),
            "answer"    to answer.text.toString(),
            "class"     to className,
            "question"  to binding.txtQuestion.text.toString()
        )
        // Access Firebase database
        db.collection("students")
            .add(student)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written successfully with ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document", exception)
            }

        firstName.setText("")
        lastName.setText("")
        prefName.setText("")
        answer.setText("")
    }
}
