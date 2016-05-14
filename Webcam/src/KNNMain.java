import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class KNNMain {
	public static final String SRC_OBJECTS_FOLDER = "resources/positives";
	public static final String SRC_NON_OBJECTS_FOLDER = "resources/negatives";

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		File[] objects = new File(SRC_OBJECTS_FOLDER).listFiles();
		File[] nonObjects = new File(SRC_NON_OBJECTS_FOLDER).listFiles();
		
		int totalNumberOfImages = objects.length + nonObjects.length;
		double[][] trainingSet = new double[totalNumberOfImages][FeatureVector.VECTOR_SIZE];
		
		for (int i = 0; i < objects.length; i++)
		{
			File f = objects[i];
			Mat m = Imgcodecs.imread(f.getPath());
			System.out.println(f.getName());
			double[] tmp = FeatureVector.getFeatureVector(m);
			double[] fv = new double[tmp.length+1];
			for (int k=0; k < tmp.length; k++)
				fv[k] = tmp[k];
			fv[tmp.length] = 1;
			
			trainingSet[i] = fv;
		}
		for (int i = 0, k=objects.length; i < nonObjects.length; i++, k++)
		{
			File f = nonObjects[i];
			Mat m = Imgcodecs.imread(f.getPath());
			double[] tmp = FeatureVector.getFeatureVector(m);
			double[] fv = new double[tmp.length+1];
			for (int j=0; j < tmp.length; j++)
				fv[j] = tmp[j];
			fv[tmp.length] = 1;
			
			trainingSet[k] = fv;
		}
		
		KNNClassifier classifier = new KNNClassifier(trainingSet, 13);
		
		Mat m = Imgcodecs.imread("resources/validation/bike.jpg");
		double[] fv = FeatureVector.getFeatureVector(m);
		int result = classifier.classify(fv);
		System.out.println(result);
	}
}
