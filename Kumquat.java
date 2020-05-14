public class Kumquat
{
	public static void main(String args[]) throws Exception
	{
		CPU cpu = new CPU();
		
		// check to see if user actually loaded game (comment out until more is done)
//		if (args.length > 0)
//		{
			//cpu.loadGame("keyboard.ch8");
/*		}
		else
		{
			System.err.println("E: No game loaded!");
			System.exit(1);
		}
*/		
		// actual emulation loop
		
		while (true)
		{
			//cpu.cycle();
			
			if (cpu.drawFlag)
			{
				cpu.setPixels();
				cpu.drawFlag = false;
			}
			
			cpu.setKeys();
		}
	}
}