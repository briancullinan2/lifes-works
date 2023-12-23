//====================================================================
//  BlackjackApplet.java
//
//  Applet-based GUI for a Blackjack game.
//
//  History:
//    2005.11.23-24 / Abe Pralle - Created
//====================================================================

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;

public class BlackjackApplet extends JApplet
{
  //----INSTANCE VARIABLES--------------------------------------------
  protected CardCanvas canvas;
  protected BlackjackGame game;

  // TODO: controls...

  ////////////////////////////////////////////////////////////////////
  //  init()
  ////////////////////////////////////////////////////////////////////
  public void init()
  {
    // create the game object
    game = new BlackjackGame();

    canvas = new CardCanvas();

    // TODO: create & arrange controls

    getContentPane().add( canvas );
  }


  //==================================================================
  //  INNER CLASS
  //  CardCanvas
  //
  //  Extends JComponent to provide a drawing area for card graphics
  //  Loads and draws card graphics (obtained from 
  //  http://www.jfitz.com/cards/ )
  //==================================================================
  protected class CardCanvas extends JComponent
  {
    //----INSTANCE VARIABLES------------------------------------------
    // TODO: add vars for extra card images
    protected Image card_back;


    //////////////////////////////////////////////////////////////////
    //  CONSTRUCTOR
    //  CardCanvas()
    //
    //  Initiates image loading.
    //////////////////////////////////////////////////////////////////
    public CardCanvas()
    {
      URL url = getDocumentBase();

      card_back = getImage( url, "cards/b1.png" );

      // TODO: load extra cards.  The card images are organized in
      // the following pattern:
      //   1.png - Ace of Clubs 
      //   2.png - Ace of Spades 
      //   3.png - Ace of Hearts 
      //   4.png - Ace of Diamonds 
      //   5.png..8.png  - King of Clubs..Diamonds
      //   9.png..12.png - Queen of Clubs..Diamonds
      //   ...
      //   49.png..52.png - Two of Clubs..Diamonds
    }


    //////////////////////////////////////////////////////////////////
    //  paintComponent( Graphics g )
    //    Redraws this display area.
    //////////////////////////////////////////////////////////////////
    public void paintComponent( Graphics g )
    {
      // clear the drawing area
      g.setColor( new Color(64,64,64) );
      g.fillRect( 0, 0, getWidth(), getHeight() );

      // TODO: replace everything below with code that draws
      // the Dealer's and Player's cards to the screen.
      // keep this around until you're sure you're drawing all
      // cards correctly

      // draw text to the screen
      g.setColor( Color.WHITE );
      g.drawString( "All Cards", 100, 30 );

      // draw a full deck of cards to the screen
      CardSet deck = new CardSet();
      deck.newDeck();
      drawHand( g, deck, 10, 50 );
    }


    //////////////////////////////////////////////////////////////////
    //  drawHand( g : Graphics, hand : CardSet, x, y : int )
    //    Draws all of the cards in a CardSet next to each other
    //    with the first card at (x,y).
    //////////////////////////////////////////////////////////////////
    public void drawHand( Graphics g, CardSet hand, int x, int y )
    {
      for (int i=0; i<hand.size(); ++i)
      {
        Card card = hand.getCard(i);

        // TODO: space out the cards more
        drawCard( g, card, x + i*12, y );
      }
    }


    //////////////////////////////////////////////////////////////////
    //  drawCard( g : Graphics, card : Card, x, y : int )
    //    Draws a given card to the screen at (x,y).  If the card is 
    //    not face-up, a card back should be drawn.
    //////////////////////////////////////////////////////////////////
    public void drawCard( Graphics g, Card card, int x, int y )
    {
      Image img;

      // TODO: set img to appropriate card image instead of just
      // the card back.
      img = card_back;

      // If the image isn't completely loaded by the time we
      // attempt to draw it, request that we be repainted again
      // soon.
      if ( !g.drawImage(img,x,y,this) ) repaint();
    }
  }
}

