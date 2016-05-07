

public class Layer 
{
	private Neuron[] neurons;
	public Layer(int size)
	{
		neurons = new Neuron[size];
	}
	
	public Layer(Neuron[] n)
	{
		neurons = n;
	}
	
	public void initialize(int numOutput, boolean addBias)
	{
		if (addBias)
		{
			for (int i = 0; i < neurons.length; i++)
			{
				if (i < neurons.length - 1)
				{
					neurons[i] = new Neuron(i, 0, numOutput);
				}
				else
				{
					neurons[i] = new BiasNeuron(i, 1, numOutput);
				}
			}
		}
		else
		{
			for (int i = 0; i < neurons.length; i++)
			{
				neurons[i] = new Neuron(i, 0, numOutput);
			}
		}
	}
	
	public Neuron[] getNeurons()
	{
		return neurons;
	}

	public void setNeurons(Neuron[] neurons) {
		this.neurons = neurons;
	}
	
	public double[] getValues()
	{
		double[] r = new double[neurons.length];
		for (int i = 0; i < r.length; i++)
		{
			r[i] = neurons[i].getValue();
		}
		return r;
	}
	
	public double[] getWeights(int index)
	{
		double[] r = new double[neurons.length];
		for (int i = 0; i < r.length; i++)
		{
			r[i] = neurons[i].getWeights().get(index).value;
		}
		return r;
	}
}
