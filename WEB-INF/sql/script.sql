-- =========================
-- RESET
-- =========================
DROP TABLE IF EXISTS inscription CASCADE;
DROP TABLE IF EXISTS organisation_evenement CASCADE;
DROP TABLE IF EXISTS fanfaron_groupe CASCADE;
DROP TABLE IF EXISTS fanfaron_instrument CASCADE;
DROP TABLE IF EXISTS evenement CASCADE;
DROP TABLE IF EXISTS groupe_fanfare CASCADE;
DROP TABLE IF EXISTS instrument CASCADE;
DROP TABLE IF EXISTS fanfaron CASCADE;

-- =========================
-- TABLES DE REFERENCE
-- =========================
CREATE TABLE instrument (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE groupe_fanfare (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE
);

-- =========================
-- FANFARON
-- =========================
CREATE TABLE fanfaron (
    id SERIAL PRIMARY KEY,
    nom_fanfaron VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    nom VARCHAR(50) NOT NULL,
    genre VARCHAR(10) CHECK (genre IN ('homme', 'femme', 'autre')),
    contraintes_alimentaires VARCHAR(20)
        CHECK (contraintes_alimentaires IN ('aucune', 'vegetarien', 'vegan', 'sans porc')),
    role VARCHAR(20) DEFAULT 'utilisateur' CHECK (role IN ('utilisateur', 'admin')),
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    derniere_connexion TIMESTAMP NULL,
    admin BOOLEAN NOT NULL DEFAULT FALSE
);

-- =========================
-- ASSOCIATIONS FANFARON <-> INSTRUMENT
-- Un fanfaron peut jouer plusieurs instruments,
-- un instrument peut être joué par plusieurs fanfarons
-- =========================
CREATE TABLE fanfaron_instrument (
    id_fanfaron INTEGER NOT NULL,
    id_instrument INTEGER NOT NULL,
    PRIMARY KEY (id_fanfaron, id_instrument),
    CONSTRAINT fk_fi_fanfaron
        FOREIGN KEY (id_fanfaron)
        REFERENCES fanfaron(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_fi_instrument
        FOREIGN KEY (id_instrument)
        REFERENCES instrument(id)
        ON DELETE CASCADE
);

-- =========================
-- ASSOCIATIONS FANFARON <-> GROUPE
-- Un fanfaron peut appartenir à plusieurs groupes
-- =========================
CREATE TABLE fanfaron_groupe (
    id_fanfaron INTEGER NOT NULL,
    id_groupe INTEGER NOT NULL,
    PRIMARY KEY (id_fanfaron, id_groupe),
    CONSTRAINT fk_fg_fanfaron
        FOREIGN KEY (id_fanfaron)
        REFERENCES fanfaron(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_fg_groupe
        FOREIGN KEY (id_groupe)
        REFERENCES groupe_fanfare(id)
        ON DELETE CASCADE
);

-- =========================
-- EVENEMENT
-- =========================
CREATE TABLE evenement (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    horodatage TIMESTAMP NOT NULL,
    duree INTEGER NOT NULL CHECK (duree > 0),
    lieu VARCHAR(150) NOT NULL,
    description TEXT
);

-- =========================
-- ASSOCIATION FANFARON <-> EVENEMENT (organisation)
-- Un fanfaron peut organiser plusieurs événements
-- Un événement peut être organisé par un ou plusieurs fanfarons
-- =========================
CREATE TABLE organisation_evenement (
    id_fanfaron INTEGER NOT NULL,
    id_evenement INTEGER NOT NULL,
    PRIMARY KEY (id_fanfaron, id_evenement),
    CONSTRAINT fk_org_fanfaron
        FOREIGN KEY (id_fanfaron)
        REFERENCES fanfaron(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_org_evenement
        FOREIGN KEY (id_evenement)
        REFERENCES evenement(id)
        ON DELETE CASCADE
);

-- =========================
-- ASSOCIATION FANFARON <-> EVENEMENT (participation)
-- avec attributs : instrument choisi + statut
-- =========================
CREATE TABLE inscription (
    id_fanfaron INTEGER NOT NULL,
    id_evenement INTEGER NOT NULL,
    id_instrument INTEGER NOT NULL,
    statut VARCHAR(10) NOT NULL
        CHECK (statut IN ('present', 'absent', 'incertain')),
    PRIMARY KEY (id_fanfaron, id_evenement),
    CONSTRAINT fk_ins_fanfaron
        FOREIGN KEY (id_fanfaron)
        REFERENCES fanfaron(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_ins_evenement
        FOREIGN KEY (id_evenement)
        REFERENCES evenement(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_ins_instrument
        FOREIGN KEY (id_instrument)
        REFERENCES instrument(id)
        ON DELETE RESTRICT
);

-- =========================
-- DONNEES INITIALES
-- =========================
INSERT INTO instrument (nom) VALUES
('clarinette'),
('saxophone alto'),
('euphonium'),
('percussion'),
('basse'),
('trompette'),
('saxophone baryton'),
('trombone');

INSERT INTO groupe_fanfare (nom) VALUES
('commission prestation'),
('commission artistique'),
('commission logistique'),
('commission communication interne');

-- Création Admin Nom d'utilisateur : admin mot de passe : admin

INSERT INTO fanfaron (nom_fanfaron, email, mot_de_passe, prenom, nom, genre, contraintes_alimentaires, admin) VALUES
('admin', 'admin@gmail.com', 'jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=', 'admin', 'admin', 'homme', 'vegetarien', TRUE);