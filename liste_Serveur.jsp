<%@page import="serveur.Serveur, java.util.List"%>
<%
    List<Serveur> liste = (List<Serveur>)request.getAttribute("liste");    
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Serveurs</title>
        <link rel="stylesheet" href="../Bootstrap/Bootstrap/bootstrap-3.3.6-dist/css/bootstrap.min.css">
        <style>
            th, td, .lis li{
                color: black !important;
            }
            a{
                text-decoration: none !important;
            }
        </style>
    </head>
    <body>
    <div class="container">
        <div class="row">
            <br>
            <div class="col container">
                <ul class="nav nav-pills lis">
                    <li role="presentation" ><a href="../Serveur/ajout_nouveau_serveur.do">Ajout nouveau serveur </a></li>
                    <li role="presentation" ><a href="../Serveur/listeServeur.do">Liste des Serveurs</a></li>
                    <li role="presentation" ><a href="../Serveur/restartHaproxy.do">Restart HAPROXY</a></li>
                    <li role="presentation" ><a href="../Serveur/fichier.do">Ajout du fichier</a></li>
                </ul>
            </div>
            <div class="panel panel-primary" >
                <div class="panel-heading">
                    <h2 class="panel-title">Listes des serveurs</h2>
                </div>
                <div class="liste panel-body">
                    <h4 style="color: rgb(13, 144, 87);">Liste Serveurs: </h4>
                    <table class="table">
                        <thead>
                        <tr>
                            <th scope="col">IP</th>
                            <th scope="col">Nom</th>
                            <th scope="col">Port</th>
                            <th scope="col">Chemin Tomcat</th>
                        </tr>
                        </thead>
                        <tbody>
                        <% for(int i=0; i<liste.size(); i++) { %>
                        <tr>
                            <td scope="row"><%= liste.get(i).getIp() %></td>
                            <td><%= liste.get(i).getNom() %></td>
                            <td><%= liste.get(i).getPort() %></td>
                            <td><%= liste.get(i).getPath() %></td>
                            <td><a href="../Serveur/modifier.do?id=<%= liste.get(i).getId() %>"><button type="button" class="btn btn-default">Modifier</button></a></td>
                            <td><a href="../Serveur/deleteServeur.do?id=<%= liste.get(i).getId() %>"><button type="button" class="btn btn-default">Supprimer</button></a></td>
                        </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    <script src="../Bootstrap/Bootstrap/bootstrap-3.3.6-dist/js/bootstrap.min.js"></script>
</body>
</html>

