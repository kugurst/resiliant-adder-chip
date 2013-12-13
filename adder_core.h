#ifndef __ADDER_CORE_H__
#define __ADDER_CORE_H__

#include <iostream>

extern double initLambda;

#define ACTIVE 1
#define RESTING 2

using namespace std;

class adder_core {
	private:
		// Fields
		bool resting = true;
		bool failed = false;
		double baseLambda;
		double currentLambda;
		double relaxLambda;
		double elapsedTimeInCurrentState = 0;
		int lastState = RESTING;
		int nextState = -1;

	public:
	    adder_core();
		adder_core(double baseLambda);
		void update(double t, int p);
		void rest();
		void activate();
		bool isActive();
		bool hasNotFailed();
		bool justFailed(double t);
		bool shouldDeactivate();
		friend ostream& operator<<(ostream& os, const adder_core& core);
};

#endif

