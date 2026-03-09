package processor.pipeline;

import generic.Instruction;
import generic.Simulator;
import generic.Instruction.OperationType;
import processor.Processor;

public class RegisterWrite {
	Processor containingProcessor;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	
	public RegisterWrite(Processor containingProcessor, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_EnableLatch)
	{
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}
	
	public void performRW()
	{
		if(MA_RW_Latch.isRW_enable() && !MA_RW_Latch.isNop())
		{
			//TODO
			
			// if instruction being processed is an end instruction, remember to call Simulator.setSimulationComplete(true);
			Instruction inst = MA_RW_Latch.getInstruction();
			int aluResult = MA_RW_Latch.getAluResult();
			System.out.println("=======\nRW Stage\nOperation Type: "+inst.getOperationType().toString());
			switch (inst.getOperationType()) {
				case bgt: case blt: case beq: case bne: case jmp: case store:
					System.out.println("Branching instruction");
					break;
				case end:
					System.out.println("RW End PC: "+inst.getProgramCounter()+"\nGlobal PC: "+containingProcessor.getRegisterFile().getProgramCounter());
					System.out.println("End instruction encounered");
					Simulator.setSimulationComplete(true);
					containingProcessor.getRegisterFile().setProgramCounter(inst.getProgramCounter()+1); // Bit of a hack but oh well
					containingProcessor.getConflictDetector().endProgram();
					break;
				default:
					containingProcessor.getRegisterFile().setValue(inst.getDestinationOperand().getValue(), aluResult);
					System.out.println("Written to register: "+inst.getDestinationOperand().getValue()+"\nValue: "+aluResult);
					break;
			}
			
			System.out.println("=========");
			// MA_RW_Latch.setRW_enable(false);
			if(inst.getOperationType()!=OperationType.end && !containingProcessor.getConflictDetector().isStalled()){
				IF_EnableLatch.setIF_enable(true);

			}
		}else if(MA_RW_Latch.isNop()){
			System.out.println("RW Stage, NOP detected");
			
		}
	}

}
