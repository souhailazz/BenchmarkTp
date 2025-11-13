# REST API Benchmark Project

Comparaison de performance de 3 variantes d'implémentation REST API avec Spring Boot.

## 📋 Vue d'ensemble

Ce projet implémente et benchmark trois approches différentes pour créer des APIs REST en Java:

- **Variante A**: Jersey (JAX-RS) - Standard Java pour REST
- **Variante C**: Spring @RestController - Approche Spring classique
- **Variante D**: Spring Data REST - Génération automatique d'APIs

## 🏗️ Architecture

### Entités
- **Category**: Catégories de produits (2000 catégories de test)
- **Item**: Articles (100,000 articles de test, ~50 par catégorie)

### Endpoints

Chaque variante expose les mêmes endpoints sous des chemins différents:

#### Variante A - Jersey (`/api/jersey`)
```
GET    /api/jersey/categories?page={p}&size={s}
GET    /api/jersey/categories/{id}
GET    /api/jersey/categories/{id}/items
POST   /api/jersey/categories
PUT    /api/jersey/categories/{id}
DELETE /api/jersey/categories/{id}

GET    /api/jersey/items?page={p}&size={s}
GET    /api/jersey/items?categoryId={id}
GET    /api/jersey/items/{id}
POST   /api/jersey/items
PUT    /api/jersey/items/{id}
DELETE /api/jersey/items/{id}
```

#### Variante C - Spring (`/api/spring`)
```
GET    /api/spring/categories?page={p}&size={s}
GET    /api/spring/categories/{id}
GET    /api/spring/categories/{id}/items
POST   /api/spring/categories
PUT    /api/spring/categories/{id}
DELETE /api/spring/categories/{id}

GET    /api/spring/items?page={p}&size={s}
GET    /api/spring/items?categoryId={id}
GET    /api/spring/items/{id}
POST   /api/spring/items
PUT    /api/spring/items/{id}
DELETE /api/spring/items/{id}
```

#### Variante D - Spring Data REST (`/api/datarest`)
```
GET    /api/datarest/categories?page={p}&size={s}
GET    /api/datarest/categories/{id}
POST   /api/datarest/categories
PUT    /api/datarest/categories/{id}
DELETE /api/datarest/categories/{id}

GET    /api/datarest/items?page={p}&size={s}
GET    /api/datarest/items/{id}
GET    /api/datarest/items/search/findByCategoryId?categoryId={id}
POST   /api/datarest/items
PUT    /api/datarest/items/{id}
DELETE /api/datarest/items/{id}
```

## 🚀 Démarrage rapide

### Prérequis

- Java 17+
- Docker & Docker Compose
- Maven 3.8+
- Python 3.8+ (pour génération de données)
- JMeter 5.6+ (pour les tests de charge)

### 1. Démarrer l'infrastructure

```bash
# PostgreSQL + Prometheus + Grafana + InfluxDB
docker-compose up -d

# Vérifier que tout est up
docker-compose ps
```

Services disponibles:
- PostgreSQL: `localhost:5432`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000` (admin/admin)
- InfluxDB: `http://localhost:8086`

### 2. Compiler l'application

```bash
mvn clean package -DskipTests
```

### 3. Générer les données de test

```bash
python3 jmeter/generate-test-data.py
```

Cela génère:
- `jmeter/data/categories.csv` (2000 lignes)
- `jmeter/data/items.csv` (~100,000 lignes)
- Fichiers de payload pour POST/PUT

### 4. Démarrer une variante

#### Variante A - Jersey
```bash
java -jar \
  -Dspring.profiles.active=jersey \
  -Xms1g -Xmx2g \
  target/rest-benchmark-0.0.1-SNAPSHOT.jar
```

#### Variante C - Spring
```bash
java -jar \
  -Dspring.profiles.active=spring \
  -Xms1g -Xmx2g \
  target/rest-benchmark-0.0.1-SNAPSHOT.jar
```

#### Variante D - Spring Data REST
```bash
java -jar \
  -Dspring.profiles.active=datarest \
  -Xms1g -Xmx2g \
  target/rest-benchmark-0.0.1-SNAPSHOT.jar
```

### 5. Vérifier le démarrage

```bash
# Health check
curl http://localhost:8080/actuator/health

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus

# Test endpoint (remplacer {variant} par jersey/spring/datarest)
curl http://localhost:8080/api/{variant}/categories?page=0&size=10
```

## 📊 Scénarios de test

### Scénario 1: READ-heavy (relation include)
- **Mix**: 50% items list, 20% items by category, 20% cat→items, 10% cat list
- **Charge**: 50 → 100 → 200 threads
- **Durée**: 30 min (10 min/palier)

### Scénario 2: JOIN-filter (ciblé)
- **Mix**: 70% items?categoryId, 30% items/{id}
- **Charge**: 60 → 120 threads
- **Durée**: 16 min (8 min/palier)

### Scénario 3: MIXED (2 entités)
- **Mix**: 40% GET, 20% POST, 10% PUT, 10% DELETE items + 10% POST/PUT categories
- **Charge**: 50 → 100 threads
- **Durée**: 20 min (10 min/palier)

### Scénario 4: HEAVY-body (5 KB)
- **Mix**: 50% POST items, 50% PUT items (payload 5KB)
- **Charge**: 30 → 60 threads
- **Durée**: 16 min (8 min/palier)

## 🧪 Exécuter les benchmarks

### Test unique

```bash
chmod +x run-single-test.sh
./run-single-test.sh spring scenario-1-read-heavy
```

### Suite complète (tous scénarios, toutes variantes)

```bash
chmod +x run-all-benchmarks.sh
./run-all-benchmarks.sh
```

⚠️ **Attention**: La suite complète prend environ 3-4 heures.

### Mode JMeter GUI (pour tester)

```bash
jmeter -t jmeter/scenario-1-read-heavy.jmx
```

## 📈 Analyse des résultats

### Métriques JMeter (InfluxDB)
- **RPS**: Requests Per Second
- **p50, p95, p99**: Percentiles des temps de réponse (ms)
- **Err %**: Taux d'erreur

### Métriques JVM (Prometheus)
- **CPU proc (%)**: Utilisation CPU du processus
- **Heap Memory (Mo)**: Mémoire heap utilisée (moy/pic)
- **GC time (ms/s)**: Temps passé en GC (moy/pic)
- **Threads actifs**: Nombre de threads actifs
- **HikariCP**: Utilisation du pool de connexions

### Grafana Dashboards

1. Ouvrir Grafana: `http://localhost:3000`
2. Login: admin/admin
3. Aller dans Dashboards > REST Benchmark
4. Sélectionner la plage horaire du test
5. Comparer les variantes

### Rapports JMeter HTML

Les rapports sont générés automatiquement dans `jmeter/results/`:
```
jmeter/results/
├── scenario-1-jersey-20250105_140523-report/
│   └── index.html
├── scenario-1-spring-20250105_142030-report/
│   └── index.html
└── ...
```

Ouvrir `index.html` dans un navigateur pour voir le rapport détaillé.

## 📋 Tableaux à remplir

Les résultats doivent être documentés dans les tableaux suivants (voir documentation originale):

### T0 - Configuration matérielle & logicielle
- Machine (CPU, RAM)
- OS / Kernel
- Java version
- PostgreSQL version
- JMeter version
- Prometheus / Grafana / InfluxDB
- JVM flags (Xms/Xmx, GC)
- HikariCP (min/max/timeout)

### T1 - Scénarios
Résumé des 4 scénarios avec mix, threads, ramp-up, durée, payload

### T2 - Résultats JMeter (par scénario et variante)
Pour chaque combinaison:
- RPS
- p50, p95, p99 (ms)
- Err %

### T3 - Ressources JVM (Prometheus)
Pour chaque variante:
- CPU proc (moy/pic)
- Heap Memory (moy/pic)
- GC time (moy/pic)
- Threads (moy/pic)
- HikariCP (actifs/max)

### T4 - Détails par endpoint (scénario JOIN-filter)
- GET /items?categoryId=...
- GET /categories/{id}/items

Pour chaque variante: RPS, p95, Err %, observations

### T5 - Détails par endpoint (scénario MIXED)
- GET /items
- POST /items
- PUT /items/{id}
- DELETE /items/{id}
- GET /categories
- POST /categories

Pour chaque variante: RPS, p95, Err %, observations

### T6 - Incidents / erreurs
- Run
- Variante
- Type d'erreur (HTTP/DB/timeout)
- %
- Cause probable
- Action corrective

### T7 - Synthèse & conclusion
- Débit global (RPS)
- Latence p95
- Stabilité (erreurs)
- Empreinte CPU/RAM
- Facilité d'expo relationnelle

Critères: Meilleure variante, écart, commentaires

## 🔧 Configuration avancée

### JVM Tuning

Pour tests avec différentes configs JVM:
```bash
java -jar \
  -Dspring.profiles.active=spring \
  -Xms2g -Xmx4g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+PrintGCDetails \
  -XX:+PrintGCTimeStamps \
  -Xloggc:gc.log \
  target/rest-benchmark-0.0.1-SNAPSHOT.jar
```

### HikariCP Tuning

Modifier dans `application.properties`:
```properties
spring.datasource.hikari.maximum-pool-size=30
spring.datasource.hikari.minimum-idle=15
```

### PostgreSQL Tuning

Modifier `docker-compose.yml`:
```yaml
command: >
  postgres
  -c shared_buffers=512MB
  -c max_connections=300
  -c effective_cache_size=2GB
```

## 🐛 Troubleshooting

### L'application ne démarre pas
```bash
# Vérifier que PostgreSQL est up
docker-compose ps

# Vérifier les logs
docker-compose logs postgres

# Tester la connexion
psql -h localhost -U benchmark_user -d benchmark_db
```

### JMeter: Connection refused
- Vérifier que l'app tourne: `curl http://localhost:8080/actuator/health`
- Vérifier le profile actif correspond au chemin API testé

### JMeter: OutOfMemoryError
```bash
export HEAP="-Xms2g -Xmx4g"
jmeter -n -t scenario.jmx ...
```

### InfluxDB: Write errors
```bash
# Vérifier InfluxDB
docker-compose logs influxdb

# Tester la connexion
curl http://localhost:8086/health
```

## 📚 Structure du projet

```
rest-benchmark/
├── src/main/java/com/projet/rest_benchmark/
│   ├── entity/              # Category, Item
│   ├── repository/          # JPA Repositories
│   ├── service/             # Business logic
│   ├── dto/                 # DTOs
│   ├── controller/
│   │   ├── jersey/          # Variante A
│   │   └── spring/          # Variante C
│   └── config/              # Configurations
├── src/main/resources/
│   ├── application.properties
│   └── jmx-exporter-config.yaml
├── jmeter/
│   ├── scenario-1-read-heavy.jmx
│   ├── scenario-2-join-filter.jmx
│   ├── scenario-3-mixed.jmx
│   ├── scenario-4-heavy-body.jmx
│   ├── generate-test-data.py
│   ├── data/                # CSV files
│   └── results/             # Test results
├── monitoring/
│   ├── prometheus.yml
│   └── grafana/
│       ├── datasources/
│       └── dashboards/
├── docker-compose.yml
├── run-all-benchmarks.sh
├── run-single-test.sh
├── pom.xml
└── README.md
```

## 📖 Documentation supplémentaire

- [JMeter Scenarios](jmeter/README.md)
- [JMeter Configuration Details](jmeter/JMETER-SCENARIOS.md)

## 🎯 Points d'attention

### N+1 Query Problem
Le projet expose **deux modes** pour tester l'impact du N+1:

1. **Mode JOIN FETCH** (projection DTO)
   - Utilise `@Query` avec `JOIN FETCH`
   - Évite le N+1
   - Variable d'env: `JOIN_MODE=true`

2. **Mode baseline** (sans JOIN FETCH)
   - Mesure l'écart de performance
   - Variable d'env: `JOIN_MODE=false`

### Pagination
Toutes les listes utilisent la pagination Spring Data:
- Par défaut: page=0, size=50
- Max size: 200

### Validation
Bean Validation (JSR-380) activée sur toutes les variantes de façon homogène.

### Sérialisation
Jackson par défaut avec les mêmes configurations pour toutes les variantes.

## 👥 Auteurs

Projet de benchmark REST API - Comparaison Jersey vs Spring @RestController vs Spring Data REST

## 📄 Licence

Projet éducatif / académique

