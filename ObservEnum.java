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
public enum ObservEnum {
    
    LOAD, CLASSIFY, CHANGE, ADD, DELETE, SEARCH;
    
  @Override
  public String toString() {
    return this.name().toLowerCase();
    }
}
