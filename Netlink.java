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
	ServerSocket server;
	Socket connectedClient;
	
	Socket client;
	String addressToConnect = "";
	
	int connected = 0;			// 0 for no connection, 1 for we're the server, 2 for we're the client
	
	ObjectInputStream ois;
	ObjectOutputStream oos;
	
	Object objectReceived;
	
	final int PORT = 6969;
	
	int trueKeyValue = -1;
	int falseKeyValue = -1;
	
	int soundValue = -1;
	
	boolean[] gfx;
	
	public void run()
	{
		if (server != null)
		{
			while (connectedClient == null)
			{
				connectAsServer();
			}
			
			while (connectedClient != null)
			{
				try
				{
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
					}
					process(objectReceived);
					checkIfShouldSend();
				}
				
				catch (Exception e)
				{
					System.err.println("E: Couldn't read Object...\n\n");
					e.printStackTrace();
				}
			}
		}
		
		else if (client != null)
		{
			connectAsClient();
			
			while (client != null)
			{
				try
				{
					process(ois.readObject());
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
			
			server = new ServerSocket(PORT);
			
			System.out.println("I: Success! Server initialized at port " + PORT + ".");
			
			connected = 1;
			
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
			
			client = new Socket(hostname, PORT);
			
			System.out.println("I: Success! Client initialized at port " + PORT + ".");
			
			connected = 2;
			
			this.start();
		}
		
		catch (Exception e)
		{
			System.err.println("E: Couldn't initialize Socket...\n\n");
			e.printStackTrace();
		}
	}
	
	void connectAsServer()
	{
		try
		{
			connectedClient = server.accept();
			ois = new ObjectInputStream(connectedClient.getInputStream());
			oos = new ObjectOutputStream(connectedClient.getOutputStream());
		}
		
		catch (Exception e)
		{
			System.err.println("E: Couldn't connect to client...\n\n");
			e.printStackTrace();
		}
	}
	
	void connectAsClient()
	{
		try
		{
			client = new Socket(addressToConnect, PORT);
			ois = new ObjectInputStream(client.getInputStream());
			oos = new ObjectOutputStream(client.getOutputStream());
		}
		
		catch (Exception e)
		{
			System.err.println("E: Couldn't connect to server...\n\n");
			e.printStackTrace();
		}
	}
	
	void sendObject(Object output) throws Exception
	{
		oos.writeObject(output);
	}
	
	void process(Object input)
	{
		try
		{
			if (input == null)
			{
				return;
			}
			
			if (input instanceof String)
			{
				String inputString = (String) input;
				
				if (inputString.startsWith("KT:"))
				{
					trueKeyValue = Integer.parseInt(inputString.substring(inputString.indexOf(':') + 2, inputString.length()));
				}
				
				else if (inputString.startsWith("KF:"))
				{
					falseKeyValue = Integer.parseInt(inputString.substring(inputString.indexOf(':') + 2, inputString.length()));
				}
				
				else if (inputString.startsWith("S:"))
				{
					soundValue = Integer.parseInt(inputString.substring(inputString.indexOf(':') + 2, inputString.length()));
				}
				
				else
				{
					System.out.println(input);
				}
			}
			
			if (input instanceof boolean[])
			{
				gfx = (boolean[]) input;
			}
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	void checkIfShouldSend()
	{
		try
		{
			if (trueKeyValue != -1)
			{
				sendObject(new String("KT: " + trueKeyValue));
				trueKeyValue = -1;
			}
			if (falseKeyValue != -1)
			{
				sendObject(new String("KF: " + falseKeyValue));
				falseKeyValue = -1;
			}
			if (soundValue != -1)
			{
				sendObject(new String("KT: " + soundValue));
				soundValue = -1;
			}
			if (gfx != null)
			{
				sendObject(gfx);
				gfx = null;
			}
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}