import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class TestMain 
{
	public static final String SRC = "C:/WebcamTest/dog.jpg";
	public static final String OUTPUT_HSV = "C:/WebcamTest/dogHSV.jpg";
	public static final String OUTPUT_BIN = "C:/WebcamTest/dogBIN.jpg";
	public static final String OUTPUT_HSV_BIN = "C:/WebcamTest/dogHSVBIN.jpg";
	public static final String OUTPUT_HSV_BIN_EDGE = "C:/WebcamTest/dogHSVBINedge.jpg";
	public static final String OUTPUT_BIN_EDGE = "C:/WebcamTest/dogBINedge.jpg";
	public static void main(String[] args)
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat m = Imgcodecs.imread(SRC);
		Mat grayMat = new Mat(m.height(),m.width(), CvType.CV_8UC1);
		Imgproc.cvtColor(m, grayMat, Imgproc.COLOR_RGB2GRAY);
        Mat binMat = new Mat(m.height(),m.width(), CvType.CV_8UC1);
		Imgproc.threshold(grayMat, binMat, 100, 255, Imgproc.THRESH_BINARY);
		Imgcodecs.imwrite(OUTPUT_BIN, binMat);
		Mat mHSV = getHSV(m);
		Imgcodecs.imwrite(OUTPUT_HSV, mHSV);
		Mat grayMatHSV = new Mat(m.height(),m.width(), CvType.CV_8UC1);
		Imgproc.cvtColor(m, grayMatHSV, Imgproc.COLOR_RGB2GRAY);
        Mat binMatHSV = new Mat(m.height(),m.width(), CvType.CV_8UC1);
		Imgproc.threshold(grayMatHSV, binMatHSV, 100, 255, Imgproc.THRESH_BINARY);
		Imgcodecs.imwrite(OUTPUT_HSV_BIN, binMatHSV);
		saveEdges(m, OUTPUT_BIN_EDGE);
		saveEdges(mHSV, OUTPUT_HSV_BIN_EDGE);
		
		System.out.println("Done!");
	}
	
	public static void saveEdges(Mat m, String fileName)
	{
		Mat edge = new Mat();
		edge = autoCanny(m);
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		ArrayList<Mat> roi = new ArrayList<Mat>();
		Mat display = edge.clone();
		Imgproc.findContours(edge, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		for (int i = 0; i < contours.size(); i++)
		{
			MatOfPoint p = contours.get(i);
			Rect r = Imgproc.boundingRect(p);
			if (r.area() > 300)
			{
				roi.add(new Mat(m, r));
				Imgproc.rectangle(display, r.tl(), r.br(), new Scalar(0, 0, 255));
			}
		}
		Imgcodecs.imwrite(fileName, display);
	}
	
	public static Mat getHSV(Mat m)
	{
		Mat r = m.clone();
		Imgproc.cvtColor(r, r, Imgproc.COLOR_RGB2HSV_FULL);
		return r;
		
	}
	 public static Mat autoCanny(Mat image)
	 {
		 MatOfDouble mu = new MatOfDouble();
		 MatOfDouble stdev = new MatOfDouble();
		 Core.meanStdDev(image, mu, stdev);
		 double sigma = 0.33;
		 //double sigma = stdev.get(0, 0)[0];
		 double v = mu.get(0, 0)[0];
		 double lower = (1.0 - sigma) * v;
		 double upper = (1.0 + sigma) * v;
		 Mat r = new Mat();
		 if (lower < 0)
			 lower = 0;
		 if (upper > 255)
			 upper = 255;
		 Imgproc.Canny(image, r, lower, upper, 3, false);
		 return r;
	 }
	
}
