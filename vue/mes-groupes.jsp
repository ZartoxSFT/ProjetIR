<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="java.util.List" %>
<%@ page import="modele.Instrument" %>
<%@ page import="modele.GroupeFanfare" %>

<%
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
    <title>FanfareHub - Mes groupes</title>

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
            display: grid;
            place-items: center;
            padding: 24px;
        }

        .card {
            width: min(700px, 100%);
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
            color: var(--accent);
        }

        .content {
            padding: 32px;
        }

        h2 {
            color: var(--accent);
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
    </style>
</head>

<body>

<div class="card">

    <div class="header">
        <h1>🎺 FanfareHub</h1>
    </div>

    <div class="content">

        <form method="POST"
              action="<%= request.getContextPath() %>/mes-groupes">

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

                            <%= instrument.getNom() %>

                        </label>

                    <% } %>

                </div>
            </div>

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

                            <%= groupe.getNom() %>

                        </label>

                    <% } %>

                </div>
            </div>

            <button type="submit">
                Enregistrer mes choix
            </button>

        </form>

    </div>

</div>

</body>
</html>