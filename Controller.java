/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentimentanalysis;

/**
 *
 * @author noah
 */
public class Controller {
 
    private Model model; 
    private View view; 
  
    public Controller(Model model, View view) 
    { 
        this.model = model; 
        this.view = view; 
    } 
  
    public void set(String user, String tweet) 
    { 
        model.setUser(user);
        model.setTweet(tweet);
    } 
           
    public void updateView() 
    {                 
        view.printDetails(model.getUser(), model.getTweet()); 
    }
    
}
