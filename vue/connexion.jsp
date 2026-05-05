<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <!DOCTYPE html>
    <html lang="fr">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>FanfareHub - Connexion</title>
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
                display: grid;
                place-items: center;
                padding: 24px;
            }

            .card {
                width: min(520px, 100%);
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
            input[type="password"] {
                width: 100%;
                padding: 10px 12px;
                border: 1px solid var(--border);
                border-radius: 8px;
                font-size: 14px;
                transition: border-color 0.3s;
            }

            input[type="text"]:focus,
            input[type="password"]:focus {
                outline: none;
                border-color: var(--accent);
                box-shadow: 0 0 0 3px rgba(177, 68, 47, 0.1);
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
                <h2 style="text-align: center; color: var(--accent); margin-top: 0;">Connexion</h2>

                <% if (request.getAttribute("erreur") !=null) { %>
                    <div class="error">
                        <%= request.getAttribute("erreur") %>
                    </div>
                    <% } %>

                        <form method="POST" action="connexion">
                            <div class="form-group">
                                <label for="nomFanfaron">Nom d'utilisateur</label>
                                <input type="text" id="nomFanfaron" name="nomFanfaron" required
                                    value="<%= request.getParameter("nomFanfaron") !=null ?
                                    request.getParameter("nomFanfaron") : "" %>">
                            </div>

                            <div class="form-group">
                                <label for="motDePasse">Mot de passe</label>
                                <input type="password" id="motDePasse" name="motDePasse" required>
                            </div>

                            <button type="submit">Se connecter</button>
                        </form>
            </div>
            <div class="footer">
                Pas encore de compte ? <a href="inscription">S'inscrire</a>
            </div>
        </div>
    </body>

    </html>
