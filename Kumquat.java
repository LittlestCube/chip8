public class Kumquat
{
	public static void main(String args[]) throws Exception
	{
		CPU cpu = new CPU();
		
		// check to see if user actually loaded game
		// (comment out until more is done)
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
		
		// let's see if we can just test all the opcodes and make sure
		// everything works before giving up on this project
		
		// P.S. forget it
		
/*		int[] test = { 0x60EA };
		
		byte[] btest = new byte[test.length * 2];
		
		System.out.println("test: " + test.length);
		System.out.println("btest: " + btest.length);
		
		for (int i = 0; i < btest.length; i += 2)
		{
			System.out.println(i);
			
			byte b1 = (byte) ((test[i / 2] & 0xFF00) >> 8);
			byte b2 = (byte) (test[i / 2] & 0x00FF);
			
			btest[i] = b1;
			btest[i + 1] = b2;
			System.out.println(Integer.toHexString(btest[i]) + " " + Integer.toHexString(btest[i + 1]));
		}
		
		for (int i = 0; i < btest.length; i++)
		{
			cpu.memory[0x200 + i].set(btest[i]);
		}
*/		
		cpu.bitmap.debugWindow();
		cpu.bitmap.debugFrame.setVisible(true);
		
		while (true)
		{
			do {
				
			} while (cpu.input.stopCycle);
			
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
				debugString += "V" + i + ":\t  0x" + Integer.toHexString(cpu.V[i].get()) + "\n";
			}
			
			debugString += "current opcode: 0x" + Integer.toHexString(cpu.opcode) + "\n" + "pc: 0x" + Integer.toHexString(cpu.pc) + "\n" + "stack[sp]: 0x" + Integer.toHexString(cpu.stack[cpu.sp].get()) + "\n" + "sp: 0x" + Integer.toHexString(cpu.sp) + "\n";
			debugString += "I: 0x" + Integer.toHexString(cpu.I.get()) + "\n" + "delay_timer: 0x" + Integer.toHexString(cpu.delay_timer.get()) + "\n" + "sound_timer: 0x" + Integer.toHexString(cpu.sound_timer.get()) + "\n";
			
			cpu.bitmap.debugDisplay.setText(debugString);
		}
	}
}