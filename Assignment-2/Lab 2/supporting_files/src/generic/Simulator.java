package generic;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import generic.Instruction.OperationType;
import generic.Operand.OperandType;


public class Simulator {
		
	static FileInputStream inputcodeStream = null;
	/*
		While we may only need 5 or 17 or 22 bits at a time to encode certain part of the instruction,
		however we only have access to 32 bit integers. To get the bits we require, we use masks

		To get the first n least significant bits

		number & ((1<<n)-1)
	*/
	private static int mask5 = (1<<5)-1;
	private static int mask17 = (1<<17)-1;
	private static int mask22 = (1<<22)-1;

	// Each type of instruction is encoded through bitwise operations into a 32 bit number
	private static int buildR3Instruction(int opCode, int src1, int src2, int rd){
		opCode&=mask5;
		src1&=mask5;
		src2&=mask5;
		rd&=mask5;
		int instruction = 0;
		instruction|=opCode;
		instruction<<=5;
		instruction|=src1;
		instruction<<=5;
		instruction|=src2;
		instruction<<=5;
		instruction|=rd;
		instruction<<=12;
		return instruction;
	}

	private static int buildR2IInstruction(int opCode, int src1, int rd, int imm){
		opCode&=mask5;
		src1&=mask5;
		rd&=mask5;
		imm&=mask17;
		int instruction = 0;
		instruction|=opCode;
		instruction<<=5;
		instruction|=src1;
		instruction<<=5;
		instruction|=rd;
		instruction<<=17;
		instruction|=imm;

		return instruction;
	}

	private static int buildRIInstruction(int opCode, int rd, int imm){
		opCode&=mask5;
		rd&=mask5;
		imm&=mask22;

		int instruction = 0;
		instruction|=opCode;
		instruction<<=5;
		instruction|=rd;
		instruction<<=22;
		instruction|=imm;
		return instruction;
	}
	
	public static void setupSimulation(String assemblyProgramFile, String objectProgramFile)
	{	
		int firstCodeAddress = ParsedProgram.parseDataSection(assemblyProgramFile);
		ParsedProgram.parseCodeSection(assemblyProgramFile, firstCodeAddress);
		ParsedProgram.printState();
	}
	
	public static void assemble(String outputFile)
	{
		//TODO your assembler code

		FileOutputStream output = null;
		try{
			// Load the file
			output = new FileOutputStream(outputFile);

			// Write the address of the first instruction
			byte[] header = ByteBuffer.allocate(4).putInt(ParsedProgram.firstCodeAddress).array();
			output.write(header);

			// Write the static data
			if(ParsedProgram.data.size()>0){
				for(Integer dataAddr : ParsedProgram.data){
					byte[] dataBuff = ByteBuffer.allocate(4).putInt(dataAddr).array();
					output.write(dataBuff);
				}
			}
			if(ParsedProgram.code.size()>0){
				for(Instruction inst : ParsedProgram.code){
					int instructionCode = 0;
					int src1 = inst.getSourceOperand1() == null ? 0 : inst.getSourceOperand1().getValue();
					int src2 = inst.getSourceOperand2()==null ? 0 : inst.getSourceOperand2().getValue();
					int rd = inst.getDestinationOperand() == null ? 0 : inst.getDestinationOperand().getValue();


					// If data is stored at a label, access it through the symbol table
					// Check if the register address provided is valid or not
					if(inst.getSourceOperand2()!=null){
						if(inst.getSourceOperand2().operandType==OperandType.Label){
							src2 = ParsedProgram.symtab.get(inst.getSourceOperand2().labelValue);
						}
						if(inst.getSourceOperand2().operandType==OperandType.Register){
							if(src2<0 || src2>31){
								throw new IllegalArgumentException("Invalid register address");
							}
						}
					}
					if(inst.getSourceOperand1()!=null){
						if((inst.getSourceOperand1().operandType==OperandType.Label)){
							src1 = ParsedProgram.symtab.get(inst.getSourceOperand1().labelValue);
						}
						if(inst.getSourceOperand1().operandType==OperandType.Register){
							if(src1<0 || src1>31){
								throw new IllegalArgumentException("Invalid register address");
							}
						}
					
					}
					if(inst.getDestinationOperand()!=null){
						if((inst.getDestinationOperand().operandType==OperandType.Label)){
							rd = ParsedProgram.symtab.get(inst.getDestinationOperand().labelValue);
						}
						if((inst.getDestinationOperand().operandType==OperandType.Register)){
							if(rd < 0 || rd >31){
								throw new IllegalArgumentException("Invalid register address");
							}
						}
					}
					

					/*
						Immediate is stored in source operand 2 for normal arithmetic instructions.
						For branching instructions, it is stored in destination operand. Also, the immediate value
						to be encoded for branching instructions need to calculated with respect to the Program Counter
					*/

					switch(inst.getOperationType()){
						case add: case sub: case mul: case div: case and: case or: case xor: case slt:
						case sll: case srl: case sra: 
							instructionCode = buildR3Instruction(inst.getOperationType().ordinal(), src1, src2, rd);
							break;
						case addi: case subi: case muli: case divi: case andi: case ori: case xori: case slti:
							case slli: case srli: case srai: case load: case store: 
							instructionCode = buildR2IInstruction(inst.getOperationType().ordinal(), src1, rd, src2);
							break;
						case beq: case bne: case blt: case bgt: 
							instructionCode = buildR2IInstruction(inst.getOperationType().ordinal(), src1, src2, rd-inst.getProgramCounter());
							break;
						case end:
							instructionCode = buildRIInstruction(inst.getOperationType().ordinal(), rd, src2);
							break;
						case jmp:
							instructionCode = buildRIInstruction(inst.getOperationType().ordinal(), src1, rd-inst.getProgramCounter());
							break;
						default:
							System.out.println(inst.toString());
							throw new IllegalArgumentException("Invalid instruction");
					}
					byte[] dataBuff = ByteBuffer.allocate(4).putInt(instructionCode).array();
					output.write(dataBuff);
				}
			}
			// Close the file
			output.close();

			
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
}

