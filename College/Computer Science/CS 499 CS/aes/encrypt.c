/*
 * This code encrypts input data using the Rijndael (AES) cipher.  The
 * key length is hard-coded to 128 key bits; this number may be changed
 * by redefining a constant near the start of the file.
 *
 * Author: Ethan L. Miller (elm@cs.ucsc.edu)
 * Based on code from Philip J. Erdelsky (pje@acm.org)
 *
 */

#include <stdio.h>
#include "rijndael.h"

static char rcsid[] = "$Id: encrypt.c,v 1.2 2003/04/15 01:05:36 elm Exp elm $";

#define KEYBITS 128
#define	CBC_MODE	1

/***********************************************************************
 *
 * hexvalue
 *
 * This routine takes a single character as input, and returns the
 * hexadecimal equivalent.  If the character passed isn't a hex value,
 * the program exits.
 *
 ***********************************************************************
 */
int hexvalue (char c)
{
  if (c >= '0' && c <= '9') {
    return (c - '0');
  } else if (c >= 'a' && c <= 'f') {
    return (10 + c - 'a');
  } else if (c >= 'A' && c <= 'F') {
    return (10 + c - 'A');
  } else {
    fprintf (stderr, "ERROR: key digit %c isn't a hex digit!\n", c);
    exit (-1);
  }
}


/***********************************************************************
 *
 * getpassword
 *
 * Get the key from the password.  The key is specified as a string of
 * hex digits, two per key byte.  password points at the character
 * currently being added to the key.  If it's '\0', the key is done.
 *
 ***********************************************************************
 */
void
getpassword (const char *password, unsigned char *key, int keylen)
{
  int		i;

  for (i = 0; i < keylen; i++) {
    if (*password == '\0') {
      key[i] = 0;
    } else {
      /* Add the first of two digits to the current key value */
      key[i] = hexvalue (*(password++)) << 4;
      /* If there's a second digit at this position, add it */
      if (*password != '\0') {
	key[i] |= hexvalue (*(password++));
      }
    }
  }
}

int main(int argc, char **argv)
{
  unsigned long rk[RKLENGTH(KEYBITS)];		/* round key */
  unsigned char key[KEYLENGTH(KEYBITS)];	/* cipher key */
  char	buf[100];
  int i, j, v;
  int nrounds;					/* # of Rijndael rounds */
  char *password;				/* supplied (ASCII) password */
  FILE *output;					/* -> output stream */
  unsigned char plaintext[16];
  unsigned char ciphertext[16];

  if (argc < 3)
  {
    fprintf (stderr, "Usage: %s <key> <output file>\n", argv[0]);
    return 1;
  }
  /*
   * Get the key from the password.  The key is specified as a string of
   * hex digits, two per key byte.  password points at the character
   * currently being added to the key.  If it's '\0', the key is done.
   */
  getpassword (argv[1], key, sizeof (key));
  /* Print the key, just in case */
  for (i = 0; i < sizeof (key); i++) {
    sprintf (buf+2*i, "%02x", key[i]);
  }
  fprintf (stderr, "KEY: %s\n", buf);
  /*
   * Open the output file.  If the file name is '-', use standard out.
   */
  if (*argv[2] == '-') {
    output = stdout;
  } else {
    output = fopen(argv[2], "wb");
    if (output == NULL)
    {
      fputs("File write error", stderr);
      return 1;
    }
  }
  /*
   * Initialize the Rijndael algorithm.  The round key is initialized by this
   * call from the values passed in key and KEYBITS.
   */
  nrounds = rijndaelSetupEncrypt(rk, key, KEYBITS);
#if	CBC_MODE
  /* Init the ciphertext to 0 (IV = 0) for CBC mode */
  for (i = 0; i < sizeof (ciphertext); i++) {
    ciphertext[i] = 0;
  }
#endif	/* CBC_MODE */
  while (!feof(stdin))
  {
    /*
     * Read 16 bytes (128 bits, the blocksize) from standard input
     */
    v = fread (plaintext, 1, sizeof (plaintext), stdin);
#if	CBC_MODE
    /* CBC mode: XOR plaintext with previous ciphertext */
    for (i = 0; i < sizeof (ciphertext); i++) {
      plaintext[i] ^= ciphertext[i];
    }
#endif	/* CBC_MODE */
    if (v == 0) {
      /* No data read -> end of file */
      break;
    } else if (v < 0) {
      /* Error code - fail ungracefully! */
      fprintf (stderr, "Read error!\n");
      return 1;
    } else {
      /* Pad the block out with spaces */
      for (j = v; j < sizeof (plaintext); j++) {
	plaintext[j] = ' ';
      }
    }
    /* Call the encryption routine */
    rijndaelEncrypt(rk, nrounds, plaintext, ciphertext);
    /* Write the result to the output file */
    if (fwrite(ciphertext, sizeof(ciphertext), 1, output) != 1)
    {
      fclose(output);
      fputs("File write error", stderr);
      return 1;
    }
  }
  fclose(output);
}
