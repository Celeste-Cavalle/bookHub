# BookHub Backend

Backend de l'application **BookHub**, développé avec **Java** et **Spring Boot**. Cette API REST gère l'ensemble des fonctionnalités métier de la plateforme : gestion des utilisateurs, authentification, catalogue de livres, emprunts, réservations, avis et conformité RGPD.

---

## Fonctionnalités principales

* Authentification sécurisée avec JWT
* Gestion des utilisateurs et des rôles
* Gestion du catalogue de livres
* Emprunt et retour de livres
* Réservation de livres
* Notes et commentaires
* Gestion des auteurs et catégories
* Conformité RGPD (consultation, modification et suppression des données personnelles)
* API REST documentée

---

## Technologies utilisées

* Java 21
* Spring Boot 3
* Spring Security
* Spring Data JPA
* Hibernate
* SQL Server
* Gradle
* JWT (JSON Web Token)
* Lombok
* MapStruct
* Swagger / OpenAPI

---

## Architecture du projet

```text
src/main/java/
├── config/          # Configuration Spring (Security, JWT, CORS...)
├── controller/      # Contrôleurs REST
├── dto/             # Objets de transfert de données
├── entity/          # Entités JPA
├── exception/       # Gestion centralisée des exceptions
├── mapper/          # Conversion Entity <-> DTO
├── repository/      # Accès aux données
├── security/        # JWT, filtres, services de sécurité
├── service/         # Logique métier
└── BookHubApplication.java
```

---

## Prérequis

Avant de lancer le projet, assurez-vous d'avoir installé :

* Java 21
* Gradle 9.4.1
* SQL Server 
* Git

---

## Installation

### 1. Cloner le dépôt

```bash
git clone <repository-url>
cd bookhub-backend
```

### 2. Créer la base de données

```sql
CREATE DATABASE IF NOT EXISTS bookhubjpa;
```

### 3. Configurer la connexion à la base de données

Modifiez le fichier `src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bookhub_jpa
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
```

### 4. Installer les dépendances et lancer l'application

```bash
mvn clean install
mvn spring-boot:run
```

L'application sera accessible à l'adresse suivante :

```text
http://localhost:8080
```

---

## Authentification

L'API utilise une authentification basée sur **JWT**.

### Processus

1. Inscription ou connexion de l'utilisateur
2. Génération d'un token JWT
3. Envoi du token dans l'en-tête de chaque requête protégée

```http
Authorization: Bearer <your_token>
```

---

## Documentation de l'API

Une fois l'application démarrée, la documentation Swagger est disponible à l'adresse :

```text
http://localhost:8080/swagger-ui/index.html
```

---

## Tests

Lancer les tests avec IntelliJ:

```bash
./gradlew test
```
Sur Windows :
```bash
gradlew.bat test
---

## Déploiement

Génération du fichier JAR :

```bash
mvn clean package
```

Exécution :

```bash
java -jar target/bookhub-backend.jar
```

---

## Auteur
 
Projet développé dans le cadre d'un projet p de l'ENI de la plateforme BookHub.
Projet Réaliser par Celeste CAVALLIN, Sebastien LALOUE et Audrey PEHUET

---

## Licence

Ce projet est développé à des fins pédagogiques et associatives.
