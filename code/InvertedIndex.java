import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class InvertedIndex {
    private static final int INITIAL_SIZE=11; //prime and >7 for double hashing
    private static final String DELIMITERS = "[-+=" +
		        " " +        //space
		        "\r\n " +    //carriage return line fit
				"1234567890" + //numbers
				"’'\"" +       // apostrophe
				"(){}<>\\[\\]" + // brackets
				":" +        // colon
				"," +        // comma
				"‒–—―" +     // dashes
				"…" +        // ellipsis
				"!" +        // exclamation mark
				"." +        // full stop/period
				"«»" +       // guillemets
				"-‐" +       // hyphen
				"?" +        // question mark
				"‘’“”" +     // quotation marks
				";" +        // semicolon
				"/" +        // slash/stroke
				"⁄" +        // solidus
				"␠" +        // space?
				"·" +        // interpunct
				"&" +        // ampersand
				"@" +        // at sign
				"*" +        // asterisk
				"\\" +       // backslash
				"•" +        // bullet
				"^" +        // caret
				"¤¢$€£¥₩₪" + // currency
				"†‡" +       // dagger
				"°" +        // degree
				"¡" +        // inverted exclamation point
				"¿" +        // inverted question mark
				"¬" +        // negation
				"#" +        // number sign (hashtag)
				"№" +        // numero sign ()
				"%‰‱" +      // percent and related signs
				"¶" +        // pilcrow
				"′" +        // prime
				"§" +        // section sign
				"~" +        // tilde/swung dash
				"¨" +        // umlaut/diaeresis
				"_" +        // underscore/understrike
				"|¦" +       // vertical/pipe/broken bar
				"⁂" +        // asterism
				"☞" +        // index/fist
				"∴" +        // therefore sign
				"‽" +        // interrobang
				"※" +          // reference mark
		        "]+"; //+ for side by side delimiters

    private HashTable wordTable; //word -> table of documents
    private HashTable stopWords;
    private String collisionType; //"LP" or "DH"
    private double loadFactorLimit;
    private HashFunction hashFunction;

    InvertedIndex(String collisionType,double loadFactorLimit,HashFunction hashFunction){
        if(!collisionType.equals("LP") && !collisionType.equals("DH")){throw new IllegalArgumentException("collision type must be LP or DH.");}
        this.collisionType=collisionType; this.loadFactorLimit=loadFactorLimit; this.hashFunction=hashFunction;
        this.wordTable=createTable();
        this.stopWords=new LinearProbingTable(INITIAL_SIZE,0.5,new PafHash()); //not counted in tests
    }

    public HashTable getwordTable(){
        return wordTable;
    }

    public HashTable createTable(){
        if(collisionType.equals("DH")){return new DoubleHashingTable(INITIAL_SIZE,loadFactorLimit,hashFunction);}
        return new LinearProbingTable(INITIAL_SIZE,loadFactorLimit,hashFunction);
    }

    public void put(String word,String docName){
        word=word.toLowerCase();
        HashTable innerTable=(HashTable)wordTable.get(word);
        if(innerTable==null){innerTable=createTable(); wordTable.put(word,innerTable);} //new word

        Integer frequency=(Integer)innerTable.get(docName);
        if(frequency==null){innerTable.put(docName,1);} //first time in document
        else{innerTable.put(docName,frequency+1);}
    }

    public String get(String word){
        word=word.toLowerCase();
        HashTable innerTable=(HashTable)wordTable.get(word);
        if(innerTable==null){return "Not found!";}

        Entry [] cells=innerTable.getEntryArray();
        Entry [] documents=new Entry[innerTable.getfullEntryCount()]; int count=0;
        for(int i=0;i<cells.length;i++){
            if(cells[i]!=null && !cells[i].isErased()){documents[count]=cells[i];count++;}
        }

        for(int i=1;i<count;i++){ //insertion sort, biggest first
            Entry temp=documents[i]; int j=i-1;
            while(j>=0 && (Integer)documents[j].getValue()<(Integer)temp.getValue()){
                documents[j+1]=documents[j]; j--;
            }
            documents[j+1]=temp;
        }

        String result=count+" documents found";
        for(int i=0;i<count;i++){result+="\n"+documents[i].getValue()+"-"+documents[i].getKey();}
        return result;
    }

    public boolean remove(String word){
        return wordTable.remove(word.toLowerCase())!=null; //true if removed
    }

    public boolean isStopWord(String word){
        return stopWords.get(word)!=null;
    }

    public void loadStopWords(String fileName){
        try{
            BufferedReader reader=new BufferedReader(new FileReader(fileName));
            String line=reader.readLine();
            while(line!=null){
                line=line.trim().toLowerCase();
                if(!line.isEmpty()){stopWords.put(line,true);}
                line=reader.readLine();
            }
            reader.close();
        }
        catch(IOException e){System.out.println("stop words file couldn't be read: "+fileName);}
    }

    public void loadDocuments(String folderName){
        File [] categories=new File(folderName).listFiles();
        if(categories==null){throw new IllegalArgumentException("documents folder not found: "+folderName);}

        for(int i=0;i<categories.length;i++){
            if(!categories[i].isDirectory()){continue;}
            File [] files=categories[i].listFiles();
            for(int j=0;j<files.length;j++){
                loadDocument(files[j],categories[i].getName()+"/"+files[j].getName()); //same file names in every category
            }
        }
    }

    public void loadDocument(File file,String docName){
        try{
            BufferedReader reader=new BufferedReader(new FileReader(file));
            String line=reader.readLine();
            while(line!=null){
                String [] words=line.toLowerCase().split(DELIMITERS);
                for(int i=0;i<words.length;i++){
                    if(!words[i].isEmpty() && !isStopWord(words[i])){put(words[i],docName);}
                }
                line=reader.readLine();
            }
            reader.close();
        }
        catch(IOException e){System.out.println("file couldn't be read, skipped: "+docName);}
    }

}
