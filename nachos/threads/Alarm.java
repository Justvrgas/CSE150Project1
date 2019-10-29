package nachos.threads;

import java.util.PriorityQueue;

import nachos.machine.*;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
	/**
	 * Allocate a new Alarm. Set the machine's timer interrupt handler to this
	 * alarm's callback.
	 *
	 * <p><b>Note</b>: Nachos will not function correctly with more than one
	 * alarm.
	 */
	public Alarm() {
		Machine.timer().setInterruptHandler(new Runnable() {
			public void run() { timerInterrupt(); }
		});
	}

	/**
	 * The timer interrupt handler. This is called by the machine's timer
	 * periodically (approximately every 500 clock ticks). Causes the current
	 * thread to yield, forcing a context switch if there is another thread
	 * that should be run.
	 */


	//Added by Justin Vargas 10/29
	//a queue of sleeping threads with their given time will ordered with the comparable interface implemented in the threadTime class
	private PriorityQueue<threadTime> threadsWaiting = new PriorityQueue<threadTime>();

	//Added by Justin Vargas 10/29
	//a thread that used as a placeholder in any method in the alarm class
	public KThread tempThread;

	public void timerInterrupt() {
		//Added by Justin Vargas 10/29
		//get the current time of the machine
		long time = Machine.timer().getTime();

		//Added by Justin Vargas 10/29
		//disable any interrupts
		Machine.interrupt().disable();

		//Added by Justin Vargas 10/29
		//while there are threads available in the threadsWaiting queue do the following
		while (!threadsWaiting.isEmpty()) {

			//Added by Justin Vargas 10/29
			//get the time of the first thread
			long threadTimeCheck = threadsWaiting.peek().time;

			//Added by Justin Vargas 10/29
			//check if the time of the first thread is less than the machines current thread
			if ( threadTimeCheck < time) {
				//Added by Justin Vargas 10/29
				//retrieve and remove the first thread in the threadsWaiting queue and now we have the thread and its corresponding wait time
				threadTime headThread = threadsWaiting.poll();

				//Added by Justin Vargas 10/29
				//in order to get the thread from the new headThread object we have to put it in a temporary Kthread variable
				tempThread = headThread.thread;

				//Added by Justin Vargas 10/29
				//check if it is null before setting to ready. if there is a case where the thread is null it would break the program
				tempThread.ready();
			}

			//break out of the loop if the time of the first thread in the queue is greater than the machines current time
			else {
				break;
			}
		}

		//Added by Justin Vargas 10/29
		//force a context switch if another thread is supposed to be set to ready
		KThread.currentThread().yield();

		//Added by Justin Vargas 10/29
		//enable interrupts
		Machine.interrupt().enable();
	}

	/**
	 * Put the current thread to sleep for at least <i>x</i> ticks,
	 * waking it up in the timer interrupt handler. The thread must be
	 * woken up (placed in the scheduler ready set) during the first timer
	 * interrupt where
	 *
	 * <p><blockquote>
	 * (current time) >= (WaitUntil called time)+(x)
	 * </blockquote>
	 *
	 * @param	x	the minimum number of clock ticks to wait.
	 *
	 * @see	nachos.machine.Timer#getTime()
	 */
	
	
	public void waitUntil(long x) {
		//Added by Justin Vargas 10/29
		//calculating the wake time of the thread
		long wakeTime = Machine.timer().getTime() + x;

		//Added by Justin Vargas 10/29
		//get the current thread that has to wait for the time above
		tempThread = KThread.currentThread();

		//Added by Justin Vargas 10/29
		//create a new threadTime object to pass in the time its supposed to wakeup and the thread
		threadTime newThread = new threadTime(wakeTime, tempThread);

		//Added by Justin Vargas 10/29
		//disable interrupts
		Machine.interrupt().disable();

		//Added by Justin Vargas 10/29
		//add to the sleeping thread queue 
		threadsWaiting.add(newThread);

		//Added by Justin Vargas 10/29
		//sleep the thread so that it can be woken up when the machine time reaches the wake time that is associated with it
		tempThread.sleep();

		//Added by Justin Vargas 10/29
		//disable interrupts 
		Machine.interrupt().enable();
	}

}

//Added by Justin Vargas 10/29
//created an object to be able to hold the time of the that implements the comparable interface so that the queue will be in order 
class threadTime implements Comparable<threadTime>{
	//Added by Justin Vargas 10/29
	//disable interrupts 
	long time;

	//Added by Justin Vargas 10/29
	//disable interrupts 
	KThread thread;

	//Added by Justin Vargas 10/29
	//disable interrupts 
	public threadTime(long time, KThread currentThread) {
		this.time = time;
		this.thread = currentThread;
	}

	//Added by Justin Vargas 10/29
	//implentation of the comparable interface that allows us to compare threads in the priority queue to make sure we get the correct order in the queue
	public int compareTo(threadTime timeComp) {

		//Added by Justin Vargas 10/29
		//check if the current threads time is greater than other threads in the queue
		if (this.time > timeComp.time) {
			//Added by Justin Vargas 10/29
			//returns the flag for the greater than comparison being true
			return 1;
		}
		//Added by Justin Vargas 10/29
		//check if the current threads time is less than other threads in the queue
		else {
			//Added by Justin Vargas 10/29
			//returns the flag for less than comparisons being true
			return -1;

		}



	}
}
