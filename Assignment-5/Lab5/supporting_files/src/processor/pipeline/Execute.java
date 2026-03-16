package processor.pipeline;

import generic.Instruction;
import generic.Statistics;
import generic.Element;
import generic.Event;
import generic.ExecutionCompleteEvent;
import generic.Simulator;
import processor.Clock;
import processor.Processor;

public class Execute implements Element {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	
	boolean isALUBusy;
	
	public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch, EX_IF_LatchType eX_IF_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
	}
	
	public void performEX()
	{

		if(EX_MA_Latch.isMA_busy() || isALUBusy){
			System.out.println("\n("+Clock.getCurrentTime()+") "+"MA Stage busy or EX busy, setting EX stage as busy\n");
			
			OF_EX_Latch.setEX_busy(true);
			return;
		} else {
			OF_EX_Latch.setEX_busy(false);
		}
		//TODO
		boolean isBranchTaken = false; // Predict not taken
		if(OF_EX_Latch.isEX_enable() && !OF_EX_Latch.isNop()){
			Instruction inst = OF_EX_Latch.getInstruction();
			int currentPC = inst.getProgramCounter();
			int aluResult = -1;
			int rs1 = -1, rs2 = -1, rd = -1;
			System.out.println("========\nEX Stage\nOperation: "+inst.getOperationType().toString());
			switch(inst.getOperationType()){
				case add:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
					aluResult = rs1 + rs2;
					break;
				case addi:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = inst.getSourceOperand2().getValue();
					aluResult = rs1 + rs2;
					break;
				case sub:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
					aluResult = rs1 - rs2;
					break;
				case subi:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = inst.getSourceOperand2().getValue();
					aluResult = rs1 - rs2;
					break;
				case mul:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
					aluResult = rs1 * rs2;
					break;
				case muli:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = inst.getSourceOperand2().getValue();
					aluResult = rs1 * rs2;
					break;
				case div:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
					aluResult = rs1 / rs2;
					containingProcessor.getRegisterFile().setValue(31, rs1%rs2);;
					break;
				case divi:
					System.out.println("Divi!!!!!");
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = inst.getSourceOperand2().getValue();
					aluResult = rs1 / rs2;
					containingProcessor.getRegisterFile().setValue(31, rs1%rs2);;
					break;
				case and:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
					aluResult = rs1 & rs2;
					break;
				case andi:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = inst.getSourceOperand2().getValue();
					aluResult = rs1 & rs2;
					break;
				case or:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
					aluResult = rs1 | rs2;
					break;
				case ori:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = inst.getSourceOperand2().getValue();
					aluResult = rs1 | rs2;
					break;
				case xor:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
					aluResult = rs1 ^ rs2;
					break;
				case xori:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = inst.getSourceOperand2().getValue();
					aluResult = rs1 ^ rs2;
					break;
				case slt:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
					aluResult = rs1 < rs2 ? 1 : 0;
					break;
				case slti:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = inst.getSourceOperand2().getValue();
					aluResult = rs1 < rs2 ? 1 : 0;
					break;
				case sll:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
					aluResult = rs1 << rs2;
					break;
				case slli:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = inst.getSourceOperand2().getValue();
					aluResult = rs1 << rs2;
					break;
				case srl:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
					aluResult = rs1 >>> rs2;
					break;
				case srli:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = inst.getSourceOperand2().getValue();
					aluResult = rs1 >>> rs2;
					break;
				case sra:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
					aluResult = rs1 >> rs2;
					break;
				case srai:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = inst.getSourceOperand2().getValue();
					aluResult = rs1 >> rs2;
					break;
				case load:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = inst.getSourceOperand2().getValue();
					aluResult = rs1+rs2;
					break;
				case store:
					rd = containingProcessor.getRegisterFile().getValue(inst.getDestinationOperand().getValue());
					rs2 = inst.getSourceOperand2().getValue();
					aluResult = rd+rs2;
					break;
				case jmp:
					rd = inst.getDestinationOperand().getValue();
					aluResult = currentPC + rd;
					EX_IF_Latch.setEX_IF_enable(true, aluResult);
					isBranchTaken = true;
					break;
				case beq:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
					rd = inst.getDestinationOperand().getValue();
					if(rs1==rs2){
						aluResult = currentPC + rd;
						EX_IF_Latch.setEX_IF_enable(true, aluResult);
						isBranchTaken = true;
					}
					break;
				case bne:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
					rd = inst.getDestinationOperand().getValue();
					if(rs1!=rs2){
						aluResult = currentPC + rd;
						EX_IF_Latch.setEX_IF_enable(true, aluResult);
						isBranchTaken = true;

					}
					break;
				case blt:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
					rd = inst.getDestinationOperand().getValue();
					if(rs1<rs2){
						aluResult = currentPC + rd;
						System.out.println("Blt result: "+aluResult);
						EX_IF_Latch.setEX_IF_enable(true, aluResult);
						isBranchTaken = true;
						System.out.println("Blt, PC = "+currentPC+" rd = "+rd);
					}
					break;
				case bgt:
					rs1 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					rs2 = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue());
					rd = inst.getDestinationOperand().getValue();
					if(rs1>rs2){
						aluResult = currentPC + rd;
						EX_IF_Latch.setEX_IF_enable(true, aluResult);
						isBranchTaken = true;

					}
					break;
				case end:
					break;
			}
			System.out.println("ALU Result: "+aluResult);
			System.out.println("rs="+rs1+"\nrs2="+rs2+"\nrd="+rd);
			System.out.println("=======");
			if(isBranchTaken){
				containingProcessor.getConflictDetector().raiseControlConflict();
				Statistics.setWrongPath_counter(Statistics.getWrongPath_counter()+1);
			}
			
			int latency = configuration.Configuration.ALU_latency;
			switch(inst.getOperationType()) {
				case mul: case muli:
					latency = configuration.Configuration.multiplier_latency;
					break;
				case div: case divi:
					latency = configuration.Configuration.divider_latency;
					break;
				default:
					break;
			}
			
			System.out.println("Scheduling ExecutionCompleteEvent with latency: " + latency);
			Simulator.getEventQueue().addEvent(new ExecutionCompleteEvent(
				Clock.getCurrentTime() + latency - 1,
				this,
				this,
				aluResult,
				inst,
				isBranchTaken
			));
			
			isALUBusy = true;
			OF_EX_Latch.setEX_busy(true);
			EX_MA_Latch.setNop(true);
			OF_EX_Latch.setEX_enable(false);
			
		}else if(OF_EX_Latch.isNop()){
			System.out.println("EX Stage, NOP detected");
			EX_MA_Latch.setNop(true);
			OF_EX_Latch.setEX_busy(false);
		}

	}

	@Override
	public void handleEvent(Event e) {
		if (e.getEventType() == Event.EventType.ExecutionComplete) {
			ExecutionCompleteEvent exeEvent = (ExecutionCompleteEvent) e;
			
			System.out.println("\nEX event handler\n" + Clock.getCurrentTime());
			System.out.println("EX-MA Latch enabled");
			EX_MA_Latch.setMA_enable(true);
			EX_MA_Latch.setAluResult(exeEvent.getAluResult());
			EX_MA_Latch.setInstruction(exeEvent.getInstruction());
			EX_MA_Latch.setNop(false);
			
			isALUBusy = false;
			OF_EX_Latch.setEX_busy(false);
		}
	}

}
