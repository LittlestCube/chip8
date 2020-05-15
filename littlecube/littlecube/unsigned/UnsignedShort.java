package littlecube.unsigned;

public class UnsignedShort
{
	short s;
	
	public UnsignedShort(short param)
	{
		set(param);
	}
	
	public UnsignedShort()
	{
		
	}
	
	public short unsign(short param)
	{
		return (short) (param & 0xFFFF);
	}
	
	public void set(short param)
	{
		s = param;
	}
	
	public short get()
	{
		return unsign(s);
	}
	
	// ------- byte parameters --------
	public short add(byte param)
	{
		return unsign((short) (s + param));
	}
	
	public short sub(byte param)
	{
		return add(-param);
	}
	
	public short or(byte param)
	{
		return unsign((short) (s |= param));
	}
	
	public short and(byte param)
	{
		return unsign((short) (s &= param));
	}
	
	public short xor(byte param)
	{
		return unsign((short) (s ^= param));
	}
	
	// ------ short parameters --------
	public short add(short param)
	{
		return unsign((short) (s + param));
	}
	
	public short sub(short param)
	{
		return add(-param);
	}
	
	public short or(short param)
	{
		return unsign((short) (s |= param));
	}
	
	public short and(short param)
	{
		return unsign((short) (s &= param));
	}
	
	public short xor(short param)
	{
		return unsign((short) (s ^= param));
	}
	
	// -------- int parameters --------
	public short add(int param)
	{
		return unsign((short) (s + param));
	}
	
	public short sub(int param)
	{
		return add(-param);
	}
	
	public short or(int param)
	{
		return unsign((short) (s |= param));
	}
	
	public short and(int param)
	{
		return unsign((short) (s &= param));
	}
	
	public short xor(int param)
	{
		return unsign((short) (s ^= param));
	}
}