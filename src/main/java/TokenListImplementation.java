class TokenListImplementation implements TokenList{
	Token[] tokenArray;
	public int maxNumberOfTokens,
		numberOfTokens;
	
	TokenListImplementation(int listSize) {
		maxNumberOfTokens = listSize;
		numberOfTokens = 0;
		tokenArray = new Token[maxNumberOfTokens];
	}
	
	@Override
	public void add(Token token) {
		tokenArray[numberOfTokens] = token;
		numberOfTokens +=1;
	}
	
	@Override
	public void remove(int index) {
		for (int i = index; i < numberOfTokens - 1; i++) {
			tokenArray[i] = tokenArray[i + 1];
			tokenArray[numberOfTokens - 1] = null;
			numberOfTokens -=1;
		}
	}
	
	@Override
	public void set(int index, Token token) {
		tokenArray[index] = token;		
	}
	
	@Override
	public Token get(int index) {		
		return tokenArray[index];
	}
	
	@Override
	public int size() {
		return numberOfTokens;
	}

}
