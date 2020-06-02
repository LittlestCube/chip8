// yeah, yeah, this file is called Bitmap.java but it really handles all graphics

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import javax.swing.filechooser.*;

import java.awt.Color;
import java.awt.FlowLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import java.awt.image.BufferedImage;

class Bitmap extends Chip8 implements ActionListener
{
	JFrame frame;
	JFrame debugFrame;
	
	JFrame netFrame;
	JTextField address;
	JButton ok;
	int netlinkPromptStatus;
	
	JFileChooser fc;
	String gamePath;
	
	JMenuBar bar;
	JMenu file;
	JMenuItem open;
	JMenuItem stop;
	JMenuItem exit;
	JMenu netmenu;
	JMenuItem netserver;
	JMenuItem netclient;
	
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
		
		frame = new JFrame("Chip 8");
		debugFrame = new JFrame("Debugger");
		
		fc = new JFileChooser();
		gamePath = "";
		
		bar = new JMenuBar();
		file = new JMenu("File");
		open = new JMenuItem("Open ROM");
		stop = new JMenuItem("Stop Emulation");
		exit = new JMenuItem("Exit");
		
		netmenu = new JMenu("Netlink");
		netserver = new JMenuItem("Start Server");
		netclient = new JMenuItem("Connect to Server");
		netlinkPromptStatus = 0;
		
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
		bar.add(netmenu);
		netmenu.add(netserver);
		netmenu.add(netclient);
		netserver.addActionListener(this);
		netclient.addActionListener(this);
		frame.pack();
		
		frame.setResizable(false);
		frame.setVisible(true);
		
		gfx = new boolean[64 * 32];
		
		WHITE = (byte) 255;
		BLACK = (byte) 0;
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	void netlinkWindow()
	{
		JPanel panel = new JPanel();
		
		address = new javax.swing.JTextField();
		ok = new javax.swing.JButton();
		
		address.setText("localhost");
		address.setPreferredSize(new java.awt.Dimension(82, 26));
		address.setSize(new java.awt.Dimension(82, 26));
		address.addActionListener(this);
		ok.addActionListener(this);
		
		ok.setText("OK");
		
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addGroup(layout.createSequentialGroup()
						.addGap(53, 53, 53)
						.addComponent(address, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
					.addGroup(layout.createSequentialGroup()
						.addGap(72, 72, 72)
						.addComponent(ok)))
				.addContainerGap(53, Short.MAX_VALUE))
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
				.addComponent(ok)
				.addContainerGap())
		);
		
		netFrame = new JFrame();
		netFrame.add(panel);
		netFrame.pack();
		netFrame.setVisible(true);
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
		
		if (src == netserver)
		{
			netlink.initAsServer();
			JOptionPane.showMessageDialog(frame, "Successfully started Netlink Server!");
		}
		
		if (src == netclient)
		{
			netlinkWindow();
		}
		
		if (src == ok)
		{
			netlink.initAsClient(address.getText());
			
			netFrame.dispatchEvent(new WindowEvent(netFrame, WindowEvent.WINDOW_CLOSING));
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