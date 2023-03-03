
/*  Controller CLASS TODO
          1. implement taking turns
          2. CPU play card logic
              test each hand card against each middle card until one can be played
              use method from Model class to compare cards??
              if no card can be played, CPU passes
          3. human play card logic
          4. game timer on its own thread 
          5. scoring, win condition/game end 
*         6. redraw the game when the data changes
          7. add sleep timers??
          8. ?????????
          9. profit
*
*/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.util.*;

/****************************************************************
 * 
 * 
 * CONTROLLER CLASS
 * 
 * 
 ***************************************************************/

public class Controller implements ActionListener {
  private Model gameModel;
  private View gameView;
  private Card tempCard;
  private int tempCardIndex;
  private boolean playTurn = true;
  public static boolean stopTimer;

  Controller(Model gameModel, View gameView) {
    this.gameModel = gameModel;
    this.gameView = gameView;
    GUICard.loadCardIcons();
    gameModel.deal();

    // should probably not pass gameModel to reduce coupling
    // maybe only pass gameModel.getHand(1)??
    // -MS
    Hand playerHand = gameModel.getHand(1);
    int numCardsRemainingInDeck = gameModel.getNumCardsRemainingInDeck();
    Card[] playAreaCards = gameModel.getPlayAreaCards();
    gameView.drawGame(playAreaCards, playerHand, numCardsRemainingInDeck, this);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String event = e.getActionCommand();
    Timer timerThread;
    // event if player hand card is clicked
    if ("Card".equals(event.substring(0, 4))) {
      /*
       * use a model.getHand(1).inspectCard((int)event.substring(4)) to store the card
       * value in a temp Card object compare the tempCard to a middleCard that is
       * clicked if the value is one higher or lower, play the card into middle stack
       * 
       */
      if (gameModel.getNumCardsRemainingInDeck() > 0) {
        int handIndex = Integer.parseInt(event.substring(4));
        Card card = new Card(gameModel.getHand(1).inspectCard(handIndex));
        tempCardIndex = handIndex;
        tempCard = card;
        System.out
          .println("Clicked play area card:\t" + tempCard + " " + handIndex);
      }else{
        gameView.displayWinning(gameModel.getPlayerScore(), gameModel.getComputerScore());
      }

      
      // System.out.print(gameModel.getHand(1).inspectCard(handIndex));
      // System.out.println(" at index:\t" + event.substring(4));
    } else if ("HumanNoPlay".equals(event)) {
      /*
       * If you cannot play, click a button that says "I cannot play". The the
       * computer gets a second turn. Same for you, a second turn if the computer
       * cannot play. If neither of you can play, then the deck puts a new card on
       * each of the three stacks in the middle of the table.
       */
      System.out.println("Human cannot play button pressed");

      playTurn = false;

      int playAreaCardslen = gameModel.getPlayAreaCardsLength();

      gameModel.incrementPlayerScore();
      gameView.updateScore(gameModel.getPlayerScore(), gameModel.getComputerScore());

      if (gameModel.getNumCardsRemainingInDeck() <= 0)
      {
        gameView.displayWinning(gameModel.getPlayerScore(), gameModel.getComputerScore());
        
      } 
      else if (computerPlay()) 
      {
        // Comp plays a card. Game continues.
      } 
      else 
      {
        System.out.println("Both side pressed Cannot play.");
        gameModel.dealThreeCards();

        for (int i = 0; i < playAreaCardslen; i++) 
        {
          Icon icon = GUICard.getIcon(gameModel.getPlayAreaCardAtIndex(i));
          gameView.setPlayedCardIcon(i, icon);
        }

        gameView.updateCardsRemaining(gameModel.getNumCardsRemainingInDeck());

      }
    }
    // event if timer start/stop is clicked
    else if ("Timer".equals(event.substring(0, 5))) {
      // event.substring(5) can be "start" or "stop"
      // System.out.println("Timer: " + event.substring(5));
      timerThread = new Timer();
      timerThread.start();

      if ("Start".equals(event.substring(5))) {
        stopTimer = false;
      }

      if ("Stop".equals(event.substring(5))) {
        stopTimer = true;
      }

    }
    // event if community card is clicked
    else if ("MiddleCard".equals(event.substring(0, 10))) {

      if (gameModel.getNumCardsRemainingInDeck() <= 0) 
      {
        gameView.displayWinning(gameModel.getPlayerScore(), gameModel.getComputerScore());
      } 
      else
      {
          
      int index = Integer.parseInt(event.substring(10));
      // Set icon to different card -- NEED TO GET CORRECT CARD ICON THAT
      // IS BEING PLAYED
      if (tempCard != null) {
        Card playAreaCard = gameModel.getPlayAreaCardAtIndex(index);
        int playAreaCardValue = playAreaCard.getValueAsInt();
        int tempCardValue = tempCard.getValueAsInt();

        // if selected card valid, update view
        if (Math.abs(playAreaCardValue - tempCardValue) == 1) {
          gameModel.setPlayAreaCardAtIndex(tempCard, index);
          gameModel.getHand(1).playCard(tempCardIndex);

          Card newCard = gameModel.getCardFromDeck();
          gameModel.getHand(1).takeCard(newCard);
          gameView.setPlayedCardIcon(index, GUICard.getIcon(tempCard));
          gameView.updateHumanHand(gameModel.getHand(1));

          gameView.updateScore(gameModel.getPlayerScore(), gameModel.getComputerScore());
          gameView.updateCardsRemaining(gameModel.getNumCardsRemainingInDeck());

          playTurn = false;
        }

        if (computerPlay()) {
          // Do nothing. Game goes on.
        }
        // computer did not play a card
        else {
          gameView.updateScore(gameModel.getPlayerScore(), gameModel.getComputerScore());

          if (gameModel.getNumCardsRemainingInDeck() <= 0) {
            System.out.println("No more card from Deck. Game Over.");
            // Display game over message
          }
          System.out.println("No card from computer is played. " + "Player's turn again.");
        }
      }
      
      }

    }
  }

  private boolean computerPlay() {

    if(playTurn)
    {
      return false;
    }

    if (gameModel.getNumCardsRemainingInDeck() > 0 ) {
      Hand computerHand = gameModel.getHand(0);

      for (int i = 0; i < gameModel.getPlayAreaCardsLength(); i++) {
        Card card = gameModel.getPlayAreaCardAtIndex(i);
        int cardValue = card.getValueAsInt();
        for (int j = 0; j < gameModel.getNumCardsPerHand(); j++) {
          Card compCard = computerHand.inspectCard(j);
          int compCardVal = compCard.getValueAsInt();
          System.out.println(
              "PACard: " + i + ", comp: " + j + ", PACValue: " + cardValue + ", CompCardValue: " + compCardVal);

          if (Math.abs(cardValue - compCardVal) == 1) {
            System.out.println(
                "Correct Value in here. *** Math.abs(cardValue - compCardVal): " + Math.abs(cardValue - compCardVal));
            System.out.println("Comp played: " + computerHand.playCard(j));

            gameModel.setPlayAreaCardAtIndex(compCard, i);
            gameView.setPlayedCardIcon(i, GUICard.getIcon(compCard));

            computerHand.takeCard(gameModel.getCardFromDeck());

            gameView.updateCardsRemaining(gameModel.getNumCardsRemainingInDeck());

            if (gameModel.getNumCardsRemainingInDeck() <= 0)
            {
              gameView.displayWinning(gameModel.getPlayerScore(), gameModel.getComputerScore());
            } 
            
            playTurn = true;
            return true;
          }
        }
      }
    }
    gameModel.incrementComputerScore();
    gameView.updateScore(gameModel.getPlayerScore(), gameModel.getComputerScore());
    return false;
  }
}

/**
 *
 * Timer class that represents a stopwatch in the game. It has the ability to be
 * started and stopped while the main game runs.
 *
 */
class Timer extends Thread {
  // private static String timeInString;
  // private static String secondsInString;
  // private static String minutesInString;
  private int PAUSE = 1000;
  private int seconds = 0;
  private int minutes = 0;

  @Override
  public void run() {
    while (!Controller.stopTimer) {
      for (int i = 0; i < 60; i++) {
        if (Controller.stopTimer) {
          break;
        }
        doNothing(PAUSE);
        System.out.printf("%02d:%02d%n", minutes, seconds);
        //secondsInString = Integer.toString(seconds);
        //minutesInString = Integer.toString(minutes);
        // should do a getClockLabel() in View based on MVC design pattern
        //View.clockLabel.setText(String.format("%02d:%02d", minutes, seconds));
        View.setClockLabel(minutes, seconds);
        seconds++;

        if (seconds == 60) {
          seconds = 0;
          minutes++;
        }
      }
    }
  }

  private void doNothing(int milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      System.out.println("Unexpected interrupt");
      System.exit(0);
    }
  }
}

// -------------------------------------------------------------------------------------------------------------

/*
 * class Timer extends Thread { // data members private long startTime; private
 * boolean isTimerStarted;
 * 
 * // constructor public void startTimer() { this.startTime =
 * System.currentTimeMillis(); this.isTimerStarted = true; this.start(); }
 * 
 * public void stopTimer() { this.isTimerStarted = false; }
 * 
 * // Make a call to a doNothing() method that will use the sleep() method of
 * the Thread class. // Overrides the run() method. // Put all of the needed
 * timer code in the run() method.
 * 
 * @Override public void run() {
 * 
 * // Note: The method Thread.sleep can throw an InterruptedException , which is
 * a checked exception— that is, it must be either caught in a catch block or
 * declared in // a throws clause.
 * 
 * 
 * while (true) { long timeTotal = System.currentTimeMillis() - this.startTime;
 * int [] timerArr = new int [4]; timerArr [0] = (int)(timeTotal / 3600000); //
 * hours timerArr [1] = (int)(timeTotal / 60000); // min timerArr [2] =
 * (int)(timeTotal /1000); // sec
 * 
 * String text = (Integer.toString(timerArr[0]) + ":" +
 * Integer.toString(timerArr[1]) + ":" + Integer.toString(timerArr[2]));
 * View.clockLabel.setText(text); } }
 * 
 * //The InterruptedException has to do with one thread interrupting another
 * thread. The book simply notes that an InterruptedException may be thrown by
 * Thread.sleep //and so must be accounted for. The example uses a simple catch
 * block. The class InterruptedException is in the java. lang package and so
 * requires no import //statement. //You will need an actionPerformed() method
 * in the main() class to create an object of the Timer class and call start().
 * 
 * void doNothing(int milliseconds) { try { Thread.sleep(milliseconds); }
 * catch(InterruptedException e) { System.out.println("Unexpected interrupt");
 * System.exit(0); } }
 * 
 * /* void startTimer() { Timer timer = new Timer(); Thread thread = new
 * Thread(timer); thread.start(); }
 * 
 * long pauseTimer() { // pause the current time and store it in variable long
 * currentTime = System.currentTimeMillis();
 * 
 * // while timer is paused, do nothing, while (isTimerPaused) { doNothing(0);
 * pausedTime = System.currentTimeMillis() - currentTime; } return currentTime -
 * startTime - pausedTime; }
 * 
 * }
 * 
 */
