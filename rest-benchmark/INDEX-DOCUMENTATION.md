# 📚 Index de la Documentation - REST API Benchmark

Voici tous les fichiers de documentation et leur utilisation.

## 🚀 Pour commencer (LIRE EN PREMIER)

### 1. **DEMARRAGE-RAPIDE.txt** ⭐ COMMENCEZ ICI
Fichier texte simple avec UNIQUEMENT les commandes à exécuter dans l'ordre.
**À lire en premier si vous débutez.**

### 2. **SETUP-INITIAL.md**
Guide complet de configuration initiale avec explications détaillées.
- Vérification des prérequis
- Installation pas à pas
- Dépannage détaillé

### 3. **COMMANDES-WINDOWS.md**
Toutes les commandes PowerShell adaptées pour Windows.
- Commandes Docker
- Commandes Maven
- Commandes pour tester les APIs
- Scripts de diagnostic

---

## 📖 Documentation principale

### 4. **README.md**
Documentation générale du projet :
- Architecture du projet
- Liste des endpoints
- Scénarios de test
- Structure du projet
- Documentation technique complète

### 5. **QUICKSTART.md**
Guide de démarrage en 5 minutes (version condensée).

---

## 🧪 Tests et Benchmarks

### 6. **jmeter/README.md**
Guide complet pour JMeter :
- Configuration des scénarios
- Exécution des tests
- Configuration InfluxDB
- Bonnes pratiques

### 7. **jmeter/JMETER-SCENARIOS.md**
Détails techniques des 4 scénarios de test :
- Configuration Thread Groups
- Configuration CSV Data
- Configuration HTTP Samplers
- Configuration Listeners

---

## 📊 Résultats

### 8. **TABLEAUX.md**
Tableaux à remplir avec les résultats des tests :
- T0 : Configuration matérielle & logicielle
- T1 : Scénarios
- T2 : Résultats JMeter
- T3 : Ressources JVM
- T4 : Détails par endpoint (JOIN-filter)
- T5 : Détails par endpoint (MIXED)
- T6 : Incidents / erreurs
- T7 : Synthèse & conclusion

---

## 🔧 Fichiers de configuration

### Code Source
- `src/main/java/com/projet/rest_benchmark/` : Code Java
  - `entity/` : Entités JPA (Category, Item)
  - `repository/` : Spring Data JPA
  - `service/` : Logique métier
  - `dto/` : Data Transfer Objects
  - `controller/jersey/` : Variante A (JAX-RS)
  - `controller/spring/` : Variante C (@RestController)
  - `config/` : Configurations Spring

### Configuration Application
- `src/main/resources/application.properties` : Configuration Spring Boot
- `src/main/resources/jmx-exporter-config.yaml` : Configuration JMX exporter

### Infrastructure
- `docker-compose.yml` : Configuration Docker (PostgreSQL, Prometheus, Grafana, InfluxDB)
- `monitoring/prometheus.yml` : Configuration Prometheus
- `monitoring/grafana/datasources/datasource.yml` : Sources de données Grafana
- `monitoring/grafana/dashboards/` : Dashboards Grafana

### Build & Déploiement
- `pom.xml` : Configuration Maven (dépendances, plugins)
- `start-variant.sh` : Script pour démarrer une variante (Linux/Mac)
- `run-all-benchmarks.sh` : Script pour lancer tous les tests
- `run-single-test.sh` : Script pour lancer un test unique

### Données de test
- `jmeter/generate-test-data.py` : Script Python pour générer les CSV
- `scripts/setup-database.sql` : Requêtes SQL utiles
- `scripts/populate-database.sh` : Script pour peupler la base

---

## 📋 Ordre de lecture recommandé

### Débutant (première utilisation)
1. **DEMARRAGE-RAPIDE.txt** - Suivre les commandes
2. **SETUP-INITIAL.md** - Si besoin de plus de détails
3. **COMMANDES-WINDOWS.md** - Référence des commandes

### Configuration complète
1. **README.md** - Comprendre le projet
2. **SETUP-INITIAL.md** - Configuration pas à pas
3. **jmeter/README.md** - Préparer les tests

### Exécution des benchmarks
1. **jmeter/JMETER-SCENARIOS.md** - Créer les tests JMeter
2. **run-all-benchmarks.sh** ou commandes manuelles
3. **TABLEAUX.md** - Documenter les résultats

---

## 🎯 Par cas d'usage

### "Je veux juste tester l'application rapidement"
→ **DEMARRAGE-RAPIDE.txt**

### "Je veux comprendre comment tout fonctionne"
→ **README.md** puis **SETUP-INITIAL.md**

### "Je veux lancer les benchmarks"
→ **jmeter/README.md** puis **jmeter/JMETER-SCENARIOS.md**

### "J'ai un problème"
→ **SETUP-INITIAL.md** (section Dépannage)
→ **COMMANDES-WINDOWS.md** (section Diagnostics)

### "Je veux documenter mes résultats"
→ **TABLEAUX.md**

---

## 📁 Structure complète du projet

```
rest-benchmark/
│
├─── Documentation
│    ├── DEMARRAGE-RAPIDE.txt ⭐ COMMENCER ICI
│    ├── SETUP-INITIAL.md
│    ├── COMMANDES-WINDOWS.md
│    ├── README.md
│    ├── QUICKSTART.md
│    ├── TABLEAUX.md
│    └── INDEX-DOCUMENTATION.md (ce fichier)
│
├─── Code source
│    ├── src/main/java/
│    ├── src/main/resources/
│    └── pom.xml
│
├─── Infrastructure
│    ├── docker-compose.yml
│    └── monitoring/
│         ├── prometheus.yml
│         └── grafana/
│
├─── Tests JMeter
│    ├── jmeter/README.md
│    ├── jmeter/JMETER-SCENARIOS.md
│    ├── jmeter/generate-test-data.py
│    └── jmeter/data/ (fichiers CSV générés)
│
└─── Scripts
     ├── start-variant.sh
     ├── run-all-benchmarks.sh
     ├── run-single-test.sh
     └── scripts/
```

---

## 🔍 Recherche rapide

**Commande pour démarrer Docker ?**
→ COMMANDES-WINDOWS.md ou DEMARRAGE-RAPIDE.txt

**Commande pour compiler ?**
→ COMMANDES-WINDOWS.md : `mvn clean package -DskipTests`

**Comment tester les endpoints ?**
→ COMMANDES-WINDOWS.md (section "Tester les endpoints")

**Configuration PostgreSQL ?**
→ docker-compose.yml ou application.properties

**Créer les scénarios JMeter ?**
→ jmeter/JMETER-SCENARIOS.md

**Liste des endpoints par variante ?**
→ README.md (section "Endpoints")

**Problème de démarrage ?**
→ SETUP-INITIAL.md (section "Dépannage")

---

## 💡 Conseils

1. **Gardez DEMARRAGE-RAPIDE.txt ouvert** pendant votre première utilisation
2. **Utilisez Ctrl+F** dans les fichiers .md pour rechercher des mots-clés
3. **Les commandes PowerShell** sont dans COMMANDES-WINDOWS.md
4. **Les commandes Linux/Mac** sont dans les fichiers .sh
5. **Consultez TABLEAUX.md régulièrement** pendant les tests pour documenter

---

## 📞 En cas de problème

1. **Vérifier** : SETUP-INITIAL.md → Section "Dépannage"
2. **Diagnostiquer** : COMMANDES-WINDOWS.md → Section "Diagnostics"
3. **Logs Docker** : `docker-compose logs -f`
4. **Logs application** : Visible dans le terminal où elle tourne

---

**Bon benchmark ! 🚀**

