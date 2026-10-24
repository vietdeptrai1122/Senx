package com.freddy.sensx

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuRemoteProcess
import android.content.pm.PackageManager

class MainActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 96, 48, 48)
        }

        val tvInfo = TextView(this).apply {
            text = "Model: ${android.os.Build.MODEL}\n" +
                   "Brand: ${android.os.Build.BRAND}\n" +
                   "SDK: ${android.os.Build.VERSION.SDK_INT}"
            textSize = 15f
        }

        val btnPerm = Button(this).apply {
            text = "Cấp quyền Shizuku"
            setOnClickListener {
                try {
                    if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                        Shizuku.requestPermission(1001)
                    } else {
                        Toast.makeText(this@MainActivity, "Đã có quyền", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Throwable) {
                    Toast.makeText(this@MainActivity, "Shizuku chưa chạy", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val btnTest = Button(this).apply {
            text = "Test Shizuku"
            setOnClickListener {
                val r = exec("id")
                tvResult.text = "Test:\n${r ?: "FAIL"}"
            }
        }

        val btnApply = Button(this).apply {
            text = "Áp dụng pointer_speed=6"
            setOnClickListener {
                val r = exec("settings put system pointer_speed 6")
                tvResult.text = "Apply:\n${if (r == null) "FAIL" else "OK"}"
            }
        }

        tvResult = TextView(this).apply {
            textSize = 14f
            setPadding(0, 32, 0, 0)
        }

        root.addView(tvInfo)
        root.addView(btnPerm)
        root.addView(btnTest)
        root.addView(btnApply)
        root.addView(tvResult)

        setContentView(root)
    }

    private fun exec(cmd: String): String? {
        return try {
            val p: ShizukuRemoteProcess = Shizuku.newProcess(
                arrayOf("sh", "-c", cmd), null, null
            )
            val out = p.inputStream.bufferedReader().readText()
            p.waitFor()
            out
        } catch (e: Throwable) {
            null
        }
    }
}
