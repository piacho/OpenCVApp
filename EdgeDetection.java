import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

public class MainApp2 extends Application {
	
	public static String path = "C:\\Users\\gregp\\Pictures\\TestDataApplication\\IMG_8170.jpg";
	

	@Override 
	public void start(Stage stage) throws IOException {
		
		WritableImage writableImage = getCoordinates();
		
		ImageView imageView = new ImageView(writableImage);
		
		imageView.setX(10);
		imageView.setY(10);
		
		imageView.setFitHeight(500);
		imageView.setFitWidth(800);
		
		imageView.setPreserveRatio(true);
		
		Group root = new Group(imageView);
		
		Scene scene = new Scene(root, 800, 500);
		
		stage.setTitle("Rect with coordinates in px and mm");
		
		stage.setScene(scene);
		
		stage.show();		
	}
	
	public WritableImage getCoordinates() throws IOException{
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Imgcodecs imgC = new Imgcodecs();
		
		// Creating a Mat for the original colour image
		
		Mat originalImageC = new Mat();
		
		// Creating a Mat for the orignlal image in grey scale
		
		Mat originalImage = new Mat();
		
		// Creating a Mat for the Processed Image
		
		Mat processedImage = new Mat();
		
		// Creating a Mat for the output of the Houhg Line Detection
		
		Mat lines = new Mat();
		
		// Reading the original colour image 
		
		originalImageC = imgC.imread(path);
		
		// Converting original to black and white Image - easier to manipulate as there is only one channel
		
		Imgproc.cvtColor(originalImageC, originalImage, Imgproc.COLOR_BGR2GRAY);
		
		
		// Applying Gaussian blur to reduce high frequencies in the image - make it smoother 
		
		Imgproc.GaussianBlur(originalImage, processedImage, new Size(45,45), 0);
		
		
		// Dectecting edges using Canny 
		
		Imgproc.Canny(processedImage, processedImage, 0, 30);
		
		// Hough Line Transform section 
		
		// Initial values for the Hough Line Transform
		
		int rho = 1;
		double theta = Math.PI / 180;
		int threshold = 15;
		int max_line_gap = 20;
		int min_line_length = 50;		
		
		// Line detection
		
		Imgproc.HoughLinesP(processedImage, lines, rho, theta, threshold, min_line_length,
				max_line_gap);
		
		int x1;
		int x2;
		int y1;
		int y2;
		int minX = originalImage.cols();
		int maxX = 0;
		int minY = originalImage.rows();
		int maxY = 0;
		
		List<Integer> allCoordinatesX = new ArrayList<Integer>();
		List<Integer> allCoordinatesY = new ArrayList<Integer>();
		
		double distance1 = 0;
		double distance2 = 0;
		double distance3 = 0;
		double distance4 = 0;
		double distance5 = 0;
		double distance6 = 0;
		double distance7 = 0;
		double distance8 = 0;

		// Looping through each of the detected "lines"		
		
		for(int i = 0; i < lines.total(); i++) {
			
			x1 = (int)lines.get(i, 0)[0];
			y1 = (int)lines.get(i, 0)[1];
			x2 = (int)lines.get(i, 0)[2];
			y2 = (int)lines.get(i, 0)[3];		
			
			allCoordinatesX.add(x1);
			allCoordinatesX.add(x2);
			
			allCoordinatesY.add(y1);
			allCoordinatesY.add(y2);			
						
		}
		
		// Obtaining individual edge coordinates
		
		allCoordinatesX.sort(null);
		allCoordinatesY.sort(null);
		minX = allCoordinatesX.get(0);
		maxX = allCoordinatesX.get(allCoordinatesX.size() - 1);
		minY = allCoordinatesY.get(0);
		maxY = allCoordinatesY.get(allCoordinatesY.size() - 1);
		
		// Obtaining a "centre" coordinate based on rectangular constructed out of the edge coordinates
		
		int centerX = (maxX + minX) / 2;
		int centerY = (minY + maxY) / 2;
		
		// Calculating true extreme points, i.e. the closest to the edges of the rectangular 
		
		double minDistanceTopLeft = originalImage.rows();
		double minDistanceTopRight = originalImage.rows();
		double minDistanceBottomLeft = originalImage.rows();
		double minDistanceBottomRight = originalImage.rows();		
		double conversionX = 210.0  / originalImage.cols();
		double conversionY = 297.0 / originalImage.rows();
		int p1X = 0;
		int p1Y = 0;
		int p2X = 0;
		int p2Y = 0;
		int p3X = 0;
		int p3Y = 0;
		int p4X = 0;
		int p4Y = 0;
		
		// Looping through each of the detected "lines" again
		
		for(int i = 0; i < lines.total(); i++) {
			
			x1 = (int)lines.get(i, 0)[0];
			y1 = (int)lines.get(i, 0)[1];
			x2 = (int)lines.get(i, 0)[2];
			y2 = (int)lines.get(i, 0)[3];		

			// Getting a Top left coordinate
			
			distance1 = Math.sqrt(Math.pow((x1 - minX),2) + Math.pow((y1 - minY),2));
			distance2 = Math.sqrt(Math.pow((x2 - minX),2) + Math.pow((y2 - minY),2));
			
			if(distance2 >= distance1 && distance1 < minDistanceTopLeft ) {				
				p1X = x1;
				p1Y = y1;
				minDistanceTopLeft = distance1;
			}
			else if(distance1 > distance2 && distance2 < minDistanceTopLeft ) {				
				p1X = x2;
				p1Y = y2;
				minDistanceTopLeft = distance2;
			}
			
			// Getting a Top right coordinate
			
			distance3 = Math.sqrt(Math.pow((x1 - maxX),2) + Math.pow((y1 - minY),2));
			distance4 = Math.sqrt(Math.pow((x2 - maxX),2) + Math.pow((y2 - minY),2));
			
			if(distance4 >= distance3 && distance3 < minDistanceTopRight ) {				
				p2X = x1;
				p2Y = y1;
				minDistanceTopRight = distance1;
			}
			else if(distance3 > distance4 && distance4 < minDistanceTopRight ) {			
				p2X = x2;
				p2Y = y2;
				minDistanceTopRight = distance2;
			}			
			
			// Getting a Bottom left coordinate
			
			distance5 = Math.sqrt(Math.pow((x1 - minX),2) + Math.pow((y1 - maxY),2));
			distance6 = Math.sqrt(Math.pow((x2 - minX),2) + Math.pow((y2 - maxY),2));
			
			if(distance6 >= distance5 && distance5 < minDistanceBottomLeft) {				
				p3X = x1;
				p3Y = y1;
				minDistanceBottomLeft = distance5;
			}
			else if(distance5 > distance6 && distance6 < minDistanceBottomLeft ) {			
				p3X = x2;
				p3Y = y2;
				minDistanceBottomLeft = distance6;
			}
			
			// Getting a Bottom right coordinate
			
			distance7 = Math.sqrt(Math.pow((x1 - maxX),2) + Math.pow((y1 - maxY),2));
			distance8 = Math.sqrt(Math.pow((x2 - maxX),2) + Math.pow((y2 - maxY),2));
			
			if(distance8 >= distance7 && distance7 < minDistanceBottomRight) {
				p4X = x1;
				p4Y = y1;
				minDistanceBottomRight = distance7;
			}
			else if(distance7 > distance8 && distance8 < minDistanceBottomRight ) {			
				p4X = x2;
				p4Y = y2;
				minDistanceBottomRight = distance8;
			}
		}
		
		// Drawing circles to indicate extreme points constructed from extreme individual coordinates	
		
		Imgproc.circle(originalImageC, new Point(minX, minY), 10, new Scalar(0,0,255), 20);
		Imgproc.circle(originalImageC, new Point(maxX, minY), 10, new Scalar(0,0,255), 20);
		Imgproc.circle(originalImageC, new Point(minX, maxY), 10, new Scalar(0,0,255), 20);
		Imgproc.circle(originalImageC, new Point(maxX, maxY), 10, new Scalar(0,0,255), 20);
		
		// Drawing a rectangle containing the shape of interest based on extreme points 
				
		Imgproc.rectangle(originalImageC, new Point(minX, minY), new Point(maxX, maxY), new Scalar(255,255,255), 20);
		
		// Drawing a circle to indicate the centre of the rectangle containing the shape of interest 
		
		Imgproc.circle(originalImageC, new Point(centerX, centerY), 10, new Scalar(255,0,0), 10);
		
		// Drawing circles to indicate border points of the shape od interest 
		
		Imgproc.circle(originalImageC, new Point(p1X, p1Y), 10, new Scalar(0,255,0), 10);
		Imgproc.circle(originalImageC, new Point(p2X, p2Y), 10, new Scalar(0,255,0), 10);
		Imgproc.circle(originalImageC, new Point(p3X, p3Y), 10, new Scalar(0,255,0), 10);
		Imgproc.circle(originalImageC, new Point(p4X, p4Y), 10, new Scalar(0,255,0), 10);
		
		// Drawing labels with attached coordinates of the border points in pixels 
		
		Imgproc.putText(originalImageC,String.valueOf(p1X) + " " + String.valueOf(p1Y), new Point(p1X - 100, p1Y - 100), 0, 2, new Scalar(0,0,255), 5); 
		Imgproc.putText(originalImageC,String.valueOf(p2X) + " " + String.valueOf(p2Y), new Point(p2X + 100, p2Y - 100), 0, 2, new Scalar(0,0,255), 5); 
		Imgproc.putText(originalImageC,String.valueOf(p3X) + " " + String.valueOf(p3Y), new Point(p3X - 100, p3Y + 100), 0, 2, new Scalar(0,0,255), 5); 
		Imgproc.putText(originalImageC,String.valueOf(p4X) + " " + String.valueOf(p4Y), new Point(p4X + 100, p4Y + 100), 0, 2, new Scalar(0,0,255), 5); 
				
		// Drawing labels with attached coordinates of the border points in millimeters 
		
		Imgproc.putText(originalImageC,"x = " + String.valueOf(Math.round(p1X * conversionX)) + " mm " + "y = " +  String.valueOf(Math.round(p1Y * conversionY)) + " mm ", new Point(p1X - 200, p1Y - 200), 0, 2, new Scalar(0,0,255), 5); 
		Imgproc.putText(originalImageC,"x = " + String.valueOf(Math.round(p2X * conversionX)) + " mm " + "y = " + String.valueOf(Math.round(p2Y * conversionY)) + " mm ", new Point(p2X + 200, p2Y - 200), 0, 2, new Scalar(0,0,255), 5); 
		Imgproc.putText(originalImageC,"x = " + String.valueOf(Math.round(p3X * conversionX)) + " mm " + "y = " + String.valueOf(Math.round(p3Y * conversionY)) + " mm ", new Point(p3X - 200, p3Y + 200), 0, 2, new Scalar(0,0,255), 5); 
		Imgproc.putText(originalImageC,"x = " + String.valueOf(Math.round(p4X * conversionX)) + " mm " + "y = " + String.valueOf(Math.round(p4Y * conversionY)) + " mm ", new Point(p4X + 200, p4Y + 200), 0, 2, new Scalar(0,0,255), 5); 
		
		// Adding borders to the original image
		
		Core.copyMakeBorder(originalImageC, originalImageC, 20, 20, 20, 20, Core.BORDER_CONSTANT);
				
		// Creating output 
		
		MatOfByte matOfByte = new MatOfByte();
		
		imgC.imencode(".jpg", originalImageC, matOfByte);
				
		byte[] byteArray = matOfByte.toArray();
		
		InputStream in = new ByteArrayInputStream(byteArray);
		
		BufferedImage bufImg = ImageIO.read(in);
		
		WritableImage writableImage = SwingFXUtils.toFXImage(bufImg, null);		
		
		return writableImage;		
		
	}	
	
	public static void main(String args[]) {
		
		launch(args);
		
	}
	
	

}
