package com.example.lab9.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab9.databinding.ActivityMainBinding
import com.example.lab9.models.Post
import com.example.lab9.network.RetrofitClient
import com.example.lab9.utils.NetworkUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Перевірка на з'єднання перед завантаженням постів
        if (NetworkUtil.isNetworkAvailable(this)) {
            fetchPosts()
        } else {
            // Якщо немає інтернету, показуємо повідомлення
            showNoInternetMessage()
        }
    }

    private fun fetchPosts() {
        // Показуємо прогрес-бар під час завантаження
        binding.progressBar.visibility = View.VISIBLE

        // Виконуємо асинхронний запит до API в корутині
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Отримуємо всі пости (без ліміту)
                val response = RetrofitClient.instance.getPosts()  // Виклик без ліміту

                // Перевірка на успішність запиту
                if (response.isSuccessful) {
                    val posts = response.body() ?: emptyList()

                    // Повертаємося на головний потік для оновлення UI
                    withContext(Dispatchers.Main) {
                        // Сховати прогрес-бар
                        binding.progressBar.visibility = View.GONE

                        // Налаштовуємо RecyclerView
                        binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                        binding.recyclerView.adapter = PostAdapter(posts) { post ->
                            // Обробка кліку на пост
                            val intent = Intent(this@MainActivity, PostDetailsActivity::class.java)
                            intent.putExtra("POST_ID", post.id)
                            startActivity(intent)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        // Сховати прогрес-бар
                        binding.progressBar.visibility = View.GONE

                        binding.recyclerView.adapter = PostAdapter(emptyList()) { }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Сховати прогрес-бар
                    binding.progressBar.visibility = View.GONE

                    binding.recyclerView.adapter = PostAdapter(emptyList()) { }
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showNoInternetMessage() {
        // Показуємо повідомлення про відсутність інтернету
        Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()

        // Показуємо прогрес-бар, навіть якщо немає інтернету
        binding.progressBar.visibility = View.VISIBLE

        // Ви можете додати додаткове повідомлення або інтерфейс, щоб позначити, що дані не можуть бути завантажені.
        binding.recyclerView.adapter = PostAdapter(emptyList()) { }
    }
}
