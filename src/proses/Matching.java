package proses;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;


public class Matching {
	
	private BufferedImage bfr1,bfr2,bfr3;
	private int x,y,cx,cy;
	private double batas;
	private static boolean match;
	private static String lokasi;
	
	private JLabel lbl;
	private JFrame fr;
	
	public Matching(JFrame f,JLabel lbl){
		super();
		this.fr = f;
		this.lbl = lbl;
	}
	
	public Matching(String input,String templ){
		this(loadJPG(input),loadJPG(templ));
	}
	
	public Matching(Image img1,Image img2){
		this(imgToBfr(img1),imgToBfr(img2));
	}
	
	public Matching(BufferedImage bfr1,BufferedImage bfr2){
		this.bfr1 = bfr1;
		this.bfr2 = bfr2;
	}

	/* Set Parameter */
	public void setParam(int x,int y,double b){
		this.x = x;
		this.y = y;
		this.batas = b;
	}
	
	/* Bandingkan Gambar*/
	public void Compare(){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		lokasi = new JFileChooser().getFileSystemView().getDefaultDirectory().toString()+File.separator;

		File f = new File(lokasi+"temp");
		f.mkdir();
		
		bfr3 = imgToBfr(bfr1);
		Graphics2D g2 = bfr3.createGraphics();
		g2.setColor(Color.RED);
		
		bfr1 = imgToBfr(GrayFilter.createDisabledImage(bfr1));
		bfr2 = imgToBfr(GrayFilter.createDisabledImage(bfr2));
		
		int blocksX = (int)bfr1.getWidth()/x;
		int blocksY = (int)bfr1.getHeight()/y;
		double korelasi = 0;
		
		match = true;
		
		for(cy=0;cy<y;cy++){
			for(cx=0;cx<x;cx++){
				saveJPG(bfr1.getSubimage(cx*blocksX, cy*blocksY, blocksX-1, blocksY-1), lokasi+"temp"+File.separator+"img1("+cx+","+cy+").jpg");
				saveJPG(bfr2.getSubimage(cx*blocksX, cy*blocksY, blocksX-1, blocksY-1), lokasi+"temp"+File.separator+"img2("+cx+","+cy+").jpg");
				korelasi = getKorelasi(lokasi+"temp"+File.separator+"img1("+cx+","+cy+").jpg",
						lokasi+"temp"+File.separator+"img2("+cx+","+cy+").jpg");
				
				if(korelasi<batas){
					float thickness = 4;
					Stroke st = g2.getStroke();
					g2.setStroke(new BasicStroke(thickness));
					g2.drawRect(cx*blocksX, cy*blocksY, blocksX-1, blocksY-1);
					g2.setStroke(st);
					match = false;
				}
				cetak(korelasi, cx, cy);
			}
			System.out.println("");
		}
		
		if(!match){
			saveJPG(getChangeIndicator(), lokasi+"Hasilnya.jpg");
		}
		DelDir(f);
	}
	
	/* Hapus Folder temp*/
	private static boolean DelDir(File dir){
		if(dir.isDirectory()){
			String[] children  = dir.list();
			for(int i=0;i<children.length;i++){
				boolean success = DelDir(new File(dir,children[i]));
				if(!success){
					return false;
				}
			}
		}
		return dir.delete();
	}
	
	/* Cetak Hasil */
	private BufferedImage getChangeIndicator(){
		return bfr3;
	}
	
	/* Match = true */
	public boolean isMatch(){
		return match;
	}
	
	/* Hitung Nilai Korelasi */
	private double getKorelasi(String f1,String f2){
		double kore=0;
		
		Mat m1 = Highgui.imread(f1);
		Mat m2 = Highgui.imread(f2);
		
		int rows = m1.rows()-m2.rows()+1;
		int cols = m1.cols()-m2.cols()+1;
		Mat result = new Mat(rows, cols, CvType.CV_32F);
		
		Imgproc.matchTemplate(m1, m2, result, Imgproc.TM_CCOEFF_NORMED);
		
		MinMaxLocResult mmr = Core.minMaxLoc(result);
		kore = mmr.minVal;
		
		return kore;
	}
	
	/* Image to BufferedImage */
	private static BufferedImage imgToBfr(Image img){
		BufferedImage bi = new BufferedImage(img.getWidth(null),img.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2 = bi.createGraphics();
		g2.drawImage(img, null, null);
		return bi;
	}
	
	/* Simpan sub Image*/
	private static void saveJPG(Image img,String nama){
		BufferedImage bi = imgToBfr(img);
		FileOutputStream fout=null;
		try {
			fout = new FileOutputStream(nama);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fout);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
		param.setQuality(0.9f, false);
		try {
			encoder.encode(bi);
			fout.close();
		} catch (ImageFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Load Gambar */
	private static Image loadJPG(String fileName){
		FileInputStream fis=null;
		BufferedImage bi=null;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		JPEGImageDecoder decode = JPEGCodec.createJPEGDecoder(fis);
		try {
			bi = decode.decodeAsBufferedImage();
			fis.close();
		} catch (ImageFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bi;
	}
	
	/* Tampilkan Hasil */
	public void Tampilin(){
		//System.out.println(match);
		if(match==true){
			JOptionPane.showMessageDialog(fr, "Gambar Sama","DONE",JOptionPane.INFORMATION_MESSAGE);
		}else{
			//System.out.println(lokasi);
			File f = new File(lokasi+"Hasilnya.jpg");
			try {
				BufferedImage bi = ImageIO.read(f);
				ImageIcon ico = new ImageIcon(bi);
				lbl.setIcon(ico);
				JOptionPane.showMessageDialog(fr, "Proses Selesai","DONE",JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	
	public void cetak(Double kr,int xx,int yy){
		String k = Double.toString(kr);
		String k2;
		if(k.length()>=6){
			k2 = k.substring(0, 6);
		}else{
			k2 = k;
		}
		System.out.print("Kor"+xx+","+yy+" : "+ k2+" ");
	}
}
