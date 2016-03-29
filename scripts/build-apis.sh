#!/bin/bash
function die {
    cat ../scripts/most-recent-maven-build.log
    printf '%s\n' "Dying...See scripts/most-recent-maven-build.log for more info."
    exit 1
}

apis=(
    "common"
    "spring-utils"
    "security-api"
    "fitness-parent"
    "data-all-api"
    "data-es-api"
    "data-db-api"
)
printf '%s\n' "Building APIs (${apis[*]})"

> scripts/most-recent-maven-build.log || die
for api in ${apis[@]}; do
    cd ${api} || die
    printf '%s\n' "Installing ${api} API"
    mvn clean install > ../scripts/most-recent-maven-build.log || die
    cd ..
done

