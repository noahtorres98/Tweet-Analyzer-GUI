package sentimentanalysis;
import java.awt.*;

import java.io.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.*;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GUIApp extends JFrame {

	private static final TweetHandler HANDLER = new TweetHandler();

	public static JTextArea input;
	private JLabel headerLabel;
	private JPanel panel;
	private static JTable table;
	private static JTable deletedTable;
	private static String tweetId;
	private static String filePath;
	
    /**
     *
     */
        public static final Logger logger = Logger.getLogger(GUIApp.class.getName());
                
	FileHandler fh;

	public static void main(String[] args) { 
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
                            try {			
                                GUIApp app = new GUIApp("Application");
                            } catch (IOException ex) {
                                Logger.getLogger(GUIApp.class.getName()).log(Level.SEVERE, null, ex);
                            }
			}
		});
	}

	/**
	 * sets up gui and adds action listner to buttons
	 * @param title
	 */

	public GUIApp(String title) throws IOException {
		super(title);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setSize(500,500);
		Component c = this;
                		               
		logger.setLevel(Level.INFO);
		
		this.fh = new FileHandler ("Log.txt");
		
		fh.setFormatter(new SimpleFormatter());
		
		fh.setLevel(Level.INFO);
		
		logger.addHandler(fh);
		
		HANDLER.loadSerialDB();

		headerLabel = new JLabel("Click a button",JLabel.CENTER );	

		JButton button0 = new JButton("0. Exit program."); 
		button0.setActionCommand("0");

		JButton button1 = new JButton("1. Load new tweet text file."); 
		button1.setActionCommand("1");

		JButton button2 = new JButton("2. Classify tweets using NLP library and report accuracy."); 
		button2.setActionCommand("2");

		JButton button3 = new JButton("3. Manually change tweet class label."); 
		button3.setActionCommand("3");

		JButton button4 = new JButton("4. Add new tweets to database."); 
		button4.setActionCommand("4");

		JButton button5 = new JButton("5. Delete tweet from database (given its id)."); 
		button5.setActionCommand("5");

		JButton button6 = new JButton("6. Search tweets by user, date, flag, or a matching substring."); 
		button6.setActionCommand("6");

		panel = new JPanel();

		BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);

		panel.setLayout(boxlayout);
		this.add(panel);

		input = new JTextArea();
		input.setEditable(false);

		JScrollPane consolePane = new JScrollPane(input);
		consolePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		input.setText("Welcome to the Sentiment Analyzer application." + "\nBuffer size: " + HANDLER.getTweetBuffer().size() + 
				"\nDatabase size: " + HANDLER.getTweetDB().size());

		button0.addActionListener(new ButtonClickListener());
		button1.addActionListener(new ButtonClickListener());
		button2.addActionListener(new ButtonClickListener());
		button3.addActionListener(new ButtonClickListener());
		button4.addActionListener(new ButtonClickListener());
		button5.addActionListener(new ButtonClickListener());
		button6.addActionListener(new ButtonClickListener());

		panel.add(headerLabel);
		panel.add(button0);
		panel.add(button1);
		panel.add(button2);
		panel.add(button3);
		panel.add(button4);
		panel.add(button5);
		panel.add(button6);
		panel.add(consolePane);
		this.setVisible(true);
	}

	/**
	 * ButtonClickListener handles button clicks based on action command
	 * @author noah, miguel
	 *
	 */

	private class ButtonClickListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {			
			String command = e.getActionCommand();

			if (command.equals("0")) {
				HANDLER.saveSerialDB();
				input.append("\nGOODBYE!");
				
				logger.info("End program");
			}

			/**
			 * when user clicks on button 1
			 * user is prompt to enter file path
			 * and then loads tweets into buffer
			 */

			else if (command.equals("1"))  {				
				filePath = JOptionPane.showInputDialog(null, "Enter filepath name");
				System.out.println(filePath);
				List<AbstractTweet> tempTweets = HANDLER.loadTweetsFromText(filePath);
				HANDLER.setTweetBuffer(tempTweets);
				int bufferSize = 0;
				int databaseSize = 0;
				if (HANDLER.getTweetBuffer()!=null && HANDLER.getTweetDB()!=null) {
					bufferSize = HANDLER.getTweetBuffer().size();
					databaseSize = HANDLER.getTweetDB().size();
					input.setText("Filepath entered: " + filePath + 
							"\nBuffer size: " + bufferSize + 
							"\nDatabase size: " + databaseSize);
				}
				
				logger.info("Loading tweets from csv file");
				
			}

			/**
			 * when user clicks button 2
			 * classifies tweets that are in buffer and reports results in text area
			 */

			else if (command.equals("2")) {
				if (HANDLER.getTweetBuffer().size() == 0) {
					input.setText("No tweets in buffer.\nMust load tweet text file first.");
					return;
				}

				int countCorrect = 0;
				int countWrong = 0;

				for (AbstractTweet t : HANDLER.getTweetBuffer()) {
					t.setPredictedPolarity(HANDLER.classifyTweet(t));
					if (t.getPredictedPolarity() == t.getTarget()) {
						countCorrect++;
					} else {
						countWrong++;
					}
				}

				double correctRate = (double)countCorrect / ((double)countWrong + (double)countCorrect) * 100;		
				input.append("\nCorrect classified tweets: " + countCorrect + "\nIncorrect classified tweets: " + countWrong + 
						"\nCorrect prediction rate: " + String.format("%.2f", correctRate) + "%");
				
				logger.info("Classify tweets");
			}

			/**
			 * if user clicks button 3 prompts user to input id of user they want to change
			 * if id is in buffer then user will be asked to change polarity and will report results
			 * in text area
			 */

			else if (command.equals("3")) {
				if (HANDLER.getTweetBuffer().size() == 0) {
					input.setText("No tweets in buffer.\nMust load tweet text file first.");

					return;
				}
				String tweetId = JOptionPane.showInputDialog(null, "Input Id of user");
				int targetId = Integer.parseInt(tweetId);
				int targetIndex = -1;
				for (AbstractTweet t : HANDLER.getTweetBuffer()) {
					if (t.getId() == targetId) {
						targetIndex = HANDLER.getTweetBuffer().indexOf(t);
					}
				}

				if (targetIndex == -1) {
					input.setText("Tweet not found.");
					return;
				} else {
					input.setText("Current polarity: " + HANDLER.getTweetBuffer().get(targetIndex).getTarget());
					int oldPolarity = HANDLER.getTweetBuffer().get(targetIndex).getTarget();
					String[] choices = { "0", "2", "4"};
					String stringPolarity = (String) JOptionPane.showInputDialog(null, "Please input the polarity. Must be 0, 2 or 4.",
							"Polarity Options", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
					int newPolarity = Integer.parseInt(stringPolarity);
					HANDLER.getTweetBuffer().get(targetIndex).setPredictedPolarity(newPolarity);
					input.append("\nPredicted polarity updated." + "\nTweet with Id: " + HANDLER.getTweetBuffer().get(targetIndex).getId() + 
							" was changed from " + oldPolarity + " to " + newPolarity);
				}
				
				logger.info("Change polarity");
			}

			/**
			 * if tweets are in buffer then they will be added to the database
			 * then a new jframe will appear with tweet database
			 */

			else if (command.equals("4")) {
				if (HANDLER.getTweetBuffer().size() == 0) {
					input.setText("No tweets in buffer.\nLoad tweets into buffer before adding to database.");
					return;
				}
				input.setText("Database size: " + HANDLER.getTweetDB().size());
				addTweets();
				input.append("\nBuffer size is now: " + HANDLER.getTweetBuffer().size());
				int choice = JOptionPane.showConfirmDialog(null, "Do you want to save the database to your file?");
				if (choice == JOptionPane.YES_OPTION) {
					HANDLER.saveSerialDB();
					input.append("\nDatabase saved. \n" + HANDLER.getTweetDB().size()  + " tweets now in the database.");
				}
				else {
					System.out.println("Data will not be saved to the file now.");
					input.append("\nDatabase not saved.\n" + HANDLER.getTweetDB().size() + " tweets now in the database.");
				}				
				showTable();
				
				logger.info("Add tweets");
			}

			/**
			 * if tweets are in database then gui will prompt user to input id of user they want to delete
			 * if id is in database then tweet will be deleted and new jtable will appear with updated data
			 */

			else if (command.equals("5")) {
				if (HANDLER.getTweetDB().size() == 0) {
					input.setText("Tweet database is empty.\nAdd tweets to database first.");
					return;
				}
				tweetId = JOptionPane.showInputDialog(null, "Enter id of the tweet you wish to delete");
				int idInteger = Integer.parseInt(tweetId);
				int location = -1;
				for (int i = 0; i < HANDLER.getTweetDB().size();i++) {
					if (HANDLER.getTweetDB().get(i).getId() == idInteger) {
						location = idInteger;
					}
				}
				if (location == -1) {
					input.setText("Tweet not found\n" + HANDLER.getTweetDB().size() + " : tweets in database still.");
					return;
				}
				deleteTweet();

				input.setText("Tweet with Id: " + tweetId + ", was deleted.\n"
						+ HANDLER.getTweetDB().size() + " : tweets now in database.");
				showDeletedTable();
				
				logger.info("Delete tweets");
			}
			
			/**
			 * prompts the user how they want to search
			 * depeding on their option the program searches database and reports tweets in text area
			 */

			else if (command.equals("6")) {
				if (HANDLER.getTweetDB().size() == 0) {
					input.setText("Tweet database is empty.\nAdd tweets to database first.");
					return;
				}
				String[] choices = { "1. Search by user.",
						"2. Search by date.",
						"3. Search by flag.",
				"4. Search by substring" };
				String panelOption = (String) JOptionPane.showInputDialog(null, "How would you like to search for tweets?",
						"Search Options", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);

				char option = panelOption.charAt(0);

				String charAt = Character.toString(option);

				if (charAt.equals("1")) {
					String userName = JOptionPane.showInputDialog("enter user name");
					List<AbstractTweet> resultList = new ArrayList<AbstractTweet>();
					resultList = HANDLER.searchByUser(userName);
					if (resultList.size() == 0) {
						input.setText("Tweet not found.");
						return;
					}
					else {
						input.setText(printList(resultList));
					}
				}

				else if (charAt.equals("2")) {
					try {
						String stringDate = JOptionPane.showInputDialog("enter date");
						SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
						Date date = formatter.parse(stringDate); 
						List<AbstractTweet> resultList = new ArrayList<AbstractTweet>();
						resultList = HANDLER.searchByDate(date);			
						if (resultList.size() == 0) {
							input.setText("Tweet not found.");
							return;
						}
						else {
							input.setText(printList(resultList));
						}
					} catch (ParseException excpetion) {
						System.out.println("Exception: fail to convert the date information.");
						excpetion.printStackTrace();
						throw new RuntimeException();
					}
				}

				else if (charAt.equals("3")) {
					String flag = JOptionPane.showInputDialog("enter flag");
					List<AbstractTweet> resultList = new ArrayList<AbstractTweet>();
					resultList = HANDLER.searchByFlag(flag);
					if (resultList.size() == 0) {
						input.setText("Tweet not found.");
						return;
					}
					else {
						input.setText(printList(resultList));
					}
				}

				else {
					String subString = JOptionPane.showInputDialog("enter substring");
					List<AbstractTweet> resultList = new ArrayList<AbstractTweet>();
					resultList = HANDLER.searchBySubstring(subString);
					if (resultList.size() == 0) {
						input.setText("Tweet not found.");
						return;
					}
					else {
						input.setText(printList(resultList));
					}
				}
				
				logger.info("Search tweet");
			}	
		}
	}

	public static void addTweets() {
		// Add all tweets in memory into database list
		HANDLER.addTweetsToDB(HANDLER.getTweetBuffer());
		System.out.println("Tweet buffer is now empty.");
	}

	public static void deleteTweet() {
		int deleteTweet = Integer.parseInt(tweetId);
		HANDLER.deleteTweet(deleteTweet);
	}
	
	/**
	 * formats list in order to diplay in text area
	 * @param target_List
	 * @return formatted string
	 */

	public static String printList(List<AbstractTweet> target_List) {
		String temp = "";
		String line = "----------------------------------------------------------------------------------"
				+ "----------------------------------------------------------------------------------\n";
		String information = "| ";
		information = information + String.format("%6s", "Target") + " | ";
		information = information + String.format("%6s", "Class") + " | ";
		information = information + String.format("%6s", "ID") + " | ";
		information = information + String.format("%30s", "Date") + " | ";
		information = information + String.format("%10s", "Flag") + " | ";
		information = information + String.format("%15s", "User") + " | ";
		information = information + String.format("%70s", "Text") + " |\n";

		temp += line;
		temp += information;
		temp += line;

		System.out.println(line);
		System.out.println(information);
		System.out.println(line);
		for (AbstractTweet t : target_List) {
			temp += t;
			temp += line;
			System.out.println(t);
			System.out.println(line);
		}
		return temp;
	}
	
	/**
	 * creates data to create jtable
	 * @return Object[][] with database data
	 */

	public static Object[][] create2DArray(){
		int row = HANDLER.getTweetDB().size();
		int column = 7;
		Object[][] array = new Object[row][column];
		for (int i = 0; i < HANDLER.getTweetDB().size();i++) {
			ArrayList<String> string = new ArrayList<String>();
			AbstractTweet tweet = HANDLER.getTweetDB().get(i);

			Integer tempTarget = tweet.getTarget();
			String target = tempTarget.toString();

			Integer tempId = tweet.getId();
			String id = tempId.toString();

			Date date = tweet.getDate();
			String dateString = date.toString();

			String flag = tweet.getFlag();

			Integer tempPolarity = tweet.getPredictedPolarity();
			String polarity = tempPolarity.toString();

			String user = tweet.getUser();

			String text = tweet.getText();

			string.add(target);
			string.add(id);
			string.add(dateString);
			string.add(flag);
			string.add(polarity);
			string.add(user);
			string.add(text);

			for (int j = 0; j < string.size(); j++) {
				String element = string.get(j);
				array[i][j] = element;
			}
		}
		return array;
	}
	
	/**
	 * displays jtable with database
	 */

	public static void showTable() {
		String[] columnNames = {"Target",
				"Id",
				"Date",
				"Flag",
				"Predicted Polarity",
				"User",
		"Text"};
		Object[][] data = create2DArray();
		JFrame frame = new JFrame("Tweet Database");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DefaultTableModel model = new DefaultTableModel(data,columnNames);
		table = new JTable(data, columnNames);
		table.setPreferredScrollableViewportSize(new Dimension(500,500));
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setVisible(true);
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.setSize(400, 400);
		frame.setVisible(true);
		table.updateUI();
	}
	
	/**
	 * shows new table without deleted tweet
	 */

	public static void showDeletedTable() {
		String[] columnNames = {"Target",
				"Id",
				"Date",
				"Flag",
				"Predicted Polarity",
				"User",
		"Text"};
		Object[][] data = create2DArray();
		JFrame frame = new JFrame("Deleted Table");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DefaultTableModel model = new DefaultTableModel(data,columnNames);
		deletedTable = new JTable(data, columnNames);
		deletedTable.setPreferredScrollableViewportSize(new Dimension(500,500));
		deletedTable.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(deletedTable);
		scrollPane.setVisible(true);
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.setSize(400, 400);
		frame.setVisible(true);
	}
}