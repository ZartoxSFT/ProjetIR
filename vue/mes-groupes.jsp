<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
    VUE MES GROUPES - Instruments et groupes du fanfaron

    Responsabilites :
    - Afficher les instruments et groupes disponibles
    - Cocher les choix deja associes au fanfaron
    - Afficher les formulaires admin de gestion des references
    - Echaper les valeurs dynamiques avant affichage HTML
--%>
<%@ page import="java.util.List" %>
<%@ page import="modele.Instrument" %>
<%@ page import="modele.GroupeFanfare" %>
<%@ page import="modele.Fanfaron" %>
<%!
    // Helper d'echappement HTML pour securiser les donnees affichees
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

<%
    // Recuperation du fanfaron connecte, avec plusieurs cles pour compatibilite entre servlets
    Fanfaron fanfaron = (Fanfaron) request.getAttribute("fanfaron");
    if (fanfaron == null) {
            fanfaron = (Fanfaron) session.getAttribute("fanfaron");
    }
    if (fanfaron == null) {
            fanfaron = (Fanfaron) session.getAttribute("utilisateur");
    }
    if (fanfaron == null) {
            response.sendRedirect("connexion");
            return;
    }

    List<Instrument> instruments =
            (List<Instrument>) request.getAttribute("instruments");

    List<GroupeFanfare> groupes =
            (List<GroupeFanfare>) request.getAttribute("groupes");

    List<Long> instrumentIdsChoisis =
            (List<Long>) request.getAttribute("instrumentIdsChoisis");

    List<Long> groupeIdsChoisis =
            (List<Long>) request.getAttribute("groupeIdsChoisis");
%>

<!DOCTYPE html>
<html lang="fr">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FanfareHub - Mes groupes</title>

    <%-- Styles locaux de la page Mes groupes --%>
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
        }

        h1,
        h2 {
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
            font-weight: 600;
        }

        .logout {
            background: var(--accent);
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

        .card {
            width: min(700px, calc(100% - 48px));
            margin: 32px auto;
            background: var(--card);
            border: 1px solid var(--border);
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(31, 27, 22, 0.15);
            overflow: hidden;
        }

        .content {
            padding: 32px;
        }

        h2 {
            margin-top: 0;
        }

        .section {
            margin-bottom: 32px;
        }

        .checkbox-group {
            display: grid;
            gap: 10px;
        }

        label {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 10px 12px;
            border: 1px solid var(--border);
            border-radius: 8px;
            background-color: #faf7f2;
        }

        input[type="checkbox"] {
            transform: scale(1.1);
        }

        button {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 8px;
            background-color: var(--accent);
            color: white;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
        }

        button:hover {
            background-color: var(--accent-strong);
        }

        .message {
            padding: 12px;
            margin-bottom: 20px;
            border-radius: 6px;
            font-weight: 600;
        }

        .success {
            background-color: #c8e6c9;
            color: #2e7d32;
        }

        .error {
            background-color: #ffebee;
            color: #c62828;
        }

        .admin-panel {
            border-top: 1px solid var(--border);
            margin-top: 32px;
            padding-top: 28px;
        }

        .admin-grid {
            display: grid;
            gap: 24px;
        }

        .admin-add-form {
            display: grid;
            grid-template-columns: 1fr auto;
            gap: 10px;
            margin-bottom: 14px;
        }

        .admin-list {
            display: grid;
            gap: 10px;
        }

        .admin-row {
            display: grid;
            grid-template-columns: 1fr auto auto;
            gap: 8px;
            align-items: center;
        }

        .admin-edit-form,
        .delete-form {
            display: contents;
        }

        input[type="text"] {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid var(--border);
            border-radius: 8px;
            font-family: inherit;
            font-size: 14px;
        }

        .admin-panel button {
            width: auto;
            white-space: nowrap;
        }

        .btn-delete {
            background-color: #c62828;
        }

        .btn-delete:hover {
            background-color: #a31818;
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

<div class="card">

    <div class="content">

        <%-- Messages de retour apres sauvegarde ou action admin --%>
        <% if ("1".equals(request.getParameter("success"))) { %>
            <div class="message success">Vos choix ont été enregistrés.</div>
        <% } else if ("admin".equals(request.getParameter("success"))) { %>
            <div class="message success">Modification enregistrée.</div>
        <% } else if ("admin".equals(request.getParameter("error"))) { %>
            <div class="message error">Impossible d'effectuer cette modification.</div>
        <% } else if ("forbidden".equals(request.getParameter("error"))) { %>
            <div class="message error">Action réservée aux administrateurs.</div>
        <% } else if (request.getAttribute("error") != null) { %>
            <div class="message error"><%= h(request.getAttribute("error")) %></div>
        <% } %>

        <form method="POST"
              action="<%= request.getContextPath() %>/mes-groupes">

            <%-- Choix personnels : instruments joues --%>
            <div class="section">
                <h2>Mes instruments</h2>

                <div class="checkbox-group">

                    <% for (Instrument instrument : instruments) { %>

                        <label>

                            <input
                                    type="checkbox"
                                    name="instruments"
                                    value="<%= instrument.getId() %>"

                                    <%= instrumentIdsChoisis.contains(instrument.getId())
                                            ? "checked"
                                            : "" %>
                            >

                            <%= h(instrument.getNom()) %>

                        </label>

                    <% } %>

                </div>
            </div>

            <%-- Choix personnels : groupes d'appartenance --%>
            <div class="section">
                <h2>Mes groupes</h2>

                <div class="checkbox-group">

                    <% for (GroupeFanfare groupe : groupes) { %>

                        <label>

                            <input
                                    type="checkbox"
                                    name="groupes"
                                    value="<%= groupe.getId() %>"

                                    <%= groupeIdsChoisis.contains(groupe.getId())
                                            ? "checked"
                                            : "" %>
                            >

                            <%= h(groupe.getNom()) %>

                        </label>

                    <% } %>

                </div>
            </div>

            <button type="submit">
                Enregistrer mes choix
            </button>

        </form>

        <%-- Panneau reserve aux administrateurs pour maintenir les listes de reference --%>
        <% if (fanfaron.getAdmin()) { %>
            <div class="admin-panel">
                <h2>Administration des instruments et groupes</h2>

                <div class="admin-grid">
                    <section>
                        <h3>Instruments</h3>

                        <form class="admin-add-form" method="POST" action="<%= request.getContextPath() %>/mes-groupes">
                            <input type="hidden" name="action" value="addInstrument">
                            <input type="text" name="nom" placeholder="Nouvel instrument" required>
                            <button type="submit">Ajouter</button>
                        </form>

                        <div class="admin-list">
                            <% for (Instrument instrument : instruments) { %>
                                <div class="admin-row">
                                    <form class="admin-edit-form" method="POST" action="<%= request.getContextPath() %>/mes-groupes">
                                        <input type="hidden" name="action" value="updateInstrument">
                                        <input type="hidden" name="id" value="<%= instrument.getId() %>">
                                        <input type="text" name="nom" value="<%= h(instrument.getNom()) %>" required>
                                        <button type="submit">Renommer</button>
                                    </form>

                                    <form class="delete-form" method="POST" action="<%= request.getContextPath() %>/mes-groupes">
                                        <input type="hidden" name="action" value="deleteInstrument">
                                        <input type="hidden" name="id" value="<%= instrument.getId() %>">
                                        <button class="btn-delete" type="submit"
                                                onclick="return confirm('Supprimer cet instrument ?')">Supprimer</button>
                                    </form>
                                </div>
                            <% } %>
                        </div>
                    </section>

                    <section>
                        <h3>Groupes</h3>

                        <form class="admin-add-form" method="POST" action="<%= request.getContextPath() %>/mes-groupes">
                            <input type="hidden" name="action" value="addGroupe">
                            <input type="text" name="nom" placeholder="Nouveau groupe" required>
                            <button type="submit">Ajouter</button>
                        </form>

                        <div class="admin-list">
                            <% for (GroupeFanfare groupe : groupes) { %>
                                <div class="admin-row">
                                    <form class="admin-edit-form" method="POST" action="<%= request.getContextPath() %>/mes-groupes">
                                        <input type="hidden" name="action" value="updateGroupe">
                                        <input type="hidden" name="id" value="<%= groupe.getId() %>">
                                        <input type="text" name="nom" value="<%= h(groupe.getNom()) %>" required>
                                        <button type="submit">Renommer</button>
                                    </form>

                                    <form class="delete-form" method="POST" action="<%= request.getContextPath() %>/mes-groupes">
                                        <input type="hidden" name="action" value="deleteGroupe">
                                        <input type="hidden" name="id" value="<%= groupe.getId() %>">
                                        <button class="btn-delete" type="submit"
                                                onclick="return confirm('Supprimer ce groupe ?')">Supprimer</button>
                                    </form>
                                </div>
                            <% } %>
                        </div>
                    </section>
                </div>
            </div>
        <% } %>

    </div>

</div>

</body>
</html>
