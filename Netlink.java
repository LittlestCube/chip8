import java.net.Socket;
import java.net.ServerSocket;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.PrintWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.EOFException;

public class Netlink extends Thread
{
	static int connected = 0;			// 0 for no connection, 1 for we're the server, 2 for we're the client
	public static final int SERVER = 1;
	public static final int CLIENT = 2;
	
	String host;
	
	final int PORT = 6969;
	
	static ObjectInputStream ois;
	static ObjectOutputStream oos;
	
	boolean clientKeepRunning = false;
	
	int sendTrueKeyValue = -1;
	int sendFalseKeyValue = -1;
	int sendSoundValue = -1;
	boolean[] sendGfx = null;
	
	int receiveTrueKeyValue = -1;
	int receiveFalseKeyValue = -1;
	int receiveSoundValue = -1;
	boolean[] receiveGfx = null;
	
	public void run()
	{
		ServerSocket server;
		Socket connectedClient = null;
		
		Socket client = null;
		
		Object objectReceived;
		
		if (connected == SERVER)
		{
			try
			{
				server = new ServerSocket(PORT);
				
				connectedClient = server.accept();
				
				oos = new ObjectOutputStream(connectedClient.getOutputStream());
				ois = new ObjectInputStream(connectedClient.getInputStream());
			}
			
			catch (Exception e)
			{
				System.err.println("E: Couldn't connect to client...\n\n");
				e.printStackTrace();
			}
			
			System.out.println("Client found! Beginning process...");
			
			while (true)
			{
				try
				{
					checkIfShouldSend();
					
					try
					{
						objectReceived = ois.readObject();
					}
					
					catch (EOFException e)
					{
						System.out.println("Client disconnected! Cleaning up...");
						ois.close();
						oos.close();
						
						connected = 0;
						System.out.println("Done!");
						break;
					}
					
					process(objectReceived);
				}
				
				catch (Exception e)
				{
					System.err.println("E: Couldn't read Object...\n\n");
					e.printStackTrace();
				}
			}
		}
		
		else if (connected == CLIENT)
		{
			try
			{
				client = new Socket(host, PORT);
				
				oos = new ObjectOutputStream(client.getOutputStream());
				ois = new ObjectInputStream(client.getInputStream());
			}
			
			catch (Exception e)
			{
				System.err.println("E: Couldn't connect to server...\n\n");
				e.printStackTrace();
			}
			
			while (true)
			{
				System.out.println("asdf");
				
				try
				{
					this.checkIfShouldSend();
					this.process(ois.readObject());
				}
				
				catch (Exception e)
				{
					System.err.println("E: Couldn't read Object...\n\n");
					e.printStackTrace();
				}
			}
		}
	}
	
	public void initAsServer()
	{
		try
		{
			System.out.println("I: Cool! We're the server.");
			
			System.out.println("I: Success! Server initialized at port " + PORT + ".");
			
			connected = SERVER;
			
			this.start();
		}
		
		catch (Exception e)
		{
			System.err.println("E: Couldn't initialize ServerSocket...\n\n");
			e.printStackTrace();
		}
	}
	
	public void initAsClient(String hostname)
	{
		try
		{
			System.out.println("I: Cool! We're the client.");
			
			System.out.println("I: Success! Client initialized at port " + PORT + ".");
			
			host = hostname;
			connected = CLIENT;
			clientKeepRunning = true;
			
			this.start();
		}
		
		catch (Exception e)
		{
			System.err.println("E: Couldn't initialize Socket...\n\n");
			e.printStackTrace();
		}
	}
	
	public void sendObject(Object output) throws Exception
	{
		oos.writeObject(output);
	}
	
	public void process(Object input)
	{
		System.out.println("bleh " + input);
		
		try
		{
			System.out.println(input);
			
			if (input == null)
			{
				return;
			}
			
			if (input instanceof String)
			{
				String inputString = (String) input;
				
				if (inputString.startsWith("KT:"))
				{
					receiveTrueKeyValue = Integer.parseInt(inputString.substring(inputString.indexOf(':') + 2, inputString.length()));
				}
				
				else if (inputString.startsWith("KF:"))
				{
					receiveFalseKeyValue = Integer.parseInt(inputString.substring(inputString.indexOf(':') + 2, inputString.length()));
				}
				
				else if (inputString.startsWith("S:"))
				{
					receiveSoundValue = Integer.parseInt(inputString.substring(inputString.indexOf(':') + 2, inputString.length()));
				}
				
				else
				{
					System.out.println(inputString);
				}
			}
			
			if (input instanceof boolean[])
			{
				receiveGfx = (boolean[]) input;
			}
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void checkIfShouldSend()
	{
		System.out.println("asdf here too");
		
		Object output = null;
		
		System.out.println("maybe one more over here");
		
		try
		{
			if (sendTrueKeyValue != -1)
			{
				output = new String("KT: " + sendTrueKeyValue);
				sendObject(output);
				
				sendTrueKeyValue = -1;
			}
			
			if (sendFalseKeyValue != -1)
			{
				output = new String("KF: " + sendFalseKeyValue);
				sendObject(output);
				
				sendFalseKeyValue = -1;
			}
			
			if (connected == SERVER)
			{
				if (sendSoundValue != -1)
				{
					output = new String("KT: " + sendSoundValue);
					sendObject(output);
					
					sendSoundValue = -1;
				}
				
				if (sendGfx != null)
				{
					output = sendGfx;
					sendObject(output);
					
					sendGfx = null;
				}
			}
			
			System.out.println(output);
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}