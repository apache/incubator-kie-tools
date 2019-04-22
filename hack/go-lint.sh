#!/bin/sh

dirs=(cmd pkg version)
for dir in "${dirs[@]}"
do
    if ! golint -set_exit_status ${dir}/...; then
        code=1
    fi
done
exit ${code:0}