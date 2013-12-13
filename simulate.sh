#!/bin/bash

spareArr=( 1 2 4 8 )
kArr=( 16 64 256 )

rm results-$1.txt

for spare in "${spareArr[@]}"; do
    for k in "${kArr[@]}"; do
        java MTTFAdder "$((spare * active))" "$1" "$k" >> results-$1.txt
    done
done
