# FanfareHub

## Auteurs

Projet realise par :

- Ayoub Nadir
- Amin Messaoudi

## Presentation

FanfareHub est une application web Java Jakarta EE permettant la gestion d'une fanfare.

L'application permet :

- l'inscription, la connexion et la deconnexion des utilisateurs ;
- la gestion des comptes fanfarons ;
- le choix des instruments joues par chaque fanfaron ;
- le choix des groupes auxquels appartient chaque fanfaron ;
- la gestion des evenements ;
- la gestion des inscriptions aux evenements ;
- l'administration des utilisateurs, instruments et groupes ;
- la creation, modification et suppression d'evenements par les administrateurs ou les membres de la commission prestation.

Le projet suit une architecture MVC :

- les JSP gerent l'affichage ;
- les Servlets gerent la logique de controle ;
- les DAO gerent l'acces aux donnees ;
- les POJO representent les entites metier.

Remarque importante : le projet ne suit pas une correspondance stricte `1 DAO = 1 POJO`.
Certains DAO sont regroupes par fonctionnalite, par exemple `InstrumentDAO` gere les instruments, les groupes et leurs associations avec les fanfarons.

## Architecture MVC

### Vues JSP

Les vues principales sont situees dans le dossier `vue/` :

- `connexion.jsp`
- `inscription.jsp`
- `accueil.jsp`
- `mes-groupes.jsp`
- `evenement.jsp`
- `admin.jsp`

### Controleurs

Les controleurs sont des Servlets situees dans `WEB-INF/classes/controleur/` :

- `ConnexionServlet`
- `InscriptionServlet`
- `DeconnexionServlet`
- `AccueilServlet`
- `MesGroupesServlet`
- `EvenementServlet`
- `AdminServlet`

### Modele

Les classes metier sont situees dans `WEB-INF/classes/modele/` :

- `Fanfaron`
- `Instrument`
- `GroupeFanfare`
- `Evenement`
- `EvenementInscrit`
- `InscriptionDetail`

`EvenementInscrit` et `InscriptionDetail` sont des objets enrichis utilises pour l'affichage.
Ils ne correspondent pas directement a une table unique : ils regroupent des donnees provenant de plusieurs tables.

## Architecture DAO amelioree

Le projet utilise le patron DAO ameliore vu en cours.

L'objectif est de separer :

- la logique applicative ;
- la logique d'acces aux donnees ;
- la configuration de connexion a la base.

### Interfaces DAO

Les interfaces DAO definissent les contrats :

- `FanfaronDAO`
- `InstrumentDAO`
- `EvenementDAO`
- `EvenementInscriptionDAO`

### Implementations JDBC

Les implementations JDBC contiennent les requetes SQL :

- `FanfaronJDBCDAO`
- `InstrumentJDBCDAO`
- `EvenementJDBCDAO`
- `EvenementInscriptionJDBCDAO`

Le nombre de DAO est donc different du nombre de POJO, car les DAO representent les acces aux donnees par domaine fonctionnel.

### DAOFactory

La creation des DAO est centralisee dans `DAOFactory`.

Exemple :

```java
InstrumentDAO dao = DAOFactory.getInstrumentDAO();
```

Cela evite aux Servlets d'instancier directement les implementations JDBC.

### DbConnectionManager

La connexion a la base est geree par un singleton :

```java
DbConnectionManager
```

Cela permet :

- la centralisation des parametres de connexion ;
- la reduction du couplage ;
- une architecture plus maintenable.

## Base de donnees

Le script SQL principal est situe dans :

```text
WEB-INF/sql/script.sql
```

### Tables principales

#### `fanfaron`

Contient les utilisateurs.

#### `instrument`

Contient les instruments.

#### `groupe_fanfare`

Contient les groupes de fanfare.

#### `evenement`

Contient les evenements.
Les informations gerees sont le nom de l'evenement, la date, la duree, le lieu et la description.
Dans l'application actuelle, le nom de l'evenement est choisi dans une liste : `atelier`, `repetition` ou `prestation`.
La base applique aussi cette liste avec une contrainte `CHECK` sur la colonne `nom`.

### Tables d'association

#### `fanfaron_instrument`

Relation many-to-many :

```text
fanfaron <-> instrument
```

Un fanfaron peut jouer plusieurs instruments.

Un instrument peut etre joue par plusieurs fanfarons.

#### `fanfaron_groupe`

Relation many-to-many :

```text
fanfaron <-> groupe_fanfare
```

Un fanfaron peut appartenir a plusieurs groupes.

Un groupe peut contenir plusieurs fanfarons.

#### `organisation_evenement`

Relation entre les fanfarons organisateurs et les evenements.

#### `inscription`

Relation entre les fanfarons et les evenements.

Elle stocke aussi :

- l'instrument choisi ;
- le statut de participation (`present`, `absent`, `incertain`).

## Securite

Le projet implemente plusieurs mecanismes de securite :

- utilisation de `PreparedStatement` ;
- hash des mots de passe avec SHA-256 ;
- validation des formulaires ;
- gestion des sessions HTTP ;
- protection des pages administrateur ;
- verification des doublons email / pseudo ;
- controle des actions reservees aux administrateurs ;
- interdiction pour un administrateur de supprimer son propre compte ;
- controle des actions reservees a la commission prestation pour les evenements ;
- controle de l'annulation des inscriptions aux evenements.

## Repartition des roles

### Ayoub Nadir

- conception de l'architecture MVC ;
- mise en place du patron DAO ameliore ;
- implementation de `DAOFactory` ;
- implementation du singleton `DbConnectionManager` ;
- developpement des DAO JDBC ;
- gestion des sessions et authentification ;
- gestion des relations many-to-many ;
- logique metier des Servlets ;
- securisation des formulaires et acces ;
- gestion des evenements et des inscriptions.

### Amin Messaoudi

- developpement des interfaces JSP ;
- integration HTML/CSS ;
- ergonomie et affichage dynamique ;
- integration des formulaires ;
- gestion des vues administrateur ;
- amelioration de l'experience utilisateur ;
- integration des messages d'erreur et de succes ;
- vues de connexion, inscription, accueil, groupes, evenements et administration.

## Execution du projet

### 1. Prerequis

- Java JDK 21+
- Apache Tomcat 11
- PostgreSQL
- Driver PostgreSQL JDBC

### 2. Creation de la base PostgreSQL

Creer une base :

```sql
CREATE DATABASE projet;
```

Puis executer le script :

```text
WEB-INF/sql/script.sql
```

Une fois le script importe dans PostgreSQL, un compte administrateur est cree automatiquement.
Il permet de se connecter directement a la plateforme avec les identifiants suivants :

```text
Login : admin
Mot de passe : admin
```

### 3. Fichier `db.properties`

Creer le fichier :

```text
WEB-INF/classes/db.properties
```

Contenu :

```properties
db.url=jdbc:postgresql://localhost:5432/projet
db.user=login
db.password=motdepasse
```

Dans l'environnement de developpement actuel, la base configuree dans `db.properties` est :

```properties
db.url=jdbc:postgresql://localhost:5432/projet
```

Un fichier exemple peut etre fourni avec le projet :

```text
WEB-INF/classes/db_exemple.properties
```

### 4. Deploiement Tomcat

Placer le projet dans :

```text
Tomcat/webapps/FanfareHub
```

Compiler les classes Java puis demarrer Tomcat.

Acces :

```text
http://localhost:8080/FanfareHub
```

Dans l'environnement de developpement actuel, le dossier peut aussi etre deploye sous le nom :

```text
ProjetIR
```

Acces correspondant :

```text
http://localhost:8080/ProjetIR
```

## Fonctionnalites principales

### Utilisateur

- inscription ;
- connexion ;
- deconnexion ;
- consultation du tableau de bord d'accueil ;
- choix des instruments ;
- choix des groupes ;
- inscription aux evenements ;
- annulation de sa propre inscription a un evenement.

### Administrateur

- gestion des comptes ;
- impossibilite de supprimer son propre compte administrateur ;
- ajout, suppression et modification des instruments ;
- ajout, suppression et modification des groupes ;
- gestion des evenements ;
- suppression d'inscriptions aux evenements ;
- acces a la page d'administration.

### Commission prestation

- proposition d'evenements ;
- modification et suppression d'evenements ;
- consultation des inscriptions aux evenements.

## Concepts techniques utilises

- MVC ;
- DAO ;
- DAO Factory ;
- Singleton ;
- JDBC ;
- JSP ;
- Servlets Jakarta EE ;
- sessions HTTP ;
- `PreparedStatement` ;
- relations many-to-many ;
- validation de formulaires ;
- messages d'erreur et de succes.

## Conclusion

Le projet met en oeuvre une architecture web Java complete et modulaire permettant :

- une bonne separation des responsabilites ;
- une architecture maintenable ;
- une couche d'acces aux donnees evolutive ;
- une gestion securisee des utilisateurs et des donnees.

L'utilisation du patron DAO ameliore permet de respecter les principes vus en cours tout en facilitant l'evolution future du projet.
