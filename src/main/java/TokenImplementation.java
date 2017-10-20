class TokenImplementation implements Token{
	String value;
	int type,
		precedence;

	TokenImplementation(String string, int type, int precedence) {
		value = string;
		this.type = type;
		this.precedence = precedence;
	}
	
	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public int getType() {
		return type;
	}
	
	@Override
	public int getPrecedence() {
		return precedence;
	}

}
