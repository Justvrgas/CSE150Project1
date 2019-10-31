package nachos.threads;


import nachos.ag.BoatGrader;

import java.util.Random;


/*

 * 

 * TESTING GIT COMMIT

 * 

 */


//added by Jesus Vera 10/30/19

//Implementation of class 'Boat'

public class Boat

{

	BoatGrader b = new BoatGrader();

	//this declares variables

	static BoatGrader bg;//given 


	static Lock threadlock = new Lock();

	//this is for children at Oahu

	static Condition2 chAtOahu = new Condition2(threadlock);

	//this is for children at Molokai

	static Condition2 chAtMolokai = new Condition2(threadlock);

	// this is for adults at Oahu

	static Condition2 adltAtOahu = new Condition2(threadlock);

	//adults and children getting on the boat

	static Condition2 boat = new Condition2(threadlock);

	static Alarm alarm;

	static Communicator mediator;

	static String bPlace; //location

	{

	}

	// Testing for false is show result and true to flag object

	//int for data

	static int OahuChildren = 0;

	static int childrenAtMolokai = 0;

	static int childrenInQueue = 0;

	static int countBoat;

	static int adultsAtOahu = 0;

	static int adultsAtMolokai = 0;

	static int data = 0;


	static boolean isChildExist;

	static boolean flag;

	static boolean isPilot;

	static boolean containsBoat;


	//added by Jesus Vera 10/28/19

	//Implementation of function 'testing'

	public static void selfTest()

	{

		BoatGrader b = new BoatGrader();


		//minimum amount of children

		System.out.println("\n ***Testing Boats with only 2 children***");

		begin(0, 2, b);

		// System.out.println("\n ***Testing Boats with 2 children, 1 adult***");

		// begin(1, 2, b);


		// System.out.println("\n ***Testing Boats with 3 children, 3 adults***");

		// begin(3, 3, b);





	}

	//added by Dexter Y. 10/29/19

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

		int vR = (int)ub - (int)lb + 1; //upper and lower bounds

		int fracValue = (int)(vR * rand.nextDouble());

		int randVal = (int)(fracValue + lb);

		return randVal;    //return random value

	}

	//added by Dexter Y. 10/29/19

	//Implementation of function 'begin'

	public static void begin(int adults, int children, BoatGrader b) 

	{


		bg = b;

		bPlace = "Oahu";


		//declare and initialize variables

		alarm = new Alarm();

		mediator = new Communicator();

		isChildExist = false;

		flag = false;

		isPilot = false;

		containsBoat = false;


		boolean showResult = false;



		//2 threads are created, for child and adult

		//creating a runnable thread

		Runnable a = new Runnable()

		{

			public void run() {

				AdultItinerary();

			}

		};




		Runnable c = new Runnable()

		{

			public void run()

			{

				ChildItinerary();

			}

		};

		//added by Jesus Vera 10/29/19


		//loop for children

		for (int i = 1; i <= children; i++) {


			KThread child = new KThread(c);

			child.setName("Child: " + i);

			if (showResult)

			{

				System.out.println("Child Thread: " + i );

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

				System.out.println("Adult Thread" + j);

			}

			adult.fork();

		}


		int combinedThread = children + adults;


		while (combinedThread != data)

		{


			data = mediator.listen();

		}


		flag = true;


		alarm.waitUntil(combinedThread * 150);

	}


	//Implementation of function 'AdultItinerary'

	//added by Jesus Vera 10/29/19

	static void AdultItinerary()

	{


		boolean showResult = false;

		boolean flagObject = true;


		threadlock.acquire();

		while(true)

		{

			adultsAtOahu = adultsAtOahu + 1;



			//sleep thread

			adltAtOahu.sleep();

			if (flagObject == true) {

				bg.AdultRowToMolokai();//calls the rows for this line

			}

			// this is for Molokai

			adultsAtOahu = adultsAtOahu - 1;

			bPlace = "Molokai";

			adultsAtMolokai = adultsAtMolokai + 1;

			if (showResult == true)

			{


			}


			//awaken thread

			chAtMolokai.wake();

			containsBoat = false;

			if (showResult == true)

			{


			}


			//release object

			threadlock.release();

		}

	}

	//added by Dexter Y. 10/30/19

	//Implementation of function 'ChildItinerary'

	static void ChildItinerary()

	{

		//acquire object

		threadlock.acquire();

		while (flag != true)

		{


			boolean showResult = false;

			boolean flagObject = true;



			countBoat = countBoat + 1;

			OahuChildren = OahuChildren + 1;



			while (bPlace.equals("Molokai") || countBoat > 2 || containsBoat) {


				countBoat = countBoat - 1;

				chAtOahu.sleep(); //sleep thread

				countBoat++;

			}


			chAtOahu.wakeAll();


			childrenInQueue = childrenInQueue + 1;


			//check children waiting or not

			if (childrenInQueue < 2 | bPlace.equals("Molokai"))

			{

				if (OahuChildren == 1 && isChildExist) //this is the case where only 1 child is left

				{


					adltAtOahu.wake();


					containsBoat = true;

				}


				boat.sleep(); //sleep thread

				countBoat = countBoat + 2;

				if (flagObject==true) 

				{

					bg.ChildRideToMolokai();

				}



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

				bPlace = "Molokai";

				chAtMolokai.sleep();

			}

			else if (OahuChildren + adultsAtOahu == 0)

			{

				mediator.speak(childrenAtMolokai + adultsAtMolokai);



				alarm.waitUntil(500);


			}


			//if flag is false

			if (flag==false)

			{

				isPilot = false;

				//display message content

				if (flagObject == true) {

					bg.ChildRowToOahu();

				}

				bPlace = "Oahu";

				childrenAtMolokai--;

				isChildExist = (childrenAtMolokai > 1);


				//display message content

				if (showResult == true)

				{

					System.out.println("Will there be at least 1 child on Molokai? " + isChildExist);

				}

			}

		}

		chAtMolokai.wakeAll();

		threadlock.release();

	}

	static void SampleItinerary()

	{

		System.out.println("Everyone can go on the boat that goes to Molokai");

	}

}