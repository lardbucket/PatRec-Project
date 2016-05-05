import java.util.Random;

public class Weight 
{
	public double value;
	public double deltaValue;

	public Weight()
	{
		value = rand(-1, 1);
		deltaValue = 0;
	}
	
	public Weight(double value, double deltaValue) 
	{
	  this.value = value;
	  this.deltaValue = deltaValue;
	}
	
	public static double rand(int min, int max) 
	{
	  Random random = new Random();
	  return min + random.nextFloat() * (max - min);
	}
}
