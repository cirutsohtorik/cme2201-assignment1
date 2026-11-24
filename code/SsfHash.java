public class SsfHash implements HashFunction {

    public int hashFunction(String word){
        word=word.toLowerCase();
        int asciiCount=0;

        for(int i=0;i<word.length();i++){
            asciiCount+=word.charAt(i);
        }

        return asciiCount;
    }
    
}
