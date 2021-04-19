/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentimentanalysis;

import java.util.logging.*;
/**
 *
 * @author noah
 */
public class Observ implements ObservInterface {
    
    private static Logger LOGGER = Logger.getLogger(Observ.class.getName());

  @Override
  public void update(ObservEnum currentOb) {
    switch (currentOb) {
        
      case LOAD:
        LOGGER.info("Observer: LOAD");
        break;
      case CLASSIFY:
        LOGGER.info("Observer: CLASSIFY");
        break;
      case CHANGE:
        LOGGER.info("Observer: CHANGE");
        break;
      case ADD:
        LOGGER.info("Observer: ADD");
        break;
      case DELETE:
        LOGGER.info("Observer: DELETE");
        break;
      case SEARCH:
        LOGGER.info("Observer: SEARCH");
        break;
      default:
        break;
    }
  }    
}
