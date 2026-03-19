package processor.pipeline;

public class IF_EnableLatchType {
	
	boolean IF_enable;
	boolean isIFBusy;
	
	public IF_EnableLatchType()
	{
		IF_enable = true;
		isIFBusy = false;
	}

	public boolean isIF_enable() {
		return IF_enable;
	}

	public void setIF_enable(boolean iF_enable) {
		IF_enable = iF_enable;
	}
	public boolean isIFBusy(){
		return isIFBusy;
	}
	public void setIFBusy(boolean signal){
		this.isIFBusy = signal;
	}

}
