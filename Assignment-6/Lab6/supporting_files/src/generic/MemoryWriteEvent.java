package generic;

public class MemoryWriteEvent extends Event {
    int address;
    int data;
    public MemoryWriteEvent(long eventTime, Element requestingElement, Element processingElement, int address, int data){
        super(eventTime, EventType.MemoryWrite, requestingElement, processingElement);
        this.address = address;
        this.data = data;
    }
    public int getData() {
        return data;
    }
    public int getAddress() {
        return address;
    }
    public void setAddress(int address) {
        this.address = address;
    }
    public void setData(int data) {
        this.data = data;
    }

}
