public class Chip8
{
	public static boolean debug = false;
	
	static CPU cpu;
	static Netlink netlink;
	
	public static void main(String args[]) throws Exception
	{
		cpu = new CPU();
		netlink = new Netlink();
		
		while (true)
		{
			if (netlink.connected != Netlink.CLIENT)
			{
				// check to see if user actually loaded game
				if (args.length > 0)
				{
					cpu.loadGame(args[0]);
				}
				
				if (debug)
				{
					cpu.bitmap.debugWindow();
					cpu.bitmap.debugFrame.setVisible(true);
				}
				
				while (true)
				{
					if (cpu.bitmap.gamePath != "")
					{
						cpu.loadGame(cpu.bitmap.gamePath);
					}
					
					if (!cpu.input.stopCycle)
					{
						cpu.cycle();
					}
					
					if (cpu.drawFlag)
					{
						cpu.setPixels();
						
						if (debug)
						{
							cpu.bitmap.debugRender();
						}
						
						if (netlink.connected == Netlink.SERVER)
						{
							netlink.sendGfx = cpu.bitmap.gfx;
						}
						
						cpu.drawFlag = false;
					}
					
					Thread.sleep(1);
					
					if (debug)
					{
						String debugString = "";
						
						for (int i = 0; i < cpu.V.length; i++)
						{
							debugString += "V" + i + ":\t  0x" + Integer.toHexString(cpu.V[i].get()) + "\n";
						}
						
						debugString += "current opcode: 0x" + Integer.toHexString(cpu.opcode) + "\n" + "pc: 0x" + Integer.toHexString(cpu.pc) + "\n" + "stack[sp]: 0x" + Integer.toHexString(cpu.stack[cpu.sp].get()) + "\n" + "sp: 0x" + Integer.toHexString(cpu.sp) + "\n";
						debugString += "I: 0x" + Integer.toHexString(cpu.I.get()) + "\n" + "delay_timer: 0x" + Integer.toHexString(cpu.delay_timer.get()) + "\n" + "sound_timer: 0x" + Integer.toHexString(cpu.sound_timer.get()) + "\n";
						
						cpu.bitmap.debugDisplay.setText(debugString);
					}
				}
			}
			
			else
			{
				while (true)
				{
					if (netlink.receiveTrueKeyValue != -1)
					{
						cpu.input.key[netlink.receiveTrueKeyValue] = true;
						
						netlink.receiveTrueKeyValue = -1;
					}
					
					if (netlink.receiveFalseKeyValue != -1)
					{
						cpu.input.key[netlink.receiveFalseKeyValue] = false;
						
						netlink.receiveFalseKeyValue = -1;
					}
					
					if (netlink.receiveSoundValue != -1)
					{
						cpu.sound_timer.set(netlink.receiveSoundValue);
						
						netlink.receiveSoundValue = -1;
					}
					
					if (netlink.receiveGfx != null)
					{
						cpu.bitmap.gfx = netlink.receiveGfx;
						cpu.setPixels();
						netlink.receiveGfx = null;
					}
				}
			}
		}
	}
}