# JMeter Test Scenarios - REST API Benchmark

## Overview
This directory contains JMeter test plans for benchmarking 3 REST API variants:
- **Variant A**: Jersey (JAX-RS) - `/api/jersey/*`
- **Variant C**: Spring @RestController - `/api/spring/*`
- **Variant D**: Spring Data REST - `/api/datarest/*`

## Prerequisites

1. **JMeter 5.6+** with plugins:
   - Backend Listener - InfluxDB v2
   - PerfMon Server Agent Listener

2. **InfluxDB v2** running (via docker-compose)

3. **Test Data**: Run data generation script first
   ```bash
   python jmeter/generate-test-data.py
   ```

## Test Scenarios

### 1. READ-heavy (relation include)
- **Mix**: 50% items list, 20% items by category, 20% cat→items, 10% cat list
- **Threads**: 50 → 100 → 200 (ramp-up 60s)
- **Duration**: 10 min/palier
- **File**: `scenario-1-read-heavy.jmx`

### 2. JOIN-filter (targeted)
- **Mix**: 70% items?categoryId=..., 30% items/{id}
- **Threads**: 60 → 120 (ramp-up 60s)
- **Duration**: 8 min/palier
- **File**: `scenario-2-join-filter.jmx`

### 3. MIXED (2 entities)
- **Mix**: 40% GET items, 20% POST items (1KB), 10% PUT items, 10% DELETE items, 10% POST categories, 10% PUT categories
- **Threads**: 50 → 100 (ramp-up 60s)
- **Duration**: 10 min
- **File**: `scenario-3-mixed.jmx`

### 4. HEAVY-body (5KB payload)
- **Mix**: 50% POST items (5KB), 50% PUT items (5KB)
- **Threads**: 30 → 60 (ramp-up 60s)
- **Duration**: 8 min/palier
- **File**: `scenario-4-heavy-body.jmx`

## Configuration

### JMeter Variables (configure in each .jmx)
```properties
# Base URL (change per variant)
BASE_URL=http://localhost:8080
API_PATH=/api/jersey  # or /api/spring or /api/datarest

# CSV Data Files
CATEGORIES_CSV=${__P(jmeter.data.dir,jmeter/data)}/categories.csv
ITEMS_CSV=${__P(jmeter.data.dir,jmeter/data)}/items.csv
PAYLOAD_LIGHT_CSV=${__P(jmeter.data.dir,jmeter/data)}/item-payload-light.csv
PAYLOAD_HEAVY_CSV=${__P(jmeter.data.dir,jmeter/data)}/item-payload-heavy.csv

# InfluxDB v2 Backend Listener
INFLUX_URL=http://localhost:8086
INFLUX_TOKEN=my-super-secret-token
INFLUX_ORG=benchmark
INFLUX_BUCKET=jmeter
```

## Running Tests

### 1. Start Infrastructure
```bash
docker-compose up -d
```

### 2. Generate Test Data
```bash
python jmeter/generate-test-data.py
```

### 3. Start Application (choose variant)
```bash
# Variant A - Jersey
java -jar -Dspring.profiles.active=jersey target/rest-benchmark-0.0.1-SNAPSHOT.jar

# Variant C - Spring @RestController
java -jar -Dspring.profiles.active=spring target/rest-benchmark-0.0.1-SNAPSHOT.jar

# Variant D - Spring Data REST
java -jar -Dspring.profiles.active=datarest target/rest-benchmark-0.0.1-SNAPSHOT.jar
```

### 4. Run JMeter Test (GUI mode for testing)
```bash
jmeter -t jmeter/scenario-1-read-heavy.jmx
```

### 5. Run JMeter Test (Non-GUI for actual benchmarks)
```bash
jmeter -n -t jmeter/scenario-1-read-heavy.jmx \
  -l results/scenario-1-jersey.jtl \
  -e -o results/scenario-1-jersey-report \
  -JVARIANT=jersey
```

### 6. Run All Scenarios for All Variants
```bash
# Automated script
./run-all-benchmarks.sh
```

## Results Analysis

### Metrics to Collect

**From JMeter (InfluxDB)**:
- RPS (Requests Per Second)
- p50, p95, p99 response times
- Error rate %

**From Prometheus/JMX**:
- CPU usage (process %)
- Heap Memory (max/pic)
- GC time (moy/pic)
- Active Threads
- HikariCP pool usage

**From Grafana**:
- Custom dashboards with all metrics
- Time-series comparisons
- Resource utilization

## Best Practices

1. **Disable HTTP cache** on server (already done in application.properties)
2. **Disable L2 cache** Hibernate (already done)
3. **Use CSV Data Set Config** for realistic IDs
4. **HTTP Request Defaults** - set base URL once
5. **Backend Listener** - InfluxDB v2 only (heavy listeners disabled during run)
6. **Run one service at a time** - isolate measurements
7. **Warm-up period** - first 1-2 minutes not counted in analysis

## Directory Structure
```
jmeter/
├── README.md
├── generate-test-data.py
├── scenario-1-read-heavy.jmx
├── scenario-2-join-filter.jmx
├── scenario-3-mixed.jmx
├── scenario-4-heavy-body.jmx
├── data/
│   ├── categories.csv
│   ├── items.csv
│   ├── category-payload-light.csv
│   ├── item-payload-light.csv
│   └── item-payload-heavy.csv
└── results/
    └── (generated during test runs)
```

## Troubleshooting

**Problem**: OutOfMemoryError in JMeter
**Solution**: Increase heap size:
```bash
export HEAP="-Xms1g -Xmx4g"
jmeter -n -t scenario.jmx ...
```

**Problem**: Connection refused
**Solution**: Check app is running and base URL is correct

**Problem**: InfluxDB write errors
**Solution**: Verify InfluxDB is running and credentials are correct in Backend Listener

## Notes

- Each test should be run 3 times for consistency
- Allow system to cool down between runs (5 minutes)
- Monitor system resources to ensure no throttling
- Document any infrastructure changes (JVM flags, DB config, etc.)

