import java.net.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * @author Anson
 * @version 1
 * @since 03/12/2023
 */
public class Client implements Runnable {
	Socket sock;
	static JFrame frame;
	static PrintWriter writer;
	static BufferedReader reader;
	static JLabel welcome;
	static JLabel[] game_board;
	int choice;
	static JButton submit = new JButton("Submit");
	static JTextField input_name = new JTextField(0);
	static JMenuBar menu = new JMenuBar();
	Client client;

	
	/**
	 * Connects and starts conversation with a TicTacToeServer.
	 * 
	 * */
	public void client_go() {  
		try {
			//Open a socket
			sock = new Socket("127.0.0.1", 5901);  
			InputStreamReader stream = new InputStreamReader(sock.getInputStream());  
			reader = new BufferedReader(stream);
			writer = new PrintWriter(sock.getOutputStream(),true);
			enable_label();
//			System.out.println("Connected");
			String type_str,arg_str;
			while(true) {
				type_str = reader.readLine();
				arg_str = reader.readLine();
				
				//process received message
				if (type_str.equals("board")) {
					display(arg_str);
				}
				else if (type_str.equals("message")) {
					welcome.setText(arg_str);
				}
				else if(type_str.equals("left")) {
					JOptionPane.showMessageDialog(frame, arg_str);
					break;
				}
				else if (type_str.equals("end")) {
					choice = JOptionPane.showConfirmDialog(frame, arg_str, "Game Over", JOptionPane.YES_NO_OPTION);
					if(choice == JOptionPane.YES_OPTION) {
						for(int i=0; i<9; i++) {
							game_board[i].setText("");
						}
						enable();
						
					}else {
						break;
					}
				}
				
			}
		} catch (Exception ex) { 
			ex.printStackTrace(); 
		}
		
	}
	
	/**
	 * Running client_go() on this thread
	 * */
	public void run() {
		this.client_go();
	}
	public void enable_label() {
		//enable all the labels
		for (int i = 0; i < game_board.length; i++) {
			game_board[i].setEnabled(true);
		}
	}
	public static void enable() {
		for(int i=0; i<9; i++) {
			game_board[i].setEnabled(true);
		}
		welcome.setText("WELCOME " + input_name.getText());
		writer.println("restart");
	}
	
	private void display(String str) {
		//display x and o on the game board
		String[] boardStr = str.split(",");
		for (int i = 0; i < 9; i++) {
			if (boardStr[i].equals("1")) {
				game_board[i].setForeground(Color.RED);
				game_board[i].setText("X");
				
			}
			if (boardStr[i].equals("0")) {
				game_board[i].setText("");
			}
			if (boardStr[i].equals("-1")) {
				game_board[i].setForeground(Color.GREEN);
				game_board[i].setText("O");
			}
		}
		return;
	}
	public static void set_topPanel() {
        //set up the top_panel
		welcome = new JLabel();
        welcome.setText("Enter your player name...");
    }
	public static void setframe() {
		JPanel LayoutPanel = new JPanel();
		LayoutPanel.setLayout(new BorderLayout());
		LayoutPanel.setFont(new Font("Courier New", 1, 24));
		set_topPanel();
		JPanel GameArea = new JPanel();
		GameArea.setLayout(new GridLayout(3, 3));

		game_board = new JLabel[9];
		
		Font font = new Font("Courier New", 1, 100);
		Border border = BorderFactory.createLineBorder(Color.BLACK, 5);
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				JLabel label = new JLabel();
				label.setPreferredSize(new Dimension(80, 80));
				label.setFont(font);
				label.setHorizontalAlignment(SwingConstants.CENTER);
				label.setBackground(Color.WHITE);
				label.setBorder(border);
				label.setEnabled(false);
				game_board[i + 3 * j] = label;
				final int _i = i;
				final int _j = j;
				label.addMouseListener(new MouseListener() {
					public void mouseClicked(MouseEvent e) {
						writer.println(Integer.toString(_i) + "," + Integer.toString(_j));
					}
					public void mousePressed(MouseEvent e) {
					}
					public void mouseReleased(MouseEvent e) {

					}
					public void mouseEntered(MouseEvent e) {
					}
					public void mouseExited(MouseEvent e) {
					}
				});
				GameArea.add(label);
			}
		}

		JPanel inputLayout = new JPanel(new GridLayout(1, 2));
		submit.addActionListener(new SubmitListener());

		inputLayout.add(input_name);
		inputLayout.add(submit);

		LayoutPanel.add(welcome, BorderLayout.NORTH);
		LayoutPanel.add(GameArea, BorderLayout.CENTER);
		LayoutPanel.add(inputLayout, BorderLayout.SOUTH);
		set_menu();
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(LayoutPanel);
		frame.setJMenuBar(menu);
		frame.setTitle("Tic Tac Toe");
		frame.setSize(400, 500);
		frame.setVisible(true);

		
	}
	public static void set_menu() {
		//set up the Menu bar
        JMenu control = new JMenu("Control");
        JMenuItem exit = new JMenuItem("Exit");
        control.add(exit);
        JMenu help = new JMenu("Help");
        JMenuItem instruction = new JMenuItem("Instruction");
        help.add(instruction);
        menu.add(control);
        menu.add(help);
        exit.addActionListener(new ExitListener());
        instruction.addActionListener(new InstructionListener());
	}
	public static void main(String[] args) {
		setframe();
	}
	static class ExitListener implements ActionListener{
        //overwrite actionlistener
        public void actionPerformed(ActionEvent e) {
            frame.dispose();
        }
    }
	static class InstructionListener implements ActionListener{
        //overwrite actionlistener
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(frame, "Some information about the game:\n"
                    + "Criteria for a valid move:\n" + "- The move is not occupied by any mark.\n"
                    + "- The move is made in the player's turn.\n" + "- The move is made within the 3 x 3 board.\n"
                    + "The game would continue and switch among the opposite player until it reaches either one of the following conditions:\n"
                    + "- Player 1 wins.\n" + "- Player 2 wins.\n" + "- Draw.");
        }
    }
	static class SubmitListener implements ActionListener{
        //overwrite actionlistener
        public void actionPerformed(ActionEvent e) {
            submit.setEnabled(false);
            input_name.setEnabled(false);
            welcome.setText("WELCOME " + input_name.getText());
            Client client = new Client();
            Thread T = new Thread(client);
            T.start();
        
            
        }
    }
	
	
	

}


