<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%--
        VUE ACCUEIL - Tableau de bord du fanfaron connecte

        Responsabilites :
        - Recuperer le fanfaron depuis la requete ou la session
        - Afficher ses informations personnelles
        - Afficher ses instruments, groupes et evenements inscrits
        - Echaper les valeurs dynamiques avant affichage HTML
    --%>
    <%@ page import="modele.Fanfaron" %>
    <%@ page import="modele.Instrument" %>
    <%@ page import="modele.GroupeFanfare" %>
    <%@ page import="modele.EvenementInscrit" %>
    <%@ page import="java.util.List" %>
    <%@ page import="java.text.SimpleDateFormat" %>
    <%!
        // Helper d'echappement HTML pour eviter l'injection de contenu dans la page
        private String h(Object value) {
            if (value == null) {
                return "";
            }
            return value.toString()
                    .replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
        }
    %>

        <%-- Recuperation du fanfaron connecte, avec plusieurs cles pour compatibilite entre servlets --%>
        <% Fanfaron fanfaron=(Fanfaron) request.getAttribute("fanfaron"); if (fanfaron==null) { fanfaron=(Fanfaron)
            session.getAttribute("fanfaron"); } if (fanfaron==null) { fanfaron=(Fanfaron)
            session.getAttribute("utilisateur"); } if (fanfaron==null) { response.sendRedirect("connexion"); return; }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat dateHeureFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            List<Instrument> instrumentsJoues = (List<Instrument>) request.getAttribute("instrumentsJoues");
            List<GroupeFanfare> groupes = (List<GroupeFanfare>) request.getAttribute("groupes");
            List<EvenementInscrit> evenementsInscrits = (List<EvenementInscrit>) request.getAttribute("evenementsInscrits");
            %>

            <!DOCTYPE html>
            <html lang="fr">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>FanfareHub - Accueil</title>
                <%-- Styles locaux de la page d'accueil --%>
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

                    .dashboard {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
                        gap: 18px;
                        margin-top: 22px;
                    }

                    .info-block {
                        background: #faf7f2;
                        border: 1px solid #d8c8b6;
                        border-radius: 8px;
                        padding: 18px;
                    }

                    .info-block h3 {
                        margin-top: 0;
                        color: #b1442f;
                    }

                    .info-block ul {
                        margin: 0;
                        padding-left: 18px;
                    }

                    .info-block li {
                        margin: 8px 0;
                    }

                    .muted {
                        color: #706257;
                    }
                </style>
            </head>

            <body>
                <%-- Barre de navigation commune aux pages connectees --%>
                <header>
                    <h1><a href="accueil">FanfareHub</a></h1>
                    <nav>
                        <% if (fanfaron.getAdmin()) { %>
                            <a href="admin">Administration</a>
                            <% } %>
                                <a href="mes-groupes">Mes Groupes</a>
                                <a href="evenement">Evenements</a>
                                <span>
                                    <%= h(fanfaron.getPrenom()) %>
                                        <%= h(fanfaron.getNom()) %>
                                            <% if (fanfaron.getAdmin()) { %>
                                                <span class="admin-badge">ADMIN</span>
                                                <% } %>
                                </span>
                                <a href="deconnexion" class="logout">Déconnexion</a>
                    </nav>
                </header>

                <%-- Contenu principal : informations du fanfaron et resume de ses activites --%>
                <main class="container">
                    <section class="card">
                        <h2>Bienvenue <%= h(fanfaron.getPrenom()) %> !</h2>
                        <p>Vous êtes connecté à FanfareHub.</p>

                        <div class="user-info">
                            <p><strong>Nom de fanfaron :</strong>
                                <%= h(fanfaron.getNomFanfaron()) %>
                            </p>
                            <p><strong>Email :</strong>
                                <%= h(fanfaron.getEmail()) %>
                            </p>
                            <p><strong>Prénom :</strong>
                                <%= h(fanfaron.getPrenom()) %>
                            </p>
                            <p><strong>Nom :</strong>
                                <%= h(fanfaron.getNom()) %>
                            </p>
                            <p><strong>Genre :</strong>
                                <%= h(fanfaron.getGenre()) %>
                            </p>
                            <p><strong>Contraintes alimentaires :</strong>
                                <%= h(fanfaron.getContraintesAlimentaires()) %>
                            </p>
                            <p><strong>Administrateur :</strong>
                                <%= fanfaron.getAdmin() ? "Oui" : "Non" %>
                            </p>
                            <p><strong>Membre depuis :</strong>
                                <%= fanfaron.getDateCreation() == null ? "" : dateFormat.format(fanfaron.getDateCreation()) %>
                            </p>
                        </div>

                        <div class="dashboard">
                            <div class="info-block">
                                <h3>Mes instruments</h3>
                                <% if (instrumentsJoues == null || instrumentsJoues.isEmpty()) { %>
                                    <p class="muted">Aucun instrument renseigné.</p>
                                <% } else { %>
                                    <ul>
                                        <% for (Instrument instrument : instrumentsJoues) { %>
                                            <li><%= h(instrument.getNom()) %></li>
                                        <% } %>
                                    </ul>
                                <% } %>
                            </div>

                            <div class="info-block">
                                <h3>Mes groupes</h3>
                                <% if (groupes == null || groupes.isEmpty()) { %>
                                    <p class="muted">Aucun groupe renseigné.</p>
                                <% } else { %>
                                    <ul>
                                        <% for (GroupeFanfare groupe : groupes) { %>
                                            <li><%= h(groupe.getNom()) %></li>
                                        <% } %>
                                    </ul>
                                <% } %>
                            </div>

                            <div class="info-block">
                                <h3>Mes événements</h3>
                                <% if (evenementsInscrits == null || evenementsInscrits.isEmpty()) { %>
                                    <p class="muted">Aucune inscription enregistrée.</p>
                                <% } else { %>
                                    <ul>
                                        <% for (EvenementInscrit evenement : evenementsInscrits) { %>
                                            <li>
                                                <strong><%= h(evenement.getNom()) %></strong><br>
                                                <span class="muted">
                                                    <%= evenement.getHorodatage() == null ? "" : dateHeureFormat.format(evenement.getHorodatage()) %>
                                                    <% if (evenement.getLieu() != null && !evenement.getLieu().isBlank()) { %>
                                                        - <%= h(evenement.getLieu()) %>
                                                    <% } %>
                                                </span><br>
                                                <span><%= h(evenement.getInstrument()) %> - <%= h(evenement.getStatut()) %></span>
                                            </li>
                                        <% } %>
                                    </ul>
                                <% } %>
                            </div>
                        </div>
                    </section>
                </main>
            </body>

            </html>
