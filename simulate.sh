#!/bin/bash

spareArr=( 0 1 2 4 8 16 )
kArr=( 64 128 256 512 1024 )
activeArr=( 4 16 64 256 )

echo "------------------------------------------------------------------------" >> results.txt

for k in "${kArr[@]}"; do
    for active in "${activeArr[@]}"; do
        for spare in "${spareArr[@]}"; do
            java MTTFAdder "$((spare * active))" "$active" "$k" >> results.txt
        done
    done
done
