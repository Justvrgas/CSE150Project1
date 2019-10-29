package nachos.threads;
import nachos.ag.BoatGrader;

/*
 * 
 * TESTING GIT COMMIT
 * 
 */


public class Boat
{
	//declaring variables
    static BoatGrader bg;
    static Lock lockObject;
    static Condition2 adltAtOahu;
    static String bPlace;
    static int data;
    static int adultsatOahu;
    static int adultsatMolokai;
    
    
    //testing 
    static boolean showResult = false;
    static boolean flagObject = true;
    
public static void selfTest()
    {
	BoatGrader b = new BoatGrader();
	
	System.out.println("\n ***Testing Boats with only 2 children***");
	begin(0, 2, b);

//	System.out.println("\n ***Testing Boats with 2 children, 1 adult***");
//  	begin(1, 2, b);

//  	System.out.println("\n ***Testing Boats with 3 children, 3 adults***");
//  	begin(3, 3, b);
    }
    
    
    public static void begin( int adults, int children, BoatGrader b )
    {
	// Store the externally generated autograder in a class
	// variable to be accessible by children.
	bg = b;
	bPlace = "Oahu";

	// Instantiate global variables here
	
	// Create threads here. See section 3.4 of the Nachos for Java
	// Walkthrough linked from the projects page.

	Runnable c = new Runnable() 
	{
	    public void run() {
                ChildItinerary();
            }
        };
        if (showResult)
        {
        	System.out.println("Created Child Itinerary");
        }
       
    Runnable a = new Runnable()
    {
    	 public void run() {
    		 AdultItinerary();
    	 }
    		 };
    	if (showResult)
    	{
    		System.out.println("Created Adult Itinerary");
    	}
    		 
        for (int i=1; i<= children; i++) {
        	
        KThread child = new KThread(c);
        child.setName("Child: " + i);
        if (showResult)
        {
        	System.out.println("Child " + i + " created");
        }
        child.fork();
    }
        
        
        

    static void AdultItinerary()
    {
	/* This is where you should put your solutions. Make calls
	   to the BoatGrader to show that it is synchronized. For
	   example:
	       bg.AdultRowToMolokai();
	   indicates that an adult has rowed the boat across to Molokai
	*/
    	lockObject.acquire();
    	adultsatOahu = adultsatOahu +1;
    	if (showResult)
    	{
    		System.out.println("Adult went to sleep on Oahu");
    	}
    	adltAtOahu.sleep();
    	if (showResult)
    	{
    		System.out.println("Adult woke up");
    	}
    	if (flagObject)
    	{
    		bg.AdultRowToMolokai();
    	}
    	if (showResult)
    	{
    		System.out.println("Adult rowed to Molokai");
    	}
    	adultsAtOahu = AdultsAtOahu -1;
    	bPlace = "Molokai";
    	adultsAt
    	
    	
    }

    static void ChildItinerary()
    {
    	boat_permit.acquire();
    }

    static void SampleItinerary()
    {
	// Please note that this isn't a valid solution (you can't fit
	// all of them on the boat). Please also note that you may not
	// have a single thread calculate a solution and then just play
	// it back at the autograder -- you will be caught.
	System.out.println("\n ***Everyone piles on the boat and goes to Molokai***");
	bg.AdultRowToMolokai();
	bg.ChildRideToMolokai();
	bg.AdultRideToMolokai();
	bg.ChildRideToMolokai();
    }
    
}