/*
 * Author: Filipe Moreira and Pedro Pio
 */

package core;

public class Event {
	private int eid;
	private double eventTime; // In microseconds
	private int type; // 0 - New; 1 - To CPU; 2 - Quantum Expires; 3 - To I/O; 4 - Leave I/O; 5 - Terminate
	
	public Event(int eid, double eventTime, int type){
		this.eid = eid;
		this.eventTime = eventTime;
		this.type = type;
	}

	public double getEventTime() {
		return eventTime;
	}

	public void setEventTime(double eventTime) {
		this.eventTime = eventTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getEid() {
		return eid;
	}

	public void setEid(int eid) {
		this.eid = eid;
	}
}
