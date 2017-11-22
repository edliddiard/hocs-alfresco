package uk.gov.homeoffice.cts.transformers;

import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

import static org.junit.Assert.*;

/**
 * //Testing the tiff to PDF transformations
 * Created by chris on 17/10/2014.
 */
public class TiffToPdfTransformerTest {
    private File pdfFile;
    @Test
    public void testSampleTiffs() {
        File tiffFile = new File("src/test/resources/transformers/MCP.tif");
        pdfFile = new File("src/test/resources/transformers/MCP.pdf");
        TiffToPdfTransformer tiffToPdfTransformer = new TiffToPdfTransformer();
        FileOutputStream pdfOutput;
        try {
            System.out.println(pdfFile.getAbsolutePath());
            pdfOutput = new FileOutputStream(pdfFile);

            //Read the Tiff File
            RandomAccessFileOrArray myTiffFile=new RandomAccessFileOrArray(tiffFile.getAbsolutePath());
            tiffToPdfTransformer.createPDF(myTiffFile,pdfOutput);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception happened");
        }

        assertTrue("PDF file not created", pdfFile.exists());

    }
    @After
    public void deleteFiles(){
        if(pdfFile!=null && pdfFile.exists()){
            pdfFile.delete();
        }
    }

    @Test
    public void transformableTests(){
        TiffToPdfTransformer tiffToPdfTransformer = new TiffToPdfTransformer();
        assertTrue(tiffToPdfTransformer.isTransformable("image/tiff","application/pdf",null));

        assertFalse(tiffToPdfTransformer.isTransformable("image/gif", "application/pdf", null));
        assertFalse(tiffToPdfTransformer.isTransformable("image/tif", "application/xml", null));
        //the extension tif is mapped to image/tiff
        assertFalse(tiffToPdfTransformer.isTransformable("image/tif", "application/pdf", null));
    }
}
