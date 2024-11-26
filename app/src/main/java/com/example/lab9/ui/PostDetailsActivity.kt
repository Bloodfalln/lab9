package com.example.lab9.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lab9.databinding.ActivityPostDetailsBinding
import com.example.lab9.network.RetrofitClient
import com.example.lab9.models.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Отримуємо ID поста з Intent
        val postId = intent.getIntExtra("POST_ID", -1)

        // Якщо ID поста правильний, виконуємо запит
        if (postId != -1) {
            fetchPostDetails(postId)
        } else {
            Toast.makeText(this, "Invalid post ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchPostDetails(postId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Викликаємо API для отримання даних про пост за ID
                val response = RetrofitClient.instance.getPost(postId)

                // Перевірка на успішність запиту
                if (response.isSuccessful) {
                    val post = response.body()

                    // Якщо пост знайдений, відображаємо його на екрані
                    withContext(Dispatchers.Main) {
                        if (post != null) {
                            binding.tvPostTitle.text = post.title
                            binding.tvPostBody.text = post.body
                        } else {
                            Toast.makeText(this@PostDetailsActivity, "Post not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@PostDetailsActivity, "Failed to load post", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Виведення помилки, якщо щось пішло не так
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PostDetailsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
