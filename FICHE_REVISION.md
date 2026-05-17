# FICHE RÉVISION - POINTS CLÉS POUR L'ORAL

## 🎯 Les 5 concepts fondamentaux à maîtriser

### 1️⃣ ARCHITECTURE MVC
```
REQUÊTE HTTP
    ↓
SERVLET (Contrôleur)
    ├─ Récupère les données de la requête
    ├─ Appelle les DAO/métier
    └─ Transmet les résultats à la JSP
    ↓
JSP (Vue)
    └─ Affiche les données
```

**Points à retenir** :
- Séparation des responsabilités
- Les servlets ne font JAMAIS le rendu HTML
- Forward/Redirect utilisés correctement
- Les modèles (POJO) sont indépendants

---

### 2️⃣ AUTHENTIFICATION ET SÉCURITÉ

**Processus d'authentification** :
1. Utilisateur rentre : nomFanfaron + motDePasse
2. **Hachage** du mot de passe : SHA-256 + Base64
3. **Requête BD** : SELECT * FROM fanfaron WHERE nom_fanfaron=? AND mot_de_passe=hash
4. **Création session** : httpSession.setAttribute("fanfaron", fanfaron)
5. **Redirection** : vers /accueil

**Sécurité appliquée** :
- ✅ Jamais de mot de passe en clair
- ✅ PreparedStatement contre injections SQL
- ✅ Vérification de session sur chaque page
- ✅ SHA-256 pour le hachage

```java
// À connaître par cœur
String hash = Base64.getEncoder().encodeToString(
    MessageDigest.getInstance("SHA-256")
               .digest(password.getBytes(StandardCharsets.UTF_8))
);
```

---

### 3️⃣ PATTERNS DE CONCEPTION UTILISÉS

#### A. Pattern DAO (Data Access Object)
**Pourquoi** : Isoler la logique d'accès à la base
```
Interface DAO ← Contrat
    ↓
Implémentation JDBC
    ↓
Base de données
```

**Avantages** :
- Découplage
- Testabilité
- Maintenabilité
- Possibilité de changer d'implémentation

#### B. Pattern Factory
**Pourquoi** : Centraliser la création des DAO
```java
FanfaronDAO dao = DAOFactory.getFanfaronDAO();
// Au lieu de : new FanfaronJDBCDAO(manager)
```

**Avantages** :
- Un seul point de création
- Changement facile d'implémentation
- Injection de dépendances simplifiée

#### C. Pattern Singleton
**DbConnectionManager** : Une seule instance en mémoire
```java
DbConnectionManager manager = DbConnectionManager.getInstance();
// Toujours la même instance
```

**Avantages** :
- Pas de duplication de ressources
- Gestion centralisée des connexions
- Thread-safe (synchronized)

---

### 4️⃣ RELATIONS N:N DANS LA BASE DE DONNÉES

**Fanfaron ↔ Instrument**
```
fanfaron (1) ←─── fanfaron_instrument ───→ (N) instrument
```

Un fanfaron peut jouer plusieurs instruments
Un instrument peut être joué par plusieurs fanfarons

**Exemple de requête** :
```sql
SELECT instrument.* 
FROM instrument
JOIN fanfaron_instrument ON instrument.id = fanfaron_instrument.id_instrument
WHERE fanfaron_instrument.id_fanfaron = ?;
```

**Gestion en Java** :
```java
// Récupérer
List<Instrument> instruments = dao.findInstrumentsByFanfaron(id);

// Mettre à jour
dao.updateInstrumentsFanfaron(idFanfaron, arrayInstruments);
// Supprime les anciens liens, ajoute les nouveaux
```

---

### 5️⃣ FLOW D'UNE REQUÊTE COMPLÈTE

**Scénario : Inscription d'un fanfaron**

```
1. GET /inscription
   └─ InscriptionServlet.doGet()
      └─ forward → inscription.jsp

2. Utilisateur remplit le formulaire

3. POST /inscription avec les données
   └─ InscriptionServlet.doPost()
      ├─ Récupération des paramètres
      ├─ Validation (champs, format, doublons)
      ├─ Hachage du mot de passe
      └─ dao.addFanfaron(fanfaron)

4. FanfaronJDBCDAO.addFanfaron()
   ├─ INSERT INTO fanfaron VALUES (?)
   └─ PreparedStatement pour éviter les injections

5. Résultat retourné à la servlet
   ├─ Succès : Message OK
   └─ Erreur : Message d'erreur + réaffichage du formulaire

6. Affichage du résultat
   └─ forward → inscription.jsp (avec message)
```

---

## 📊 Les 4 couches du projet

### Couche PRÉSENTATION (Vue)
**Fichiers** : `*.jsp` dans le dossier `vue/`
- Affichage uniquement
- Logique métier NULLE
- Accès aux données via ${} (attributes)

### Couche CONTRÔLE (Servlets)
**Fichiers** : `*Servlet.java`
- Récupération des paramètres
- Appel au métier/DAO
- Gestion de la session
- Forward/Redirect

### Couche MÉTIER (DAO)
**Fichiers** : `*DAO.java`, `*JDBCDAO.java`
- Requêtes SQL
- Transformation données ↔ objets
- Transactions
- Gestion des erreurs

### Couche DONNÉES (Database)
**PostgreSQL**
- Tables : fanfaron, instrument, groupe_fanfare, evenement, etc.
- Intégrité des données
- Contraintes CHECK, UNIQUE

---

## 🔑 Les 10 méthodes à connaître

### Servlets
1. `doGet()` - Afficher une page
2. `doPost()` - Traiter un formulaire
3. `getAttribute()` / `setAttribute()` - Transmettre des données
4. `getRequestDispatcher().forward()` - Forward interne
5. `sendRedirect()` - Redirection externe

### DAO
6. `authenticate()` - Vérifier les identifiants
7. `create()` / `insert()` - Créer
8. `update()` - Modifier
9. `delete()` - Supprimer
10. `getAll()` / `findAll()` - Récupérer tous

---

## ⚠️ Erreurs courantes à éviter

❌ **Mauvais** :
```java
// 1. Mettre de la logique HTML en servlet
out.println("<html><body>...");

// 2. Pas de vérification de session
Fanfaron user = (Fanfaron) session.getAttribute("fanfaron");
user.getId();  // NPE si pas connecté

// 3. Mot de passe en clair en BD
fanfaron.setMotDePasse(password);  // DANGER !

// 4. Injection SQL
String query = "SELECT * FROM fanfaron WHERE nom = '" + nom + "'";

// 5. Pas de gestion d'erreur
dao.addFanfaron(fanfaron);  // Et si ça échoue ?
```

✅ **Bon** :
```java
// 1. HTML uniquement en JSP

// 2. Vérification systématique
if (session == null || session.getAttribute("fanfaron") == null) {
    response.sendRedirect("/connexion");
    return;
}

// 3. Hachage du mot de passe
String hash = hashPassword(password);
fanfaron.setMotDePasse(hash);

// 4. PreparedStatement
ps = connection.prepareStatement("SELECT * FROM fanfaron WHERE nom = ?");
ps.setString(1, nom);

// 5. Gestion d'erreur
if (dao.addFanfaron(fanfaron)) {
    request.setAttribute("success", "OK");
} else {
    request.setAttribute("error", "Erreur");
}
```

---

## 🎤 Questions probables en oral

### Technique
- **Q** : "Pourquoi utiliser des DAO ?"
  **R** : Séparation, testabilité, maintenabilité, flexibilité

- **Q** : "Comment éviter les injections SQL ?"
  **R** : PreparedStatement, paramètres bindés avec ?

- **Q** : "Comment fonctionne l'authentification ?"
  **R** : Hash SHA-256, vérification en BD, création session

- **Q** : "Qu'est-ce qu'une relation N:N ?"
  **R** : Table de liaison, jointures SQL, gestion complexe

### Architecture
- **Q** : "Pourquoi MVC ?"
  **R** : Séparation des responsabilités, maintenabilité, testabilité

- **Q** : "Différence entre forward et redirect ?"
  **R** : Forward = interne, URL inchangée / Redirect = redirection HTTP

- **Q** : "Qu'est-ce qu'une session ?"
  **R** : Stockage côté serveur de données utilisateur, identifiée par cookie

### Métier
- **Q** : "Combien d'instruments peut jouer un fanfaron ?"
  **R** : N instruments via relation N:N

- **Q** : "Un fanfaron peut-il s'inscrire deux fois au même événement ?"
  **R** : Non, clé primaire sur (idFanfaron, idEvenement)

---

## 🎯 Fil rouge pour présenter

### Début (vue d'ensemble)
"FanfareHub est une application web de gestion de fanfare utilisant l'architecture MVC."

### Milieu (entrez dans les détails)
"Les utilisateurs se connectent avec leur identifiant, le mot de passe est hashé en SHA-256..."

### Fin (résumé)
"En résumé, nous avons 3 couches (présentation, contrôle, données) avec les patterns Factory, Singleton et DAO."

---

## ⏱️ Timing estimé

- **Introduction** (2-3 min) : Vue d'ensemble, objectif
- **Architecture** (3-5 min) : MVC, couches, flows
- **Implémentation** (5-8 min) : Exemples de code, patterns
- **Démo possible** (2-3 min) : Montrer l'appli en action
- **Questions** (5+ min) : Discussions approfondies

**Total** : 17-25 minutes selon le niveau de détail

---

## 📚 Ressources à relire avant l'oral

1. **GUIDE_ORAL.md** - Vue complète du projet
2. **RESUME_COMMENTAIRES.md** - Liste des fichiers commentés
3. **Fichiers commentés** - Revoir 2-3 servlets et DAO clés
4. **Architecture diagram** - Visualiser le flux

---

## ✅ Checklist avant d'aller à l'oral

- [ ] Ai-je relu le GUIDE_ORAL.md ?
- [ ] Peux-je expliquer le flux d'une inscription ?
- [ ] Comprends-je pourquoi utiliser des DAO ?
- [ ] Peux-je parler de la sécurité (hachage, session) ?
- [ ] Comprends-je les patterns (Factory, Singleton, MVC) ?
- [ ] Peux-je expliquer une relation N:N ?
- [ ] Ai-je des exemples de code prêts à présenter ?
- [ ] Peux-je répondre aux erreurs courantes ?

**Si OUI à tous** → Vous êtes prêt ! 🚀

---

*Dernière révision conseillée : 1 heure avant l'oral*
*Relisez ce document rapidement pour vous remettre en mémoire les points clés*
