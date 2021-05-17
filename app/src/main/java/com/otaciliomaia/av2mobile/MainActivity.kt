package com.otaciliomaia.av2mobile

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), FileListAdapter.OnDeleteClickListener {
    var fileList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val initIntFiles: Array<String> = this.fileList()
        fileList.addAll(initIntFiles)
        val recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView

        val internalRadio = findViewById<RadioButton>(R.id.internalButton)
        internalRadio.setOnClickListener {
            fileList.clear()
            val intFiles: Array<String> = this.fileList()
            fileList.addAll(intFiles)
            recyclerView.adapter?.notifyDataSetChanged()
        }
        val externalRadio = findViewById<RadioButton>(R.id.externalButton)
        externalRadio.setOnClickListener {
            val extFiles: Array<out File>? = this.getExternalFilesDir(null)?.listFiles()
            val extFilesList: List<String>? = extFiles?.map { it.name }
            fileList.clear()
            if (extFilesList != null) {
                fileList.addAll(extFilesList.toTypedArray())
            }
            recyclerView.adapter?.notifyDataSetChanged()
        }

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val fileNameInput = findViewById<TextView>(R.id.fileNameInput)
            val fileName = fileNameInput.text.toString()
            val isInternalMemory = internalRadio.isChecked
            if (fileName.isNotEmpty()) {
                val fileContentInput = findViewById<TextView>(R.id.fileContentInput)
                val fileContent = fileContentInput.text.toString()
                val jetpackCheck = findViewById<CheckBox>(R.id.jetpackCheck)
                if (jetpackCheck.isChecked) {
                    val fileDir =
                        if (isInternalMemory) this.filesDir else this.getExternalFilesDir(null)
                    val masterKeyAlias = MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

                    val file = File(fileDir, fileName)
                    if (file.exists()) {
                        file.delete()
                    }
                    val encryptedFile = EncryptedFile.Builder(
                        this,
                        file,
                        masterKeyAlias,
                        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
                    ).build()
                    encryptedFile.openFileOutput().use { writer -> writer.write(fileContent.toByteArray()) }
                    Toast.makeText(
                        this,
                        "Salvo utilizando jetpack",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (isInternalMemory) {
                        this.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                            it.write(fileContent.toByteArray())
                        }
                        Toast.makeText(
                            this,
                            "Arquivo salvo com sucesso na memória interna",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val extFile = File(this.getExternalFilesDir(null), fileName)
                        val outputStream = FileOutputStream(extFile)
                        outputStream.use { stream ->
                            stream.write(fileContent.toByteArray())
                        }
                        Toast.makeText(
                            this,
                            "Arquivo salvo com sucesso na memória externa",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                fileList.add(fileName)
                recyclerView.adapter?.notifyItemInserted(fileList.size)
            }
        }

        recyclerView.adapter = FileListAdapter(fileList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }

    override fun onDeleteClick(position: Int) {
        val internalRadio = findViewById<RadioButton>(R.id.internalButton)
        val fileName = fileList[position]
        if (internalRadio.isChecked) {
            this.deleteFile(fileName)
        } else {
            val extFile = File(this.getExternalFilesDir(null), fileName)
            extFile.delete()
        }
        fileList.removeAt(position)
        val recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        recyclerView.adapter?.notifyItemRemoved(position)
    }
}