package nachos.threads;

import java.util.LinkedList;


/**
* A <i>communicator</i> allows threads to synchronously exchange 32-bit
* messages. Multiple threads can be waiting to <i>speak</i>,
* and multiple threads can be waiting to <i>listen</i>. But there should never
* be a time when both a speaker and a listener are waiting, because the two
* threads can be paired off at this point.
*/
public class Communicator {
/**
* Allocate a new communicator.
*/

public Communicator() {
//Added by Jake 10/28
//Edited by Justin Vargas 10/28
//Initialize lock to provide atomicity
lock = new Lock();
//Creates new condition2 to check before doing anything without using semaphores
Speakers = new Condition2(lock);
Listeners = new Condition2(lock);
//Create counters for listen and speak to check if they've been used
speakerCount = 0;
listenerCount = 0;
}

/**
* Wait for a thread to listen through this communicator, and then transfer
* <i>word</i> to the listener.
*
* <p>
* Does not return until this thread is paired up with a listening thread.
* Exactly one listener should receive <i>word</i>.
*
* @param word the integer to transfer.
*/
public void speak(int word) {
//Added by Jake 10/28
//Acquire atomicity
lock.acquire();
//Increment Speak Counter since you use the speak function
speakerCount++;
//Wait for the listen function to be called
while(listenerCount <= 0){
Speakers.sleep();
}
listenerCount--;
//Add word to front the number list, this way when we poll we always remove the correct value
number.add(word);
//Get listen function ready to work
Listeners.wake();
//Sleep speaker function
Speakers.sleep();
//Release atomicity
lock.release();
}

/**
* Wait for a thread to speak through this communicator, and then return
* the <i>word</i> that thread passed to <tt>speak()</tt>.
*
* @return the integer transferred.
*/
public int listen() {
//Addded by Jake 10/28
//Initialize temp value for the transfer coming up
int temp = 0;
//Acquire atomicity
lock.acquire();
//Increment listen counter since listen is called
listenerCount++;
//If speak isn't called wait
while(speakerCount <= 0){
Listeners.sleep();
}
//If the word wasn't given to number list you set speaker to ready, and decrement speaker since this
//speaker wasn't transfering any data, and you set listen to wait again
if(number.isEmpty()) {
Speakers.wake();
speakerCount--;
Listeners.sleep();
}
//Else, you decrement speaker and continue with the transfer
//Else statement is there to insure you don't take more than necessary
else {
speakerCount--;
}
//Set temp to the first element and remove it if there is an element to use
if(!number.isEmpty())
temp = number.pollFirst().intValue();
//Set speaker to ready
Speakers.wake();
//Release atomicity
lock.release();
//Return the transfer value
return temp;
}
//Added by Justin Vargas 10/28: create private variables in the public class communicator so that the methods can access the variables
//Edited by Jake 10/29 : Initializing everything like in Condition 2
private Lock lock;
private Condition2 Speakers;
private Condition2 Listeners;
private LinkedList<Integer> number = new LinkedList<Integer>();
private int speakerCount;
private int listenerCount;
}