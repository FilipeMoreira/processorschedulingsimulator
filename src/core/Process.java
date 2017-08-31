/*
 * Author: Filipe Moreira and Pedro Pio
 */

package core;

import java.util.Date;

public class Process {
	private double totalCPUTime; // In microseconds
	private boolean IOBound;
	private double cpuBurst;
	private int pid;
	private double CPU;
	
	private double start;
	
	private double waitingTime, redyqueueStart;
	private double IOTime;
	
	public Process(double totalCPUTime, boolean IOBound, double cpuBurst, int pid, double start){
		this.totalCPUTime = totalCPUTime;
		this.CPU = totalCPUTime;
		this.IOBound = IOBound;
		this.cpuBurst = cpuBurst;
		this.pid = pid;
		this.start = start;
		this.waitingTime = 0;
		this.IOTime = 0;
	}
	
	public double getTotalCPUTime() {
		return totalCPUTime;
	}

	public void setTotalCPUTime(double totalCPUTime) {
		this.totalCPUTime = totalCPUTime;
	}
	
	public double getCPU() {
		return CPU;
	}

	public boolean isIOBound() {
		return IOBound;
	}

	public void setIOBound(boolean iOBound) {
		IOBound = iOBound;
	}

	public double getCpuBurst() {
		return cpuBurst;
	}

	public void setCpuBurst(double cpuBurst) {
		this.cpuBurst = cpuBurst;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public double getStart() {
		return start;
	}

	public void setStart(double start) {
		this.start = start;
	}

	public double getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(double waitingTime) {
		this.waitingTime = waitingTime;
	}

	public double getRedyqueueStart() {
		return redyqueueStart;
	}

	public void setRedyqueueStart(double redyqueueStart) {
		this.redyqueueStart = redyqueueStart;
	}

	public double getIOTime() {
		return IOTime;
	}

	public void setIOTime(double iOTime) {
		IOTime = iOTime;
	}

}
