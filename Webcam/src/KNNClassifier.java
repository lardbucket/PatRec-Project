
public class KNNClassifier {
	
	
	private double[][] trainingSet;
	
	private int kValue;
	
	
	/**
	 * Classifies using Weighted KNN
	 * 
	 * @param trainingSet
	 * @param kValue
	 */
	public KNNClassifier(double[][] trainingSet, int kValue) {
		
		// accepts a list of feature vectors with its corresponding label
		this.trainingSet = trainingSet;
		this.kValue = kValue;
	}
	
	/** 
	 * classify a feature vector 
	 * 
	 * Assumed that the length of the featureVector is equal to
	 * trainingSet[i].length-1
	 */
	public int classify(double[] featureVector) {
		// compute distance of the current featureVector to classify
		// to each of the feature vectors in the training set
		int fvLen = featureVector.length;
		int trainingSetLen = this.trainingSet.length;
		int[] labels = new int[trainingSetLen];
		double[] distances = new double[trainingSetLen];
		
		// foreach feature vector in the training set
		for (int i=0; i < trainingSetLen; i++) {
			distances[i] = this.computeDistance(featureVector, this.trainingSet[i]);
			
			// assumed that the last entry is the label
			// and convert it into 1 and 2 (instead of 0 and 1) for the meantime
			labels[i] = ((int) this.trainingSet[i][fvLen]) + 1;
			
		}
		
		// compute the weights
		double[] weights = new double[trainingSetLen];
		for (int i=0; i < trainingSetLen; i++) {
			weights[i] =  1 / (Math.pow(distances[i], 2));
		}
		
		// compute the weights * weight_value
		double totalWeightsFn = 0.0;
		for (int i=0; i < trainingSetLen; i++) {
			totalWeightsFn += weights[i] * labels[i];
		}
		
		
		// classify using the total, to where label it is near
		double oneDistance = Math.abs(1 - totalWeightsFn);
		double twoDistance = Math.abs(1 - totalWeightsFn);
		if (oneDistance < twoDistance) {
			// meaning the totalWeighthsFn is closer to one, return 0
			return 0;
		}
		else {
			// meaning the totalWeightsFn is close to two, return 1
			return 1;
		}		
	}
	
	private double computeDistance(double[] fv1, double[] fv2) {
		double total = 0.0;
		for (int i=0; i < fv1.length; i++)
		{
			total = total + Math.pow((fv1[i] - fv2[i]), 2);
		}
		
		return Math.sqrt(total);
	}

}
