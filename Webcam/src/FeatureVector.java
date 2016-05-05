import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;



public class FeatureVector {
	public static final int HUE_MAX = 180;
	public static final int SAT_MAX = 255;
	public static final int LIGHT_MAX = 255;
	public static final int IMG_SIZE = 10;
	
	
	public static double[] getFeatureVector(Mat img) {
		double[] result = new double[IMG_SIZE * IMG_SIZE * 3];
		
		Mat newMat = new Mat();
		Imgproc.cvtColor(img, newMat, Imgproc.COLOR_RGB2HSV);
		newMat.reshape(IMG_SIZE, IMG_SIZE);
		int r = 0;
		for (int i=0; i < newMat.rows(); i++) {
			for (int j =0; j < newMat.cols(); j++) {
			 double[] temp = newMat.get(i,j);
			 result[r] = temp[0];
			 result[r+1] = temp[1];
			 result[r+2] = temp[2];
			 r = r + 3;
			}
		}
		
		return result;
	}

}
