public class PafHash implements HashFunction {
    private static final int PAF_MOD = 1000000007; //big prime so no overflow

    public int hashFunction(String word){
        word=word.toLowerCase();

        long asciiCount=0; //int is not enough
        for(int i=0;i<word.length();i++){

            asciiCount=((asciiCount*33+word.charAt(i)%96)%PAF_MOD);
        }

        return (int)asciiCount;
    }
}
