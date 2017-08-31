/*
 * Author: Filipe Moreira and Pedro Pio
 */

package core;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

import javax.swing.plaf.synth.SynthSeparatorUI;

import util.Constants;
import util.Exponential;
import util.FileReader;
import util.Logger;

public class Simulator {
	private double totalSimulationTime; // In seconds
	private double quantumSize; // In microseconds
	private double contextSwitchTime; // In microseconds
	private double averageProcessLength; // In microseconds
	private double averageProcessCreationInterval; // In microseconds
	private double IOBoundPercentage; // Between 1 and 100
	private double averageIOServiceTime; // In microseconds

	private double clock;
	private int processCount;
	private int eventCount;
	private int completedProcesses;
	private int completedCPUBound;
	private int turnaroundSum;
	private int waitingtimeSum;
	private int IOServiceTimeSum;

	private int turnaroundCPUSum;
	private int waitingtimeCPUSum;

	private int contextSwitches;

	private int cpuUtilization;
	
	private Logger logger;

	public Simulator() throws IOException, Exception {
		int parameters[] = new FileReader().readFile(Constants.CONFIG_FILE_PATH);

		totalSimulationTime = parameters[0];
		quantumSize = parameters[1];
		contextSwitchTime = parameters[2];
		averageProcessLength = parameters[3];
		averageProcessCreationInterval = parameters[4];
		IOBoundPercentage = parameters[5];
		averageIOServiceTime = parameters[6];

		processCount = 0;
		eventCount = 0;
		cpuUtilization = 0;
		completedProcesses = 0;
		completedCPUBound = 0;
		waitingtimeSum = 0;
		IOServiceTimeSum = 0;
		
		logger = new Logger();

		System.out.println("Total Sim Time: " + totalSimulationTime + " s");
		logger.log("Total Sim Time: " + totalSimulationTime + " s");
		System.out.println("Quantum Size: " + quantumSize + " us");
		logger.log("Quantum Size: " + quantumSize + " us");
		System.out.println("Context Switch Time: " + contextSwitchTime + " us");
		logger.log("Context Switch Time: " + contextSwitchTime + " us");
		System.out.println("Average Process Length: " + averageProcessLength + " us");
		logger.log("Average Process Length: " + averageProcessLength + " us");
		System.out.println("Average Process Creation Interval: " + averageProcessCreationInterval + " us");
		logger.log("Average Process Creation Interval: " + averageProcessCreationInterval + " us");
		System.out.println("I/O Bound Percentage: " + IOBoundPercentage + " %");
		logger.log("I/O Bound Percentage: " + IOBoundPercentage + " %");
		System.out.println("Average I/O Service Time: " + averageIOServiceTime + " us\n");
		logger.log("Average I/O Service Time: " + averageIOServiceTime + " us\n");
	}

	public void simulate() {
		// whole bunch of setup stuff
		clock = 0;
		contextSwitches = 0;
		//create priority queue of events
		MyPriorityQueue eventQueue = new MyPriorityQueue();
		
		//create queue of ready process and waiting for I/O 
		MyQueue readyQueue = new MyQueue();
		MyQueue waitingForIO = new MyQueue();

		//create process on CPU
		Process processOnCPU = null;

		Stack<Event> doneEvents = new Stack<Event>();
		
		//create the first event
		Event event = new Event(++eventCount, clock, 0);

		// System.out.println("Clock\teid\tpid\tEType\t\t\tProc. Type\tNext
		// Event\tProcess Info");

		Event nextEvent;

		// main event loop
		while (clock < totalSimulationTime * 1000000) {
			//if CPU is empty it will call the next process on the ready queue to the CPU
			if (processOnCPU == null) {
				// Pop ready queue
				//
				//get the next process of the ready queue and moves it to CPU
				//if the ready queue is not empty
				if (readyQueue.peek() != null) {
					contextSwitch();
					processOnCPU = readyQueue.poll();
					
					//Actualize process 
					processOnCPU
							.setWaitingTime(processOnCPU.getWaitingTime() + (clock - processOnCPU.getRedyqueueStart()));
					processOnCPU.setRedyqueueStart(0);
					
					//create next event 
					//if process is done create a type 5 (Process is over) event
					//if process is I/O Bound create a type 3 (waiting for I/O) event
					//if process is CPU Bound create a type 2 (quantum expire) event
					if (processOnCPU.getTotalCPUTime() - processOnCPU.getCpuBurst() <= 0) {
						nextEvent = new Event(++eventCount, clock + processOnCPU.getTotalCPUTime(), 5);
						eventQueue.add(nextEvent);
					} else if (processOnCPU.isIOBound()) {
						nextEvent = new Event(++eventCount, clock + processOnCPU.getCpuBurst(), 3);
						eventQueue.add(nextEvent);
					} else {
						nextEvent = new Event(++eventCount, clock + processOnCPU.getCpuBurst(), 2);
						eventQueue.add(nextEvent);
					}

					// System.out.println(processOnCPU.getPid()+" went to CPU
					// from the ready queue");
				}
			}
			
			//create a new process
			if (event.getType() == 0) {
				// Creation
				double processLength = new Exponential().exponential((int)averageProcessLength);
				boolean IO = (Math.random() * 100) <= IOBoundPercentage;
				double cpuBurst;
				//calculate the CPU-bust of the process
				if (IO) {
					cpuBurst = (int) Math.round((Math.random() * 2000) + 2000);
				} else {
					cpuBurst = processLength < quantumSize ? processLength : quantumSize;
				}
				//create new process and move it to ready queue
				Process newProcess = new Process(processLength, IO, cpuBurst, ++processCount, clock);
				readyQueue.add(newProcess);
				newProcess.setRedyqueueStart(clock);
				//create next event and allocate it in the ready queue
				nextEvent = new Event(++eventCount,
						clock + (new Exponential().exponential((int)averageProcessCreationInterval)), 0);
				eventQueue.add(nextEvent);
				//clock control
				nextEvent = new Event(++eventCount, clock, 1);
				eventQueue.add(nextEvent);

				System.out.println(
						"Time " + (int)clock + "\tEvent 'New Process': pid=" + processCount + " totalCPU=" + processLength
								+ " " + (IO ? "I/O-bound" : "CPU-bound") + " next New at\t" + (int)nextEvent.getEventTime());
				
				logger.log("Time " + (int)clock + "\tEvent 'New Process': pid=" + processCount + " totalCPU=" + processLength
						+ " " + (IO ? "I/O-bound" : "CPU-bound") + " next New at\t" + (int)nextEvent.getEventTime());
			}
			//if quantum expire and is CPU bound move process to ready queue
			else if (event.getType() == 2) {
				// Quantum Expired
				Process temp = processOnCPU;
				 
				cpuUtilization += temp.getCpuBurst();

				double newLength = temp.getTotalCPUTime() - temp.getCpuBurst();
				temp.setTotalCPUTime(newLength);

				double newCpuBurst = newLength < quantumSize ? newLength : quantumSize;
				temp.setCpuBurst(newCpuBurst);

				temp.setRedyqueueStart(clock);
				readyQueue.add(temp);

				nextEvent = new Event(++eventCount, clock, 1);
				eventQueue.add(nextEvent);

				// System.out.println(processOnCPU.getPid()+" quantum expired,
				// getting out od CPU");

				processOnCPU = null;
			}
			//
			else if (event.getType() == 3) {
				// Process on CPU is leaving for IO
				Process temp = processOnCPU;

				cpuUtilization += temp.getCpuBurst();

				temp.setTotalCPUTime(temp.getTotalCPUTime() - temp.getCpuBurst());

				double serviceTime = (new Exponential().exponential((int)averageIOServiceTime));
				temp.setIOTime(temp.getIOTime() + serviceTime);

				waitingForIO.add(temp);

				nextEvent = new Event(++eventCount, clock + serviceTime, 4);
				eventQueue.add(nextEvent);

				nextEvent = new Event(++eventCount, clock, 1);
				eventQueue.add(nextEvent);

				// System.out.println(processOnCPU.getPid()+" left CPU for
				// I/O");

				processOnCPU = null;
			}

			else if (event.getType() == 4) {
				// Process on the head of the IO queue was served
				Process temp = waitingForIO.poll();

				temp.setCpuBurst(Math.round((Math.random() * 2000) + 2000));

				// System.out.println(temp.getPid()+" was I/O served an is now
				// at ready queue");

				temp.setRedyqueueStart(clock);
				readyQueue.add(temp);
			}

			else if (event.getType() == 5) {
				// Process on CPU is over

				cpuUtilization += processOnCPU.getCpuBurst();

				System.out.println(
						"Time " + (int) clock + "\tEvent 'Process Finished': pid=" + processOnCPU.getPid() + " totalCPU="
								+ processOnCPU.getCPU() + " " + (processOnCPU.isIOBound() ? "I/O-bound" : "CPU-bound"));

				logger.log("Time " + (int) clock + "\tEvent 'Process Finished': pid=" + processOnCPU.getPid() + " totalCPU="
						+ processOnCPU.getCPU() + " " + (processOnCPU.isIOBound() ? "I/O-bound" : "CPU-bound"));
				
				nextEvent = new Event(++eventCount, clock, 1);
				eventQueue.add(nextEvent);

				completedProcesses++;
				if (!processOnCPU.isIOBound()) {
					completedCPUBound++;
					turnaroundCPUSum += (clock - processOnCPU.getStart());
					waitingtimeCPUSum += processOnCPU.getWaitingTime();
				} else {
					IOServiceTimeSum += processOnCPU.getIOTime();
				}

				waitingtimeSum += processOnCPU.getWaitingTime();

				turnaroundSum += (clock - processOnCPU.getStart());

				processOnCPU = null;
			}
			
			event = eventQueue.poll();
			clock = event.getEventTime();

		}

		System.out.println("");
		logger.log("");
		System.out.println("# of created processes:\t\t" + processCount);
		logger.log("# of created processes:\t\t" + processCount);
		System.out.println("CPU Utilization: \t\t" + ((cpuUtilization / (double) (totalSimulationTime * 1000000)) * 100)
				+ "% ( " + cpuUtilization + " us )");
		logger.log("CPU Utilization: \t\t" + ((cpuUtilization / (double) (totalSimulationTime * 1000000)) * 100)
				+ "% ( " + cpuUtilization + " us )");
		System.out.println("Time in context switches: \t" + (contextSwitches * contextSwitchTime) + " us");
		logger.log("Time in context switches: \t" + (contextSwitches * contextSwitchTime) + " us");
		System.out.println("Completed Processes:\t\t" + completedProcesses);
		logger.log("Completed Processes:\t\t" + completedProcesses);

		if (completedProcesses != 0) {
			System.out.println("Average Turnaround Time:\t" + (turnaroundSum / completedProcesses) + " us");
			logger.log("Average Turnaround Time:\t" + (turnaroundSum / completedProcesses) + " us");
			System.out.println("Average Time on Ready Queue:\t" + (waitingtimeSum / completedProcesses) + " us");
			logger.log("Average Time on Ready Queue:\t" + (waitingtimeSum / completedProcesses) + " us");
		}
		System.out.println("\n-- CPU Bound --");
		logger.log("\n-- CPU Bound --");
		System.out.println("Completed CPU Bound Processes:\t" + completedCPUBound);
		logger.log("Completed CPU Bound Processes:\t" + completedCPUBound);

		if (completedCPUBound != 0) {
			System.out.println("Average Turnaround Time:\t" + (turnaroundCPUSum / completedCPUBound) + " us");
			logger.log("Average Turnaround Time:\t" + (turnaroundCPUSum / completedCPUBound) + " us");
			System.out.println("Average Waiting Time:\t\t" + (waitingtimeCPUSum / completedCPUBound) + " us");
			logger.log("Average Waiting Time:\t\t" + (waitingtimeCPUSum / completedCPUBound) + " us");
		}

		System.out.println("\n-- I/O Bound --");
		logger.log("\n-- I/O Bound --");
		System.out.println("Completed I/O Bound Processes:\t" + (completedProcesses - completedCPUBound));
		logger.log("Completed I/O Bound Processes:\t" + (completedProcesses - completedCPUBound));

		if ((completedProcesses - completedCPUBound) != 0) {
			System.out.println("Average Turnaround Time:\t"
					+ ((turnaroundSum - turnaroundCPUSum) / (completedProcesses - completedCPUBound)) + " us");
			logger.log("Average Turnaround Time:\t"
					+ ((turnaroundSum - turnaroundCPUSum) / (completedProcesses - completedCPUBound)) + " us");
			System.out.println("Average Waiting Time:\t\t"
					+ ((waitingtimeSum - waitingtimeCPUSum) / (completedProcesses - completedCPUBound)) + " us");
			logger.log("Average Waiting Time:\t\t"
					+ ((waitingtimeSum - waitingtimeCPUSum) / (completedProcesses - completedCPUBound)) + " us");
			System.out.println("Average I/O Service Time:\t"
					+ (IOServiceTimeSum / (completedProcesses - completedCPUBound)) + " us");
			logger.log("Average I/O Service Time:\t"
					+ (IOServiceTimeSum / (completedProcesses - completedCPUBound)) + " us");
		}

		System.out.println("\nEnd of Simulation.");
		logger.log("\nEnd of Simulation.");
		
		try {
			logger.save();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	private void contextSwitch() {
		clock += contextSwitchTime;
		contextSwitches++;
	}
}
