package core;

import java.util.ArrayList;

public class MyPriorityQueue {
	private ArrayList<Event> queue;
	
	public MyPriorityQueue(){
		queue = new ArrayList<Event>();
	}
	
	public Event peek(){
		return queue.size()<=0?null:queue.get(0);
	}
	
	public Event poll(){
		if(queue.size()<=0)
			return null;
		Event result = queue.get(0);
		queue.remove(0);
		return result;
	}
	
	public void add(Event event){
		queue.add(event);
		quicksort(0,queue.size()-1);
	}
	
	private void quicksort(int lowerIndex, int higherIndex){
		int i = lowerIndex;
        int j = higherIndex;
        // calculate pivot number, I am taking pivot as middle index number
        Event pivot = queue.get(lowerIndex+(higherIndex-lowerIndex)/2);
        // Divide into two arrays
        while (i <= j) {
            /**
             * In each iteration, we will identify a number from left side which
             * is greater then the pivot value, and also we will identify a number
             * from right side which is less then the pivot value. Once the search
             * is done, then we exchange both numbers.
             */
            while (queue.get(i).getEventTime() < pivot.getEventTime()) {
                i++;
            }
            while (queue.get(j).getEventTime() > pivot.getEventTime()) {
                j--;
            }
            if (i <= j) {
                swap(i, j);
                //move index to next position on both sides
                i++;
                j--;
            }
        }
        // call quickSort() method recursively
        if (lowerIndex < j)
            quicksort(lowerIndex, j);
        if (i < higherIndex)
            quicksort(i, higherIndex);
	}
	
	private void swap(int i, int j){
		Event temp = queue.get(i);
		queue.set(i, queue.get(j));
		queue.set(j, temp);
	}
}
