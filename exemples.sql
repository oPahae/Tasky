INSERT INTO User (id, nom, prenom, email, password, competence, telephone, disponibilite) VALUES
(1, 'Benali', 'Youssef', 'y.benali@example.com', 'hash123', 'Frontend, React', '0611223344', TRUE),
(2, 'Karimi', 'Imane', 'imane.k@example.com', 'hash456', 'Backend, Java', '0677889900', TRUE),
(3, 'Othmani', 'Ayoub', 'a.othmani@example.com', 'hash789', 'DevOps, Docker', '0655332211', FALSE);

INSERT INTO Projet (id, nom, description, dateDebut, dateFin, deadline, budget, budgetConsomme, statut) VALUES
(1, 'Tasky Manager', 'Application de gestion de tâches', '2025-01-01', NULL, '2025-06-01', 150000, 20000, 'En cours'),
(2, 'E-commerce Pro', 'Plateforme de vente en ligne', '2024-11-15', NULL, '2025-05-30', 90000, 15000, 'En cours');

INSERT INTO Membre (id, description, dateCreation, role, type, userID, projetID) VALUES
(1, 'Chef de projet', '2025-01-10', 'Manager', 'Interne', 1, 1),
(2, 'Développeur Backend', '2025-01-10', 'Dev', 'Interne', 2, 1),
(3, 'DevOps Engineer', '2025-01-12', 'Ops', 'Externe', 3, 1);

INSERT INTO Tache (id, titre, description, dateLimite, etat, dateCreation, dateFin, projetID) VALUES
(1, 'Création interface login', 'Développer la page login', '2025-02-10', 'En cours', '2025-01-15', NULL, 1),
(2, 'API d’authentification', 'Créer API login/register', '2025-02-15', 'Non commencé', '2025-01-16', NULL, 1),
(3, 'Pipeline CI/CD', 'Mettre en place GitHub Actions', '2025-03-01', 'En cours', '2025-01-20', NULL, 1);

INSERT INTO SousTache (id, titre, dateCreation, dateFin, termine, tacheID) VALUES
(1, 'Design UI login', '2025-01-15', NULL, FALSE, 1),
(2, 'Connexion API', '2025-01-16', NULL, FALSE, 2),
(3, 'Dockerisation', '2025-01-20', NULL, FALSE, 3);

INSERT INTO Commentaire (id, contenu, dateCreation, tacheID, membreID) VALUES
(1, 'Je commence la maquette.', '2025-01-15', 1, 1),
(2, 'API presque prête.', '2025-01-18', 2, 2),
(3, 'Pipeline configuré.', '2025-01-22', 3, 3);

INSERT INTO Document (id, nom, description, dateCreation, contenu, tacheID) VALUES
(1, 'maquette_login.png', 'Maquette de la page login', '2025-01-15', NULL, 1),
(2, 'schema_api.pdf', 'Schéma de l’API Auth', '2025-01-18', NULL, 2);

INSERT INTO Blocage (id, description, dateSignalement, statut, dateResolution, documentID, tacheID) VALUES
(1, 'Erreur sur pipeline CI/CD', '2025-01-22', 'En attente', NULL, NULL, 3),
(2, 'Bug validation email', '2025-01-19', 'Résolu', '2025-01-20', NULL, 2);

INSERT INTO Message (id, contenu, dateEnvoi, estLu, membreID, projetID) VALUES
(1, 'On se réunit demain.', '2025-01-15', FALSE, 1, 1),
(2, 'API backend update.', '2025-01-17', TRUE, 2, 1);

INSERT INTO Appel (id, dateAppel, heureDebut, heureFin, duree, projetID, initiateurID) VALUES
(1, '2025-01-18', '10:00:00', '10:30:00', 30, 1, 1),
(2, '2025-01-19', '15:00:00', '15:10:00', 10, 1, 3);

INSERT INTO Notification (id, contenu, dateEnvoie, estLue, userID) VALUES
(1, 'Nouvelle tâche assignée.', '2025-01-15', FALSE, 1),
(2, 'Document ajouté.', '2025-01-18', TRUE, 2);

INSERT INTO TacheMembre (tacheID, membreID) VALUES
(1, 1),
(2, 2),
(3, 3),
(1, 2);