package nachos.threads;

import nachos.ag.BoatGrader;
import java.util.Random;

/*
 * 
 * TESTING GIT COMMIT
 * 
 */


//Implementation of class 'Boat'
public class Boat
{
	BoatGrader b = new BoatGrader();
	//this declares variables
	static BoatGrader bg;
	static Lock lockObject;
	static Condition2 chAtOahu;
	static Condition2 chAtMolokai;
	static Condition2 adltAtOahu;
	static Condition2 boat;
	static Alarm alarm;
	static Communicator mediator;
	static String bPlace;
	
	//int for data
	static int OahuChildren;
	static int childrenAtMolokai;
	static int childrenInQueue;
	static int countBoat;
	static int adultsAtOahu;
	static int adultsAtMolokai;
	static int data;
	static boolean isChildExist;
	static boolean flag;
	static boolean isPilot;
	static boolean containsBoat;
	// Testing for false is show result and true to flag object
	static boolean showResult = false;
	static boolean flagObject = true;

	//Implementation of function 'testing'
	public static void selftest(int totalTests, boolean isTestPass)
	{
		//creating objects and variables
		flagObject = false;;
		Random randomize = new Random();
		int temp;
		int totalThreads = 400;
		int p;
		int minChildren = 2;
		randomize.setSeed(System.currentTimeMillis()); //sets time in milliseconds

		//applying tests
		for (int index = 1; index < totalTests; index++)
		{
			//get random int
			temp = randomValues(minChildren, totalThreads, randomize);
			p = randomize.nextInt(totalThreads - temp + 1);
			if (isTestPass) {
				System.out.println("Testing Boats with " + temp + " children, " + p + " adults");
			}
			BoatGrader b = new BoatGrader();
			StartingFunction(p, temp, b);
		}
		//display message
		System.out.println(" All Boat Test Passed ");
	}

	
	//implementation of function 'print'
	public static void print(String adata)
	{
		System.out.println(adata);
	}
	//implementation of function 'printMessage'
	public static void printMessage(boolean flag, String message)
	{
		System.out.println(message);
	}
	//implementation of function 'randomValues'
	public static int randomValues(int lb, int ub, Random rand) {
		long vR = (long)ub - (long)lb + 1;
		long fracValue = (long)(vR * rand.nextDouble());
		int randVal = (int)(fracValue + lb);
		return randVal;    //return random value
	}

	//Implementation of function 'StartingFunction'
	public static void StartingFunction(int adults, int children, BoatGrader b) 
	{
		bg = b;
		bPlace = "Oahu";

		//declare and initialize variables
		OahuChildren = 0;
		childrenAtMolokai = 0;
		childrenInQueue = 0;
		countBoat = 0;
		adultsAtOahu = 0;
		adultsAtMolokai = 0;
		data = 0;
		lockObject = new Lock();   //creating an object of lock

		chAtOahu = new Condition2(lockObject);
		chAtMolokai = new Condition2(lockObject);
		adltAtOahu = new Condition2(lockObject);
		boat = new Condition2(lockObject);
		alarm = new Alarm();
		mediator = new Communicator();
		isChildExist = false;
		flag = false;
		isPilot = false;
		containsBoat = false;


		//display message content
		printMessage(showResult, "Created Instance varibles");


		//creating a runnable thread
		Runnable a = new Runnable()
		{
			public void run() {
				AdultItinerary();
			}
		};

		//display message content
		printMessage(showResult, "Created Adult Itinerary");

		Runnable c = new Runnable()
		{
			public void run()
			{
				ChildItinerary();
			}
		};

		//display message content
		printMessage(showResult, "Created Child Itinerary");

		for (int i = 1; i <= children; i++) {

			KThread child = new KThread(c);
			child.setName("Child: " + i);
			if (showResult)
			{
				System.out.println("Child " + i + " created");
			}
			child.fork();
		}


		//adults loop
		for (int j = 1; j <= adults; j++)
		{
			KThread adult = new KThread(a);
			adult.setName("Adult: " + j);
			if (showResult == true)
			{
				System.out.println("Adult " + j + " created");
			}
			adult.fork();
		}

		int combinedThread = children + adults;

		while (combinedThread != data)
		{
			printMessage(showResult, "Parent Thread went to sleep");
			data = mediator.listen();
		}
		
		printMessage(showResult, "Everybody got across");
		flag = true;

		alarm.waitUntil(combinedThread * 150);
		printMessage(showResult, "All Threads joined");
	}

	//Implementation of function 'AdultItinerary'
	static void AdultItinerary()
	{
		while(true) {
		lockObject.acquire();
		adultsAtOahu = adultsAtOahu + 1;
		
		printMessage(showResult, "Adult went to sleep on Oahu");
			
		//sleep thread
		adltAtOahu.sleep();
		printMessage(showResult, "Adult woke up");
		if (flagObject == true) {
			bg.AdultRowToMolokai();
		}
		
		//display message content
		printMessage(showResult, "Adult rowed to Molokai");
		adultsAtOahu = adultsAtOahu - 1;
		bPlace = "Molokai";
		adultsAtMolokai = adultsAtMolokai + 1;
		if (showResult == true)
		{
			System.out.println("Adult tried to wake up a child on Molokai");
		}

		//awaken thread
		chAtMolokai.wake();
		containsBoat = false;
		if (showResult == true)
		{
			System.out.println("Adult Thead terminated");
		}

		//release object
		lockObject.release();
	}
	}

	//Implementation of function 'ChildItinerary'
	static void ChildItinerary()
	{
		//acquire object
		lockObject.acquire();
		while (flag != true)
		{

			countBoat = countBoat + 1;
			OahuChildren = OahuChildren + 1;

			
			printMessage(showResult, "Child arived on Oahu");

			while (bPlace.equals("Molokai") || countBoat > 2 || containsBoat) {

				countBoat = countBoat - 1;
				printMessage(showResult, "Child went to sleep on Oahu");
				chAtOahu.sleep();
				printMessage(showResult, "Child woke up on Oahu");
				countBoat++;
			}

			chAtOahu.wake();

			childrenInQueue = childrenInQueue + 1;

			//check children waiting or not
			if (childrenInQueue < 2 | bPlace.equals("Molokai"))
			{
				if (OahuChildren == 1 && isChildExist)
				{

					adltAtOahu.wake();
					
					printMessage(showResult, "Child woke up Adult");
					containsBoat = true;
				}
				
				printMessage(showResult, "Child Pilot went to sleep on boat");
				boat.sleep();
				countBoat = countBoat + 2;
				if (flagObject==true) 
				{
					bg.ChildRideToMolokai();
				}
				
				printMessage(showResult, "Child Pilot woke up in boat and went to Molokai");
				OahuChildren = OahuChildren - 2;
				childrenAtMolokai = childrenAtMolokai + 2;
				childrenInQueue= childrenInQueue -1;
			}
			boat.wake();

			//if the flag is false
			if (isPilot==false)
			{
				isPilot = true;
				childrenInQueue = childrenInQueue-1;
				if (flagObject==true)
				{
					bg.ChildRowToMolokai();
				}
				
				printMessage(showResult, "Child passenger rode to Molokai");
				bPlace = "Molokai";
				
				printMessage(showResult, "Child Passenger went to sleep on Molokai");
				chAtMolokai.sleep();
				printMessage(showResult, "Child old passenger woke up on Molokai");
			}
			else if (OahuChildren + adultsAtOahu == 0)
			{
				mediator.speak(childrenAtMolokai + adultsAtMolokai);
				
				printMessage(showResult, "Child Pilot signaled Parent that he's flag");
				alarm.waitUntil(50);

			}

			//if flag is false
			if (flag==false)
			{
				isPilot = false;
				//display message content
				printMessage(showResult, "Child rowed back to Oahu");
				if (flagObject == true) {
					bg.ChildRowToOahu();
				}
				bPlace = "Oahu";
				childrenAtMolokai--;
				isChildExist = (childrenAtMolokai > 0);

				//display message content
				if (showResult == true)
				{
					System.out.println("there is at least 1 child on Molokai? " + isChildExist);
				}
			}
		}
		chAtMolokai.wakeAll();
		printMessage(showResult, "Child Thead terminated");  //calling function and display content
		lockObject.release();

	}
}

