package processor.pipeline;

import generic.Element;
import generic.Event;
import generic.Instruction;
import generic.MemoryReadEvent;
import generic.MemoryResponseEvent;
import generic.MemoryWriteEvent;
import generic.Simulator;
import generic.Event.EventType;
import generic.Instruction.OperationType;
import processor.Clock;
import processor.Processor;

public class MemoryAccess implements Element{
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	
	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
	}
	
	public void performMA()
	{
		//TODO
		if(!EX_MA_Latch.isMA_busy()){
			if(EX_MA_Latch.isMA_enable() && !EX_MA_Latch.isNop()){
				
				Instruction inst = EX_MA_Latch.getInstruction();
				int aluResult = EX_MA_Latch.getAluResult();
				OperationType operationType = inst.getOperationType();
				System.out.println("========\nMA Stage\nOperation type: "+operationType.toString());
				if(operationType==OperationType.store){
					int rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					// containingProcessor.getRegisterFile().setValue(rs1, containingProcessor.getMainMemory().getWord(aluResult));


					// containingProcessor.getMainMemory().setWord(aluResult, rs1);


					// System.out.println("rs1="+rs1+"\nvalue: "+containingProcessor.getMainMemory().getWord(aluResult));
					Simulator.getEventQueue().addEvent(new MemoryWriteEvent(
						Clock.getCurrentTime()+configuration.Configuration.mainMemoryLatency,
						this, 
						containingProcessor.getMainMemory(), 
						aluResult, 
						rs1
					));
					EX_MA_Latch.setMA_busy(true);
					
					// Pass NOP forward while waiting
					MA_RW_Latch.setNop(true);
					MA_RW_Latch.setRW_enable(true);
				}else{
					if(operationType==OperationType.load){
						// MA_RW_Latch.setAluResult(containingProcessor.getMainMemory().getWord(aluResult));
						// System.out.println("Load instruction alu: "+MA_RW_Latch.getAluResult());

						Simulator.getEventQueue().addEvent(new MemoryReadEvent(
							Clock.getCurrentTime()+configuration.Configuration.mainMemoryLatency,
							this,
							containingProcessor.getMainMemory(),
							aluResult
						));
						EX_MA_Latch.setMA_busy(true);
						
						// Pass NOP forward while waiting
						MA_RW_Latch.setNop(true);
						MA_RW_Latch.setRW_enable(true);

					}else{
						MA_RW_Latch.setAluResult(aluResult);
						// EX_MA_Latch.setMA_busy(false); Already happens below or elsewhere

					}
				}
				EX_MA_Latch.setMA_enable(false);
				if (!EX_MA_Latch.isMA_busy()) {
					System.out.println("MA-RW latch enabled");
					MA_RW_Latch.setRW_enable(true);
					MA_RW_Latch.setInstruction(inst);
					System.out.println("ALU Result set: "+MA_RW_Latch.getAluResult());
					MA_RW_Latch.setNop(false);
				}
				
	
			}else if(EX_MA_Latch.isNop()){
				System.out.println("MA stage, NOP detected");
				MA_RW_Latch.setNop(true);
				EX_MA_Latch.setMA_busy(false);
			}
			// MA_RW_Latch.setMA_RWBusy(false);
			
		}else{
			// EX_MA_Latch.setMA_busy(true);
			// System.out.println("\n("+Clock.getCurrentTime()+") "+"MA stage found busy, setting EX stage busy\n");
			
		}
	}

	@Override
	public void handleEvent(Event e){
		if(e.getEventType()==EventType.MemoryResponse){
			MemoryResponseEvent memoryResponseEvent = (MemoryResponseEvent) e;
			MA_RW_Latch.setAluResult(memoryResponseEvent.getResponse());
			EX_MA_Latch.setMA_busy(false);
			
			// Put instruction in latch and enable RW
			MA_RW_Latch.setInstruction(EX_MA_Latch.getInstruction());
			MA_RW_Latch.setRW_enable(true);
			MA_RW_Latch.setNop(false);
		} else if (e.getEventType() == EventType.ExecutionComplete) {
			EX_MA_Latch.setMA_busy(false);
			
			// Put instruction in latch and enable RW
			MA_RW_Latch.setInstruction(EX_MA_Latch.getInstruction());
			MA_RW_Latch.setRW_enable(true);
			MA_RW_Latch.setNop(false);
		}
	}
}
