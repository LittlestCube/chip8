import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.IOException;

import java.util.Random;

import littlecube.unsigned.*;

class CPU
{
	UnsignedByte[] memory;
	
	UnsignedByte[] V;
	
	UnsignedShort[] stack;
	
	boolean drawFlag;
	
	short pc;
	short opcode;
	UnsignedShort I;
	short sp;
	
	UnsignedByte delay_timer;
	UnsignedByte sound_timer;
	
	Bitmap bitmap = new Bitmap();
	
	Input input = new Input();
	
	CPU() throws Exception
	{
		pc = 0x200;
		opcode = 0;
		I = new UnsignedShort();
		sp = 0;
		
		stack = new UnsignedShort[16];
		V = new UnsignedByte[16];
		memory = new UnsignedByte[4096];
		
		drawFlag = false;
		
		delay_timer = new UnsignedByte(0);
		sound_timer = new UnsignedByte(0);
		
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
		
		bitmap.frame.addKeyListener(input);
		
		for (int i = 0; i < stack.length; i++)
		{
			stack[i] = new UnsignedShort();
		}
		
		for (int i = 0; i < V.length; i++)
		{
			V[i] = new UnsignedByte();
		}
		
		for (int i = 0; i < memory.length; i++)
		{
			memory[i] = new UnsignedByte();
		}
		
		for (int i = 0; i < 80; i++)
		{
			memory[i].set(tmp_chip8_fontset[i]);
		}
	}
	
	void loadGame(String filename) throws IOException
	{
		byte[] buffer = Files.readAllBytes(Paths.get(filename));
		
		for (int i = 0; i < buffer.length; i++)
		{
			memory[i + 0x200].set(buffer[i]);
		}
	}
	
	void cycle()
	{
		// todo: convert $opcode to UnsignedShort
		
		opcode = (short) ((memory[pc].get() << 8) | (memory[pc + 1].get() & 0xFF));
		
		System.out.println("0x" + Integer.toHexString(opcode & 0xFFFF) + "        pc: " + Integer.toHexString(pc & 0x0FFF));
		
		// BEGIN THE MADNESS
		boolean opcodeNotFound = false;
		
		switch (opcode & 0xF000)
		{
			case 0x0000:
			{
				switch (opcode & 0x00FF)
				{
/*					case 0x0000:	// 0x0000: calls RCA 1802 program at address NNN. not necessary for most ROMs
					{
						pc = (short) (opcode & 0x0FFF);
						break;
					}
*/					
					case 0x00E0:	// 0x00E0: clears the screen
					{
						clearDisplay();
						
						pc += 2;
						break;
					}
					
					case 0x00EE:	// 0x00EE: returns from a subroutine
					{
						sp -= 1;
						pc = stack[sp].s;
						stack[sp].set(0);
						stack[sp + 1].set(0);
						
						pc += 2;
						break;
					}
					
					default:									// opcode not recognized
					{
						opcodeNotFound = true;
					}
				}
				break;
			}
			
			case 0x1000:			// 0x1NNN: jumps to address NNN
			{
				pc = (short) (opcode & 0x0FFF);
				break;
			}
			
			case 0x2000:			// 0x2NNN: calls subroutine at NNN
			{
				stack[sp].set(pc);
				sp += 1;
				pc = (short) (opcode & 0x0FFF);
				break;
			}
			
			case 0x3000:			// 0x3XNN: skips the next instruction if VX equals NN. (Usually the next instruction is a jump to skip a code block)
			{
				if (V[(opcode & 0x0F00) >> 8].b == (byte) (opcode & 0x00FF))
				{
					pc += 4;
				}
				
				else
				{
					pc += 2;
				}
				break;
			}
			
			case 0x4000:			// 0x4XNN: skips the next instruction if VX doesn't equal NN. (Usually the next instruction is a jump to skip a code block)
			{
				if (V[(opcode & 0x0F00) >> 8].b != (byte) (opcode & 0x00FF))		// try casting both of these to (byte)
				{
					pc += 4;
				}
				
				else
				{
					pc += 2;
				}
				break;
			}
			
			case 0x5000:			// 0x5XY0: skips the next instruction if VX equals VY. (usually the next instruction is a jump to skip a code block)
			{
				int VXaddr = (opcode & 0x0F00) >> 8;
				int VYaddr = (opcode & 0x00F0) >> 4;
				
				if (V[VXaddr].get() == V[VYaddr].get())
				{
					pc += 4;
				}
				
				else
				{
					pc += 2;
				}
				break;
			}
			
			case 0x6000:			// 0x6XNN: sets VX to NN
			{
				int VXaddr = (opcode & 0x0F00) >> 8;
				int val = V[0].unsign((byte) (opcode & 0x00FF));
				
				V[VXaddr].set(val);
				
				pc += 2;
				break;
			}
			
			case 0x7000:			// 0x7XNN: adds NN to VX (carry flag is not changed)
			{
				V[(opcode & 0x0F00) >> 8].add(opcode & 0x00FF);
				
				pc += 2;
				break;
			}
			
			case 0x8000:
			{
				switch (opcode & 0x000F)
				{
					case 0x0000:	// 0x8XY0: sets VX to the value of VY
					{
						int VXaddr = (opcode & 0x0F00) >> 8;
						int VYaddr = (opcode & 0x00F0) >> 4;
						
						V[VXaddr].set(V[VYaddr].get());
						
						pc += 2;
						break;
					}
					
					case 0x0001:	// 0x8XY1: sets VX to VX or VY (bitwise OR operation)
					{
						int VXaddr = (opcode & 0x0F00) >> 8;
						int VYaddr = (opcode & 0x00F0) >> 4;
						
						V[VXaddr].or(V[VYaddr]);
						
						pc += 2;
						break;
					}
					
					case 0x0002:	// 0x8XY2: sets VX to VX and VY (bitwise AND operation)
					{
						int VXaddr = (opcode & 0x0F00) >> 8;
						int VYaddr = (opcode & 0x00F0) >> 4;
						
						V[VXaddr].and(V[VYaddr]);
						
						pc += 2;
						break;
					}
					
					case 0x0003:	// 0x8XY3: sets VX to VX xor VY (bitwise XOR operation)
					{
						int VXaddr = (opcode & 0x0F00) >> 8;
						int VYaddr = (opcode & 0x00F0) >> 4;
						
						V[VXaddr].xor(V[VYaddr]);
						
						pc += 2;
						break;
					}
					
					case 0x0004:	// 0x8XY4: adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't
					{
						int VXaddr = (opcode & 0x0F00) >> 8;	// the reason >> is used is because we need to shift the value over to the ones place nybble
						int VYaddr = (opcode & 0x00F0) >> 4;	// same deal here, except it's already 4 bits farther than 0x0F00 was, so we only move it four bits
						
						if (V[VYaddr].get() > 0xFF - V[VXaddr].get())
						{
							V[0xF].set(1);						// set carry flag to 1, there was a carry
						}
						
						else
						{
							V[0xF].set(0);						// set carry flag to 0, there was no carry
						}
						
						V[VXaddr].add(V[VYaddr]);
						
						pc += 2;
						break;
					}
					
					case 0x0005:	// 0x8XY5: VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't; additional findings reveal that a "borrow" in bit operations means that VY is greater than VX
					{
						int VXaddr = (opcode & 0x0F00) >> 8;
						int VYaddr = (opcode & 0x00F0) >> 4;
						
						if (V[VYaddr].get() > V[VXaddr].get())
						{
							V[0xF].set(0);						// 0 means there was a borrow
						}
						
						else
						{
							V[0xF].set(1);						// 1 means there was no borrow
						}
						
						V[VXaddr].sub(V[VYaddr].get());
						
						pc += 2;
						break;
					}
					
					case 0x0006:	// 0x8X?6: stores the least significant bit of VX in VF and then shifts VX to the right by 1
					{
						int VXaddr = (opcode & 0x0F00) >> 8;
						
						V[0xF].set((byte) (V[VXaddr].get() & 0x01));		// take right-most bit and save it in VF
						
						V[VXaddr].set(V[VXaddr].get() >> 1);
						
						pc += 2;
						break;
					}
					
					case 0x0007:	// 0x8XY7: sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't
					{
						int VXaddr = (opcode & 0x0F00) >> 8;
						int VYaddr = (opcode & 0x00F0) >> 4;
						
						if (V[VYaddr].get() > V[VXaddr].get())
						{
							V[0xF].set(0);						// 0 means there was a borrow
						}
						
						else
						{
							V[0xF].set(1);						// 1 means there was no borrow
						}
						
						V[VXaddr].set((byte) (V[VYaddr].get() - V[VXaddr].get()));
						
						pc += 2;
						break;
					}
					
					case 0x000E:	// 0x8X?E: stores the most significant bit of VX in VF and then shifts VX to the left by 1
					{
						int VXaddr = (opcode & 0x0F00) >> 8;
						
						V[0xF].set((byte) (V[VXaddr].b >> 7));	// take left-most bit and save it in VF
						
						V[VXaddr].set(V[VXaddr].get() << 1);
						
						pc += 2;
						break;
					}
					
					default:									// opcode not recognized
					{
						opcodeNotFound = true;
					}
				}
				break;
			}
			
			case 0x9000:			// 0x9XY0: skips the next instruction if VX doesn't equal VY (usually the next instruction is a jump to skip a code block)
			{
				int VXaddr = (opcode & 0x0F00) >> 8;
				int VYaddr = (opcode & 0x00F0) >> 4;
				
				if (V[VXaddr] != V[VYaddr])
				{
					pc += 4;
				}
				
				else
				{
					pc += 2;
				}	
				break;
			}
			
			case 0xA000:			// 0xANNN: sets I to the address NNN
			{
				I.set(opcode & 0x0FFF);
				
				pc += 2;
				break;
			}
			
			case 0xB000:			// 0xBNNN: jumps to the address NNN plus V0
			{
				pc = (short) ((opcode & 0x0FFF) + V[0x0].get());
				break;
			}
			
			case 0xC000:			// 0xCXNN: sets VX to the result of a bitwise and operation on a random number (typically: 0 to 255) and NN
			{
				Random random = new Random();
				int randInt = random.nextInt();				// basically, we are generating a random number from 0-255, but nextInt() is garbage, so we just generate an int and mod 256 it
				
				V[opcode & 0x0F00 >> 8].set((randInt % 256) & (opcode & 0x00FF));
				
				pc += 2;
				break;
			}
			
									// 0xDXYN: draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels
									// each row of 8 pixels is read as bit-coded starting from memory location I; I value doesn’t change after the execution of this instruction
			case 0xD000:			// as described above, VF is set to 1 if any screen pixels are flipped from set to unset when the sprite is drawn, and to 0 if that doesn’t happen
			{
				int xPos = V[(opcode & 0x0F00) >> 8].get();
				int yPos = V[(opcode & 0x00F0) >> 4].get();
				int spriteHeight = (opcode & 0x000F);
				
				V[0xF].set(0);
				for (int currLine = 0; currLine < spriteHeight; currLine++)
				{
					for (int currPixel = 0; currPixel < 8; currPixel++)
					{
						if ((memory[I.get() + currLine].b & (0x80 >> currPixel)) != 0)
						{
							boolean collide = setPixel(xPos + currPixel, yPos + currLine);
							
							if (!collide)
							{
								V[0xF].set(1);
							}
						}
					}
				}
				
				drawFlag = true;
				pc += 2;
				break;
			}
			
			case 0xE000:
			{
				switch (opcode & 0x000F)
				{
					case 0x000E:	// 0xEX9E: skips the next instruction if the key stored in VX is pressed (usually the next instruction is a jump to skip a code block)
					{
						if (input.key[V[opcode & 0x0F00 >> 8].get()])
						{
							pc += 4;
						}
						
						else
						{
							pc += 2;
						}
						break;
					}
					
					case 0x0001:	// 0xEXA1: skips the next instruction if the key stored in VX isn't pressed (usually the next instruction is a jump to skip a code block)
					{
						if (!input.key[V[(opcode & 0x0F00) >> 8].get()])
						{
							pc += 4;
						}
						
						else
						{
							pc += 2;
						}
						break;
					}
					
					default:									// opcode not recognized
					{
						opcodeNotFound = true;
					}
				}
				break;
			}
			
			case 0xF000:
			{
				switch (opcode & 0x00FF)
				{
					case 0x0007:	// 0xFX07: sets VX to the value of the delay timer
					{
						V[(opcode & 0x0F00) >> 8].set(delay_timer);
						
						pc += 2;
						break;
					}
					
					case 0x000A:	// 0xFX0A: a key press is awaited, and then stored in VX (blocking operation: all instruction halted until next key event)
					{
						for (int i = 0; i < input.key.length; i++)
						{
							if (input.key[i])
							{
								V[(opcode & 0x0F00) >> 8].set(i);
								
								pc += 2;
							}
						}
						break;
					}
					
					case 0x0015:	// 0xFX15: sets the delay timer to VX
					{
						delay_timer.set(V[(opcode & 0x0F00) >> 8].get());
						
						pc += 2;
						break;
					}
					
					case 0x0018:	// 0xFX15: sets the sound timer to VX
					{
						sound_timer.set(V[(opcode & 0x0F00) >> 8].get());
						
						pc += 2;
						break;
					}
					
					case 0x001E:	// 0xFX1E: adds VX to I. VF is set to 1 when there is a range overflow (I+VX>0xFFF), and to 0 when there isn't
					{
						int VXaddr = (opcode & 0x0F00) >> 8;
						
						if (I.get() + V[VXaddr].get() > 0xFFF)
						{
							V[0xF].set(1);
						}
						
						else
						{
							V[0xF].set(0);
						}
						
						I.add(V[VXaddr].get());
						
						pc += 2;
						break;
					}
					
					case 0x0029:	// 0xFX29: sets I to the location of the sprite for the character in VX. characters 0-F (in hexadecimal) are represented by a 4x5 font
					{
						I.set(V[(opcode & 0x0F00) >> 8].get() * 0x5);
						
						pc += 2;
						break;
					}
					
					case 0x0033:	// 0xFX33: stores the binary-coded decimal representation of VX in memory[I], with the greatest digit in memory[I], the middle digit in memory[I + 1] and the smallest digit in memory[I + 2] (I remains unchanged)
					{
						int VXaddr = (opcode & 0x0F00) >> 8;
						
						UnsignedByte lDigit = new UnsignedByte(V[VXaddr].get() / 100);
						UnsignedByte mDigit = new UnsignedByte((V[VXaddr].get() / 10) % 10);
						UnsignedByte rDigit = new UnsignedByte((V[VXaddr].get() % 100) % 10);
						
						System.out.println(lDigit.get() + " " + mDigit.get() + " " + rDigit.get());
						
						memory[I.get()].set(lDigit.get());
						memory[I.get() + 1].set(mDigit.get());
						memory[I.get() + 2].set(rDigit.get());
					}
					
					case 0x0055:	// 0xFX55: stores V0 to VX (including VX) in memory starting at address I. the offset from I is increased by 1 for each value written, but I itself is left unmodified
					{
						int VXaddr = (opcode & 0x0F00) >> 8;
						
						for (int i = 0; i <= VXaddr; i++)
						{
							memory[I.get() + i].set(V[i].b);
						}
						
						I.add(VXaddr + 1);
						
						pc += 2;
						break;
					}
					
					case 0x0065:	// 0xFX65: fills V0 to VX (including VX) with values from memory starting at address I. the offset from I is increased by 1 for each value written, but I itself is left unmodified
					{
						int VXaddr = (opcode & 0x0F00) >> 8;
						
						for (int i = 0; i <= VXaddr; i++)
						{
							V[i].set(memory[I.get() + i]);
						}
						
						I.add(VXaddr + 1);
						
						pc += 2;
						break;
					}
					
					default:									// opcode not recognized
					{
						opcodeNotFound = true;
					}
					break;
				}
			}
			default:
			{
				if (opcodeNotFound)
				{
					System.out.println("opcode not recognized: 0x" + Integer.toHexString(opcode & 0xFFFF));
					System.exit(1);
				}
			}
		}
		// ahh, end the madness
		
		// Update timers
		if (delay_timer.get() > 0)
		{
			delay_timer.sub(1);
		}
			
		if (sound_timer.get() > 0)
		{
			if (sound_timer.get() == 1)
			{
				System.out.println("BEEP!");
			}
			
			sound_timer.sub(1);
		}
	}
	
	void setKeys()
	{
		// todo
	}
	
	boolean setPixel(int x, int y)
	{
		return bitmap.setPixel(x, y);
	}
	
	void setPixels()
	{
		bitmap.setPixels();
		bitmap.updateDisplay();
	}
	
	void clearDisplay()
	{
		for (int i = 0; i < bitmap.gfx.length; i++)
		{
			bitmap.gfx[i] = false;
		}
		drawFlag = true;
	}
}	