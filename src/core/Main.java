/*
 * Author: Filipe Moreira and Pedro Pio
 */

package core;

import java.io.IOException;

import javax.swing.JOptionPane;

public class Main {
	public static void main(String args[]){
		try {
			Simulator simulation = new Simulator();
			simulation.simulate();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Fail to read input file.");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fail to acquire parameters.");
		}
	}
}
