package etu2068.framework.servlet;

import javax.servlet.*;
import javax.servlet.http.*;

import etu2068.annotations.Url;
import etu2068.annotations.Argument;
import etu2068.annotations.Singleton;
import etu2068.annotations.Auth;
import etu2068.annotations.Session;
import etu2068.annotations.restAPI;
import etu2068.annotations.ExportXML;
import etu2068.annotations.Test;
import etu2068.modelView.ModelView;

import java.io.*;
import java.lang.Thread;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Set;
import java.util.Enumeration;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import etu2068.framework.Mapping;
import java.lang.ClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.net.URL;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;

import java.util.List;

// import org.jdom2.Document;
// import org.jdom2.Element;
// import org.jdom2.input.SAXBuilder;
// import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.servlet.annotation.*;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.InvocationTargetException;

import com.google.gson.Gson;

@MultipartConfig()
public class FrontServlet extends HttpServlet {
    HashMap<String, Mapping> mappingUrls;
    Vector<Class<?>> listeClasse;
    HashMap<String, Object> instances;

    public void init() {
        try {
            this.setMappingUrls(new HashMap<String, Mapping>());
            ClassLoader classLoader = new Thread().getContextClassLoader();
            Enumeration<URL> urls = classLoader.getResources("");
            this.listeClasse = new Vector<Class<?>>();
            this.instances = new HashMap<String, Object>();

            /// maka ny nom an'ilay package misy an'ilay models rehetra, alaina avy eo
            /// amin'ny web.xml
            String nomPackage = getServletContext().getInitParameter("NomduPackage");

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                File dir = new File(url.toURI());
                for (File file : dir.listFiles()) {
                    if (file.isDirectory() && file.getName().equals(nomPackage)) {
                        this.listePackage(file, file.getName());
                    }
                }
            }

        } catch (Exception io) {
            System.out.println("Erreur: " + io.getMessage());
            io.printStackTrace();
        }
    }

    public HashMap<String, Mapping> getMappingUrls() {
        return this.mappingUrls;
    }

    public void setMappingUrls(HashMap<String, Mapping> mappingUrls) {
        this.mappingUrls = mappingUrls;
    }

    public Vector<Class<?>> getListeClasse() {
        return this.listeClasse;
    }

    public void setListeClasse(Vector<Class<?>> listeClasse) {
        this.listeClasse = listeClasse;
    }

    public HashMap<String, Object> getInstances() {
        return this.instances;
    }

    public void setInstances(HashMap<String, Object> instances) {
        this.instances = instances;
    }

    public void addInstance(String key, Object value) {
        this.getInstances().put(key, value);
    }

    // test si c'est un fichier
    private boolean isFilePart(Part part) {
        String disposition = part.getHeader("content-disposition");
        return (disposition != null && disposition.contains("filename"));
    }

    // retourne les valeurs de ce qui ne sont pas un fichier
    private String[] readValueFromPart(Part part) throws IOException {
        InputStream partContent = part.getInputStream();
        InputStreamReader reader = new InputStreamReader(partContent, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        return lines.toArray(new String[0]);
    }

    // retourne les valeurs en byte de ce qui sont fichier
    private byte[] readBytesFromPart(Part part) throws IOException {
        InputStream partContent = part.getInputStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = partContent.read(buffer)) != -1) {
            output.write(buffer, 0, length);
        }
        return output.toByteArray();
    }

    public void resetFieldToNull(Object object) throws Exception {
        String reinitialiser = getServletContext().getInitParameter("reinitialiser");
        Class<?> objectClass = object.getClass();
        Method[] methods = objectClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(reinitialiser)) {
                System.out.println("nisy aki anh");
                method.invoke(object);
            }
        }
        System.out.println("nandalo ve?");

    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String contentType = request.getContentType();
        try {
            response.setContentType("text/plain");
            // out.println("URL = " + request.getRequestURI().substring(request.getContextPath().length()).split(".do")[0]);
            // out.println("Method = " + request.getMethod().toString());
            out.println();

            Mapping mapping1 = (Mapping) this.getMappingUrls().get(request.getRequestURI().substring(request.getContextPath().length()).split(".do")[0]);
            if (mapping1 != null) {
                System.out.println("url====>" + request.getRequestURI().substring(request.getContextPath().length())
                        + "==== >>>> classe = " + mapping1.getClassName());
                System.out.println("url====>" + request.getRequestURI().substring(request.getContextPath().length())
                        + "==== >>>> method = " + mapping1.getMethod());
                for (Class<?> class1 : this.getListeClasse()) {
                    if (class1.getSimpleName().equals(mapping1.getClassName())) {
                        // out.println(class1.getSimpleName());
                        Object object = null;
                        // mijery hoe singleton ve le class natsoina sa tsia
                        if (this.getInstances().containsKey(class1.getSimpleName())) {
                            object = this.getInstances().get(class1.getSimpleName());
                            if (object == null) {
                                object = class1.newInstance();
                                System.out.println("micreer instance");
                                this.getInstances().replace(class1.getSimpleName(), object);
                            } else {
                                System.out.println("mamerina instance");
                                this.resetFieldToNull(object);
                            }
                        } else {
                            object = class1.newInstance();
                        }

                        Map<String, String[]> params = request.getParameterMap();
                        if (contentType != null && contentType.startsWith("multipart/")) {
                            this.makaParametreDonneesAvecFichier(object, request, class1, out); // maka an'ilay
                                                                                                // parametre setters any
                                                                                                // jsp fa avec setters
                        } else {
                            object = this.makaParametreDonnees(object, params, class1); // maka an'ilay parametre
                                                                                        // setters any jsp
                        }

                        Method[] methods = class1.getDeclaredMethods();
                        for (Method method : methods) {
                            if (method.getName().equals(mapping1.getMethod())) {
                                // test si le methode a l'annotation Auth
                                this.checkMethod(method, request);

                                // get tous les sessions demander par la methode
                                this.checkSession(object, method, request);

                                Object[] argument = this.mamenoParametreMethode(method, params); // mameno parametre
                                                                                                 // an'ilay fonction

                                if (method.isAnnotationPresent(restAPI.class)) {
                                    Object json = null;
                                    if (argument != null) {
                                        json = method.invoke(object, argument);
                                    } else {
                                        json = method.invoke(object);
                                    }
                                    String json_tab = new Gson().toJson(json);
                                    System.out.println("io: " + json_tab);
                                    out.println(json_tab);
                                // } 
                                // else if (method.isAnnotationPresent(ExportXML.class)) {
                                //     Object objetXML = null;
                                //     objetXML = method.invoke(object);
                                //     String fichier = (String) objetXML;
                                //     this.fichierXML(fichier, request, response);
                                } else {
                                    ModelView view = null;
                                    try {
                                        if (argument != null) {
                                            view = (ModelView) method.invoke(object, argument);
                                        } else {
                                            view = (ModelView) method.invoke(object);
                                        }
                                        // Code où l'exception InvocationTargetException se produit
                                    } catch (InvocationTargetException e) {
                                        Throwable cause = e.getCause();
                                        if (cause != null) {
                                            // Gérez ou affichez l'exception réelle
                                            cause.printStackTrace();
                                        }
                                    }

                                    // supprime les sessions demander
                                    this.removeSession(view, request);

                                    // on ajout les sessions
                                    if (view.getSession() != null) {
                                        this.addSession(view, request);
                                    }

                                    // on ajout les attributs a envoyer vers JSP ici
                                    if (view.getData() != null) {
                                        this.setAttribute(view, request);
                                    }

                                    // test si c'est un JSON
                                    if (view.isJson()) {
                                        out.println("true");
                                        String json = new Gson().toJson(view.getData());
                                        System.out.println(json);
                                        out.println(json);
                                    } else {
                                        RequestDispatcher dispatcher = request.getRequestDispatcher("/" + view.getView());
                                        dispatcher.forward(request, response);
                                    }
                                }

                            }
                        }
                    }
                }
            }
        } catch (Exception io) {
            // out.println("Erreur aki = " + io);
            io.printStackTrace();
            out.println("erreur piso: " + io);
            request.setAttribute("erreur: ", io);
            // RequestDispatcher dispatcher = request.getRequestDispatcher("/Erreur.jsp");
            // dispatcher.forward(request, response);
        }
    }

    // fichier XML

    // public void fichierXML(String fichier, HttpServletRequest request, HttpServletResponse response)
    //         throws ServletException, IOException {
    //     // Remplacez "chemin/vers/votre/fichier.xml" par le chemin de votre fichier XML
    //     File file = new File(fichier);

    //     // Utiliser JDOM pour lire le fichier XML
    //     SAXBuilder saxBuilder = new SAXBuilder();
    //     try {
    //         Document document = saxBuilder.build(file);
    //         Element racine = document.getRootElement();

    //         // Afficher le contenu XML dans la réponse HTTP
    //         response.setContentType("text/plain");
    //         response.setCharacterEncoding("UTF-8");

    //         // Utiliser JDOM pour convertir le document XML en texte
    //         String xmlContent = new XMLOutputter().outputString(document);

    //         // Écrire le contenu XML dans la réponse HTTP
    //         response.getWriter().println(xmlContent);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    //         response.getWriter().println("Erreur lors de la lecture du fichier XML.");
    //     }
    // }

    // ajout de donnees dans l'Attribute du servlet
    public void setAttribute(ModelView view, HttpServletRequest request) {
        for (Map.Entry<String, Object> entry : view.getData().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            request.setAttribute(key, value);
        }
    }

    // ajout de nouveau session dans la session
    public void addSession(ModelView view, HttpServletRequest request) {
        HttpSession session = request.getSession();
        for (Map.Entry<String, Object> entry : view.getSession().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            session.setAttribute(key, value);
        }
    }

    // supprime les sessions demander
    public void removeSession(ModelView view, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (view.isInvalidateSession()) {
            session.invalidate();
        } else {
            for (String key : view.getRemoveSession()) {
                session.removeAttribute(key);
            }
        }
    }

    // check si la methode a l'annotation Auth
    public void checkMethod(Method method, HttpServletRequest request) throws Exception {
        if (method.isAnnotationPresent(Auth.class)) {
            String profil = method.getAnnotation(Auth.class).profil();
            String auth_session = getServletContext().getInitParameter("auth_session");
            String profil_session = getServletContext().getInitParameter("profil_session");
            HttpSession session = request.getSession();
            if (session.getAttribute(auth_session) == null || (Boolean) session.getAttribute(auth_session) == false) {
                throw new Exception("Desole vous n'etes pas connecte");
            }

            if (profil.equals("") == false && session.getAttribute(profil_session) != null
                    && profil.equals((String) session.getAttribute(profil_session)) == false) {
                throw new Exception("Desole vous n'avez pas acces a cette methode");
            }
        }
    }

    public void checkSession(Object object, Method method, HttpServletRequest request) throws Exception {
        if (method.isAnnotationPresent(Session.class)) {
            System.out.println("manontany izyy??");
            String attribut = "session";
            String[] sessions = method.getAnnotation(Session.class).sessions();
            Class<?> class1 = object.getClass();
            Field champ = class1.getDeclaredField(attribut);
            if (champ == null)
                throw new Exception(
                        "Verifier votre class mais vous n'aviez pas encore l'attribut session.\n Veuillez ajouter une attribut session dans votre class.");
            String nomMethode = "set" + this.changeFirstAName(attribut);
            Method setter = class1.getDeclaredMethod(nomMethode, champ.getType());
            HttpSession session = request.getSession();
            Map<String, Object> sessionAttributes = new HashMap<>();

            if (sessions.length == 0) {
                Enumeration<String> attributeNames = session.getAttributeNames();
                while (attributeNames.hasMoreElements()) {
                    String attributeName = attributeNames.nextElement();
                    Object attributeValue = session.getAttribute(attributeName);
                    sessionAttributes.put(attributeName, attributeValue);
                }
            } else {
                for (String nom : sessions) {
                    sessionAttributes.put(nom, session.getAttribute(nom));
                }
            }

            setter.invoke(object, sessionAttributes);
        }
    }

    public void listePackage(File dossier, String packages) {
        try {
            String packageName = packages;
            for (File file : dossier.listFiles()) {
                if (file.isDirectory()) {
                    packages = packages + "." + file.getName();
                    this.listePackage(file, packages);
                } else {
                    this.getClass(file, packages);
                }
                packages = packageName;
            }

        } catch (Exception io) {
            io.printStackTrace();
        }
    }

    public void getClass(File fichier, String packages) throws Exception {
        String name = fichier.getName();
        name = name.split(".class")[0];
        Class<?> classe = Class.forName(packages + "." + name);
        this.getListeClasse().add(classe);

        // prends tous les noms des methodes ou il y a des annotation URL

        Method[] methods = classe.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Url.class)) {
                Mapping mapping = new Mapping(name, method.getName());
                this.getMappingUrls().put("/" + name + "" + method.getAnnotation(Url.class).name(), mapping);
            }
        }

        if (classe.getSuperclass() != null) {
            Class<?> c = classe.getSuperclass();
            methods = c.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Url.class)) {
                    Mapping mapping = new Mapping(name, method.getName());
                    this.getMappingUrls().put("/" + name + "" + method.getAnnotation(Url.class).name(), mapping);
                }
            }
        }

        if (classe.isAnnotationPresent(Singleton.class)) {
            System.out.println("singeton: " + name);
            this.getInstances().put(name, null);
        }

    }

    public String changeFirstAName(String nom) {
        return nom.substring(0, 1).toUpperCase() + nom.substring(1);
    }

    // ty le fonction manao setters anle objet
    public Object makaParametreDonnees(Object object, Map<String, String[]> params, Class<?> class1) {
        PrintWriter out = new PrintWriter(System.out);
        if (params.isEmpty() == false) {
            for (String paramName : params.keySet()) {
                String[] values = params.get(paramName);
                Object reponse = null;
                if (values != null && values.length == 1) {
                    reponse = (Object) values[0];
                    try {
                        Field champ = class1.getDeclaredField(paramName);
                        String nomMethode = "set" + this.changeFirstAName(paramName);
                        Method setter = class1.getDeclaredMethod(nomMethode, champ.getType());
                        reponse = castValue(champ.getType(), values[0]); // micaste anle valiny io fonction io
                        setter.invoke(object, reponse);
                    } catch (Exception io) {
                        io.printStackTrace();
                    }
                }

                else if (values != null && values.length > 1) {
                    reponse = (Object) values;
                    try {
                        Field champ = class1.getDeclaredField(paramName);
                        String nomMethode = "set" + this.changeFirstAName(paramName);
                        Method setter = class1.getDeclaredMethod(nomMethode, champ.getType());
                        reponse = liste(champ.getType(), values);
                        setter.invoke(object, reponse);
                    } catch (Exception io) {
                        io.printStackTrace();
                    }
                }
            }
        }
        return object;
    }

    // ty le fonction manao setters anle objet avec un fichier
    private Object makaParametreDonneesAvecFichier(Object objet, HttpServletRequest request, Class<?> class1,
            PrintWriter out) throws IOException {
        try {
            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                if (isFilePart(part)) {
                    String partName = part.getName();
                    out.println("fichier: " + partName);
                    byte[] fileBytes = readBytesFromPart(part);
                    Field champ = class1.getDeclaredField(partName);
                    String nomMethode = "set" + this.changeFirstAName(partName);
                    Method setter = class1.getDeclaredMethod(nomMethode, byte[].class);
                    setter.invoke(objet, fileBytes);
                    out.println("eto aloha " + nomMethode + " type = " + champ.getType());
                } else {
                    Object reponse = null;
                    String partName = part.getName();
                    out.println("simple: " + partName);
                    String[] partValue = readValueFromPart(part);
                    if (partValue != null && partValue.length == 1) {
                        try {
                            Field champ = class1.getDeclaredField(partName);
                            String nomMethode = "set" + this.changeFirstAName(partName);
                            Method setter = class1.getDeclaredMethod(nomMethode, champ.getType());
                            reponse = castValue(champ.getType(), partValue[0]); // micaste anle valiny io fonction io
                            setter.invoke(objet, reponse);
                        } catch (Exception io) {
                            io.printStackTrace();
                        }
                    }

                    else if (partValue != null && partValue.length > 1) {
                        try {
                            Field champ = class1.getDeclaredField(partName);
                            String nomMethode = "set" + this.changeFirstAName(partName);
                            Method setter = class1.getDeclaredMethod(nomMethode, champ.getType());
                            reponse = liste(champ.getType(), partValue);
                            setter.invoke(objet, reponse);
                        } catch (Exception io) {
                            io.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception io) {
            io.printStackTrace();
            out.println(io.getMessage());
        }
        return objet;
    }

    public Object[] mamenoParametreMethode(Method method, Map<String, String[]> params) throws Exception {
        Object[] arguments = null;
        if (params.isEmpty() == false) {
            Parameter[] parameters = method.getParameters();
            if (parameters.length != 0) {
                arguments = new Object[parameters.length];
                int i = 0;
                for (Parameter parameter : parameters) {
                    for (String paramName : params.keySet()) {
                        if (paramName.equals(parameter.getAnnotation(Argument.class).name())) {
                            String[] values = params.get(paramName);
                            Object reponse = null;
                            if (values != null && values.length == 1) {
                                arguments[i] = castValue(parameter.getType(), values[0]);
                            }

                            else if (values != null && values.length > 1) {
                                arguments[i] = liste(parameter.getType(), values);
                            }
                        }
                    }
                    i++;
                }
            }
        }
        return arguments;
    }

    public Object castValue(Class<?> type, String value) throws Exception {
        if (type == String.class) {
            return value;
        } else if (type == Integer.class || type == int.class) {
            return Integer.parseInt(value);
        } else if (type == Double.class || type == double.class) {
            return Double.parseDouble(value);
        } else if (type == Boolean.class || type == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == Long.class || type == long.class) {
            return Long.parseLong(value);
        } else if (type.toString() == "java.sql.Date") {
            return java.sql.Date.valueOf(value);
        } else if (type == Timestamp.class) {
            value = value.replace('T', ' ');
            value += ":00";
            System.out.println(value);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("Mandalo retsy egg");
            return new java.sql.Timestamp(formatter.parse(value).getTime());
        } else if (type == Time.class) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            return new java.sql.Time(formatter.parse(value).getTime());
        } else {
            return null;
        }
    }

    public Object liste(Class<?> type, String[] value) {
        if (type == String.class) {
            return value;
        } else if (type == Integer.class || type == int.class) {
            int[] liste = new int[value.length];
            for (int i = 0; i < value.length; i++) {
                liste[i] = Integer.parseInt(value[i]);
            }
            return liste;
        } else if (type == Double.class || type == double.class) {
            double[] liste = new double[value.length];
            for (int i = 0; i < value.length; i++) {
                liste[i] = Double.parseDouble(value[i]);
            }
            return liste;
        } else {
            return null;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        try {
            processRequest(request, response);
            // 1 - Maka ny parametre nalefa raha misy
            // 2 - controlle de valeur na instanciation objet raha ilaina

            // 3 - miset ny attribut ho an redirection raha misy

            // 4 - Redirection

        } catch (Exception io) {
            out.print(io.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        processRequest(request, response);
        // 1 - Maka ny parametre nalefa raha misy
        // 2 - controlle de valeur na instanciation objet raha ilaina

        // 3 - miset ny attribut ho an redirection raha misy

        // 4 - Redirection

    }
}
