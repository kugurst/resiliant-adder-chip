#ifndef __ADDER_H__
#define __ADDER_H__

#include "adder_core.h"

extern double initLambda;
extern double global_timeStep;

class adder {
    private:
        adder_core *cores;
        double timeStep;
        bool failed = false;
        int minimumCores;
        double elapsedTime = 0;

    public:
        adder(int active, int spares, double baseLambda, double lambdaSeconds,
            int Q, double Qseconds);
        ~adder();
        bool hasFailed(int P);
        double getTimeOfDeath();
};

double pickStep(int clockPeriod, double secondsPerPeriod, double meanLambda,
    double secondsPerLambda);

#endif

