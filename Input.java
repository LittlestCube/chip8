import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

class Input implements KeyListener
{
	boolean[] key;
	
	public static boolean stopCycle;
	
	Input()
	{
		key = new boolean[16];
		
		stopCycle = false;
	}
	
	public void keyPressed(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_1:
			{
				key[0x1] = true;
				break;
			}
			
			case KeyEvent.VK_2:
			{
				key[0x2] = true;
				break;
			}
			
			case KeyEvent.VK_3:
			{
				key[0x3] = true;
				break;
			}
			
			case KeyEvent.VK_Q:
			{
				key[0x4] = true;
				break;
			}
			
			case KeyEvent.VK_W:
			{
				key[0x5] = true;
				break;
			}
			
			case KeyEvent.VK_E:
			{
				key[0x6] = true;
				break;
			}
			
			case KeyEvent.VK_A:
			{
				key[0x7] = true;
				break;
			}
			
			case KeyEvent.VK_S:
			{
				key[0x8] = true;
				break;
			}
			
			case KeyEvent.VK_D:
			{
				key[0x9] = true;
				break;
			}
			
			case KeyEvent.VK_X:
			{
				key[0x0] = true;
				break;
			}
			
			case KeyEvent.VK_Z:
			{
				key[0xA] = true;
				break;
			}
			
			case KeyEvent.VK_C:
			{
				key[0xB] = true;
				break;
			}
			
			case KeyEvent.VK_4:
			{
				key[0xC] = true;
				break;
			}
			
			case KeyEvent.VK_R:
			{
				key[0xD] = true;
				break;
			}
			
			case KeyEvent.VK_F:
			{
				key[0xE] = true;
				break;
			}
			
			case KeyEvent.VK_V:
			{
				key[0xF] = true;
				break;
			}
			
			case KeyEvent.VK_SPACE:
			{
				stopCycle ^= true;
				break;
			}
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_1:
			{
				key[0x1] = false;
				break;
			}
			
			case KeyEvent.VK_2:
			{
				key[0x2] = false;
				break;
			}
			
			case KeyEvent.VK_3:
			{
				key[0x3] = false;
				break;
			}
			
			case KeyEvent.VK_Q:
			{
				key[0x4] = false;
				break;
			}
			
			case KeyEvent.VK_W:
			{
				key[0x5] = false;
				break;
			}
			
			case KeyEvent.VK_E:
			{
				key[0x6] = false;
				break;
			}
			
			case KeyEvent.VK_A:
			{
				key[0x7] = false;
				break;
			}
			
			case KeyEvent.VK_S:
			{
				key[0x8] = false;
				break;
			}
			
			case KeyEvent.VK_D:
			{
				key[0x9] = false;
				break;
			}
			
			case KeyEvent.VK_X:
			{
				key[0x0] = false;
				break;
			}
			
			case KeyEvent.VK_Z:
			{
				key[0xA] = false;
				break;
			}
			
			case KeyEvent.VK_C:
			{
				key[0xB] = false;
				break;
			}
			
			case KeyEvent.VK_4:
			{
				key[0xC] = false;
				break;
			}
			
			case KeyEvent.VK_R:
			{
				key[0xD] = false;
				break;
			}
			
			case KeyEvent.VK_F:
			{
				key[0xE] = false;
				break;
			}
			
			case KeyEvent.VK_V:
			{
				key[0xF] = false;
				break;
			}
		}
	}
	
	public void keyTyped(KeyEvent e) {}			// get the compiler to stop nagging
}