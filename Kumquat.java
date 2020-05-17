public class Kumquat
{
	public static void main(String args[]) throws Exception
	{
		CPU cpu = new CPU();
		
		// check to see if user actually loaded game (comment out until more is done)
//		if (args.length > 0)
//		{
			cpu.loadGame("blinky.ch8");
/*		}
		else
		{
			System.err.println("E: No game loaded!");
			System.exit(1);
		}
*/		
		// actual emulation loop
		
		cpu.bitmap.debugWindow();
		cpu.bitmap.debugFrame.setVisible(true);
		
		while (true)
		{
			if (cpu.input.stopCycle)
			{
				continue;
			}
			
			cpu.cycle();
			
			if (cpu.drawFlag)
			{
				cpu.setPixels();
//				cpu.bitmap.debugRender();
				cpu.drawFlag = false;
			}
			
			cpu.setKeys();
			
			Thread.sleep(0);
			
			String debugString = "";
			
			for (int i = 0; i < cpu.V.length; i++)
			{
				debugString += "V" + i + ":\t  " + (cpu.V[i].get()) + "\n";
			}
			
			debugString += "current opcode: 0x" + Integer.toHexString(cpu.opcode) + "\n" + "pc: 0x" + Integer.toHexString(cpu.pc) + "\n" + "stack[sp]: 0x" + Integer.toHexString(cpu.stack[cpu.sp].get()) + "\n" + "sp: 0x" + Integer.toHexString(cpu.sp) + "\n";
			debugString += "I: 0x" + Integer.toHexString(cpu.I.get()) + "\n" + "delay_timer: 0x" + Integer.toHexString(cpu.delay_timer.get()) + "\n" + "sound_timer: 0x" + Integer.toHexString(cpu.sound_timer.get()) + "\n";
			
			cpu.bitmap.debugDisplay.setText(debugString);
		}
	}
}