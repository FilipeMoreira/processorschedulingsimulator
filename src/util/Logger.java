/*
 * Author: Filipe Moreira and Pedro Pio
 */

package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logger {
	private StringBuffer log;
	
	public Logger(){
		this.log = new StringBuffer();
	}
	
	public void log(String log){
		this.log.append(log+"\n");
	}
	
	public void save() throws IOException{
		File output = new File("src/files/output_"+new Date()+".txt");
		
		FileWriter fw = new FileWriter(output);
		
		fw.write(log.toString());
		
		fw.close();
	}
}
