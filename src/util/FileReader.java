/*
 * Author: Filipe Moreira
 */

package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class FileReader {
	public int[] readFile(String path) throws IOException, Exception{
		
		int parameters[] = new int[7];
		
		/*
		 * Creates a new BufferedReader instance with a new FileReader pointing to the path
		 * of the file acquired from the argument
		 */
		
		BufferedReader br = new BufferedReader(new java.io.FileReader(new File(path)));
		
		/*
		 * Reads each line of the file inserting them in the array
		 */
		
		br.readLine();
		String parameter = br.readLine();
		char par[];
		String finalParameter;
		for(int i=0; parameter!=null && i<7; i++){
			par = parameter.toCharArray();
			finalParameter = "";
			for(char c : par)
				if(c >= '0' && c <= '9')
					finalParameter = finalParameter+c;
			
			parameters[i] = Integer.parseInt(finalParameter);
			
			parameter = br.readLine();
		}
		
		br.close();
		return parameters;
	}
}
