public class DoubleHashingTable extends HashTable{
    private static final int STEP_CONSTANT=7;
    DoubleHashingTable(int arraySize,double loadFactorLimit,HashFunction hashFunction){
        super(arraySize,loadFactorLimit,hashFunction);
        if(arraySize<=STEP_CONSTANT){throw new IllegalArgumentException("array size must be bigger than "+STEP_CONSTANT+" for double hashing.");}
        if(!isPrime(arraySize)){throw new IllegalArgumentException("array size must be prime for double hashing.");}
    }

    public void resize(int capacity){
        if(!isPrime(capacity)){throw new IllegalArgumentException("new capacity must be prime for double hashing.");}
        super.resize(capacity);
    }

    public boolean isPrime(int n){
        boolean isPrime=n>1;
        for(int i=2;i<=(int)Math.sqrt(n);i++){
            if(n%i==0){isPrime=false; break;}
        }
        return isPrime;
    }

    public int getStep(String word){
        int hash=findStart(word);
        return STEP_CONSTANT - (hash%STEP_CONSTANT);
    }    

}