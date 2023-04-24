package te.mini_project.skincancerdetection.report_gen

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.Environment
import com.google.android.material.internal.ViewUtils.getContentView
import te.mini_project.skincancerdetection.room.models.MoleScan
import java.io.File

object ReportGenerator {

    fun generate(context: Context, record:List<MoleScan>):File{
        val  document =  PdfDocument();

        val  pageInfo =  PdfDocument.PageInfo.Builder(100, 200, 1).create();

        val  page = document.startPage(pageInfo);

        val canvas  =page.canvas
//        canvas.drawText()
        //.... more content from mole scan

        document.finishPage(page);

        val pdfReport  =File(Environment.getExternalStorageDirectory(),"${System.currentTimeMillis()}-skin-cancer-report.pdf")
        document.writeTo(pdfReport.outputStream());

        document.close();

        return  pdfReport
    }
}