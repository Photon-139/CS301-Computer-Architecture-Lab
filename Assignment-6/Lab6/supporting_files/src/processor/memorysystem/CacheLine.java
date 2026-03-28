package processor.memorysystem;

public class CacheLine {
    int data; // Cache Line/Block size = 4B so a single integer does the trick
    int tag;
    boolean isValid;
    long lastAccessed;


    public CacheLine(){
        this.tag = -1;
        this.data = -1;
        this.isValid = false;
    }
    public int getData() {
        return data;
    }
    public int getTag() {
        return tag;
    }
    public void setData(int data) {
        this.data = data;
    }
    public void setTag(int tag) {
        this.tag = tag;
    }

    public void setValid(boolean signal){
        this.isValid = signal;
    }

    public boolean isValid(){
        return this.isValid;
    }

    public void setLastAccess(long time){
        this.lastAccessed = time;
    }
    public long getLastAccess(){
        return this.lastAccessed;
    }
}
