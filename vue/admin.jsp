<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="modele.Fanfaron" %>
        <%@ page import="java.util.List" %>
            <!DOCTYPE html>
            <html lang="fr">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>FanfareHub - Administration</title>
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
                        --error: #d32f2f;
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
                    }

                    h1 {
                        margin: 0;
                        color: var(--accent);
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
                        background: var(--success);
                        color: white;
                        padding: 4px 8px;
                        border-radius: 4px;
                        font-size: 12px;
                        font-weight: 700;
                    }

                    .container {
                        max-width: 1200px;
                        margin: 32px auto;
                        padding: 0 24px;
                    }

                    .success {
                        background-color: #c8e6c9;
                        border-left: 4px solid var(--success);
                        color: var(--success);
                        padding: 12px;
                        margin-bottom: 20px;
                        border-radius: 4px;
                    }

                    .error {
                        background-color: #ffebee;
                        border-left: 4px solid var(--error);
                        color: var(--error);
                        padding: 12px;
                        margin-bottom: 20px;
                        border-radius: 4px;
                    }

                    .section {
                        background: var(--card);
                        border: 1px solid var(--border);
                        border-radius: 16px;
                        padding: 24px;
                        margin-bottom: 24px;
                        box-shadow: 0 10px 30px rgba(31, 27, 22, 0.15);
                    }

                    .section h2 {
                        margin-top: 0;
                        border-bottom: 2px solid var(--border);
                        padding-bottom: 12px;
                    }

                    .form-group {
                        margin-bottom: 16px;
                    }

                    label {
                        display: block;
                        margin-bottom: 6px;
                        font-weight: 500;
                    }

                    input[type="text"],
                    input[type="email"],
                    input[type="password"],
                    select {
                        width: 100%;
                        padding: 8px 12px;
                        border: 1px solid var(--border);
                        border-radius: 6px;
                        font-size: 14px;
                        font-family: inherit;
                    }

                    input:focus,
                    select:focus {
                        outline: none;
                        border-color: var(--accent);
                        box-shadow: 0 0 0 3px rgba(177, 68, 47, 0.1);
                    }

                    .form-row {
                        display: grid;
                        grid-template-columns: 1fr 1fr;
                        gap: 16px;
                    }

                    button {
                        padding: 10px 16px;
                        background-color: var(--accent);
                        color: white;
                        border: none;
                        border-radius: 6px;
                        font-weight: 600;
                        cursor: pointer;
                        transition: background-color 0.3s;
                    }

                    button:hover {
                        background-color: var(--accent-strong);
                    }

                    .btn-secondary {
                        background-color: #757575;
                    }

                    .btn-secondary:hover {
                        background-color: #616161;
                    }

                    .btn-delete {
                        background-color: var(--error);
                    }

                    .btn-delete:hover {
                        background-color: #b71c1c;
                    }

                    table {
                        width: 100%;
                        border-collapse: collapse;
                        margin-top: 20px;
                    }

                    thead {
                        background-color: #f5f5f5;
                    }

                    th {
                        padding: 12px;
                        text-align: left;
                        font-weight: 600;
                        border-bottom: 2px solid var(--border);
                    }

                    td {
                        padding: 12px;
                        border-bottom: 1px solid var(--border);
                    }

                    tr:hover {
                        background-color: #faf7f2;
                    }

                    .actions {
                        display: flex;
                        gap: 8px;
                    }

                    .actions a,
                    .actions form {
                        display: inline;
                    }

                    .actions button {
                        padding: 6px 12px;
                        font-size: 12px;
                    }

                    .role-badge {
                        padding: 4px 8px;
                        border-radius: 4px;
                        font-size: 12px;
                        font-weight: 600;
                        background-color: #e0e0e0;
                        display: inline-block;
                    }

                    .role-badge.admin {
                        background-color: var(--success);
                        color: white;
                    }

                    .role-badge.user {
                        background-color: #90caf9;
                        color: white;
                    }
                </style>
            </head>

            <body>
                <% Fanfaron utilisateur=(Fanfaron) session.getAttribute("utilisateur"); if (utilisateur==null ||
                    !utilisateur.getAdmin()) { response.sendRedirect("connexion"); return; }
                    Fanfaron fanfaron=utilisateur; %>

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

                    <div class="container">
                        <% if (request.getAttribute("succes") !=null) { %>
                            <div class="success">
                                <%= request.getAttribute("succes") %>
                            </div>
                            <% } %>

                                <% if (request.getAttribute("erreur") !=null) { %>
                                    <div class="error">
                                        <%= request.getAttribute("erreur") %>
                                    </div>
                                    <% } %>

                                        <!-- Section Ajouter un utilisateur -->
                                        <div class="section">
                                            <h2>➕ Ajouter un nouveau fanfaron</h2>
                                            <form method="POST" action="admin?action=add">
                                                <input type="hidden" name="action" value="add">
                                                <div class="form-row">
                                                    <div class="form-group">
                                                        <label for="nomFanfaron">Nom d'utilisateur *</label>
                                                        <input type="text" id="nomFanfaron" name="nomFanfaron" required>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="email">Email *</label>
                                                        <input type="email" id="email" name="email" required>
                                                    </div>
                                                </div>
                                                <div class="form-row">
                                                    <div class="form-group">
                                                        <label for="prenom">Prénom *</label>
                                                        <input type="text" id="prenom" name="prenom" required>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="nom">Nom *</label>
                                                        <input type="text" id="nom" name="nom" required>
                                                    </div>
                                                </div>
                                                <div class="form-row">
                                                    <div class="form-group">
                                                        <label for="motDePasse">Mot de passe *</label>
                                                        <input type="password" id="motDePasse" name="motDePasse"
                                                            required>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="genre">Genre *</label>
                                                        <select id="genre" name="genre" required>
                                                            <option value="">-- Sélectionner --</option>
                                                            <option value="homme">Homme</option>
                                                            <option value="femme">Femme</option>
                                                            <option value="autre">Autre</option>
                                                        </select>
                                                    </div>
                                                </div>
                                                <div class="form-row">
                                                    <div class="form-group">
                                                        <label for="contraintesAlimentaires">Contraintes
                                                            alimentaires</label>
                                                        <select id="contraintesAlimentaires"
                                                            name="contraintesAlimentaires">
                                                            <option value="aucune">Aucune</option>
                                                            <option value="vegetarien">Végétarien</option>
                                                            <option value="vegan">Vegan</option>
                                                            <option value="sans porc">Sans porc</option>
                                                        </select>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="isAdmin">
                                                            <input type="checkbox" id="isAdmin" name="isAdmin"
                                                                value="on">
                                                            Administrateur
                                                        </label>
                                                    </div>
                                                </div>
                                                <button type="submit">Ajouter le fanfaron</button>
                                            </form>
                                        </div>

                                        <!-- Section Modification (si un fanfaron est sélectionné) -->
                                        <% Fanfaron fanfaronEdit=(Fanfaron) request.getAttribute("fanfaron"); if
                                            (fanfaronEdit !=null) { %>
                                            <div class="section" style="border-left: 4px solid var(--accent);">
                                                <h2>✏️ Modifier le fanfaron</h2>
                                                <form method="POST" action="admin?action=update">
                                                    <input type="hidden" name="action" value="update">
                                                    <input type="hidden" name="id" value="<%= fanfaronEdit.getId() %>">
                                                    <div class="form-row">
                                                        <div class="form-group">
                                                            <label>Nom d'utilisateur</label>
                                                            <input type="text"
                                                                value="<%= fanfaronEdit.getNomFanfaron() %>" disabled
                                                                style="background:#f5f5f5;">
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="emailEdit">Email</label>
                                                            <input type="email" id="emailEdit" name="email"
                                                                value="<%= fanfaronEdit.getEmail() %>" required>
                                                        </div>
                                                    </div>
                                                    <div class="form-row">
                                                        <div class="form-group">
                                                            <label for="prenomEdit">Prénom</label>
                                                            <input type="text" id="prenomEdit" name="prenom"
                                                                value="<%= fanfaronEdit.getPrenom() %>" required>
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="nomEdit">Nom</label>
                                                            <input type="text" id="nomEdit" name="nom"
                                                                value="<%= fanfaronEdit.getNom() %>" required>
                                                        </div>
                                                    </div>
                                                    <div class="form-row">
                                                        <div class="form-group">
                                                            <label for="genreEdit">Genre</label>
                                                            <select id="genreEdit" name="genre" required>
                                                                <option value="homme" <%="homme"
                                                                    .equals(fanfaronEdit.getGenre()) ? "selected" : ""
                                                                    %>>Homme</option>
                                                                <option value="femme" <%="femme"
                                                                    .equals(fanfaronEdit.getGenre()) ? "selected" : ""
                                                                    %>>Femme</option>
                                                                <option value="autre" <%="autre"
                                                                    .equals(fanfaronEdit.getGenre()) ? "selected" : ""
                                                                    %>>Autre</option>
                                                            </select>
                                                        </div>
                                                        <div class="form-group">
                                                            <label for="rolesEdit">
                                                                <input type="checkbox" id="rolesEdit" name="isAdmin"
                                                                    value="on" <%=fanfaronEdit.getAdmin() ? "checked"
                                                                    : "" %>>
                                                                Administrateur
                                                            </label>
                                                        </div>
                                                    </div>
                                                    <div class="form-row">
                                                        <div class="form-group">
                                                            <label for="contraintesEdit">Contraintes
                                                                alimentaires</label>
                                                            <select id="contraintesEdit" name="contraintesAlimentaires">
                                                                <option value="aucune" <%="aucune"
                                                                    .equals(fanfaronEdit.getContraintesAlimentaires())
                                                                    ? "selected" : "" %>>Aucune</option>
                                                                <option value="vegetarien" <%="vegetarien"
                                                                    .equals(fanfaronEdit.getContraintesAlimentaires())
                                                                    ? "selected" : "" %>>Végétarien</option>
                                                                <option value="vegan" <%="vegan"
                                                                    .equals(fanfaronEdit.getContraintesAlimentaires())
                                                                    ? "selected" : "" %>>Vegan</option>
                                                                <option value="sans porc" <%="sans porc"
                                                                    .equals(fanfaronEdit.getContraintesAlimentaires())
                                                                    ? "selected" : "" %>>Sans porc</option>
                                                            </select>
                                                        </div>
                                                    </div>
                                                    <button type="submit"
                                                        style="width:48%;display:inline-block;">Enregistrer les
                                                        modifications</button>
                                                    <a href="admin"
                                                        style="width:48%;display:inline-block;margin-left:2%;"><button
                                                            type="button" class="btn-secondary"
                                                            style="width:100%;">Annuler</button></a>
                                                </form>
                                            </div>
                                            <% } %>

                                                <!-- Section Liste des utilisateurs -->
                                                <div class="section">
                                                    <h2>👥 Liste des fanfarons</h2>
                                                    <table>
                                                        <thead>
                                                            <tr>
                                                                <th>Nom d'utilisateur</th>
                                                                <th>Email</th>
                                                                <th>Prénom</th>
                                                                <th>Nom</th>
                                                                <th>Rôle</th>
                                                                <th>Actions</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <% List<Fanfaron> fanfarons = (List<Fanfaron>)
                                                                    request.getAttribute("fanfarons");
                                                                    if (fanfarons != null) {
                                                                    for (Fanfaron fan : fanfarons) {
                                                                    %>
                                                                    <tr>
                                                                        <td>
                                                                            <%= fan.getNomFanfaron() %>
                                                                        </td>
                                                                        <td>
                                                                            <%= fan.getEmail() %>
                                                                        </td>
                                                                        <td>
                                                                            <%= fan.getPrenom() %>
                                                                        </td>
                                                                        <td>
                                                                            <%= fan.getNom() %>
                                                                        </td>
                                                                        <td>
                                                                            <span class='role-badge <%= fan.getAdmin() ? "admin" : "user" %>'>
                                                                                <%= fan.getAdmin() ? "Admin"
                                                                                    : "Utilisateur" %>
                                                                            </span>
                                                                        </td>
                                                                        <td>
                                                                            <div class="actions">
                                                                                <a
                                                                                    href="admin?action=edit&id=<%= fan.getId() %>">
                                                                                    <button type="button"
                                                                                        class="btn-secondary">Modifier</button>
                                                                                </a>
                                                                                <form method="GET" action="admin"
                                                                                    style="display:inline;">
                                                                                    <input type="hidden" name="action"
                                                                                        value="delete">
                                                                                    <input type="hidden" name="id"
                                                                                        value="<%= fan.getId() %>">
                                                                                    <button type="submit"
                                                                                        class="btn-delete"
                                                                                        onclick="return confirm('Êtes-vous sûr ?')">Supprimer</button>
                                                                                </form>
                                                                            </div>
                                                                        </td>
                                                                    </tr>
                                                                    <% } } %>
                                                        </tbody>
                                                    </table>
                                                </div>
                    </div>
            </body>

            </html>
