#include "adder.h"
#include "adder_core.h"

double initLambda;
int numCores;
double global_timeStep;

adder::adder(int active, int spares, double baseLambda, double lambdaSeconds,
    int Q, double Qseconds)
{
    initLambda = baseLambda / lambdaSeconds;
    numCores = active + spares;
    cores = new adder_core[numCores];
    int activated = 0;
    for (int i = 0; i < numCores; i++) {
        if (activated++ < active) {
            cores[i].activate();
            cores[i].update(0, 1);
        }
    }
    timeStep = pickStep(Q, Qseconds, 1.0 / baseLambda, lambdaSeconds);
    global_timeStep = timeStep;
    minimumCores = active;
}

bool adder::hasFailed(int P)
{
    if (failed)
        return failed;
    int remaining = 0;
    int activateCount = 0;
    bool wasActivate[numCores];
    for (int i = 0; i < numCores; i++) {
        wasActivate[i] = false;
        if (cores[i].hasNotFailed()) {
            if (!cores[i].justFailed(timeStep)) {
                cores[i].update(timeStep, P);
                remaining++;
                // We should activate if we need to
                if (activateCount > 0 && !cores[i].isActive()) {
                    cores[i].activate();
                    activateCount--;
                }
                // Deactivate this core and attempt to activate the next one
                else if (cores[i].isActive() && cores[i].shouldDeactivate()) {
                    wasActivate[i] = true;
                    cores[i].rest();
                    activateCount++;
                }
            } else
                activateCount++;
        }
    }
    // Do one more pass to activate until we have an active number of cores
    for (int i = numCores - 1; i >= 0 && activateCount > 0; i--) {
        if (cores[i].hasNotFailed() && !cores[i].isActive() && !wasActivate[i]) {
            cores[i].activate();
            activateCount--;
        }
    }
    // Do one last pass to activate until we have an active number of cores
    for (int i = 0; i < numCores && activateCount > 0; i++) {
        if (cores[i].hasNotFailed() && !cores[i].isActive()) {
            cores[i].activate();
            activateCount--;
        }
    }
    if (remaining < minimumCores)
        failed = true;
    elapsedTime += timeStep;
    return failed;
}

adder::~adder()
{
    delete[] cores;
}

double adder::getTimeOfDeath()
{
    return elapsedTime;
}

double pickStep(int clockPeriod, double secondsPerPeriod, double meanLambda,
    double secondsPerLambda)
{
    double mean = 0.1;
    return mean;
}

