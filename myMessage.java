
public class myMessage implements java.io.Serializable{
	private int type = -1;
	private Object message = null;
	private String role = null;
	private String src = null;
	
	public myMessage(int type, Object message) {
		this.type = type;
		this.message = message;
	}
	
	public myMessage(int type, Object message, String role, String src) {
		this.type = type;
		this.message = message;
		this.role = role;
		System.out.println("Passed role: " + role);
		System.out.println("Saved role: " + this.role);
		this.src = src;
	}
	
	public myMessage(int type) {
		this.type = type;
	}
	
	protected boolean isType(int type) {
		return this.type == type;
	}
	
	protected Object getMessage() {
		return message;
	}
	
	protected String getSource() {
		return src;
	}
	
	protected String getRoleOfSource() {
		System.out.println("Object returns role " + this.role);
		return this.role;
	}
	
	/* OBJECT TYPES
	 * 0 - LinkedList. List of IP adresses
	 * 1 - Boolean. A prompt that this player is ready to continue
	 * 2 - Integer. Roles
	 * 
	 * 		1 - Judge is selected				| -1 Judge is unselected
	 * 		2 - Defense Attorney is selected	| -2 Defense Attorney is unselected
	 * 		3 - Prosecutor is selected			| -3 Prosecutor is unselected
	 * 		4 - Jury is selected				| -4 Jury is unselected
	 * 3 - String. Message
	 * 4 - String. Who is allowed to talk. Prosecutor or Defense Lawyer?
	 * 5 - Call for partial verdict
	 * 6 - Objection
	 * 7 - Ask to leave the courtroom
	 * 8 - String. GUILTY OR NOT GUILTY. Jury's vote for guilty or not guilty
	 * 9 - A prompt saying the sender is a jury
	 * 10 - timeout
	 * 11 - objection or order
	 * */
}

