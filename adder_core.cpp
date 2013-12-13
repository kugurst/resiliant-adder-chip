#include "adder_core.h"
#include "adder.h"
#include <cmath>
#include <cstdlib>

#define K 128

adder_core::adder_core(double bLambda)
{
	baseLambda = bLambda;
	currentLambda = bLambda;
	relaxLambda = bLambda;
}

adder_core::adder_core()
{
	baseLambda = initLambda;
	currentLambda = initLambda;
	relaxLambda = initLambda;
}

void adder_core::update(double t, int p)
{
	if (failed)
		return;
	elapsedTimeInCurrentState += t;
	if (lastState == RESTING)
		currentLambda = fmax(baseLambda, relaxLambda
			* exp(-K * elapsedTimeInCurrentState));
	else if (lastState == ACTIVE)
		currentLambda = baseLambda + p *
			sqrt(elapsedTimeInCurrentState);
	if (nextState != lastState) {
		elapsedTimeInCurrentState = 0;
		if (nextState == RESTING)
			relaxLambda = currentLambda;
		lastState = nextState;
	}
}

void adder_core::rest()
{
	if (failed)
		return;
	nextState = RESTING;
	resting = true;
}

void adder_core::activate()
{
	if (failed)
		return;
	nextState = ACTIVE;
	resting = false;
}

bool adder_core::isActive()
{
	if (failed)
		return false;
	return !resting;
}

bool adder_core::hasNotFailed()
{
	return !failed;
}

bool adder_core::justFailed(double t)
{
	if (failed)
		return true;
	if (resting)
		return false;
	double prob = 1.0 - exp(-currentLambda * t);
	if (((double)rand() / RAND_MAX) < prob)
		failed = true;
	return failed;
}

bool adder_core::shouldDeactivate()
{
	if (currentLambda > 1.5 * baseLambda)
		return true;
	return false;
}

ostream& operator<<(ostream& os, const adder_core& core)
{
    os << "currentLambda: " << core.currentLambda << ", failed: " << core.failed
        << ", current state: " << core.lastState << endl;
    return os;
}

