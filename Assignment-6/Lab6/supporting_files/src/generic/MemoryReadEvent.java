package generic;

import generic.Event.EventType;

public class MemoryReadEvent extends Event {
    int addressToReadFrom;
    public MemoryReadEvent(long eventTime, Element requestingtElement, Element processingElement, int address){
        super(eventTime, EventType.MemoryRead, requestingtElement, processingElement);
        this.addressToReadFrom = address;
    }
    public void setAddressToReadFrom(int addressToReadFrom) {
        this.addressToReadFrom = addressToReadFrom;
    }
    public int getAddressToReadFrom() {
        return addressToReadFrom;
    }
}
