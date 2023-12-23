//====================================================================
//  BlackjackGame.java
//
//  Controls the logic for a game of Blackjack.
//
//  History:
//    2005.11.22-24 / Abe Pralle - Created
//====================================================================

public class BlackjackGame
{
  //----CLASS VARIABLES-----------------------------------------------
  final static public int
    STATUS_PLAYING          = 0,
    STATUS_PLAYER_BUST      = 1,
    STATUS_DEALER_BUST      = 2,
    STATUS_DEALER_WIN       = 3,
    STATUS_PLAYER_BLACKJACK = 4,
    STATUS_DEALER_BLACKJACK = 5,
    STATUS_TIE              = 6;

  //----INSTANCE VARIABLES--------------------------------------------
  protected CardSet deck;
  protected CardSet dealer_hand;
  protected CardSet player_hand;

  protected int     status;

  ////////////////////////////////////////////////////////////////////
  //  CONSTRUCTOR
  //  BlackjackGame()
  ////////////////////////////////////////////////////////////////////
  public BlackjackGame()
  {
    deck = new CardSet();
    deck.newDeck();

    dealer_hand = new CardSet();
    player_hand = new CardSet();
    newGame();
  }


  ////////////////////////////////////////////////////////////////////
  //  newGame()
  //    - Returns any cards in the dealer's and player's hands to the
  //      deck.
  //    - Shuffles the deck
  //    - Deals two cards each to the dealer and player.
  //    - Checks for dealer or player Blackjack.
  ////////////////////////////////////////////////////////////////////
  public void newGame()
  {
    // put last hands back into deck and shuffle
    deck.add( dealer_hand );
    deck.add( player_hand );

    dealer_hand.clear();
    player_hand.clear();

    deck.shuffle();

    // deal initial cards
    dealer_hand.add( deck.draw().turnFaceUp() );
    dealer_hand.add( deck.draw().turnFaceDown() );

    player_hand.add( deck.draw().turnFaceUp() );
    player_hand.add( deck.draw().turnFaceUp() );

    status = STATUS_PLAYING;

    // check for player or dealer blackjack
    if (bestScore(player_hand) == 21)
    {
      dealer_hand.turnFaceUp();

      if (bestScore(dealer_hand) == 21)
      {
        status = STATUS_TIE;
      }
      else
      {
        status = STATUS_PLAYER_BLACKJACK;
      }
    }
    else if (bestScore(dealer_hand) == 21)
    {
      dealer_hand.turnFaceUp();
      status = STATUS_DEALER_BLACKJACK;
    }
  }

  ////////////////////////////////////////////////////////////////////
  //  isGameOver() : boolean
  //    Returns true if the game is over.
  ////////////////////////////////////////////////////////////////////
  public boolean isGameOver() { return status != STATUS_PLAYING; }


  ////////////////////////////////////////////////////////////////////
  //  hit()
  //    Player requests an another card.  If the player's cards equal
  //    21, the dealer takes its turn.  If the player busts, the game
  //    is over.
  ////////////////////////////////////////////////////////////////////
  public void hit()
  {
    player_hand.add( deck.draw().turnFaceUp() );

    int player_score = bestScore(player_hand);
    if (player_score > 21)
    {
      status = STATUS_PLAYER_BUST;
    }
    else if (player_score == 21)
    {
      stay();  // let dealer take its turn
    }
  }

  ////////////////////////////////////////////////////////////////////
  //  stay()
  //    The dealer takes its turn; it continues drawing cards until
  //    it busts or ties/beats the player.
  ////////////////////////////////////////////////////////////////////
  public void stay()
  {
    // all is revealed
    dealer_hand.turnFaceUp();

    int player_score = bestScore(player_hand);
    int dealer_score = bestScore(dealer_hand);

    while (dealer_score < player_score)
    {
      dealer_hand.add( deck.draw().turnFaceUp() );
      dealer_score = bestScore(dealer_hand);

      if (dealer_score > 21) break;
    }

    if (dealer_score > 21)
    {
        status = STATUS_DEALER_BUST;
    }
    else if (dealer_score == player_score)
    {
      status = STATUS_TIE;
    }
    else status = STATUS_DEALER_WIN;
  }


  ////////////////////////////////////////////////////////////////////
  //  bestScore( hand : CardSet ) : int
  //    Calculates the score for a given hand of cards in the most 
  //    favorable way.  Aces count as 11 if they don't cause a bust,
  //    or 1 otherwise.
  ////////////////////////////////////////////////////////////////////
  public int bestScore( CardSet hand )
  {
    // loop through once and add the minimum value of
    // each card to the sum (ACE = 1)
    int score = 0;

    for (int i=0; i<hand.size(); ++i)
    {
      int rank = hand.getCard(i).getRank();
      if (rank == Card.ACE) score += 1;
      else if (rank >= Card.JACK && rank <= Card.KING)
      {
        score += 10;
      }
      else score += rank;
    }

    // Loop through again.  If a card is an Ace and
    // the total score is <= 11, add 10 more points
    // so the Ace is counting as 11 instead of 1.
    for (int i=0; i<hand.size(); ++i)
    {
      if (score > 11) break;
      if (hand.getCard(i).getRank() == Card.ACE) score += 10;
    }

    return score;
  }


  ////////////////////////////////////////////////////////////////////
  //  getPlayerHand() : CardSet
  //  getDealerHand() : CardSet
  //  
  //  Returns the current hand for the dealer or player.
  ////////////////////////////////////////////////////////////////////
  public CardSet getPlayerHand() { return player_hand; }
  public CardSet getDealerHand() { return dealer_hand; }


  ////////////////////////////////////////////////////////////////////
  //  getStatus() : int
  //  getStatusText() : String
  //
  //  Returns the current status as either an integer representing a
  //  more abstract situation or a more specific string.
  ////////////////////////////////////////////////////////////////////
  public int getStatus() { return status; }

  public String getStatusText() 
  {
    switch (status)
    {
      case STATUS_PLAYING: return "Choose your action...";
      case STATUS_PLAYER_BUST: return "You've busted!";
      case STATUS_DEALER_BUST: return "Dealer busts - you win!";
      case STATUS_DEALER_WIN:  return "Dealer wins!";
      case STATUS_PLAYER_BLACKJACK: return "You got Blackjack!";
      case STATUS_DEALER_BLACKJACK: return "Dealer got Blackjack!";
      case STATUS_TIE: return "Tie game!";
      default: return "ERROR: bad status!";
    }
  }
}

