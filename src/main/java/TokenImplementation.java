class TokenImplementation implements Token{
	String value;
	int type,
		precedence;

	TokenImplementation(String string, int type, int precedence) {
		value = string;
		this.type = type;
		this.precedence = precedence;
	}
	
	public String getValue() {
		return value;
	}

	public int getType() {
		return type;
	}

	public int getPrecedence() {
		return precedence;
	}

}
