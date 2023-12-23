#
# Makefile for Rijndael (AES) code.
#
# $Id: Makefile,v 1.2 2003/04/15 00:26:33 elm Exp elm $
# NOTE: has GNU make specific stuff

SRCS = encrypt.c decrypt.c rijndael.c
INCS = rijndael.h
PROGS = decrypt encrypt
OTHERS = testfile Makefile
DIR = $(notdir $(PWD))
TAR = tar
TARFILE = aes.tgz
CC = gcc

COBJS = rijndael.o
CFLAGS = -O3

all: $(PROGS)

encrypt: encrypt.o $(COBJS) $(INCS)
	$(CC) $(CFLAGS) -o $@ encrypt.o $(COBJS)

decrypt: decrypt.o $(COBJS) $(INCS)
	$(CC) $(CFLAGS) -o $@ decrypt.o $(COBJS)

# NOTE: bruteforce is not distributed with the problem set, but you're
#       probably going to want to write it....
bruteforce: bruteforce.o  $(OBJS) $(INCS)
	$(CC) $(CFLAGS) -o $@ bruteforce.o $(OBJS)

rijndael.o: rijndael.h

tarball:
	cd .. ; $(TAR) cvf - $(addprefix $(DIR)/, $(SRCS) $(INCS) $(OTHERS)) | gzip - > $(TARFILE)

test: all
	@echo "NOTE: a difference in the last line is probably OK---It's"
	@echo "      due to padding the last block encrypted."
	@echo
	./encrypt abcd - < testfile | ./decrypt abcd - | diff - testfile
	./encrypt 123456 - < testfile | ./decrypt 123456 - | diff - testfile
	./encrypt 98765432 - < testfile | ./decrypt 98765432 - | diff - testfile

clean:
	$(RM) *~ *.o

spotless: clean
	$(RM) $(PROGS)
