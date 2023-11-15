<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="../Bootstrap/Bootstrap/bootstrap-3.3.6-dist/css/bootstrap.min.css">
    <title>Insertion</title>
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
                        <form action="../Serveur/uploadFichier.do" method="post"  enctype="multipart/form-data">
                            <h3 class="text-center">Upload</h3>
                            <br>
                            <p>
                                <label class="custom-file-input"><input type="file" name="fichier"  onchange="updateFileName(this)" required></label>
                                <span id="file-name-display">Aucun fichier selectionne</span>
                            </p>
                            <button type="submit" class="btn btn-default">Telecharger</button>
                        </form>
                    </section>
                </div>
            </div>            
        </div>
    </div>

    <script src="../Bootstrap/Bootstrap/bootstrap-3.3.6-dist/js/bootstrap.min.js"></script>
    
    <script>
        function updateFileName(input) {
            const fileNameDisplay = document.getElementById('file-name-display');
            if (input.files.length > 0) {
                fileNameDisplay.textContent = input.files[0].name;
                console.log("misy " + fileNameDisplay.textContent);
            } else {
                console.log("tsisy");
                fileNameDisplay.textContent = ' fichier sélectionné';
            }
        }
    </script>

</body>
</html>