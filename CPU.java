import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.IOException;

class CPU
{
	byte[] memory;
	
	byte[] V;
	
	short[] stack;
	
	boolean[] key;
	
	boolean drawFlag;
	
	short pc;
	short opcode;
	short I;
	short sp;
	
	byte delay_timer;
	byte sound_timer;
	
	boolean[] gfx;
	
	Bitmap bitmap = new Bitmap();
	
	CPU() throws Exception
	{
		pc		= 0x200;
		opcode	= 0;
		I		= 0;
		sp		= 0;
		
		gfx = new boolean[64 * 32];
		stack = new short[16];
		V = new byte[16];
		memory = new byte[4096];
		
		key = new boolean[16];
		drawFlag = false;
		
		int[] tmp_chip8_fontset =
		{ 
			0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
			0x20, 0x60, 0x20, 0x20, 0x70, // 1
			0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
			0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
			0x90, 0x90, 0xF0, 0x10, 0x10, // 4
			0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
			0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
			0xF0, 0x10, 0x20, 0x40, 0x40, // 7
			0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
			0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
			0xF0, 0x90, 0xF0, 0x90, 0x90, // A
			0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
			0xF0, 0x80, 0x80, 0x80, 0xF0, // C
			0xE0, 0x90, 0x90, 0x90, 0xE0, // D
			0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
			0xF0, 0x80, 0xF0, 0x80, 0x80  // F
		};
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(tmp_chip8_fontset.length * 4);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(tmp_chip8_fontset);
		
		byte[] chip8_fontset = byteBuffer.array();
		
		for(int i = 0; i < 80; ++i)
		{
			memory[i] = chip8_fontset[i];
		}
	}
	
	void loadGame(String filename) throws IOException
	{
		byte[] buffer = Files.readAllBytes(Paths.get(filename));
		
		for (int i = 0; i < buffer.length; i++)
		{
			memory[i + 0x200] = buffer[i];
		}
	}
	
	void cycle()
	{
		// todo: the rest of the opcodes
		
		
		if (pc <= 4095 && pc >= 0)
		{
			opcode = (short) (memory[pc] << 8 | memory[pc + 1]);
		}
		
		// BEGIN THE MADNESS
		boolean opcodeNotFound = false;
		
		switch (opcode & 0xF000)
		{
			case 0x0000:
			{
				switch (opcode & 0x00FF)
				{
					case 0x00E0:	// 0x00E0: clears the screen
					{
						clearDisplay();
						pc += 2;
						break;
					}
					
					case 0x00EE:	// 0x00EE: returns from a subroutine
					{
						pc = stack[sp];
						stack[sp--] = (short) 0;
						break;
					}
					
					default:									// opcode not recognized
					{
						opcodeNotFound = true;
						break;
					}
				}
				break;
			}
			
			case 0x1000:
			{
				pc = (short) (opcode & 0x0FFF);
				break;
			}
			
			case 0x2000:			// 0x2NNN: calls subroutine at NNN
			{
				stack[sp++] = pc;
				pc = (short) (opcode & 0x0FFF);
				break;
			}
			
			case 0x3000:			// 0x3XNN: skips the next instruction if VX equals NN. (Usually the next instruction is a jump to skip a code block)
			{
				int VXaddr = opcode & 0x0F00 >> 8;
				int conditionVal = opcode & 0x00FF;
				
				if (V[VXaddr] == (byte) conditionVal)
				{
					pc += 2;
				}
				
				pc += 2;
				break;
			}
			
			case 0x4000:			// 0x4XNN: skips the next instruction if VX doesn't equal NN. (Usually the next instruction is a jump to skip a code block)
			{
				int VXaddr = opcode & 0x0F00 >> 8;
				int conditionVal = opcode & 0x00FF;
				
				if (V[VXaddr] != (byte) conditionVal)
				{
					pc += 2;
				}
				
				pc += 2;
				break;
			}
			
			case 0x5000:			// 0x5XY0: skips the next instruction if VX equals VY. (usually the next instruction is a jump to skip a code block)
			{
				int VXaddr = opcode & 0x0F00 >> 8;
				int VYaddr = opcode & 0x00F0 >> 4;
				
				if (V[VXaddr] == V[VYaddr])
				{
					pc += 2;
				}
				
				pc += 2;
				break;
			}
			
			case 0x6000:			// 0x6XNN: sets VX to NN
			{
				int VXaddr = opcode & 0x0F00 >> 8;
				int val = opcode & 0x00FF;
				
				V[VXaddr] = (byte) val;
				break;
			}
			
			case 0x7000:			// 0x7XNN: adds NN to VX (carry flag is not changed)
			{
				int VXaddr = opcode & 0x0F00 >> 8;
				int val = opcode & 0x00FF;
				
				V[VXaddr] += (byte) val;
				break;
			}
			
			case 0x8000:
			{
				switch (opcode & 0x000F)
				{
					case 0x0000:	// 0x8XY0: sets VX to the value of VY
					{
						int VXaddr = opcode & 0x0F00 >> 8;
						int VYaddr = opcode & 0x00F0 >> 4;
						
						V[VXaddr] = V[VYaddr];
						break;
					}
					
					case 0x0001:	// 0x8XY1: sets VX to VX or VY (bitwise OR operation)
					{
						int VXaddr = opcode & 0x0F00 >> 8;
						int VYaddr = opcode & 0x00F0 >> 4;
						
						V[VXaddr] = (byte) (V[VXaddr] | V[VYaddr]);
						break;
					}
					
					case 0x0002:	// 0x8XY2: sets VX to VX and VY (bitwise AND operation)
					{
						int VXaddr = opcode & 0x0F00 >> 8;
						int VYaddr = opcode & 0x00F0 >> 4;
						
						V[VXaddr] = (byte) (V[VXaddr] & V[VYaddr]);
						break;
					}
					
					case 0x0003:	// 0x8XY3: sets VX to VX xor VY (bitwise XOR operation)
					{
						int VXaddr = opcode & 0x0F00 >> 8;
						int VYaddr = opcode & 0x00F0 >> 4;
						
						V[VXaddr] = (byte) (V[VXaddr] ^ V[VYaddr]);
						break;
					}
					
					case 0x0004:	// 0x8XY4: adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't
					{
						int VXaddr = (opcode & 0x0F00 >> 8);	// the reason >> is used is because we need to shift the value over to the ones place nybble
						int VYaddr = (opcode & 0x00F0 >> 4);	// same deal here, except it's already 4 bits farther than 0x0F00 was, so we only move it four bits
						
						if (V[VXaddr] + V[VYaddr] > 255)
						{
							V[0xF] = 1;							// set carry flag
						}
						
						else
						{
							V[0xF] = 0;							// not sure if this is right, but set carry flag to 0? It probably shouldn't be left at 1 in the first place...
						}
						
						V[VXaddr] += V[VYaddr];
						
						pc += 2;
						break;
					}
					
					case 0x0005:	// 0x8XY5: VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't; additional findings reveal that a "borrow" in bit operations means that VY was greater than VX, so 
					{
						int VXaddr = (opcode & 0x0F00 >> 8);	// the reason >> is used is because we need to shift the value over to the ones place nybble
						int VYaddr = (opcode & 0x00F0 >> 4);	// same deal here, except it's already 4 bits farther than 0x0F00 was, so we only move it four bits
						
						if (V[VYaddr] > V[VXaddr])
						{
							V[0xF] = 0;							// set borrow flag (apparently 0 means there is a borrow)
						}
						
						else
						{
							V[0xF] = 1;							// 1 means there was no borrow
						}
						
						V[VXaddr] -= V[VYaddr];
						
						pc += 2;
						break;
					}
					
					case 0x0006:	// 0x
					
					default:									// opcode not recognized
					{
						opcodeNotFound = true;
						break;
					}
				}
				break;
			}
			
			case 0x9000:			// 0x9XY0: skips the next instruction if VX doesn't equal VY. (usually the next instruction is a jump to skip a code block)
			
			case 0xA000:			// 0xANNN: sets I to the address NNN
			{
				I = (short) (opcode & 0x0FFF);
				
				pc += 2;
				break;
			}
			
			default:											// opcode not recognized
			{
				if (opcodeNotFound)
				{
					System.err.println("opcode not recognized: 0x" + opcode);
					System.exit(1);
				}
				
				else
				{
					System.out.println(opcode);
				}
			}
		}
		// ahh, end the madness
		
		// Update timers
		if (delay_timer > 0)
		{
			--delay_timer;
		}
			
		if (sound_timer > 0)
		{
			if (sound_timer == 1)
			{
				System.out.println("BEEP!");
			}
			
			--sound_timer;
		}
	}
	
	void setKeys()
	{
		// todo
	}
	
	boolean setPixel(int x, int y)
	{
		gfx[x + (bitmap.w * y)] ^= true;
		
		return gfx[x + (bitmap.w * y)];
	}
	
	void setPixels()
	{
		bitmap.setPixels(gfx);
		bitmap.updateDisplay();
	}
	
	void clearDisplay()
	{
		for (int i = 0; i < gfx.length; i++)
		{
			gfx[i] = false;
		}
		setPixels();
	}
}