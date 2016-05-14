import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;



public class FeatureVector {
	public static final int HUE_MAX = 180;
	public static final int SAT_MAX = 255;
	public static final int LIGHT_MAX = 255;
	public static final int IMG_SIZE = 10;
	public static final int VECTOR_SIZE = IMG_SIZE * IMG_SIZE * 3;
	
	
	public static double[] getFeatureVector(Mat img) {
		double[] result = new double[IMG_SIZE * IMG_SIZE * 3];
		
		Mat hsvMat = new Mat();
		Mat smallMat = new Mat();
		Imgproc.cvtColor(img, hsvMat, Imgproc.COLOR_RGB2HSV);
		Imgproc.resize(hsvMat, smallMat, new Size(10, 10));
		int r = 0;
		for (int i=0; i < smallMat.rows(); i++) {
			for (int j =0; j < smallMat.cols(); j++) {
			 double[] temp = smallMat.get(i,j);
			 result[r] = temp[0];
			 result[r+1] = temp[1];
			 result[r+2] = temp[2];
			 r = r + 3;
			}
		}
		
		return result;
	}

}
