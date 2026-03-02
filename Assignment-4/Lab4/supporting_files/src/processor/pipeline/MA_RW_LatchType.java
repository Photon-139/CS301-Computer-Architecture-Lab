package processor.pipeline;

import generic.Instruction;

public class MA_RW_LatchType {
	
	boolean RW_enable;
	int aluResult;
	Instruction inst;
	boolean isNop;
	
	public MA_RW_LatchType()
	{
		RW_enable = false;
		isNop = false;
	}

	public boolean isRW_enable() {
		return RW_enable;
	}

	public void setRW_enable(boolean rW_enable) {
		RW_enable = rW_enable;
	}
	public int getAluResult() {
		return aluResult;
	}
	public void setAluResult(int aluResult) {
		this.aluResult = aluResult;
	}
	public Instruction getInstruction() {
		return inst;
	}
	public void setInstruction(Instruction inst) {
		this.inst = inst;
	}
	public boolean isNop(){
		return isNop;
	}
	public void setNop(boolean status){
		this.isNop = status;
	}
}
