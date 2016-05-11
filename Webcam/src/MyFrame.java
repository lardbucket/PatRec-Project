//test
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

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


public class MyFrame extends JFrame implements KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	//private int state = 0;
	int[] topology = {300, 150, 75, 100, 50, 25, 10, 5, 1};
	private Brain b = new Brain(topology, false);
	Scalar red = new Scalar(0, 0, 255);
	Scalar blue = new Scalar(255, 0, 0);
	/**
	 * states:
	 * 0 = normal (a)
	 * 1 = binary (s)
	 * 2 = edges  (d)
	 * 3 = hsv    (f)
	 */
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					MyFrame frame = new MyFrame();
					frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MyFrame() 
	{
		super("Object Detection");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 490);
		this.setFocusable(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new FlowLayout());
		contentPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		//JPanel j = new JPanel();
		//JButton b1 = new JButton("Test");
		//b1.setVisible(true);
		//contentPane.add(j);
		//j.add(b1);
		setContentPane(contentPane);
		addKeyListener(this);
		//contentPane.setLayout(null);
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		trainBrain(b);
		new MyThread().start();
	}

	VideoCap videoCap = new VideoCap();

	public void paint(Graphics g)
	{
	//	if (state == 0)
		//{
			this.setTitle("Object Detection: Normal");
			g = contentPane.getGraphics();
			BufferedImage src = videoCap.getOneFrame();
			byte[] data = ((DataBufferByte) src.getRaster().getDataBuffer()).getData();
			Mat mat = new Mat(src.getHeight(), src.getWidth(), CvType.CV_8UC3);
			mat.put(0, 0, data);
			Imgcodecs.imwrite("C:/WebcamTest/test2.jpg", mat);
			mat = Imgcodecs.imread("C:/WebcamTest/test2.jpg");
			Mat grayMat = new Mat(mat.height(),mat.width(), CvType.CV_8UC1);
			Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY);
			Mat binMat = new Mat(mat.height(),mat.width(), CvType.CV_8UC1);
			Imgproc.threshold(grayMat, binMat, 100, 255, Imgproc.THRESH_BINARY);

			Mat edge = new Mat();
			edge = autoCanny(mat);
			ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			ArrayList<Mat> roi = new ArrayList<Mat>();
			Mat display = mat.clone();
			Imgproc.findContours(edge, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
			for (int i = 0; i < contours.size(); i++)
			{
				MatOfPoint m = contours.get(i);
				Rect r = Imgproc.boundingRect(m);
				if (r.area() > 100)
				{
					Mat newROI = new Mat(mat, r);
					roi.add(newROI);
					if (isObject(newROI))
						Imgproc.rectangle(display, r.tl(), r.br(), blue);
					else
						Imgproc.rectangle(display, r.tl(), r.br(), red);
				}
			}
			g.drawImage(colorMatToImage(display), 0, 0, this);
		}
		/**
		else if (state == 1)
		{
			this.setTitle("Object Detection: Binary");
			g = contentPane.getGraphics();
			BufferedImage src = videoCap.getOneFrame();
			byte[] data = ((DataBufferByte) src.getRaster().getDataBuffer()).getData();
			Mat mat = new Mat(src.getHeight(), src.getWidth(), CvType.CV_8UC3);
			mat.put(0, 0, data);
			Imgcodecs.imwrite("C:/WebcamTest/test2.jpg", mat);
			mat = Imgcodecs.imread("C:/WebcamTest/test2.jpg");
			Mat grayMat = new Mat(mat.height(),mat.width(), CvType.CV_8UC1);
			Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY);
			Mat binMat = new Mat(mat.height(),mat.width(), CvType.CV_8UC1);
			Imgproc.threshold(grayMat, binMat, 100, 255, Imgproc.THRESH_BINARY);

			Mat edge = new Mat();
			edge = autoCanny(mat);
			ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			ArrayList<Mat> roi = new ArrayList<Mat>();
			Mat display = binMat.clone();
			Imgproc.findContours(edge, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
			for (int i = 0; i < contours.size(); i++)
			{
				MatOfPoint m = contours.get(i);
				Rect r = Imgproc.boundingRect(m);
				if (r.area() > 100)
				{
					Mat newROI = new Mat(mat, r);
					
					roi.add(newROI);
					if (isObject(newROI))
						Imgproc.rectangle(display, r.tl(), r.br(), red);
					else
						Imgproc.rectangle(display, r.tl(), r.br(), red);
				}
			}
			g.drawImage(binMatToImage(display), 0, 0, this);

		}
		*/
		//edit here
	//}

	public static BufferedImage grayMatToImage(Mat m)
	{
		byte[] data2 = new byte[m.rows() * m.cols() * (int)(m.elemSize())];
		m.get(0, 0, data2);
		BufferedImage image1 = new BufferedImage(m.cols(),m.rows(), BufferedImage.TYPE_BYTE_GRAY);
		image1.getRaster().setDataElements(0, 0, m.cols(), m.rows(), data2);
		return image1;
	}
	public static BufferedImage colorMatToImage(Mat m)
	{
		byte[] data2 = new byte[m.rows() * m.cols() * (int)(m.elemSize())];
		m.get(0, 0, data2);
		BufferedImage image1 = new BufferedImage(m.cols(),m.rows(), BufferedImage.TYPE_3BYTE_BGR);
		image1.getRaster().setDataElements(0, 0, m.cols(), m.rows(), data2);
		return image1;
	}
	public static BufferedImage binMatToImage(Mat m)
	{
		byte[] data2 = new byte[m.rows() * m.cols() * (int)(m.elemSize())];
		m.get(0, 0, data2);
		BufferedImage image1 = new BufferedImage(m.cols(),m.rows(), BufferedImage.TYPE_BYTE_BINARY);
		image1.getRaster().setDataElements(0, 0, m.cols(), m.rows(), data2);
		return image1;
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
	public static void trainBrain(Brain b)
	{
		String obj_dir = CreateTrainingSet.OUT_OBJECTS_FOLDER;
		String non_obj_dir = CreateTrainingSet.OUT_NON_OBJECTS_FOLDER;
		File[] objects = new File(obj_dir).listFiles();
		File[] nonObjects = new File(non_obj_dir).listFiles();
		double[] object_target = {1};
		double[] non_object_target = {-1};
		double errorThreshold = 1.01;
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

	public static Mat getHSV(Mat m)
	{
		Mat r = m.clone();
		Imgproc.cvtColor(r, r, Imgproc.COLOR_RGB2HSV_FULL);
		return r;
	}
	
	public static double[] createFeatureVector(Mat mat)
	{
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
				r[size] = temp[0];
				r[size + 1] = temp[1];
				r[size + 2] = temp[2];
				size += 3;
			}
		}
		return r;
	}
	
	public boolean isObject(Mat m)
	{
		double[] inputs = createFeatureVector(m);
		b.feedForward(inputs);
		double[] output = b.getOutput();
		if (output[0] > 0)
			return true;
		else
			return false;
	}
	
	@Override	
	public void keyPressed(KeyEvent e) 
	{
		int k = e.getKeyCode();
		if (k == KeyEvent.VK_RIGHT) 
		{

			//if (state < 4)
			//	state++;
		}
		else if (k == KeyEvent.VK_LEFT){
			//if (state > 0)
			//	state--;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	class MyThread extends Thread{
		@Override
		public void run() 
		{
			for (;;)
			{
				repaint();
				try 
				{ 
					Thread.sleep(30);
				} 
				catch (InterruptedException e) 
				{    

				}
			}  
		} 
	}
}