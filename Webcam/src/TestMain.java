import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

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
		int[] topology = {300, 100, 50, 25, 10, 5, 3};
		Brain b = new Brain(topology, true);
		trainBrain(b);
		for (int i = 0; i < 5; i++)
		{
			Mat test = Imgcodecs.imread("C:/WebcamTest/outobjects/" + i + ".jpg");
			double[] input = createFeatureVector(test);
			b.feedForward(input);
			System.out.println(b.getOutput()[0] + " " + b.getOutput()[1] + " " + b.getOutput()[2]);
		}
		System.out.println("Done!");
	}
	public static void trainBrain(Brain b) 
	{
		String obj_dir = CreateTrainingSet.OUT_OBJECTS_FOLDER;
		String non_obj_dir = CreateTrainingSet.OUT_NON_OBJECTS_FOLDER;
		//String backup_dir = "C:/WebcamTest/brain.sav";
		File[] objects = new File(obj_dir).listFiles();
		File[] nonObjects = new File(non_obj_dir).listFiles();
		double[] object_target = {1, 1 , 1};
		double[] non_object_target = {-1, -1, -1};
		double errorThreshold = 0.3;
		int epoch = 1;
		Brain prevBrain = null;
		//double averageError = 0;
		double prevError = 0;
		do 
		{
			double currentError = 0;
			for (int i = 0; i < objects.length; i++)
			{
				currentError += b.overallError;
				Mat m = Imgcodecs.imread(objects[i].getPath());
				double[] inputs = createFeatureVector(m);
				b.feedForward(inputs);
				b.backPropagate(object_target);
				
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
	public static double[] createFeatureVector(Mat m)
	{
		double hMax = 180;
		double sMax = 255;
		double vMax = 255;
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
	public static void saveNetwork(Brain b, String fileName) throws FileNotFoundException
	{
		File f = new File(fileName);
		if (f.exists())
			f.delete();
		PrintWriter p = new PrintWriter(f);
		String w = "";
		if (b.isBiased())
			w += "B\n";
		else
			w += "N\n";
		w += b.overallError + "\n" + b.layers.size() + "\n";
		for (int i = 0; i < b.layers.size(); i++)
		{
			Neuron[] neurons = b.layers.get(i).getNeurons();
			w += neurons.length + "\n";
			for (int j = 0; j < neurons.length; j++)
			{
				Neuron n = neurons[j];
				w += n.getId() + " " + n.getValue() + " ";
				for (int k = 0; k < n.getWeights().size(); k++)
				{
					w += n.getWeights().get(k).value + " " + n.getWeights().get(k).deltaValue + " ";
				}
				w += "\n";
			}

		}
		p.write(w);
		p.close();
	}

	public static Brain loadNetwork(String fileName) throws FileNotFoundException
	{
		File f = new File(fileName);
		Scanner s = new Scanner(f);
		boolean bias = false;
		if (s.nextLine().equals("B"))
		{
			bias = true;
		}
		double error = Double.parseDouble(s.nextLine());
		int numLayers = Integer.parseInt(s.nextLine());
		ArrayList<Layer> layers = new ArrayList<Layer>();
		if (!bias)
		{
			for (int i = 0; i < numLayers; i++)
			{
				int numNeurons = Integer.parseInt(s.nextLine());
				Neuron[] neurons = new Neuron[numNeurons];
				for (int j = 0; j < numNeurons; j++)
				{
						String[] data = s.nextLine().split(" ");
						Neuron n = new Neuron(Integer.parseInt(data[0]), Double.parseDouble(data[1]), (data.length - 2)/2);
						double[] wData = dataToWeightData(data);
						ArrayList<Weight> weights = new ArrayList<Weight>();
						for (int k = 0; k < wData.length; k += 2)
						{
							Weight w = new Weight(wData[k], wData[k + 1]);
							weights.add(w);
						}
						n.setWeights(weights);
						neurons[j] = n;
				}
				Layer l = new Layer(neurons);
				layers.add(l);
			}
		}
		else
		{
			for (int i = 0; i < numLayers; i++)
			{
				if (i == numLayers - 1)
				{
					int numNeurons = Integer.parseInt(s.nextLine());
					Neuron[] neurons = new Neuron[numNeurons];
					for (int j = 0; j < numNeurons; j++)
					{
							String[] data = s.nextLine().split(" ");
							Neuron n = new Neuron(Integer.parseInt(data[0]), Double.parseDouble(data[1]), (data.length - 2)/2);
							double[] wData = dataToWeightData(data);
							ArrayList<Weight> weights = new ArrayList<Weight>();
							for (int k = 0; k < wData.length; k += 2)
							{
								Weight w = new Weight(wData[k], wData[k + 1]);
								weights.add(w);
							}
							n.setWeights(weights);
							neurons[j] = n;
					}
					Layer l = new Layer(neurons);
					layers.add(l);
				}
				else
				{
					int numNeurons = Integer.parseInt(s.nextLine());
					Neuron[] neurons = new Neuron[numNeurons];
					for (int j = 0; j < numNeurons; j++)
					{
						if (j < numNeurons - 1)
						{
							String[] data = s.nextLine().split(" ");
							Neuron n = new Neuron(Integer.parseInt(data[0]), Double.parseDouble(data[1]), (data.length - 2)/2);
							double[] wData = dataToWeightData(data);
							ArrayList<Weight> weights = new ArrayList<Weight>();
							for (int k = 0; k < wData.length; k += 2)
							{
								Weight w = new Weight(wData[k], wData[k + 1]);
								weights.add(w);
							}
							n.setWeights(weights);
							neurons[j] = n;
						}
						else
						{
							String[] data = s.nextLine().split(" ");
							BiasNeuron n = new BiasNeuron(Integer.parseInt(data[0]), Double.parseDouble(data[1]), (data.length - 2)/2);
							double[] wData = dataToWeightData(data);
							ArrayList<Weight> weights = new ArrayList<Weight>();
							for (int k = 0; k < wData.length; k += 2)
							{
								Weight w = new Weight(wData[k], wData[k + 1]);
								weights.add(w);
							}
							n.setWeights(weights);
							neurons[j] = n;
						}
					}
					Layer l = new Layer(neurons);
					layers.add(l);
				}
			}
		}
		s.close();
		Brain b = new Brain(layers, bias);
		b.overallError = error;
		return b;
	}
	public static double[] dataToWeightData(String[] data)
	{
		double[] w = new double[data.length - 2];
		for (int i = 0; i < w.length; i++)
		{
			w[i] = Double.parseDouble(data[i + 2]);
		}
		return w;
	}


}
