import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final String DOCUMENTS_FOLDER="bbc"; //program is run from ds-assignment1 folder
    private static final String STOP_WORDS_FILE="stop_words_en.txt";
    private static final String SEARCH_FILE="1000.txt";

    public static void main(String[] args){
        if(!new File(DOCUMENTS_FOLDER).isDirectory()){System.out.println("bbc folder couldn't be found, open ds-assignment1 folder and run again."); return;}

        Scanner scanner=new Scanner(System.in);
        boolean flag=true;
        while(flag){
            System.out.println("\n1- Run performance test (8 combinations)");
            System.out.println("2- Build an index, then search / remove");
            System.out.println("0- Exit");
            System.out.print("> ");
            String choice=scanner.nextLine().trim();

            try{
                if(choice.equals("1")){runPerformanceTest();}
                else if(choice.equals("2")){runIndexMenu(scanner);}
                else if(choice.equals("0")){flag=false;}
                else{System.out.println("wrong choice.");}
            }
            catch(IllegalArgumentException e){System.out.println("error: "+e.getMessage());}
            catch(IllegalStateException e){System.out.println("error: "+e.getMessage());}
        }
        scanner.close();
    }

    public static HashFunction createHashFunction(String name){
        if(name.equals("SSF")){return new SsfHash();}
        if(name.equals("PAF")){return new PafHash();}
        throw new IllegalArgumentException("hash function must be SSF or PAF.");
    }

    public static InvertedIndex buildIndex(String collisionType,double loadFactor,String hashName){
        InvertedIndex index=new InvertedIndex(collisionType,loadFactor,createHashFunction(hashName));
        index.loadStopWords(STOP_WORDS_FILE); index.loadDocuments(DOCUMENTS_FOLDER);
        return index;
    }

    public static String [] readSearchWords(String fileName){
        String [] words=new String[1000];
        int count=0;
        try{
            BufferedReader reader=new BufferedReader(new FileReader(fileName));
            String line=reader.readLine();
            while(line!=null){
                line=line.trim().toLowerCase();
                if(!line.isEmpty()){
                    if(count==words.length){ //full, make double
                        String [] old=words; words=new String[old.length*2];
                        for(int i=0;i<old.length;i++){words[i]=old[i];}
                    }
                    words[count]=line; count++;
                }
                line=reader.readLine();
            }
            reader.close();
        }
        catch(IOException e){System.out.println("search file couldn't be read: "+fileName);}

        String [] result=new String[count];
        for(int i=0;i<count;i++){result[i]=words[i];}
        return result;
    }

    public static void runPerformanceTest(){
        double [] loadFactors={0.5,0.8};
        String [] hashNames={"SSF","PAF"};
        String [] collisionTypes={"LP","DH"};
        String [] searchWords=readSearchWords(SEARCH_FILE);
        if(searchWords.length==0){throw new IllegalStateException("there is no word to search.");}

        System.out.println("warming up...");
        buildIndex("DH",0.5,"PAF"); //jvm warm up, not measured

        System.out.println("LF\tHash\tColl.\tIdx(ms)\tAvg(ns)\tMin(ns)\tMax(ns)\tCollisions");
        for(int i=0;i<loadFactors.length;i++){
            for(int j=0;j<hashNames.length;j++){
                for(int k=0;k<collisionTypes.length;k++){
                    InvertedIndex index=new InvertedIndex(collisionTypes[k],loadFactors[i],createHashFunction(hashNames[j]));
                    index.loadStopWords(STOP_WORDS_FILE);

                    long startTime=System.nanoTime();
                    index.loadDocuments(DOCUMENTS_FOLDER);
                    long indexingTime=(System.nanoTime()-startTime)/1000000;

                    HashTable wordTable=index.getwordTable();
                    for(int s=0;s<searchWords.length;s++){wordTable.get(searchWords[s]);} //warm up

                    long totalTime=0; long minTime=-1; long maxTime=0;
                    for(int s=0;s<searchWords.length;s++){
                        long searchStart=System.nanoTime();
                        wordTable.get(searchWords[s]); //only search, no output
                        long time=System.nanoTime()-searchStart;
                        totalTime+=time;
                        if(minTime==-1 || time<minTime){minTime=time;}
                        if(time>maxTime){maxTime=time;}
                    }

                    System.out.println((int)(loadFactors[i]*100)+"%\t"+hashNames[j]+"\t"+collisionTypes[k]+"\t"+indexingTime
                        +"\t"+(totalTime/searchWords.length)+"\t"+minTime+"\t"+maxTime+"\t"+wordTable.getcollisionCount());
                }
            }
        }
    }

    public static void runIndexMenu(Scanner scanner){
        double loadFactor;
        System.out.print("Load factor (0.5 / 0.8): ");
        try{loadFactor=Double.parseDouble(scanner.nextLine().trim());}
        catch(NumberFormatException e){System.out.println("load factor must be a number."); return;}
        System.out.print("Hash function (SSF / PAF): "); String hashName=scanner.nextLine().trim().toUpperCase();
        System.out.print("Collision handling (LP / DH): "); String collisionType=scanner.nextLine().trim().toUpperCase();

        System.out.println("indexing documents...");
        InvertedIndex index=buildIndex(collisionType,loadFactor,hashName);
        System.out.println(index.getwordTable().getfullEntryCount()+" words indexed.");

        boolean flag=true;
        while(flag){
            System.out.println("\n1- Search  2- Remove  0- Back");
            System.out.print("> ");
            String choice=scanner.nextLine().trim();
            if(choice.equals("1")){System.out.print(">Search: "); System.out.println(index.get(scanner.nextLine().trim()));}
            else if(choice.equals("2")){
                System.out.print(">Remove: "); String word=scanner.nextLine().trim();
                if(index.remove(word)){System.out.println(word+" removed.");}
                else{System.out.println("Not found!");}
            }
            else if(choice.equals("0")){flag=false;}
            else{System.out.println("wrong choice.");}
        }
    }

}
