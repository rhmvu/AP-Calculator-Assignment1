class DoubleStackImplementation implements DoubleStack{
	Double[] doubleArray;
	public int maxNumberOfElements,
		numberOfElements;
	
	DoubleStackImplementation(int number) {
		maxNumberOfElements = number;
		doubleArray = new Double[maxNumberOfElements];
		numberOfElements = 0;
	}
	
	@Override
	public void push(Double element) {
		doubleArray[numberOfElements] = element;
		numberOfElements +=1;
	}
	
	@Override
	public Double pop() {
		double result = doubleArray[numberOfElements - 1];
		doubleArray[numberOfElements - 1] = null;
		numberOfElements -=1;
		
		return result;
	}
	
	@Override
	public Double top() {
		return doubleArray[numberOfElements - 1];
	}
	
	@Override
	public int size() {
		return numberOfElements;
	}
}
