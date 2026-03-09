package processor.pipeline;

import generic.Instruction;

public class OF_EX_LatchType {
	
	boolean EX_enable;
	Instruction inst;
	boolean isStalled;
	boolean isNop;

	
	public OF_EX_LatchType()
	{
		EX_enable = false;
		isStalled = false;
		isNop = false;
	}

	public boolean isEX_enable() {
		return EX_enable;
	}

	public void setEX_enable(boolean eX_enable) {
		EX_enable = eX_enable;
	}

	public Instruction getInstruction(){
		return inst;
	}
	public void setInstruction(Instruction in){
		this.inst = in;
	}

	public boolean isStalled(){
		return isStalled;
	}
	public void setStallSignal(boolean signal){
		this.isStalled = signal;
	}

	public void setNop(boolean status){
		this.isNop = status;
	}

	public boolean isNop(){
		return isNop;
	}

}
