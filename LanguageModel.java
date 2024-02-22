import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;



    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        this.randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        this.randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
    String window = "";
    char c;
    int counter = 0;
    In in = new In(fileName);
    while (counter < this.windowLength && !in.isEmpty()) {
        window += in.readChar();
        counter++;
    }
    //to be removed
    //int printable = 0;
    
    while (!in.isEmpty()){
        c = in.readChar();
        if (CharDataMap.get(window) == null) {
            List probs = new List();
            CharDataMap.put(window, probs);
            /**if (window.startsWith("nged"))
            {
            	int sixth = (int) window.charAt(4);
            	int seventh = (int) window.charAt(5);
            	System.out.println("Found one : " + window.substring(0,4) + sixth + seventh );
            	System.out.println(window + " next is " + c);
            			
            	if ( (sixth == 13 && seventh == 10) || (sixth == 46 && seventh == 13))
            		{
            			System.out.println("Found one : " + window.substring(0,4) + sixth + seventh );
            			System.out.println(window + " next is " + c);
            			System.out.println("This one is marked");
            			probs.tempMark = true;
            		}
            }*/
          }
        List probsToUpdate = CharDataMap.get(window) ;
        //if (c=='S' && window.contains("ed"))
        ///	System.out.println("Found S " + window);

        probsToUpdate.update(c);
        window = window.substring(1) + c;
        //to be removed
        //if(window.startsWith("anged")){
    	//printable = 1;
    	///System.out.println("window - " + window);
    	//System.out.println(probsToUpdate.toString(printable));
    //}
    //else {
    //	printable = 0;
    //}
        
    }
       
    for (List probs : CharDataMap.values()) {
        calculateProbabilities(probs);
    }
}

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public void calculateProbabilities(List probs) {				
    int size = probs.getSize();
    int numberOfChars = 0;
    for(int j = 0; j < size; j++){
    	numberOfChars += probs.get(j).count;
    }
    for(int i = 0; i < size; i++)
        {
            probs.get(i).p = (double) (probs.get(i).count) / (double) (numberOfChars);;
        }
    probs.getFirst().cp = probs.getFirst().p;
    for(int y = 1; y < size; y++)
        {
            probs.get(y).cp = probs.get(y-1).cp+probs.get(y).p;
        }

    //if (size != 0){
    //	double cp = 0;
    //	for(int i = 0; i < size; i++) {
    //		CharData item = probs.get(i);
    //		double p = (double) (item.count) / (double) (numberOfChars);
    //		cp += p;
    //		item.p = p;
    //		item.cp = cp;
    }
    }

    
}

    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probs) {
		double random = randomGenerator.nextDouble();
		// To be removed
		//if (printable > 0)
			//{
				//System.out.println("Random is : " + random);
				//System.out.println(probs.toString(printable));	
			//}
		for(int i = 0; i<probs.getSize(); i++){
			if(probs.get(i).cp > random){
				return probs.get(i).chr;
			}
		} 
		return '1';
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
		if (textLength < windowLength) {
        return initialText;
    }

    StringBuilder generatedText = new StringBuilder(initialText);

    String window = initialText.substring(initialText.length() - windowLength);
    
    //To be removed
    //int printable = 0 ;

    while (generatedText.length() < textLength) {
        List charList = CharDataMap.get(window);

        if (charList == null) {
            System.out.println("Break");
            break;
        }
        // To be removed
    	//if (window.equals("changed") || ((0 < printable) && (printable < 4)))
    	//	{
    	//		System.out.println("Window is " + window);
    	//		if (window.startsWith("anged"))
    	//			System.out.println("Found");
    	///		printable++;
    		//}
    	 //else 
    	 	//{printable = 0 ;}
    	//////


        char nextChar = getRandomChar(charList);
        generatedText.append(nextChar);
        window = window.substring(1) + nextChar;

    }

    return generatedText.toString();
}


    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs.toString() + "\n");
		}
		return str.toString();
	}

/** To be removed */
	//public void print() {
	//	for (String key : CharDataMap.keySet()) {
	//		List keyProbs = CharDataMap.get(key);
	//		if ( key.startsWith("ange") && keyProbs.getSize() > 0 )
	//		 System.out.println(key + " : " + keyProbs.toString());
	//	}
	//}

    public static void main(String[] args) {
    	int windowLength = Integer.parseInt(args[0]);
    	String initialText = args[1];
    	int generatedTextLength = Integer.parseInt(args[2]);
    	Boolean randomGeneration = args[3].equals("random");
    	String fileName = args[4];
    	// Create the LanguageModel object
    	LanguageModel lm;
    	if (randomGeneration)
    		lm = new LanguageModel(windowLength);
    	else
    		lm = new LanguageModel(windowLength, 20);
    		// Trains the model, creating the map.
    	
    	lm.train(fileName);
    	// to be removed
    	//lm.print();
    	// Generates text, and prints it.
    	System.out.println(lm.generate(initialText, generatedTextLength));
}

}
