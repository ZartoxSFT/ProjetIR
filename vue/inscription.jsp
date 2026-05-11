<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="java.util.Map" %>
    <%!
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
        <!DOCTYPE html>
        <html lang="fr">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>FanfareHub - Inscription</title>
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
                    display: grid;
                    place-items: center;
                    padding: 24px;
                }

                .card {
                    width: min(600px, 100%);
                    background: var(--card);
                    border: 1px solid var(--border);
                    border-radius: 16px;
                    box-shadow: 0 10px 30px rgba(31, 27, 22, 0.15);
                    overflow: hidden;
                }

                .header {
                    padding: 22px 24px;
                    border-bottom: 1px solid var(--border);
                    background: linear-gradient(120deg, #f6ecde, #fdfaf4);
                }

                .header h1 {
                    margin: 0;
                    font-size: 24px;
                    color: var(--accent);
                }

                .content {
                    padding: 32px;
                    max-height: 70vh;
                    overflow-y: auto;
                }

                .form-row {
                    display: grid;
                    grid-template-columns: 1fr 1fr;
                    gap: 16px;
                }

                .form-row.full {
                    grid-template-columns: 1fr;
                }

                .form-group {
                    margin-bottom: 20px;
                }

                label {
                    display: block;
                    margin-bottom: 8px;
                    font-weight: 500;
                    color: var(--text);
                }

                input[type="text"],
                input[type="email"],
                input[type="password"],
                select {
                    width: 100%;
                    padding: 10px 12px;
                    border: 1px solid var(--border);
                    border-radius: 8px;
                    font-size: 14px;
                    transition: border-color 0.3s;
                    font-family: inherit;
                }

                input:focus,
                select:focus {
                    outline: none;
                    border-color: var(--accent);
                    box-shadow: 0 0 0 3px rgba(177, 68, 47, 0.1);
                }

                .error-message {
                    color: var(--error);
                    font-size: 12px;
                    margin-top: 4px;
                }

                .error {
                    background-color: #ffebee;
                    border-left: 4px solid var(--error);
                    color: var(--error);
                    padding: 12px;
                    margin-bottom: 20px;
                    border-radius: 4px;
                    font-size: 14px;
                }

                button {
                    width: 100%;
                    padding: 12px;
                    background-color: var(--accent);
                    color: white;
                    border: none;
                    border-radius: 8px;
                    font-size: 16px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: background-color 0.3s;
                }

                button:hover {
                    background-color: var(--accent-strong);
                }

                .footer {
                    padding: 16px 24px;
                    background-color: #faf7f2;
                    text-align: center;
                    font-size: 14px;
                    color: var(--muted);
                    border-top: 1px solid var(--border);
                }

                .footer a {
                    color: var(--accent);
                    text-decoration: none;
                    font-weight: 600;
                }

                .footer a:hover {
                    text-decoration: underline;
                }
            </style>
        </head>

        <body>
            <div class="card">
                <div class="header">
                    <h1>🎺 FanfareHub</h1>
                </div>
                <div class="content">
                    <h2 style="text-align: center; color: var(--accent); margin-top: 0;">Inscription</h2>

                    <% java.util.Map<String, String> erreurs = (java.util.Map<String, String>)
                            request.getAttribute("erreurs");
                            if (request.getAttribute("success") != null) { %>
                            <div
                                style="background:#e6ffed;border-left:4px solid #28a745;color:#155724;padding:12px;margin-bottom:20px;border-radius:4px;">
                                <%= h(request.getAttribute("success")) %>
                            </div>
                            <% } %>
                                <% if (erreurs !=null && !erreurs.isEmpty()) { for (String erreur : erreurs.values()) {
                                    %>
                                    <div class="error">
                                        <%= h(erreur) %>
                                    </div>
                                    <% } } %>

                                        <form method="POST" action="inscription">
                                            <div class="form-row full">
                                                <div class="form-group">
                                                    <label for="nomFanfaron">Nom d'utilisateur *</label>
                                                    <input type="text" id="nomFanfaron" name="nomFanfaron" required
                                                        value="<%= h(request.getAttribute("nomFanfaron")) %>">
                                                    <% if (erreurs !=null && erreurs.containsKey("nomFanfaron")) { %>
                                                        <div class="error-message">
                                                            <%= h(erreurs.get("nomFanfaron")) %>
                                                        </div>
                                                        <% } %>
                                                </div>
                                            </div>

                                            <div class="form-row full">
                                                <div class="form-group">
                                                    <label for="email">Email *</label>
                                                    <input type="email" id="email" name="email" required
                                                        value="<%= h(request.getAttribute("email")) %>">
                                                    <% if (erreurs !=null && erreurs.containsKey("email")) { %>
                                                        <div class="error-message">
                                                            <%= h(erreurs.get("email")) %>
                                                        </div>
                                                        <% } %>
                                                </div>
                                            </div>

                                            <div class="form-row full">
                                                <div class="form-group">
                                                    <label for="emailConfirm">Confirmer l'email *</label>
                                                    <input type="email" id="emailConfirm" name="emailConfirm" required>
                                                    <% if (erreurs !=null && erreurs.containsKey("emailConfirm")) { %>
                                                        <div class="error-message">
                                                            <%= h(erreurs.get("emailConfirm")) %>
                                                        </div>
                                                        <% } %>
                                                </div>
                                            </div>

                                            <div class="form-row full">
                                                <div class="form-group">
                                                    <label for="motDePasse">Mot de passe *</label>
                                                    <input type="password" id="motDePasse" name="motDePasse" required>
                                                    <% if (erreurs !=null && erreurs.containsKey("motDePasse")) { %>
                                                        <div class="error-message">
                                                            <%= h(erreurs.get("motDePasse")) %>
                                                        </div>
                                                        <% } %>
                                                </div>
                                            </div>

                                            <div class="form-row full">
                                                <div class="form-group">
                                                    <label for="motDePasseConfirm">Confirmer le mot de passe *</label>
                                                    <input type="password" id="motDePasseConfirm"
                                                        name="motDePasseConfirm" required>
                                                    <% if (erreurs !=null && erreurs.containsKey("motDePasseConfirm")) {
                                                        %>
                                                        <div class="error-message">
                                                            <%= h(erreurs.get("motDePasseConfirm")) %>
                                                        </div>
                                                        <% } %>
                                                </div>
                                            </div>

                                            <div class="form-row">
                                                <div class="form-group">
                                                    <label for="prenom">Prénom *</label>
                                                    <input type="text" id="prenom" name="prenom" required
                                                        value="<%= h(request.getAttribute("prenom")) %>">
                                                </div>
                                                <div class="form-group">
                                                    <label for="nom">Nom *</label>
                                                    <input type="text" id="nom" name="nom" required
                                                        value="<%= h(request.getAttribute("nom")) %>">
                                                </div>
                                            </div>

                                            <div class="form-row">
                                                <div class="form-group">
                                                    <label for="genre">Genre *</label>
                                                    <select id="genre" name="genre" required>
                                                        <option value="">-- Sélectionner --</option>
                                                        <option value="homme" <%="homme"
                                                            .equals(request.getAttribute("genre")) ? "selected" : "" %>
                                                            >Homme</option>
                                                        <option value="femme" <%="femme"
                                                            .equals(request.getAttribute("genre")) ? "selected" : "" %>
                                                            >Femme</option>
                                                        <option value="autre" <%="autre"
                                                            .equals(request.getAttribute("genre")) ? "selected" : "" %>
                                                            >Autre</option>
                                                    </select>
                                                </div>
                                                <div class="form-group">
                                                    <label for="contraintesAlimentaires">Contraintes
                                                        alimentaires</label>
                                                    <select id="contraintesAlimentaires" name="contraintesAlimentaires">
                                                        <option value="aucune" <%="aucune"
                                                            .equals(request.getAttribute("contraintesAlimentaires"))
                                                            ? "selected" : "" %>>Aucune</option>
                                                        <option value="vegetarien" <%="vegetarien"
                                                            .equals(request.getAttribute("contraintesAlimentaires"))
                                                            ? "selected" : "" %>>Végétarien</option>
                                                        <option value="vegan" <%="vegan"
                                                            .equals(request.getAttribute("contraintesAlimentaires"))
                                                            ? "selected" : "" %>>Vegan</option>
                                                        <option value="sans porc" <%="sans porc"
                                                            .equals(request.getAttribute("contraintesAlimentaires"))
                                                            ? "selected" : "" %>>Sans porc</option>
                                                    </select>
                                                </div>
                                            </div>

                                            <button type="submit">S'inscrire</button>
                                        </form>
                </div>
                <div class="footer">
                    Vous avez un compte ? <a href="connexion">Se connecter</a>
                </div>
            </div>
        </body>

        </html>
