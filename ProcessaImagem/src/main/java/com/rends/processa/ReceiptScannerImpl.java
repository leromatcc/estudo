package com.rends.processa;

import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;
import static org.bytedeco.javacpp.opencv_core.BORDER_DEFAULT;
import static org.bytedeco.javacpp.opencv_core.CV_32FC1;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvCloneImage;
import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvCreateMat;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_core.cvRect;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvSize;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_INTER_LINEAR;
import static org.bytedeco.javacpp.opencv_imgproc.CV_MEDIAN;
import static org.bytedeco.javacpp.opencv_imgproc.CV_POLY_APPROX_DP;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_LIST;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_OTSU;
import static org.bytedeco.javacpp.opencv_imgproc.GaussianBlur;
import static org.bytedeco.javacpp.opencv_imgproc.cvApproxPoly;
import static org.bytedeco.javacpp.opencv_imgproc.cvBoundingRect;
import static org.bytedeco.javacpp.opencv_imgproc.cvCanny;
import static org.bytedeco.javacpp.opencv_imgproc.cvContourPerimeter;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvDilate;
import static org.bytedeco.javacpp.opencv_imgproc.cvDrawCircle;
import static org.bytedeco.javacpp.opencv_imgproc.cvErode;
import static org.bytedeco.javacpp.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.opencv_imgproc.cvGetPerspectiveTransform;
import static org.bytedeco.javacpp.opencv_imgproc.cvResize;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;
import static org.bytedeco.javacpp.opencv_imgproc.cvWarpPerspective;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.lept.PIX;
import org.bytedeco.javacpp.opencv_core.CvContour;
import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.CvSize;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.tesseract.TessBaseAPI;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public final class ReceiptScannerImpl implements ReceiptScanner{
/*
	public String getTextFromReceiptImage(final String receiptImageFilePath){
		final File receiptImageFile = new File(receiptImageFilePath);
		final String receiptImagePathFile = receiptImageFile.getAbsolutePath();
		log.info(receiptImagePathFile);
		IplImage receiptImage = cvLoadImage(receiptImagePathFile);
		
		return "ainda não concluido"; //TODO concluir
	}
	*/

	String timeStampDefinido = null;
	
	public ReceiptScannerImpl(){
		this.timeStampDefinido = getCurrentTimeStamp();
	}

	public String getTextFromReceiptImage(final String receiptImageFilePath){
		final String cleanedReceiptPathFile = preprocessaImagem(receiptImageFilePath);
		log.info("cleanedReceiptPathFile: "+cleanedReceiptPathFile);
		return getStringsFromImage(cleanedReceiptPathFile);
	}

	private String preprocessaImagem(final String receiptImageFilePath) {
		final File receiptImageFile = new File(receiptImageFilePath);
		final String receiptImagePathFile = receiptImageFile.getAbsolutePath();
		log.info(receiptImagePathFile);
		IplImage receiptImage = cvLoadImage(receiptImagePathFile);
		salvaImagemPasso(receiptImage, "a0-original.jpg");
		IplImage cannyEdgeImage = applyCannySquareEdgeDetectionOnImage(receiptImage, 30);
		// --imagens impressas
		CvSeq largestSquare = findLargestSquareOnCannyDetectedImage(cannyEdgeImage);
		receiptImage = applyPerspectiveTransformThresholdOnOriginalImage(receiptImage, largestSquare, 30);
		log.info(receiptImagePathFile + "04");		
		// receiptImage = cleanImageGaussianBlurForOCR(receiptImage );
		receiptImage = cleanImageSmoothingForOCR(receiptImage);
		log.info(receiptImagePathFile + "05");		
		//
		
		final String cleanedReceiptPathFile = salvaImagemPasso(receiptImage, "a1-cleanedFile.jpg");
		//
		cvReleaseImage(cannyEdgeImage);
		cannyEdgeImage = null;
		cvReleaseImage(receiptImage);
		receiptImage = null;
		//
		
		/*
		 * a versão original do programa sobre-escrevia o arquivo original, porém isso dificulta o debug
		final File cleanedReceiptFile = new File(receiptImageFilePath);
		final String cleanedReceiptPathFile = cleanedReceiptFile.getAbsolutePath();
		cvSaveImage( cleanedReceiptPathFile, receiptImage );
		log.info(cleanedReceiptPathFile);
		*/
				
		return cleanedReceiptPathFile;
	}
		
	/*
	1) reduce the size of the image, to easily detect the borders 
	2) convert the image to gray, 
	3) apply a gaussian blur over the image, 
	4) optionally, clean the image for better detection, 
	5) apply the canny detection over the image. 
	*/
	
	/*
	 * Resizes an image given a percent
	 */
	private IplImage downScaleImage(IplImage srcImage, int percent){
		//
		log.info("srcImage - height - " + srcImage.height() + ", width - " +  srcImage.width() );
		//
		CvSize larguraAltura = cvSize( ((srcImage.width()  * percent) / 100), ((srcImage.height() * percent) / 100) );
		IplImage destImage = cvCreateImage(larguraAltura, srcImage.depth(), srcImage.nChannels() );
		//
		cvResize(srcImage, destImage);
		log.info("destImage - height - "+ destImage.height() +", width - " +destImage.width());
		//
		salvaImagemPasso(destImage, "b1-downScaleImage.jpg");
		
		return destImage;
	}
	
	/*
	 * Detect the edges of an image using the cannv edge detect method given a 
	 * percent in order to reduce the size o f the image and be abcle to process
	 * it better 
	 */
	private IplImage applyCannySquareEdgeDetectionOnImage(IplImage srcImage, int percent){
		IplImage destImage = downScaleImage(srcImage, percent);
		IplImage grayImage = cvCreateImage( cvGetSize(destImage), IPL_DEPTH_8U, 1);
		//convert to gray
		cvCvtColor(destImage, grayImage, CV_BGR2GRAY);
		salvaImagemPasso(grayImage, "c1-grayImage.jpeg");
		
		OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
		Frame grayImageFrame = converterToMat.convert(grayImage);
		Mat grayImageMat = converterToMat.convert(grayImageFrame);
		//apply gausian blur
		GaussianBlur(grayImageMat, grayImageMat, new Size(5, 5), 0.0, 0.0, BORDER_DEFAULT);
		// grayImageFrame = converterToMat.convert(grayImageMat);
		destImage = converterToMat.convertToIplImage(grayImageFrame);
		salvaImagemPasso(destImage, "c2-GaussianBlur.jpeg");
		// clean it for better detection..
		cvErode(destImage, destImage);
		salvaImagemPasso(destImage, "c3-cvErode.jpeg");
		cvDilate(destImage, destImage);
		salvaImagemPasso(destImage, "c4-cvDilate.jpeg");

		// apply the canny edge detection method... 
		cvCanny(destImage, destImage, 75.0, 200.0);
		
		salvaImagemPasso(destImage, "c5-applyCanny.jpeg");
		
		return destImage;
	}

	/*
	 * Once applied canny edge to the image, we can find the largest square
	 * using the find contours (square) method and asking for the largest one
	 * that will be the receipt on the image, hopefully.
	 */
	private CvSeq findLargestSquareOnCannyDetectedImage( IplImage cannyEdgeDetectedImage ) {
		IplImage foundedContoursImage = cvCloneImage(cannyEdgeDetectedImage);
		CvMemStorage memory = CvMemStorage.create();
		CvSeq contours = new CvSeq();
		cvFindContours(	foundedContoursImage, 
						memory, 
						contours,
						Loader.sizeof(CvContour.class),
						CV_RETR_LIST,
						CV_CHAIN_APPROX_SIMPLE,
						cvPoint(0,0)
						);
		int maxWidth = 0;
		int maxHeight = 0;
		CvRect contour = null;
		CvSeq seqFounded = null;
		CvSeq nextSeq = new CvSeq();
		for ( nextSeq = contours; nextSeq!=null; nextSeq = nextSeq.h_next()){
			contour = cvBoundingRect(nextSeq, 0);
			if ((contour.width() >= maxWidth ) 
				&& ( contour.height() >= maxHeight ) ){
				maxWidth = contour.width();
				maxHeight = contour.height();
				seqFounded = nextSeq;
			}
		}
		CvSeq result = cvApproxPoly( seqFounded,
									 Loader.sizeof( CvContour.class),
									 memory,
									 CV_POLY_APPROX_DP,
									 cvContourPerimeter(seqFounded) * 0.02,
									 0 );
		//
		for ( int i = 0; i < result.total(); i++ ){
			CvPoint v = new CvPoint( cvGetSeqElem(result, i) );
			cvDrawCircle( foundedContoursImage, v, 5, CvScalar.BLUE, 20, 8, 0);
			log.info("found point( "+ v.x() +" , "+ v.y() +" )");
		}
		salvaImagemPasso(foundedContoursImage, "d6-foundedContoursImage.jpg");

		return result;
	}
	
	/*
	 * Finally we apply a transformation on the original image to obtain a 
	 * top-down image of the receipt from the same original image using the 
	 * points detected early. 
	 */
	private IplImage applyPerspectiveTransformThresholdOnOriginalImage( IplImage srcImage, 
																		CvSeq contour, 
																		int percent ){
		//
		IplImage warpImage = cvCloneImage(srcImage);
		// first, given the percentage, adjust to the original image
		for (int i=0; i < contour.total(); i++){
			CvPoint point = new CvPoint( cvGetSeqElem(contour, i));
			point.x((int)(point.x() * 100) / percent);
			point.y((int)(point.y() * 100) / percent);
		}
		// get each corner point of the image... 
		CvPoint topRightPoint = new CvPoint(cvGetSeqElem(contour, 0));
		CvPoint topLeftPoint  = new CvPoint(cvGetSeqElem(contour, 1));
		CvPoint bottomLeftPoint = new CvPoint(cvGetSeqElem(contour, 2));
		CvPoint bottomRightPoint = new CvPoint(cvGetSeqElem(contour, 3));
		//
		// set the new image width
		// identify which is larger: the top or bottom distance 
		int resultWidth = 0;
		int topWidth    = (int) ( topRightPoint.x() - topLeftPoint.x() );
		int bottomWidth = (int) ( bottomRightPoint.x() - bottomLeftPoint.x() );
		if ( bottomWidth > topWidth ){
			resultWidth = bottomWidth; 
		} else {
			resultWidth = topWidth; 
		}
		// set the new image height
		// identify which is larger: the right or left distance 
		int resultHeight = 0;
		int topHeight    = (int) (bottomLeftPoint.y() - topLeftPoint.y());
		int bottomHeight = (int) (bottomRightPoint.y() - topRightPoint.y());
		if ( bottomHeight > topHeight){
			resultHeight = bottomHeight;
		} else {
			resultHeight = topHeight;
		}
		//
		//
		float[] sourcePoints = { 
								topLeftPoint.x(),     topLeftPoint.y(),
								topRightPoint.x(),    topRightPoint.y(),
								bottomLeftPoint.x(),  bottomLeftPoint.y(),
								bottomRightPoint.x(), bottomRightPoint.y() 
								};
		float[] destinationPoints = {
									0, 0,
									resultWidth, 0,
									0, resultHeight,
									resultWidth, resultHeight
									};
		CvMat homography = cvCreateMat(3, 3, CV_32FC1);
		cvGetPerspectiveTransform(sourcePoints, destinationPoints, homography);
		log.info("Homography" + homography.toString());
		//
		IplImage destImage = cvCloneImage(warpImage);
		cvWarpPerspective(warpImage, destImage, homography, CV_INTER_LINEAR, CvScalar.ZERO);
		return cropImage(destImage, 0, 0, resultWidth, resultHeight);
	}
	
	/*
	 * Crops an square from an image to the new width and height from the 0,0 position
	 */ 
	private IplImage cropImage( IplImage srcImage, 
								int fromX, 
								int fromY, 
								int toWidth, 
								int toHeight){
		//
		cvSetImageROI(srcImage, cvRect(fromX,fromY,toWidth,toHeight));
		IplImage destImage = cvCloneImage(srcImage);
		cvCopy(srcImage, destImage);
		return destImage;
		//
	}

	/*
	 * Cleans an image of noise converting to grey, smoothing and applying Otsu
	 * threshold to the image and leaving the image with white background and
	 * black foreground (letters)
	 */
	private IplImage cleanImageSmoothingForOCR(IplImage srcImage){
		//
		IplImage destImage = cvCreateImage( cvGetSize(srcImage), IPL_DEPTH_8U, 1);
		cvCvtColor(srcImage, destImage, CV_BGR2GRAY);
		salvaImagemPasso(destImage, "e1-cleanImage_CV_BGR2GRAY.jpg");
		cvSmooth(destImage, destImage, CV_MEDIAN, 3, 0, 0, 0);
		salvaImagemPasso(destImage, "e2-cleanImage_smooth.jpg");		
		cvThreshold(destImage, destImage, 0, 255, CV_THRESH_OTSU);
		salvaImagemPasso(destImage, "e3-cleanImage_otsu.jpg");
		return destImage;
		//
	}
	
	/*
	 * Call Tesseract with the receipt image and return the text founded
	 */
	private String getStringsFromImage(final String pathToReceiptImageFile){
		try {
			log.info("getStringsFromImage-01");
			final URL tessDataResource = getClass().getResource("/");
			log.info("getStringsFromImage-02");
			final File tessFolder = new File(tessDataResource.toURI());
			log.info("getStringsFromImage-03");
			//original final String tessFolderPath = tessFolder.getAbsolutePath();
			final String tessFolderPath = "/";
			log.info("getStringsFromImage-04");
			log.info("tessFolderPath:" + tessFolderPath);
			log.info("getStringsFromImage-05");
			//
			BytePointer outText;
			log.info("getStringsFromImage-06");
			TessBaseAPI api = new TessBaseAPI();
			log.info("getStringsFromImage-07");
			api.SetVariable("tessedit_char_whitelist", 
					"0123456798,/ABCDEFGHJKLMNPQRSTUVWXY");
			log.info("getStringsFromImage-08");
			// Initialize tesseract-ocr with Spanish...
			// TODO alterar para portugues/ingles
			if ( api.Init(tessFolderPath, "por") != 0 ){
				log.info("getStringsFromImage-09");
				log.error("Could not initialize Tesseract.");
				return "";
			}
			//Open input image with leptonica library
			log.info("getStringsFromImage-10");
			PIX image = pixRead(pathToReceiptImageFile);
			
			log.info("getStringsFromImage-11");
			api.SetImage(image);
			// Get OCR result
			log.info("getStringsFromImage-12");
			outText = api.GetUTF8Text();
			log.info("getStringsFromImage-13");
			String string = outText.getString();
			// Destroy used object and release memory
			log.info("getStringsFromImage-14");
			api.End();
			// api.close();
			log.info("getStringsFromImage-15");
			outText.deallocate();
			log.info("getStringsFromImage-16");
			pixDestroy(image);
			//
			log.info("getStringsFromImage-17");
			return string;
			//
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
		//
	}

	

	private String salvaImagemPasso(IplImage destImage, String nameImage) {
		//if debug
		File f = new File(System.getProperty("user.dir") + File.separator + "processamentos" + File.separator + getTimeStampDefinido() + "-" + nameImage);
		log.info(nameImage +": "+f.getAbsolutePath());
		cvSaveImage(f.getAbsolutePath(), destImage);
		//endif debug
		return f.getAbsolutePath();
	}
	
	private String getTimeStampDefinido(){
		return timeStampDefinido;
	}
	
	public String getCurrentTimeStamp() {
		//
		log.info("TIMESTAMP - calculando");
		//
		// SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd
		// HH:mm:ss.SSS"); // exibe mili-segundos
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
		Date now = new Date();
		String strDate = sdfDate.format(now);
		log.info("TIMESTAMP: " + strDate);
		return strDate;
	}
	
	
}
