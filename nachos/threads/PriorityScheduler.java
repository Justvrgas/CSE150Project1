package nachos.threads;

import nachos.machine.*;

import java.util.LinkedList;

/**
 * A scheduler that chooses threads based on their priorities.
 *
 * <p>
 * A priority scheduler associates a priority with each thread. The next thread
 * to be dequeued is always a thread with priority no less than any other
 * waiting thread's priority. Like a round-robin scheduler, the thread that is
 * dequeued is, among all the threads of the same (highest) priority, the
 * thread that has been waiting longest.
 *
 * <p>
 * Essentially, a priority scheduler gives access in a round-robin fassion to
 * all the highest-priority threads, and ignores all other threads. This has
 * the potential to
 * starve a thread if there's always a thread waiting with higher priority.
 *
 * <p>
 * A priority scheduler must partially solve the priority inversion problem; in
 * particular, priority must be donated through locks, and through joins.
 */
public class PriorityScheduler extends Scheduler {
	/**
	 * Allocate a new priority scheduler.
	 */
	public PriorityScheduler() {
	}

	/**
	 * Allocate a new priority thread queue.
	 *
	 * @param	transferPriority	<tt>true</tt> if this queue should
	 *					transfer priority from waiting threads
	 *					to the owning thread.
	 * @return	a new priority thread queue.
	 */
	public ThreadQueue newThreadQueue(boolean transferPriority) {
		return new PriorityQueue(transferPriority);
	}

	public int getPriority(KThread thread) {
		Lib.assertTrue(Machine.interrupt().disabled());
		return getThreadState(thread).getPriority();
	}

	public int getEffectivePriority(KThread thread) {
		Lib.assertTrue(Machine.interrupt().disabled());
		return getThreadState(thread).getEffectivePriority();
	}

	public void setPriority(KThread thread, int priority) {
		Lib.assertTrue(Machine.interrupt().disabled());
		Lib.assertTrue(priority >= priorityMinimum &&
				priority <= priorityMaximum);
		getThreadState(thread).setPriority(priority);
	}

	public boolean increasePriority() {
		boolean intStatus = Machine.interrupt().disable();
		KThread thread = KThread.currentThread();
		int priority = getPriority(thread);
		if (priority == priorityMaximum) {
			return false;
		}
		setPriority(thread, priority+1);
		Machine.interrupt().restore(intStatus);
		return true;
	}

	public boolean decreasePriority() {
		boolean intStatus = Machine.interrupt().disable();

		KThread thread = KThread.currentThread();

		int priority = getPriority(thread);
		if (priority == priorityMinimum)
			return false;

		setPriority(thread, priority-1);

		Machine.interrupt().restore(intStatus);
		return true;
	}

	/**
	 * The default priority for a new thread. Do not change this value.
	 */
	public static final int priorityDefault = 1;
	/**
	 * The minimum priority that a thread can have. Do not change this value.
	 */
	public static final int priorityMinimum = 0;
	/**
	 * The maximum priority that a thread can have. Do not change this value.
	 */
	public static final int priorityMaximum = 7;

	/**
	 * Return the scheduling state of the specified thread.
	 *
	 * @param	thread	the thread whose scheduling state to return.
	 * @return	the scheduling state of the specified thread.
	 */
	protected ThreadState getThreadState(KThread thread) {
		if (thread.schedulingState == null)
			thread.schedulingState = new ThreadState(thread);

		return (ThreadState) thread.schedulingState;
	}

	/**
	 * A <tt>ThreadQueue</tt> that sorts threads by priority.
	 */

	/*
		 ######                                           #####                              
		 #     # #####  #  ####  #####  # ##### #   #    #     # #    # ###### #    # ###### 
		 #     # #    # # #    # #    # #   #    # #     #     # #    # #      #    # #      
		 ######  #    # # #    # #    # #   #     #      #     # #    # #####  #    # #####  
		 #       #####  # #    # #####  #   #     #      #   # # #    # #      #    # #      
		 #       #   #  # #    # #   #  #   #     #      #    #  #    # #      #    # #      
		 #       #    # #  ####  #    # #   #     #       #### #  ####  ######  ####  ###### 

	 */
	protected class PriorityQueue extends ThreadQueue {
		/*Added By Justin Vargas 10/30: New variables added */

		//Added By Justin Vargas 10/30
		//this linked list will hold all of the threads
		private LinkedList<KThread> waitingThreads = new LinkedList<KThread>();

		//Added By Justin Vargas 10/30
		//creating a new object of type ThreadState to be able to reference in the priority queue class
		private ThreadState tempState = null;

		//Added By Justin Vargas 10/30
		//this boolean is used to identify if a thread needs to transfer priority becasue it is dependent on a low priority thread
		private boolean marker;

		//Added By Justin Vargas 10/30
		//the result of getting the effective priority from the helper function
		private int effectivePriority;

		//Added By Justin Vargas 10/30
		//set the flag that indicates whether or not the thread needs to transfer priority
		public void setMarker() {

			if(transferPriority == false) {
				return;
			}
			else {
				marker = true;
				
				if(tempState != null) {
					tempState.setMarker();
				}
			}
			
		}

		//Added By Justin Vargas 10/30
		//helper function to get the effective priority
		public int getEffectivePriority() {

			if(transferPriority == false) {
				return priorityMinimum;
			}
			else if(marker) {
				effectivePriority = priorityMinimum;
				int index = 0;
				while(index < waitingThreads.size() && !waitingThreads.isEmpty()){  
					KThread thread = waitingThreads.get(index); 
					int priority = getThreadState(thread).getEffectivePriority();
					if( priority > effectivePriority) { 
						effectivePriority = priority;
					}
					index++;
				}
				marker = false;
			}
			return effectivePriority;
		}



		/**
		 * <tt>true</tt> if this queue should transfer priority from waiting
		 * threads to the owning thread.
		 */
		public boolean transferPriority;


		PriorityQueue(boolean transferPriority) {
			this.transferPriority = transferPriority;
		}

		public void waitForAccess(KThread thread) {
			Lib.assertTrue(Machine.interrupt().disabled());
			getThreadState(thread).waitForAccess(this);
		}

		//Added By Justin Vargas 10/30
		//Notify this thread queue that a thread has received access, without going through request() and nextThread(). 
		//For example, if a thread acquires a lock that no other threads are waiting for, it should call this method.

		public void acquire(KThread thread) {
			Lib.assertTrue(Machine.interrupt().disabled());
			//Added By Justin Vargas 10/30
			//creating a new variable of type ThreadState that is equal to the ThreadState of the thread passed in
			ThreadState tempState = getThreadState(thread);
			//Added By Justin Vargas 10/30
			//creating a new variable of type threadState that is the state of the PriorityQueue's thread (tempState)
			ThreadState currentState = this.tempState;
			//Added By Justin Vargas 10/30
			//if the current state of the PriorityQueue's thread is not null 
			if(currentState != null) {
				//Added By Justin Vargas 10/30
				//if the tranfser priority is true
				if (this.transferPriority) {
					//Added By Justin Vargas 10/30
					//remove the current states thread from the available threads queue
					currentState.availableThreads.remove(this);
				}
			}
			//Added By Justin Vargas 10/30
			//set PriorityQueue's temporary threadState(tempState) to the state of the passed thread
			this.tempState = tempState; 
			//Added By Justin Vargas 10/30
			//the thread passed in has aquired access from the thread in the waitingThreads queue
			tempState.acquire(this);
		}

		//Added by Justin Vargas 10/29
		/*
		 * Notify this thread queue that another thread can receive access. Choose and return the next thread to receive access, 
		 * or null if there are no threads waiting.If the limited access object transfers priority, and if there are 
		 * other threads waiting for access, then they will donate priority to the returned thread.
		 */
		public KThread nextThread() {
			Lib.assertTrue(Machine.interrupt().disabled());
			//Added By Justin Vargas 10/30
			//if there are no more threads return null
			if(waitingThreads.isEmpty()) {
				return null;
			}
			//Added By Justin Vargas 10/30
			//get the next thread from the queue this will also make sure its priority is correct 
			KThread nextThread = pickNextThread();
			//Added By Justin Vargas 10/30
			//if the thread from the pickNextThread method is not null do the following
			if(pickNextThread() != null) {
				//Added By Justin Vargas 10/30
				//remove the same thread from the waiting thread queue 
				waitingThreads.remove(nextThread);
				//Added By Justin Vargas 10/30
				//aquire the state of the nextThread
				getThreadState(nextThread).acquire(this);
			}
			//Added By Justin Vargas 10/30
			//now return the nextThread with the thread and its state
			return nextThread;
		}

		/**
		 * Return the next thread that <tt>nextThread()</tt> would return,
		 * without modifying the state of this queue.
		 *
		 * @return	the next thread that <tt>nextThread()</tt> would
		 *		return.
		 */
		//Added By Justin Vargas 10/30
		//Return the next thread that nextThread() would return, without modifying the state of the waiting threads queue
		protected KThread pickNextThread() {
			//Added By Justin Vargas 10/30
			//declare a new KThread called nextThread
			KThread nextThread = null;
			//Added By Justin Vargas 10/30
			//used to to iterate over the threads in the waiting queue
			int index = 0;
			//Added By Justin Vargas 10/30
			//check if the queue is empty or if the index is less that the size
			while(!waitingThreads.isEmpty() && index < waitingThreads.size()) { 
				//Added By Justin Vargas 10/30
				//set a new KThread value to the next available thread in the queue
				KThread thread = waitingThreads.get(index); 
				//Added By Justin Vargas 10/30
				//get the priority of thread
				int priority = getThreadState(thread).getEffectivePriority();
				
				//Added By Justin Vargas 10/30
				//check if the the thread is null or if the priority is greater than the nextThreads priority
				if(nextThread == null || priority > getThreadState(nextThread).getEffectivePriority()) { 
					//Added By Justin Vargas 10/30
					//set the next thread to be the next thread in the queue
					nextThread = thread;
				}
				//Added By Justin Vargas 10/30
				//continue on to the next thread
				index++;
			}
			//Added By Justin Vargas 10/30
			//return the next thread in the queue
			return nextThread;
		}

		//skipping
		public void print() {
			Lib.assertTrue(Machine.interrupt().disabled());
		}




	}

	/**
	 * The scheduling state of a thread. This should include the thread's
	 * priority, its effective priority, any objects it owns, and the queue
	 * it's waiting for, if any.
	 *
	 * @see	nachos.threads.KThread#schedulingState
	 */

	/*
	 #######                                        #####                            
	    #    #    # #####  ######   ##   #####     #     # #####   ##   ##### ###### 
	    #    #    # #    # #       #  #  #    #    #         #    #  #    #   #      
	    #    ###### #    # #####  #    # #    #     #####    #   #    #   #   #####  
	    #    #    # #####  #      ###### #    #          #   #   ######   #   #      
	    #    #    # #   #  #      #    # #    #    #     #   #   #    #   #   #      
	    #    #    # #    # ###### #    # #####      #####    #   #    #   #   ###### 

	 */
	protected class ThreadState {
		/**
		 * Allocate a new <tt>ThreadState</tt> object and associate it with the
		 * specified thread.
		 *
		 * @param	thread	the thread this state belongs to.
		 */
		
		/** The thread with which this object is associated. */
		protected KThread thread;
		/** The priority of the associated thread. */
		protected int priority;

		//Added By Justin Vargas 10/30
		//variable to hold the effective priority calculated
		protected int effectivePriority;
		//Added By Justin Vargas 10/30
		//created a linked list that held thread queue objects
		protected LinkedList<ThreadQueue> availableThreads = new LinkedList<ThreadQueue>(); 
		//Added By Justin Vargas 10/30
		//stores all the queue that are dependent on other threads
		protected ThreadQueue dependentsQueue; 
		//Added By Justin Vargas 10/30
		//flag used to check if the threads need to change pririty or if the queue needs to change order
		private boolean marker = false; 
		
		public ThreadState(KThread thread) {
			this.thread = thread;

			setPriority(priorityDefault);
		}

		/**
		 * Return the priority of the associated thread.
		 *
		 * @return	the priority of the associated thread.
		 */
		public int getPriority() {
			return priority;
		}

		/**
		 * Return the effective priority of the associated thread.
		 *
		 * @return	the effective priority of the associated thread.
		 */
		//added by justin vargas 10/30
		//just like the helper function for priority queue
		public int getEffectivePriority() {
			
			int priority = this.priority;
			
			if(marker) {
				int index = 0;
				while(index < availableThreads.size()) {  
					PriorityQueue effectivePriorities = (PriorityQueue)(availableThreads.get(index)); 
					int effective = effectivePriorities.getEffectivePriority();
					if(priority < effective) {
						priority = effective;
					}
					index++;
					
				}
			}
			return priority;
		}

		/**
		 * Set the priority of the associated thread to the specified value.
		 *
		 * @param	priority	the new priority.
		 */
		//added by justin vargas 10/30
		//same as the helper function for priority queue
		public void setPriority(int priority) {
			if (this.priority == priority)
				return;
			this.priority = priority;
			//Added By Justin Vargas 10/30
			//tell thread to change its priority
			setMarker();
		}

		/**
		 * Called when <tt>waitForAccess(thread)</tt> (where <tt>thread</tt> is
		 * the associated thread) is invoked on the specified priority queue.
		 * The associated thread is therefore waiting for access to the
		 * resource guarded by <tt>waitQueue</tt>. This method is only called
		 * if the associated thread cannot immediately obtain access.
		 *
		 * @param	waitQueue	the queue that the associated thread is
		 *				now waiting on.
		 *
		 * @see	nachos.threads.ThreadQueue#waitForAccess
		 */
		public void waitForAccess(PriorityQueue waitQueue) {
			//Added By Justin Vargas 10/30
			//disable interrupts
			Lib.assertTrue(Machine.interrupt().disabled());
			Lib.assertTrue(waitQueue.waitingThreads.indexOf(thread) == -1);
			//Added By Justin Vargas 10/30
			//add the current thread to the wait queue
			waitQueue.waitingThreads.add(thread);
			//Added By Justin Vargas 10/30
			//check if the thread needs to change its priority
			waitQueue.setMarker();
			//Added By Justin Vargas 10/30
			//add the thread to the queue of dependent threads
			dependentsQueue = waitQueue;
			if(availableThreads.indexOf(waitQueue) != -1) {
				//Added By Justin Vargas 10/30
				//remove 
				availableThreads.remove(waitQueue);
				//Added By Justin Vargas 10/30
				//
				waitQueue.tempState = null;
			}
		}

		/**
		 * Called when the associated thread has acquired access to whatever is
		 * guarded by <tt>waitQueue</tt>. This can occur either as a result of
		 * <tt>acquire(thread)</tt> being invoked on <tt>waitQueue</tt> (where
		 * <tt>thread</tt> is the associated thread), or as a result of
		 * <tt>nextThread()</tt> being invoked on <tt>waitQueue</tt>.
		 *
		 * @see	nachos.threads.ThreadQueue#acquire
		 * @see	nachos.threads.ThreadQueue#nextThread
		 */
		public void acquire(PriorityQueue waitQueue) {
			//Added By Justin Vargas 10/30
			//disable interrupts
			Lib.assertTrue(Machine.interrupt().disabled());
			//Added By Justin Vargas 10/30
			//add the waitQueue to the available threads
			availableThreads.add(waitQueue);
			//Added By Justin Vargas 10/30
			//check for duplicates
			if(waitQueue == dependentsQueue) {
				//Added By Justin Vargas 10/30
				//set the dependent thread to null because it is now in the wait queue
				dependentsQueue = null;
			}
			//Added By Justin Vargas 10/30
			//check if the priority needs to be changed so that the order of the queue can be changed
			setMarker();
		}


		//Added By Justin Vargas 10/30
		//just like the helper function in priority queue, it helps keep the priority queue in order and lets 
		//the thread know if theyt to need to change priority
		public void setMarker() {
			if(marker) {
				return;
			}
			marker = true;
			PriorityQueue effectivePriorities = (PriorityQueue)dependentsQueue;
			if(effectivePriorities != null) {
				effectivePriorities.setMarker();
			}
		}
		 
	}
}  