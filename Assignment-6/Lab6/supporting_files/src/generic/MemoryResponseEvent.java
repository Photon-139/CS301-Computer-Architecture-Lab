package generic;

public class MemoryResponseEvent extends Event {
    int response;

    public MemoryResponseEvent(long eventTime, Element requestingElement, Element processingElement, int response){
        super(eventTime, EventType.MemoryResponse, requestingElement, processingElement);
        this.response = response;
    }
    public int getResponse() {
        return response;
    }
    public void setResponse(int response) {
        this.response = response;
    }
    
}
