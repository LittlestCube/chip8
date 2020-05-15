package littlecube.unsigned;

public class UnsignedInt
{
	int in;
	
	public UnsignedInt(int param)
	{
		set(param);
	}
	
	public UnsignedInt()
	{
		
	}
	
	public int unsign(int param)
	{
		return (int) (param & 0xFFFFFFFF);
	}
	
	public void set(int param)
	{
		in = param;
	}
	
	public int get()
	{
		return (int) unsign(in);
	}
	
	// ------- byte parameterin --------
	public int add(byte param)
	{
		return unsign((int) (in + param));
	}
	
	public int sub(byte param)
	{
		return add(-param);
	}
	
	public int or(byte param)
	{
		return unsign((int) (in |= param));
	}
	
	public int and(byte param)
	{
		return unsign((int) (in &= param));
	}
	
	public int xor(byte param)
	{
		return unsign((int) (in ^= param));
	}
	
	// ------ int parameterin --------
	public int add(short param)
	{
		return unsign((int) (in + param));
	}
	
	public int sub(short param)
	{
		return add(-param);
	}
	
	public int or(short param)
	{
		return unsign((int) (in |= param));
	}
	
	public int and(short param)
	{
		return unsign((int) (in &= param));
	}
	
	public int xor(short param)
	{
		return unsign((int) (in ^= param));
	}
	
	// -------- int parameterin --------
	public int add(int param)
	{
		return unsign((int) (in + param));
	}
	
	public int sub(int param)
	{
		return add(-param);
	}
	
	public int or(int param)
	{
		return unsign((int) (in |= param));
	}
	
	public int and(int param)
	{
		return unsign((int) (in &= param));
	}
	
	public int xor(int param)
	{
		return unsign((int) (in ^= param));
	}
}