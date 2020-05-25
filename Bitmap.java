// yeah, yeah, this file is called Bitmap.java but it really handles all graphics

import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import javax.swing.filechooser.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.Color;
import java.awt.FlowLayout;

import java.awt.image.BufferedImage;

class Bitmap implements ActionListener
{
	JFrame frame;
	JFrame debugFrame;
	
	JFileChooser fc;
	String gamePath;
	
	JMenuBar bar;
	JMenu file;
	JMenuItem open;
	JMenuItem exit;
	
	JTextArea debugDisplay;
	
	int w;
	int h;
	int scale;
	
	boolean[] gfx;
	
	JLabel item;
	
	final byte WHITE;
	final byte BLACK;
	
	BufferedImage display;
	
	Bitmap()
	{
		w = 64;
		h = 32;
		scale = 16;
		
		frame = new JFrame();
		debugFrame = new JFrame();
		
		fc = new JFileChooser();
		gamePath = "";
		
		bar = new JMenuBar();
		file = new JMenu("File");
		open = new JMenuItem("Open ROM");
		exit = new JMenuItem("Exit");
		
		debugDisplay = new JTextArea(30, 20);
		
		display = new BufferedImage(w * scale, h * scale, BufferedImage.TYPE_INT_RGB);
		
		item = new JLabel(new ImageIcon(display));
		
		frame.add(item);
		frame.setJMenuBar(bar);
		bar.add(file);
		file.add(open);
		file.addSeparator();
		file.add(exit);
		open.addActionListener(this);
		exit.addActionListener(this);
		frame.pack();
		
		frame.setResizable(false);
		frame.setVisible(true);
		
		gfx = new boolean[64 * 32];
		
		WHITE = (byte) 255;
		BLACK = (byte) 0;
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		
		if (src == open)
		{
			SwingUtilities.updateComponentTreeUI(fc);
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int fci = fc.showOpenDialog(null);
			
			if (fci == JFileChooser.APPROVE_OPTION) {
				gamePath = fc.getSelectedFile().getAbsolutePath();
			}
		}
		
		if (src == exit)
		{
			System.exit(0);
		}
	}
	
	void updateDisplay()
	{
		frame.repaint();
	}
	
	boolean setPixel(int x, int y)
	{
		int location = (x + (w * y)) % 2048;
		
		gfx[location] ^= true;
		
		return gfx[location];			// returns true if no collision
	}
	
	public void setPixels()
	{
		for (int x = 0; x < w; x++)
		{
			for (int y = 0; y < h; y++)
			{
				byte bytec = 0;
				
				if (gfx[x + (w * y)])
				{
					bytec = WHITE;
				}
				else
				{
					bytec = BLACK;
				}
				
				for (int i = 0; i < scale; i++)
				{
					for (int j = 0; j < scale; j++)
					{
						display.setRGB(x * scale + i, y * scale + j, bytec);
					}
				}
			}
		}
	}
	
	void debugWindow()
	{
		debugFrame.add(debugDisplay);
		debugFrame.pack();
		debugFrame.setTitle("Debugger");
	}
	
	void debugRender()
	{
		// Draw
		for(int y = 0; y < h; y++)
		{
			for(int x = 0; x < w; x++)
			{
				if(gfx[(y * w) + x]) 
					System.out.print("O");
				else 
					System.out.print(" ");
			}
			System.out.print("\n");
		}
	}
}