#!/bin/bash

# Populate database with test data using Spring Boot's data loading
# This is an alternative to JMeter CSV data

echo "Starting database population..."

# Check if PostgreSQL is running
if ! docker ps | grep -q benchmark_postgres; then
    echo "ERROR: PostgreSQL container not running"
    exit 1
fi

echo "Generating data..."
python3 jmeter/generate-test-data.py

echo "Starting application to populate database..."
java -jar \
    -Dspring.profiles.active=spring \
    -Dspring.jpa.hibernate.ddl-auto=create \
    target/rest-benchmark-0.0.1-SNAPSHOT.jar &

APP_PID=$!
echo "Application PID: $APP_PID"

# Wait for app to start
sleep 30

# Check if app is running
if ! curl -s http://localhost:8080/actuator/health > /dev/null; then
    echo "ERROR: Application failed to start"
    kill $APP_PID 2>/dev/null || true
    exit 1
fi

echo "Application started. Populating data via REST API..."

# Populate categories
echo "Creating categories..."
CATEGORIES_CREATED=0
while IFS=, read -r id code name; do
    if [ "$id" != "id" ]; then  # Skip header
        curl -s -X POST http://localhost:8080/api/spring/categories \
            -H "Content-Type: application/json" \
            -d "{\"code\":\"$code\",\"name\":\"$name\"}" > /dev/null
        CATEGORIES_CREATED=$((CATEGORIES_CREATED + 1))
        if [ $((CATEGORIES_CREATED % 100)) -eq 0 ]; then
            echo "  Created $CATEGORIES_CREATED categories..."
        fi
    fi
done < jmeter/data/categories.csv

echo "✓ Created $CATEGORIES_CREATED categories"

# Populate items (first 1000 only for demo, rest will be via JMeter)
echo "Creating sample items (first 1000)..."
ITEMS_CREATED=0
LIMIT=1000
while IFS=, read -r id name price stock categoryId; do
    if [ "$id" != "id" ] && [ $ITEMS_CREATED -lt $LIMIT ]; then
        curl -s -X POST http://localhost:8080/api/spring/items \
            -H "Content-Type: application/json" \
            -d "{\"name\":\"$name\",\"price\":$price,\"stock\":$stock,\"categoryId\":$categoryId}" > /dev/null
        ITEMS_CREATED=$((ITEMS_CREATED + 1))
        if [ $((ITEMS_CREATED % 100)) -eq 0 ]; then
            echo "  Created $ITEMS_CREATED items..."
        fi
    fi
done < jmeter/data/items.csv

echo "✓ Created $ITEMS_CREATED items"

# Stop application
echo "Stopping application..."
kill $APP_PID 2>/dev/null || true
sleep 5

echo ""
echo "Database populated successfully!"
echo "Categories: $CATEGORIES_CREATED"
echo "Items: $ITEMS_CREATED"
echo ""
echo "You can now run benchmarks."

