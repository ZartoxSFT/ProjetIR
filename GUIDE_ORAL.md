# GUIDE DE PRÉSENTATION ORALE - FanfareHub

## 📌 Vue d'ensemble du projet

**Nom du projet** : FanfareHub
**Type** : Application Web Jakarta EE / Java Servlets
**Architecture** : MVC (Model-View-Controller)
**Base de données** : PostgreSQL
**Auteurs** : Ayoub Nadir, Amin Messaoudi

**Objectif** : Gérer une fanfare (orchestre de musiciens) avec inscription, gestion des instruments, des groupes et des événements.

---

## 🧱 Architecture générale

### Pattern MVC appliqué :

```
┌─────────────────────────────────────────────────────────────┐
│                    VUE (JSP)                                 │
│ connexion.jsp, inscription.jsp, accueil.jsp, etc.           │
└────────────────────────────────────────────────────────────┐
                     ↑ Affichage
                     ↓ Interaction utilisateur
┌─────────────────────────────────────────────────────────────┐
│                CONTRÔLEUR (Servlets)                         │
│ AccueilServlet, ConnexionServlet, AdminServlet, etc.        │
└────────────────────────────────────────────────────────────┐
                     ↑ Données traitées
                     ↓ Récupération données
┌─────────────────────────────────────────────────────────────┐
│        MODÈLE (DAO + Classes Métier)                         │
│ Accès base de données, opérations métier                    │
└─────────────────────────────────────────────────────────────┐
                     ↑ Connexion DB
                     ↓ Requêtes SQL
┌─────────────────────────────────────────────────────────────┐
│         BASE DE DONNÉES (PostgreSQL)                         │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔐 Authentification et Sécurité

### Processus de connexion :

1. **Affichage du formulaire** (`ConnexionServlet.doGet()`)
   - L'utilisateur voit la page connexion.jsp

2. **Soumission des identifiants** (`ConnexionServlet.doPost()`)
   - Récupération : nomFanfaron + motDePasse

3. **Hachage du mot de passe** (`hashPassword()`)
   - Algorithme : SHA-256
   - Encodage : Base64
   - Jamais stocker de mots de passe en clair !

4. **Vérification en base** (`FanfaronDAO.authenticate()`)
   - Requête SQL : `SELECT * FROM fanfaron WHERE nom_fanfaron = ? AND mot_de_passe = ?`

5. **Création de session** (`HttpSession`)
   - Stockage du fanfaron en session
   - Redirection vers /accueil

### Vérifications de sécurité appliquées partout :
```java
// Dans chaque servlet
HttpSession session = request.getSession(false);
if (session == null || session.getAttribute("fanfaron") == null) {
    response.sendRedirect(request.getContextPath() + "/connexion");
    return;
}
```

---

## 👤 Classes Métier (Modèles)

### **Fanfaron.java**
Représente un membre de la fanfare.

**Attributs principaux** :
- `id` : Identifiant unique
- `nomFanfaron` : Nom d'utilisateur (unique)
- `prenom`, `nom` : Identité civile
- `email` : Adresse unique
- `motDePasseHash` : Mot de passe hashé (SHA-256)
- `genre` : M ou F
- `contraintesAlimentaires` : Régimes, allergies
- `admin` : Booléen (true = administrateur)
- `dateCreation`, `derniereConnexion` : Métadonnées

### **Evenement.java**
Représente un événement (concert, répétition, etc.)

**Attributs** :
- `id` : Identifiant unique
- `nom` : Nom de l'événement
- `horodatage` : Date et heure
- `duree` : Durée en minutes
- `lieu` : Localisation
- `description` : Détails

### **Instrument.java**
Représente un instrument de musique.

**Attributs** :
- `id` : Identifiant unique
- `nom` : Nom (ex: "Trompette")

**Relation N:N avec Fanfaron** :
- Un fanfaron peut jouer plusieurs instruments
- Un instrument peut être joué par plusieurs fanfarons
- Table de liaison : `fanfaron_instrument`

### **GroupeFanfare.java**
Représente un groupe/section de la fanfare.

**Attributs** :
- `id` : Identifiant unique
- `nom` : Nom du groupe (ex: "Cuivres")

**Relation N:N avec Fanfaron** :
- Un fanfaron appartient à plusieurs groupes
- Un groupe contient plusieurs fanfarons
- Table de liaison : `fanfaron_groupe`

### **EvenementInscrit.java**
Vue enrichie : événement + données d'inscription.

**Attributs spécialisés** :
- Tous les attributs d'Événement (nom, date, lieu, etc.)
- `instrument` : L'instrument joué à cet événement
- `statut` : Participation (present, absent, incertain)

### **InscriptionDetail.java**
Vue enrichie : fanfaron + données d'inscription.

**Utilisée pour** : Afficher la liste des fanfarons inscrits à un événement.

---

## 🗄️ Pattern DAO (Data Access Object)

### Objectif :
- Isoler la logique d'accès à la base de données
- Faciliter les tests et la maintenance
- Permettre de changer d'implémentation

### Architecture DAO :

```
Interface DAO (contrat)
    ↓
Implémentation JDBC
    ↓
DbConnectionManager (Singleton)
    ↓
Base de données PostgreSQL
```

### Classes importantes :

#### **DAOFactory.java**
Pattern Factory pour créer les DAO.

```java
// Utilisation
FanfaronDAO dao = DAOFactory.getFanfaronDAO();
InstrumentDAO instrumentDao = DAOFactory.getInstrumentDAO();
```

**Avantages** :
- Point d'accès unique
- Découplage
- Facilite les changements

#### **DbConnectionManager.java**
Pattern Singleton pour la gestion des connexions.

```java
DbConnectionManager manager = DbConnectionManager.getInstance();
Connection conn = manager.getConnection();
```

**Caractéristiques** :
- Initialisation lazy (à la première utilisation)
- Thread-safe (synchronized)
- Charge les paramètres depuis `db.properties`
- Charge le driver PostgreSQL

#### **FanfaronJDBCDAO.java**
Implémentation des opérations CRUD sur les fanfarons.

**Méthodes principales** :
- `getById(int id)` : Récupère un fanfaron par ID
- `getByNomFanfaron(String nom)` : Récupère par nom d'utilisateur
- `authenticate(String nom, String hash)` : Vérifie les identifiants
- `existsByNomFanfaron()`, `existsByEmail()` : Vérifie les doublons
- `create()`, `update()`, `delete()` : Opérations CRUD

**Paramètres de sécurité** :
- Utilise `PreparedStatement` pour éviter les injections SQL
- Les paramètres sont bindés avec `?`

#### **InstrumentJDBCDAO.java**
Gère les instruments et les relations N:N.

**Particularité** : Gère les relations N:N
```java
// Récupère les instruments d'un fanfaron
List<Instrument> instruments = dao.findInstrumentsByFanfaron(id);

// Met à jour les instruments d'un fanfaron
dao.updateInstrumentsFanfaron(idFanfaron, arrayInstruments);
```

#### **EvenementJDBCDAO.java**
Gère les événements et les inscriptions.

```java
// Récupère les événements d'un fanfaron
List<EvenementInscrit> evenements = dao.getEvenementsInscritsByFanfaron(id);
```

---

## 🎮 Servlets (Contrôleurs)

### **ConnexionServlet**
- **URL** : `/connexion`
- **GET** : Affiche le formulaire de connexion
- **POST** : Vérifie les identifiants et crée la session
- **Sécurité** : Hachage SHA-256 du mot de passe

### **InscriptionServlet**
- **URL** : `/inscription`
- **GET** : Affiche le formulaire d'inscription
- **POST** : Crée un nouveau fanfaron après validation
- **Validations** :
  - Champs obligatoires
  - Correspondance emails/mots de passe
  - Vérification des doublons (email, nomFanfaron)

### **DeconnexionServlet**
- **URL** : `/deconnexion`
- **GET** : Invalide la session et redirige vers la connexion

### **AccueilServlet**
- **URL** : `/accueil`
- **GET** : Affiche le tableau de bord
- **Données chargées** :
  - Profil du fanfaron connecté
  - Instruments qu'il joue
  - Groupes auxquels il appartient
  - Événements auxquels il s'est inscrit
- **Sécurité** : Vérification de session obligatoire

### **MesGroupesServlet**
- **URL** : `/mes-groupes`
- **GET** : Affiche les instruments et groupes disponibles
- **POST** : Met à jour les sélections du fanfaron
- **Particularité** : Gère les relations N:N

### **EvenementServlet**
- **URL** : `/evenement`
- **GET** : Affiche les événements et les inscriptions
- **POST** : Gère les inscriptions aux événements
- **Statuts** : present, absent, incertain

### **AdminServlet**
- **URL** : `/admin`
- **Sécurité** : Réservé aux administrateurs
- **GET** : Affiche la liste de tous les fanfarons
- **POST** : Ajoute/modifie/supprime des fanfarons
- **Actions** :
  - Créer un nouveau fanfaron
  - Modifier un fanfaron
  - Supprimer un fanfaron
  - Gérer le statut admin

---

## 🔄 Flux de requête typique

### Exemple : Inscription d'un nouveau fanfaron

```
1. Utilisateur → GET /inscription
   ↓
2. InscriptionServlet.doGet()
   ↓ Forward
3. inscription.jsp (affiche formulaire)
   ↓
4. Utilisateur remplit et soumet → POST /inscription
   ↓
5. InscriptionServlet.doPost()
   ├─ Récupération des paramètres
   ├─ Validation des données
   ├─ Hachage du mot de passe
   ├─ Vérification des doublons (appel DAO)
   └─ Insertion en base (appel DAO)
      ↓
6. FanfaronJDBCDAO.addFanfaron()
   ├─ Création de PreparedStatement
   ├─ Binding des paramètres
   ├─ Exécution INSERT
   └─ Retour du résultat
      ↓
7. InscriptionServlet reçoit résultat
   ├─ Si succès : message de succès
   └─ Si erreur : message d'erreur
      ↓
8. inscription.jsp (affichage du résultat)
```

---

## 🗄️ Base de données : Tables principales

### **fanfaron**
```sql
CREATE TABLE fanfaron (
    id SERIAL PRIMARY KEY,
    nom_fanfaron VARCHAR(50) UNIQUE NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    nom VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(100) NOT NULL,
    genre VARCHAR(1),
    contraintes_alimentaires TEXT,
    admin BOOLEAN DEFAULT false,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    derniere_connexion TIMESTAMP
);
```

### **instrument**
```sql
CREATE TABLE instrument (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL
);
```

### **groupe_fanfare**
```sql
CREATE TABLE groupe_fanfare (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL
);
```

### **evenement**
```sql
CREATE TABLE evenement (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    horodatage TIMESTAMP NOT NULL,
    duree INTEGER,
    lieu VARCHAR(100),
    description TEXT
);
```

### Relations N:N

**fanfaron_instrument** (liaison instruments/fanfaron)
**fanfaron_groupe** (liaison groupes/fanfaron)
**inscription** (participation aux événements avec statut)

---

## 💡 Points clés à mémoriser pour l'oral

### ✅ Architecture
- Pattern MVC bien appliqué
- Séparation claire des responsabilités
- Factory et Singleton pattern utilisés
- DAO pour l'isolation de la base de données

### ✅ Sécurité
- Authentification par session
- Hachage SHA-256 des mots de passe
- PreparedStatement contre injections SQL
- Vérification des droits (admin check)
- Validation serveur des données

### ✅ Gestion des données
- Relations N:N correctement implémentées
- Transactions SQL cohérentes
- Gestion d'erreurs appropriée

### ✅ Points de discussion possibles
1. Pourquoi utiliser des DAO ? → Séparation, testabilité, maintenabilité
2. Comment fonctionne l'authentification ? → Hash, session
3. Comment gérer les relations N:N ? → Tables de liaison
4. Quels patterns avez-vous utilisés ? → Factory, Singleton, MVC
5. Comment éviter les injections SQL ? → PreparedStatement
6. Comment gérer la sécurité ? → Hachage, vérification session, droits

---

## 📝 Fichiers commentés

Tous les fichiers Java du projet ont été commentés :

### Servlets (Contrôleurs)
✅ AccueilServlet.java
✅ ConnexionServlet.java
✅ InscriptionServlet.java
✅ AdminServlet.java
✅ DeconnexionServlet.java

### DAO et Accès données
✅ DAOFactory.java
✅ DbConnectionManager.java
✅ FanfaronDAO.java (interface)
✅ InstrumentDAO.java (interface)
✅ EvenementDAO.java (interface)

### Modèles (POJO)
✅ Fanfaron.java
✅ Evenement.java
✅ Instrument.java
✅ GroupeFanfare.java
✅ EvenementInscrit.java
✅ InscriptionDetail.java

---

## 🎯 Avant votre présentation

1. **Lisez le code** : Tous les fichiers ont des commentaires détaillés
2. **Tracez un flux** : Suivez un cas d'usage du début à la fin
3. **Comprenez les patterns** : Factory, Singleton, MVC, DAO
4. **Mémoriser les tables** : Fanfaron, Instrument, Groupe, Événement
5. **Pratiquez votre explication** : Commencez simple, profondeur progressive
6. **Préparez des diagrammes** : UML, flux de requête, architecture

Bonne présentation ! 🎉
