
public class BlindLevel {
  
  private int BigBlind;
  private int SmallBlind;
  private int ante;
  
  private int currentLevel;
  private int blindTime;
  
  public BlindLevel(int BigBlind, int SmallBlind, int ante, int blindTime) {
    this.BigBlind = BigBlind;
    this.SmallBlind = SmallBlind;
    this.ante = ante;
    this.blindTime = blindTime;
    currentLevel = 0;
  }
  
  public static BlindLevel[] getNormalBlindStructure() {
    BlindLevel[] blinds = new BlindLevel[16];
    
    blinds[0] = new BlindLevel(30, 15, 0, 5);
    blinds[1] = new BlindLevel(40, 20, 0, 5);
    blinds[2] = new BlindLevel(50, 25, 0, 5);
    blinds[3] = new BlindLevel(60, 30, 0, 5);
    blinds[4] = new BlindLevel(80, 40, 0, 5);
    blinds[5] = new BlindLevel(100, 50, 0, 5);
    blinds[6] = new BlindLevel(120, 60, 0, 5);
    blinds[7] = new BlindLevel(160, 80, 0, 5);
    blinds[8] = new BlindLevel(200, 100, 0, 5);
    blinds[9] = new BlindLevel(240, 120, 0, 5);
    blinds[10] = new BlindLevel(300, 150, 0, 5);
    blinds[11] = new BlindLevel(400, 200, 0, 5);
    blinds[12] = new BlindLevel(500, 250, 0, 5);
    blinds[13] = new BlindLevel(600, 300, 0, 5);
    blinds[14] = new BlindLevel(800, 400, 0, 5);
    blinds[15] = new BlindLevel(1000, 500, 0, 5);
    
    return blinds;
  }
  public int getBigBlind () {
    return BigBlind;
  }
  public void setBigBlind(int newBlind) {
    this.BigBlind = newBlind;
  }
  
  public int getSmallBlind () {
    return SmallBlind;
  }
  public void setSmallBlind(int newBlind) {
    this.SmallBlind = newBlind;
  }
  
  public int getAnte () {
    return ante;
  }
  public void setAnte(int newAnte) {
    this.ante = newAnte;
  }
  public int getBlindLevel() {
      return currentLevel;
  }
  public int getBlindTime() {
      return blindTime;
  }
  public void increaseBlinds() {
      currentLevel++;
  }
}