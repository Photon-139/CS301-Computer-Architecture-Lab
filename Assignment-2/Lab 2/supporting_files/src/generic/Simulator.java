package generic;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


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
					if(inst.getSourceOperand2()!=null && (inst.getSourceOperand2().operandType==OperandType.Label)){
						src2 = ParsedProgram.symtab.get(inst.getSourceOperand2().labelValue);
					}
					if(inst.getSourceOperand1()!=null && (inst.getSourceOperand1().operandType==OperandType.Label)){
						src1 = ParsedProgram.symtab.get(inst.getSourceOperand1().labelValue);
					
					}
					if(inst.getDestinationOperand()!=null && (inst.getDestinationOperand().operandType==OperandType.Label)){
						rd = ParsedProgram.symtab.get(inst.getDestinationOperand().labelValue);
					}

					/*
						Immediate is stored in source operand 2 for normal arithmetic instructions.
						For branching instructions, it is stored in destination operand. Also, the immediate value
						to be encoded for branching instructions need to calculated with respect to the Program Counter
					*/

					switch(inst.getOperationType()){
						case add:
							instructionCode = buildR3Instruction(0b00000, src1, src2, rd);
							break;
						case addi:
							instructionCode = buildR2IInstruction(0b00001, src1, rd, src2);
							break;
						case sub:
							instructionCode = buildR3Instruction(0b00010, src1, src2, rd);
							break;
						case subi:
							instructionCode = buildR2IInstruction(0b00011, src1, rd, src2);
							break;
						case mul:
							instructionCode = buildR3Instruction(0b00100, src1, src2, rd);
							break;
						case muli:
							instructionCode = buildR2IInstruction(0b00101, src1, rd, src2);
							break;
						case div:
							instructionCode = buildR3Instruction(0b00110, src1, src2, rd);
							break;
						case divi:
							instructionCode = buildR2IInstruction(0b00111, src1, rd, src2);
							break;
						case and:
							instructionCode = buildR3Instruction(0b01000, src1, src2, rd);
							break;
						case andi:
							instructionCode = buildR2IInstruction(0b01001, src1, rd, src2);
							break;
						case or:
							instructionCode = buildR3Instruction(0b01010, src1, src2, rd);
							break;
						case ori:
							instructionCode = buildR2IInstruction(0b01011, src1, rd, src2);
							break;
						case xor:
							instructionCode = buildR3Instruction(0b01100, src1, src2, rd);
							break;
						case xori:
							instructionCode = buildR2IInstruction(0b01101, src1, rd, src2);
							break;
						case slt:
							instructionCode = buildR3Instruction(0b01110, src1, src2, rd);
							break;
						case slti:
							instructionCode = buildR2IInstruction(0b01111, src1, rd, src2);
							break;
						case sll:
							instructionCode = buildR3Instruction(0b10000, src1, src2, rd);
							break;
						case slli:
							instructionCode = buildR2IInstruction(0b10001, src1, rd, src2);
							break;
						case srl:
							instructionCode = buildR3Instruction(0b10010, src1, src2, rd);
							break;
						case srli:
							instructionCode = buildR2IInstruction(0b10011, src1, rd, src2);
							break;
						case sra:
							instructionCode = buildR3Instruction(0b10100, src1, src2, rd);
							break;
						case srai:
							instructionCode = buildR2IInstruction(0b10101, src1, rd, src2);
							break;
						case load:
							instructionCode = buildR2IInstruction(0b10110, src1, rd, src2);
							break;
						case store:
							instructionCode = buildR2IInstruction(0b10111, src1, rd, src2);
							break;
						case jmp:
							instructionCode = buildRIInstruction(0b11000, src1, rd-inst.getProgramCounter());
							break;
						case beq:
							instructionCode = buildR2IInstruction(0b11001, src1, src2, rd-inst.getProgramCounter());
							break;
						case bne:
							instructionCode = buildR2IInstruction(0b11010, src1, src2, rd-inst.getProgramCounter());
							break;
						case blt:
							instructionCode = buildR2IInstruction(0b11011, src1, src2, rd-inst.getProgramCounter());
							break;
						case bgt:
							instructionCode = buildR2IInstruction(0b11100, src1, src2, rd-inst.getProgramCounter());
							break;
						case end:
							instructionCode = buildRIInstruction(0b11101, rd, src2);
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

