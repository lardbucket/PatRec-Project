import java.io.File;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
//class to test new methods 
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
		/** save images, color conversion
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
		*/
		/**
		Mat m = Imgcodecs.imread(SRC);
		Mat HSVMat = getHSV(m);
		
		Mat smallMat = new Mat();
		Imgproc.resize(HSVMat, smallMat, new Size(10, 10));
		int[] topology = {300, 150, 100, 50, 1};
		Brain b = new Brain(topology, false);
		double[] fV = createFeatureVector(smallMat);
		for (int i = 0; i < fV.length; i++)
			System.out.println(i + ": " + fV[i]);
		b.feedForward(fV);
		double[] output = b.getOutput();
		
		for (int i = 0; i < output.length; i++)
			System.out.println(output[i]);
		*/
		File f = new File("C:/WebcamTest/srcobjects/img0.jpg");
		System.out.println(fixPath(f.getPath()));
		System.out.println("Done!");
	}
	
	public static double[] createFeatureVector(Mat m)
	{
		double[] r = new double[300];
		int size = 0;
		for (int i = 0; i < m.rows(); i++)
		{
			for (int j = 0; j < m.cols(); j++)
			{
				double[] temp = m.get(i, j);
				r[size] = temp[0];
				r[size + 1] = temp[1];
				r[size + 2] = temp[2];
				size += 3;
			}
		}
		return r;
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
	 
	 public static String fixPath(String oldPath)
		{
			String r = "";
			String[] parts = oldPath.split("\\");
			for (int i = 0; i < parts.length; i++)
			{
				if (i < parts.length - 1)
					r += parts[i] + "/";
				else
					r += parts[i];
			}
			return r;
			
		}
	
}
