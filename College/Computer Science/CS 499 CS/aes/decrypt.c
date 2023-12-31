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

static char rcsid[] = "$Id: decrypt.c,v 1.2 2003/04/15 01:05:36 elm Exp elm $";

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

/***********************************************************************
 *
 * main
 *
 ***********************************************************************
 */
int main(int argc, char **argv)
{
  unsigned long rk[RKLENGTH(KEYBITS)];
  unsigned char key[KEYLENGTH(KEYBITS)];
  char	buf[100];
  int i, j;
  int nrounds;
  FILE *input;
  unsigned char plaintext[16];
  unsigned char ciphertext[16];
  unsigned char prevtext[16];

  if (argc < 3)
  {
    fprintf (stderr, "Usage: %s <key> <input file>\n", argv[0]);
    return 1;
  }
  getpassword (argv[1], key, sizeof (key));
  /* Print the key, just in case */
  for (i = 0; i < sizeof (key); i++) {
    sprintf (buf+2*i, "%02x", key[i]);
  }
  fprintf (stderr, "KEY: %s\n", buf);

  if (*argv[2] == '-') {
    input = stdin;
  } else {
    input = fopen(argv[2], "rb");
    if (input == NULL)
    {
      fputs("File read error", stderr);
      return 1;
    }
  }

  nrounds = rijndaelSetupDecrypt(rk, key, KEYBITS);
#if	CBC_MODE
  /* Init the ciphertext to 0 (IV = 0) for CBC mode */
  for (i = 0; i < sizeof (ciphertext); i++) {
    prevtext[i] = 0;
  }
#endif	/* CBC_MODE */
  while (1)
  {
    if (fread(ciphertext, sizeof(ciphertext), 1, input) != 1)
      break;
    rijndaelDecrypt(rk, nrounds, ciphertext, plaintext);
#if	CBC_MODE
    for (i = 0; i < sizeof (plaintext); i++) {
      plaintext[i] ^= prevtext[i];
      prevtext[i] = ciphertext[i];
    }
#endif	/* CBC_MODE */
    fwrite(plaintext, sizeof(plaintext), 1, stdout);
  }
  fclose(input);
}

