DROP DATABASE IF EXISTS taskydb;
CREATE DATABASE taskydb;
USE taskydb;
-- TABLE USER
CREATE TABLE User (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100),
    prenom VARCHAR(100),
    email VARCHAR(150),
    password VARCHAR(255),
    competance VARCHAR(255),
    telephone VARCHAR(50),
    disponibilite BOOLEAN,
    dateCreation DATE,
    verifCode VARCHAR(255)
);
-- TABLE PROJET
CREATE TABLE Projet (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(150),
    description VARCHAR(255),
    dateDebut DATE,
    dateFin DATE,
    deadline DATE,
    budget FLOAT,
    budgetConsomme FLOAT,
    statut VARCHAR(50),
    code VARCHAR(50)
);
-- TABLE MEMBRE
CREATE TABLE Membre (
    id INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255),
    dateRejointe DATE,
    role VARCHAR(100),
    type VARCHAR(100),
    userID INT,
    projetID INT,
    FOREIGN KEY (userID) REFERENCES User(id),
    FOREIGN KEY (projetID) REFERENCES Projet(id)
);
-- TABLE RAPPORT
CREATE TABLE Rapport (
    id INT PRIMARY KEY AUTO_INCREMENT,
    dateGeneration DATE,
    nom VARCHAR(150),
    projetID INT,
    FOREIGN KEY (projetID) REFERENCES Projet(id)
);
-- TABLE TACHE
CREATE TABLE Tache (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titre VARCHAR(150),
    description VARCHAR(255),
    dateLimite DATE,
    etat VARCHAR(50),
    dateCreation DATE,
    dateFin DATE,
    projetID INT,
    depense INT DEFAULT 0,
    FOREIGN KEY (projetID) REFERENCES Projet(id)
);
-- TABLE SOUS-TACHE
CREATE TABLE SousTache (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titre VARCHAR(150),
    dateCreation DATE,
    dateFin DATE,
    termine BOOLEAN,
    tacheID INT,
    FOREIGN KEY (tacheID) REFERENCES Tache(id)
);
-- TABLE COMMENTAIRE
CREATE TABLE Commentaire (
    id INT PRIMARY KEY AUTO_INCREMENT,
    contenu TEXT,
    dateCreation DATE,
    tacheID INT,
    membreID INT,
    FOREIGN KEY (tacheID) REFERENCES Tache(id),
    FOREIGN KEY (membreID) REFERENCES Membre(id)
);
-- TABLE DOCUMENT
CREATE TABLE Document (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(150),
    description VARCHAR(255),
    dateCreation DATE,
    contenu LONGBLOB,
    size INT,
    type VARCHAR(50),
    tacheID INT,
    FOREIGN KEY (tacheID) REFERENCES Tache(id)
);
-- TABLE BLOCAGE (CORRIGÉE)
CREATE TABLE Blocage (
    id INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255),
    dateSignalement DATE,
    statut VARCHAR(50),
    dateResolution DATE,
    tacheID INT,
    FOREIGN KEY (tacheID) REFERENCES Tache(id)
);
-- TABLE MESSAGE
CREATE TABLE Message (
    id INT PRIMARY KEY AUTO_INCREMENT,
    contenu TEXT,
    dateEnvoi DATE,
    estLu BOOLEAN,
    membreID INT,
    projetID INT,
    FOREIGN KEY (membreID) REFERENCES Membre(id),
    FOREIGN KEY (projetID) REFERENCES Projet(id)
);
-- TABLE APPEL
CREATE TABLE Appel (
    id INT PRIMARY KEY AUTO_INCREMENT,
    dateAppel DATE,
    heureDebut TIME,
    heureFin TIME,
    duree INT,
    projetID INT,
    initiateurID INT,
    FOREIGN KEY (projetID) REFERENCES Projet(id),
    FOREIGN KEY (initiateurID) REFERENCES Membre(id)
);
-- TABLE NOTIFICATION (CORRIGÉE)
CREATE TABLE Notification (
    id INT PRIMARY KEY AUTO_INCREMENT,
    contenu VARCHAR(255),
    dateEnvoie DATE,
    estLue BOOLEAN,
    membreID INT,
    FOREIGN KEY (membreID) REFERENCES Membre(id)
);
-- TABLE DE LIAISON TACHE-MEMBRE
CREATE TABLE TacheMembre (
    tacheID INT,
    membreID INT,
    PRIMARY KEY AUTO_INCREMENT (tacheID, membreID),
    FOREIGN KEY (tacheID) REFERENCES Tache(id),
    FOREIGN KEY (membreID) REFERENCES Membre(id)
);