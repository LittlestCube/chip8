public class Kumquat
{
	public static void main(String args[]) throws Exception
	{
		CPU cpu = new CPU();
		
		// check to see if user actually loaded game (comment out until more is done)
//		if (args.length > 0)
//		{
			cpu.loadGame("BLINKY");
/*		}
		else
		{
			System.err.println("E: No game loaded!");
			System.exit(1);
		}
*/		
		// actual emulation loop
		
		//cpu.memory[pc] = 0x
		
		cpu.bitmap.debugWindow();
		cpu.bitmap.debugFrame.setVisible(true);
		
		while (true)
		{
			cpu.cycle();
			
			String debugString = "";
			
			for (int i = 0; i < cpu.V.length; i++)
			{
				debugString += "V" + i + ":\t  " + (cpu.V[i] & 0xFF) + "\n";
			}
			
			debugString += "current opcode: 0x" + String.format("%02X", cpu.opcode) + "\n" + "pc: 0x" + String.format("%02X", cpu.pc) + "\n" + "stack[sp]: 0x" + String.format("%02X", cpu.stack[cpu.sp]) + "\n" + "sp: 0x" + String.format("%02X", cpu.sp) + "\n";
			debugString += "I: 0x" + String.format("%02X", cpu.I) + "\n" + "delay_timer: 0x" + String.format("%02X", cpu.delay_timer) + "\n" + "sound_timer: 0x" + String.format("%02X", cpu.sound_timer) + "\n";
			
			cpu.bitmap.debugDisplay.setText(debugString);
			
			if (cpu.drawFlag)
			{
				cpu.setPixels();
				cpu.bitmap.debugRender();
				cpu.drawFlag = false;
			}
			
			cpu.setKeys();
			
			Thread.sleep(2);
			
			if (cpu.input.stopCycle)
			{
				while (true);
			}
		}
	}
}