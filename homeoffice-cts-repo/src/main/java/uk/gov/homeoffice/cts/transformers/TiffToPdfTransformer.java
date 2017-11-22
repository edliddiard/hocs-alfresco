package uk.gov.homeoffice.cts.transformers;

//This object will hold our Tiff File
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

//Read Tiff File, Get number of Pages
//We need the library below to write the final
//PDF file which has our image converted to PDF
//The image class to extract separate images from Tiff image
//PdfWriter object to write the PDF document
//Document object to add logical image files to PDF

/**
 * Transforms tiff images into PDF. Does everything as A4 portrait at the moment.
 * Based on the IText sample TiffToPDF class.
 * Created by chris on 16/10/2014.
 */
public class TiffToPdfTransformer extends AbstractContentTransformer2 {
    private static final String MIMETYPE_IMAGE_TIFF = "image/tiff";
    private static final Log logger = LogFactory.getLog(TiffToPdfTransformer.class);

    @Override
    public boolean isTransformable(String sourceMimetype, String targetMimetype,
                                   TransformationOptions options) {
        return (MIMETYPE_IMAGE_TIFF.equals(sourceMimetype) && MimetypeMap.MIMETYPE_PDF.equals(targetMimetype));
    }

    @Override
    protected void transformInternal(ContentReader reader, ContentWriter writer, TransformationOptions options) throws Exception {
        File tempTiffFile = TempFileProvider.createTempFile(reader.getContentInputStream(), "tiff-to-pdf-"+UUID.randomUUID(),"tif");

        //Read the Tiff File
        RandomAccessFileOrArray myTiffFile=new RandomAccessFileOrArray(tempTiffFile.getAbsolutePath());

        createPDF(myTiffFile,writer.getContentOutputStream());

    }

    public void createPDF(RandomAccessFileOrArray myTiffFile, OutputStream fos) throws DocumentException {
        //this creates A4 as standard
        Document tiffToPDF = new Document();
        try {
            //Find number of images in Tiff file
            int numberOfPages = TiffImage.getNumberOfPages(myTiffFile);
            logger.debug("Tiff has " + numberOfPages + " pages");


            //set margins to 0 so the tiff fits on the page
            tiffToPDF.setMargins(0, 0, 0, 0);

            //get the sizes of the PDF
            float pdfWidth = tiffToPDF.getPageSize().getWidth();
            float pdfHeight = tiffToPDF.getPageSize().getHeight();

            PdfWriter.getInstance(tiffToPDF, fos);

            tiffToPDF.open();
            //Run a for loop to extract images from Tiff file
            //into a Image object and add to PDF recursively
            for (int i = 1; i <= numberOfPages; i++) {
                Image tempImage = TiffImage.getTiffImage(myTiffFile, i);
                tempImage.scaleToFit(pdfWidth, pdfHeight);
                tiffToPDF.add(tempImage);
            }
        }finally{
            //tidy up files
            if(myTiffFile!=null) {
                try {
                    myTiffFile.close();
                } catch (IOException e) {
                    logger.error("Could not close tiff file");
                }
            }
            tiffToPDF.close();
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    logger.error("Could not close PDF file");
                }
            }

        }
    }
}
