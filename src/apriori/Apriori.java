
/**
 *
 * @author Nathan Magnus, under the supervision of Howard Hamilton
 * Copyright: University of Regina, Nathan Magnus and Su Yibin, June 2009.
 * No reproduction in whole or part without maintaining this copyright notice
 * and imposing this condition on any subsequent users.
 *
 * File:
 * Input files needed:
 *      1. config.txt - three lines, each line is an integer
 *          line 1 - number of items per transaction
 *          line 2 - number of transactions
 *          line 3 - minsup
 *      2. transa.txt - transaction file, each line is a transaction, items are separated by a space
 */

package apriori;
import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;

public class Apriori {

    public static void main(String[] args) {
        AprioriCalculation ap = new AprioriCalculation();

        ap.aprioriProcess();
        

        
        

        
    }
}
/******************************************************************************
 * Class Name   : AprioriCalculation
 * Purpose      : generate Apriori itemsets
 *****************************************************************************/
class AprioriCalculation
{
    Vector<String> candidates=new Vector<String>(); //the current candidates
    
    Vector<String> prevCandidates=new Vector<String>();
    Vector<String> leftCandidates=new Vector<String>();
    
    String configFile="data/config.txt"; //configuration file
    String transaFile="data/transa.txt"; //transaction file
    String outputFile="data/apriori-output.txt";//output file
    int numItems; //number of items per transaction
    int numTransactions; //number of transactions
    double minSup; //minimum support for a frequent itemset
    String oneVal[]; //array of value per column that will be treated as a '1'
    String itemSep = " "; //the separator value for items in the database
    
    
    

    JSONObject json=new JSONObject();   //whole json
    JSONArray nodes = new JSONArray(); //array of nodes
    
    
    
    
    
	/************************************************************************
     * Method Name  : aprioriProcess
     * Purpose      : Generate the apriori itemsets
     * Parameters   : None
     * Return       : None
     *************************************************************************/
    public void aprioriProcess()
    {
    	json.put("Node", nodes);
    	 
    	//---------json operation
    	
    	
    	
    	
        Date d; //date object for timing purposes
        long start, end; //start and end time
        int itemsetNumber=0; //the current itemset being looked at
        //get config
        getConfig();

        System.out.println("Apriori algorithm has started.\n");

        //start timer
        d = new Date();
        start = d.getTime();

        //while not complete
        do
        {
            //increase the itemset that is being looked at
            itemsetNumber++;
            
            
            System.out.println("----------itemsetNumber=" + itemsetNumber + "----------");
            //generate the candidates
            generateCandidates(itemsetNumber);
            
            
            //prune itemsets which are not in previous subset
            pruneSubset(itemsetNumber);
            
            

            //remove itemsets under threhold frequency 
            calculateFrequentItemsets(itemsetNumber);
            
            if(candidates.size()!=0)
            {
                System.out.println("Frequent " + itemsetNumber + "-itemsets:");
                System.out.println(candidates);
                
                System.out.println("candidates size=" + candidates.size() + " afer frequency cut\n");
                               
            }
            
            prevCandidates = new Vector<String>(candidates);
            
        //if there are <=1 frequent items, then its the end. This prevents reading through the database again. When there is only one frequent itemset.
        }while(candidates.size()>1);

        //end timer
        d = new Date();
        end = d.getTime();

        //display the execution time
        System.out.println("Execution time is: "+((double)(end-start)/1000) + " seconds.");
        
        
        //----------------output json
        //System.out.print(json);
        
        
        try {
        	 
    		FileWriter file = new FileWriter("data/test.json");
    		file.write(json.toJSONString());
    		file.flush();
    		file.close();
     
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

         
        
             
 
          
        
        
        
    }

    /************************************************************************
     * Method Name  : getInput
     * Purpose      : get user input from System.in
     * Parameters   : None
     * Return       : String value of the users input
     *************************************************************************/
    public static String getInput()
    {
        String input="";
        //read from System.in
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        //try to get users input, if there is an error print the message
        try
        {
            input = reader.readLine();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return input;
    }

    /************************************************************************
     * Method Name  : getConfig
     * Purpose      : get the configuration information (config filename, transaction filename)
     *              : configFile and transaFile will be change appropriately
     * Parameters   : None
     * Return       : None
     *************************************************************************/
    private void getConfig()
    {
        FileWriter fw;
        BufferedWriter file_out;

        String input="";
        //ask if want to change the config
        System.out.println("Default Configuration: ");
        System.out.println("\tRegular transaction file with '" + itemSep + "' item separator.");
        System.out.println("\tConfig File: " + configFile);
        System.out.println("\tTransa File: " + transaFile);
        System.out.println("\tOutput File: " + outputFile);
        System.out.println("\nPress 'C' to change the item separator, configuration file and transaction files");
        System.out.print("or any other key to continue.  ");
        input=getInput();

        if(input.compareToIgnoreCase("c")==0)
        {
            System.out.print("Enter new transaction filename (return for '"+transaFile+"'): ");
            input=getInput();
            if(input.compareToIgnoreCase("")!=0)
                transaFile=input;

            System.out.print("Enter new configuration filename (return for '"+configFile+"'): ");
            input=getInput();
            if(input.compareToIgnoreCase("")!=0)
                configFile=input;

            System.out.print("Enter new output filename (return for '"+outputFile+"'): ");
            input=getInput();
            if(input.compareToIgnoreCase("")!=0)
                outputFile=input;

            System.out.println("Filenames changed");

            System.out.print("Enter the separating character(s) for items (return for '"+itemSep+"'): ");
            input=getInput();
            if(input.compareToIgnoreCase("")!=0)
                itemSep=input;


        }

        try
        {
             FileInputStream file_in = new FileInputStream(configFile);
             BufferedReader data_in = new BufferedReader(new InputStreamReader(file_in));
             //number of items
             numItems=Integer.valueOf(data_in.readLine()).intValue();

             //number of transactions
             numTransactions=Integer.valueOf(data_in.readLine()).intValue();

             //minsup
             minSup=(Double.valueOf(data_in.readLine()).doubleValue());

             //output config info to the user
             System.out.print("\nInput configuration: "+numItems+" items, "+numTransactions+" transactions, ");
             System.out.println("minsup = "+minSup+"%");
             System.out.println();
             minSup/=100.0;

            oneVal = new String[numItems];
            System.out.print("Enter 'y' to change the value each row recognizes as a '1':");
            if(getInput().compareToIgnoreCase("y")==0)
            {
                for(int i=0; i<oneVal.length; i++)
                {
                    System.out.print("Enter value for column #" + (i+1) + ": ");
                    oneVal[i] = getInput();
                }
            }
            else
                for(int i=0; i<oneVal.length; i++)
                    oneVal[i]="1";

            //create the output file
            fw= new FileWriter(outputFile);
            file_out = new BufferedWriter(fw);
            //put the number of transactions into the output file
            file_out.write(numTransactions + "\n");
            file_out.write(numItems + "\n******\n");
            file_out.close();
        }
        //if there is an error, print the message
        catch(IOException e)
        {
            System.out.println(e);
        }
    }

    /************************************************************************
     * Method Name  : generateCandidates
     * Purpose      : Generate all possible candidates for the n-th itemsets
     *              : these candidates are stored in the candidates class vector
     * Parameters   : n - integer value representing the current itemsets to be created
     * Return       : None
     *************************************************************************/
    private void generateCandidates(int n)
    {
        Vector<String> tempCandidates = new Vector<String>(); //temporary candidate string vector
        String str1, str2; //strings that will be used for comparisons
        StringTokenizer st1, st2; //string tokenizers for the two itemsets being compared
        
        //if its the first set, candidates are just the numbers
        if(n==1)
        {
            for(int i=1; i<=numItems; i++)
            {
                tempCandidates.add(Integer.toString(i));
            }
        }
        else if(n==2) //second itemset is just all combinations of itemset 1
        {
            //add each itemset from the previous frequent itemsets together
            for(int i=0; i<candidates.size(); i++)
            {
                st1 = new StringTokenizer(candidates.get(i));
                str1 = st1.nextToken();
                for(int j=i+1; j<candidates.size(); j++)
                {
                    st2 = new StringTokenizer(candidates.elementAt(j));
                    str2 = st2.nextToken();
                    tempCandidates.add(str1 + " " + str2);
                }
            }
        }
        else
        {
            //for each itemset
            for(int i=0; i<candidates.size(); i++)
            {
                //compare to the next itemset
                for(int j=i+1; j<candidates.size(); j++)
                {
                    //create the strigns
                    str1 = new String();
                    str2 = new String();
                    //create the tokenizers
                    st1 = new StringTokenizer(candidates.get(i));
                    st2 = new StringTokenizer(candidates.get(j));
                    
                    
                    String a = candidates.get(i);
                    String b = candidates.get(j);
                    	
                    
                    
            		int freq_count=0;
            		Vector<String> match = new Vector<String>();
            
            		List<String> aa = Arrays.asList(a.split(" "));
            		List<String> bb = Arrays.asList(b.split(" "));
            		 
                    
            		for(int ii=0; ii<aa.size(); ii++)
            		{
            			for(int jj=ii;jj<bb.size(); jj++)
            			{
            				
            				//if there is a match character
            				if(aa.get(ii).compareToIgnoreCase(bb.get(jj))==0)
            				{
            					
            					match.add(aa.get(ii));
            					
            					freq_count++;
            				}
            			}
            		}
            		
            					
            					//number of matching characters is (n-2)
            					if(freq_count==n-2)
            					{
 
            						// if have (n-2) matching characters, join aa and bb
            						//Set<String> set = new HashSet<>(aa);
            						//set.addAll(bb);
            						//Vector<String> merged = new Vector<>(set);
            						String matchSplit = bb.get(bb.size()-1);
            						
            						 
            						
            						if(aa.contains(matchSplit)==false){
            							Vector<String> merged = new Vector<String>(aa);
            							merged.add(matchSplit);
            							
            							//System.out.println(aa + " merge with " + bb + " become " + merged);
            							
            							
            							Iterator<String> itr = merged.iterator();
                						
                						Object Candidate = itr.next();
                						
                						
                						while(itr.hasNext())
                						{
                							Candidate = Candidate + " " + itr.next();
                						}
                						
                						 
                						if(tempCandidates.contains(Candidate)==false)
                						{
                							 
                							tempCandidates.add((String) Candidate);
                							
                							
                							
                							//--------------output complete join result--------------
                							//System.out.println("join history:" + tempCandidates); 
                							
                						}
                						
            							
            							
            						}
            						
            						           						
            					 
            			
            		}       
                    
                    
                }
            }
            
            //System.out.println("temp candidates:" + tempCandidates);  
        }
        
        System.out.println("temp candidates:" + tempCandidates);
        
        
        int currentDepth = 0;
        int currentIndex = 0;
        int numNode = 0;
        for(int ii=0; ii < tempCandidates.size(); ii++){
        	String candName = tempCandidates.get(ii);
        	String[] candSplit = candName.split(" ");
        	int candDepth = candSplit.length; //depth = candDepth-1
        	
        	if(candDepth -1 > currentDepth){
        		currentIndex = 0;
        		currentDepth = candDepth -1;
        	}
        	
        	JSONObject node = new JSONObject();
    	    JSONArray parents = new JSONArray();
    	    		    
    	    //load data from here
    	    node.put("id", new String(Integer.toString(currentDepth) + " " + Integer.toString(currentIndex)));
    	    node.put("name", new String(candName));
    	    node.put("freq", new Float(0));
    	      
    	    
    	    
    	    node.put("parentid", parents); 
    	    
    	    nodes.add(node);
    	    
    	    currentIndex++;
    	 
    	    
        }
        
        json.put("numNodes", nodes.size());
        
        json.put("minSup", minSup);
        
        json.put("numItems", numItems);
        
        
        
        
        
        
        
        //clear the old candidates
         candidates.clear();
         candidates = new Vector<String>(tempCandidates);
         tempCandidates.clear();
          
         
    }

    
    //method: find all (n-1) subsets  and prune infrequent subset, n is itemset size
    
    private void pruneSubset(int n)
    {
    	Iterator<String> itr = candidates.iterator();
    	int countCandidate = 0;
		 
    	//decompose into single entries
		while(itr.hasNext())
		{
			//array<String> itemSplit = Arrays.asList(itr.next().split(" "));
			String currentCandidate = itr.next();
			String[] itemSplit = currentCandidate.split(" ");
			
			//System.out.println("items:" + itemSplit);
			
			
			Vector<String> itemSubset = new Vector<String>();
			for(int i=0;i<itemSplit.length;i++)
			{
				String itemString = new String();
				for(int j=0;j<itemSplit.length;j++)
				{
					if(i!=j){
						itemString +=itemSplit[j] + " ";
					}					
				}				
				itemSubset.add(itemString.trim());
				 
								 
			}
			
			 
			//output all subsets for each candidate
			//System.out.println("parent of " + currentCandidate +" is " + itemSubset);	
			
			
			for (int i=0; i<nodes.size(); i++){
						
				JSONObject tempNode = (JSONObject) nodes.get(i);
				
				//tempNode.put("freq", 1);
				if (tempNode.get("name").equals(currentCandidate)){
										
					JSONArray parents = new JSONArray();
										
					Vector<String> itemParents = new Vector<String>(2);
					int itemSize = itemSubset.size();
					
					if(itemSize >1 ){
						
						
						//itemParents.add( itemSubset.get(itemSize - 1));
						//itemParents.add( itemSubset.get(itemSize - 2));
						Collections.reverse(itemSubset);
						itemParents = itemSubset;
						
						
						
						 
					}else{
						itemParents = itemSubset;
					}
					
					parents.add(itemParents);
					
					tempNode.put("parentid", parents.get(0));
					
				}
				
				//System.out.println("node name ======= " + tempNode.get("name"));
				
				
				
			}
		
					

			 
			//System.out.println("Previous:" + prevCandidates);	
			
			if(n>2){
			
			Iterator<String> itr1 = itemSubset.iterator(); 
			
			
			while(itr1.hasNext()){
				String str1 = new String(itr1.next());
				
				Iterator<String> itr2 = prevCandidates.iterator(); 
				while(itr2.hasNext()){
					String str2 = new String(itr2.next());
					
					if(str1.equals(str2)){
						leftCandidates.add(str1);
					}
				}
			}
			
			
			if (leftCandidates.equals(itemSubset)){
				
				//System.out.println("subset " + itemSubset +" is parent of [" + currentCandidate +"]");	
				countCandidate++;
			}else{
				//System.out.println("candidate [" + currentCandidate +"] is pruned due to its subsets not in parent nodes");	
				
				for (int i=0; i<nodes.size(); i++){
					JSONObject pruneNode = (JSONObject) nodes.get(i);	
					if( pruneNode.get("name").equals(currentCandidate)){
						pruneNode.put("freq", -1);
					}
					
				}
			}
			
			//
			
			}
			leftCandidates.clear();
		}
    	 
		
		
		System.out.println("candidate size=" + countCandidate + " after prunning" );
		 
    } 
    
 
    
  

    /************************************************************************
     * Method Name  : calculateFrequentItemsets
     * Purpose      : Determine which candidates are frequent in the n-th itemsets
     *              : from all possible candidates
     * Parameters   : n - iteger representing the current itemsets being evaluated
     * Return       : None
     *************************************************************************/
    private void calculateFrequentItemsets(int n)
    {
        Vector<String> frequentCandidates = new Vector<String>(); //the frequent candidates for the current itemset
        FileInputStream file_in; //file input stream
        BufferedReader data_in; //data input stream
        FileWriter fw;
        BufferedWriter file_out;

        StringTokenizer st, stFile; //tokenizer for candidate and transaction
        boolean match; //whether the transaction has all the items in an itemset
        boolean trans[] = new boolean[numItems]; //array to hold a transaction so that can be checked
        int count[] = new int[candidates.size()]; //the number of successful matches

        try
        {
                //output file
                fw= new FileWriter(outputFile, true);
                file_out = new BufferedWriter(fw);
                //load the transaction file
                file_in = new FileInputStream(transaFile);
                data_in = new BufferedReader(new InputStreamReader(file_in));

                //for each transaction
                for(int i=0; i<numTransactions; i++)
                {
                    //System.out.println("Got here " + i + " times"); //useful to debug files that you are unsure of the number of line
                    stFile = new StringTokenizer(data_in.readLine(), itemSep); //read a line from the file to the tokenizer
                    //put the contents of that line into the transaction array
                    for(int j=0; j<numItems; j++)
                    {
                        trans[j]=(stFile.nextToken().compareToIgnoreCase(oneVal[j])==0); //if it is not a 0, assign the value to true
                    }

                    //check each candidate
                    for(int c=0; c<candidates.size(); c++)
                    {
                        match = false; //reset match to false
                        //tokenize the candidate so that we know what items need to be present for a match
                        st = new StringTokenizer(candidates.get(c));
                        //check each item in the itemset to see if it is present in the transaction
                        while(st.hasMoreTokens())
                        {
                            match = (trans[Integer.valueOf(st.nextToken())-1]);
                            if(!match) //if it is not present in the transaction stop checking
                                break;
                        }
                        if(match) //if at this point it is a match, increase the count
                            count[c]++;
                        //System.out.println("---Freq---:"+ frequentCandidates);
                    }
                    
                }
                
                //System.out.println("---Temp---:"+ candidates);
                
                for(int i=0; i<candidates.size(); i++)
                {
                	//System.out.println("---Count---:"+ count[i]);
                	
                    //System.out.println("Candidate: " + candidates.get(c) + " with count: " + count + " % is: " + (count/(double)numItems));
                    //if the count% is larger than the minSup%, add to the candidate to the frequent candidates
                    if((count[i]/(double)numTransactions)>=minSup)
                    {
                        frequentCandidates.add(candidates.get(i));
                        //put the frequent itemset into the output file
                        file_out.write(candidates.get(i) + "," + count[i]/(double)numTransactions + "\n");
                        
                        float tempFreq = (float) (count[i]/(double)numTransactions);
                        
                        for(int j=0; j<nodes.size(); j++){
                        	JSONObject tempNode = (JSONObject) nodes.get(j);
            				
            				if(tempNode.get("name").equals(candidates.get(i))){
            					
            					//System.out.println("SUCCESS tempFreq = " + tempFreq);
                        		tempNode.put("freq", tempFreq ); //put frequency
                        		
                        	} 
                        }
                        
                    }else{
                    	//System.out.println("low freq = " + (count[i]/(double)numTransactions));
                    	
                    	float tempFreq = (float) (count[i]/(double)numTransactions);
                    	
                    	for(int j=0; j<nodes.size(); j++){
                        	JSONObject tempNode = (JSONObject) nodes.get(j);
            				
            				if(tempNode.get("name").equals(candidates.get(i))){
            					
            					//System.out.println("SUCCESS tempFreq = " + tempFreq);
                        		if( !tempNode.get("freq").equals(-1) ){
                        			tempNode.put("freq", tempFreq );
                        		}//put frequency
                        		
                        	} 
                        }
                    	
                    }
                    
                    
                    /*else{
                    	float tempFreq = (float) (count[i]/(double)numTransactions);
                        
                        for(int j=0; j<nodes.size(); j++){
                        	JSONObject tempNode = (JSONObject) nodes.get(j);
            				
            				if(tempNode.get("name").equals(candidates.get(i))){
            					
            					//System.out.println("SUCCESS tempFreq = " + tempFreq);
                        		//tempNode.put("freq", tempFreq ); //put frequency
                        		
                        	} 
                        }
                    }*/
                    
                    
                    
                    
                    
                }
                file_out.write("-\n");
                file_out.close();
        }
        //if error at all in this process, catch it and print the error messate
        catch(IOException e)
        {
            System.out.println(e);
        }
        //clear old candidates
        candidates.clear();
        //new candidates are the old frequent candidates
        candidates = new Vector<String>(frequentCandidates);
        frequentCandidates.clear();
    }
}