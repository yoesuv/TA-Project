package proses;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Crop {

	private static String path;
	
	public Crop(){
		super();
	}
	
	public void Run(String inFile,String TempFile){
		
		path = new JFileChooser().getFileSystemView().getDefaultDirectory().toString()+File.separator;
		File f = new File(path+"crop");
		f.mkdir();
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat img = Highgui.imread(inFile);
		Mat temp = Highgui.imread(TempFile);
		
		int resultCols = img.cols() - temp.cols()-1;
		int resultRows = img.rows() - temp.rows()-1;
		Mat result = new Mat(resultRows, resultCols, CvType.CV_32FC1);
		
		Imgproc.matchTemplate(img, temp, result, Imgproc.TM_CCOEFF_NORMED);
		Core.normalize(result, result,0,1,Core.NORM_MINMAX,-1,new Mat());
		
		MinMaxLocResult mmr = Core.minMaxLoc(result);		
		Point matcLock = mmr.maxLoc;
		Highgui.imwrite(path+"crop"+File.separator+"CC.jpg", img);
		
		// Crop image
		Mat imgtoCrop = Highgui.imread(path+"crop"+File.separator+"CC.jpg");
		Rect roi = new Rect((int)matcLock.x, (int)matcLock.y,temp.cols(),temp.rows());
		Mat imgCrop = new Mat(imgtoCrop, roi);
		Highgui.imwrite(path+"crop"+File.separator+"HasilCrop.jpg", imgCrop);
	}
	
	public void tampilinCropnya(JLabel lbl){
		try {
			BufferedImage bfrImg = ImageIO.read(new File(path+"crop"+File.separator+"HasilCrop.jpg"));
			ImageIcon iic = new ImageIcon(bfrImg);
			lbl.setIcon(iic);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
