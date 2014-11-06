package gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JButton;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JTextField;

import proses.Crop;
import proses.Inputan;
import proses.Matching;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;


public class F_MainMatching {

	public JFrame frameMatching;
	private JLabel lblInput,lblTemplate,lblResult;
	private JButton btnOpenInput,btnCrop,btnOpenTemplate,btnMatching,btnReset;
	private JFileChooser jfc;
	private File fileIn,fileTemp;
	private BufferedImage bfrImg;
	private ImageIcon imgIco;
	private Dimension dim;
	private JTextField text_X,text_Y,text_Batas;

	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					F_MainMatching window = new F_MainMatching();
					window.frameMatching.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public F_MainMatching() {
		initialize();
	}
	
	private void initialize() {
		frameMatching = new JFrame();
		frameMatching.setTitle("Template Matching");
		frameMatching.setResizable(false);
		frameMatching.setBounds(100, 100, 950, 600);
		frameMatching.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameMatching.getContentPane().setLayout(null);
		
		lblInput = new JLabel(new ImageIcon("")){
			private static final long serialVersionUID = 2923322145833656628L;
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				g.drawImage(((ImageIcon)getIcon()).getImage(), 0, 0, getWidth(),getHeight(),null);
			}
		};
		lblInput.setBounds(10, 32, 299, 422);
		frameMatching.getContentPane().add(lblInput);
		
		JLabel labelInput = new JLabel("INPUT");
		labelInput.setBounds(119, 11, 46, 14);
		frameMatching.getContentPane().add(labelInput);
		
		JLabel labelTemplate = new JLabel("TEMPLATE");
		labelTemplate.setBounds(429, 11, 66, 14);
		frameMatching.getContentPane().add(labelTemplate);
		
		btnOpenInput = new JButton("OPEN INPUT");
		btnOpenInput.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnOpenInput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Open Input Image
				jfc = new JFileChooser();
				jfc.setDialogTitle("Open Input Image");
				jfc.setMultiSelectionEnabled(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("File JPG", "jpg");
				jfc.setFileFilter(filter);
				if(jfc.showOpenDialog(frameMatching)==JFileChooser.APPROVE_OPTION){
					fileIn = jfc.getSelectedFile();
					try {
						bfrImg = ImageIO.read(fileIn);
						imgIco = new ImageIcon(bfrImg);
						lblInput.setIcon(imgIco);						
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		btnOpenInput.setBounds(10, 465, 108, 37);
		frameMatching.getContentPane().add(btnOpenInput);
		
		lblTemplate = new JLabel(new ImageIcon("")) {
			private static final long serialVersionUID = 6327336942162421802L;
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(((ImageIcon)getIcon()).getImage(), 0, 0, getWidth(),getHeight(),null);
			}
		};
		lblTemplate.setBounds(319, 32, 299, 422);
		frameMatching.getContentPane().add(lblTemplate);
		
		btnCrop = new JButton("CROP");
		btnCrop.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnCrop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int ask = JOptionPane.showConfirmDialog(frameMatching, "Crop Input Image ?", "CROP", JOptionPane.YES_NO_OPTION);
				if(ask==JOptionPane.YES_OPTION){
					String lokasi = new JFileChooser().getFileSystemView().getDefaultDirectory().toString()+File.separator+"crop"+File.separator+"HasilCrop.jpg";
					//System.out.println(lokasi);
					Crop c = new Crop();
					c.Run(fileIn.getAbsolutePath(), fileTemp.getAbsolutePath());
					c.tampilinCropnya(lblInput);
					fileIn = new File(lokasi);
				}
			}
		});
		btnCrop.setBounds(147, 465, 108, 37);
		frameMatching.getContentPane().add(btnCrop);
		
		btnOpenTemplate = new JButton("OPEN TEMPLATE");
		btnOpenTemplate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Open Template Image
				jfc = new JFileChooser();
				jfc.setDialogTitle("Open Template Image");
				jfc.setMultiSelectionEnabled(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("File JPG", "jpg");
				jfc.setFileFilter(filter);
				if(jfc.showOpenDialog(frameMatching)==JFileChooser.APPROVE_OPTION){
					fileTemp = jfc.getSelectedFile();
					try {
						bfrImg = ImageIO.read(fileTemp);
						imgIco = new ImageIcon(bfrImg);
						lblTemplate.setIcon(imgIco);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		btnOpenTemplate.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnOpenTemplate.setBounds(319, 465, 128, 37);
		frameMatching.getContentPane().add(btnOpenTemplate);
		
		lblResult = new JLabel(new ImageIcon("")) {
			private static final long serialVersionUID = 3068259127499142566L;
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(((ImageIcon)getIcon()).getImage(), 0, 0, getWidth(),getHeight(),null);
			}
		};
		lblResult.setBounds(628, 32, 299, 422);
		frameMatching.getContentPane().add(lblResult);
		
		btnMatching = new JButton("MATCHING");
		btnMatching.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnMatching.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(fileIn==null){
					JOptionPane.showMessageDialog(frameMatching, "Image Input Kosong !", "ERROR", JOptionPane.ERROR_MESSAGE);
				}else if(fileTemp==null){
					JOptionPane.showMessageDialog(frameMatching, "Image Template Kosong !", "ERROR", JOptionPane.ERROR_MESSAGE);
				}else{
					//Proses Matching
					Inputan i = new Inputan();
					i.setInput(fileIn.getAbsolutePath());
					i.setTemplate(fileTemp.getAbsolutePath());
					i.setX(Integer.parseInt(text_X.getText()));
					i.setY(Integer.parseInt(text_Y.getText()));
					i.setBatas(Double.parseDouble(text_Batas.getText()));
					
					Matching m = new Matching(i.getInput(), i.getTemplate());
					m.setParam(i.getX(), i.getY(), i.getBatas());
					m.Compare();
					
					Matching m2 = new Matching(frameMatching, lblResult);
					m2.Tampilin();
				}	
			}
		});
		btnMatching.setBounds(628, 465, 128, 37);
		frameMatching.getContentPane().add(btnMatching);
		
		btnReset = new JButton("RESET");
		btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int ask = JOptionPane.showConfirmDialog(frameMatching, "Reset Proses ?", "RESET", JOptionPane.YES_NO_OPTION);
				if(ask==JOptionPane.YES_OPTION){
					ResetMatching();
				}
			}
		});
		btnReset.setBounds(766, 465, 128, 37);
		frameMatching.getContentPane().add(btnReset);
		
		JPanel panel_param = new JPanel();
		panel_param.setBorder(new TitledBorder(null, "Parameter", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_param.setBounds(322, 510, 299, 52);
		frameMatching.getContentPane().add(panel_param);
		panel_param.setLayout(null);
		
		JLabel lblX = new JLabel("X");
		lblX.setBounds(13, 23, 27, 16);
		panel_param.add(lblX);
		
		text_X = new JTextField();
		text_X.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent v){
				if(v.getKeyCode()==KeyEvent.VK_ENTER){
					text_Y.requestFocus();
				}
			}
		});
		text_X.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				text_X.setBackground(Color.YELLOW);
			}
			public void focusLost(FocusEvent ev){
				text_X.setBackground(Color.WHITE);
			}
		});
		text_X.setBounds(25, 21, 54, 20);
		panel_param.add(text_X);
		text_X.setColumns(10);
		
		JLabel lblY = new JLabel("Y");
		lblY.setBounds(89, 24, 27, 16);
		panel_param.add(lblY);
		
		text_Y = new JTextField();
		text_Y.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent v){
				if(v.getKeyCode()==KeyEvent.VK_ENTER){
					text_Batas.requestFocus();
				}
			}
		});
		text_Y.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				text_Y.setBackground(Color.YELLOW);
			}
			public void focusLost(FocusEvent ev){
				text_Y.setBackground(Color.WHITE);
			}
		});
		text_Y.setColumns(10);
		text_Y.setBounds(102, 21, 54, 20);
		panel_param.add(text_Y);
		
		JLabel lblBatas = new JLabel("Batas");
		lblBatas.setBounds(166, 24, 44, 16);
		panel_param.add(lblBatas);
		
		text_Batas = new JTextField();
		text_Batas.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent v){
				if(v.getKeyCode()==KeyEvent.VK_ENTER){
					text_X.requestFocus();
				}
			}
		});
		text_Batas.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				text_Batas.setBackground(Color.YELLOW);
			}
			public void focusLost(FocusEvent ev){
				text_Batas.setBackground(Color.WHITE);
			}
		});
		text_Batas.setColumns(10);
		text_Batas.setBounds(200, 21, 78, 20);
		panel_param.add(text_Batas);
		
		Tengah();
		ResetMatching();
		setParam();
	}
	
	private void Tengah(){
		dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = frameMatching.getSize().width;
		int h = frameMatching.getSize().height;
		int x = (dim.width-w)/2;
		int y = (dim.height-h)/2;
		frameMatching.setLocation(x, y);
	}
	
	private void ResetMatching(){
		String lokasi;
		ImageIcon ico;
		URL url;
		
		lokasi = File.separator+"input.jpg";
		url = F_MainMatching.class.getResource(lokasi);
		ico = new ImageIcon(url);
		lblInput.setIcon(ico);
		
		lokasi = File.separator+"template.jpg";
		url = F_MainMatching.class.getResource(lokasi);
		ico = new ImageIcon(url);
		lblTemplate.setIcon(ico);
		
		lokasi = File.separator+"result.jpg";
		url = F_MainMatching.class.getResource(lokasi);
		ico = new ImageIcon(url);
		lblResult.setIcon(ico);
		
		fileIn = null;
		fileTemp = null;
	}
	
	private void setParam(){
		text_X.setText("10");
		text_Y.setText("15");
		text_Batas.setText("0.99");
	}
}
