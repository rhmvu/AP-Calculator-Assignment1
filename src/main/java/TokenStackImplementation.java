class TokenStackImplementation implements TokenStack{
	Token[] tokenArray;
	int maxNumberOfTokens,
		numberOfTokens;
	
	TokenStackImplementation(int maxNumberOfTokens) {
		this.maxNumberOfTokens = maxNumberOfTokens;
		numberOfTokens = 0;
		tokenArray = new Token[this.maxNumberOfTokens];
	}

	public void push(Token token) {
		tokenArray[numberOfTokens] = token;
		numberOfTokens +=1;
	}

	public Token pop() {
		Token result = tokenArray[numberOfTokens - 1];
		tokenArray[numberOfTokens - 1] = null;
		numberOfTokens -=1;
		return result;
	}

	public Token top() {
		return tokenArray[numberOfTokens - 1];
	}

	public int size() {
		return numberOfTokens;
	}

}
