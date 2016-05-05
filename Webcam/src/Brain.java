import java.util.ArrayList;

public class Brain 
{
	public ArrayList<Layer> layers;
	public double overallError;
	private boolean biased;
	public Brain(int[] topology, boolean hasBias) //topology = layer sizes w/o bias neurons
	{
		this.biased = hasBias;
		layers = new ArrayList<Layer>();
		overallError = 0;
		if (hasBias == false)
		{
			for (int i = 0; i < topology.length; i++)
			{
				Layer l = new Layer(topology[i]);
				if (i < topology.length - 1)
				{
					l.initialize(topology[i + 1], hasBias);
				}
				else //output layer
				{
					l.initialize(0, false);
				}
				layers.add(l);
			}
		}
		else
		{
			int[] newTop = new int[topology.length];
			for (int i = 0; i < topology.length; i++)
			{
				if (i < topology.length - 1)
					newTop[i] = topology[i] + 1;
				else
					newTop[i] = topology[i];
			}
			for (int i = 0; i < newTop.length; i++)
			{
				Layer l = new Layer(newTop[i]);
				if (i < newTop.length - 1)
				{
					l.initialize(newTop[i + 1], hasBias);
				}
				else //output layer
				{
					l.initialize(0, false);
				}
				layers.add(l);
			}
			
		}
	}

	public boolean isBiased() 
	{
		return biased;
	}

	public Brain(ArrayList<Layer> l, boolean hasBias)
	{
		layers = l;
		biased = hasBias;
	}

	public void feedForward(double[] inputs)
	{
		if (!biased)
		{
			Layer inputLayer = layers.get(0);
			Neuron[] inputNeurons = inputLayer.getNeurons();
			for(int i = 0; i < inputNeurons.length; i++) 
			{
				Neuron n = inputNeurons[i];
				n.setValue(inputs[i]);
				inputNeurons[i] =  n;
			}
			inputLayer.setNeurons(inputNeurons);
			layers.set(0, inputLayer);
	
			//step 2
	
			for(int i = 1; i < layers.size(); i++) {
				Layer currentLayer = layers.get(i);
				Layer previousLayer = layers.get(i - 1);
	
				Neuron[] currentNeurons = currentLayer.getNeurons();
				Neuron[] newNeurons = new Neuron[currentNeurons.length];
	
				double[] previousInputs = previousLayer.getValues();
	
				for(int j = 0; j < currentNeurons.length; j++) {
					Neuron currentNeuron = currentNeurons[j];
					double[] previousWeights = previousLayer.getWeights(j);
	
					currentNeuron.feedForward(previousInputs, previousWeights);
					newNeurons[j] = currentNeuron;
				}
	
				currentLayer.setNeurons(newNeurons);
				layers.set(i, currentLayer);
			}
		}
		else
		{
			Layer inputLayer = layers.get(0);
			Neuron[] inputNeurons = inputLayer.getNeurons();
			for(int i = 0; i < inputNeurons.length; i++) 
			{
				Neuron n = inputNeurons[i];
				try
				{
					n.setValue(inputs[i]);
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					//nothing
				}
				inputNeurons[i] =  n;
			}
			inputLayer.setNeurons(inputNeurons);
			layers.set(0, inputLayer);
	
			//step 2
	
			for(int i = 1; i < layers.size(); i++) 
			{
				Layer currentLayer = layers.get(i);
				Layer previousLayer = layers.get(i - 1);
	
				Neuron[] currentNeurons = currentLayer.getNeurons();
				Neuron[] newNeurons = new Neuron[currentNeurons.length];
				
				double[] previousInputs = previousLayer.getValues();
	
				for(int j = 0; j < currentNeurons.length; j++) 
				{
					Neuron currentNeuron = currentNeurons[j];
					//if (j == currentNeurons.length)
					//{
						double[] previousWeights = previousLayer.getWeights(j);
						
						currentNeuron.feedForward(previousInputs, previousWeights);
						newNeurons[j] = currentNeuron;
					//}
				}
	
				currentLayer.setNeurons(newNeurons);
				layers.set(i, currentLayer);
			}
		}
	}

	public void backPropagate(double[] targetValues) 
	{
		Layer outputLayer = layers.get(layers.size() - 1);
		Neuron[] outputLayerNeurons = outputLayer.getNeurons();

		overallError = 0;

		for(int i = 0; i < outputLayerNeurons.length; i++) 
		{
			double delta = targetValues[i] - outputLayerNeurons[i].getValue();
			overallError += delta * delta;
		}

		overallError /= outputLayerNeurons.length;
		overallError = Math.sqrt(overallError);

		// Calculate output neurons gradients
		for(int i = 0; i < outputLayerNeurons.length; i++) 
		{
			Neuron n = outputLayerNeurons[i];
			n.calculateOutputGradient(targetValues[i]);
			outputLayer.getNeurons()[i] = n;
		}

		// Calculate gradients on hidden layer
		for(int i = layers.size() - 2; i > 0; i--) 
		{
			Layer hiddenLayer = layers.get(i);
			Layer nextLayer = layers.get(i + 1);
			Neuron[] hiddenNeurons = hiddenLayer.getNeurons();

			for(int n = 0; n < hiddenNeurons.length; n++) 
			{
				Neuron hNeuron = hiddenNeurons[n];
				hNeuron.calculateHiddenGradient(nextLayer.getNeurons());

				hiddenNeurons[n] =  hNeuron;
			}

			hiddenLayer.setNeurons(hiddenNeurons);

			layers.set(i, hiddenLayer);
		}

		// Update weights of connections in output and hidden layers
		for(int i = layers.size() - 1; i > 0; i--) 
		{
			Layer layer = layers.get(i);
			Layer previousLayer = layers.get(i - 1);
			Neuron[] currentNeurons = layer.getNeurons();
			Neuron[] previousLayerNeurons = previousLayer.getNeurons();

			for(int j = 0; j < currentNeurons.length; j++) 
			{
				Neuron n = currentNeurons[j];
				previousLayerNeurons = n.updateInputWeights(previousLayerNeurons);
				previousLayer.setNeurons(previousLayerNeurons);
				layers.set(i - 1, previousLayer);
			}
		}
	}


	public String printNetwork()
	{
		String r = "";
		if (!biased)
		{
			for (int i = 0; i < layers.size(); i++)
			{
				Layer l = layers.get(i);
				r += "Layer " + i + ": \n";
				r += "   Size: " + l.getNeurons().length + "\n";
				for (int j = 0; j < l.getNeurons().length; j++)
				{
					Neuron n = l.getNeurons()[j];
					r += "   Neuron " + j + ":\n";
					r += "      Value: " + n.getValue() + "\n";
					r += "      Weights: ";
					for (int k = 0; k < n.getWeights().size(); k++)
					{
						r += n.getWeights().get(k).value;
						if (k < n.getWeights().size() - 1)
							r += ", ";
					}
					r += "\n";
				}
			}
		}
		else
		{
			for (int i = 0; i < layers.size(); i++)
			{
				if (i < layers.size() - 1)
				{
					Layer l = layers.get(i);
					r += "Layer " + i + ": \n";
					r += "   Size: " + l.getNeurons().length + "\n";
					for (int j = 0; j < l.getNeurons().length; j++)
					{
						if (j < l.getNeurons().length - 1)
						{
							Neuron n = l.getNeurons()[j];
							r += "   Neuron " + j + ":\n";
							r += "      Value: " + n.getValue() + "\n";
							r += "      Weights: ";
							for (int k = 0; k < n.getWeights().size(); k++)
							{
								r += n.getWeights().get(k).value;
								if (k < n.getWeights().size() - 1)
									r += ", ";
							}
							r += "\n";
						}
						else
						{
							Neuron n = l.getNeurons()[j];
							r += "   Bias Neuron:\n";
							r += "      Value: " + n.getValue() + "\n";
							r += "      Weights: ";
							for (int k = 0; k < n.getWeights().size(); k++)
							{
								r += n.getWeights().get(k).value;
								if (k < n.getWeights().size() - 1)
									r += ", ";
							}
							r += "\n";
						}
					}
				}
				else
				{
					Layer l = layers.get(i);
					r += "Layer " + i + ": \n";
					r += "   Size: " + l.getNeurons().length + "\n";
					for (int j = 0; j < l.getNeurons().length; j++)
					{
						Neuron n = l.getNeurons()[j];
						r += "   Neuron " + j + ":\n";
						r += "      Value: " + n.getValue() + "\n";
						r += "      Weights: ";
						for (int k = 0; k < n.getWeights().size(); k++)
						{
							r += n.getWeights().get(k).value;
							if (k < n.getWeights().size() - 1)
								r += ", ";
						}
						r += "\n";
					}
				}
			}
		}
		return r;
	}
	public double[] getOutput()
	{
		return layers.get(layers.size() - 1).getValues();
	}
}
