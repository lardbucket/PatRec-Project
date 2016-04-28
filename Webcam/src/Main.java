import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class Main 
{
	public static void main(String[] args)
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		VideoCapture v = new VideoCapture();
		v.open(0);
		Mat m = new Mat();
		v.read(m);
		Imgcodecs.imwrite("C:/WebcamTest/img.jpg", m);
		
	}
	
}
