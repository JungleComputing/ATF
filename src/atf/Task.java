package atf;

import java.io.Serializable;
import java.util.Set;


public abstract class Task implements Serializable, Comparable<Task> {

	private static long nextID = 0;

	private static synchronized long nextID() {
		return nextID++;
	}

	public final long ID;	

	protected Task() {
		this.ID = nextID();		
	}
	
	
}
