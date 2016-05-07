import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class Main 
{
	public static void main(String[] args)
	{
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//		VideoCapture v = new VideoCapture();
//		v.open(0);
//		Mat m = new Mat();
//		v.read(m);
//		Imgcodecs.imwrite("./img.jpg", m);
//		System.out.println("hello");
		Mat img = Imgcodecs.imread("/Users/markronquillo/Documents/workspace/PatRec-Project/Webcam/people.jpg");
		
		double[] f = FeatureVector.getFeatureVector(img);
		
		for (int i=0; i < 300 ; i++) {
			System.out.println(f[i]);
		}
		
	}
	
}
