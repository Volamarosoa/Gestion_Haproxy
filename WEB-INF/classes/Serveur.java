package serveur;

import dao.generiqueDAO.GeneriqueDAO;
import dao.annotations.Table;

import java.util.List;

import dao.annotations.Colonne;
import dao.annotations.PrimaryKey;

import etu2068.modelView.ModelView;
import etu2068.annotations.Url;
import etu2068.fileUpload.FileUpload;

import java.io.*;


@Table
public class Serveur extends GeneriqueDAO{
    @PrimaryKey
    @Colonne
    int id = -1;
    @Colonne
    String ip;
    @Colonne
    String nom;
    @Colonne
    String path;
    @Colonne
    int port = -1;
    @Colonne
    int etat = -1;
    FileUpload fichier;

    // public void reinitialiser() {
    //     this.id = -1;
    //     this.idEmploye = "";
    //     this.idPlat = "";
    //     this.date = null;
    // }


    public Serveur() {}

    public Serveur(String ip, String nom) {
        this.setId(id);
        this.setNom(nom);
    }

    public Serveur(String ip) {
        this.setId(id);
    }

    public Serveur(int etat) {
        this.setEtat(etat);
    }

    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setId(String id) {
        this.id = Integer.valueOf(id);
    }

    public String getIp() {
        return this.ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNom() {
        return this.nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    public int getPort() {
        return this.port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public void setPort(String port) {
        this.port = Integer.valueOf(port);
    }

    public int getEtat() {
        return this.etat;
    }
    public void setEtat(int etat) {
        this.etat = etat;
    }
    public void setEtat(String etat) {
        this.etat = Integer.valueOf(etat);
    }

    public FileUpload getFichier() {
        return this.fichier;
    }

    public void setFichier(byte[] fichier) {
        this.fichier = new FileUpload(fichier);
        this.createWarFile(fichier);
    }

    @Url(name = "/ajout_nouveau_serveur")
    public ModelView inserer() throws Exception {
        ModelView view = new ModelView("insertion_Serveur.jsp");
        return view;
    }

    @Url(name = "/enregistrer")
    public ModelView save() throws Exception {
        this.setEtat(10);
        this.insert(null);
        ModelView view = new ModelView("insertion_Serveur.jsp");
        return view;
    }

    @Url(name = "/listeServeur")
    public ModelView listeDesServeur() throws Exception {
        ModelView view = new ModelView("liste_Serveur.jsp");
        this.setEtat(10);
        view.addItem("liste", (List<Serveur>) this.list(null));
        return view;
    }

    @Url(name = "/deleteServeur")
    public ModelView deleteServeur() throws Exception {
        this.setEtat(5);
        this.updateById(null, "" + this.getId());
        ModelView view = new ModelView("liste_Serveur.jsp");
        view.addItem("liste", (List<Serveur>) new Serveur(10).list(null));
        return view;
    }

    @Url(name = "/updateServeur")
    public ModelView updateServeur() throws Exception {
        this.updateById(null, "" + this.getId());
        ModelView view = new ModelView("liste_Serveur.jsp");
        view.addItem("liste", (List<Serveur>) new Serveur(10).list(null));
        return view;
    }

    @Url(name = "/modifier")
    public ModelView ModifierServeur() throws Exception {
        ModelView view = new ModelView("update.jsp");
        view.addItem("serveur", ((List<Serveur>) this.list(null)).get(0));
        return view;
    }

    @Url(name = "/restartHaproxy")
    public ModelView restart() throws Exception {
        this.setEtat(10);
        this.ajouterLesServeur("/home/rota/Documents/backend1.cfg");
        // this.restartHaproxy();
        ModelView view = new ModelView("liste_Serveur.jsp");
        view.addItem("liste", (List<Serveur>) this.list(null));
        return view;
    }

    @Url(name = "/fichier")
    public ModelView fichier() throws Exception {
        ModelView view = new ModelView("fichier.jsp");
        return view;
    }

    @Url(name = "/uploadFichier")
    public ModelView uploadFichier() throws Exception {
        ModelView view = new ModelView("fichier.jsp");
        return view;
    }

    public void changerLeHaproxy() throws Exception{
        String filePath = "/etc/haproxy/haproxy.cfg";
        String keyword = "backend mybackend";

        File inputFile = new File(filePath);
        File tempFile = new File("/etc/haproxy/temp.cfg");
        tempFile.setExecutable(true);

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;

        // Lecture du fichier et écriture dans le nouveau fichier temporaire
        while ((currentLine = reader.readLine()) != null) {
            writer.write(currentLine + System.getProperty("line.separator"));
            if (currentLine.contains(keyword)) {
                writer.newLine();
                writer.write(keyword + System.getProperty("line.separator"));
                break;
            }
        }

        // Fermeture des flux
        reader.close();
        writer.close();

        // Renommer le fichier temporaire pour remplacer le fichier d'origine
        if (inputFile.delete()) {
            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Impossible de renommer le fichier temporaire.");
            } else {
                System.out.println("Suppression réussie des lignes après le mot clé.");
            }
        } else {
            System.out.println("Impossible de supprimer le fichier d'origine.");
        }

        this.ajouterLesServeur(filePath);

        // this.restartHaproxy();
            
    }

    public void ajouterLesServeur(String filePath) throws Exception{
        FileWriter fileWriter = new FileWriter(filePath);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write("frontend myfrontend");
        bufferedWriter.newLine();
        bufferedWriter.write("    bind *:81");
        bufferedWriter.newLine();
        bufferedWriter.write("    mode http");
        bufferedWriter.newLine();
        bufferedWriter.write("default_backend mybackend");
        bufferedWriter.newLine();
        bufferedWriter.write("    mode http");
        bufferedWriter.newLine();

        List<Serveur> servers = (List<Serveur> )this.list(null);

        // Utilisation de server-template pour générer dynamiquement les entrées du serveur
        for (int i = 0; i < servers.size(); i++) {
            bufferedWriter.write("    server " + servers.get(i).getNom() + " " + servers.get(i).getIp() + ":" + servers.get(i).getPort());
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public void restartHaproxy() throws Exception{
        // this.copy();
        this.execute("echo 2004 | sudo -S 'scp /home/rota/Documents/backend1.cfg rota@localhost:/etc/haproxy/conf.d'/", "Nety le copy", "Nanondrana le copy");
        this.execute("echo 2004 | sudo -S systemctl restart haproxy", "Nety le restart", "Nanondrana le restart");
    }

    public void execute(String requette, String nety, String tsy_nety) throws Exception {
        String[] command = {"/bin/sh", "-c", requette};

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        // Attendre que le processus se termine
        int exitCode = process.waitFor();

        InputStream input = process.getErrorStream();
        BufferedReader reader = new BufferedReader( new InputStreamReader(input) );
        StringBuilder builder = new StringBuilder();

        String line = null;

        while( (line = reader.readLine()) != null ){
            System.out.println( "Erreur : " + line );           
        }

        if (exitCode == 0) {
            System.out.println(nety);
        } else {
            System.err.println(tsy_nety + " " + exitCode);
        }
    }

    public void copy() throws Exception {
        String[] command = {"/bin/sh/sudo", "/home/rota/Documents/backend1.cfg", "/etc/haproxy/conf.d/"};

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        // Attendre que le processus se termine
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("Nety le copy");
        } else {
            System.err.println("Tsy nety le copy : " + exitCode);
        }
    }

    public void createWarFile(byte[] warBytes) {
        String outputPath = "/home/rota/ITU/Mr_Naina/Cluster/fichier/projet_cluster.war";
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            // Écriture des bytes dans le fichier
            fos.write(warBytes);
            System.out.println("Fichier WAR créé avec succès à : " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}