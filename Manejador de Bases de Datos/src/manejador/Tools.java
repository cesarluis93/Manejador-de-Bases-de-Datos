package manejador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;

public class Tools {
	public Tools(){}
	
	public String LeerFichero(File file){
		String data = "";
		try {
			BufferedReader Flee= new BufferedReader(new FileReader(file));  
			String Slinea;
			while((Slinea = Flee.readLine())!=null) {  
				data += Slinea;
			}
			Flee.close();				
		} catch (Exception ex) {
			System.out.println(ex.getMessage());  
		}
		return data;
	 }
	
	public static void EcribirFichero(File Ffichero,String SCadena){  
	  try {
		  BufferedWriter Fescribe=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Ffichero,true), "utf-8"));  
		  /*Escribe en el fichero la cadena que recibe la función.  
		   *el string "\r\n" significa salto de linea*/  
		  Fescribe.write(SCadena + "\r\n");  
		  //Cierra el flujo de escritura  
  Fescribe.close();  
	   } catch (Exception ex) {  
	      //Captura un posible error le imprime en pantalla   
	          System.out.println(ex.getMessage());  
	       }   
	}  	
}
