<%@page import="serveur.Serveur" %>
<%
    Serveur serveur = (Serveur)request.getAttribute("serveur");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="../Bootstrap/Bootstrap/bootstrap-3.3.6-dist/css/bootstrap.min.css">
    <title>Modification</title>
    <style>
        th, td, .lis li{
            color: black !important;
        }
        a{
            text-decoration: none !important;
        }
        .custom-file-input {
            display: inline-block;
            padding: 6px 12px;
            cursor: pointer;
            background-color: #e9ecef;
            border: 1px solid #ced4da;
            border-radius: 4px;
        }

        .custom-file-input::before {
            content: 'Choisir un fichier';
        }

        .custom-file-input:hover::before {
            background-color: #d1d1d1;
        }

        .custom-file-input:active {
            background-color: #ccc;
        }

        /* Cacher le champ d'import de fichier par défaut */
        input[type="file"] {
            display: none;
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
                <div class="col-md-4 panel-body">
                    <section class="contact-clean">
                        <form action="../Serveur/updateServeur.do" method="post">
                            <h3 class="text-center">Modifier un serveur</h3>
                            <br>
                            <!-- <div class="form-group"> -->
                                <!-- <label>ID</label> -->
                                <input class="form-control" type="hidden"  name="id" value="<%= serveur.getId() %>">
                            <!-- </div> -->
                            <div class="form-group">
                                <label>IP</label>
                                <input class="form-control" type="text"  name="ip" value="<%= serveur.getIp() %>"  placeholder="Ip 0.0.0.0 du serveur" required>
                            </div>
                            <div class="form-group">
                                <label>Nom</label>
                                <input class="form-control" type="text"  name="nom" value="<%= serveur.getNom() %>" placeholder="Nom du serveur" required>
                            </div>
                            <div class="form-group">
                                <label>Port</label>
                                <input class="form-control" type="texte"  name="port" value="<%= serveur.getPort() %>" placeholder="Port du serveur tomcat" required>
                            </div>
                            <div class="form-group">
                                <label>Chemin</label>
                                <input class="form-control" type="texte"  name="path" value="<%= serveur.getPath() %>" placeholder="Chemin du serveur tomcat" required>
                            </div>
                            <div class="form-group">
                                <button class="btn btn-primary" type="submit">Ajouter</button>
                            </div>
    
                        </form>
                    </section>
                </div>
            </div>            
        </div>
    </div>

    <script src="../Bootstrap/Bootstrap/bootstrap-3.3.6-dist/js/bootstrap.min.js"></script>

</body>
</html>