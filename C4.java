import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

//ArchedNosi#1484: There are likely ways to optimize this to be faster that I am unaware of, if any advancements can be made, feel free to let me know.

public class C4 
{
	public static int counterTotalHits = 0;
	public static int counterTotalElectroApp = 0;
	public static int minApp = Integer.MAX_VALUE;
	public static int maxApp = Integer.MIN_VALUE;
	public static ArrayList<Integer> apps = new ArrayList<Integer>();
	public static float avgApp = 0;
	
	public static void main(String[] args) {
		
		try (Scanner reader = new Scanner(System.in)) {
			System.out.println("Enter the desired amount of entities (1, 2, or 3+).");
			int entityCount = reader.nextInt();
				if (entityCount < 1)
					entityCount = 1;
			System.out.println("Enter the total number of iterations.");
			int trialCount = reader.nextInt();
				if (trialCount < 1)
					trialCount = 1;
			System.out.println("-----");
			//-----
			if (entityCount == 1) {
				for (int i = 1; i <= trialCount; i++) {
					int counterICD = 0;
					double timerICD = 0.0;
					int counterElectroApp = 0;
					
					System.out.println("Trial " + i + ".\nEntity #1 | ");
					for (int k = 1; k <= 29; k++) {
						if (counterICD == 0) {
							System.out.print("X");
							timerICD += 0.5;
							counterICD++;
							counterElectroApp++;
						}
						else if (timerICD >= 2.5) {
							System.out.print("X");
							timerICD = 0.5;
							counterICD = 1;
							counterElectroApp++;
						}
						else if (counterICD == 3) {
							System.out.print("X");
							timerICD += 0.5;
							counterICD = 1;
							counterElectroApp++;
							}
						else {
							System.out.print("O");
							timerICD += 0.5;
							counterICD++;
						}
						counterTotalHits++;
					}
					System.out.println(" | Applied Electro " + counterElectroApp + " times");
					counterTotalElectroApp += counterElectroApp;
				}
				System.out.println("-----\nApplied Electro \" + counterTotalElectroApp + \" times total out of \" + counterTotalHits");
			}
			//-----
			if (entityCount == 2) {
				//entity constructor
				C4entity[] entities = new C4entity[entityCount];
				for (int i = 0; i < entityCount; i++) {
					int k = i + 1;
					entities[i] = new C4entity(k);
				}
				for (int i = 1; i <= trialCount; i++) {
					System.out.print("Trial " + i + ".\n");
					for (int k = 1; k <= 29; k++) {
						int countTarget = 0;
						//target count selector
						if (k == 1) {
							if (Math.random() >= .6)
								countTarget = 2;
							else
								countTarget = 1;
						}
						else {
							if (Math.random() >= .16)
								countTarget = 2;
							else 
								countTarget = 1;
						}
						
						//target selector
				        ArrayList<Integer> entityList = new ArrayList<Integer>();
				        ArrayList<Integer> entityHitList = new ArrayList<Integer>();
				        for (int a = 1; a <= entityCount; a++) 
				        	entityList.add(a);
				        shuffle(entityList);
				        for (int a = 0; a < countTarget; a++)
				        	entityHitList.add(entityList.remove(0));
				        
						//execute per target
				        for (int b = 0; b < entityHitList.size(); b++) {
				        	entities[entityHitList.get(b) - 1].hitCheck(entities[entityHitList.get(b) - 1]);
				        }
				        for (int b = 0; b < entityList.size(); b++) {
				        	entities[entityList.get(b) - 1].missCheck(entities[entityList.get(b) - 1]);
				        }
					}
					int concurrentElectroApp = 0;
					for (int c = 0; c < entities.length; c++) {
						concurrentElectroApp += entities[c].getCounterElectroApp(entities[c]);
						entities[c].finalPass(entities[c]);
			        }
					//ending text
					System.out.println("Electro was individually applied " + concurrentElectroApp + " times");
					if (minApp > concurrentElectroApp)
						minApp = concurrentElectroApp;
					if (maxApp < concurrentElectroApp)
						maxApp = concurrentElectroApp;
					apps.add(concurrentElectroApp);
					float sum = 0;
					for(int app:apps)
						sum += app;
					avgApp = (sum/apps.size());
				}
				//final ending text
				System.out.println("-----\nApplied Electro " + counterTotalElectroApp + " times total out of " + counterTotalHits + " | (" + round((100*(double)counterTotalElectroApp/counterTotalHits)) + "%)");
				System.out.println("Min: " + minApp + " | Avg: " + round(avgApp) + " | Max: " + maxApp);
			}
			//-----
			if (entityCount >= 3) {
				//entity constructor
				C4entity[] entities = new C4entity[entityCount];
				for (int i = 0; i < entityCount; i++) {
					int k = i + 1;
					entities[i] = new C4entity(k);
				}
				for (int i = 1; i <= trialCount; i++) {
					System.out.print("Trial " + i + ".\n");
					for (int k = 1; k <= 29; k++) {
						int countTarget = 0;
						int countTargetPrev = 0;
						//target count selector
						if ((k == 1)||(countTargetPrev == 3)) {
							if (Math.random() >= .5)
								countTarget = 2;
							else
								countTarget = 1;
						}
						else {
							double randomHolder = Math.random();
							if (randomHolder <= .25)
								countTarget = 1;
							else if (randomHolder <= .5)
								countTarget = 3;
							else
								countTarget = 2;
						}
						countTargetPrev = countTarget;
						
						//target selector
				        ArrayList<Integer> entityList = new ArrayList<Integer>();
				        ArrayList<Integer> entityHitList = new ArrayList<Integer>();
				        for (int a = 1; a <= entityCount; a++) 
				        	entityList.add(a);
				        shuffle(entityList);
				        for (int a = 0; a < countTarget; a++)
				        	entityHitList.add(entityList.remove(0));
				        
						//execute per target
				        for (int b = 0; b < entityHitList.size(); b++) {
				        	entities[entityHitList.get(b) - 1].hitCheck(entities[entityHitList.get(b) - 1]);
				        }
				        for (int b = 0; b < entityList.size(); b++) {
				        	entities[entityList.get(b) - 1].missCheck(entities[entityList.get(b) - 1]);
				        }
					}
					int concurrentElectroApp = 0;
					for (int c = 0; c < entities.length; c++) {
						concurrentElectroApp += entities[c].getCounterElectroApp(entities[c]);
						entities[c].finalPass(entities[c]);
			        }
					//ending text
					System.out.println("Electro was individually applied " + concurrentElectroApp + " times");
					if (minApp > concurrentElectroApp)
						minApp = concurrentElectroApp;
					if (maxApp < concurrentElectroApp)
						maxApp = concurrentElectroApp;
					apps.add(concurrentElectroApp);
					float sum = 0;
					for(int app:apps)
						sum += app;
					avgApp = (sum/apps.size());
				}
				//final ending text

				System.out.println("-----\nApplied Electro " + counterTotalElectroApp + " times total out of " + counterTotalHits + " | (" + round((100*(double)counterTotalElectroApp/counterTotalHits)) + "%)");
				System.out.println("Min: " + minApp + " | Avg: " + round(avgApp) + " | Max: " + maxApp);
			}
		}
	}
	public static double round(double x) {
		x *= 100;
		if(x > 0)
			return (int)(x + 0.5)/100.0;
		else
			return (int)(x - 0.5)/100.0;
	}
	public static<T> void shuffle(List<T> list) {
	    Random random = new Random();
	    for (int i = list.size() - 1; i >= 1; i--) {
	        int j = random.nextInt(i + 1);
	        T obj = list.get(i);
	        list.set(i, list.get(j));
	        list.set(j, obj);
	        }
	}
}
//
class C4entity 
{
private int counterICD;
private double timerICD;
private int counterElectroApp;
private String arcLog;

	public C4entity(int i) {
		this.counterICD = 0;
		this.timerICD = 0.0;
		this.counterElectroApp = 0;
		this.arcLog = "Entity #" + i + " | ";
	}
	public String toString() {
		String str = "\n";
		str += "counterICD: " + counterICD + "\n";
		str += "timerICD: " + timerICD + "\n";
		str += "counterElectroApp: " + counterElectroApp + "\n";
		str += "arcLog: " + arcLog + "\n";
		return str;
	}
	public int getCounterElectroApp(C4entity entity) {
		return this.counterElectroApp;
	}
	public void hitCheck(C4entity entity) {
		if (this.counterICD == 0) {
			this.arcLog += "X";
			this.counterICD++;
			this.counterElectroApp++;
		}
		else if (this.timerICD >= 2.5 || this.counterICD == 3) {
			this.arcLog += "X";
			this.counterICD = 1;
			this.counterElectroApp++;
		}
		else {
			this.arcLog += "O";
			this.counterICD++;
		}
		C4.counterTotalHits++;
		//timer reset
		if (this.timerICD >= 2.5)
			this.timerICD = 0.5;
		else
			this.timerICD += 0.5;
	}
	public void missCheck(C4entity entity) {
		if (this.timerICD != 0)
			this.timerICD += 0.5;
		this.arcLog += "-";
	}
	public void finalPass(C4entity entity) {
		System.out.println(this.arcLog + " | Applied Electro " + this.counterElectroApp + " times");
		C4.counterTotalElectroApp += this.counterElectroApp;
		this.counterICD = 0;
		this.timerICD = 0.0;
		this.counterElectroApp = 0;
		this.arcLog = this.arcLog.substring(0, this.arcLog.length() - 29);
	}
}