//test
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class MyFrame extends JFrame {
    private JPanel contentPane;

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 650, 490);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
  
        new MyThread().start();
    }
 
    VideoCap videoCap = new VideoCap();
 
    public void paint(Graphics g)
    {
        g = contentPane.getGraphics();
        BufferedImage src = videoCap.getOneFrame();
        byte[] data = ((DataBufferByte) src.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(src.getHeight(), src.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, data);
        Imgcodecs.imwrite("./test2.jpg", mat);
        mat = Imgcodecs.imread("./test2.jpg");
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
				//Mat temp = src.clone();
				
				//Imgproc.rectangle(temp, r.tl(), r.br(), new Scalar(0, 0, 255));
				roi.add(new Mat(mat, r));
				Imgproc.rectangle(display, r.tl(), r.br(), new Scalar(0, 0, 255));
			}
		}
        g.drawImage(colorMatToImage(display), 0, 0, this);
        
        //edit here
    }
    
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