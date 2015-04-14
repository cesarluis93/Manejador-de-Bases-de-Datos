package manejador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Tools {
	public Tools(){}
	
	/**
	 * Metodo para leer un archivo.
	 * @param file
	 * @return
	 */
	public String readFile(File file){
		String data = "";
		try {
			BufferedReader reader= new BufferedReader(new FileReader(file));  
			String line;
			while((line = reader.readLine())!=null) {  
				data += line + " ";
			}
			reader.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());  
		}
		return data;
	}
	
	/**
	 * Metodo para escribir un archivo.
	 * @param file
	 * @param text
	 * @return
	 */
	public boolean writeFile(File file,String text){  
	  try {
		  BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
		  writer.write(text);
		  writer.close();
		  return true;
	   } catch (Exception ex) {  
		   //Captura un posible error le imprime en pantalla   
		   System.out.println(ex.getMessage());
		   return false;
	   }
	}
	
	/**
	 * Metodo para visualizar un archivo .json con tabulaciones.
	 * @param data
	 * @return
	 */
	public String convertToContentJsonView(String data){
		String toshow = "", tabs;
		int numTabs = 0;
		for (int i=0; i<data.length(); i++){			
			if ("{[".contains(String.valueOf(data.charAt(i)))){
				numTabs += 1;
				tabs = "";
				for (int j=0; j<numTabs; j++)
					tabs += "\t";
				toshow += String.valueOf(data.charAt(i)) + "\n" + tabs;				
			}
			else if ("]}".contains(String.valueOf(data.charAt(i)))){
				numTabs -= 1;
				tabs = "";
				for (int j=0; j<numTabs; j++)
					tabs += "\t";
				toshow += "\n" + tabs + String.valueOf(data.charAt(i));
			}
			else if (data.charAt(i) == ','){
				tabs = "";
				for (int j=0; j<numTabs; j++)
					tabs += "\t";
				toshow += String.valueOf(data.charAt(i)) + "\n" + tabs;
			}
			else {
				toshow += String.valueOf(data.charAt(i));	
			}			
		}			
		return toshow;
	}
	
	/**
	 * Elimina un directorio y su contenido.
	 * @param directory
	 */
	public void deletteDirectory(File directory){		
		File[] files = directory.listFiles();
		for (int x=0; x<files.length; x++){
			if (files[x].isDirectory()) {
				deletteDirectory(files[x]);
			}
			files[x].delete();
		}
		directory.delete();
	}
	
    public void copyFiles(String source, String target){
		File folderSource = new File(source);
		File newFolder = new File(target);		
		newFolder.mkdir();
		File[] files = folderSource.listFiles();		
		for (int x=0; x<files.length; x++){
			File newFile = new File(target + "\\" + files[x].getName());			
			files[x].renameTo(newFile);
		}
	    
    }
    
    
    /**
     * Verifica si un JSONArray de Strings contiene un String determinado.
     * @param array - Arreglo de strings.
     * @param item - String a buscar en el arreglo.
     * @return true - Si item existe en array.
     * @return false - Si item no existe en array.
     */
    public boolean jsonArrayContain(JSONArray array, String item){
    	for (int i=0; i<array.length(); i++){
    		try {
				if (array.getString(i).equals(item))
					return true;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return false;
    }
    
    /**
     * Verifica si un JSONArray de Objetos contiene un Objeto determinado.
     * @param array
     * @param object
     * @param objectItems
     * @return
     */
    public boolean jsonArrayContain(JSONArray array, JSONObject object, ArrayList<String> objectItems){
    	int cont;
    	for (int i=0; i<array.length(); i++){
    		JSONObject obj;
			try {
				obj = array.getJSONObject(i);
				cont = 0;
	    		for (String objItem: objectItems){
	    			cont++;
	    			if (!obj.get(objItem).equals(object.get(objItem))){	    				
	    				break;
	    			}
	    			if (cont == objectItems.size()){
    					return true;
    				}
	    		}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return false;
    }
    
    
    /**
     * Método auxiliar que sustrae los ids de una expresión booleana.
     * @param expression
     * @return Object[] - array con la expressión limpiada y Arraylist de los ids.
     */
    public Object[] getColumns(String expression){		
		String newExpression = "";
		ArrayList<String> columns = new ArrayList();
		char prev = ' ';		 
		for (int i=0; i<expression.length(); i++){
			if (expression.charAt(i) == 'd' && prev == 'i'){
				newExpression = newExpression.substring(0, newExpression.length()-1);
				String resto = expression.substring(i, expression.length());
				String id = resto.substring(2, resto.indexOf("]"));
				columns.add(id);
				i += id.length()+2;
				newExpression += id;
				prev = ' ';
			}
			else{
				newExpression += expression.charAt(i);
				prev = expression.charAt(i);
			}
		}
		Object[] result = {newExpression, columns};
		return result;
    }
    
    
}
