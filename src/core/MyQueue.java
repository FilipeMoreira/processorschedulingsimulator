package core;

import java.util.ArrayList;

public class MyQueue {
	private ArrayList<Process> queue;
	
	public MyQueue(){
		queue = new ArrayList<Process>();
	}
	
	public Process peek(){
		return queue.size()<=0?null:queue.get(0);
	}
	
	public Process poll(){
		if(queue.size()<=0)
			return null;
		Process result = queue.get(0);
		queue.remove(0);
		return result;
	}
	
	public void add(Process process){
		queue.add(process);
	}
}
