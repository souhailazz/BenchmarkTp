#!/bin/bash

# Run a single JMeter test scenario
# Usage: ./run-single-test.sh <variant> <scenario>
# Example: ./run-single-test.sh spring scenario-1-read-heavy

if [ $# -ne 2 ]; then
    echo "Usage: $0 <variant> <scenario>"
    echo "Variants: jersey, spring, datarest"
    echo "Scenarios: scenario-1-read-heavy, scenario-2-join-filter, scenario-3-mixed, scenario-4-heavy-body"
    exit 1
fi

VARIANT=$1
SCENARIO=$2

case $VARIANT in
    jersey)
        API_PATH="/api/jersey"
        ;;
    spring)
        API_PATH="/api/spring"
        ;;
    datarest)
        API_PATH="/api/datarest"
        ;;
    *)
        echo "Invalid variant: $VARIANT"
        exit 1
        ;;
esac

echo "Running $SCENARIO for variant $VARIANT"
echo "API Path: $API_PATH"

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
RESULTS_DIR="jmeter/results"
RESULT_FILE="$RESULTS_DIR/${SCENARIO}-${VARIANT}-${TIMESTAMP}.jtl"
REPORT_DIR="$RESULTS_DIR/${SCENARIO}-${VARIANT}-${TIMESTAMP}-report"

mkdir -p $RESULTS_DIR

jmeter -n \
    -t "jmeter/${SCENARIO}.jmx" \
    -l "$RESULT_FILE" \
    -e -o "$REPORT_DIR" \
    -JAPI_PATH=$API_PATH \
    -JVARIANT=$VARIANT

echo ""
echo "Test completed!"
echo "Results: $RESULT_FILE"
echo "Report: $REPORT_DIR/index.html"

