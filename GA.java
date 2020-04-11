import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
 
/**
 * @author Kunuk Nykjaer
 * Improvements: simplifications, comments - Antonio Mora
 * 
* Simple Genetic Algorithm example - OneMax Problem.
* 
* Selection -> 4-Tournament
* Crosssover -> Uniform (2 parents => 2 childs)
* Mutation -> Bitflip
* Replacement: Steady State (Elitism)
* 
*/
 
public class GA {

    static final int NUM_GENERATIONS = 1000;
    
    static int SIZE_INDIVIDUAL;     /* When I changed from final to (Protect or Private) Can't using Setter & Getter show an error because (boolean)
                                       remove final from size individual Anwer */
    
    static final int NUM_INDIVIDUALS = 10; /* A very large population size does not improve performance (speed of finding a solution)  WILL CHANGE FROM 20 T0 10****
                                              A good population size is between (20-30) and sometimes the sizes are between (50-100) better. Anwer */
    
    static final int PARENT_USE_PERCENT = 10;  // To define the Steady-State approach
    static final double CROSSOVER_PROBABILITY = 1.00;  // For Steady-State is always performed
    static final double MUTATION_PROBABILITY = 0.5;
    
    // Random numbers generator
    static Random rand = new Random();  // change from final to static Anwer*
 
	// Population: List of Individuals
    LinkedList<Individual> population = new LinkedList<Individual>();
     
    // Constructor: It creates the initial population
    public GA(int size_individual) {    // Constructor: will take one Parameter integer type    Anwer**

        // ************************************
        // ********** INITIALIZATION **********
        // ************************************
    	
    	SIZE_INDIVIDUAL = size_individual; // Anwer**
    	
    	
        for (int i = 0; i < NUM_INDIVIDUALS; i++) {
            // Create an individual of the desired size
            Individual c = new Individual(SIZE_INDIVIDUAL);
            // It is initialized randomly
            c.random();
            // And added to the population
            population.add(c);
        }
 
        // Sort it (from highest to lowest fitness value)
        /* The viability of an organism is measured by the ability to succeed in its life or not, 
         * and the evaluation method is based on a set of ideal solutions we are looking for. Anwer */
        
        Collections.sort(population); // sort method
        System.out.println("Initializaion population sorted");
        print();
    }

	// print: shows the whole population in the screen
    void print() {
        System.out.println("-- print --");
        for (Individual c : population) {
            System.out.println(c);
        }
    }
 
    /**
     * This method generates the new population applying the genetic operators:
     *   - Selection strategy: 4-Tournament -> 2 winners are parents
     *   - Crossover method: Uniform crossover -> 2 children are generated
     *   - Replacement strategy: Steady-State (10%) + Elitism
    */
    void produceNextGen() {
        LinkedList<Individual> newpopulation = new LinkedList<Individual>();
       
        while (newpopulation.size() < NUM_INDIVIDUALS
                * (1.0 - (PARENT_USE_PERCENT / 100.0))) {
 
            // *******************************
            // ********** SELECTION **********
            // *******************************
            // Parents Chromosomes are selected from the specified population according to the degree of suitability Anwer*
        	
            // select 4 different and random indexes of individuals in the population
            int size = population.size();
            int i = rand.nextInt(size);
            int j, k, l;
 
            j = k = l = i;
 
            while (j == i)
                j = rand.nextInt(size);
            while (k == i || k == j)
                k = rand.nextInt(size);
            while (l == i || l == j || k == l)
                l = rand.nextInt(size);
 
            // Get the selected individuals (chromosomes)
            Individual c1 = population.get(i);
            Individual c2 = population.get(j);
            Individual c3 = population.get(k);
            Individual c4 = population.get(l);
 
            // Obtain their fitness
            int f1 = c1.fitness();
            int f2 = c2.fitness();
            int f3 = c3.fitness();
            int f4 = c4.fitness();
 
            // Perform the tournament and select the winners (parents)
            Individual w1, w2;
 
            if (f1 >= f2)
                w1 = c1;
            else
                w1 = c2;
 
            if (f3 >= f4)
                w2 = c3;
            else
                w2 = c4;
 
            // *******************************
            // ********** CROSSOVER **********
            // *******************************
            Individual child1, child2;
            /* It takes place between parents to form a new offspring (children). 
             * If no intersection occurs, then the offspring is an identical copy of the parents. Anwer */
 
            // ONE-POINT CROSSOVER - Random pivot
            // int pivot = rand.nextInt(Candidate.SIZE-2) + 1; // cut interval is 1 .. size-1
            // child1 = newChild(w1,w2,pivot);
            // child2 = newChild(w2,w1,pivot);
 
            
            // The Crossover is applied depending on a probability
            if (rand.nextFloat() <= CROSSOVER_PROBABILITY){
                // UNIFORM CROSSOVER
            	// Random bits are selected and copied from one parent to the child. Anwer*
                Individual[] childs = newChilds(w1, w2);
                child1 = childs[0];
                child2 = childs[1];
            }
            else {
                // If not applied, the childs are the same as the parents
                child1 = w1;
                child2 = w2;
            }
            
            
            // ******************************
            // ********** MUTATION **********
            // ******************************
            /* In which a gene is changed randomly to produce a new trait that is not present in the parents. 
             * This trait may be better or worse than the solution before the mutation. Anwer */

            // The individuals are mutated depending on the mutation probability
            if (rand.nextFloat() <= MUTATION_PROBABILITY)
                mutate(child1);
            if (rand.nextFloat() <= MUTATION_PROBABILITY)
                mutate(child2);
 
            
            // *********************************
            // ********** REPLACEMENT **********
            // *********************************
            // The new childs are added to the population if they are better
            // than their parents. Otherwise the parents remain.
            if (child1.fitness() >= w1.fitness() || child1.fitness() >= w2.fitness())
                newpopulation.add(child1);
            else
                newpopulation.add(w1);
            
            if (child2.fitness() >= w1.fitness() || child2.fitness() >= w2.fitness())
                newpopulation.add(child2);
            else
                newpopulation.add(w2);
        }
 
        // Add top percent parent (Elitism)
        // Good Chromosomes are transferred directly to the second generation without applying previous processes to them Anwer*
        int j = (int) (NUM_INDIVIDUALS * PARENT_USE_PERCENT / 100.0);
        for (int i = 0; i < j; i++) {
            newpopulation.add(population.get(i));
        }
 
        population = newpopulation;
        Collections.sort(population);
    }
 
    
    // one-point crossover random pivot
    Individual newChild(Individual c1, Individual c2, int pivot) {
        Individual child = new Individual(SIZE_INDIVIDUAL);
 
        for (int i = 0; i < pivot; i++) {
            child.genotype[i] = c1.genotype[i];
        }
        for (int j = pivot; j < Individual.SIZE; j++) {
            child.genotype[j] = c2.genotype[j];
        }
 
        return child;
    }
 
    
    // Uniform crossover: The genes are copied from parents to childs randomly 
    //                    (following a uniform distribution)
    Individual[] newChilds(Individual c1, Individual c2) {
        Individual child1 = new Individual(SIZE_INDIVIDUAL);
        Individual child2 = new Individual(SIZE_INDIVIDUAL);
 
        // Each gene is taken from one of the parents randomly
        for (int i = 0; i < Individual.SIZE; i++) {
            if (rand.nextFloat() >= 0.5) {
                child1.genotype[i] = c1.genotype[i];
                child2.genotype[i] = c2.genotype[i];
            } else {
                child1.genotype[i] = c2.genotype[i];
                child2.genotype[i] = c1.genotype[i];
            }
        }
 
        return new Individual[] { child1, child2 };
    }
 
    
    // Flip mutation: a random gene is selected and turned from '0' to '1' or viceversa
    void mutate(Individual c) {
        int i = rand.nextInt(Individual.SIZE);
        c.genotype[i] = !c.genotype[i]; // flip
    }
 
 
    // PROCESS OF THE GENETIC ALGORITHM
    void run() {
        int count = 0;
 
        while (count < NUM_GENERATIONS) {
            produceNextGen();
            count++;
        }
 
        System.out.println("\nResult:");
        print();
    }
    
    // ################################################################
    // ##########    Genetic Algorithm for ONEMAX PROBLEM    ##########
    // ################################################################
    
	public static void main(String[] args) {
		
		// Initial time
        long BEGIN = System.currentTimeMillis();
 
        // RUN the Genetic Algorithm
        
        GA ga = new GA(100);
        ga.run();
        System.out.println("____________________*****_________________________");
        
//        GA ga1 = new GA(500); // The SIZE_INDIVIDUAL will be 500
//        ga1.run();
//        System.out.println("____________________*****_________________________");
//        
//        GA ga2 = new GA(1000); // The SIZE_INDIVIDUAL will be 1000
//        ga2.run();
//        System.out.println("____________________*****_________________________");
//        
//        GA ga3 = new GA(5000); // The SIZE_INDIVIDUAL will be 5000
//        ga3.run();
//        System.out.println("____________________*****_________________________");
//        
//        GA ga4 = new GA(10000); // The SIZE_INDIVIDUAL will be 10000
//        ga4.run();
//        System.out.println("____________________*****_________________________");

        
        
        
        // Final time
       long END = System.currentTimeMillis();
       System.out.println("Time: " + (END - BEGIN) / 1000.0 + " sec.");
    
	}
}
   