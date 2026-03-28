package processor.memorysystem;

import configuration.Configuration;
import generic.Element;
import generic.Event;
import generic.MemoryReadEvent;
import generic.MemoryResponseEvent;
import generic.MemoryWriteEvent;
import generic.Simulator;
import generic.Event.EventType;
import processor.Clock;
import processor.Processor;
import processor.pipeline.MemoryAccess;

public class Cache implements Element {

    Processor containingProcessor;
    int cacheLatency;
    int cacheSize;
    int numOfLines;
    int numOfSets;
    CacheLine[][] cacheLines;
    int tagBits;
    int indexBits;
    int associativity;

    int offset_bits = 0; // Memory is word addressable instead of byte addressable

    private int cacheMissAddress;
    private Element cacheMissElement;
    private boolean isRead;
    private int writeData;

    public Cache(Processor processor, int numOfLines, int cacheLatency, int associativity){
        this.containingProcessor = processor;
        this.numOfLines = numOfLines;
        this.cacheLatency = cacheLatency;
        this.associativity = associativity;
        this.numOfSets = numOfLines/associativity;

        cacheLines = new CacheLine[numOfSets][associativity];
        for(int i = 0; i<numOfSets; i++){
            for(int j = 0; j<associativity; j++){
                cacheLines[i][j] = new CacheLine();
            }
        }
        this.indexBits = (int)(Math.log(numOfSets)/Math.log(2));
        this.tagBits = 32-indexBits-offset_bits;
        
        
    }

    public void cacheRead(int address, Element requestingElement){
        int index = (address >> offset_bits) & (numOfSets-1);
        int tag = address >> (offset_bits+indexBits);
        
        for(int i = 0; i<associativity; i++){
            if(cacheLines[index][i].getTag()==tag && cacheLines[index][i].isValid()){
                System.out.println("Cache hit while reading");

                Simulator.getEventQueue().addEvent(
                    new MemoryResponseEvent(Clock.getCurrentTime(), this, requestingElement, cacheLines[index][i].getData()
                ));

                cacheLines[index][i].setLastAccess(Clock.getCurrentTime());
                return;
            }
        }
        System.out.println("Cache miss while reading");
        handleCacheMiss(address, requestingElement);
        isRead = true;
    
    }

    public void handleCacheMiss(int address, Element requestingElement){

        Simulator.getEventQueue().addEvent(new MemoryReadEvent(
            Clock.getCurrentTime()+Configuration.mainMemoryLatency, this, containingProcessor.getMainMemory(), address
        ));
        cacheMissAddress = address;
        cacheMissElement = requestingElement;
    }

    public void handleResponse(int data){
        int index = (cacheMissAddress >> offset_bits) & (numOfSets-1);
        int tag = cacheMissAddress >> (offset_bits+indexBits);
        long minAccessTime = Long.MAX_VALUE;
        int indexToReplace = -1;
        for(int i = 0; i<associativity; i++){
            if(!cacheLines[index][i].isValid()){
                indexToReplace = i;
                break;
            }
            if(cacheLines[index][i].getLastAccess()<minAccessTime){
                indexToReplace = i;
                minAccessTime = cacheLines[index][i].getLastAccess();
            }
        }
        cacheLines[index][indexToReplace].setData(data);
        cacheLines[index][indexToReplace].setTag(tag);
        cacheLines[index][indexToReplace].setLastAccess(Clock.getCurrentTime());
        cacheLines[index][indexToReplace].setValid(true);

        if(isRead){
            Simulator.getEventQueue().addEvent(
                new MemoryResponseEvent(
                    Clock.getCurrentTime(), this, cacheMissElement, data
                ));
        }else{

            // Logic for write

            cacheLines[index][indexToReplace].setData(this.writeData);

            // Release the pipeline
            MemoryAccess ma = (MemoryAccess) cacheMissElement;
            ma.EX_MA_Latch.setMA_busy(false);
            ma.MA_RW_Latch.setRW_enable(true);

            // Write-Through: Update Main Memory
            Simulator.getEventQueue().addEvent(new MemoryWriteEvent(
                Clock.getCurrentTime() + Configuration.mainMemoryLatency, 
                this, containingProcessor.getMainMemory(), cacheMissAddress, writeData
            ));
        }

    }

    public void cacheWrite(int address, int data, Element requestingElement){
        int index = (address >> offset_bits) & (numOfSets-1);
        int tag = address >> (offset_bits+indexBits);
        
        for(int i = 0; i<associativity; i++){
            if(cacheLines[index][i].getTag()==tag && cacheLines[index][i].isValid()){
                System.out.println("Cache hit while writing");
                cacheLines[index][i].setData(data);
                cacheLines[index][i].setLastAccess(Clock.getCurrentTime());
                ((MemoryAccess)requestingElement).EX_MA_Latch.setMA_busy(false);
                ((MemoryAccess)requestingElement).MA_RW_Latch.setRW_enable(true);
                Simulator.getEventQueue().addEvent(new MemoryWriteEvent(
                    Clock.getCurrentTime()+configuration.Configuration.mainMemoryLatency, this, containingProcessor.getMainMemory(), address, data
                ));
                return;
            }
        }
        System.out.println("Cache miss while writing");
        handleCacheMiss(address, requestingElement);
        isRead = false;
        writeData = data;
    }

    @Override
    public void handleEvent(Event e){
        if(e.getEventType()==EventType.MemoryResponse){
            MemoryResponseEvent memoryResponseEvent = (MemoryResponseEvent) e;
            handleResponse(memoryResponseEvent.getResponse());
        }else if(e.getEventType()==EventType.MemoryWrite){
            MemoryWriteEvent memoryWriteEvent = (MemoryWriteEvent) e;
            cacheWrite(memoryWriteEvent.getAddress(), memoryWriteEvent.getData(), memoryWriteEvent.getRequestingElement());
        }else if(e.getEventType()==EventType.MemoryRead){
            MemoryReadEvent memoryReadEvent = (MemoryReadEvent) e;
            cacheRead(memoryReadEvent.getAddressToReadFrom(), memoryReadEvent.getRequestingElement());
        }
    }
}
