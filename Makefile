CC = g++
CXX = g++

LDLIBS = -lpthread
CXXFLAGS = -std=c++11 -g -Wall -march=native
LDFLAGS = -g -march=native

mttf_adder: adder.o adder_core.o

mttf_adder.o: adder.h adder_core.h

adder.o: adder.h adder_core.h

adder_core.o: adder.h adder_core.h

.PHONY: clean all
clean:
	rm -rf mttf_adder *~ *.o core a.out *.gch *.class

all: clean mttf_adder
