
#include "16F690.h"
#include "int16Cxx.h"
#pragma config |= 0x00D4
#define MAX_STRING 11

void initserial( void );
void FlushRecieverBuffer(void);
void putchar( char );
char getchar( void );
void string_in( char * ); 
void printf(const char *string, uns8 variable); 
bit check_password( char * input_string, const char * candidate_string );
char check_all_passwords( char * input_string );
char input_string[MAX_STRING];

#pragma origin 4
interrupt int_server( void ) /* the place for the interrupt routine */
{
	
	int_save_registers
	if( RCIF == 1 )  /* test if it is the receive-interrupt         */
    {              /* this time it's obvius that it is!        */
		
		char trash;
		char sync_b;
		char addr_b;
		char data_b;
		GIE=0;
		while ( RCIF == 0 );
			sync_b = RCREG;
		if(sync_b == 'a'){
			while ( RCIF == 0 );
			addr_b = RCREG;
			while ( RCIF == 0 );
			data_b = RCREG;
			
			if(addr_b == 'b'){
			
				if(data_b == 'l'){ //turn left
					PORTA.2 = 1;
					char i=20;

					OPTION = 5; //128 presc
					while(i!=0){
						TMR0 = 0;
						while(TMR0<156){ //pulse width = 20 ms
							if(TMR0 <=60){
								PORTC.7 = 1;
							}
							else{
								PORTC.7 = 0;
							}
							
						}
						--i;
					}
				}
				else if(data_b == 'r'){ //turn left
					PORTA.2 = 0;
					char i=20;

					OPTION = 5; //128 presc
					while(i!=0){
						TMR0 = 0;
						while(TMR0<156){ //pulse width = 20 ms
							if(TMR0 <=125){
								PORTC.7 = 1;
							}
							else{
								PORTC.7 = 0;
							}
							
						}
						--i;
					}
				}
			}	
		}
		trash = RCREG;
	    trash = RCREG;
		CREN = 0;       /* the unlock procedure ...                 */
		CREN = 1;
		GIE=1;
    }
	
  int_restore_registers
}






void main( void)
{
   char c, num=0;
   CM1CON0 = 0;                    // disable comparator 1 (RB0, RB1, RB2 usable)
   CM2CON0 = 0;                    // disable comparator 2 (RC0, RC1, RC4 usable)
   VRCON = 0;                      // disable CVref (RC2 usable)
   ANSEL = 0;
   TRISA.0 = 1; /* not to disturb UART-Tool          */
   TRISA.1 = 1; /* not to disturb UART-Tool          */
   TRISA.2 = 0;
   TRISC.7 = 0;
   PORTA.2 = 1;
   PORTC.7 = 1;
   initserial();
	
   /* 2,5 s to turn on VDD and Connect UART-Tool     */

   while(1)
	{

		
		
		
	
	}
}
void turnservo(char time){
	
}



/* *********************************** */
/*             FUNCTIONS               */
/* *********************************** */




void initserial( void )  /* initialise PIC16F690 serialcom port */
{
   /* One start bit, one stop bit, 8 data bit, no parity. 9600 Baud. */
   RCIE = 1;
   PEIE = 1;
   GIE = 1;
   
   
   TXEN = 1;      /* transmit enable                   */
   SYNC = 0;      /* asynchronous operation            */
   TX9  = 0;      /* 8 bit transmission                */
   SPEN = 1;

   BRGH  = 0;     /* settings for 6800 Baud            */
   BRG16 = 1;     /* @ 4 MHz-clock frequency           */
   SPBRG = 25;

   CREN = 1;      /* Continuous receive                */
   RX9  = 0;      /* 8 bit reception                   */
   ANSELH.3 = 0;  /* RB5 not AD-input but serial_in    */
}

void FlushRecieverBuffer(void)
{  
   char trash;
   trash = RCREG;  /* the two char's that locked the reciever  */
   trash = RCREG;  /* are read and ignored                     */
   CREN = 0;       /* the unlock procedure ...                 */
   CREN = 1;
}


char getchar( void )  /* recieves one char */
{
   char d_in;
   while ( RCIF == 0 ) ;  /* wait for char */
   d_in = RCREG;
   return d_in;
}

void putchar( char d_out )  /* sends one char */
{
   while (!TXIF) ;   /* wait until previus character transmitted */
   TXREG = d_out;
}

bit check_password( char * input_string, const char * candidate_string )
{
   /* compares input buffer with the candidate string */
   char i, c, d;
   for(i=0; ; i++)
	 {
	   c = input_string[i];
	   d = candidate_string[i];
	   if(d != c ) return 0;       /* no match    */
		 if( d == '\0' ) return 1; /* exact match */
	 }
}

char check_all_passwords( char * input_string )
{
   if( check_password( input_string, "left\r\n" ) )      return 1;
   else if( check_password( input_string, "right\r\n" ) )  return 2;
   else if( check_password( input_string, "nisse" ) ) return 3;
   else return 0;
}





void string_in( char * string ) 
{
   char charCount, c;
   for( charCount = 0; ; charCount++ )
	   {
		 c = getchar( );     /* input 1 character             */
		 string[charCount] = c;   /* store the character           */
		 if( (charCount == (MAX_STRING-1))||(c=='\r' )) /* end of input   */
		   {
			 string[charCount] = '\0'; /* add "end of string"      */
			 return;
		   }
	   }
}

void printf(const char * string, uns8 variable)
{
  char i, k, m, a, b;
  for(i = 0 ; ; i++)
   {
	 k = string[i];
	 if( k == '\0') break;   // at end of string
	 if( k == '%')           // insert variable in string
	  { 
		i++;
		k = string[i];
		switch(k)
		 {
		   case 'd':         // %d  signed 8bit
			 if( variable.7 ==1) putchar('-');
			 else putchar(' ');
			 if( variable > 128) variable = -variable;  // no break!
		   case 'u':         // %u unsigned 8bit
			 a = variable/100;
			 putchar('0'+a); // print 100's
			 b = variable%100; 
			 a = b/10;
			 putchar('0'+a); // print 10's
			 a = b%10;         
			 putchar('0'+a); // print 1's 
			 break;
		   case 'b':         // %b BINARY 8bit
			 for( m = 0 ; m < 8 ; m++ )
			  {
				if (variable.7 == 1) putchar('1');
				else putchar('0');
				variable = rl(variable);
			   }
			  break;
		   case 'c':         // %c  'char'
			 putchar(variable); 
			 break;
		   case '%':
			 putchar('%');
			 break;
		   default:          // not implemented 
			 putchar('!');   
		 }   
	  }
	  else putchar(k); 
   }
}
