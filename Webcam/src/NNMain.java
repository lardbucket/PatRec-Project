import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class NNMain 
{
	public static final String FILENAME = "C:/NN/brain.txt";
	public static final String FILENAME_FINAL = "C:/NN/brainFINAL.txt";
	public static final String INPUT = "C:/NN/input.txt";
	public static final String VALIDATION = "C:/NN/validation.txt";
	public static void main(String[] args) throws FileNotFoundException
	{
		//System.out.println("I am derp");
		//int[] topology = {3, 3, 2};
		//save test
		//Brain b = new Brain(topology);
		//System.out.println(b.printNetwork());
		//saveNetwork(b);
		//load test
		//Brain b = loadNetwork(FILENAME);
		//System.out.println(b.printNetwork());
		//run test
		/**
		Brain brain = new Brain(topology, false);
		System.out.println("Before Feed Forward:");
		System.out.println(brain.printNetwork());
		double data[] = {0.8f, 0.9f, 0.8f};
		double target[] = {0.9f, 0.5f};

		double idealError = 0.1;
		int epoch = 50;
		for(int i = 0; i < epoch; i++) 
		{
			double currentError = brain.overallError;
			System.out.println("Epoch: " + i);
			brain.feedForward(data);
			brain.backPropagate(target);
			System.out.println("Error: " + brain.overallError);
			if(brain.overallError < currentError) 
			{
				System.out.println("finally learning!");
				if(brain.overallError < idealError) 
				{
					System.out.println("I'm ready!");
					break;
				}
			}
			else 
			{
				System.out.println("Derp");
			}
			System.out.println();
		}
		*/
		
		
		//biasTest();
		
		Brain b = trainingTest();
		System.out.println("=================================================\n");
		System.out.println(b.printNetwork());
		validationTest(b);
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
	private static Brain trainingTest() throws FileNotFoundException
	{
		File f = new File(INPUT);
		Scanner in = new Scanner(f);
		
		int numInputs = Integer.parseInt(in.nextLine());
		
		DataValue[] inputValues = new DataValue[numInputs];
		
		for (int i = 0; i < numInputs; i++)
		{
			String[] data = in.nextLine().split(" ");
			double[] values = new double[data.length - 1];
			String label = "";
			for (int j = 0; j < data.length; j++)
			{
				if (j < data.length - 1)
				{
					values[j] = Double.parseDouble(data[j]);
				}
				else
					label = data[j];
			}
			inputValues[i] = new DataValue(values, label);
		}
		
		in.close();
		
		int[] topology = {3, 4, 3};
		Brain b = new Brain(topology, true);
		saveNetwork(b, FILENAME);
		double idealError = 0.1;
		
		System.out.println("Ideal Error:  " + idealError + "\n");
		int epoch = 500;
		double prevError = 0;
		for (int i = 0; i < epoch; i++)
		{
			double averageError = 0;
			System.out.println("Epoch " + i + ":\n");
			for (int j = 0; j < inputValues.length; j++)
			{
				double[] data = inputValues[j].values;
				double[] target = inputValues[j].convertLabel();
				averageError += b.overallError;
				b.feedForward(data);
				b.backPropagate(target);	
			}
			averageError /= inputValues.length;
			System.out.println("Error: " + averageError);
			if(averageError < prevError)
			{
				System.out.println("finally learning!");
				if(averageError < idealError) 
				{
					System.out.println("I'm ready!");
					break;
				}
			}
			else 
			{
				System.out.println("Not learning!");
			}
			prevError = averageError;
			System.out.println();
		}
		return b;
		
		
	}
	
	private static void validationTest(Brain b) throws FileNotFoundException
	{
		
		File f2 = new File(VALIDATION);
		Scanner v = new Scanner(f2);
		int numValid = Integer.parseInt(v.nextLine());
		DataValue[] validationValues = new DataValue[numValid];
		for (int i = 0; i < numValid; i++)
		{
			String[] data = v.nextLine().split(" ");
			double[] values = new double[data.length - 1];
			String label = "";
			for (int j = 0; j < data.length; j++)
			{
				if (j < data.length - 1)
				{
					values[j] = Double.parseDouble(data[j]);
				}
				else
					label = data[j];
			}
			validationValues[i] = new DataValue(values, label);
		}
		v.close();
		int correctAnswers = 0;
		for (int i = 0; i < validationValues.length; i++)
		{
			double[] values = validationValues[i].values;
			double[] target = validationValues[i].convertLabel();
			System.out.print("Test " + (i + 1) + ": \nInput: {");
			for (int j = 0; j < values.length; j++)
				if (j < values.length - 1)
					System.out.print(values[j] + ",");
				else
					System.out.print(values[j] + "}\n");
			String outString = "Raw Output: {";
			b.feedForward(values);
			double[] output = b.getOutput();
			for (int j = 0; j < output.length; j++)
			{
				if (j < values.length - 1)
					outString += output[j] + ",";
				else 
					outString += output[j] + "}";
			}
			System.out.println(outString);
			String roundedString = "Rounded Output: {";
			double[] roundedOut = convertOutput(output);
			for (int j = 0; j < target.length; j++)
				if (j < values.length - 1)
					roundedString += roundedOut[j] + ",";
				else
					roundedString += roundedOut[j] + "}";
			roundedString += ", Closest Label: " + getClosestLabel(roundedOut);
			System.out.println(roundedString);
			String targetString = "Target: {";
			for (int j = 0; j < target.length; j++)
				if (j < values.length - 1)
					targetString += target[j] + ",";
				else
					targetString += target[j] + "}";
			targetString += ", Label: " + validationValues[i].label;
			System.out.println(targetString);
			if (getClosestLabel(roundedOut).equals(validationValues[i].label))
			{
				correctAnswers++;
				System.out.println("Correct!");
			}
			else
				System.out.println("Wrong!");
		}
		String scoreString = "Score: " + correctAnswers + "/" + validationValues.length;;
		System.out.println(scoreString);
		saveNetwork(b, FILENAME_FINAL);
	}
	
	private static double[] convertOutput(double[] output)
	{
		double[] r = new double[output.length];
		for (int i = 0; i < output.length; i++)
			r[i] = Math.round(output[i]);
		return r;
	}
	
	public static String getClosestLabel(double[] convertedOutput)
	{
		String a = "A";
		String b = "B";
		String c = "C";
		if (convertedOutput[0] == 1 && convertedOutput[1] == 0 && convertedOutput[2] == 0)
			return a;
		else if (convertedOutput[0] == 0 && convertedOutput[1] == 1 && convertedOutput[2] == 0)
			return b;
		else if (convertedOutput[0] == 0 && convertedOutput[1] == 0 && convertedOutput[2] == 1)
			return c;
		else
			return "Invalid";
	}
	
	private static void biasTest() throws FileNotFoundException
	{
		int[] topology = {3, 3, 3};
		Brain b = new Brain(topology, true);
		String old = b.printNetwork();
		System.out.println(b.printNetwork());
		saveNetwork(b, FILENAME);
		System.out.println("==========================\nSaved\n==========================");
		b = loadNetwork(FILENAME);
		System.out.println(b.printNetwork());
		String notOld = b.printNetwork();
		if (old.equals(notOld)) 
			System.out.println("YAY");
		else
			System.out.println("BOO");
	}
	
	
}
