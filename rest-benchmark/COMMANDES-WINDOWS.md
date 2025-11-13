# Commandes Windows PowerShell - REST API Benchmark

Guide avec toutes les commandes adaptées pour Windows PowerShell.

## 📋 Configuration initiale

### 1. Se placer dans le projet
```powershell
cd C:\Users\Microsoft\Desktop\rest-benchmark
```

### 2. Créer les dossiers nécessaires
```powershell
New-Item -ItemType Directory -Force -Path jmeter\data
New-Item -ItemType Directory -Force -Path jmeter\results
New-Item -ItemType Directory -Force -Path scripts
```

---

## 🐳 Docker

### Démarrer l'infrastructure
```powershell
docker-compose up -d
```

### Vérifier les conteneurs
```powershell
docker-compose ps
```

### Voir les logs
```powershell
# Tous les logs
docker-compose logs -f

# Logs d'un service spécifique
docker-compose logs -f postgres
docker-compose logs -f prometheus
docker-compose logs -f grafana
docker-compose logs -f influxdb
```

### Redémarrer un service
```powershell
docker-compose restart postgres
```

### Arrêter tout
```powershell
docker-compose down
```

### Arrêter et supprimer les volumes (données)
```powershell
docker-compose down -v
```

---

## 🔨 Maven

### Compiler le projet
```powershell
mvn clean package -DskipTests
```

### Compiler avec tous les threads (plus rapide)
```powershell
mvn clean package -DskipTests -T 4
```

### Nettoyer
```powershell
mvn clean
```

### Voir les dépendances
```powershell
mvn dependency:tree
```

---

## 🚀 Démarrer l'application

### Variante Spring @RestController (avec chargement des données)
```powershell
java -jar `
  -Dspring.profiles.active=spring,load-data `
  -Xms1g `
  -Xmx2g `
  -XX:+UseG1GC `
  target\rest-benchmark-0.0.1-SNAPSHOT.jar
```

### Variante Spring (sans charger les données)
```powershell
java -jar `
  -Dspring.profiles.active=spring `
  -Xms1g `
  -Xmx2g `
  target\rest-benchmark-0.0.1-SNAPSHOT.jar
```

### Variante Jersey (JAX-RS)
```powershell
java -jar `
  -Dspring.profiles.active=jersey `
  -Xms1g `
  -Xmx2g `
  target\rest-benchmark-0.0.1-SNAPSHOT.jar
```

### Variante Spring Data REST
```powershell
java -jar `
  -Dspring.profiles.active=datarest `
  -Xms1g `
  -Xmx2g `
  target\rest-benchmark-0.0.1-SNAPSHOT.jar
```

### Arrêter l'application
```
Ctrl + C
```

---

## 🧪 Tester les endpoints

### Health check
```powershell
curl http://localhost:8080/actuator/health
```

### Métriques Prometheus
```powershell
curl http://localhost:8080/actuator/prometheus
```

### Lister les catégories
```powershell
# Spring
curl http://localhost:8080/api/spring/categories?page=0&size=10

# Jersey
curl http://localhost:8080/api/jersey/categories?page=0&size=10

# Spring Data REST
curl http://localhost:8080/api/datarest/categories?page=0&size=10
```

### Lister les items
```powershell
curl http://localhost:8080/api/spring/items?page=0&size=10
```

### Créer une catégorie
```powershell
$body = @{
    code = "TEST01"
    name = "Test Category"
} | ConvertTo-Json

Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8080/api/spring/categories" `
  -ContentType "application/json" `
  -Body $body
```

### Créer un item
```powershell
$body = @{
    name = "Test Item"
    price = 99.99
    stock = 10
    categoryId = 1
} | ConvertTo-Json

Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8080/api/spring/items" `
  -ContentType "application/json" `
  -Body $body
```

### Mettre à jour une catégorie
```powershell
$body = @{
    code = "TEST01"
    name = "Updated Category"
} | ConvertTo-Json

Invoke-RestMethod -Method Put `
  -Uri "http://localhost:8080/api/spring/categories/1" `
  -ContentType "application/json" `
  -Body $body
```

### Supprimer un item
```powershell
Invoke-RestMethod -Method Delete `
  -Uri "http://localhost:8080/api/spring/items/1"
```

---

## 🗄️ PostgreSQL

### Se connecter à la base de données
```powershell
docker exec -it benchmark_postgres psql -U benchmark_user -d benchmark_db
```

### Commandes SQL dans PostgreSQL
```sql
-- Voir les tables
\dt

-- Compter les catégories
SELECT COUNT(*) FROM category;

-- Compter les items
SELECT COUNT(*) FROM item;

-- Voir des exemples
SELECT * FROM category LIMIT 10;
SELECT * FROM item LIMIT 10;

-- Voir la structure d'une table
\d category
\d item

-- Quitter
\q
```

### Exécuter un fichier SQL
```powershell
docker exec -i benchmark_postgres psql -U benchmark_user -d benchmark_db < scripts\setup-database.sql
```

### Backup de la base
```powershell
docker exec -t benchmark_postgres pg_dump -U benchmark_user benchmark_db > backup.sql
```

### Restore de la base
```powershell
Get-Content backup.sql | docker exec -i benchmark_postgres psql -U benchmark_user -d benchmark_db
```

---

## 🐍 Python - Génération des données de test

### Générer les fichiers CSV
```powershell
python jmeter\generate-test-data.py
```

Ou si vous avez Python 3 spécifiquement :
```powershell
python3 jmeter\generate-test-data.py
```

### Vérifier que les fichiers ont été créés
```powershell
Get-ChildItem jmeter\data\*.csv
```

Devrait afficher :
- categories.csv
- items.csv
- category-payload-light.csv
- item-payload-light.csv
- item-payload-heavy.csv

---

## 📊 JMeter

### Lancer JMeter GUI
```powershell
# Si JMeter est dans le PATH
jmeter

# Sinon, chemin complet
C:\apache-jmeter\bin\jmeter.bat
```

### Lancer un test en mode non-GUI
```powershell
jmeter -n `
  -t jmeter\scenario-1-read-heavy.jmx `
  -l jmeter\results\test-result.jtl `
  -e -o jmeter\results\report `
  -JAPI_PATH=/api/spring `
  -JVARIANT=spring
```

### Ouvrir un rapport HTML
```powershell
Start-Process jmeter\results\report\index.html
```

---

## 📈 Surveillance

### Ouvrir Prometheus
```powershell
Start-Process http://localhost:9090
```

### Ouvrir Grafana
```powershell
Start-Process http://localhost:3000
```

### Ouvrir InfluxDB
```powershell
Start-Process http://localhost:8086
```

---

## 🔍 Diagnostics

### Vérifier que Java est installé
```powershell
java -version
```

### Vérifier que Maven est installé
```powershell
mvn -version
```

### Vérifier que Docker fonctionne
```powershell
docker --version
docker-compose --version
docker ps
```

### Vérifier que Python est installé
```powershell
python --version
```

### Vérifier les ports utilisés
```powershell
# PostgreSQL (5432)
Test-NetConnection -ComputerName localhost -Port 5432

# Application Spring Boot (8080)
Test-NetConnection -ComputerName localhost -Port 8080

# Prometheus (9090)
Test-NetConnection -ComputerName localhost -Port 9090

# Grafana (3000)
Test-NetConnection -ComputerName localhost -Port 3000
```

### Trouver quel processus utilise un port
```powershell
# Exemple pour le port 8080
Get-NetTCPConnection -LocalPort 8080 | Select-Object -Property LocalPort, OwningProcess

# Voir le nom du processus
Get-Process -Id <PID>
```

### Tuer un processus sur un port
```powershell
# Trouver le PID
$pid = (Get-NetTCPConnection -LocalPort 8080).OwningProcess
# Tuer le processus
Stop-Process -Id $pid -Force
```

---

## 📁 Gestion des fichiers

### Voir la structure du projet
```powershell
tree /F
```

### Compter les lignes d'un fichier CSV
```powershell
(Get-Content jmeter\data\categories.csv).Count
(Get-Content jmeter\data\items.csv).Count
```

### Voir les 10 premières lignes d'un CSV
```powershell
Get-Content jmeter\data\categories.csv -Head 10
```

### Voir la taille des fichiers
```powershell
Get-ChildItem jmeter\data\*.csv | Format-Table Name, @{Name="Size (MB)"; Expression={[math]::Round($_.Length/1MB, 2)}}
```

### Nettoyer les résultats JMeter
```powershell
Remove-Item jmeter\results\* -Recurse -Force
```

---

## 🛠️ Scripts utiles

### Script pour démarrer tout (créer un fichier start-all.ps1)
```powershell
# start-all.ps1
Write-Host "Démarrage de l'infrastructure..." -ForegroundColor Green
docker-compose up -d

Write-Host "`nAttente du démarrage de PostgreSQL..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host "`nDémarrage de l'application (variante Spring)..." -ForegroundColor Green
java -jar `
  -Dspring.profiles.active=spring,load-data `
  -Xms1g `
  -Xmx2g `
  target\rest-benchmark-0.0.1-SNAPSHOT.jar
```

### Exécuter le script
```powershell
.\start-all.ps1
```

### Script pour tout arrêter (créer un fichier stop-all.ps1)
```powershell
# stop-all.ps1
Write-Host "Arrêt de l'infrastructure..." -ForegroundColor Yellow
docker-compose down

Write-Host "`nTout est arrêté." -ForegroundColor Green
```

---

## 🎯 Workflow complet

### Setup initial (une seule fois)
```powershell
# 1. Se placer dans le projet
cd C:\Users\Microsoft\Desktop\rest-benchmark

# 2. Créer les dossiers
New-Item -ItemType Directory -Force -Path jmeter\data, jmeter\results

# 3. Compiler
mvn clean package -DskipTests

# 4. Démarrer Docker
docker-compose up -d

# 5. Générer les données de test
python jmeter\generate-test-data.py
```

### Démarrage quotidien
```powershell
# 1. Démarrer Docker (si pas déjà fait)
docker-compose up -d

# 2. Démarrer l'application
java -jar `
  -Dspring.profiles.active=spring `
  -Xms1g `
  -Xmx2g `
  target\rest-benchmark-0.0.1-SNAPSHOT.jar
```

### Arrêt
```powershell
# 1. Arrêter l'application
# Ctrl + C dans le terminal

# 2. Arrêter Docker (optionnel)
docker-compose down
```

---

## 🔄 Changer de variante

```powershell
# 1. Arrêter l'application actuelle (Ctrl + C)

# 2. Démarrer une autre variante
# Jersey
java -jar -Dspring.profiles.active=jersey -Xms1g -Xmx2g target\rest-benchmark-0.0.1-SNAPSHOT.jar

# Spring
java -jar -Dspring.profiles.active=spring -Xms1g -Xmx2g target\rest-benchmark-0.0.1-SNAPSHOT.jar

# Spring Data REST
java -jar -Dspring.profiles.active=datarest -Xms1g -Xmx2g target\rest-benchmark-0.0.1-SNAPSHOT.jar
```

---

## 💡 Astuces PowerShell

### Créer un alias pour les commandes longues
```powershell
# Dans votre profil PowerShell ($PROFILE)
function Start-SpringBoot {
    java -jar -Dspring.profiles.active=spring -Xms1g -Xmx2g target\rest-benchmark-0.0.1-SNAPSHOT.jar
}

# Utilisation
Start-SpringBoot
```

### Surveiller les logs en temps réel
```powershell
# Docker logs
docker-compose logs -f --tail=100

# Logs d'un service spécifique
docker-compose logs -f --tail=50 postgres
```

---

Bon benchmark ! 🚀

