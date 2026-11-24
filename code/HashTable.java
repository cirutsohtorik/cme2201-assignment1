public abstract class HashTable implements DictionaryADT {

    private Entry [] entries;
    private int arraySize;
    private int fullEntryCount=0;
    private double loadFactorLimit;
    private HashFunction hashFunction;
    private long collisionCount;
    private boolean isResizing=false; //no collision count while rehashing

    HashTable(int arraySize, double loadFactorLimit, HashFunction hashFunction){
        if(arraySize<=0){throw new IllegalArgumentException("array size must be positive.");}
        if(loadFactorLimit<=0 || loadFactorLimit>1){throw new IllegalArgumentException("load factor must be between 0 and 1.");}
        this.arraySize=arraySize;
        this.entries= new Entry[arraySize];
        this.loadFactorLimit=loadFactorLimit;
        this.hashFunction=hashFunction;
    }
 
    public abstract int getStep(String word);
        
    
    
    public Entry[] getEntryArray(){
        return entries;
    }
    

    public int getfullEntryCount(){
        return fullEntryCount;
    }

    public double getloadFactorLimit(){
        return loadFactorLimit;
    }

    public HashFunction gethashFunction(){
        if(hashFunction==null){throw new IllegalArgumentException("proper hash function method didn't specified.");}
        return hashFunction;
    }


    
    public long getcollisionCount(){
        return collisionCount;
    }

    public int turnToCellIndex(int value){
        return Math.abs(value%entries.length); //no negative index
    }




    public int findStart(String key){
        return gethashFunction().hashFunction(key);
    }

    public void put(String key, Object value){  
        
        int start=turnToCellIndex(findStart(key)); boolean flag=true; 
        int step=getStep(key);
        int probeCount=0;

        while(flag){
            if(probeCount>entries.length){throw new IllegalStateException("no empty cell found for key: "+key);} //no endless loop
            probeCount++;
            if(start>=entries.length){start=start%entries.length;}
        if(entries[start]==null){ //check if there isn't any entry
            Entry a=new Entry(key,value);
            entries[start]=a;fullEntryCount++; flag=false;
        }

        else if(entries[start]!=null && !entries[start].isErased() && entries[start].getKey().equals(key)){ 

            
            entries[start].setValue(value); flag=false; //if keys are same, update value
        }

        else{   
            start+=step;
            if(!isResizing){collisionCount++;}
        }
    }
    if(loadFactorLimit<=((double)fullEntryCount/(double)arraySize)){resize();}
    }

    public void resize(){
        resize(getnextPrime(arraySize)); //next prime after 2*size
    }

    public void resize(int capacity){
        if(capacity<=fullEntryCount){throw new IllegalArgumentException("new capacity is too small.");}

        Entry [] old=entries;
        arraySize=capacity;
        entries=new Entry[arraySize];
        fullEntryCount=0; isResizing=true;
        for(int i=0;i<old.length;i++){
            if(old[i]!=null && !old[i].isErased()){put(old[i].getKey(),old[i].getValue());}
        }
        isResizing=false;
    }

    public Object get(String key){

        int start=turnToCellIndex(findStart(key));
        int step=getStep(key);
        for(int i=0;i<entries.length;i++){
            int index=start%entries.length;
            if(entries[index]==null){return null;}
            else if(entries[index].isErased()){start+=step;continue;}

            else if(entries[index].getKey().equals(key)){return entries[index].getValue();}

        
            else {
                start+=step;}
        }
        return null;
    }


    public Object remove(String key){
        int start=turnToCellIndex(findStart(key));
        int step=getStep(key);
        for(int i=0;i<entries.length;i++){
            int index=start%entries.length;
            if(entries[index]==null){return null;}

            else if(entries[index].isErased()){start+=step;continue;}
            else if(entries[index].getKey().equals(key)){
                Object temp=entries[index].getValue();
                entries[index].eraseValue();
                return temp;
            }

            else{start+=step;}

        }
        return null;
    }

    public int getnextPrime(int n){

        int index=n*2-1;boolean flag=true;

        while(flag){
            index++;
            boolean isPrime=true;
            for(int i=2;i<=(int)Math.sqrt(index);i++){
                if(index%i==0){isPrime=false; break;}
            }
            if(isPrime){flag=false;}
    }
        return index;
}


}