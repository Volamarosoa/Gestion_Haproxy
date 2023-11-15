package dao.generiqueDAO;
import  dao.annotations.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import connexionBase.ConnexionBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import etu2068.annotations.Url;


public class GeneriqueDAO {
    String nomTable;
    String maFonction;
    String prefix;
    int longPK = 7;
    String whereCondition = "";

    public String getMaFonction() {
        return this.maFonction;
    }

    public void setMaFonction(String maFunction) {
        this.maFonction = maFunction;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getLongPK() {
        return this.longPK;
    }

    public void setLongPK(int longPK) {
        this.longPK = longPK;
    }

    public String getWhereCondition() {
        return this.whereCondition;
    }

    public void setWhereCondition(String whereCondition) {
        this.whereCondition = whereCondition;
    }

    public String changeFirstAName(String nom){
		return nom.substring(0,1).toUpperCase() + nom.substring(1);
	}

    public String getNomTable() {
        Table table = this.getClass().getAnnotation(Table.class);
        String nomTable = "";
        if(table!=null && table.name().equals("")==false) {
            nomTable = table.name();
        }
        else{
            nomTable = this.getClass().getSimpleName();
        }
        return nomTable;
    }

    public String getNomColonne(Field field, Colonne colonne) {
        String nomColonne = "";
        if(colonne.name().equals("")==false) {
            nomColonne = colonne.name();
        }
        else{
            nomColonne = field.getName();
        }
        return nomColonne;
    }

    public String getNomColonne(Field field) {
        Colonne colonne = field.getAnnotation(Colonne.class);
        String nomColonne = "";
        if(colonne.name().equals("")==false) {
            nomColonne = colonne.name();
        }
        else{
            nomColonne = field.getName();
        }
        return nomColonne;
    }

    public String getNamePrimaryKey() {
        Field[] field = this.getClass().getDeclaredFields();
        String nomColonne = "";
        for(Field field1 : field) {
            Colonne colonne = field1.getAnnotation(Colonne.class);
            nomColonne = this.getNomColonne(field1, colonne);
            PrimaryKey primaryKey = field1.getAnnotation(PrimaryKey.class);
            if(primaryKey != null) {
                isPrimaryKeyAvecFonction(primaryKey);
                break;
            }
        }
        return nomColonne;
    }

    public boolean isPrimaryKeyAvecFonction(PrimaryKey primaryKey) {
        if(primaryKey.maFonction().equals("") == false) {
            this.setMaFonction(primaryKey.maFonction()  + "()");
            this.setPrefix(primaryKey.prefix());
            this.setLongPK(primaryKey.longPK());
            return true;
        }
        return false;
    }

    public String mamenoZero(int id){
		String idE = String.valueOf(id); 
		String retour = this.getPrefix();
		for(int i=0;i<(this.getLongPK()-(this.getPrefix().length()+idE.length()));i++){
			retour+="0";
		}
		retour+=id;
		return retour;
	}

    public String construirePK(ConnexionBase con) throws Exception{
		con = ConnexionBase.verifierConnexion(con);
		int id = -1;
        this.getNamePrimaryKey();
		Statement stmt = con.getConnection().createStatement();
		ResultSet res = stmt.executeQuery("select "+ this.getMaFonction());
		while(res.next()){
			id = res.getInt(1);
		} 
		String pk = this.mamenoZero(id);
		con = ConnexionBase.fermetureConnexion(con);
		return pk;
	}

    @Url(name ="/delete")
    public void deleteById(ConnexionBase con, String id) throws Exception{
        con = ConnexionBase.verifierConnexion(con);
        String requette = "delete from "+ this.getNomTable() + " where " + this.getNamePrimaryKey() + " = " + id;
        System.out.println(requette);
        con.getConnection().createStatement().executeUpdate(requette);
        con = ConnexionBase.fermetureConnexion(con);
    }

    @Url(name = "/update")
    public void updateById(ConnexionBase con,String id) throws Exception {
        con = ConnexionBase.verifierConnexion(con);
        StringBuilder sql = new StringBuilder("UPDATE " + getNomTable() + " set ");
        for (Method methode : getMethodsNotNull()) {
            sql.append(methode.getName().substring(3));
            sql.append("='").append(methode.invoke(this));
            sql.append("' , ");
        }
        if (sql.toString().split(" ")[sql.toString().split(" ").length - 1].equals(",")) {
            sql.delete(sql.lastIndexOf(","), sql.length());
        }
        sql.append("where " + this.getNamePrimaryKey() + " = '").append(id).append("'");
        System.out.println(sql);
        con.getConnection().createStatement().executeUpdate(String.valueOf(sql));
        con = ConnexionBase.fermetureConnexion(con);
       
    }

    @Url(name = "/insert")
    public void insert(ConnexionBase con) throws Exception{
        con = ConnexionBase.verifierConnexion(con); 
        StringBuilder sql = new StringBuilder("insert into " + getNomTable().toLowerCase() + "(");
        StringBuilder attr = new StringBuilder(" ");
        StringBuilder value = new StringBuilder(" ");
        List<Method> methodes = this.getMethodsNotNull();
        for (Method methode : methodes) {
            String attribut = methode.getName().substring(3);
            Field field = methode.getDeclaringClass().getDeclaredField(attribut.substring(0, 1).toLowerCase() + attribut.substring(1));
            boolean primitive = methode.getReturnType().isPrimitive();
            if (field.getAnnotation(Colonne.class) != null && ((primitive && !methode.invoke(this).equals(-1)) || (!primitive && methode.invoke(this) != null))) {
                attr.append( this.getNomColonne(field)).append(" , ");
                value.append("'").append(methode.invoke(this)).append("'").append(" , ");
            }
        }
        if (attr.toString().split(" ")[attr.toString().split(" ").length - 1].equals(",")) {
            attr.delete(attr.lastIndexOf(","), attr.length());
        }
        if (value.toString().split(" ")[value.toString().split(" ").length - 1].equals(",")) {
            value.delete(value.lastIndexOf(","), value.length());
        }
        sql.append(attr).append(") values(").append(value).append(")");
        System.out.println(sql);
        con.getConnection().createStatement().executeUpdate(String.valueOf(sql));
        con = ConnexionBase.fermetureConnexion(con);
    }

    @Url(name ="/list")
    public List<?> list(ConnexionBase con) throws Exception {
        con = ConnexionBase.verifierConnexion(con); 
        Statement statement = null;
        ResultSet resultSet = null;
        Class<?> classe = this.getClass();
        String sql = generateSql();
        System.out.println(sql);
        List liste = new ArrayList<>();
        statement = con.getConnection().createStatement();
        resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            Object instance = classe.getConstructor().newInstance();
            List<Method> setters = getMethodsSetOrGet("set");
            List<Method> getters = getMethodsSetOrGet("get");
            for (int i = 0; i < getters.size(); i++) {
                String column = ((Method)getters.get(i)).getName().substring(3);
                String retType = ((Method)getters.get(i)).getReturnType().getSimpleName().substring(0, 1).toUpperCase() + getters.get(i).getReturnType().getSimpleName().substring(1);
                Method metRes = retType.equalsIgnoreCase("integer") ? ResultSet.class.getDeclaredMethod("getInt", String.class) : ResultSet.class.getDeclaredMethod("get" + retType, String.class);
                setters.get(i).invoke(instance, metRes.invoke(resultSet, column));
            }
            liste.add(instance);
        }
        statement.close();
        con = ConnexionBase.fermetureConnexion(con);
        return liste;
        
    }

    public String generateSql() throws Exception {
        StringBuilder sql = new StringBuilder("select * from " + getNomTable());
        List<Method> methodes = getMethodsNotNull();
        if(methodes.size()!=0 || !this.whereCondition.equals(""))
            sql.append(" where ");

        if(!this.whereCondition.equals(""))
            sql.append(whereCondition);
        for (Method methode : methodes) {
            String attribut = methode.getName().substring(3);
            Field field = methode.getDeclaringClass().getDeclaredField(attribut.substring(0, 1).toLowerCase() + attribut.substring(1));
            sql.append(this.getNomColonne(field)).append(" = ");
            sql.append("'").append(methode.invoke(this)).append("'");
            sql.append(" and ");
        }
        if (sql.toString().split(" ")[sql.toString().split(" ").length - 1].equals("and")) {
            sql.delete(sql.lastIndexOf("and"), sql.length());
        }
        return sql.toString();
    }

    protected final List<Method> getMethodsNotNull() throws Exception {
        try {
            List<Method> methodes = new ArrayList<>();
            for (Method methode : getMethodsSetOrGet("get")) {
                boolean primitive = methode.getReturnType().isPrimitive();
            if ((primitive && !methode.invoke(this).equals(-1)) || (!primitive && methode.invoke(this) != null)) {
                if(!primitive && methode.getReturnType().getName().equals("java.lang.String") && methode.invoke(this).equals("")==false) {
                    methodes.add(methode);
                }
                else if(methode.getReturnType().getName().equals("java.lang.String") == false) {
                    methodes.add(methode);
                }
            }
        }
            return methodes;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    public final List<Method> getMethodsSetOrGet(String start) throws NoSuchMethodException {
        try {
            Class<?> classe = this.getClass();
            List<Method> methodes = new ArrayList<>();
            while (classe != GeneriqueDAO.class) {
                Field[] attributs = classe.getDeclaredFields();
                for (Field attribut : attributs) {
                    if (attribut.getAnnotation(Colonne.class) != null) {
                        String nomMethode = start + "" + this.changeFirstAName(attribut.getName());
                        boolean b = start.equals("set") ? methodes.add(classe.getDeclaredMethod(nomMethode, attribut.getType())) : methodes.add(classe.getDeclaredMethod(nomMethode));
                    }
                }
                classe = classe.getSuperclass();
            }
            return methodes;
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw e;
        }
    }

}


// // Obtenez le chargeur de classe système
// ClassLoader classLoader = ClassLoader.getSystemClassLoader();

// // Obtenez les noms de toutes les classes dans votre projet
// Enumeration<URL> urls = classLoader.getResources("");
// while (urls.hasMoreElements()) {
//     URL url = urls.nextElement();
//     File dir = new File(url.toURI());
//     for (File file : dir.listFiles()) {
//         if (file.isDirectory()) {
//             // Obtenez le nom du package et affichez-le
//             String packageName = file.getName();
//             Package pkg = classLoader.getPackage(packageName);
//             System.out.println(pkg.getName());
//         }
//     }
// }
