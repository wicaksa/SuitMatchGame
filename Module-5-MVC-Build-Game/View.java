/******
* VIEW CLASS TODO
      1. fix the deck icon on the left side
**********/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
/**************************************************************** 
 * 
 * 
 *       VIEW CLASS
 *       
 *       
 ***************************************************************/
public class View extends JFrame
{
   static int MAX_CARDS_PER_HAND = 56;
   static int MAX_PLAYERS = 2;
   private int numPlayers, numCardsPerHand;

   JPanel pnlGameBoard, pnlComputerHand, pnlHumanHand, pnlPlayArea, pnlScoreArea, pnlDeckArea;
   JPanel pnlPlayAreaCardButtons, pnlPlayAreaHumanButtons, pnlTimer, pnlScore;
   JLabel computerLabels[], humanLabels[], playAreaLabels[];
   JLabel count;
   JButton humanButtons[], playAreaCardButtons[], playAreaHumanButtons[];
   JLabel humanScoreLabel, computerScoreLabel;
   static JLabel clockLabel;
   
   int iconHeight, iconWidth;
   
   View(String title, int numCardsPerHand, int numPlayers)
   {
      // initialize
      super();
      this.setTitle(title);
      this.numCardsPerHand = numCardsPerHand;
      this.numPlayers = numPlayers;
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setSize(800,  600);
      this.setLayout(new BorderLayout());
      this.iconHeight = GUICard.getCardBackIcon().getIconHeight();
      this.iconWidth = GUICard.getCardBackIcon().getIconWidth();
      
      //instantiate the main area panels
      this.pnlComputerHand = new JPanel();
      this.pnlHumanHand = new JPanel();
      this.pnlPlayArea = new JPanel();
      this.pnlScoreArea = new JPanel();
      this.pnlDeckArea = new JPanel();
      
      // instantiate sub-panels
      this.pnlPlayAreaCardButtons = new JPanel();
      this.pnlPlayAreaHumanButtons = new JPanel();
      this.pnlScore = new JPanel();
      this.pnlTimer = new JPanel();
      
      // instantiate labels and buttons
      computerLabels = new JLabel[numCardsPerHand];
      playAreaCardButtons = new JButton[3]; // HARDCODED
      playAreaHumanButtons = new JButton[3]; // HARDCODED
      humanButtons = new JButton[numCardsPerHand];
   }

   public void drawGame(Card[] playAreaCards, Hand playerHand, int numCardsRemainingInDeck, ActionListener buttonEar)
   {
      //this.clearGUI(); 
      this.createComputerLabels();
      this.createHumanButtons(playerHand, buttonEar);
      this.createPlayArea(playAreaCards, buttonEar);
      this.createScoreArea();
      createDeckArea(numCardsRemainingInDeck);
      
      // add main panels to JFrame
      /*
      this.setLayout(new BorderLayout());
      this.add(pnlComputerHand, BorderLayout.NORTH);
      this.add(pnlPlayArea, BorderLayout.CENTER);
      this.add(pnlDeckArea, BorderLayout.WEST);
      this.add(pnlScoreArea, BorderLayout.EAST);
      this.add(pnlHumanHand, BorderLayout.SOUTH);
      */

      this.setLayout(new BorderLayout());
      this.add(pnlComputerHand, BorderLayout.NORTH);
      this.add(pnlPlayArea, BorderLayout.CENTER);      
      this.add(pnlHumanHand, BorderLayout.SOUTH);
      setVisible(true);
   }

   public void clearGUI()
   {
      this.pnlComputerHand.removeAll();
      this.pnlHumanHand.removeAll();
      this.pnlPlayArea.removeAll();
   }

   public void createComputerLabels()
   {
      pnlComputerHand.setBorder(new TitledBorder("Computer Hand"));
      // create labels
      // need to adjust for actual num cards in computer hand

      for (int i = 0; i < 7; i++)
      {
         computerLabels[i] = new JLabel(GUICard.getCardBackIcon());
         pnlComputerHand.add(computerLabels[i]);
      }
   }

   public void createHumanButtons(Hand hand, ActionListener buttonEar)
   {
      //create buttons
      // need to get actual numCardsInHand
      pnlHumanHand.setBorder(new TitledBorder("Your Hand"));
      for (int i = 0; i < hand.getNumCards(); i++)
      {
         humanButtons[i] = new JButton();
         humanButtons[i].setPreferredSize(new Dimension(iconWidth, iconHeight));
         humanButtons[i].setIcon(GUICard.getIcon(hand.inspectCard(i)));
         humanButtons[i].addActionListener(buttonEar);
         humanButtons[i].setActionCommand("Card" + i);

         pnlHumanHand.add(humanButtons[i]);
      }
   }

   public void createPlayArea(Card[] playAreaCards, ActionListener buttonEar )
   {  
      pnlPlayArea.setBorder(new TitledBorder("Play Area"));
      /*
      pnlPlayArea.setBorder(new TitledBorder(new EmptyBorder(10, 10, 10, 10), "Play Area"));
      Border border = pnlPlayArea.getBorder();
      Border lineBorder = new LineBorder(Color.BLACK);
      pnlPlayArea.setBorder(new CompoundBorder(border, lineBorder));
      */
      
      //add sub-panels to play area
      //pnlPlayArea.setLayout(new GridLayout(3, 2, 5, 5));
      pnlPlayArea.setLayout(new BorderLayout());
      pnlPlayArea.add(pnlDeckArea, BorderLayout.WEST);
      pnlPlayArea.add(pnlScoreArea, BorderLayout.EAST);
      pnlPlayArea.add(pnlPlayAreaCardButtons, BorderLayout.NORTH);
      pnlPlayArea.add(pnlPlayAreaHumanButtons, BorderLayout.SOUTH);
      
      // These are the buttons for cant' play and timer start/stop
      playAreaHumanButtons[0] = new JButton("I cannot play");
      playAreaHumanButtons[1] = new JButton("Start Timer");
      playAreaHumanButtons[2] = new JButton("Stop Timer");

      playAreaHumanButtons[0].addActionListener(buttonEar);
      playAreaHumanButtons[1].addActionListener(buttonEar);
      playAreaHumanButtons[2].addActionListener(buttonEar);

      playAreaHumanButtons[0].setActionCommand("HumanNoPlay");
      playAreaHumanButtons[1].setActionCommand("TimerStart");
      playAreaHumanButtons[2].setActionCommand("TimerStop");

      // these are the buttons for the three play area cards
      for (int i = 0; i < 3; i++)
      {
         playAreaCardButtons[i] = new JButton();
         playAreaCardButtons[i].setPreferredSize(new Dimension(iconWidth, iconHeight));
         playAreaCardButtons[i].setIcon(GUICard.getIcon(playAreaCards[i]));
         playAreaCardButtons[i].addActionListener(buttonEar);
         playAreaCardButtons[i].setActionCommand("MiddleCard" + i);

         pnlPlayAreaCardButtons.add(playAreaCardButtons[i]);
         pnlPlayAreaHumanButtons.add(playAreaHumanButtons[i]);
      }
   }

   public void createScoreArea()
   {
      int humanScore = 0, computerScore = 0;
      // add sub-panels to score area
      pnlScoreArea.setLayout(new GridLayout(3, 1));

      
      humanScoreLabel = new JLabel("Human Cannot Play: " + humanScore, JLabel.CENTER);
      computerScoreLabel = new JLabel("Computer Cannot Play: " + computerScore, JLabel.CENTER);
      clockLabel = new JLabel("00:00:00", JLabel.CENTER);
 
      pnlScoreArea.add(humanScoreLabel);
      pnlScoreArea.add(computerScoreLabel);
      pnlScoreArea.add(clockLabel);

   }
   
   public void createDeckArea(int numCardsRemainingInDeck)
   {

      //deck area
      pnlDeckArea.setLayout(new GridLayout(2, 2));
      pnlDeckArea.setPreferredSize(new Dimension(150, 200));
      JLabel deck = new JLabel();
      count = new JLabel("Cards Remaining: " + numCardsRemainingInDeck);

      deck.setIcon(GUICard.getCardBackIcon());
      deck.setSize(new Dimension(iconWidth, iconHeight));
      pnlDeckArea.add(deck);
      pnlDeckArea.add(count);
   }
   
   // changes a middle card when a new card is played
   // cardIndex is 0, 1, or 2
   public void setPlayedCardIcon(int cardIndex, Icon cardIcon)
   {
      playAreaCardButtons[cardIndex].setIcon(cardIcon);
   }
   
   public void updateHumanHand(Hand hand) 
   {
      for(int i = 0; i < numCardsPerHand; i++)
      {
         humanButtons[i].setIcon(GUICard.getIcon(hand.inspectCard(i)));
      }
   }
   
   public void updateCardsRemaining(int num)
   {
      count.setText("Cards Remaining: " + num);
      
   }
   
   public void updateScore(int playScore, int compScore)
   {
      humanScoreLabel.setText("Your Score: " + playScore);
      computerScoreLabel.setText("Computer Score: " + compScore);
   }
   
   public void displayWinning(int playScore, int compScore)
   {
      humanScoreLabel.setText("Game Over!");

      if(playScore < compScore)
         computerScoreLabel.setText("You Win!");
      else if (playScore == compScore)
         computerScoreLabel.setText("A Draw!");
      else
         computerScoreLabel.setText("You Lose.");
   }

   public static void setClockLabel(int minutes, int seconds) 
   {
     clockLabel.setText(String.format("%02d:%02d", minutes, seconds));
   }
}
