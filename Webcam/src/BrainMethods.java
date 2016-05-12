import java.io.File;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class BrainMethods //contains the trainBrain, getHSV, createFeatureVector and isObject methods
{
	static final double[] object_target = {1};
	static final double[] non_object_target = {-1};
	public static void trainBrain(Brain b)
	{
		String obj_dir = CreateTrainingSet.OUT_OBJECTS_FOLDER;
		String non_obj_dir = CreateTrainingSet.OUT_NON_OBJECTS_FOLDER;
		File[] objects = new File(obj_dir).listFiles();
		File[] nonObjects = new File(non_obj_dir).listFiles();
		
		double errorThreshold = 0.25;
		int epoch = 1;
		Brain prevBrain = null;
		//double averageError = 0;
		double prevError = 0;
		do 
		{
			double currentError = 0;
			for (int i = 0; i < objects.length; i++)
			{
				
				Mat m = Imgcodecs.imread(objects[i].getPath());
				double[] inputs = createFeatureVector(m);
				b.feedForward(inputs);
				b.backPropagate(object_target);
				currentError += b.overallError;
				//m = Imgcodecs.imread(nonObjects[i].getPath());
				//inputs = createFeatureVector(m);
				//b.feedForward(inputs);
				//b.backPropagate(non_object_target);
				//currentError += b.overallError;
			}
			
			for (int i = 0; i < objects.length; i++)
			{
				currentError += b.overallError;
				Mat m = Imgcodecs.imread(nonObjects[i].getPath());
				double[] inputs = createFeatureVector(m);
				b.feedForward(inputs);
				b.backPropagate(non_object_target);
				
			}
			currentError /= (objects.length + objects.length);
			System.out.println("Epoch " + epoch + ": " + currentError);
			
			
			if (epoch == 1)
			{
				prevBrain = b;
				prevError = currentError;
			}
			else if (currentError > prevError)
			{
				b = prevBrain;
			}
			else
			{
				//saveNetwork(b, backup_dir);
				prevBrain = b;
				prevError = currentError;
			}
			
			epoch++;
		}
		while (prevError > errorThreshold);
	}
	
	public static double[] createFeatureVector(Mat mat)
	{
		double hMax = 180;
		double sMax = 255;
		double vMax = 255;
		Mat m = mat.clone();
		m = getHSV(m);
		if (m.cols() != 10 || m.rows() != 10)
		{
			Imgproc.resize(m, m, new Size(10, 10));
		}
		//System.out.println(m.dump());
		double[] r = new double[300];
		int size = 0;
		for (int i = 0; i < m.rows(); i++)
		{
			for (int j = 0; j < m.cols(); j++)
			{
				double[] temp = m.get(i, j);
				r[size] = temp[0] / hMax;
				r[size + 1] = temp[1] /sMax;
				r[size + 2] = temp[2] /vMax;
				size += 3;
			}
		}
		return r;
	}
	

	public static Mat getHSV(Mat m)
	{
		Mat r = m.clone();
		Imgproc.cvtColor(r, r, Imgproc.COLOR_RGB2HSV);
		return r;
	}
	
	public static boolean isObject(Mat m, Brain b)
	{
		double[] inputs = BrainMethods.createFeatureVector(m);
		b.feedForward(inputs);
		double[] output = b.getOutput();
		if (output[0] > 0)
			return true;
		else
			return false;
	}
	
}
