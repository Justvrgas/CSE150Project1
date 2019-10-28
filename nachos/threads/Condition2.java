package nachos.threads;

import java.util.Currency;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import nachos.machine.*;
import nachos.threads.PriorityScheduler.ThreadState;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see nachos.threads.Condition
 */
public class Condition2 {
	
	//added by Justin Vargas 10/27
	//using queue to make sure that threads are first in and first out
	Queue<KThread> threadQueue = new LinkedList<KThread>();
	
	
	
	/**
     * Allocate a new condition variable.
     *
     * @param conditionLock the lock associated with this condition
     *    variable. The current thread must hold this
     *    lock whenever it uses <tt>sleep()</tt>,
     *    <tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
    public Condition2(Lock conditionLock) {
		this.conditionLock = conditionLock;
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    
    
    
    public void sleep() {
	    Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	    
	    //Added by Justin Vargas 10/27
	    //we first release the associated lock
	    conditionLock.release();
	   
	    //Added by Justin Vargas 10/27
	    //disable any machine interrupts
	    Machine.interrupt().disable();
	    
	    //Added by Justin Vargas 10/27
	    //make a new thread and make it the current thread
	    KThread sleepyThread = KThread.currentThread();
	    
	    //Added by Justin Vargas 10/27
	    //add the current thread into the stack
	    threadQueue.add(sleepyThread);
	    
	    //Added by Justin Vargas 10/27
	    //once added to the queue we make the current thread sleep on this condition variable until another thread wakes it
	    sleepyThread.sleep();
	   
	 	//Added by Justin Vargas 10/27
	    //let the current thread reaquire the lock
		conditionLock.acquire();
	    
		//Added by Justin Vargas 10/27
	    //enable any machine interrupts
		Machine.interrupt().enable();
		
		
		
	}

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    
    
    
    
    public void wake() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		Machine.interrupt().disable();
		//CODE HERE
		if (!threadQueue.isEmpty()) {
			//Added by Justin Vargas 10/27
		    //awaken the first asleep thread in the queue by removing it from the sleeping queue and setting it to ready
		    KThread awakenThread = threadQueue.remove();
		    awakenThread.ready();
		}
			
		Machine.interrupt().enable();
    }

    	


    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    
    public void wakeAll() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		//CODE HERE
		while (!threadQueue.isEmpty()) {
			//Added by Justin Vargas 10/27
		    //awaken all threads in the queue by removing it from the sleeping queue and set them to ready
			KThread awakenThread = threadQueue.remove();
		    awakenThread.ready();
		}
	}

    private Lock conditionLock;
    
}