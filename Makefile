CC = clang++
CXX = clang++

LDLIBS = -lpthread
CXXFLAGS = -std=c++11 -g -Wall
LDFLAGS = -g

mttf_adder: adder.o adder_core.o

mttf_adder.o: adder.h adder_core.h

adder.o: adder.h adder_core.h

adder_core.o: adder.h adder_core.h

.PHONY: clean all
clean:
	rm -rf mttf_adder *~ *.o core a.out *.gch

all: clean mttf_adder
