<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="java.util.List" %>
        <%@ page import="modele.Evenement" %>
            <%@ page import="modele.Fanfaron" %>
                <%@ page import="modele.Instrument" %>
                    <%@ page import="modele.InscriptionDetail" %>

                        <% Fanfaron fanfaron=(Fanfaron) request.getAttribute("fanfaron"); if (fanfaron==null) {
                            fanfaron=(Fanfaron) session.getAttribute("fanfaron"); } if (fanfaron==null) {
                            fanfaron=(Fanfaron) session.getAttribute("utilisateur"); } if (fanfaron==null) {
                            response.sendRedirect("connexion"); return; } List<Evenement> evenements = (List<Evenement>)
                                request.getAttribute("evenements");
                                Evenement evenementSelectionne = (Evenement)
                                request.getAttribute("evenementSelectionne");
                                List<Instrument> instruments = (List<Instrument>) request.getAttribute("instruments");
                                        List<InscriptionDetail> inscriptions = (List<InscriptionDetail>)
                                                request.getAttribute("inscriptions");
                                                Boolean peutProposer = (Boolean) request.getAttribute("peutProposer");
                                                boolean canPropose = peutProposer != null &&
                                                peutProposer.booleanValue();
                                                %>

                                                <!DOCTYPE html>
                                                <html lang="fr">

                                                <head>
                                                    <meta charset="UTF-8">
                                                    <meta name="viewport"
                                                        content="width=device-width, initial-scale=1.0">
                                                    <title>FanfareHub - Evenements</title>
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
                                                            --present: #2e7d32;
                                                            --absent: #c62828;
                                                            --incertain: #ef6c00;
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
                                                            background: var(--success);
                                                            color: white;
                                                            padding: 4px 8px;
                                                            border-radius: 4px;
                                                            font-size: 12px;
                                                            font-weight: 700;
                                                        }

                                                        .container {
                                                            max-width: 1100px;
                                                            margin: 32px auto;
                                                            padding: 0 24px;
                                                        }

                                                        .card {
                                                            background: var(--card);
                                                            border: 1px solid var(--border);
                                                            border-radius: 16px;
                                                            padding: 24px;
                                                            box-shadow: 0 10px 30px rgba(31, 27, 22, 0.15);
                                                            margin-bottom: 24px;
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
                                                            margin-bottom: 16px;
                                                        }

                                                        label {
                                                            display: block;
                                                            margin-bottom: 6px;
                                                            font-weight: 600;
                                                        }

                                                        input[type="text"],
                                                        input[type="datetime-local"],
                                                        input[type="number"],
                                                        select,
                                                        textarea {
                                                            width: 100%;
                                                            padding: 10px 12px;
                                                            border: 1px solid var(--border);
                                                            border-radius: 8px;
                                                            font-size: 14px;
                                                            font-family: inherit;
                                                        }

                                                        textarea {
                                                            min-height: 90px;
                                                            resize: vertical;
                                                        }

                                                        button {
                                                            padding: 10px 16px;
                                                            background-color: var(--accent);
                                                            color: white;
                                                            border: none;
                                                            border-radius: 8px;
                                                            font-weight: 600;
                                                            cursor: pointer;
                                                        }

                                                        button:hover {
                                                            background-color: var(--accent-strong);
                                                        }

                                                        .btn-delete {
                                                            background-color: var(--error);
                                                            margin-top: 8px;
                                                        }

                                                        .btn-delete:hover {
                                                            background-color: #b71c1c;
                                                        }

                                                        table {
                                                            width: 100%;
                                                            border-collapse: collapse;
                                                            margin-top: 16px;
                                                        }

                                                        thead {
                                                            background-color: #f5f5f5;
                                                        }

                                                        th,
                                                        td {
                                                            text-align: left;
                                                            padding: 12px;
                                                            border-bottom: 1px solid var(--border);
                                                        }

                                                        .success {
                                                            background-color: #c8e6c9;
                                                            border-left: 4px solid var(--success);
                                                            color: var(--success);
                                                            padding: 12px;
                                                            border-radius: 6px;
                                                            margin-bottom: 16px;
                                                        }

                                                        .error {
                                                            background-color: #ffebee;
                                                            border-left: 4px solid var(--error);
                                                            color: var(--error);
                                                            padding: 12px;
                                                            border-radius: 6px;
                                                            margin-bottom: 16px;
                                                        }

                                                        .statut {
                                                            display: inline-flex;
                                                            align-items: center;
                                                            padding: 4px 10px;
                                                            border-radius: 999px;
                                                            font-size: 12px;
                                                            font-weight: 700;
                                                            color: white;
                                                            text-transform: uppercase;
                                                            letter-spacing: 0.5px;
                                                        }

                                                        .statut-present {
                                                            background: var(--present);
                                                        }

                                                        .statut-absent {
                                                            background: var(--absent);
                                                        }

                                                        .statut-incertain {
                                                            background: var(--incertain);
                                                        }

                                                        .muted {
                                                            color: var(--muted);
                                                        }

                                                        .link-action {
                                                            color: var(--accent);
                                                            font-weight: 600;
                                                            text-decoration: none;
                                                        }

                                                        .link-action:hover {
                                                            text-decoration: underline;
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
                                                            <h2>Proposer un evenement</h2>

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

                                                                            <% if (!canPropose) { %>
                                                                                <p class="muted">Seuls les membres de la
                                                                                    commission prestation peuvent
                                                                                    proposer un evenement.</p>
                                                                                <% } else { %>
                                                                                    <form method="POST"
                                                                                        action="evenement">
                                                                                        <input type="hidden"
                                                                                            name="action"
                                                                                            value="add-evenement">
                                                                                        <div class="form-row">
                                                                                            <div class="form-group">
                                                                                                <label for="nom">Nom
                                                                                                    *</label>
                                                                                                <select
                                                                                                    class="form-control"
                                                                                                    id="nom" name="nom"
                                                                                                    required>
                                                                                                    <option value="">--
                                                                                                        Selectionner --
                                                                                                    </option>
                                                                                                    <option
                                                                                                        value="atelier">
                                                                                                        Atelier</option>
                                                                                                    <option
                                                                                                        value="repetition">
                                                                                                        Repetition
                                                                                                    </option>
                                                                                                    <option
                                                                                                        value="prestation">
                                                                                                        Prestation
                                                                                                    </option>
                                                                                            </div>
                                                                                        </div>
                                                                                        <div class="form-row">
                                                                                            <div class="form-group">
                                                                                                <label
                                                                                                    for="horodatage">Horodatage
                                                                                                    *</label>
                                                                                                <input
                                                                                                    type="datetime-local"
                                                                                                    id="horodatage"
                                                                                                    name="horodatage"
                                                                                                    required>
                                                                                            </div>
                                                                                            <div class="form-group">
                                                                                                <label for="duree">Duree
                                                                                                    (minutes) *</label>
                                                                                                <input type="number"
                                                                                                    id="duree"
                                                                                                    name="duree" min="1"
                                                                                                    required>
                                                                                            </div>
                                                                                        </div>
                                                                                        <div class="form-row">
                                                                                            <div class="form-group">
                                                                                                <label for="lieu">Lieu
                                                                                                    *</label>
                                                                                                <input type="text"
                                                                                                    id="lieu"
                                                                                                    name="lieu"
                                                                                                    required>
                                                                                            </div>
                                                                                            <div class="form-group">
                                                                                                <label
                                                                                                    for="description">Description</label>
                                                                                                <textarea
                                                                                                    id="description"
                                                                                                    name="description"></textarea>
                                                                                            </div>
                                                                                        </div>
                                                                                        <button
                                                                                            type="submit">Ajouter</button>
                                                                                    </form>
                                                                                    <% } %>
                                                        </section>

                                                        <section class="card">
                                                            <h2>Evenements existants</h2>

                                                            <% if (evenements==null || evenements.isEmpty()) { %>
                                                                <p>Aucun evenement pour le moment.</p>
                                                                <% } else { %>
                                                                    <table>
                                                                        <thead>
                                                                            <tr>
                                                                                <th>Nom</th>
                                                                                <th>Date</th>
                                                                                <th>Duree</th>
                                                                                <th>Lieu</th>
                                                                                <th>Description</th>
                                                                                <th>Actions</th>
                                                                            </tr>
                                                                        </thead>
                                                                        <tbody>
                                                                            <% for (Evenement ev : evenements) { %>
                                                                                <tr>
                                                                                    <td>
                                                                                        <%= ev.getNom() %>
                                                                                    </td>
                                                                                    <td>
                                                                                        <%= ev.getHorodatage() %>
                                                                                    </td>
                                                                                    <td>
                                                                                        <%= ev.getDuree() %> min
                                                                                    </td>
                                                                                    <td>
                                                                                        <%= ev.getLieu() %>
                                                                                    </td>
                                                                                    <td>
                                                                                        <%= ev.getDescription()==null
                                                                                            ? "" : ev.getDescription()
                                                                                            %>
                                                                                    </td>
                                                                                    <td>
                                                                                        <a class="link-action"
                                                                                            href="evenement?evenementId=<%= ev.getId() %>">
                                                                                            Voir inscriptions
                                                                                        </a>
                                                                                        <% if (canPropose) { %>
                                                                                            <form method="POST"
                                                                                                action="evenement">
                                                                                                <input type="hidden"
                                                                                                    name="action"
                                                                                                    value="delete-evenement">
                                                                                                <input type="hidden"
                                                                                                    name="evenementId"
                                                                                                    value="<%= ev.getId() %>">
                                                                                                <button
                                                                                                    class="btn-delete"
                                                                                                    type="submit">Supprimer</button>
                                                                                            </form>
                                                                                            <% } %>
                                                                                    </td>
                                                                                </tr>
                                                                                <% } %>
                                                                        </tbody>
                                                                    </table>
                                                                    <% } %>
                                                        </section>

                                                        <section class="card">
                                                            <h2>Inscriptions</h2>

                                                            <% if (evenementSelectionne==null) { %>
                                                                <p class="muted">Selectionnez un evenement pour voir les
                                                                    inscriptions.</p>
                                                                <% } else { %>
                                                                    <p>
                                                                        <strong>Evenement :</strong>
                                                                        <%= evenementSelectionne.getNom() %>
                                                                            (<%= evenementSelectionne.getHorodatage() %>
                                                                                )
                                                                    </p>

                                                                    <% if (instruments==null || instruments.isEmpty()) {
                                                                        %>
                                                                        <p class="muted">Aucun instrument disponible
                                                                            pour l'inscription.</p>
                                                                        <% } else { %>
                                                                            <form method="POST" action="evenement">
                                                                                <input type="hidden" name="action"
                                                                                    value="inscription">
                                                                                <input type="hidden" name="evenementId"
                                                                                    value="<%= evenementSelectionne.getId() %>">
                                                                                <div class="form-row">
                                                                                    <div class="form-group">
                                                                                        <label
                                                                                            for="instrumentId">Instrument
                                                                                            *</label>
                                                                                        <select id="instrumentId"
                                                                                            name="instrumentId"
                                                                                            required>
                                                                                            <option value="">--
                                                                                                Selectionner --</option>
                                                                                            <% for (Instrument
                                                                                                instrument :
                                                                                                instruments) { %>
                                                                                                <option
                                                                                                    value="<%= instrument.getId() %>">
                                                                                                    <%= instrument.getNom()
                                                                                                        %>
                                                                                                </option>
                                                                                                <% } %>
                                                                                        </select>
                                                                                    </div>
                                                                                    <div class="form-group">
                                                                                        <label for="statut">Statut
                                                                                            *</label>
                                                                                        <select id="statut"
                                                                                            name="statut" required>
                                                                                            <option value="present">
                                                                                                Present</option>
                                                                                            <option value="incertain">
                                                                                                Incertain</option>
                                                                                            <option value="absent">
                                                                                                Absent</option>
                                                                                        </select>
                                                                                    </div>
                                                                                </div>
                                                                                <button
                                                                                    type="submit">S'inscrire</button>
                                                                            </form>
                                                                            <% } %>

                                                                                <% if (inscriptions==null ||
                                                                                    inscriptions.isEmpty()) { %>
                                                                                    <p class="muted">Aucune inscription
                                                                                        pour cet evenement.</p>
                                                                                    <% } else { %>
                                                                                        <table>
                                                                                            <thead>
                                                                                                <tr>
                                                                                                    <th>Instrument</th>
                                                                                                    <th>Fanfaron</th>
                                                                                                    <th>Statut</th>
                                                                                                </tr>
                                                                                            </thead>
                                                                                            <tbody>
                                                                                                <% for
                                                                                                    (InscriptionDetail
                                                                                                    detail :
                                                                                                    inscriptions) { %>
                                                                                                    <tr>
                                                                                                        <td>
                                                                                                            <%= detail.getInstrument()
                                                                                                                %>
                                                                                                        </td>
                                                                                                        <td>
                                                                                                            <%= detail.getPrenom()
                                                                                                                %>
                                                                                                                <%= detail.getNom()
                                                                                                                    %>
                                                                                                                    <span
                                                                                                                        class="muted">(@
                                                                                                                        <%= detail.getNomFanfaron()
                                                                                                                            %>
                                                                                                                            )
                                                                                                                    </span>
                                                                                                        </td>
                                                                                                        <td>
                                                                                                            <span
                                                                                                                class="statut statut-<%= detail.getStatut() %>">
                                                                                                                <%= detail.getStatut()
                                                                                                                    %>
                                                                                                            </span>
                                                                                                        </td>
                                                                                                    </tr>
                                                                                                    <% } %>
                                                                                            </tbody>
                                                                                        </table>
                                                                                        <% } %>
                                                                                            <% } %>
                                                        </section>
                                                    </main>
                                                </body>

                                                </html>
