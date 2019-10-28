package nachos.threads;

import java.util.LinkedList;

import nachos.machine.*;

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
	LinkedList<Lock> waitQueue = new LinkedList<Lock>();
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
		waitQueue = new LinkedList<Lock>();
		//CODE HERE
		    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    public void sleep() {
    //CODE HERE  
	    Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	    
	    Lock waiter = new Lock();
	    waitQueue.add(waiter);
	    
		conditionLock.release();
		waiter.acquire();
		conditionLock.acquire();
		    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		//CODE HERE
		if (!waitQueue.isEmpty())
		    ((Lock)waitQueue.removeFirst()).release();
		    }



    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		//CODE HERE
		while (!waitQueue.isEmpty())
		   	waitQueue.pop();
	    }

    private Lock conditionLock;
}