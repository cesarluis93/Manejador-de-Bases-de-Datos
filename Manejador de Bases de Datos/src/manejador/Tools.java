package manejador;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

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
	 * @param data - Contenido del archivo json.
	 * @return Contenido tabulada.
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
	
	public String convertSelectResultToContentJsonView(JSONArray data){
		String toshow = "";
		for (int i=0; i<data.length(); i++){
			try {
				toshow += String.valueOf(i+1) + " : " + data.getJSONArray(i).toString() + "\n";
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		return toshow;
	}
	
	
	/**
	 * Elimina un directorio y su contenido.
	 * @param directory - Directorio a eliminar.
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
	
	/**
	 * Copia los archivos de un directorio a otro.
	 * @param source - Directorio origen.
	 * @param target - Directorio destino.
	 */
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
    
    /**
     * Muestra una ventana con una tabla. Cuando se cierre la tabla, el Programa se termina automaticamente.
     * @param rowLabels Etiquetas para las filas.
     * @param columnLabels Etiquetas para las columnas.
     * @param data Matriz de datos para la tabla.
     */
    public void showTable(ArrayList<String> rowLabels, ArrayList<String> columnLabels, ArrayList<String>[][] data){        
        
        //Modificacion del numero de filas y columnas cuando estas poseen etiquetas.
        int rows = data.length, columns = data[0].length;
        rows = (columnLabels != null)? (rows + 1): rows;
        columns = (rowLabels != null)? (columns + 1): columns;
        //Creacion del modelo de datos para la tabla.
        DefaultTableModel modelo = new DefaultTableModel(rows,columns);
        JTable tabla = new JTable (modelo);        
        
        //Si las columnas tienen etiquetas, llenarlas.
        if (columnLabels != null){
            int index = (rowLabels != null)? 0: -1;
            for (String str: columnLabels){
                index++;                
                modelo.setValueAt(str, 0, index);
            }
        }
        //Si las filas tienen etiquetas, llenarlas.
        if (rowLabels != null){            
            int index = (columnLabels != null)? 0: -1;
            for (String str: rowLabels){
                index++;
                modelo.setValueAt(str, index, 0);
            }
        }
        
        //tabla.setRowHeight(50); //Modificar la altura de la tabla.
        
        //Obtencion de los datos para la tabla
        String content;
        int row, column;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                content = "";
                for (String value: data[i][j])
                    content += value;
                row = (columnLabels != null)? (i+1): i;
                column = (rowLabels != null)? (j+1): j;                
                modelo.setValueAt(content, row, column);
            }
        }
                
        
        //VENTANA PARA MOSTRAR LA TABLA
        JFrame ventana = new JFrame();
        //manejamos la salida
        ventana.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        ventana.add(tabla);
        ventana.pack();
        ventana.setVisible(true);
        JOptionPane.showMessageDialog(null, "Doble click en una celda para ver contenido completo..!", "Compiladores", 1);
    }
    
}
