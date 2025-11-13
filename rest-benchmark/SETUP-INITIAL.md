# Guide de Configuration Initiale - REST API Benchmark

Ce guide vous accompagne pas à pas pour configurer et lancer le projet depuis zéro.

## ✅ Prérequis à installer

Avant de commencer, assurez-vous d'avoir installé :

### 1. Java 17 ou supérieur
```bash
# Vérifier la version
java -version
```

Si pas installé : https://adoptium.net/ (recommandé)

### 2. Maven 3.8+
```bash
# Vérifier la version
mvn -version
```

Si pas installé : https://maven.apache.org/download.cgi

### 3. PostgreSQL 14+ (avec pgAdmin)
```bash
# Vérifier la version
psql --version
```

Si pas installé : https://www.postgresql.org/download/windows/
Note: Installer avec pgAdmin inclus

### 4. Python 3.8+ (pour génération des données de test)
```bash
# Vérifier la version
python --version
# ou
python3 --version
```

Si pas installé : https://www.python.org/downloads/

### 5. JMeter 5.6+ (optionnel pour l'instant, nécessaire pour les benchmarks)
- Télécharger : https://jmeter.apache.org/download_jmeter.cgi
- Extraire dans un dossier (ex: C:\apache-jmeter ou /opt/apache-jmeter)

---

## 📋 Étape 1 : Vérifier que tous les fichiers sont présents

Vérifiez que votre projet contient ces fichiers clés :

```
rest-benchmark/
├── docker-compose.yml          ✓ Configuration infrastructure
├── pom.xml                     ✓ Configuration Maven
├── start-variant.sh            ✓ Script démarrage application
├── src/                        ✓ Code source Java
│   └── main/
│       ├── java/
│       └── resources/
│           └── application.properties
├── jmeter/
│   ├── generate-test-data.py  ✓ Script génération données
│   └── data/                   ✓ Dossier pour CSV (sera créé)
└── monitoring/
    ├── prometheus.yml          ✓ Config Prometheus
    └── grafana/                ✓ Config Grafana
```

---

## 🗄️ Étape 2 : Configurer PostgreSQL local

### 2.1 Ouvrir pgAdmin 4

1. Lancez **pgAdmin 4** depuis le menu Démarrer Windows
2. Entrez votre Master Password pgAdmin

### 2.2 Se connecter au serveur PostgreSQL

1. Cliquez sur **Servers** dans l'arbre à gauche
2. Cliquez sur **PostgreSQL 14** (ou votre version)
3. Entrez le mot de passe PostgreSQL que vous avez défini lors de l'installation

### 2.3 Créer la base de données

1. **Clic droit** sur **Databases** → **Create** → **Database...**
2. **Database name** : `benchmark_db`
3. **Owner** : `postgres`
4. Cliquez sur **Save**

### 2.4 Créer l'utilisateur

1. **Clic droit** sur **Login/Group Roles** → **Create** → **Login/Group Role...**
2. **Onglet "General"** → **Name** : `benchmark_user`
3. **Onglet "Definition"** → **Password** : `benchmark_pass`
4. **Onglet "Privileges"** → Cochez **Can login?** et **Superuser?**
5. Cliquez sur **Save**

Cette configuration va permettre à l'application de :
- ✅ Se connecter à PostgreSQL sur port 5432
- ✅ Créer automatiquement les tables
- ✅ Insérer automatiquement les données de test

### 2.5 Vérifier la base de données

Dans pgAdmin :
1. Développez **Servers** → **PostgreSQL** → **Databases**
2. Vous devriez voir **benchmark_db** ✅
3. Développez **Login/Group Roles**
4. Vous devriez voir **benchmark_user** ✅

La base est maintenant prête à recevoir les données !

---

## 🔨 Étape 3 : Compiler l'application Java

### 3.1 Nettoyer et compiler

```bash
# Dans le dossier du projet
mvn clean package -DskipTests
```

Cette commande va :
- ✅ Télécharger toutes les dépendances Maven (première fois ~5-10 min)
- ✅ Compiler le code source
- ✅ Créer le fichier JAR : `target/rest-benchmark-0.0.1-SNAPSHOT.jar`

### 3.2 Vérifier la compilation

```bash
# Vérifier que le JAR existe
# Windows PowerShell
Test-Path target\rest-benchmark-0.0.1-SNAPSHOT.jar
```

Devrait retourner `True`.

---

## 🗄️ Étape 4 : Préparer la base de données

La base de données PostgreSQL est déjà créée par Docker Compose avec :
- **Database** : `benchmark_db`
- **Username** : `benchmark_user`
- **Password** : `benchmark_pass`
- **Port** : `5432`

Les **tables seront créées automatiquement** par Hibernate au premier démarrage de l'application (grâce à `spring.jpa.hibernate.ddl-auto=update`).

---

## 🚀 Étape 5 : Premier démarrage de l'application

### 5.1 Démarrer avec la variante Spring (recommandé pour commencer)

**Option A : Avec script (Linux/Mac/Git Bash)** :
```bash
./start-variant.sh spring load-data
```

**Option B : Commande directe (Windows PowerShell)** :
```powershell
java -jar `
  -Dspring.profiles.active=spring,load-data `
  -Xms1g `
  -Xmx2g `
  -XX:+UseG1GC `
  target\rest-benchmark-0.0.1-SNAPSHOT.jar
```

Le paramètre `load-data` va automatiquement :
- ✅ Créer les tables (category, item)
- ✅ Insérer 2000 catégories
- ✅ Insérer ~100,000 items

### 5.2 Attendre le démarrage

Dans la console, attendez de voir :
```
Started RestBenchmarkApplication in X.XXX seconds
```

Cela prend environ 30-60 secondes avec le chargement des données.

### 5.3 Vérifier que l'application fonctionne

**Dans un NOUVEAU terminal** (laissez l'application tourner dans l'autre) :

```bash
# Health check
curl http://localhost:8080/actuator/health
```

Devrait retourner :
```json
{"status":"UP"}
```

**Tester les endpoints** :
```bash
# Lister les catégories (page 1, 10 résultats)
curl http://localhost:8080/api/spring/categories?page=0&size=10

# Lister les items (page 1, 10 résultats)
curl http://localhost:8080/api/spring/items?page=0&size=10

# Obtenir une catégorie spécifique
curl http://localhost:8080/api/spring/categories/1

# Obtenir un item spécifique
curl http://localhost:8080/api/spring/items/1
```

Si vous obtenez du JSON en retour, **tout fonctionne** ! ✅

---

## 📊 Étape 6 : Vérifier les données dans PostgreSQL

```bash
# Se connecter à PostgreSQL
docker exec -it benchmark_postgres psql -U benchmark_user -d benchmark_db
```

Dans PostgreSQL :
```sql
-- Voir les tables créées
\dt

-- Compter les catégories
SELECT COUNT(*) FROM category;
-- Devrait retourner : 2000

-- Compter les items
SELECT COUNT(*) FROM item;
-- Devrait retourner : ~100,000

-- Voir quelques exemples
SELECT * FROM category LIMIT 5;
SELECT * FROM item LIMIT 5;

-- Quitter PostgreSQL
\q
```

---

## 🎯 Étape 7 : Tester les 3 variantes

Maintenant que tout fonctionne, vous pouvez tester les 3 variantes :

### 7.1 Arrêter l'application actuelle
Dans le terminal où l'application tourne : `Ctrl + C`

### 7.2 Tester la variante Jersey (JAX-RS)

```powershell
# Sans recharger les données (elles sont déjà là)
java -jar `
  -Dspring.profiles.active=jersey `
  -Xms1g `
  -Xmx2g `
  target\rest-benchmark-0.0.1-SNAPSHOT.jar
```

Endpoints disponibles : `http://localhost:8080/api/jersey/*`

Test :
```bash
curl http://localhost:8080/api/jersey/categories?page=0&size=10
```

### 7.3 Tester la variante Spring Data REST

```powershell
java -jar `
  -Dspring.profiles.active=datarest `
  -Xms1g `
  -Xmx2g `
  target\rest-benchmark-0.0.1-SNAPSHOT.jar
```

Endpoints disponibles : `http://localhost:8080/api/datarest/*`

Test :
```bash
curl http://localhost:8080/api/datarest/categories?page=0&size=10
```

---

## 📈 Étape 8 : Vérifier les métriques

### 8.1 Prometheus

1. Ouvrir : http://localhost:9090
2. Dans la barre de recherche, essayer ces requêtes :
   ```promql
   # Nombre de requêtes HTTP
   http_server_requests_seconds_count
   
   # Mémoire JVM
   jvm_memory_used_bytes
   
   # CPU
   process_cpu_usage
   ```
3. Cliquer sur "Execute" puis "Graph"

### 8.2 Grafana

1. Ouvrir : http://localhost:3000 (admin/admin)
2. Aller dans "Connections" → "Data sources"
3. Vérifier que "Prometheus" est connecté (point vert)
4. Aller dans "Dashboards" → "Import"
5. Importer le fichier : `monitoring/grafana/dashboards/rest-benchmark-dashboard.json`
6. Vous devriez voir les graphiques avec les métriques en temps réel

---

## 🧪 Étape 9 : Générer les données de test JMeter

```bash
# Générer les fichiers CSV pour JMeter
python jmeter/generate-test-data.py
```

Cela crée :
- ✅ `jmeter/data/categories.csv` (2000 lignes)
- ✅ `jmeter/data/items.csv` (~100,000 lignes)
- ✅ `jmeter/data/category-payload-light.csv`
- ✅ `jmeter/data/item-payload-light.csv`
- ✅ `jmeter/data/item-payload-heavy.csv`

---

## ✅ Récapitulatif - Vous êtes prêt !

Si vous avez suivi toutes les étapes, vous avez maintenant :

✅ Infrastructure Docker running (PostgreSQL, Prometheus, Grafana, InfluxDB)  
✅ Application Java compilée  
✅ Base de données créée et peuplée (2000 catégories, ~100,000 items)  
✅ Les 3 variantes fonctionnelles (Jersey, Spring, DataREST)  
✅ Métriques disponibles dans Prometheus et Grafana  
✅ Données de test générées pour JMeter  

---

## 🎯 Prochaines étapes

### Pour utiliser l'application manuellement :
- Consulter le [README.md](README.md) pour la liste complète des endpoints
- Utiliser Postman ou curl pour tester les APIs

### Pour lancer les benchmarks :
1. Installer JMeter 5.6+
2. Consulter le [guide JMeter](jmeter/JMETER-SCENARIOS.md)
3. Créer les 4 scénarios de test dans JMeter GUI
4. Lancer les tests avec `run-single-test.sh` ou `run-all-benchmarks.sh`

### Pour analyser les résultats :
- Remplir les tableaux dans [TABLEAUX.md](TABLEAUX.md)
- Comparer les performances des 3 variantes
- Documenter vos conclusions

---

## 🐛 Dépannage

### Problème : Docker Compose ne démarre pas
```bash
# Vérifier les logs
docker-compose logs

# Redémarrer
docker-compose restart

# Nettoyer complètement (ATTENTION: supprime les données)
docker-compose down -v
docker-compose up -d
```

### Problème : Port 5432 déjà utilisé (PostgreSQL)
Un autre PostgreSQL tourne peut-être déjà. Options :
1. Arrêter l'autre PostgreSQL
2. Changer le port dans `docker-compose.yml` :
   ```yaml
   ports:
     - "5433:5432"  # Utiliser 5433 au lieu de 5432
   ```
   Et dans `application.properties` :
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5433/benchmark_db
   ```

### Problème : Application ne démarre pas
```bash
# Vérifier que PostgreSQL est accessible
docker-compose ps

# Vérifier les logs PostgreSQL
docker-compose logs postgres

# Tester la connexion
docker exec -it benchmark_postgres psql -U benchmark_user -d benchmark_db -c "SELECT 1"
```

### Problème : Maven ne télécharge pas les dépendances
```bash
# Nettoyer le cache Maven
mvn clean
rm -rf ~/.m2/repository  # Linux/Mac
Remove-Item -Recurse -Force $env:USERPROFILE\.m2\repository  # Windows PowerShell

# Retélécharger
mvn clean package -DskipTests -U
```

### Problème : Données non chargées
```bash
# Vérifier le profil actif
# Le profil 'load-data' doit être présent :
java -jar -Dspring.profiles.active=spring,load-data target\rest-benchmark-0.0.1-SNAPSHOT.jar

# Ou recharger manuellement via SQL :
docker exec -it benchmark_postgres psql -U benchmark_user -d benchmark_db -f scripts/setup-database.sql
```

---

## 📞 Besoin d'aide ?

Consultez :
- [README.md](README.md) - Documentation principale
- [QUICKSTART.md](QUICKSTART.md) - Guide rapide
- [TABLEAUX.md](TABLEAUX.md) - Tableaux de résultats à remplir
- [jmeter/README.md](jmeter/README.md) - Guide JMeter

---

## 🎉 C'est parti !

Tout est maintenant configuré et prêt pour le benchmark. Bon courage ! 🚀

