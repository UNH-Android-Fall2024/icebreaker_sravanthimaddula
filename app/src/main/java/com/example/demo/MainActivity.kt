package com.example.demo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.demo.databinding.ActivityMainBinding
import com.example.demo.questions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val className = "F24"
    private val db = Firebase.firestore
    private var TAG = "IcebreakerF24-TAG"
    private var questionBank: MutableList<questions>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getQuestionsFromFirebase()

        binding.btnSetRandomQuestion.setOnClickListener {
            val randomQuestion = questionBank!!.random().text
            binding.txtQuestion.text = randomQuestion
        }

        binding.btnSubmit.setOnClickListener {
            writeStudentToFirebase()
        }
    }

    private fun getQuestionsFromFirebase(){
        db.collection("questions")
            .get()
            .addOnSuccessListener { result ->
                questionBank = mutableListOf()
                for(document in result){
                    val question = document.toObject(questions::class.java)
                    questionBank!!.add(question)
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
        Log.d(TAG, "Question being written: ${binding.txtQuestion.text.toString()}")

        val student = hashMapOf(
            "firstname" to firstName.text.toString(),
            "lastname" to lastName.text.toString(),
            "prefname" to prefName.text.toString(),
            "answer" to answer.text.toString(),
            "class" to className,
            "question" to binding.txtQuestion.text.toString()
        )

        db.collection("students")
            .add(student)
            .addOnSuccessListener{documentReference ->
                Log.d(TAG,"Document Snapshot written successfully with ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding Document", exception)
            }
        firstName.setText("")
        lastName.setText("")
        prefName.setText("")
        answer.setText("")
        binding.txtQuestion.text = ""

    }


    /*
    setContentView(R.layout.activity_main)
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Header1)) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        insets
    }*/
}