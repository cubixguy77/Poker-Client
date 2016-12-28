public class Pot {
    
    private int amount;        // pot size
    private int[] eligibles;   // list of seat numbers of players eligible to win this pot
        
    public Pot(int amount, int[] eligibles) {
        this.amount = amount;
        this.eligibles = eligibles;
    }
    
    public Pot(int amount) {
        this.amount = amount;
        eligibles = new int[9];
        setNoneEligible();
    }
    
    public Pot() {
        amount = 0;
        eligibles = new int[9];
        setNoneEligible();
    }
    
    public void setAllEligible() {
        for (int i=0; i<eligibles.length; i++)
            eligibles[i] = i;
    }
    
    public void setNoneEligible() {
        for (int i=0; i<eligibles.length; i++)
            eligibles[i] = -1;
    }
    
    public void addPlayer(int seat) {
        System.out.println("+++++Adding player " + seat + " to eligibility list");
        int i=0;
        while (true) {
            if (eligibles[i] == -1) {
                eligibles[i] = seat;
                break;
            }
            else
                i++;
        }
    }
    
    public void removePlayer(int seat) {
        int i=0;
        while (true) {
            if (eligibles[i] == seat) {
                eligibles[i] = -1;
                break;
            }
            else
                i++;
        }
    }
    
    public int getNumEligible() {
        int total = 0;
        for (int i=0; i<eligibles.length; i++) {
            if (eligibles[i] != -1)
                total++;
        }
        return total;
    }
    
    public int getWinner() {
        if (getNumEligible() == 1) {
            for (int i=0; i<eligibles.length; i++) {
                if (eligibles[i] != -1)
                    return eligibles[i];
            }
        }
        return -1;
    }
    
    public boolean isEligible(int seat) {
        for (int i=0; i<eligibles.length; i++) {
            if (eligibles[i] == seat)
                return true;
        }
        return false;
    }
    
    public void add(int amount) {
        this.amount += amount;
    }
    
    public void subtract(int amount) {
        this.amount -= amount;
    }
    
    public int getPotSize() {
        return amount;
    }
    public int[] getEligibles() {
        return eligibles;
    }
    
    public void setPotSize(int amount) {
        this.amount = amount;
    }
    public void setEligibles(int[] eligibles) {
        this.eligibles = eligibles;
    }
}