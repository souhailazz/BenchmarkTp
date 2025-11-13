#!/bin/bash

# REST API Benchmark - Run All Scenarios for All Variants
# This script runs all 4 scenarios for each of the 3 variants

set -e

echo "======================================="
echo "REST API Benchmark - Automated Runner"
echo "======================================="

# Configuration
VARIANTS=("jersey" "spring" "datarest")
API_PATHS=("/api/jersey" "/api/spring" "/api/datarest")
SCENARIOS=(
    "scenario-1-read-heavy"
    "scenario-2-join-filter"
    "scenario-3-mixed"
    "scenario-4-heavy-body"
)

RESULTS_DIR="jmeter/results"
JMETER_HOME=${JMETER_HOME:-"/opt/apache-jmeter"}
JMETER="jmeter"

# Check prerequisites
echo "Checking prerequisites..."

if ! command -v $JMETER &> /dev/null; then
    echo "ERROR: JMeter not found in PATH. Please install JMeter or set JMETER_HOME"
    exit 1
fi

# Note: Using local PostgreSQL installation
# Make sure PostgreSQL is running locally on port 5432
echo "Make sure PostgreSQL is running locally on port 5432"

# Create results directory
mkdir -p $RESULTS_DIR

echo ""
echo "Starting benchmark runs..."
echo "This will take approximately 3-4 hours"
echo ""

# Generate test data if not exists
if [ ! -f "jmeter/data/categories.csv" ]; then
    echo "Generating test data..."
    python3 jmeter/generate-test-data.py
fi

# Loop through variants
for i in "${!VARIANTS[@]}"; do
    VARIANT="${VARIANTS[$i]}"
    API_PATH="${API_PATHS[$i]}"
    
    echo ""
    echo "======================================="
    echo "Testing Variant: $VARIANT"
    echo "API Path: $API_PATH"
    echo "======================================="
    
    # Start application with specific profile
    echo "Starting application with profile: $VARIANT"
    java -jar \
        -Dspring.profiles.active=$VARIANT \
        -Xms1g -Xmx2g \
        -XX:+UseG1GC \
        target/rest-benchmark-0.0.1-SNAPSHOT.jar &
    
    APP_PID=$!
    echo "Application started with PID: $APP_PID"
    
    # Wait for application to start
    echo "Waiting for application to be ready..."
    sleep 30
    
    # Check if app is running
    if ! curl -s http://localhost:8080/actuator/health > /dev/null; then
        echo "ERROR: Application failed to start"
        kill $APP_PID 2>/dev/null || true
        continue
    fi
    
    echo "Application ready!"
    
    # Run each scenario
    for SCENARIO in "${SCENARIOS[@]}"; do
        echo ""
        echo "Running: $SCENARIO for $VARIANT..."
        
        TIMESTAMP=$(date +%Y%m%d_%H%M%S)
        RESULT_FILE="$RESULTS_DIR/${SCENARIO}-${VARIANT}-${TIMESTAMP}.jtl"
        REPORT_DIR="$RESULTS_DIR/${SCENARIO}-${VARIANT}-${TIMESTAMP}-report"
        
        $JMETER -n \
            -t "jmeter/${SCENARIO}.jmx" \
            -l "$RESULT_FILE" \
            -e -o "$REPORT_DIR" \
            -JAPI_PATH=$API_PATH \
            -JVARIANT=$VARIANT \
            2>&1 | tee "$RESULTS_DIR/${SCENARIO}-${VARIANT}-${TIMESTAMP}.log"
        
        echo "✓ Completed: $SCENARIO for $VARIANT"
        echo "  Results: $RESULT_FILE"
        echo "  Report: $REPORT_DIR"
        
        # Cool down between scenarios
        echo "Cooling down for 2 minutes..."
        sleep 120
    done
    
    # Stop application
    echo ""
    echo "Stopping application (PID: $APP_PID)..."
    kill $APP_PID 2>/dev/null || true
    sleep 10
    
    # Ensure it's stopped
    kill -9 $APP_PID 2>/dev/null || true
    
    # Cool down between variants
    echo "Cooling down for 5 minutes before next variant..."
    sleep 300
done

echo ""
echo "======================================="
echo "All benchmarks completed!"
echo "======================================="
echo "Results available in: $RESULTS_DIR"
echo ""
echo "Next steps:"
echo "1. Open Grafana: http://localhost:3000"
echo "2. Review JMeter HTML reports in $RESULTS_DIR"
echo "3. Fill in the comparison tables (T2-T7)"
echo ""

