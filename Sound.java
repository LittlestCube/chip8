import javax.sound.sampled.*;

public class Sound
{
	static AudioFormat af;
	
	static SourceDataLine sdl;
	
	static Thread soundThread;
	
	static float SAMPLE_RATE = 44100f;
	
	static byte[] buf;
	
	static boolean alreadyPlaying;
	
	public Sound(int hz, int msecs, float vol)
	{
		try
		{
			alreadyPlaying = false;
			
			buf = new byte[msecs * 8];
			af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
			sdl = AudioSystem.getSourceDataLine(af);
			sdl.open(af);
			
			for (int i = 0; i < buf.length; i++) {
				double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
				buf[i] = (byte)(Math.sin(angle) * 127.0 * vol);
			}
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void beep() {
		if (alreadyPlaying)
		{
			return;
		}
		
		alreadyPlaying = true;
		
		soundThread = new SoundThread();
		
		soundThread.setPriority(Thread.MAX_PRIORITY);
		soundThread.start();
	}
	
	public void stopBeep()
	{
		alreadyPlaying = false;
	}
	
	class SoundThread extends Thread {
		public void run(){
			try {
				sdl.start();
				
				do
				{
					sdl.write(buf, 0, buf.length);
				} while (alreadyPlaying);
				
				sdl.stop();
				sdl.flush();
			}
			
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}