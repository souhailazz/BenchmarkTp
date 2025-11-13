#!/bin/bash

# Start application with a specific variant profile
# Usage: ./start-variant.sh <variant> [load-data]
# Example: ./start-variant.sh spring
# Example: ./start-variant.sh jersey load-data

if [ $# -lt 1 ]; then
    echo "Usage: $0 <variant> [load-data]"
    echo "Variants: jersey, spring, datarest"
    echo "Add 'load-data' to populate database on startup"
    exit 1
fi

VARIANT=$1
LOAD_DATA=$2

case $VARIANT in
    jersey|spring|datarest)
        echo "Starting application with variant: $VARIANT"
        ;;
    *)
        echo "Invalid variant: $VARIANT"
        echo "Valid variants: jersey, spring, datarest"
        exit 1
        ;;
esac

# Check if JAR exists
JAR_FILE="target/rest-benchmark-0.0.1-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "JAR file not found: $JAR_FILE"
    echo "Please run: mvn clean package"
    exit 1
fi

# Build profiles
PROFILES="$VARIANT"
if [ "$LOAD_DATA" == "load-data" ]; then
    PROFILES="$PROFILES,load-data"
    echo "Data loading enabled"
fi

# Note: Using local PostgreSQL installation
# Make sure PostgreSQL is running locally on port 5432

# Start application
echo "Starting application with profiles: $PROFILES"
echo "JVM Options: -Xms1g -Xmx2g -XX:+UseG1GC"
echo ""
echo "Endpoints will be available at:"
case $VARIANT in
    jersey)
        echo "  http://localhost:8080/api/jersey/categories"
        echo "  http://localhost:8080/api/jersey/items"
        ;;
    spring)
        echo "  http://localhost:8080/api/spring/categories"
        echo "  http://localhost:8080/api/spring/items"
        ;;
    datarest)
        echo "  http://localhost:8080/api/datarest/categories"
        echo "  http://localhost:8080/api/datarest/items"
        ;;
esac
echo ""
echo "Actuator endpoints:"
echo "  http://localhost:8080/actuator/health"
echo "  http://localhost:8080/actuator/prometheus"
echo ""

java -jar \
    -Dspring.profiles.active=$PROFILES \
    -Xms1g \
    -Xmx2g \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    "$JAR_FILE"

