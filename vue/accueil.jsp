<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="modele.Fanfaron" %>

<%
    Fanfaron fanfaron = (Fanfaron) request.getAttribute("fanfaron");
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>FanfareHub - Accueil</title>
</head>
<body>
    <h1>Bienvenue <%= fanfaron.getNomFanfaron() %> </h1>
    <a href="<%= request.getContextPath() %>/deconnexion">Se déconnecter</a>

    <p>Prénom : <%= fanfaron.getPrenom() %></p>
    <p>Nom : <%= fanfaron.getNom() %></p>
    <p>Email : <%= fanfaron.getEmail() %></p>

    <a href="<%= request.getContextPath() %>/deconnexion">Se déconnecter</a>
</body>
</html>