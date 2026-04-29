<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="modele.Fanfaron" %>
        <!DOCTYPE html>
        <html lang="fr">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>FanfareHub - Tableau de bord</title>
            <style>
                :root {
                    --bg-1: #f7f4ee;
                    --bg-2: #ece4d8;
                    --card: #fffdfa;
                    --text: #1f1b16;
                    --muted: #706257;
                    --accent: #b1442f;
                    --accent-strong: #8f3423;
                    --border: #d8c8b6;
                    --success: #388e3c;
                }

                * {
                    box-sizing: border-box;
                }

                body {
                    margin: 0;
                    font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
                    color: var(--text);
                    background: linear-gradient(165deg, var(--bg-1), var(--bg-2));
                    min-height: 100vh;
                }

                header {
                    background: var(--card);
                    border-bottom: 1px solid var(--border);
                    padding: 16px 32px;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    box-shadow: 0 2px 8px rgba(31, 27, 22, 0.1);
                }

                h1 {
                    margin: 0;
                    color: var(--accent);
                }

                nav {
                    display: flex;
                    gap: 16px;
                    align-items: center;
                }

                a {
                    color: var(--accent);
                    text-decoration: none;
                    font-weight: 500;
                    padding: 8px 16px;
                    border-radius: 6px;
                    transition: background-color 0.3s;
                }

                a:hover {
                    background-color: rgba(177, 68, 47, 0.1);
                }

                .logout {
                    background-color: var(--accent);
                    color: white !important;
                }

                .logout:hover {
                    background-color: var(--accent-strong);
                }

                .admin-badge {
                    background-color: var(--success);
                    color: white;
                    padding: 4px 8px;
                    border-radius: 4px;
                    font-size: 12px;
                    font-weight: 600;
                }

                .container {
                    max-width: 1000px;
                    margin: 32px auto;
                    padding: 0 24px;
                }

                .welcome-card {
                    background: var(--card);
                    border: 1px solid var(--border);
                    border-radius: 16px;
                    padding: 32px;
                    box-shadow: 0 10px 30px rgba(31, 27, 22, 0.15);
                    text-align: center;
                }

                .welcome-card h2 {
                    margin-top: 0;
                    color: var(--accent);
                }

                .user-info {
                    background: #f5f5f5;
                    padding: 16px;
                    border-radius: 8px;
                    margin: 24px 0;
                    text-align: left;
                }

                .user-info p {
                    margin: 8px 0;
                }

                .user-info strong {
                    color: var(--accent);
                }

                .button-group {
                    display: flex;
                    gap: 12px;
                    margin-top: 24px;
                    justify-content: center;
                }

                button,
                .btn {
                    padding: 12px 24px;
                    background-color: var(--accent);
                    color: white;
                    border: none;
                    border-radius: 8px;
                    font-size: 16px;
                    font-weight: 600;
                    cursor: pointer;
                    text-decoration: none;
                    display: inline-block;
                    transition: background-color 0.3s;
                }

                button:hover,
                .btn:hover {
                    background-color: var(--accent-strong);
                }

                .btn-secondary {
                    background-color: #757575;
                }

                .btn-secondary:hover {
                    background-color: #616161;
                }
            </style>
        </head>

        <body>
            <% Fanfaron utilisateur=(Fanfaron) session.getAttribute("utilisateur"); if (utilisateur==null) {
                response.sendRedirect("connexion"); return; } %>

                <header>
                    <div>
                        <h1>🎺 FanfareHub</h1>
                    </div>
                    <nav>
                        <% if (utilisateur.isAdmin()) { %>
                            <a href="admin">🔧 Administration</a>
                            <% } %>
                                <span>
                                    <%= utilisateur.getPrenom() %>
                                        <%= utilisateur.getNom() %>
                                            <% if (utilisateur.isAdmin()) { %>
                                                <span class="admin-badge">ADMIN</span>
                                                <% } %>
                                </span>
                                <a href="deconnexion" class="logout">Déconnexion</a>
                    </nav>
                </header>

                <div class="container">
                    <div class="welcome-card">
                        <h2>Bienvenue <%= utilisateur.getPrenom() %> ! 🎉</h2>
                        <p>Vous êtes connecté à FanfareHub</p>

                        <div class="user-info">
                            <p><strong>Nom d'utilisateur :</strong>
                                <%= utilisateur.getNomFanfaron() %>
                            </p>
                            <p><strong>Email :</strong>
                                <%= utilisateur.getEmail() %>
                            </p>
                            <p><strong>Prénom :</strong>
                                <%= utilisateur.getPrenom() %>
                            </p>
                            <p><strong>Nom :</strong>
                                <%= utilisateur.getNom() %>
                            </p>
                            <p><strong>Genre :</strong>
                                <%= utilisateur.getGenre() %>
                            </p>
                            <p><strong>Contraintes alimentaires :</strong>
                                <%= utilisateur.getContraintesAlimentaires() %>
                            </p>
                            <p><strong>Rôle :</strong>
                                <%= utilisateur.getRole() %>
                            </p>
                            <p><strong>Membre depuis :</strong>
                                <%= utilisateur.getDateCreation() %>
                            </p>
                        </div>

                        <div class="button-group">
                            <% if (utilisateur.isAdmin()) { %>
                                <a href="admin" class="btn">Gérer les utilisateurs</a>
                                <% } %>
                                    <a href="deconnexion" class="btn btn-secondary">Déconnexion</a>
                        </div>
                    </div>
                </div>
        </body>

        </html>