USE taskydb;

-- Insertion de 3 utilisateurs
INSERT INTO User (nom, prenom, email, password, competance, telephone, disponibilite, dateCreation, verifCode)
VALUES
    ('Dupont', 'Jean', 'jean.dupont@example.com', 'password123', 'Développement Web', '+123456789', TRUE, '2025-11-01', 'ABC123'),
    ('Martin', 'Marie', 'marie.martin@example.com', 'password456', 'Design UI/UX', '+987654321', TRUE, '2025-11-01', 'DEF456'),
    ('Bernard', 'Pierre', 'pierre.bernard@example.com', 'password789', 'Gestion de projet', '+1122334455', TRUE, '2025-11-01', 'GHI789');

-- Insertion d'un projet avec id 1
INSERT INTO Projet (id, nom, description, dateDebut, dateFin, deadline, budget, budgetConsomme, statut, code)
VALUES
    (1, 'Projet Tasky', 'Un projet de gestion de tâches collaboratives.', '2025-11-01', '2025-12-31', '2025-12-15', 5000.00, 0.00, 'En cours', 'TASKY2025');

-- Insertion de 3 membres pour le projet (référencés aux 3 utilisateurs)
INSERT INTO Membre (description, nom, prenom, email, telephone, dateRejointe, role, type, userID, projetID)
VALUES
    ('Membre actif', 'Dupont', 'Jean', 'jean.dupont@example.com', '+123456789', '2025-11-01', 'Développeur', 'Interne', 1, 1),
    ('Membre actif', 'Martin', 'Marie', 'marie.martin@example.com', '+987654321', '2025-11-01', 'Designer', 'Interne', 2, 1),
    ('Membre actif', 'Bernard', 'Pierre', 'pierre.bernard@example.com', '+1122334455', '2025-11-01', 'Chef de projet', 'Interne', 3, 1);

-- Insertion de 3 tâches pour le projet, chacune affectée à un membre
INSERT INTO Tache (titre, description, dateLimite, etat, dateCreation, dateFin, projetID, depense)
VALUES
    ('Développement Backend', 'Créer l''API pour le projet.', '2025-11-15', 'En cours', '2025-11-01', NULL, 1, 1000),
    ('Design UI', 'Créer les maquettes pour l''application.', '2025-11-20', 'En cours', '2025-11-01', NULL, 1, 800),
    ('Planification', 'Planifier les étapes du projet.', '2025-11-10', 'Terminé', '2025-11-01', '2025-11-05', 1, 500);

-- Insertion de la table de liaison TacheMembre
INSERT INTO TacheMembre (tacheID, membreID)
VALUES
    (1, 1),
    (2, 2),
    (3, 3);

-- Insertion de sous-tâches pour chaque tâche
INSERT INTO SousTache (titre, dateCreation, dateFin, termine, tacheID)
VALUES
    ('Créer les endpoints', '2025-11-01', '2025-11-10', TRUE, 1),
    ('Tester l''API', '2025-11-11', '2025-11-15', FALSE, 1),
    ('Maquettes Homepage', '2025-11-01', '2025-11-15', TRUE, 2),
    ('Maquettes Dashboard', '2025-11-16', '2025-11-20', FALSE, 2),
    ('Définir les étapes', '2025-11-01', '2025-11-03', TRUE, 3),
    ('Assigner les ressources', '2025-11-04', '2025-11-05', TRUE, 3);

-- Insertion de commentaires pour chaque tâche
INSERT INTO Commentaire (contenu, dateCreation, tacheID, membreID)
VALUES
    ('L''API est en cours de développement.', '2025-11-02', 1, 1),
    ('Les maquettes sont prêtes pour révision.', '2025-11-03', 2, 2),
    ('Les étapes sont définies.', '2025-11-04', 3, 3);

-- Insertion de documents pour chaque tâche
INSERT INTO Document (nom, description, dateCreation, contenu, size, type, tacheID)
VALUES
    ('API_Specs.pdf', 'Spécifications techniques de l''API.', '2025-11-02', NULL, 500, 'PDF', 1),
    ('UI_Mockups.png', 'Maquettes pour l''interface utilisateur.', '2025-11-03', NULL, 800, 'Image', 2),
    ('Project_Plan.docx', 'Planification détaillée du projet.', '2025-11-04', NULL, 300, 'Document', 3);

-- Insertion de notifications pour chaque membre
INSERT INTO Notification (contenu, dateEnvoie, estLue, membreID, projetID)
VALUES
    ('Nouvelle tâche assignée : Développement Backend.', '2025-11-01', FALSE, 1, 1),
    ('Nouvelle tâche assignée : Design UI.', '2025-11-01', FALSE, 2, 1),
    ('Nouvelle tâche assignée : Planification.', '2025-11-01', FALSE, 3, 1);