/**
 * BarberShop.java

 This class defines the Barber Shop Scenario.  This scenario allows N customers
 to enter it.  It also contains a number of chairs that allow the customers to
 wait in.
 
*/
public class BarberShop
{
    private int chairNum;
    private int barber;
    private int chairState[];
    static final int FULL = -1;
    static final int EMPTY = 0;
    static final int OCUPIED = 1;
    static final int SLEEPING = 2;
    static final int DONE = 3;
    static final int WAITED = 4;
    
    /**
     * Construct a new Barber Shop scenario for customers to get haircuts at.
     * Set the barber shop to have N number of chairs to wait in.  Then make
     * all the chairs to be initially empty.  Also set the barber to be 
     * asleep so that when his first customer comes in he can wake him up.
     * @param pChairNum The number of waiting chairs in this barber shop
    */
    public BarberShop(int pChairNum)
    {
        chairNum = pChairNum;
        chairState = new int[chairNum];
        barber = SLEEPING;
        
        //initialize every chair in the waiting room to be empty
        for(int i = 0; i < chairNum; i++)
            chairState[i] = EMPTY;
    }
    
    /**
     * This method is called when a customer sees that the Barber is busy.
     * This means that the customer must wait for the barber.  Therefore,
     * find a chair in the waiting room.  When the customer finds a chair
     * they set its state to OCUPIED (so there's only one customer per
     * chair) and return true. Otherwise, there are no chairs available
     * so return false.
     * @param pCustomer The customer wanting to find a chair to wait in.
     * @return boolean True if chair is found; False otherwise.
    */
    private boolean findChair(int pCustomer) 
    {
        //try to find a chair to wait in
        int test = getFirstEmptyChair();
        
        //if barber shop is full return false
        if(test == FULL)
            return false;
        //otherwise sit down in this chair
        else
            chairState[test] = OCUPIED;
        
        return true;
    }
    
    /**
     * This method is called by the Customer thread to get a haircut.  IF the
     * barber's asleep then the customer wakes him up and the customer gets 
     * their haircut.  IF the barber's already busy then the customer tries to
     * find a chair to wait in.  However, if there are no chairs and the barbers
     * busy, then the shop is full, so customer will then leave.  If the
     * barber's state is not sleeping or ocupied, then he can take the customer
     * immediately.
     *
     * This solution doesn't prevent starvation for the waiting customers.  If
     * A customer finnaly gets notified to leave the waiting state they will
     * be the next haircut.  If another customer enters the shop at the same
     * time, then that customer will always have to wair for existing customers to 
     * be serviced.  
     *
     * @param customer The customer wanting to get a haircut
     * @return int The state of the Barber Shop and/or barber
    */
    public synchronized int getHairCut(int customer)
    {
        //if the barber's sleeping, then wake him up and tell him to get to work
        if(barber == SLEEPING)
        {
        //please fill in...
        }

        //the barber's busy try to wait for him in the waiting room
        else if(barber == OCUPIED)
        {
        //please fill in...
        }

        //barber's done.  This customer can get their haircut immediately
        else
        {
            barber = OCUPIED;
            return DONE;
        }
    }
    
    /**
     * This method is called when the customer has recieved their haircut and
     * they leave the shop.  They first check to see if anyone else is waiting
     * in the shop.  If there isn't anyone, then they were the last customer.
     * This means the barber must go back to sleep.  Otherwise poeple are
     * waiting, so just set barber's state to done.  Then notify anyone waiting.
     * @param customer The customer finished with haircut and leaving shop
     * @return void
    */
    public synchronized void leaveBarberShop(int customer)
    {
        boolean test = isAnyoneWaiting();
        
        if(test == true)
            barber = DONE;
        else
            barber = SLEEPING;

        //notify only one waiting customer (if any exist)
        //this helps on performance of the program.
        notify();
    }
        
    /**
     * Find the first empty chair in the waiting room and return it.  If no 
     * chairs are found then all chairs are ocupied by a waiting customer.
     * @return int The number of the empty chair; FULL otherwise
    */
    private int getFirstEmptyChair()
    {
        //if an empty chair is found return it
        for(int i = 0; i < chairNum; i++)
        {
            if(chairState[i] == EMPTY)
                return i;
        }
        
        //all chairs are occupied so return FULL
        return FULL;
    }
    
    /**
     * Test to see if the waiting room has any customers in it.
     * @return boolean True if there are customers waiting; False if empty
    */
    private boolean isAnyoneWaiting()
    {
        //see if anyone is in a chair waiting
        for(int i = 0; i < chairNum; i++)
        {
            if(chairState[i] == OCUPIED)
                return true;
        }
        
        //couldn't find anyone waiting 
        return false;
    }
}
