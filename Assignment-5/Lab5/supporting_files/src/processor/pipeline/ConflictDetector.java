package processor.pipeline;

import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand.OperandType;
import processor.Processor;

public class ConflictDetector {
    Processor containingProcessor;
    IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	MA_RW_LatchType MA_RW_Latch;

    public ConflictDetector(Processor processor, 
        IF_EnableLatchType IF_EnableLatch, IF_OF_LatchType IF_OF_Latch, 
        OF_EX_LatchType OF_EX_Latch, EX_MA_LatchType EX_MA_Latch, 
        EX_IF_LatchType EX_IF_Latch, MA_RW_LatchType MA_RW_Latch){
            this.containingProcessor = processor;
            this.IF_EnableLatch = IF_EnableLatch;
            this.IF_OF_Latch = IF_OF_Latch;
            this.OF_EX_Latch = OF_EX_Latch;
            this.EX_MA_Latch = EX_MA_Latch;
            this.EX_IF_Latch = EX_IF_Latch;
            this.MA_RW_Latch = MA_RW_Latch;
    }

    private boolean isBranchInstruction(String str){
        return str.equals("beq") || str.equals("bgt") || str.equals("blt") || str.equals("bne") || str.equals("jmp");
    }

    public boolean detectConflict(){
        Instruction of_inst = OF_EX_Latch.getInstruction();
        Instruction ex_inst = EX_MA_Latch.getInstruction();
        Instruction ma_inst = MA_RW_Latch.getInstruction();
        if(ex_inst==null && ma_inst==null) return false;
        
        String of_inst_name = of_inst.getOperationType().name();
        
        // Does not read from any register
        if(of_inst_name.equals("jmp") || of_inst_name.equals("end")) return false;

        String ex_inst_name = ex_inst!=null ? ex_inst.getOperationType().name() : "";
        String ma_inst_name = ma_inst!=null ? ma_inst.getOperationType().name() : "";
        

        // Does not write to any register
        if((ex_inst_name.equals("store") || ex_inst_name.equals("jmp") || ex_inst==null) && (ma_inst_name.equals("store") || ma_inst_name.equals("jmp") || ma_inst==null)) return false;


        // Branch instructions will not have register number in the destinations operand, it will be the label offset
        // Branch insturctions also won't write to any registers
        int rd1 = ex_inst!=null && ex_inst_name!="end" &&  !isBranchInstruction(ex_inst_name) && !EX_MA_Latch.isNop()? ex_inst.getDestinationOperand().getValue() : -1;
        int rd2 = ma_inst!=null && ma_inst_name!="end" && !isBranchInstruction(ma_inst_name) && !MA_RW_Latch.isNop()? ma_inst.getDestinationOperand().getValue() : -1;
        int rs1 = of_inst.getSourceOperand1().getValue();
        int rs2 = of_inst.getSourceOperand2().getOperandType()!=OperandType.Immediate ? of_inst.getSourceOperand2().getValue() : -1;

        System.out.println("1 = ex 2 = ma");
        System.out.println("(rd1!=-1 && rs1==rd1) "+(rd1!=-1 && rs1==rd1));
        System.out.println("(rd2!=-1 && rs1==rd2) "+(rd2!=-1 && rs1==rd2));
        System.out.println("(rd1!=-1 && rs2!=-1 && rs2==rd1) "+(rd1!=-1 && rs2!=-1 && rs2==rd1));
        System.out.println("(rd2!=-1 && rs2!=-1 && rs2==rd2) "+(rd2!=-1 && rs2!=-1 && rs2==rd2));

        if((rd1!=-1 && rs1==rd1) || (rd2!=-1 && rs1==rd2) || (rd1!=-1 && rs2!=-1 && rs2==rd1) || (rd2!=-1 && rs2!=-1 && rs2==rd2)) {
            
            return true;
        }
        

        return false;
    }
    public void raiseControlConflict(){
        // OF_EX_Latch.setInstruction(null);
        IF_OF_Latch.setNop(true);
    }

    public void setIFStage(boolean signal){
        IF_EnableLatch.setIF_enable(signal);
    }
    public boolean isStalled(){
        return OF_EX_Latch.isStalled();
    }
    public void endProgram(){
        IF_EnableLatch.setIF_enable(false);
        IF_OF_Latch.setOF_enable(false);
        OF_EX_Latch.setEX_enable(false);
        EX_MA_Latch.setMA_enable(false);
        MA_RW_Latch.setRW_enable(false);

        IF_OF_Latch.setNop(false);
        OF_EX_Latch.setNop(false);
        OF_EX_Latch.setStallSignal(false);
        EX_IF_Latch.setEX_IF_enable(false);
        EX_MA_Latch.setNop(false);
        MA_RW_Latch.setNop(false);
        IF_EnableLatch.setIFBusy(false);
        IF_OF_Latch.setOF_busy(false);
        OF_EX_Latch.setEX_busy(false);
        EX_MA_Latch.setMA_busy(false);
    }

}
