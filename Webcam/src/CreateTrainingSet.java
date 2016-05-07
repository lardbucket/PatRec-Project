import java.io.*;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
public class CreateTrainingSet 
{
	public static final String SRC_OBJECTS_FOLDER = "C:/WebcamTest/srcobjects/";
	public static final String SRC_NON_OBJECTS_FOLDER = "C:/WebcamTest/srcnonobjects/";
	public static final String OUT_OBJECTS_FOLDER = "C:/WebcamTest/objects/";
	public static final String OUT_NON_OBJECTS_FOLDER = "C:/WebcamTest/nonobjects/";
	public static void main(String[] args)
	{
		File[] objects = new File(SRC_OBJECTS_FOLDER).listFiles();
		File[] nonObjects = new File(SRC_NON_OBJECTS_FOLDER).listFiles();
		for (int i = 0; i < objects.length; i++)
		{
			File f = objects[i];
			Mat m = Imgcodecs.imread(f.getAbsolutePath());
			Mat hsvMat = getHSV(m);
			Mat smallMat = new Mat();
			Imgproc.resize(hsvMat, smallMat, new Size(10, 10));
			Imgcodecs.imwrite(OUT_OBJECTS_FOLDER + i + ".jpg", smallMat);
		}
		for (int i = 0; i < nonObjects.length; i++)
		{
			File f = nonObjects[i];
			Mat m = Imgcodecs.imread(f.getAbsolutePath());
			Mat hsvMat = getHSV(m);
			Mat smallMat = new Mat();
			Imgproc.resize(hsvMat, smallMat, new Size(10, 10));
			Imgcodecs.imwrite(OUT_NON_OBJECTS_FOLDER + i + ".jpg", smallMat);
		}
	}
	
	public static Mat getHSV(Mat m)
	{
		Mat r = m.clone();
		Imgproc.cvtColor(r, r, Imgproc.COLOR_RGB2HSV_FULL);
		return r;
	}
	
	public String fixPath(String oldPath)
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
