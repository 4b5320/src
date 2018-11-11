
public class myMessage implements java.io.Serializable{
	private int type = -1;
	private Object message = null;
	
	public myMessage(int type, Object message) {
		this.type = type;
		this.message = message;
	}
	
	protected boolean isType(int type) {
		return this.type == type;
	}
	
	protected Object getMessage() {
		return message;
	}
	
	/* OBJECT TYPES
	 * 1 - Boolean. A prompt that this player is ready to continue
	 * 2 - Integer. Roles
	 * 3 - String.Player Name
	 * 4 - String. Player Role
	 * 5 - String. Message of the Courtroom
	 * 6 - String. Message of the Jury
	 * 
	 * 		1 - Judge is selected				| -1 Judge is unselected
	 * 		2 - Defense Attorney is selected	| -2 Defense Attorney is unselected
	 * 		3 - Prosecutor is selected			| -3 Prosecutor is unselected
	 * 		4 - Jury is selected				| -4 Jury is unselected
	 * */
}

