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
}
