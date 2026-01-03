USE taskydb;

-- =========================
-- 1. UTILISATEURS
-- =========================
INSERT INTO User (nom, prenom, email, password, competance, telephone, disponibilite, dateCreation)
VALUES
('Gana', 'Anas', 'anas.gana@gmail.com', '111111', 'Fullstack', '0707070707', TRUE, CURDATE()),
('Tayef', 'Jihane', 'tayefjihane@gmail.com', '111111', 'Backend', '0707070707', TRUE, CURDATE());

SET @anasUserId   = (SELECT id FROM User WHERE email='anas.gana@gmail.com');
SET @jihaneUserId = (SELECT id FROM User WHERE email='tayefjihane@gmail.com');

-- =========================
-- 2. PROJET
-- =========================
INSERT INTO Projet (nom, description, dateDebut, deadline, budget, budgetConsomme, statut, code)
VALUES ('Projet Tasky', 'Projet de gestion des tâches Tasky', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 MONTH), 5000, 5500, 'En cours', 'TASKY-001');

SET @projetId = LAST_INSERT_ID();

-- =========================
-- 3. MEMBRES
-- =========================
INSERT INTO Membre (nom, prenom, email, telephone, dateRejointe, role, type, userID, projetID)
VALUES
('Gana', 'Anas', 'anas.gana@gmail.com', '0707070707', CURDATE(), 'Responsable', 'Interne', @anasUserId, @projetId),
('Tayef', 'Jihane', 'tayefjihane@gmail.com', '0707070707', CURDATE(), 'Membre', 'Interne', @jihaneUserId, @projetId);

SET @anasMembreId   = (SELECT id FROM Membre WHERE userID=@anasUserId AND projetID=@projetId);
SET @jihaneMembreId = (SELECT id FROM Membre WHERE userID=@jihaneUserId AND projetID=@projetId);

-- =========================
-- 4. TÂCHES (TOTAL DÉPENSES = 5500)
-- =========================
INSERT INTO Tache (titre, description, dateLimite, etat, dateCreation, projetID, depense) VALUES
('Analyse besoins', 'Analyse fonctionnelle', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'terminée', CURDATE(), @projetId, 800),
('Conception UI', 'Maquettes UI', DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'terminée', CURDATE(), @projetId, 600),
('Backend Auth', 'Auth & sécurité', DATE_ADD(CURDATE(), INTERVAL 20 DAY), 'en cours', CURDATE(), @projetId, 900),
('API Tâches', 'CRUD tâches', DATE_ADD(CURDATE(), INTERVAL 25 DAY), 'en cours', CURDATE(), @projetId, 700),
('API Projets', 'CRUD projets', DATE_ADD(CURDATE(), INTERVAL 25 DAY), 'en cours', CURDATE(), @projetId, 500),
('Frontend Dashboard', 'Dashboard UI', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'en attente', CURDATE(), @projetId, 400),
('Notifications', 'Système notifications', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'en attente', CURDATE(), @projetId, 300),
('Tests', 'Tests unitaires', DATE_ADD(CURDATE(), INTERVAL 35 DAY), 'en attente', CURDATE(), @projetId, 200),
('Déploiement', 'Mise en production', DATE_ADD(CURDATE(), INTERVAL 40 DAY), 'en attente', CURDATE(), @projetId, 100),
('Documentation', 'Documentation projet', DATE_ADD(CURDATE(), INTERVAL 45 DAY), 'en attente', CURDATE(), @projetId, 0);

-- =========================
-- 5. RÉPARTITION TÂCHES / MEMBRES
-- =========================
INSERT INTO TacheMembre (tacheID, membreID)
SELECT id, @anasMembreId FROM Tache WHERE id <= (SELECT MIN(id)+4 FROM Tache);

INSERT INTO TacheMembre (tacheID, membreID)
SELECT id, @jihaneMembreId FROM Tache WHERE id > (SELECT MIN(id)+4 FROM Tache);

-- =========================
-- 6. SOUS-TÂCHES (7 TÂCHES AVEC 2–5 SOUS-TÂCHES)
-- =========================
INSERT INTO SousTache (titre, dateCreation, termine, tacheID) VALUES
('Préparer specs', CURDATE(), TRUE, 1),
('Valider specs', CURDATE(), TRUE, 1),
('Wireframes', CURDATE(), TRUE, 2),
('Design final', CURDATE(), FALSE, 2),
('JWT', CURDATE(), FALSE, 3),
('Roles', CURDATE(), FALSE, 3),
('Endpoints', CURDATE(), FALSE, 4),
('Validation', CURDATE(), FALSE, 4),
('Controllers', CURDATE(), FALSE, 5),
('Services', CURDATE(), FALSE, 5),
('Layout', CURDATE(), FALSE, 6),
('Charts', CURDATE(), FALSE, 6),
('Email notif', CURDATE(), FALSE, 7),
('In-app notif', CURDATE(), FALSE, 7);

-- =========================
-- 7. NOTIFICATIONS (POUR ANAS)
-- =========================
INSERT INTO Notification (contenu, dateEnvoie, estLue, membreID, projetID) VALUES
('Nouvelle dépense pour la tâche Analyse besoins (800)', CURDATE(), FALSE, @anasMembreId, @projetId),
('Nouvelle dépense pour la tâche Conception UI (600)', CURDATE(), FALSE, @anasMembreId, @projetId),
('Nouveau blocage pour la tâche Backend Auth (JWT)', CURDATE(), FALSE, @anasMembreId, @projetId),
('Nouvelle dépense pour la tâche Backend Auth (900)', CURDATE(), FALSE, @anasMembreId, @projetId),
('Nouveau blocage pour la tâche API Tâches (Validation)', CURDATE(), FALSE, @anasMembreId, @projetId),
('Nouvelle dépense pour la tâche API Tâches (700)', CURDATE(), FALSE, @anasMembreId, @projetId),
('Nouvelle dépense pour la tâche API Projets (500)', CURDATE(), FALSE, @anasMembreId, @projetId),
('Nouveau blocage pour la tâche Frontend Dashboard (Layout)', CURDATE(), FALSE, @anasMembreId, @projetId),
('Nouvelle dépense pour la tâche Frontend Dashboard (400)', CURDATE(), FALSE, @anasMembreId, @projetId),
('Nouvelle dépense pour la tâche Notifications (300)', CURDATE(), FALSE, @anasMembreId, @projetId);