import java.net.*;
import java.io.*;

/**
 * This is a TicTacToe Game Server
 * @author Anson
 * @version 1
 * @since 03/12/2023
 * 
 * */
public class Server implements Runnable {
    //create two socket one for player1 and another one for player2
	private Socket player1_sock;
	private Socket player2_sock;
    //create two writer one for player1 and another one for player2
	private PrintWriter player1_writer;
	private PrintWriter player2_writer;
    //create two reader one for player1 and another one for player2
	private BufferedReader reader1;
	private BufferedReader reader2;
	private static Server server;
	private static boolean isRunning;
	private Game_status game;
	boolean restart = true;

	public static void main(String[] args) {
		server = new Server();
		server.Server_start();
	}
	/**
	 * This is a go function
	 * Start the socket connection
	 */
	public void Server_start() {
		game = new Game_status();
		try {
			//try socket connection
			ServerSocket serverSock = new ServerSocket(5901);
			//connection for player1
			server.player1_sock = serverSock.accept();
			server.player1_writer = new PrintWriter(server.player1_sock.getOutputStream(),true);
			server.reader1 = new BufferedReader(new InputStreamReader(server.player1_sock.getInputStream()));
			//connection for player2
			server.player2_sock = serverSock.accept();
			server.player2_writer = new PrintWriter(server.player2_sock.getOutputStream(),true);
			server.reader2 = new BufferedReader(new InputStreamReader(server.player2_sock.getInputStream()));
            //create two threads, one for player1, another for player2
			Thread p1 = new Thread(server);  
			Thread p2 = new Thread(server);
            //set name for threads
			p1.setName("player1");
			p2.setName("player2");
			
			isRunning = true;
            //start threads
			p1.start();
			p2.start();
			
			p1.join();
			p2.join();
            //close serversock
			serverSock.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * This is a function in Runnable
	 * 
	 * */
	public void run() {
		boolean isPlayer1 = (Thread.currentThread().getName().equals("player1"));
//		System.out.println("thread started"); //DEBUG
		String response;
		
		while (isRunning || restart) {
			try {
				response = read_from_client(isPlayer1);
				process(isPlayer1, response);
			} catch (IOException e) {
				write("left","Game Ends. One of the players left.");
				isRunning = false;
			}
		}
	}


	private void process(boolean isPlayer1, String response) throws IOException {
		if (response.equals("restart")) {
			isRunning = true;
			game.restart();
			return;
		}
		String[] input = response.split(",");
		int row = Integer.parseInt(input[0]);
		int col = Integer.parseInt(input[1]);
		boolean valid = game.Move(isPlayer1, row, col);
		if (valid) {
            //write message to client
			write("board",game.getInfo());
			int check = game.checkResult();
			switch (check) {
			
			case 0: {
				if (isPlayer1) {
					write1("message", "Valid move, wait for you opponent.");
					write2("message","Your opponent has moved, now is your turn.");
				} else {
					write2("message","Valid move, wait for you opponent.");
					write1("message","Your opponent has moved, now is your turn.");
				}
				break;
			}
			case 1: {
				write1("end","Congratulations. You Win. Do you want to play again?");
				write2("end","You lose. Do you want to play again?");
				isRunning = false;
				break;
			}
			case 2: {
				write2("end","Congratulations. You Win. Do you want to play again?");
				write1("end","You lose. Do you want to play again?");
				isRunning = false;
				break;
			}
			case 3: {
				write("end","Draw. Do you want to play again?");
				isRunning = false;
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + Integer.toString(check));
			}
		} else {
			write("invalid","");
		}
	}
	
	private void write1(String type, String argument) {
//		System.out.println("server: "+type+" "+argument);
		player1_writer.println(type);
		player1_writer.println(argument);
	}
	
	private void write2(String type, String argument) {
//		System.out.println("server: "+type+" "+argument);
		player2_writer.println(type);
		player2_writer.println(argument);
	}
	
	private void write(String type, String argument) {
//		System.out.println("server: "+type+" "+argument);
		player1_writer.println(type);
		player1_writer.println(argument);
		
		player2_writer.println(type);
		player2_writer.println(argument);
	}
	//This is a function that read the message from client
	private String read_from_client(boolean isPlayer1) throws IOException {
		String response;
		//check which player
		if (isPlayer1) {
			response = reader1.readLine();
		}
		else {
			response = reader2.readLine();
		}
		return response;
	}

}

