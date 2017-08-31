/*
 * Author: Filipe Moreira and Pedro Pio
 */

package util;

public class Exponential {
	public double exponential ( int expected ) {
		int meu = (int) (((((int) ((Math.random()*10)%2) )*2)-1)*Math.random()*(expected/2)) + expected;
	    
	    return meu;

	}
}
