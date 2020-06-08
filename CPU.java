import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.IOException;

import java.util.Random;

import littlecube.unsigned.*;

class CPU extends Chip8
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
	
	String currGame;
	
	Bitmap bitmap = new Bitmap();
	
	Input input = new Input();
	
	Sound sound = new Sound(200, 100, 0.3f);
	
	CPU() throws Exception
	{
		init();
	}
	
	void init()
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
		
		currGame = "";
		
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
	
	void nextOp()
	{
		pc += 2;
	}
	
	void loadGame(String filename) throws IOException
	{
		
		
		input.stopCycle = true;
		
		currGame = bitmap.gamePath;
		
		init();
		
		clearDisplay();
		
		bitmap.updateDisplay();
		
		byte[] buffer = Files.readAllBytes(Paths.get(filename));
		
		for (int i = 0; i < buffer.length; i++)
		{
			memory[i + 0x200].set(buffer[i]);
		}
		
		bitmap.gamePath = "";
		
		input.stopCycle = false;
		
		boolean hires = false;
		
		// check for hires mode
		if (memory[0x200].get() == 0x12 && memory[0x201].get() == 0x60)
		{
			hires = true;
			pc = 0x2C0;
		}
		
		bitmap.initDisplay(hires);
	}
	
	void cycle() throws Exception
	{
		if (pc <= 4095 && pc >= 0)
		{
			opcode = (short) ((memory[pc].get() << (short) 8) | memory[pc + 1].get());
		}
		
		else
		{
			System.out.println("\npc 0x" + pc + " out of bounds, closing...");
			System.exit(1);
		}
		
		UnsignedByte VXaddr = new UnsignedByte((opcode & 0x0F00) >> 8);
		UnsignedByte VYaddr = new UnsignedByte((opcode & 0x00F0) >> 4);
		UnsignedByte N = new UnsignedByte(opcode & 0x000F);
		UnsignedByte NN = new UnsignedByte(opcode & 0x00FF);
		UnsignedShort NNN = new UnsignedShort(opcode & 0x0FFF);
		
		// BEGIN THE MADNESS
		switch (opcode & 0xF000)
		{
			case 0x0000:
			{
				switch (opcode & 0x0FFF)
				{
					case 0x00E0:	// 0x00E0: clears the screen
					{
						clearDisplay();
						
						nextOp();
						break;
					}
					
					case 0x00EE:	// 0x00EE: returns from a subroutine
					{
						--sp;
						pc = (short) stack[sp].get();
						stack[sp].set(0);
						
						nextOp();
						break;
					}
					
					case 0x0230:	// 0x0230: clears the screen (hires)
					{
						System.out.println("bleh");
						
						clearDisplay();
						
						nextOp();
						break;
					}
				}
				break;
			}
			
			case 0x1000:			// 0x1NNN: jumps to address NNN
			{
				pc = (short) NNN.get();
				break;
			}
			
			case 0x2000:			// 0x2NNN: calls subroutine at NNN
			{
				stack[sp].set(pc);
				++sp;
				pc = (short) NNN.get();
				break;
			}
			
			case 0x3000:			// 0x3XNN: skips the next instruction if VX equals NN
			{
				if (V[VXaddr.get()].get() == NN.get())
				{
					nextOp();
				}
				
				nextOp();
				break;
			}
			
			case 0x4000:			// 0x4000: skips the next instruction if VX doesn't equal NN
			{
				if (V[VXaddr.get()].get() != NN.get())
				{
					nextOp();
				}
				
				nextOp();
				break;
			}
			
			case 0x5000:			// 0x5XY0: skips the next instruction if VX equals VY
			{
				if (V[VXaddr.get()].get() == V[VYaddr.get()].get())
				{
					nextOp();
				}
				
				nextOp();
				break;
			}
			
			case 0x6000:			// 0x6XNN: sets VX to NN
			{
				V[VXaddr.get()].set(NN.get());
				
				nextOp();
				break;
			}
			
			case 0x7000:			// 0x7XNN: adds NN to VX
			{
				V[VXaddr.get()].add(NN.get());
				
				nextOp();
				break;
			}
			
			case 0x8000:
			{
				switch (opcode & 0x000F)
				{
					case 0x0000:	// 0x8XY0: sets VX to the value of VY
					{
						V[VXaddr.get()].set(V[VYaddr.get()].get());
						
						nextOp();
						break;
					}
					
					case 0x0001:	// 0x8XY1: sets VX to VX | VY
					{
						V[VXaddr.get()].or(V[VYaddr.get()].get());
						
						nextOp();
						break;
					}
					
					case 0x0002:	// 0x8XY2: sets VX to VX & VY
					{
						V[VXaddr.get()].and(V[VYaddr.get()].get());
						
						nextOp();
						break;
					}
					
					case 0x0003:	// 0x8XY3: sets VX to VX ^ VY
					{
						V[VXaddr.get()].xor(V[VYaddr.get()].get());
						
						nextOp();
						break;
					}
					
					case 0x0004:	// 0x8XY4: adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't
					{
						if (V[VXaddr.get()].get() + V[VYaddr.get()].get() > 255)
						{
							V[0xF].set(1);
						}
						
						else
						{
							V[0xF].set(0);
						}
						
						V[VXaddr.get()].add(V[VYaddr.get()].get());
						
						nextOp();
						break;
					}
					
					case 0x0005:	// 0x8XY5: VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't
					{
						if (V[VXaddr.get()].get() > V[VYaddr.get()].get())
						{
							V[0xF].set(1);
						}
						
						else
						{
							V[0xF].set(0);
						}
						
						V[VXaddr.get()].sub(V[VYaddr.get()].get());
						
						nextOp();
						break;
					}
					
					case 0x0006:	// 0x8X?6: stores the least significant bit of VX in VF and then shifts VX to the right by 1
					{
						V[0xF].set(V[VXaddr.get()].get() & 0x01);
						
						V[VXaddr.get()].set(V[VXaddr.get()].get() >> 1);
						
						nextOp();
						break;
					}
					
					case 0x0007:	// 0x8XY7: sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't
					{
						if (V[VXaddr.get()].get() > V[VYaddr.get()].get())
						{
							V[0xF].set(0);
						}
						
						else
						{
							V[0xF].set(1);
						}
						
						V[VXaddr.get()].set(V[VYaddr.get()].get() - V[VXaddr.get()].get());
						
						nextOp();
						break;
					}
					
					case 0x000E:	// 0x8X?E: stores the most significant bit of VX in VF and then shifts VX to the left by 1
					{
						V[0xF].set((V[VXaddr.get()].get() & 0x80) >> 7);
						
						V[VXaddr.get()].set(V[VXaddr.get()].get() << 1);
						
						nextOp();
						break;
					}
				}
				break;
			}
			
			case 0x9000:			// 0x9XY0: skips the next instruction if VX doesn't equal VY
			{
				if (V[VXaddr.get()].get() != V[VYaddr.get()].get())
				{
					nextOp();
				}
				
				nextOp();
				break;
			}
			
			case 0xA000:			// 0xANNN: sets I to the address NNN
			{
				I.set(NNN.get());
				
				nextOp();
				break;
			}
			
			case 0xB000:			// 0xBNNN: jumps to the address NNN plus V0
			{
				pc = (short) (NNN.get() + V[0x0].get());
				break;
			}
			
			case 0xC000:			// 0xCXNN: sets VX to the result of a bitwise and operation on a random number (Typically: 0 to 255) and NN
			{
				Random rand = new Random();
				
				int rand_num = rand.nextInt() % 256;
				
				V[VXaddr.get()].set(rand_num & NN.get());
				
				nextOp();
				break;
			}
			
			case 0xD000:			// 0xDXYN: draws a sprite from memory[I] at (VX, VY) that has a width of 8 pixels and a height of N pixels. VF is set to 1 if any screen pixels are flipped from set to unset when the sprite is drawn, and to 0 if that doesn't happen
			{
				V[0xF].set(0);
				
				for (int currLine = 0; currLine < N.get(); currLine++)
				{
					for (int currPixel = 0; currPixel < 8; currPixel++)
					{
						if ((memory[I.get() + currLine].get() & (0x80 >> currPixel)) != 0)
						{
							boolean collision = setPixel(V[VXaddr.get()].get() + currPixel, V[VYaddr.get()].get() + currLine);		// remember, this returns true if no collision
							
							if (!collision)
							{
								V[0xF].set(1);
							}
						}
					}
				}
				
				drawFlag = true;
				
				nextOp();
				
				Thread.sleep(3);
				
				setPixels();
				break;
			}
			
			case 0xE000:
			{
				switch (opcode & 0x00FF)
				{
					case 0x009E:	// 0xEX9E: skips the next instruction if the key stored in VX is pressed
					{
						if (input.key[V[VXaddr.get()].get()])
						{
							nextOp();
						}
						
						nextOp();
						break;
					}
					
					case 0x00A1:	// 0xEXA1: skips the next instruction if the key stored in VX isn't pressed
					{
						if (!input.key[V[VXaddr.get()].get()])
						{
							nextOp();
						}
						
						nextOp();
						break;
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
						V[VXaddr.get()].set(delay_timer);
						
						nextOp();
						break;
					}
					
					case 0x000A:	// 0xFX0A: a key press is awaited, and then stored in VX
					{
						for (int i = 0; i < V.length; i++)
						{
							if (input.key[i])
							{
								V[VXaddr.get()].set(i);
								
								nextOp();
							}
						}
						
						break;
					}
					
					case 0x0015:	// 0xFX15: sets the delay timer to VX
					{
						delay_timer.set(V[VXaddr.get()].get());
						
						nextOp();
						break;
					}
					
					case 0x0018:	// 0xFX18: sets the sound timer to VX
					{
						int newSound = V[VXaddr.get()].get();
						
						sound_timer.set(newSound);
						
						if (netlink.connected == Netlink.SERVER)
						{
							netlink.sendSoundValue = newSound;
						}
						
						nextOp();
						break;
					}
					
					case 0x001E:	// 0xFX1E: adds VX to I. VF is set to 1 when there is a range overflow (I+VX>0xFFF), and to 0 when there isn't
					{
						if ((I.get() + V[VXaddr.get()].get()) > 0x0FFF)
						{
							V[0xF].set(1);
						}
						
						else
						{
							V[0xF].set(0);
						}
						
						I.add(V[VXaddr.get()].get());
						
						nextOp();
						break;
					}
					
					case 0x0029:	// 0xFX29: sets I to the location of the sprite for the character in VX
					{
						I.set(V[VXaddr.get()].get() * 0x5);
						
						nextOp();
						break;
					}
					
					case 0x0033:	// 0xFX33: stores the binary-coded decimal representation of VX in memory[I]
					{
						UnsignedByte l_digit = new UnsignedByte(V[VXaddr.get()].get() / 100);
						UnsignedByte m_digit = new UnsignedByte((V[VXaddr.get()].get() / 10) % 10);
						UnsignedByte r_digit = new UnsignedByte((V[VXaddr.get()].get() % 100) % 10);
						
						memory[I.get()].set(l_digit.get());
						memory[I.get() + 1].set(m_digit.get());
						memory[I.get() + 2].set(r_digit.get());
						
						nextOp();
						break;
					}
					
					case 0x0055:	// 0xFX55: stores V0 to VX (including VX) in memory starting at address I. I itself is left unmodified
					{
						for (int i = 0; i <= VXaddr.get(); i++)
						{
							memory[I.get() + i].set(V[i].get());
						}
						
						nextOp();
						break;
					}
					
					case 0x0065:	// 0xFX65: fills V0 to VX (including VX) with values from memory starting at address I. I itself is left unmodified
					{
						for (int i = 0; i <= VXaddr.get(); i++)
						{
							V[i].set(memory[I.get() + i].get());
						}
						
						nextOp();
						break;
					}
				}
				break;
			}
		}
		// ahh, end the madness
		
		// Update timers
		if (delay_timer.get() > 0)
		{
			delay_timer.sub(1);
			Thread.sleep(10);
		}
		
		if (sound_timer.get() > 0)
		{
			sound.beep();
			
			sound_timer.sub(1);
			
			if (sound_timer.get() == 0)
			{
				sound.stopBeep();
			}
		}
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
		bitmap.clearDisplay();
		
		drawFlag = true;
	}
}