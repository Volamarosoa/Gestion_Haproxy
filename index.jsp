<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="./Bootstrap/Bootstrap/bootstrap-3.3.6-dist/css/bootstrap.min.css">
    <title>Formulaire</title>
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
                    <li role="presentation" ><a href="Serveur/ajout_nouveau_serveur.do">Ajout nouveau serveur </a></li>
                    <li role="presentation" ><a href="Serveur/listeServeur.do">Liste des Serveurs</a></li>
                    <li role="presentation" ><a href="Serveur/restartHaproxy.do">Restart HAPROXY</a></li>
                    <li role="presentation" ><a href="Serveur/fichier.do">Ajout du fichier</a></li>
                </ul>
            </div>
        </div>
    </div>

    <script src="./Bootstrap/Bootstrap/bootstrap-3.3.6-dist/js/bootstrap.min.js"></script>
</body>
</html>