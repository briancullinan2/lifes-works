//====================================================================
//  BlackjackTextUI.java
//
//  Uses a console text-based interface to play a game of Blackjack.
//
//  History:
//    2005.11.23-24 / Abe Pralle - Created
//====================================================================

import java.io.*;

public class BlackjackTextUI
{
  ////////////////////////////////////////////////////////////////////
  //  main( args : String[] )
  //    Drives the game.
  ////////////////////////////////////////////////////////////////////
  static public void main( String[] args )
  {
    BlackjackGame game = new BlackjackGame();

    System.out.println( "\n----New Game----" );

game_loop:
    while (true)
    {
      // Print out the hands of the Dealer and Player
      System.out.println();
      showHand( "Dealer:", game.getDealerHand() );
      showHand( "You:   ", game.getPlayerHand() );

      // If the game is over, print out the reason, pause for 
      // 2 seconds, and start a new game.
      if (game.isGameOver())
      {
        System.out.println();
        System.out.println( "*** " + game.getStatusText() + " ***" );

        // pause 2 seconds
        try
        {
          Thread.sleep( 2000 );
        }
        catch (InterruptedException e) { }

        game.newGame();
        System.out.println( "\n----New Game----" );
        continue;
      }

      // read choice
      char choice = 0;
      while (choice != 'h' && choice != 's' && choice != 'q')
      {
        choice = readChar("(H)it, (S)tay, or (Q)uit? ");

        // make lower-case
        if (choice >= 'A' && choice <= 'Z') choice += ('a' - 'A');
      }

      switch ( choice )
      {
        case 'h': game.hit(); break;
        case 's': game.stay(); break;
        case 'q': break game_loop;
      }
    }
  }


  ////////////////////////////////////////////////////////////////////
  //  showHand( name : String, set : CardSet )
  //    Displays the specified cards alongside the given name.
  ////////////////////////////////////////////////////////////////////
  static public void showHand( String name, CardSet set )
  {
    System.out.println( name + " " + cardSetToString(set) );
  }


  ////////////////////////////////////////////////////////////////////
  //  cardSetToString( set : CardSet ) : String
  //    Converts a CardSet into a string representation.
  ////////////////////////////////////////////////////////////////////
  static public String cardSetToString( CardSet set )
  {
    String result = "";
    for (int i=0; i<set.size(); ++i)
    {
      if (i != 0) result += " ";  // spaces between
      result += cardToString( set.getCard(i) );
    }
    return result;
  }


  ////////////////////////////////////////////////////////////////////
  //  cardToString( card : Card ) : String
  //    Converts a specific card object into a string denoting its
  //    rank.  A question mark is returned if the card is face-down.
  ////////////////////////////////////////////////////////////////////
  static public String cardToString( Card card )
  {
    if ( !card.isFaceUp() ) return "?";

    switch (card.getRank())
    {
      case Card.ACE:    return "A";
      case Card.JACK:   return "J";
      case Card.QUEEN:  return "Q";
      case Card.KING:   return "K";
      default:          return "" + card.getRank();
    }
  }


  ////////////////////////////////////////////////////////////////////
  //  readChar( prompt : String ) : char
  //    Reads a line of text from the keyboard and returns ether the 
  //    first character or 0 if no text was entered.
  ////////////////////////////////////////////////////////////////////
  static public char readChar( String prompt )
  {
    System.out.print( prompt );
    try
    {
      BufferedReader in = new BufferedReader(
          new InputStreamReader(System.in) );

      String st = in.readLine();

      if (st==null || st.length()==0) return (char) 0;
      return st.charAt(0);
    }
    catch (Exception e)
    {
      return (char) 0;
    }
  }
}

