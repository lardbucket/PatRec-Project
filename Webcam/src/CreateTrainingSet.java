import java.io.*;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
public class CreateTrainingSet 
{
	public static final String SRC_OBJECTS_FOLDER = "C:/WebcamTest/srcobjects/";
	public static final String SRC_NON_OBJECTS_FOLDER = "C:/WebcamTest/srcnonobjects/";
	public static final String OUT_OBJECTS_FOLDER = "C:/WebcamTest/outobjects/";
	public static final String OUT_NON_OBJECTS_FOLDER = "C:/WebcamTest/outnonobjects/";
	public static void main(String[] args)
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		File[] objects = new File(SRC_OBJECTS_FOLDER).listFiles();
		File[] nonObjects = new File(SRC_NON_OBJECTS_FOLDER).listFiles();
		for (int i = 0; i < objects.length; i++)
		{
			File f = objects[i];
			Mat m = Imgcodecs.imread(f.getPath());
			Mat hsvMat = getHSV(m);
			Mat smallMat = new Mat();
			Imgproc.resize(hsvMat, smallMat, new Size(10, 10));
			Imgcodecs.imwrite(OUT_OBJECTS_FOLDER + i + ".jpg", smallMat);
		}
		for (int i = 0; i < nonObjects.length; i++)
		{
			File f = nonObjects[i];
			Mat m = Imgcodecs.imread(f.getPath());
			Mat hsvMat = getHSV(m);
			Mat smallMat = new Mat();
			Imgproc.resize(hsvMat, smallMat, new Size(10, 10));
			Imgcodecs.imwrite(OUT_NON_OBJECTS_FOLDER + i + ".jpg", smallMat);
		}
		System.out.println("Done!");
	}
	
	public static Mat getHSV(Mat m)
	{
		Mat r = m.clone();
		Imgproc.cvtColor(r, r, Imgproc.COLOR_RGB2HSV);
		return r;
	}
	
	
}
