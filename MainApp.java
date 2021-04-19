package sentimentanalysis;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class MainApp {

	/**
	 * @author Noah Torres
	 * @param args
	 * @throws Exception
	 * 
	 * This is the main function
	 */
	public static void main(String[]args) {

		TweetHandler th = new TweetHandler();

		List<AbstractTweet> tempDB = new ArrayList<>();

		Scanner scanner = new Scanner(System.in);
//		try {

		/**
		 * loops until user inputs 0
		 */
		int loop = -1;
		while (loop != 0) {
			

				System.out.println("\n" + "Choose an option 0-6" + "\n");

				System.out.println("0. Exit Program.\n" +
						"1. Load new tweet text file.\n" +
						"2. Classify tweets using NLP library and report accuracy.\n" +
						"3. Manually change tweet class label.\n" + 
						"4. Add new tweets to database.\n" +
						"5. Delete tweet from database (given its id).\n" +
						"6. Search tweets by user, date, flag, or a matching substring.\n");
				
//				try {


				int option = scanner.nextInt();
				


				/**
				 * saves the database to tweetDatabase
				 */
				if (option == 0) {
					th.saveSerialDB();
					loop = 0;
				}
				
				/**
				 * loads the tweets from csv file
				 */
				else if (option == 1) {
					System.out.println("type the file path name" + scanner.nextLine());
					String filePath = scanner.nextLine();
					tempDB = th.loadTweetsFromText(filePath);
				}
				
				/**
				 * classifies tweets if tweets have been added to perm database
				 */
				else if (option == 2) {
					double correct = 0;
					double total = 0;
					if (TweetHandler.perm.size() == 0) {
						System.out.print("you must first add tweets into the database in order to classify them\n");
						continue;
					}
					for (int i = 0; i < tempDB.size(); i++) {
						System.out.println("-----------------------------------------------------------------------------------------------------");
						AbstractTweet tweet = TweetHandler.perm.get(i);
						int target = th.classifyTweet(tweet);
						System.out.println("Calculated Target: " + target);
						System.out.println("Target: " + tweet.getTarget());
						System.out.println("Tweet: " + tweet.getText());
						System.out.println("User: " + tweet.getUser());
						System.out.println("Date: " + tweet.getDate());
						System.out.println(tweet.getId());
						System.out.println("-----------------------------------------------------------------------------------------------------");
						if (tweet.getTarget() == target) {
							correct++;
						}
						total++;				
					}
					double accuracy = correct/total;
					double percent = accuracy * 100;
					System.out.printf("%.2f", percent);
					System.out.println("%");
				}
				
				/**
				 * change the class of the tweet given an id
				 */
				
				else if (option == 3) {
					if (TweetHandler.perm.size() == 0) {
						System.out.println("you must first add tweets into the database");
						continue;
					}
					System.out.println("enter the id of the tweet you would like to change");
					int id = scanner.nextInt();
					for (int i = 0; i < TweetHandler.perm.size(); i++) {
						if (TweetHandler.perm.get(i).getId() == id) {
							System.out.println("what would you like to change class label to");
							int label = scanner.nextInt();
							TweetHandler.perm.get(i).setPredictedPolarity(label);
						}
					}
				}
				
				/**
				 * adds tweet to database if csv file was loaded
				 */

				else if (option == 4) {
					if (tempDB.size() == 0) {
						System.out.println("you must first load the tweet text file");
						continue;
					}
					th.addTweetsToDB(tempDB);
				}
				
				 /**
				  * deletes tweet from permanent database if tweets are in database
				  */

				else if (option == 5) {
					if (TweetHandler.perm.size() == 0) {
						System.out.println("you must first add tweets into the database in order to one\n");
						continue;
					}
					System.out.println("type an id to delete");
					int givenID = scanner.nextInt();
					for (int i = 0; i < TweetHandler.perm.size(); i++) {
						if (TweetHandler.perm.get(i).getId() == givenID) {
							th.deleteTweet(givenID);
						}
					}
					System.out.println(TweetHandler.perm.size());
				}				
				/**
				 * searches for tweets by user, date, flag, or substring
				 */
				else if (option == 6) {
					if (TweetHandler.perm.size() == 0) {
						System.out.println("you must first add tweets into the database in order to search for a tweet");
						continue;
					}
					System.out.println("how would you like to search for tweets?\n" +
							"1. user\n" +
							"2. date\n" +
							"3. flag\n" +
							"4. substring");
					int choice = scanner.nextInt();
					if (choice == 1) {
						System.out.println("type in the user you would like to search for" + scanner.nextLine());
						String userName = scanner.nextLine();
						List <AbstractTweet> list = new ArrayList<>();
						list = th.searchByUser(userName);
						System.out.println(list);
					}
					else if (choice == 2) {
						DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
						Date date = null;
						System.out.println("type the date you would like to search" + scanner.nextLine());
						String strDate = scanner.nextLine();
						try {
							date = formatter.parse(strDate);
						} catch (ParseException e) {
							System.out.println("parse exception caught");
						}
						System.out.println(th.searchByDate(date));
					}
					else if (choice == 3) {
						System.out.println("type in the flag you would like to search" + scanner.nextLine());
						String strFlag = scanner.nextLine();
						System.out.println(th.searchByFlag(strFlag));
					}
					else if(choice == 4) {
						System.out.println("type in the substring you would like to search" + scanner.nextLine());
						String subString = scanner.nextLine();
						System.out.println(th.searchBySubstring(subString));
					}
				}				
		}
		System.out.println("goodbye");
	}
}