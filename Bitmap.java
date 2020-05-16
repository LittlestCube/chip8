// yeah, yeah, this file is called Bitmap.java but it really handles all graphics

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.ImageIcon;

import java.awt.Color;
import java.awt.FlowLayout;

import java.awt.image.BufferedImage;

class Bitmap
{
	JFrame frame = new JFrame();
	
	JFrame debugFrame = new JFrame();
	JTextArea debugDisplay = new JTextArea(30, 20);
	
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
		
		display = new BufferedImage(w * scale, h * scale, BufferedImage.TYPE_INT_RGB);
		
		item = new JLabel(new ImageIcon(display));
		
		frame.add(item);
		frame.pack();
		
		frame.setResizable(false);
		frame.setVisible(true);
		
		gfx = new boolean[64 * 32];
		
		WHITE = (byte) 255;
		BLACK = (byte) 0;
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	void updateDisplay()
	{
		frame.repaint();
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
		for(int y = 0; y < 32; y++)
		{
			for(int x = 0; x < 64; x++)
			{
				if(gfx[(y * 64) + x]) 
					System.out.print("O");
				else 
					System.out.print(" ");
			}
			System.out.print("\n");
		}
	}
}