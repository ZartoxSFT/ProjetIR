# 📦 Projet Jakarta EE - FanfareHub

## 🧠 Contexte général

Projet Java Web avec :
- Servlets
- JSP
- DAO
- PostgreSQL

Objectif :
- gérer des utilisateurs (fanfarons)
- inscription / connexion
- événements (plus tard)
- architecture propre (MVC)

---

# 🧱 1. Modélisation

## 🎯 MCD

### Entités
- FANFARON
- INSTRUMENT (fusion pupitre)
- GROUPE_FANFARE
- EVENEMENT

### Associations
- FANFARON ↔ INSTRUMENT (N,N)
- FANFARON ↔ GROUPE (N,N)
- FANFARON ↔ EVENEMENT (organisation) N,N
- FANFARON ↔ EVENEMENT (participation) N,N

### INSCRIPTION (association enrichie)
- id_fanfaron
- id_evenement
- id_instrument
- statut

---

## 🧾 MLD (tables)

- fanfaron
- instrument
- groupe_fanfare
- evenement
- fanfaron_instrument
- fanfaron_groupe
- organisation_evenement
- inscription

---

## ⚠️ Corrections prof
- pas de table régime → champ direct
- 2 relations avec événement (organiser + participer)

---

# 💻 2. Base de données

- PostgreSQL
- snake_case
- mot de passe = hash
- contraintes CHECK (genre, contraintes_alimentaires)

---

# 🌐 3. Q2 - Formulaires

## ✔️ Inscription
- nom_fanfaron
- prenom
- nom
- email + confirmation
- password + confirmation
- genre
- contraintes_alimentaires

## ✔️ Connexion
- nom_fanfaron
- password

---

# 🧠 4. Architecture

## MVC

- Servlet = contrôleur
- DAO = accès données
- JSP = vue
- POJO = modèle

👉 Séparation des responsabilités

---

# 🧩 5. POJO Fanfaron

```java
package modele;

import java.sql.Timestamp;

public class Fanfaron {
    private Long id;
    private String nomFanfaron;
    private String prenom;
    private String nom;
    private String email;
    private String motDePasseHash;
    private String genre;
    private String contraintesAlimentaires;
    private Timestamp dateCreation;
    private Timestamp derniereConnexion;

    public Fanfaron() {}

    public Fanfaron(String nomFanfaron, String prenom, String nom, String email,
                    String motDePasseHash, String genre, String contraintesAlimentaires) {
        this.nomFanfaron = nomFanfaron;
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.motDePasseHash = motDePasseHash;
        this.genre = genre;
        this.contraintesAlimentaires = contraintesAlimentaires;
    }
}