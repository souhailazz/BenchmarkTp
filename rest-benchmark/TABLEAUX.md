# Tableaux de résultats - REST API Benchmark

Ce fichier contient les tableaux à remplir avec les résultats des tests.

## T0 — Configuration matérielle & logicielle

| Élément | Valeur |
|---------|--------|
| Machine (CPU, cœurs, RAM) | |
| OS / Kernel | |
| Java version | 17 |
| Docker/Compose versions | |
| PostgreSQL version | 14 |
| JMeter version | |
| Prometheus / Grafana / InfluxDB | latest |
| JVM flags (Xms/Xmx, GC) | -Xms1g -Xmx2g -XX:+UseG1GC |
| HikariCP (min/max/timeout) | min=10, max=20, timeout=30s |

## T1 — Scénarios

| Scénario | Mix | Threads (paliers) | Ramp-up | Durée/palier | Payload |
|----------|-----|-------------------|---------|--------------|---------|
| READ-heavy (relation) | 50% items list, 20% items by category, 20% cat→items, 10% cat list | 50→100→200 | 60s | 10 min | – |
| JOIN-filter | 70% items?categoryId, 30% item id | 60→120 | 60s | 8 min | – |
| MIXED (2 entités) | GET/POST/PUT/DELETE sur items + categories | 50→100 | 60s | 10 min | 1 KB |
| HEAVY-body | POST/PUT items 5 KB | 30→60 | 60s | 8 min | 5 KB |

## T2 — Résultats JMeter (par scénario et variante)

| Scénario | Mesure | A : Jersey | C : @RestController | D : Spring Data REST |
|----------|--------|------------|---------------------|---------------------|
| READ-heavy | RPS | | | |
| READ-heavy | p50 (ms) | | | |
| READ-heavy | p95 (ms) | | | |
| READ-heavy | p99 (ms) | | | |
| READ-heavy | Err % | | | |
| JOIN-filter | RPS | | | |
| JOIN-filter | p50 (ms) | | | |
| JOIN-filter | p95 (ms) | | | |
| JOIN-filter | p99 (ms) | | | |
| JOIN-filter | Err % | | | |
| MIXED (2 entités) | RPS | | | |
| MIXED (2 entités) | p50 (ms) | | | |
| MIXED (2 entités) | p95 (ms) | | | |
| MIXED (2 entités) | p99 (ms) | | | |
| MIXED (2 entités) | Err % | | | |
| HEAVY-body | RPS | | | |
| HEAVY-body | p50 (ms) | | | |
| HEAVY-body | p95 (ms) | | | |
| HEAVY-body | p99 (ms) | | | |
| HEAVY-body | Err % | | | |

## T3 — Ressources JVM (Prometheus)

| Variante | CPU proc. (%) moy/pic | Heap (Mo) moy/pic | GC time (ms/s) moy/pic | Threads actifs moy/pic | Hikari (actifs/max) |
|----------|----------------------|-------------------|----------------------|----------------------|-------------------|
| A : Jersey | | | | | |
| C : @RestController | | | | | |
| D : Spring Data REST | | | | | |

## T4 — Détails par endpoint (scénario JOIN-filter)

| Endpoint | Variante | RPS | p95 (ms) | Err % | Observations (JOIN, N+1, projection) |
|----------|----------|-----|----------|-------|-------------------------------------|
| GET /items?categoryId= | A | | | | |
| | C | | | | |
| | D | | | | |
| GET /categories/{id}/items | A | | | | |
| | C | | | | |
| | D | | | | |

## T5 — Détails par endpoint (scénario MIXED)

| Endpoint | Variante | RPS | p95 (ms) | Err % | Observations |
|----------|----------|-----|----------|-------|--------------|
| GET /items | A | | | | |
| | C | | | | |
| | D | | | | |
| POST /items | A | | | | |
| | C | | | | |
| | D | | | | |
| PUT /items/{id} | A | | | | |
| | C | | | | |
| | D | | | | |
| DELETE /items/{id} | A | | | | |
| | C | | | | |
| | D | | | | |
| GET /categories | A | | | | |
| | C | | | | |
| | D | | | | |
| POST /categories | A | | | | |
| | C | | | | |
| | D | | | | |

## T6 — Incidents / erreurs

| Run | Variante | Type d'erreur (HTTP/DB/timeout) | % | Cause probable | Action corrective |
|-----|----------|--------------------------------|---|----------------|------------------|
| | | | | | |
| | | | | | |

## T7 — Synthèse & conclusion

| Critère | Meilleure variante | Écart (justifier) | Commentaires |
|---------|-------------------|-------------------|--------------|
| Débit global (RPS) | | | |
| Latence p95 | | | |
| Stabilité (erreurs) | | | |
| Empreinte CPU/RAM | | | |
| Facilité d'expo relationnelle | | | |

### Observations générales

**N+1 Query Problem:**
- Mode JOIN FETCH vs baseline:
  - Différence de performance observée: ____%
  - Impact sur la latence p95: ___ms
  - Recommandation: 

**Validation:**
- Impact de Bean Validation sur les performances: ____%
- Différence entre variantes: 

**Sérialisation:**
- Performance Jackson: 
- Overhead de sérialisation estimé: 

**Pagination:**
- Impact sur les performances: 
- Taille de page optimale: 

### Conclusion finale

Variante recommandée: **___________**

Justification:


Points forts:
- 
- 
- 

Points faibles:
- 
- 
- 

Cas d'usage idéaux:
- 
- 

### Recommandations

1. Configuration JVM:
   - 

2. Configuration HikariCP:
   - 

3. Optimisations applicatives:
   - 

4. Infrastructure:
   - 

---

## Notes méthodologiques

### Conditions de test
- Chaque test exécuté 3 fois minimum
- Période de warm-up: 1-2 minutes non comptées
- Cool-down entre tests: 5 minutes
- Infrastructure stable (pas de throttling observé)

### Limitations
- Tests en local (non distribué)
- Base de données sur même machine
- Réseau localhost (latence minimale)

### Améliorations possibles
- Tests distribués (JMeter en mode remote)
- Base de données sur serveur dédié
- Tests en conditions réelles (network latency, etc.)
- Tests de montée en charge progressive
- Tests d'endurance (plusieurs heures)

