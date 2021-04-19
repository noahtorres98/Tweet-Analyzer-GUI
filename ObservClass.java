/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentimentanalysis;

import java.util.*;
import java.util.logging.Logger;

/**
 *
 * @author noah
 */
public class ObservClass {
    
    private static Logger Log = Logger.getLogger(Observer.class.getName());
    
    private ObservEnum currentOb;
    
    private List<ObservInterface> observers;
  
    /**
     *
     */
    public ObservClass(){
        
        observers = new ArrayList<>();
        currentOb = ObservEnum.LOAD; 
        
    }//end ObserverClass

    public void addObserver(ObservInterface obs) {
      observers.add(obs);
    }

    public void removeObserver(ObservInterface obs) {
      observers.remove(obs);
    }
    
    //message
    public void message(String string) {
       
        switch(string){
            
            case "Load":
                // Load new tweet text file.
                currentOb = ObservEnum.LOAD;
                break;
            case "Classify":
                // Classify tweets using NLP library and report accuracy.
                currentOb = ObservEnum.CLASSIFY;
                break;
            case "Change":
                // Manually change tweet class label.
                currentOb = ObservEnum.CHANGE;
                break;
            case "Add":
                // Add new tweets to database.
                currentOb = ObservEnum.ADD;
            case "Delete":
                // Delete tweet from database (given its id).
                currentOb = ObservEnum.DELETE;
                break;
            case "Search":
                // Search for tweet.
                currentOb = ObservEnum.SEARCH;
                break;           
            
        }//end switch
        
        notifyObservers();
    }

    private void notifyObservers() {
      for (ObservInterface obs : observers) {
        obs.update(currentOb);
      }
      
    }
}
