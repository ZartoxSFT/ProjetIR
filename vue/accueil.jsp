<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="modele.Fanfaron" %>

        <% Fanfaron fanfaron=(Fanfaron) request.getAttribute("fanfaron"); if (fanfaron==null) { fanfaron=(Fanfaron)
            session.getAttribute("fanfaron"); } if (fanfaron==null) { fanfaron=(Fanfaron)
            session.getAttribute("utilisateur"); } if (fanfaron==null) { response.sendRedirect("connexion"); return; }
            %>

            <!DOCTYPE html>
            <html lang="fr">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>FanfareHub - Accueil</title>
                <style>
                    body {
                        margin: 0;
                        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
                        color: #1f1b16;
                        background: linear-gradient(165deg, #f7f4ee, #ece4d8);
                        min-height: 100vh;
                    }

                    header {
                        background: #fffdfa;
                        border-bottom: 1px solid #d8c8b6;
                        padding: 16px 32px;
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                    }

                    h1,
                    h2 {
                        color: #b1442f;
                    }

                    nav {
                        display: flex;
                        gap: 16px;
                        align-items: center;
                    }

                    a {
                        color: #b1442f;
                        text-decoration: none;
                        font-weight: 600;
                    }

                    .logout {
                        background: #b1442f;
                        color: white;
                        padding: 8px 14px;
                        border-radius: 6px;
                    }

                    .admin-badge {
                        background: #388e3c;
                        color: white;
                        padding: 4px 8px;
                        border-radius: 4px;
                        font-size: 12px;
                        font-weight: 700;
                    }

                    .container {
                        max-width: 900px;
                        margin: 32px auto;
                        padding: 0 24px;
                    }

                    .card {
                        background: #fffdfa;
                        border: 1px solid #d8c8b6;
                        border-radius: 10px;
                        padding: 28px;
                        box-shadow: 0 10px 30px rgba(31, 27, 22, 0.12);
                    }

                    .user-info {
                        background: #f5f5f5;
                        padding: 16px;
                        border-radius: 8px;
                        margin-top: 20px;
                    }

                    .user-info p {
                        margin: 8px 0;
                    }
                </style>
            </head>

            <body>
                <header>
                    <h1><a href="accueil">FanfareHub</a></h1>
                    <nav>
                        <% if (fanfaron.getAdmin()) { %>
                            <a href="admin">Administration</a>
                            <% } %>
                                <a href="mes-groupes">Mes Groupes</a>
                                <a href="evenement">Evenements</a>
                                <span>
                                    <%= fanfaron.getPrenom() %>
                                        <%= fanfaron.getNom() %>
                                            <% if (fanfaron.getAdmin()) { %>
                                                <span class="admin-badge">ADMIN</span>
                                                <% } %>
                                </span>
                                <a href="deconnexion" class="logout">Déconnexion</a>
                    </nav>
                </header>

                <main class="container">
                    <section class="card">
                        <h2>Bienvenue <%= fanfaron.getPrenom() %> !</h2>
                        <p>Vous êtes connecté à FanfareHub.</p>

                        <div class="user-info">
                            <p><strong>Nom de fanfaron :</strong>
                                <%= fanfaron.getNomFanfaron() %>
                            </p>
                            <p><strong>Email :</strong>
                                <%= fanfaron.getEmail() %>
                            </p>
                            <p><strong>Prénom :</strong>
                                <%= fanfaron.getPrenom() %>
                            </p>
                            <p><strong>Nom :</strong>
                                <%= fanfaron.getNom() %>
                            </p>
                            <p><strong>Genre :</strong>
                                <%= fanfaron.getGenre() %>
                            </p>
                            <p><strong>Contraintes alimentaires :</strong>
                                <%= fanfaron.getContraintesAlimentaires() %>
                            </p>
                            <p><strong>Administrateur :</strong>
                                <%= fanfaron.getAdmin() ? "Oui" : "Non" %>
                            </p>
                            <p><strong>Membre depuis :</strong>
                                <%= fanfaron.getDateCreation() %>
                            </p>
                        </div>
                    </section>
                </main>
            </body>

            </html>