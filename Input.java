import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

class Input extends Chip8 implements KeyListener
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
				
				netKeyTrue(0x1);
				
				break;
			}
			
			case KeyEvent.VK_2:
			{
				key[0x2] = true;
				
				netKeyTrue(0x2);
				
				break;
			}
			
			case KeyEvent.VK_3:
			{
				key[0x3] = true;
				
				netKeyTrue(0x3);
				
				break;
			}
			
			case KeyEvent.VK_Q:
			{
				key[0x4] = true;
				
				netKeyTrue(0x4);
				
				break;
			}
			
			case KeyEvent.VK_W:
			{
				key[0x5] = true;
				
				netKeyTrue(0x5);
				
				break;
			}
			
			case KeyEvent.VK_E:
			{
				key[0x6] = true;
				
				netKeyTrue(0x6);
				
				break;
			}
			
			case KeyEvent.VK_A:
			{
				key[0x7] = true;
				
				netKeyTrue(0x7);
				
				break;
			}
			
			case KeyEvent.VK_S:
			{
				key[0x8] = true;
				
				netKeyTrue(0x8);
				
				break;
			}
			
			case KeyEvent.VK_D:
			{
				key[0x9] = true;
				
				netKeyTrue(0x9);
			
				break;
			}
			
			case KeyEvent.VK_X:
			{
				key[0x0] = true;
				
				netKeyTrue(0x0);
				
				break;
			}
			
			case KeyEvent.VK_Z:
			{
				key[0xA] = true;
				
				netKeyTrue(0xA);
				
				break;
			}
			
			case KeyEvent.VK_C:
			{
				key[0xB] = true;
				
				netKeyTrue(0xB);
			
				break;
			}
			
			case KeyEvent.VK_4:
			{
				key[0xC] = true;
				
				netKeyTrue(0xC);
				
				break;
			}
			
			case KeyEvent.VK_R:
			{
				key[0xD] = true;
				
				netKeyTrue(0xD);
			
				break;
			}
			
			case KeyEvent.VK_F:
			{
				key[0xE] = true;
				
				netKeyTrue(0xE);
				
				break;
			}
			
			case KeyEvent.VK_V:
			{
				key[0xF] = true;
				
				netKeyTrue(0xF);
				
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
				
				netKeyFalse(0x1);
				
				break;
			}
			
			case KeyEvent.VK_2:
			{
				key[0x2] = false;
				
				netKeyFalse(0x2);
				
				break;
			}
			
			case KeyEvent.VK_3:
			{
				key[0x3] = false;
				
				netKeyFalse(0x3);
				
				break;
			}
			
			case KeyEvent.VK_Q:
			{
				key[0x4] = false;
				
				netKeyFalse(0x4);
				
				break;
			}
			
			case KeyEvent.VK_W:
			{
				key[0x5] = false;
				
				netKeyFalse(0x5);
				
				break;
			}
			
			case KeyEvent.VK_E:
			{
				key[0x6] = false;
				
				netKeyFalse(0x6);
				
				break;
			}
			
			case KeyEvent.VK_A:
			{
				key[0x7] = false;
				
				netKeyFalse(0x7);
				
				break;
			}
			
			case KeyEvent.VK_S:
			{
				key[0x8] = false;
				
				netKeyFalse(0x8);
				
				break;
			}
			
			case KeyEvent.VK_D:
			{
				key[0x9] = false;
				
				netKeyFalse(0x9);
			
				break;
			}
			
			case KeyEvent.VK_X:
			{
				key[0x0] = false;
				
				netKeyFalse(0x0);
				
				break;
			}
			
			case KeyEvent.VK_Z:
			{
				key[0xA] = false;
				
				netKeyFalse(0xA);
				
				break;
			}
			
			case KeyEvent.VK_C:
			{
				key[0xB] = false;
				
				netKeyFalse(0xB);
			
				break;
			}
			
			case KeyEvent.VK_4:
			{
				key[0xC] = false;
				
				netKeyFalse(0xC);
				
				break;
			}
			
			case KeyEvent.VK_R:
			{
				key[0xD] = false;
				
				netKeyFalse(0xD);
			
				break;
			}
			
			case KeyEvent.VK_F:
			{
				key[0xE] = false;
				
				netKeyFalse(0xE);
				
				break;
			}
			
			case KeyEvent.VK_V:
			{
				key[0xF] = false;
				
				netKeyFalse(0xF);
				
				break;
			}
		}
	}
	
	void netKeyTrue(int keyIndex)
	{
		if (netlink.connected != 0)
		{
			netlink.sendTrueKeyValue = keyIndex;
		}
	}
	
	void netKeyFalse(int keyIndex)
	{
		if (netlink.connected != 0)
		{
			netlink.sendFalseKeyValue = keyIndex;
		}
	}
	
	public void keyTyped(KeyEvent e) {}			// get the compiler to stop nagging
}