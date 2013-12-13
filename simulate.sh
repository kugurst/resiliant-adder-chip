#!/bin/bash

spareArr=( 1 2 4 8 )
kArr=( 16 64 256 )
# activeArr=( 4 16 64 256 )

active=$1
rm results-$active.txt

for spare in "${spareArr[@]}"; do
    for k in "${kArr[@]}"; do
        java MTTFAdder "$((spare * active))" "$active" "$k" >> results-$active.txt
    done
done
