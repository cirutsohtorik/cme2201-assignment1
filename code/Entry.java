public class Entry{
    private String key;
    private Object value;
    private boolean isErased=false; //true=once full now empty, false=full


    Entry(String key,Object value){
        this.key=key;
        this.value=value;
    }

    public String getKey(){
        return key;
    }

    public Object getValue(){
        return value;
    }

    public boolean isErased(){
        return isErased;
    }

    public void eraseValue(){
        if(isErased){
            return;
        }

        value=null;
        isErased=true;
    }

    public void setValue(Object newValue){
        value=newValue;
    }

}