public interface DictionaryADT {

    public void put(String key, Object value); //adds or updates

    public Object get(String key); //null if not found

    public Object remove(String key); //null if not found

    public void resize(int capacity); //moves entries to new array

    public int getfullEntryCount();

}
