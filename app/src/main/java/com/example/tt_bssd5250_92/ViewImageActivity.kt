package com.example.tt_bssd5250_92

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.camera.core.Preview

class ViewImageActivity : AppCompatActivity() {

    private lateinit var imagePreview: ImageView

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fileUri = Uri.parse(intent.getStringExtra("filePath"))

        imagePreview = ImageView(this).apply {
            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                0.1f
            )
            setImageURI(fileUri)
        }

        val saveButton = Button(this).apply {
            text = "Save"
            setOnClickListener {
                MediaScannerConnection.scanFile(
                    applicationContext,
                    arrayOf(fileUri.toString()),
                    null,
                    null
                )
            }
            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                0.5f
            )
        }

        val closeButton = Button(this).apply {
            text = "Discard"
            setOnClickListener {
                finish()
            }
            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                0.5f
            )
        }

        val asciiButton = Button(this).apply {
            text = "Ascii"
            setOnClickListener {
                editBitmap(intent.getStringExtra("filePath"))
            }
            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                0.5f
            )
        }

        val containerButtons = LinearLayoutCompat(this).apply {
            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                0.9f
            )
            addView(saveButton)
            addView(closeButton)
            addView(asciiButton)
        }
        val container = LinearLayoutCompat(this).apply {
            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayoutCompat.VERTICAL
            addView(containerButtons)
            addView(imagePreview)
        }
        setContentView(container)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun editBitmap(filePath: String?) {
        val orig = BitmapFactory.decodeFile(filePath)
        val bmp = Bitmap.createScaledBitmap(orig, orig.width / 64, orig.height / 64, true)

        val w = bmp.width
        val h = bmp.height
        var outputString = ""
        for (y in 0 until h) {
            for (x in 0 until w) {
                var currColor: Int = (bmp.getColor(x, y).red() * 255).toInt()
                currColor += (bmp.getColor(x, y).blue() * 255).toInt()
                currColor += (bmp.getColor(x, y).green() * 255).toInt()
                currColor /= 3

                if (currColor < 255 / 4) {
                    outputString += "_"
                } else if (currColor < (255 / 4) * 2) {
                    outputString += "+"
                } else if (currColor < (255 / 4) * 3) {
                    outputString += "!"
                } else {
                    outputString += "@"
                }
            }
            outputString += "\n"
        }
        val tv = TextView(this).apply {
            text = outputString
            typeface = Typeface.MONOSPACE
        }

        AlertDialog.Builder(this).apply {
            setTitle("Ascii Output")
            setMessage(outputString)
            create()
            show()
        }

    }
}