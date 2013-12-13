#include <cstdio>
#include <cstdlib>
#include <ctime>
#include <cmath>
#include <cstring>
#include <iostream>
#include <sstream>
#include <atomic>

#include <pthread.h>
#include <unistd.h>
#include <sys/time.h>
#include <sys/types.h>

#include "adder.h"
#include "adder_core.h"

typedef struct timeval timeval_t;

struct adder_args {
    int spares;
    int active;
    double lambda;
    int Q;
    double Qseconds;
    double lambdaSeconds;
    volatile double *timeSum;
    int P;
    int simul_count;
};

typedef struct adder_args adder_args_t;

using namespace std;

stringstream stats;
atomic_int worker_num(0);
atomic_int added(0);

void *mttf_worker(void *args_void)
{
    int worker = worker_num++;
    adder_args_t *args = (adder_args_t *)args_void;
    double timeArr[args->simul_count];
    for (int i = 0; i < args->simul_count; i++) {
        adder a(args->active, args->spares, args->lambda, args->lambdaSeconds,
            args->Q, args->Qseconds);
        if (added++ == 0)
            stats << '\t' << global_timeStep;
        while (!a.hasFailed(args->P));
        srand(time(NULL));
        timeArr[i] = a.getTimeOfDeath();
    }
    for (int i = 0; i < args->simul_count; i++)
        args->timeSum[worker] += timeArr[i];
    return NULL;
}

void mttf(int P)
{
    worker_num = 0;
    long cores = sysconf(_SC_NPROCESSORS_ONLN);
    long int simul_count = lround(80 / cores);
    volatile double timeSum[cores];
    for (int i = 0; i < cores; i++)
        timeSum[i] = 0;
    adder_args_t args;
    args.spares = 1;
    args.active = 1;
    args.lambda = 0.05;
    args.Q = 10;
    args.Qseconds = pow(10, -9);
    args.lambdaSeconds = 86400;
    args.P = P;
    args.simul_count = simul_count;
    args.timeSum = timeSum;
    if (P == 1)
        stats << args.lambda << '\t' << args.spares << '\t' << args.active;
    pthread_t threads[cores];
    for (int i = 0; i < cores; i++) {
        pthread_create(&threads[i], NULL, mttf_worker, (void *)&args);
    }
    for (int i = 0; i < cores; i++) {
        pthread_join(threads[i], NULL);
    }
    double total_sum = 0;
    for (int i = 0; i < cores; i++) {
        total_sum += timeSum[i];
    }
    double result = total_sum / (cores * simul_count);
    cout << "Average time: " << result << endl;
    stats << '\t' << result;
}

int main()
{
    timeval_t start, end;
    gettimeofday(&start, NULL);
    for (int i = 1; i <= 3; i++)
        mttf(i);
    cout << stats.str() << endl;
    gettimeofday(&end, NULL);
    printf("Time taken: %ld.%.6ld\n", (end.tv_sec - start.tv_sec),
        (long int)fdim(end.tv_usec, start.tv_usec));
    return 0;
}

