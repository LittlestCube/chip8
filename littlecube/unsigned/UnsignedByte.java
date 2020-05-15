package littlecube.unsigned;

public class UnsignedByte
{
	public byte b;
	
	public UnsignedByte(byte param)
	{
		set(param);
	}
	
	public UnsignedByte(short param)
	{
		set(param);
	}
	
	public UnsignedByte(int param)
	{
		set(param);
	}
	
	public UnsignedByte()
	{
		
	}
	
	public int unsign(byte param)
	{
		return param & 0xFF;
	}
	
	public int get()
	{
		return unsign(b);
	}
	
	// -------- UnsignedByte parameters --------
	public void set(UnsignedByte param)
	{
		b = (byte) param.get();
	}
	
	public void add(UnsignedByte param)
	{
		b += param.get();
	}
	
	public void sub(UnsignedByte param)
	{
		add(-param.get());
	}
	
	public void or(UnsignedByte param)
	{
		b |= param.get();
	}
	
	public void and(UnsignedByte param)
	{
		b &= param.get();
	}
	
	public void xor(UnsignedByte param)
	{
		b ^= param.get();
	}
	
	// ------- byte parameters --------
	public void set(byte param)
	{
		b = param;
	}
	
	public void add(byte param)
	{
		b += param;
	}
	
	public void sub(byte param)
	{
		add(-param);
	}
	
	public void or(byte param)
	{
		b |= param;
	}
	
	public void and(byte param)
	{
		b &= param;
	}
	
	public void xor(byte param)
	{
		b ^= param;
	}
	
	// ------ short parameters --------
	public void set(short param)
	{
		b = (byte) param;
	}
	
	public void add(short param)
	{
		b += param;
	}
	
	public void sub(short param)
	{
		add(-param);
	}
	
	public void or(short param)
	{
		b |= param;
	}
	
	public void and(short param)
	{
		b &= param;
	}
	
	public void xor(short param)
	{
		b ^= param;
	}
	
	// -------- int parameters --------
	public void set(int param)
	{
		b = (byte) param;
	}
	
	public void add(int param)
	{
		b += param;
	}
	
	public void sub(int param)
	{
		add(-param);
	}
	
	public void or(int param)
	{
		b |= param;
	}
	
	public void and(int param)
	{
		b &= param;
	}
	
	public void xor(int param)
	{
		b ^= param;
	}
}