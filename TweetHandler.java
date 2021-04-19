package sentimentanalysis;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class TweetHandler implements TweetHandlerInterface{
	
	static List<AbstractTweet> temp = new ArrayList<>();
	
	static List<AbstractTweet> perm = new ArrayList<>();

	
	public List<AbstractTweet> loadTweetsFromText(String filePath) {
		String line = null;
		int count = 0;
		List <AbstractTweet> list = new ArrayList();
		try {
			FileReader fr = new FileReader(filePath);
			Scanner inFile = new Scanner(fr);
			while(inFile.hasNextLine()) {

				line = inFile.nextLine();
				String[] temp = line.split("\",\"");

				String strTar = temp[0];
				StringBuilder sb = new StringBuilder(strTar);
				sb.deleteCharAt(0);
				String resultString = sb.toString();
				int target = Integer.parseInt(resultString);

				String strId = temp[1];
				int id = Integer.parseInt(strId);

				String flag = temp[3];

				String user = temp[4];

				String text = temp[5];

				DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
				Date date = null;
				
				try {
					date = formatter.parse(temp[2]);
				} catch (ParseException e) {
					System.out.println("parse exception caught");
				}

				AbstractTweet object = new Tweet(target,id,date,flag,user,text);

				list.add(object);
				count++;
			}			
			System.out.println(count);
			System.out.println("done...");
			inFile.close();	

		}	catch (FileNotFoundException e) {
			System.out.println("The file was not found.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

	
	public AbstractTweet parseTweetLine(String tweetLine) {
		String[] temp = tweetLine.split("\",\"");
		String strTar = temp[0];
		StringBuilder sb = new StringBuilder(strTar);
		sb.deleteCharAt(0);
		String resultString = sb.toString();
		int target = Integer.parseInt(resultString);

		String strId = temp[1];
		int id = Integer.parseInt(strId);

		DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		
		Date date = null;
		
		try {
			date = formatter.parse(temp[2]);
		} catch (ParseException e) {
			System.out.println("parse exception caught");
		}
		
		String flag = temp[3];

		String user = temp[4];

		String text = temp[5];

		Tweet object = new Tweet(target,id,date,flag,user,text);
		return object;
	}	
	
	public int classifyTweet(AbstractTweet tweet) {
		SentimentAnalyzer sa = new SentimentAnalyzer();
		String strTweet = tweet.getText();
		int classified = sa.getParagraphSentiment(strTweet);
		return classified;
	}

	public void addTweetsToDB(List<AbstractTweet> tweets) {
		System.out.println("loading...");
		List<AbstractTweet> temp;
		for (AbstractTweet tweet : tweets) {
			TweetHandler.perm.add(tweet);
		}
		System.out.println("done...");
		
	}

	@Override
	public void deleteTweet(int id) {
		for (int i = 0; i < perm.size(); i++) {
			if (perm.get(i).getId() == id) {
				perm.remove(i);
			}
		}
	}

	public void saveSerialDB() {
		try {
	         FileOutputStream fileOut = new FileOutputStream("tweetDatabase.ser");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(TweetHandler.perm);
	         out.close();
	         fileOut.close();
	         System.out.println("Serialized data is saved in tweetDatabase.ser");
	      } catch (IOException i) {
	         i.printStackTrace();
	      }		
	}

//	@SuppressWarnings("unchecked")
//	@Override
	public void loadSerialDB() {
		try {
			FileInputStream fis = new FileInputStream("tweetDatabase.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);		
			TweetHandler.perm = (List<AbstractTweet>) ois.readObject();
		} catch (IOException | ClassNotFoundException i) {
			i.printStackTrace();
		}
	}

	
	public List<AbstractTweet> searchByUser(String user) {
		List <AbstractTweet> temp = new ArrayList<>();
		for (int i = 0; i < perm.size();i++) {
			if (perm.get(i).getUser().equals(user)) {
				temp.add(perm.get(i));
			}
		}
		return temp;
	}

	
	public List<AbstractTweet> searchByDate(Date date) {
		List <AbstractTweet> temp = new ArrayList<>();
		for (int i = 0; i < perm.size(); i++) {
			if (perm.get(i).getDate().equals(date)) {
				temp.add(perm.get(i));
			}
		}
		return temp;
	}

	@Override
	public List<AbstractTweet> searchByFlag(String flag) {
		List <AbstractTweet> temp = new ArrayList<>();
		for (int i = 0; i < perm.size(); i++) {
			if (perm.get(i).getFlag().equals(flag)) {
				temp.add(perm.get(i));
			}
		}
		return temp;
	}

	@Override
	public List<AbstractTweet> searchBySubstring(String substring) {
		List <AbstractTweet> temp = new ArrayList<>();
		for (int i = 0; i < perm.size(); i++) {
			if (perm.get(i).getText().toLowerCase().contains(substring.toLowerCase())) {
				temp.add(perm.get(i));
			}
		}
		return temp;
	}

}