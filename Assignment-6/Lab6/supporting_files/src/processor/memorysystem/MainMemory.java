package processor.memorysystem;

import generic.Element;
import generic.Event;
import generic.MemoryReadEvent;
import generic.MemoryResponseEvent;
import generic.MemoryWriteEvent;
import generic.MemoryWriteEvent;
import generic.Simulator;
import generic.Event.EventType;
import processor.Clock;

public class MainMemory implements Element {
	int[] memory;
	
	public MainMemory()
	{
		memory = new int[65536];
	}
	
	public int getWord(int address)
	{
		return memory[address];
	}
	
	public void setWord(int address, int value)
	{
		memory[address] = value;
	}
	
	public String getContentsAsString(int startingAddress, int endingAddress)
	{
		if(startingAddress == endingAddress)
			return "";
		
		StringBuilder sb = new StringBuilder();
		sb.append("\nMain Memory Contents:\n\n");
		for(int i = startingAddress; i <= endingAddress; i++)
		{
			sb.append(i + "\t\t: " + memory[i] + "\n");
		}
		sb.append("\n");
		return sb.toString();
	}

	public void handleEvent(Event e){
		if(e.getEventType()==EventType.MemoryRead){
			System.out.println("Memory Read event being handled");
			MemoryReadEvent memoryReadEvent = (MemoryReadEvent) e;
			Simulator.getEventQueue().addEvent(new MemoryResponseEvent(
				Clock.getCurrentTime(), 
				this, 
				memoryReadEvent.getRequestingElement(), 
				getWord(memoryReadEvent.getAddressToReadFrom())
			));
		}else if(e.getEventType()==EventType.MemoryWrite){
			System.out.println("Memory Write being handled");
			MemoryWriteEvent memoryWriteEvent = (MemoryWriteEvent) e;
			setWord(memoryWriteEvent.getAddress(), memoryWriteEvent.getData());
			Simulator.getEventQueue().addEvent(new Event(
				Clock.getCurrentTime(),
				EventType.ExecutionComplete,
				this,
				memoryWriteEvent.getRequestingElement()
			));
		}
	}
}
