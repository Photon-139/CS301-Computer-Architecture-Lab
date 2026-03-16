package processor.pipeline;

import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand.OperandType;
import generic.Operand;
import generic.Statistics;
import processor.Clock;
import processor.Processor;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	OperationType[] operations = OperationType.values();
	
	public OperandFetch(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
	}

	
	
	public void performOF()
	{
		if(OF_EX_Latch.isEX_busy()){
			System.out.println("\n("+Clock.getCurrentTime()+") "+"EX stage busy, setting OF stage busy\n");
			IF_OF_Latch.setOF_busy(true);
			return;
		} else {
			IF_OF_Latch.setOF_busy(false);
		}

		if(OF_EX_Latch.isStalled()){
			boolean stallStatus = containingProcessor.getConflictDetector().detectConflict();

			if(!stallStatus){
				OF_EX_Latch.setStallSignal(false);
				containingProcessor.getConflictDetector().setIFStage(true);
				OF_EX_Latch.setNop(false);
				System.out.println("OF Stage, conflict from previous cycles resolved");
			}else{
				OF_EX_Latch.setNop(true);
				System.out.println("OF Stage, Conflict from previous stages not resolved");
				Statistics.setOF_stallCounter(Statistics.getOF_stallCounter()+1);
			}
			return;
		}
		if(IF_OF_Latch.isOF_enable() && !IF_OF_Latch.isNop())
		{
			//TODO

			int inst = IF_OF_Latch.getInstruction();
			int opcode = (inst >> 27) & (0b11111);
			OperationType operation = operations[opcode];
			Operand rs1 = new Operand();
			Operand rs2 = new Operand();
			Operand rd = new Operand();
			Operand imm = new Operand();
			Instruction instruction = new Instruction();
			rs1.setOperandType(OperandType.Register);
			rs2.setOperandType(OperandType.Register);
			rd.setOperandType(OperandType.Register);
			imm.setOperandType(OperandType.Immediate);
			instruction.setOperationType(operation);
			
			int immediate;
			System.out.println("=======\nOF Stage\nOperationType="+operation.toString());
			switch (operation) {
				case add: case sub: case mul: case div: case and: case or: case xor: case slt: case sll: case srl: case sra:
					rs1.setValue((inst>>22) & (0b11111));
					rs2.setValue((inst>>17) & (0b11111));
					rd.setValue((inst>>12) & (0b11111));
					instruction.setSourceOperand1(rs1);
					instruction.setSourceOperand2(rs2);
					instruction.setDestinationOperand(rd);
					System.out.println("rs1="+rs1.getValue()+"\nrs2="+rs2.getValue()+"\nrd="+rd.getValue());
					break;
				case addi: case subi: case muli: case divi: case andi: case ori: case xori: case slti: case slli: case srli: case srai:
					case load: case store:
					rs1.setValue((inst>>22) & (0b11111));
					rd.setValue((inst>>17) & (0b11111));
					immediate = inst & ((1<<17)-1);
					immediate = (immediate << 15)>>15;
					imm.setValue(immediate);
					instruction.setSourceOperand1(rs1);
					instruction.setSourceOperand2(imm);
					instruction.setDestinationOperand(rd);
					System.out.println("rs1="+instruction.getSourceOperand1().getValue()+"\nimm(rs2)="+instruction.getSourceOperand2().getValue()+"\nrd="+instruction.getDestinationOperand().getValue());
					break;
				case beq: case bne: case blt: case bgt:
	
					rs1.setValue((inst>>22) & (0b11111));
					
					rd.setValue((inst>>17) & (0b11111));
					
					immediate = inst & ((1<<17)-1);
					immediate = (immediate << 15)>>15;
					imm.setValue(immediate);
					instruction.setSourceOperand1(rs1);
					instruction.setSourceOperand2(rd);
					instruction.setDestinationOperand(imm);
					System.out.println("rs1="+instruction.getSourceOperand1().getValue()+"\nrs2="+instruction.getSourceOperand2().getValue()+"\nrd(imm)="+instruction.getDestinationOperand().getValue());
					break;
				case jmp:
					/*Look into this if something breaks
						Perhaps look if adding a distinction between registers and immediate
						makes a difference
					*/
					immediate = inst & ((1<<22)-1);
					immediate = (immediate << 10) >> 10;
					imm.setValue(immediate);
					System.out.println("Instruction: "+inst+"\nImm: "+imm.getValue());
					instruction.setDestinationOperand(imm);
					break;
				// case end:
				// 	break;
				default:
					break;
			}
	
			System.out.println("========");



			instruction.setProgramCounter(IF_OF_Latch.getInstructionPC());
			OF_EX_Latch.setInstruction(instruction);

			boolean isThereConflict = containingProcessor.getConflictDetector().detectConflict();

			if(isThereConflict){
				OF_EX_Latch.setStallSignal(true);
				containingProcessor.getConflictDetector().setIFStage(false);
				OF_EX_Latch.setNop(true);
				System.out.println("OF Stage, conflict detected after processing instruction");
				Statistics.setOF_stallCounter(Statistics.getOF_stallCounter()+1);

			}else{
				OF_EX_Latch.setNop(false);
			}

			System.out.println("OF-EX latch enbaled");
			OF_EX_Latch.setEX_enable(true);
			
			IF_OF_Latch.setOF_busy(false);
			IF_OF_Latch.setOF_enable(false);

			

		}else if(IF_OF_Latch.isNop()){
			System.out.println("OF Stage, NOP detected");
			OF_EX_Latch.setNop(true);
			IF_OF_Latch.setOF_busy(false);
		}
	}

}
