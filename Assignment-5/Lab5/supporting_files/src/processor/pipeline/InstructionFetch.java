package processor.pipeline;

import generic.Element;
import generic.Event;
import generic.MemoryReadEvent;
import generic.MemoryResponseEvent;
import generic.Simulator;
import generic.Event.EventType;
import processor.Clock;
import processor.Processor;

public class InstructionFetch implements Element {
	
	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	EX_IF_LatchType EX_IF_Latch;
	
	public InstructionFetch(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch, IF_OF_LatchType iF_OF_Latch, EX_IF_LatchType eX_IF_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_EnableLatch = iF_EnableLatch;
		this.IF_OF_Latch = iF_OF_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
	}
	
	public void performIF()
	{

		if(!IF_EnableLatch.isIFBusy()){
			if(IF_EnableLatch.isIF_enable())
			{
				if(IF_OF_Latch.isOF_busy()) {
					// Stall IF stage: do not fetch new instruction, OF is busy
					return;
				}
				if(EX_IF_Latch.isEX_IF_enable()){
					System.out.println("PC update after EX stage in IF stage: "+EX_IF_Latch.getPC());
					containingProcessor.getRegisterFile().setProgramCounter(EX_IF_Latch.getPC());
					EX_IF_Latch.setEX_IF_enable(false);
				}
				int currentPC = containingProcessor.getRegisterFile().getProgramCounter();
				// int newInstruction = containingProcessor.getMainMemory().getWord(currentPC);
				// IF_OF_Latch.setInstruction(newInstruction);
				// IF_OF_Latch.setInstructionPC(currentPC);
				// System.out.println("=======\nIF stage\nPC="+containingProcessor.getRegisterFile().getProgramCounter()+"\n"+"Instruction: "+newInstruction+"\n===========");
				
				// // IF_EnableLatch.setIF_enable(false);
				// System.out.println("IF-OF latch enabled");
				// IF_OF_Latch.setOF_enable(true);
				// IF_OF_Latch.setNop(false);
				
				
				System.out.println("("+Clock.getCurrentTime()+")"+"IF memory fetch added to queue. Current PC: "+currentPC);
				
				Simulator.getEventQueue().addEvent(new MemoryReadEvent(
					Clock.getCurrentTime()+configuration.Configuration.mainMemoryLatency,
					this, 
					containingProcessor.getMainMemory(),
					currentPC
				));
				
				containingProcessor.getRegisterFile().setProgramCounter(currentPC + 1);
				IF_EnableLatch.setIFBusy(true);
			}

		}
	}

	@Override
	public void handleEvent(Event e){
		if(e.getEventType()==EventType.MemoryResponse){
			System.out.println("\nIF event handler\n"+Clock.getCurrentTime());
			if (EX_IF_Latch.isEX_IF_enable()) {
				System.out.println("Branch taken detected in IF event handler. Discarding fetched instruction.");
				IF_EnableLatch.setIFBusy(false);
				return;
			}
			MemoryResponseEvent memoryResponseEvent = (MemoryResponseEvent) e;
			System.out.println("IF stage memory access handled");
			int newInstruction = memoryResponseEvent.getResponse();
			IF_OF_Latch.setInstruction(newInstruction);
			int currentPC = containingProcessor.getRegisterFile().getProgramCounter()-1;
			IF_OF_Latch.setInstructionPC(currentPC);
			containingProcessor.getRegisterFile().setProgramCounter(currentPC + 1);
			System.out.println("=======\nIF stage\nPC="+containingProcessor.getRegisterFile().getProgramCounter()+"\n"+"Instruction: "+newInstruction+"\n===========");
			
			
			System.out.println("IF-OF latch enabled");
			IF_OF_Latch.setOF_enable(true);
			IF_OF_Latch.setNop(false);
			
			IF_EnableLatch.setIFBusy(false);
			System.out.println("\nIF event handler\n");
		}
	}

}
