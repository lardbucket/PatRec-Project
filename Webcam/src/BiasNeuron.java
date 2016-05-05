
public class BiasNeuron extends Neuron
{

	public BiasNeuron(int id, double value, int numOutputs) 
	{
		super(id, 1, numOutputs);
		
	}
	public double activate(double val) 
	{
		return 1;
	}

}
