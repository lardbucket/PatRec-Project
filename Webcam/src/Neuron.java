import java.util.ArrayList;

public class Neuron 
{
	private int id;
	private double value, gradient;
	private ArrayList<Weight> weights;
	public Neuron(int id, double value, int numOutputs)
	{
		this.id = id;
		this.value = value;
		gradient = 0;
		weights = new ArrayList<Weight>();
		for (int i = 0; i < numOutputs; i++)
			weights.add(new Weight());
	}
	
	public void feedForward(double[] inputs, double[] weights)
	{
	    float newVal = 0;
	    for(int i = 0; i < inputs.length; i++) 
	    {
	    	newVal +=  inputs[i] * weights[i];
	    }

	    this.value = activate(newVal);
	}
	
	public double activate(double val) 
	{
		return Math.tanh(val);
	}
	
	 public void calculateOutputGradient(double targetVal)
	  {
	    double delta = targetVal - this.value;
	    this.gradient = delta * tanhDerivative(value);
	  }
	 
	  public void calculateHiddenGradient(Neuron[] neurons)
	  {
	    double dow = sumDOW(neurons);
	    this.gradient = dow * tanhDerivative(value);
	  }

	  public double sumDOW(Neuron[] neurons)
	  {
	    double sum = 0;
	    for(int i = 0; i < neurons.length; i++) 
	    {
	      sum += weights.get(i).value * neurons[i].getGradient();
	    }

	    return sum;
	  }

	  public double tanhDerivative(double val)
	  {
	    return 1.0d - val * val;
	  }
	
	  public Neuron[] updateInputWeights(Neuron[] previousLayerNeurons)
	  {
	    double eta = 0.15;
	    double alpha = 0.5;
	    for(int i = 0; i < previousLayerNeurons.length; i++) {
	      Neuron n = previousLayerNeurons[i];
	      double oldDeltaWeight = n.weights.get(this.id).deltaValue;
	      double newDeltaWeight = 0.15d * n.value * gradient + 0.5d * oldDeltaWeight;
	  
	      Weight w = n.weights.get(this.id);
	      w.deltaValue = newDeltaWeight;
	      w.value += newDeltaWeight;

	      n.weights.set(this.id, w);
	      previousLayerNeurons[i] = n;
	    }

	    return previousLayerNeurons;
	  }
	
	public void setValue(double value) {
		this.value = value;
	}
	public double getValue() {
		return value;
	}
	public void setWeights(ArrayList<Weight> weights) {
		this.weights = weights;
	}
	public double getGradient() {
		return gradient;
	}
	public ArrayList<Weight> getWeights() {
		return weights;
	}
	public int getId() {
		return id;
	}
	
}
