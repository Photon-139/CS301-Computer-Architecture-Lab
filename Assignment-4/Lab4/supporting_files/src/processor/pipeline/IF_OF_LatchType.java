package processor.pipeline;

public class IF_OF_LatchType {
	
	boolean OF_enable;
	int instruction;
	boolean isNop;
	int instructionPC;
	
	public IF_OF_LatchType()
	{
		OF_enable = false;
	}

	public boolean isOF_enable() {
		return OF_enable;
	}

	public void setOF_enable(boolean oF_enable) {
		OF_enable = oF_enable;
	}

	public int getInstruction() {
		return instruction;
	}

	public void setInstruction(int instruction) {
		this.instruction = instruction;
	}
	public void setNop(boolean status){
		isNop = status;
	}
	public boolean isNop(){
		return isNop;
	}
	public int getInstructionPC() {
		return instructionPC;
	}
	public void setInstructionPC(int instructionPC) {
		this.instructionPC = instructionPC;
	}

}
