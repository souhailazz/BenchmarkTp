# JMeter Scenarios Configuration

Due to the complexity of JMeter XML files, the full .jmx files should be created using the JMeter GUI.
Here are the detailed configurations for each scenario:

## Scenario 1: READ-heavy (relation include)

### Test Plan Configuration
- **Name**: READ-heavy Benchmark
- **Variables**:
  - BASE_URL: http://localhost:8080
  - API_PATH: ${__P(API_PATH,/api/spring)}

### Thread Group
- **Name**: READ-heavy Users
- **Threads**: 50, 100, 200 (use Stepping Thread Group)
  - Initial: 50 threads
  - Step 1: Add 50 threads after 10 min
  - Step 2: Add 100 threads after 10 min
- **Ramp-up**: 60 seconds per step
- **Duration**: 30 minutes total (10 min each step)

### CSV Data Config
1. **Categories CSV**:
   - Filename: jmeter/data/categories.csv
   - Variable Names: categoryId,categoryCode,categoryName
   - Sharing mode: All threads

2. **Items CSV**:
   - Filename: jmeter/data/items.csv
   - Variable Names: itemId,itemName,itemPrice,itemStock,itemCategoryId
   - Sharing mode: All threads

### HTTP Samplers (with Throughput Controller)

1. **GET /items?page=0&size=50** (50%)
   - Method: GET
   - Path: ${API_PATH}/items?page=${__Random(0,100)}&size=50
   - Throughput: 50%

2. **GET /items?categoryId={id}** (20%)
   - Method: GET
   - Path: ${API_PATH}/items?categoryId=${categoryId}&page=0&size=50
   - Throughput: 20%

3. **GET /categories/{id}/items** (20%)
   - Method: GET
   - Path: ${API_PATH}/categories/${categoryId}/items?page=0&size=50
   - Throughput: 20%

4. **GET /categories?page=0&size=50** (10%)
   - Method: GET
   - Path: ${API_PATH}/categories?page=${__Random(0,20)}&size=50
   - Throughput: 10%

### Listeners
1. **Backend Listener - InfluxDB v2**
   - influxdbUrl: http://localhost:8086
   - influxdbToken: my-super-secret-token
   - influxdbOrganization: benchmark
   - influxdbBucket: jmeter
   - measurement: scenario_1_${__P(VARIANT)}

2. **Summary Report** (for post-test analysis)

---

## Scenario 2: JOIN-filter (targeted)

### Test Plan Configuration
- **Name**: JOIN-filter Benchmark
- **Variables**: Same as Scenario 1

### Thread Group
- **Name**: JOIN-filter Users
- **Threads**: 60, 120 (Stepping)
  - Initial: 60 threads
  - Step 1: Add 60 threads after 8 min
- **Ramp-up**: 60 seconds per step
- **Duration**: 16 minutes total

### HTTP Samplers

1. **GET /items?categoryId={id}** (70%)
   - Method: GET
   - Path: ${API_PATH}/items?categoryId=${categoryId}&page=${__Random(0,20)}&size=50
   - Throughput: 70%

2. **GET /items/{id}** (30%)
   - Method: GET
   - Path: ${API_PATH}/items/${itemId}
   - Throughput: 30%

### Listeners
- Backend Listener: measurement: scenario_2_${__P(VARIANT)}

---

## Scenario 3: MIXED (2 entities)

### Test Plan Configuration
- **Name**: MIXED Operations Benchmark
- **Variables**: Same as Scenario 1 + payload files

### CSV Data Config (additional)
3. **Item Payload Light**:
   - Filename: jmeter/data/item-payload-light.csv
   - Variable Names: itemPayload

4. **Category Payload Light**:
   - Filename: jmeter/data/category-payload-light.csv
   - Variable Names: categoryPayload

### Thread Group
- **Threads**: 50, 100 (Stepping)
- **Ramp-up**: 60 seconds per step
- **Duration**: 20 minutes total (10 min each step)

### HTTP Samplers

1. **GET /items** (40%)
   - Method: GET
   - Path: ${API_PATH}/items?page=${__Random(0,100)}&size=50

2. **POST /items** (20%)
   - Method: POST
   - Path: ${API_PATH}/items
   - Body: ${itemPayload}

3. **PUT /items/{id}** (10%)
   - Method: PUT
   - Path: ${API_PATH}/items/${itemId}
   - Body: ${itemPayload}

4. **DELETE /items/{id}** (10%)
   - Method: DELETE
   - Path: ${API_PATH}/items/${itemId}

5. **POST /categories** (10%)
   - Method: POST
   - Path: ${API_PATH}/categories
   - Body: ${categoryPayload}

6. **PUT /categories/{id}** (10%)
   - Method: PUT
   - Path: ${API_PATH}/categories/${categoryId}
   - Body: ${categoryPayload}

### Listeners
- Backend Listener: measurement: scenario_3_${__P(VARIANT)}

---

## Scenario 4: HEAVY-body (5 KB payload)

### Test Plan Configuration
- **Name**: HEAVY-body Benchmark

### CSV Data Config
- **Item Payload Heavy**:
  - Filename: jmeter/data/item-payload-heavy.csv
  - Variable Names: heavyPayload

### Thread Group
- **Threads**: 30, 60 (Stepping)
- **Ramp-up**: 60 seconds per step
- **Duration**: 16 minutes total (8 min each step)

### HTTP Samplers

1. **POST /items** (50%)
   - Method: POST
   - Path: ${API_PATH}/items
   - Body: ${heavyPayload}
   - Throughput: 50%

2. **PUT /items/{id}** (50%)
   - Method: PUT
   - Path: ${API_PATH}/items/${itemId}
   - Body: ${heavyPayload}
   - Throughput: 50%

### Listeners
- Backend Listener: measurement: scenario_4_${__P(VARIANT)}

---

## Creating JMeter Test Plans

### Option 1: Use JMeter GUI
1. Open JMeter GUI: `jmeter`
2. Follow the configurations above
3. Add HTTP Request Defaults (see http-defaults.jmx)
4. Add Thread Groups with Stepping configurations
5. Add CSV Data Configs
6. Add HTTP Samplers with Throughput Controllers
7. Add Backend Listener (InfluxDB v2)
8. Save as .jmx file

### Option 2: Use JMeter Templates
```bash
# Create from template (if available)
jmeter -t jmeter/templates/read-heavy-template.jmx
```

### Option 3: Command-Line Generation (Advanced)
The configurations above can be used to manually create the .jmx files or use JMeter's recording feature.

---

## Important Notes

1. **Throughput Controllers**: Use "Percent Executions" mode
2. **Random Variables**: Use `${__Random(min,max)}` function
3. **CSV Recycle**: Set to true, sharing mode: All threads
4. **Assertions**: Add Response Code assertions (200, 201, 204)
5. **Timers**: Constant Throughput Timer if needed to control load

## Testing Checklist

- [ ] Test data generated (CSV files exist)
- [ ] InfluxDB running and accessible
- [ ] Application running with correct profile
- [ ] Base URL and API_PATH variables set correctly
- [ ] Backend Listener configured with correct credentials
- [ ] CSV file paths are correct (relative to JMeter working directory)
- [ ] Warm-up period completed before measuring
- [ ] Heavy listeners disabled during actual run

## Validation

Before running full tests, validate with 1-2 threads for 1 minute:
```bash
jmeter -n -t scenario-1-read-heavy.jmx -l test.jtl -JAPI_PATH=/api/spring
```

Check:
- No connection errors
- Response codes are 2xx
- Data from CSV is being used correctly
- InfluxDB receiving metrics

