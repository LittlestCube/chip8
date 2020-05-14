// yeah, yeah, this file is called Bitmap.java but it really handles all graphics

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import javax.swing.ImageIcon;

import java.awt.Color;
import java.awt.FlowLayout;

import java.awt.image.BufferedImage;

class Bitmap
{
	JFrame frame = new JFrame();
	
	int w = 64;
	int h = 32;
	int scale = 16;
	
	JLabel item;
	
	final byte WHITE = (byte) 255;
	final byte BLACK = (byte) 0;
	
	BufferedImage display = new BufferedImage(w * scale, h * scale, BufferedImage.TYPE_INT_RGB);
	
	Bitmap()
	{
		initDisplay();
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	void initDisplay()
	{
		item = new JLabel(new ImageIcon(display));
		
		frame.add(item);
		frame.pack();
		
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	void updateDisplay()
	{
		frame.repaint();
	}
	
	public void setPixels(boolean[] pix)
	{
		for (int x = 0; x < w; x++)
		{
			for (int y = 0; y < h; y++)
			{
				byte bytec = 0;
				
				if (pix[x + (w * y)])
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
}