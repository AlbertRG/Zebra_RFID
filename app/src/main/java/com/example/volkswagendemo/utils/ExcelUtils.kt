package com.example.volkswagendemo.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.example.volkswagendemo.data.TagData
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ExcelUtils @Inject constructor(private val context: Context) {

    fun writeExcelFile(tagsFlow: List<TagData>, workshop: String) {
        val timestamp = getActualDate()
        val fileName = "$workshop $timestamp.xls"
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("REPUVES")

        sheet.createRow(0).createCell(0).setCellValue("RFID ALTATEC")
        sheet.createRow(1).createCell(0).setCellValue(timestamp)

        val headerRow = sheet.createRow(2)
        headerRow.createCell(0).setCellValue("REPUVE")
        headerRow.createCell(1).setCellValue("VIN")

        tagsFlow.forEachIndexed { index, tag ->
            val row = sheet.createRow(index + 3)
            row.createCell(0).setCellValue(tag.repuve)
            row.createCell(1).setCellValue(tag.vin)
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.ms-excel")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/RFID/$workshop")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
            contentValues
        )

        uri?.let {
            runCatching {
                resolver.openOutputStream(it)?.use { fos ->
                    workbook.use { wb -> wb.write(fos) }
                }
                Log.d("writeExcelFile", "📁 File stored: $fileName")
            }.onFailure { e ->
                Log.e("writeExcelFile", "⚠️ Error saving the file: ${e.message}")
            }
        } ?: run {
            Log.e("writeExcelFile", "⚠️ Error getting URI for file")
        }

    }

    fun indexExcelFiles(workshop: String): List<String> {
        val folderPath = "Documents/RFID/$workshop%"
        val projection = arrayOf(
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_MODIFIED
        )
        val selection =
            "${MediaStore.Files.FileColumns.RELATIVE_PATH} LIKE ? AND ${MediaStore.Files.FileColumns.MIME_TYPE} = ?"
        val selectionArgs = arrayOf(folderPath, "application/vnd.ms-excel")
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"
        val excelFiles = mutableListOf<String>()

        context.contentResolver.query(
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
            projection, selection, selectionArgs, sortOrder
        )?.use { cursor ->
            val nameColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val fileName = cursor.getString(nameColumn)
                Log.d("indexExcelFiles", "📂 File found: $fileName")

                if (fileName.endsWith(".xls", ignoreCase = true)) {
                    excelFiles.add(fileName)
                }
            }
        }
        Log.d("indexExcelFiles", "📂 XLS files found: ${excelFiles.size}")
        return excelFiles.sorted()
    }

    fun readSpecificExcelFile(fileName: String): List<TagData> {
        return readExcelFile(fileName)
    }

    private fun readExcelFile(fileName: String): List<TagData> {
        val dataList = mutableListOf<TagData>()
        val projection = arrayOf(MediaStore.Files.FileColumns._ID)
        val selection = "${MediaStore.Files.FileColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        context.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection, selection, selectionArgs, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
                val fileId = cursor.getLong(idColumn)
                val fileUri =
                    ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), fileId)

                runCatching {
                    context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                        HSSFWorkbook(inputStream).use { workbook ->
                            val sheet = workbook.getSheetAt(0)

                            for (rowIndex in 3 until sheet.physicalNumberOfRows) {
                                val row = sheet.getRow(rowIndex)

                                val repuve = row?.getCell(0)?.toString()?.trim() ?: ""
                                val vin = row?.getCell(1)?.toString()?.trim() ?: ""

                                if (vin.isNotEmpty() && repuve.isNotEmpty()) {
                                    dataList.add(TagData(repuve, vin, ""))
                                }
                            }
                        }
                    }
                }.onFailure { e ->
                    Log.e("readExcelFile", "⚠️ Error reading file: ${e.message}")
                }
            }
        }
        return dataList
    }

    private fun getActualDate(): String =
        SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault()).format(Date())

}