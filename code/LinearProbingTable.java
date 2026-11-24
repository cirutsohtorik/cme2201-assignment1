public class LinearProbingTable extends HashTable {

    LinearProbingTable(int arraySize,double loadFactorLimit,HashFunction hashFunction){
        super(arraySize,loadFactorLimit,hashFunction);
    }

    public int getStep(String word){
        return 1;
    }
    
}
