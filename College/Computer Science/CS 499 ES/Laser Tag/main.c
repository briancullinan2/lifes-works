//******************************************************************************
//  MSP430F20x3 Demo - SD16A, Sample A1+ Continuously, Set P1.0 if > 0.3V
//
//  Description: A continuous single-ended sample is made on A1+ using internal
//  VRef Unipolar output format used.
//  Inside of SD16 ISR, if A1 > 1/2VRef (0.3V), P1.0 set, else reset.
//  ACLK = n/a, MCLK = SMCLK = SD16CLK = default DCO
//
//                MSP430F20x3
//             ------------------
//         /|\|              XIN|-
//          | |                 |
//          --|RST          XOUT|-
//            |                 |
//    Vin+ -->|A1+ P1.2         |
//            |A1- = VSS    P1.0|-->LED
//            |                 |
//
//  M. Buccini / L. Westlund
//  Texas Instruments Inc.
//  October 2005
//  Built with CCE Version: 3.2.0 and IAR Embedded Workbench Version: 3.40A
//******************************************************************************
#include  <in430.h>
#include  <io430x20x3.h>
#include  <intrinsics.h>

int toggle = 0;
int switcher = 0;
int off_counter = 0;
int on_counter = 0;

const unsigned int START = 43520;

int score[10] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

unsigned int data = 0;

unsigned int data_to_send = 0;

void send_recieve()
{
  int last = data;
  
  if(toggle == 1 && on_counter > 0)
  {
    data = data << 1;
    data += 1;
    on_counter = 0;
  }
  
  if(toggle == 0 && off_counter > 4)
  {
    data = data << 1;
    off_counter = 0;
  }
  
  // only check if there is new data
  if(last != data)
  {
    unsigned int start_code = data;
    start_code &= 65280; // mask off 0b1111000000000000
    if(start_code == START)
    {
      // we have a valid start point
      unsigned int input = data;
      input &= 255; // get number after start code
      if(input < 10)
        score[input] += 1;
    }
  }
  
  // send next bit of data
  if(data_to_send & 1 == 1)
  {
    P1OUT |= 0x01;
  }
  else
  {
    P1OUT &= ~0x01;
  }
  
  data_to_send = data_to_send >> 1;
}

void main(void)
{
  WDTCTL = WDTPW + WDTHOLD;                 // Stop watchdog timer
  P1OUT = 0;
  P1DIR = 1;                            // Set P1.0 to output direction
  P1REN = 2;
  P1SEL = 0;
  //P1IES = 2;  // hi->low
  //P1IE = 2;
  //P1IFG = 0;
  SD16CTL = SD16REFON + SD16SSEL_1;         // 1.2V ref, SMCLK
  SD16INCTL0 = SD16INCH_1 | SD16GAIN_1;                  // A1+/-
  SD16CCTL0 =  SD16UNI + SD16IE;            // 256OSR, unipolar, interrupt enable
  SD16AE = SD16AE2;                         // P1.1 A1+, A1- = VSS
  SD16CCTL0 |= SD16SC;                      // Set bit to start conversion
  TACTL = MC_1 | ID_3 | TASSEL_2 | TACLR;
  TACCR0 = 4000;
  P1OUT &= ~0x01;
 
  //while(P1IFG != 0) P1IFG = 0;
  
  for (;;)
  {
    // check for light
    int x = SD16MEM0;
    if (x == 0)           	    // SD16MEM0 > 0.3V?, clears IFG
    {
      if(toggle != 1)
      {
        switcher = 1;
        on_counter = 0;
      }
      toggle = 1;
      on_counter += 1;
    }
    else
    {
      if(toggle != 0)
      {
        switcher = 1;
        off_counter = 0;
      }
      toggle = 0;
      off_counter += 1;
    }
    
    // check for button pushes
    if(P1IN & 2 > 0)
    {
      if(data_to_send == 0)
      {
        data_to_send = 1 | START;
      }
    }
    
    // send recieve
    if(TACTL_bit.TAIFG != 0)
    {
      TACTL_bit.TAIFG = 0;
      send_recieve();
    }
    
    //_BIS_SR(LPM0_bits + GIE);
  }
}

/*
#pragma vector = PORT1_VECTOR
__interrupt void PORT1_ISR(void) {
  while(P1IFG != 0) P1IFG = 0;
}
*/