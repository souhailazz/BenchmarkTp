# Quick Start Guide - REST API Benchmark

Guide de démarrage rapide en 5 minutes.

## Prérequis

✅ Java 17+  
✅ Docker & Docker Compose  
✅ Maven 3.8+  

## Étape 1: Démarrer l'infrastructure (2 min)

```bash
# Démarrer PostgreSQL, Prometheus, Grafana, InfluxDB
docker-compose up -d

# Vérifier que tout est up
docker-compose ps
```

Vous devriez voir 4 conteneurs running:
- benchmark_postgres
- benchmark_prometheus
- benchmark_grafana
- benchmark_influxdb

## Étape 2: Compiler l'application (1 min)

```bash
mvn clean package -DskipTests
```

Cela créé le JAR dans `target/rest-benchmark-0.0.1-SNAPSHOT.jar`

## Étape 3: Démarrer une variante avec données (2 min)

### Option A: Avec chargement automatique des données

```bash
chmod +x start-variant.sh
./start-variant.sh spring load-data
```

Cela:
1. Démarre l'application avec le profil Spring @RestController
2. Charge automatiquement 2000 catégories et ~100,000 items
3. Expose les endpoints sur `/api/spring/*`

### Option B: Sans données (pour tester vide)

```bash
./start-variant.sh spring
```

## Étape 4: Tester les endpoints

Une fois l'application démarrée (attendre "Started RestBenchmarkApplication"):

```bash
# Health check
curl http://localhost:8080/actuator/health

# Lister les catégories (page 1)
curl http://localhost:8080/api/spring/categories?page=0&size=10

# Lister les items (page 1)
curl http://localhost:8080/api/spring/items?page=0&size=10

# Créer une catégorie
curl -X POST http://localhost:8080/api/spring/categories \
  -H "Content-Type: application/json" \
  -d '{"code":"TEST01","name":"Test Category"}'

# Créer un item
curl -X POST http://localhost:8080/api/spring/items \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Item","price":99.99,"stock":10,"categoryId":1}'
```

## Étape 5: Tester les autres variantes

### Jersey (JAX-RS)

```bash
# Arrêter l'application Spring (Ctrl+C)
./start-variant.sh jersey load-data
```

Endpoints: `/api/jersey/*`

### Spring Data REST

```bash
# Arrêter l'application Jersey (Ctrl+C)
./start-variant.sh datarest load-data
```

Endpoints: `/api/datarest/*`

**Note**: Spring Data REST a une structure légèrement différente (HAL format)

## Visualiser les métriques

### Prometheus
```bash
open http://localhost:9090
```

Requêtes utiles:
```promql
# RPS
rate(http_server_requests_seconds_count[1m])

# Latence p95
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[1m]))

# CPU Usage
process_cpu_usage * 100

# Memory
jvm_memory_used_bytes{area="heap"} / 1024 / 1024
```

### Grafana
```bash
open http://localhost:3000
# Login: admin / admin
```

1. Aller dans Dashboards
2. Importer le dashboard dans `monitoring/grafana/dashboards/rest-benchmark-dashboard.json`
3. Sélectionner la datasource Prometheus
4. Visualiser les métriques en temps réel

## Prochaines étapes

✅ **Application démarrée et testée**

Maintenant vous pouvez:

### 1. Générer les données de test JMeter

```bash
python3 jmeter/generate-test-data.py
```

Cela crée les CSV files dans `jmeter/data/`

### 2. Créer les tests JMeter

Suivre le guide dans `jmeter/JMETER-SCENARIOS.md` pour créer les 4 scénarios de test avec JMeter GUI.

Ou utiliser les templates fournis (à créer manuellement dans JMeter):
- scenario-1-read-heavy.jmx
- scenario-2-join-filter.jmx
- scenario-3-mixed.jmx
- scenario-4-heavy-body.jmx

### 3. Lancer les benchmarks

```bash
# Test unique
./run-single-test.sh spring scenario-1-read-heavy

# Suite complète (3-4 heures)
./run-all-benchmarks.sh
```

### 4. Analyser les résultats

Remplir les tableaux dans `TABLEAUX.md` avec:
- Métriques JMeter (RPS, latences, erreurs)
- Métriques JVM (CPU, mémoire, GC)
- Comparaison des variantes

## Troubleshooting

### Port 8080 déjà utilisé
```bash
# Trouver le processus
lsof -i :8080  # Mac/Linux
netstat -ano | findstr :8080  # Windows

# Tuer le processus ou changer le port
java -jar -Dserver.port=8081 target/rest-benchmark-0.0.1-SNAPSHOT.jar
```

### PostgreSQL ne démarre pas
```bash
# Logs PostgreSQL
docker-compose logs postgres

# Redémarrer
docker-compose restart postgres
```

### Base de données vide après démarrage
```bash
# Vérifier le profil
# Le profil 'load-data' doit être actif pour charger les données
./start-variant.sh spring load-data
```

### Application ne démarre pas
```bash
# Vérifier que PostgreSQL est accessible
docker-compose ps
nc -zv localhost 5432  # Mac/Linux
Test-NetConnection -ComputerName localhost -Port 5432  # PowerShell

# Vérifier les logs de l'application
# Elles s'affichent dans le terminal
```

## Commandes utiles

### Docker
```bash
# Arrêter tout
docker-compose down

# Nettoyer les volumes (ATTENTION: supprime les données)
docker-compose down -v

# Voir les logs
docker-compose logs -f postgres
docker-compose logs -f prometheus
docker-compose logs -f grafana
docker-compose logs -f influxdb
```

### Base de données
```bash
# Se connecter à PostgreSQL
docker exec -it benchmark_postgres psql -U benchmark_user -d benchmark_db

# Compter les données
SELECT COUNT(*) FROM category;
SELECT COUNT(*) FROM item;

# Quitter
\q
```

### Maven
```bash
# Recompiler rapidement
mvn clean package -DskipTests -T 4

# Nettoyer complètement
mvn clean

# Voir les dépendances
mvn dependency:tree
```

## Architecture rapide

```
rest-benchmark/
├── src/                          # Code source
│   ├── entity/                   # Category, Item (JPA)
│   ├── repository/               # Spring Data JPA
│   ├── service/                  # Business logic
│   ├── dto/                      # Data Transfer Objects
│   ├── controller/
│   │   ├── jersey/               # Variante A (JAX-RS)
│   │   └── spring/               # Variante C (@RestController)
│   └── config/                   # Configurations
├── docker-compose.yml            # Infrastructure
├── start-variant.sh              # Démarrer une variante
├── jmeter/                       # Tests de charge
│   ├── generate-test-data.py    # Génération CSV
│   └── data/                     # Fichiers CSV
└── monitoring/                   # Prometheus + Grafana
```

## Profils Spring

L'application utilise des profils pour activer les variantes:

- **jersey**: Active la variante Jersey (JAX-RS)
- **spring**: Active la variante @RestController
- **datarest**: Active Spring Data REST
- **load-data**: Charge les données au démarrage

Combiner avec:
```bash
java -jar -Dspring.profiles.active=spring,load-data target/rest-benchmark-0.0.1-SNAPSHOT.jar
```

## URLs de référence

- Application: http://localhost:8080
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/prometheus
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin)
- InfluxDB: http://localhost:8086

## Documentation complète

- [README principal](README.md)
- [Guide JMeter](jmeter/README.md)
- [Scénarios JMeter détaillés](jmeter/JMETER-SCENARIOS.md)
- [Tableaux de résultats](TABLEAUX.md)

---

**Temps total**: ~5 minutes pour être opérationnel ! 🚀

