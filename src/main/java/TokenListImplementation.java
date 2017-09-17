class TokenListImplementation implements TokenList{
	Token[] tokenArray;
	public int maxNumberOfTokens,
		numberOfTokens;
	
	TokenListImplementation(int listSize) {
		maxNumberOfTokens = listSize;
		numberOfTokens = 0;
		tokenArray = new Token[maxNumberOfTokens];
	}
	
	public void add(Token token) {
		tokenArray[numberOfTokens] = token;
		numberOfTokens +=1;
	}

	public void remove(int index) {
		for (int i = index; i < numberOfTokens - 1; i++) {
			tokenArray[i] = tokenArray[i + 1];
			tokenArray[numberOfTokens - 1] = null;
			numberOfTokens -=1;
		}
	}

	public void set(int index, Token token) {
		tokenArray[index] = token;		
	}

	public Token get(int index) {		
		return tokenArray[index];
	}

	public int size() {
		return numberOfTokens;
	}

}
