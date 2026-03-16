package processor.pipeline;

import generic.Instruction;

public class EX_MA_LatchType {
	Instruction inst;
	int aluResult;
	boolean MA_enable;
	boolean isNop;
	boolean isMA_busy;
	
	public EX_MA_LatchType()
	{
		MA_enable = false;
		isNop = false;
		isMA_busy = false;
	}

	public boolean isMA_enable() {
		return MA_enable;
	}

	public void setMA_enable(boolean mA_enable) {
		MA_enable = mA_enable;
	}
	public Instruction getInstruction() {
		return inst;
	}
	public void setInstruction(Instruction inst) {
		this.inst = inst;
	}
	public void setAluResult(int aluResult) {
		this.aluResult = aluResult;
	}
	public int getAluResult() {
		return aluResult;
	}
	public void setNop(boolean status){
		this.isNop = status;
	}
	public boolean isNop(){
		return isNop;
	}
	public boolean isMA_busy(){
		return isMA_busy;
	}
	public void setMA_busy(boolean signal){
		this.isMA_busy = signal;
	}

}
