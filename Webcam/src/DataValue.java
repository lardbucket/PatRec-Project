
public class DataValue 
{
	public double[] values;
	public String label;
	public DataValue(double[] values, String label)
	{
		this.values = values;
		this.label = label;
	}
	public double[] convertLabel()
	{
		if (label.equals("A"))
		{
			double[] r = {1, 0, 0};
			return r;
		}
		else if (label.equals("B"))
		{
			double[] r = {0, 1, 0};
			return r;
		}
		else
		{
			double[] r = {0, 0, 1};
			return r;
		}
	}
}
