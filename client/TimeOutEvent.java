package client;

import java.util.TimerTask;

public class TimeOutEvent extends TimerTask {
	public iCallback callback;
	Object attachment = null;
	
	public TimeOutEvent(iCallback callback) {
		this.callback = callback;
	}
	
	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}
	
	public void run() {
		synchronized (attachment) {
			callback.call();
		}
	}
}
