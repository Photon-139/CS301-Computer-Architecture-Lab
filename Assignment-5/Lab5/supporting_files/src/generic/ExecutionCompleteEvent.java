package generic;

public class ExecutionCompleteEvent extends Event {
	
	int aluResult;
	Instruction instruction;
	boolean isBranchTaken;
	
	public ExecutionCompleteEvent(long eventTime, Element requestingElement, Element processingElement, int aluResult, Instruction instruction, boolean isBranchTaken) {
		super(eventTime, EventType.ExecutionComplete, requestingElement, processingElement);
		this.aluResult = aluResult;
		this.instruction = instruction;
		this.isBranchTaken = isBranchTaken;
	}

	public int getAluResult() {
		return aluResult;
	}

	public void setAluResult(int aluResult) {
		this.aluResult = aluResult;
	}

	public Instruction getInstruction() {
		return instruction;
	}

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}

	public boolean isBranchTaken() {
		return isBranchTaken;
	}

	public void setBranchTaken(boolean isBranchTaken) {
		this.isBranchTaken = isBranchTaken;
	}

}
