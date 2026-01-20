package generic;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

import generic.Operand.OperandType;


public class Simulator {
		
	static FileInputStream inputcodeStream = null;
	
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
			output = new FileOutputStream(outputFile);
			byte[] header = ByteBuffer.allocate(4).putInt(ParsedProgram.firstCodeAddress).array();
			output.write(header);

			
			if(ParsedProgram.data.size()>0){
				for(Integer dataAddr : ParsedProgram.data){
					byte[] dataBuff = ByteBuffer.allocate(4).putInt(dataAddr).array();
					output.write(dataBuff);
				}
			}
			if(ParsedProgram.code.size()>0){
				for(Instruction inst : ParsedProgram.code){
					System.out.println(inst.toString());
					
				}
			}

			
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
}

