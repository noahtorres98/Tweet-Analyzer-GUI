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
public class Model {

    private String user; 
    private String tweet; 
     
    public String getUser()  
    { 
        return user; 
    } 
     
    public void setUser(String user)  
    { 
        this.user = user; 
    } 
     
    public String getTweet()  
    { 
        return tweet; 
    } 
     
    public void setTweet(String tweet)  
    { 
        this.tweet = tweet; 
    }
    
}
