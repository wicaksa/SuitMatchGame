/*  MODEL CLASS TODO
*         1. need a Card array to represent the three cards dealt into the middle
                need deal() to put cards into that array
                need some redeal() to put 3 new cards when no one can play
          2. way to see if a card is valid to play
                compare middle card to played card
                one higher or lower in value is valid
                what if it isn't valid?
*
*
*
*/

import java.util.Random;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/*****************************************************************
 * 
 * 
 *       Model CLASS
 *       
 *       
 ****************************************************************/
class Model
{
 private static final int MAX_PLAYERS = 50;

 private int numPlayers;
 private int numPacks;                  // # standard 52-card packs per deck
                                        // ignoring jokers or unused cards
 private int numJokersPerPack;          // if 2 per pack & 3 packs per deck, get 6
 private int numUnusedCardsPerPack;     // # cards removed from each pack
 private int numCardsPerHand;           // # cards to deal each player
 private Deck deck;                     // holds the initial full deck and gets
                                        // smaller (usually) during play
 private Hand[] hand;                   // one Hand for each player
 private Card[] unusedCardsPerPack;     // an array holding the cards not used
                                        // in the game.  e.g. pinochle does not
                                        // use cards 2-8 of any suit
 private Card[] playAreaCards;
 private int playerScore, computerScore;
 

 public Model( int numPacks, 
               int numJokersPerPack,
               int numUnusedCardsPerPack,  
               Card[] unusedCardsPerPack,
               int numPlayers, 
               int numCardsPerHand)
 {
    int k;

    // filter bad values
    if (numPacks < 1 || numPacks > 6)
    {
       numPacks = 1;
    }
    if (numJokersPerPack < 0 || numJokersPerPack > 4)
    {
       numJokersPerPack = 0;
    }
    if (numUnusedCardsPerPack < 0 || numUnusedCardsPerPack > 50) //  > 1 card
    { 
       numUnusedCardsPerPack = 0;
    }
    if (numPlayers < 1 || numPlayers > MAX_PLAYERS)
    {
       numPlayers = 4;
    }
    // one of many ways to assure at least one full deal to all players
    if  (numCardsPerHand < 1 || numCardsPerHand >  numPacks * (52 - numUnusedCardsPerPack) / numPlayers )
    {
       numCardsPerHand = numPacks * (52 - numUnusedCardsPerPack) / numPlayers;
    }

    // allocate
    this.unusedCardsPerPack = new Card[numUnusedCardsPerPack];
    this.hand = new Hand[numPlayers];
    for (k = 0; k < numPlayers; k++)
    {
       this.hand[k] = new Hand();
    }
    deck = new Deck(numPacks);
    
    //allocate playAreaHand
    this.playAreaCards = new Card[3];

    // assign to members
    this.numPacks = numPacks;
    this.numJokersPerPack = numJokersPerPack;
    this.numUnusedCardsPerPack = numUnusedCardsPerPack;
    this.numPlayers = numPlayers;
    this.numCardsPerHand = numCardsPerHand;
    for (k = 0; k < numUnusedCardsPerPack; k++)
    {
       this.unusedCardsPerPack[k] = unusedCardsPerPack[k];
    }

    // prepare deck and shuffle
    newGame();
 }

 // constructor overload/default for game like bridge
 public Model()
 {
    this(1, 0, 0, null, 4, 13);
 }

 public Hand getHand(int k)
 {
    // hands start from 0 like arrays
    // on error return automatic empty hand
    if (k < 0 || k >= numPlayers)
    {
       return new Hand();
    }
    return hand[k];
 }

 public Card getCardFromDeck() 
 { 
    return deck.dealCard(); 
 }

 public int getNumCardsRemainingInDeck() 
 { 
    return deck.getNumCards(); 
 }

 public void newGame()
 {
    int k, j;

    // clear the hands
    for (k = 0; k < numPlayers; k++)
    {
       hand[k].resetHand();
    }

    //clear cards in playArea
    for (int i = 0; i < numPlayers; i++)
    {
       playAreaCards[i] = null;
    }

    // restock the deck
    deck.init(numPacks);

    // remove unused cards
    for (k = 0; k < numUnusedCardsPerPack; k++)
    {
       deck.removeCard(unusedCardsPerPack[k]);
    }

    // add jokers
    for (k = 0; k < numPacks; k++)
    {
       for ( j = 0; j < numJokersPerPack; j++)
       {
          deck.addCard(new Card('X', Card.Suit.values()[j]));
       }
    }

    computerScore = 0;
    playerScore = 0;

    // shuffle the cards
    deck.shuffle();
 }

 public boolean deal()
 {
    // returns false if not enough cards, but deals what it can
    int k, j;
    boolean enoughCards;

    // clear all hands
    for (j = 0; j < numPlayers; j++)
       hand[j].resetHand();

    enoughCards = true;

    for (k = 0; k < numCardsPerHand && enoughCards ; k++)
    {
       for (j = 0; j < numPlayers; j++)
          if (deck.getNumCards() > 0)
             hand[j].takeCard( deck.dealCard() );
          else
          {
             enoughCards = false;
             break;
          }
    }
    
    for(int i = 0; i < playAreaCards.length && enoughCards; i++)
    {
       if (deck.getNumCards() > 0)
       {
          playAreaCards[i] = new Card(deck.dealCard());
       }
       else
       {
          enoughCards = false;
          break;
       }
    }

    return enoughCards;
 }

 public void sortHands()
 {
    int k;

    for (k = 0; k < numPlayers; k++)
    {
       hand[k].sort();
    }
 }

 public Card playCard(int playerIndex, int cardIndex)
 {
    // returns bad card if either argument is bad
    if (playerIndex < 0 ||  playerIndex > numPlayers - 1 ||
          cardIndex < 0 || cardIndex > numCardsPerHand - 1)
    {
       //Creates a card that does not work
       return new Card('M', Card.Suit.spades);      
    }

    // return the card played
    return hand[playerIndex].playCard(cardIndex);
 }

 public boolean takeCard(int playerIndex)
 {
    // returns false if either argument is bad
    if (playerIndex < 0 || playerIndex > numPlayers - 1)
    {
       return false;
    }

    // Are there enough Cards?
    if (deck.getNumCards() <= 0)
    {
       return false;
    }

    return hand[playerIndex].takeCard(deck.dealCard());
 }

 public int getnumPacks()
 {
    return this.numPacks;
 }

 public int getNumJokers()
 {
    return this.numJokersPerPack;
 }

 public int getNumPlayers()
 {
    return this.numPlayers;
 }
 
 public int getPlayAreaCardsLength() 
 {
    return playAreaCards.length;
 }

 public Card[] getPlayAreaCards()
 {
   return playAreaCards;
 }
 
 public Card getPlayAreaCardAtIndex(int index)
 {
    if(playAreaCards[index] != null)
    {
       return playAreaCards[index];
    }
    return new Card('I', Card.Suit.spades);
 }
 
 public int getNumCardsPerHand()
 {
    return numCardsPerHand;
 }
 
 public void setPlayAreaCardAtIndex(Card card, int index)
 {
    if(index >= 0 && index < playAreaCards.length)
    {
       playAreaCards[index] = new Card(card);
    }
 }

 public  void incrementPlayerScore() 
 {
    playerScore++;
 }
 
 public void incrementComputerScore()
 {
    computerScore++;
 }
 
 public int getPlayerScore() {
    return playerScore;
 }
 
 public int getComputerScore()
 {
    return computerScore;
 }
 
 public void dealThreeCards() 
 {
    for(int i = 0; i < playAreaCards.length; i++)
    {
       playAreaCards[i] = deck.dealCard();
    }
 }
}

/*****************************************************************
 * 
 * 
 * 
 *       GUICard CLASS
 *       
 *       
 ****************************************************************/
class GUICard
{
   private static Icon[][] iconCards = new ImageIcon[14][4];
   private static Icon iconBack;
   private static boolean iconsLoaded = false;

   static void loadCardIcons()
   {
      if(iconsLoaded)
         return;
      String values = "A23456789TJQKX";
      String suits = "CHSD";
      for (int i = 0; i < suits.length(); i++)
      {
         for (int j = 0; j < values.length(); j++)
         {
            String path = "images/" + values.charAt(j) + suits.charAt(i) + ".gif";
            iconCards[j][i] = new ImageIcon(path);
         }
      }
      iconBack = new ImageIcon("images/BK.gif");
      iconsLoaded = true;
   }

   public static Icon getIcon(Card card)
   {
      loadCardIcons();
      return iconCards[card.getValueAsInt()][card.getSuitAsInt()]; 
   }

   public static Icon getCardBackIcon()
   {
      loadCardIcons();
      return iconBack;
   }

}
/*****************************************************************
 * 
 * 
 * 
 *       CARD CLASS
 *       
 *       
 ****************************************************************/
class Card
{
   public enum Suit {clubs, hearts, spades, diamonds}
   private char value;
   private Suit suit;
   private boolean errorFlag = false;
   public final static char[] valuRanks = new char[] {'A', '2', '3', '4', 
         '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'X'};

   public Card()
   {
      this.set('A', Suit.spades);
   }

   public Card(char value, Suit suit)
   {
      this.set(value, suit);
   }

   public Card(Card other)
   {
      this.set(other.getValue(), other.getSuit());
   }

   public String toString()
   {
      if (this.errorFlag == true)
      {
         return "[Invalid Card]";
      }
      else
      {
         return this.value + " of " + this.suit;
      }
   }

   public boolean set(char value, Suit suit)
   {
      if (isValid(value, suit))
      {
         this.value = value;
         this.suit = suit;
         this.errorFlag = false;
         return true;
      }
      else {
         {
            this.value = 'A';
            this.suit = Suit.spades;
            this.errorFlag = true;
            return false;
         }
      }
   }

   public char getValue()
   {
      return this.value;
   }

   public Suit getSuit()
   {
      return this.suit;
   }

   public boolean getErrorFlag()
   {
      return this.errorFlag;
   }

   public boolean equals(Card card)
   {
      return (this.value == card.value && this.suit == card.suit);
   }

   private boolean isValid(char value, Suit suit)
   {
      for (char val : valuRanks)
      {
         if (val == value)
         {
            return true;
         }
      }
      return false;
   }

   static void arraySort(Card[] cards, int arraySize)
   {
      // bubble sort
      Card temp;
      for (int i = 0; i < arraySize; i++)
      {
         for (int j = 1; j < (arraySize - i); j++)
         {
            if (cards[j - 1].getCardAsInt() > cards[j].getCardAsInt())
            {
               temp = new Card(cards[j - 1]);
               cards[j - 1] = new Card(cards[j]);
               cards[j] = new Card(temp);
            }
         }
      }
   }

   public boolean cardGreaterThan(Card other)
   {
      if(this.getCardAsInt() > other.getCardAsInt())
      {
        return true;
      }
      else 
      {
        return false;
      }
   }

   public int getValueAsInt()
   {
      for (int i = 0; i < valuRanks.length; i++)
      {
         if (this.getValue() == valuRanks[i])
            return i;
      }
      return -1; //error
   }

   public int getSuitAsInt()
   {
      for (int i = 0; i < Card.Suit.values().length; i++)
      {
         if (this.getSuit() == Card.Suit.values()[i])
            return i;
      }
      return -1; //error
   }

   public int getCardAsInt()
   {
      return ((4 * this.getValueAsInt()) + this.getSuitAsInt());
   }
}

/*************************************************************************
 * 
 * 
 * 
 *          Hand Class
 * 
 * 
 * 
 **************************************************************************/
class Hand
{
   public static final int MAX_CARDS = 100;
   private Card[] myCards;
   private int numCards;

   Hand()
   {
      this.myCards = new Card[MAX_CARDS];
      this.numCards = 0;
   }

   public void resetHand()
   {
      this.myCards = new Card[MAX_CARDS];
      this.numCards = 0;
   }

   public boolean takeCard(Card card)
   {
      if (numCards <= MAX_CARDS)
      {
         this.myCards[this.numCards]= new Card(card);
         numCards++;
         return true;
      }
      return false;
   }

   public Card playCard()
   {
      if (numCards > 0)
      {
         numCards--;
         Card playCard = new Card(myCards[numCards]);
         myCards[numCards] = null;
         return playCard;
      }
      return null;
   }

   public Card playCard(int cardIndex)
   {
      if (this.numCards == 0)
      {
         return new Card('I', Card.Suit.spades);
      }
      Card card = myCards[cardIndex];
      numCards--;

      for (int i = cardIndex; i < numCards; i++)
      {
         myCards[i] = myCards[i + 1];
      }

      myCards[numCards] = null;
      return card;
   }

   public String toString()
   {
      if(numCards == 0)
         return "Hand = ( )";
      String fullHand = "Hand = (";

      for(int i = 0; i < numCards - 1; i++)
      {
         if(myCards[i] != null)
            fullHand += " " + myCards[i].toString() + "," ;
      }

      fullHand += " " + myCards[numCards - 1].toString() + " )" ;

      return fullHand;
   }

   public int getNumCards()
   {
      return this.numCards;
   }

   public Card inspectCard(int k)
   {
      if (k < 0 || k > numCards)
      {
         return new Card('I', Card.Suit.spades);
      }
      return myCards[k];
   }

   public void sort()
   {
      Card.arraySort(myCards, myCards.length);
   }
}

/*****************************************************************
 * 
 * 
 * 
 *       DECK CLASS
 *       
 *       
 ****************************************************************/

class Deck
{
   public final int MAX_CARDS = 6 * 56;
   private static Card[] masterPack = new Card[52];
   private int numPacks;
   private Card[] cards;
   private int topCard;

   Deck()
   {
      allocateMasterPack();
      cards = new Card[56];
      this.numPacks = 1;
      init(numPacks);
   }

   Deck(int numPacks)
   {
      allocateMasterPack();
      cards = new Card[56 * numPacks];
      this.numPacks = numPacks;
      init(numPacks);
   }

   public void init(int numPacks)
   {
      topCard = 0;
      for (int i = 0; i < numPacks; i++)
      {
         for (Card card : masterPack)
         {
            cards[topCard] = new Card(card);
            topCard++;
         }
      }
   }

   private static void allocateMasterPack()
   {
      if(masterPack[0] != null)
      {
         return;
      }
      int masterCardCount = 0;
      for (Card.Suit suit : Card.Suit.values())
      {
         for (char value : Card.valuRanks)
         {
            // Jokers do not go into masterPack
            if (value != 'X')
            {
               masterPack[masterCardCount] = new Card(value, suit);
               masterCardCount++;
            }
         }
      }
   }

   public void shuffle()
   {
      Random rand = new Random();
      int numCards = topCard;
      for (int i = 0; i < numCards; i++)
      {

         int randomCard = 1 + rand.nextInt(numCards - 1);
         Card temp = new Card(cards[randomCard]);
         cards[randomCard] = new Card(cards[i]);
         cards[i] = new Card(temp);
      }
   }

   public Card dealCard()
   {
      Card newCard;
      if (topCard > 0 && cards[topCard - 1] != null)
      {
         newCard = new Card(cards[topCard - 1]);
         cards[topCard - 1] = null;
         topCard--;
         return newCard;
      }
      else 
      {
         return new Card('I', Card.Suit.spades);
      }
   }

   public int getTopCard()
   {
      return this.topCard;
   }

   public Card inspectCard(int k)
   {
      if (k < cards.length && k >= 0)
      {
         return new Card(cards[k]);
      }
      else return new Card('I', Card.Suit.spades);
   }

   public boolean addCard(Card card)
   {
      // First check to see how many instances of the card are present
      int cardInstances = 0;
      for (int i = 0; i < cards.length; i++)
      {
         if (cards[i] != null && cards[i].equals(card))
         {
            cardInstances++;
         }
      }
      // if there are fewer instances than there should be in numPacks decks,
      // add card to top of deck
      if (cardInstances < numPacks)
      {
         cards[topCard] = new Card(card); // not sure about topCard - 1
         topCard++;
         return true;
      }
      else
         System.out.println("Too many of that card!");
      return false;
   }

   public boolean removeCard(Card card)
   {
      for (int i = 0; i < topCard; i++)
      {
         if (cards[i].equals(card))
         {
            cards[i] = new Card(cards[topCard - 1]);
            cards[topCard - 1] = null;
            topCard--;
            return true;
         }
      }
      return false;
   }

   public void sort()
   {
      Card.arraySort(cards, this.topCard);
   }

   public int getNumCards()
   {
      return this.topCard;
   }

}