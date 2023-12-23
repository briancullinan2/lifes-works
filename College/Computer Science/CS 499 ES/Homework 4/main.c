#include "io430x20x3.h"
#include "intrinsics.h"

#define E BIT0
#define CP BIT1
#define RS BIT2
#define DATA BIT2
#define KEYS 120

int first = 0;
int number = 0;
int last = 0;
int op = 0;
int reprint = 0;

int col = 0;
int row = 0;
const char input[4][4] = {
  {'1', '2', '3', '+'}, 
  {'4', '5', '6', '-'},
  {'7', '8', '9', '*'},
  {'*', '0', '=', '/'}
};

void wait(unsigned int length)
{
  for(int i = 0; i < length; i++)
  {
    while(TACTL_bit.TAIFG == 0) {}
    TACTL_bit.TAIFG = 0;
  }
}

void sendNibble(unsigned int nibble, int is_char)
{
  for(int i = 0; i < 4; i++)
  {
    P1OUT = (((nibble % 2)>0)?DATA:0)|E;
    if(i == 3 && is_char == 1)
      P1OUT = CP|E|RS;
    else
      P1OUT = CP|E;
    nibble = nibble / 2;
  }
}

void send(unsigned int data)
{
  P1OUT = E;
  sendNibble(data >> 4, 0);
  P1OUT ^= E;
  wait(1);
  P1OUT = E;
  sendNibble(data & 0x0F, 0);
  P1OUT ^= E;
  wait(1);
}

void send_char(int data)
{
  P1OUT = E;
  sendNibble(data >> 4, 1);
  P1OUT ^= E;
  wait(1);
  P1OUT = E;
  sendNibble(data & 0x0F, 1);
  P1OUT ^= E;
  wait(1);
}

void waitForInput()
{
  P1OUT = DATA;
  P1OUT = DATA|CP;
  P1OUT = DATA;
  P1OUT = DATA|CP;
  P1OUT = DATA;
  P1OUT = DATA|CP;
  P1OUT = DATA;
  P1OUT = DATA|CP;
}

void initlcd()
{
  wait(10);
  send(0x02); // set to 4 bit mode
  wait(10);
  send(0x28); // set to 4 bit mode
  wait(10);
  send(0x0F); // display on command
  wait(10);
  send(0x06); // move the cursor from left to right
  wait(10);
  send(0x01); // clear display
  wait(10);
}

void set_row(int row)
{
  for(int i = 0; i < 4; i++)
  {
    if(i == row)
    {
      P1OUT = DATA;
      P1OUT = DATA|CP;
    }
    else
    {
      P1OUT = 0;
      P1OUT = 0|CP;
    }
  }
}

#pragma vector = PORT1_VECTOR
__interrupt void PORT1_ISR(void) {
  P1IE = 0;
  col = 0;
  row = 0;
  int tmp_col = 0;
  if(P1IFG & 0x08) {tmp_col = 0x08; col = 0;}
  else if(P1IFG & 0x10) {tmp_col = 0x10; col = 1;}
  else if(P1IFG & 0x20) {tmp_col = 0x20; col = 2;}
  else if(P1IFG & 0x40) {tmp_col = 0x40; col = 3;}
  set_row(3);
  if(P1IN & tmp_col) row = 0;
  else
  {
    set_row(2);
    if(P1IN & tmp_col) row = 1;
    else
    {
      set_row(1);
      if(P1IN & tmp_col) row = 2;
      else
      {
        set_row(0);
        if(P1IN & tmp_col) row = 3;
      }
    }
  }
  P1IFG = 0;
    
  // handle numbers
  if((row < 3 && col < 3) || (row == 3 && col == 1))
  {
    number = number * 10 + (input[row][col] - 48);
  }
  else
  {
    if(col == 3)
    {
      // handle operators
      first = number;
      op = input[row][col];
      number = 0;
    }
    else
    {
      if(row = 3 && col == 2)
      {
        last = number;
        if(op == '+') number = first + last;
        else if(op == '-') number = first - last;
        else if(op == '*') number = first * last;
        else if(op == '/') number = first / last;
      }
    }
  }
  
  wait(200);
  reprint = 1;
  __low_power_mode_off_on_exit();
  while(P1IFG != 0) P1IFG = 0;
  P1IE = KEYS;
}

int main( void )
{
  // Stop watchdog timer to prevent time out reset
  WDTCTL = WDTPW + WDTHOLD;
  TACTL = MC_1 | ID_2 | TASSEL_2 | TACLR;
  TACCR0 = 274;
  P1DIR = RS|E|DATA|CP;
  P1REN = KEYS;
  P1IES = 0x00;  // hi->low
  P1IFG = 0;
  P1OUT = 0;
  
  initlcd();
  
  char* string_1 = "Brian Cullinan";
  char* string_2 = "Was Here";
  
  while(*string_1 != 0)
  {
    wait(10);
    send_char(*string_1); // 
    string_1++;
  }
  wait(10);
  send(0xC0); // begin waiting of first char of second line
  while(*string_2 != 0)
  {
    wait(10);
    send_char(*string_2); // 
    string_2++;
  }

  waitForInput();
  P1IE = KEYS;
  
  while(P1IFG != 0) P1IFG = 0;
  while(1)
  {
    if(reprint == 1)
    {
      wait(10);
      send(0x01); // clear display
      wait(10);
      // output numbers
      wait(10);
      if(row = 3 && col == 2)
      {
        int tmp_number = 10000;
        while(tmp_number != 0)
        {
          if(first >= tmp_number)
          {
            send_char((first / tmp_number % 10)+48);
            wait(10);
          }
          tmp_number = tmp_number / 10;
        }
        wait(10);
        send_char(op);
        wait(10);
        tmp_number = 10000;
        while(tmp_number != 0)
        {
          if(last >= tmp_number)
          {
            send_char((last / tmp_number % 10)+48);
            wait(10);
          }
          tmp_number = tmp_number / 10;
        }
        wait(10);
        send_char('=');
        wait(10);
        tmp_number = 10000;
        while(tmp_number != 0)
        {
          if(number >= tmp_number)
          {
            send_char((number / tmp_number % 10)+48);
            wait(10);
          }
          tmp_number = tmp_number / 10;
        }
        first = 0;
        last = 0;
        op = 0;
      }
      else
      {
        if(op == 0)
        {
          int tmp_number = 10000;
          while(tmp_number != 0)
          {
            if(number >= tmp_number)
            {
              send_char((number / tmp_number % 10)+48);
              wait(10);
            }
            tmp_number = tmp_number / 10;
          }
        }
        else
        {
          int tmp_number = 10000;
          while(tmp_number != 0)
          {
            if(first >= tmp_number)
            {
              send_char((first / tmp_number % 10)+48);
              wait(10);
            }
            tmp_number = tmp_number / 10;
          }
          wait(10);
          send_char(op);
          wait(10);
          tmp_number = 10000;
          while(tmp_number != 0)
          {
            if(number >= tmp_number)
            {
              send_char((number / tmp_number % 10)+48);
              wait(10);
            }
            tmp_number = tmp_number / 10;
          }
        }
      }
      reprint = 0;
      waitForInput();
    }
    else
    {
      __low_power_mode_4();
    }
  }
}


