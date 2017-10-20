import java.io.PrintStream;
import java.util.Scanner;

class Main implements CalculatorInterface {

	private static final String OPEN_PARENTHESIS = "(",
			CLOSE_PARENTHESIS = ")",
			ADDITION = "+",
			SUBSTRACTION = "-",
			MULTIPLICATION = "*",
			DIVISION = "/", POWER = "^",
			ADDITION_SUBSTRACTION = "+-",
			ADDITION_ADDITION= "++",
			SUBSTRACTION_ADDITION = "-+",
			SUBSTRACTION_SUBSTRACTION = "--",
			ERROR_MESSAGE = "Invalid token in the arithmetic expression. Please fix your input.",
			ERROR_MESSAGE_2 = "Mismatched parenthesis detected. Please check your input.",
			ERROR_MESSAGE_3 = "Invalid input, remaining numbers on stack. Please fix your input.",
			ERROR_MESSAGE_4 = "Invalid input, operation misses an operand for operation: ",
			ERROR_MESSAGE_5 = "Input results a calculation this calculator's power function can not handle, please fix your operands";
	private static final int NON_OPERATOR_PRECEDENCE = -1,
			HIGH_PRECEDENCE = 3,
			MEDIUM_PRECEDENCE = 2,
			LOW_PRECEDENCE = 1;

	PrintStream out;

	Main(){
		out = new PrintStream(System.out);
	}

	private double power(double base, double power){
		if (power == 0) {
			return 1;
		}

		return base * power(base, --power);
	}

	private int setPrecedence(String token) {
		int result = 0;

		if (token.equals(ADDITION) || token.equals(SUBSTRACTION)) {
			result = LOW_PRECEDENCE;
		} else if (token.equals(MULTIPLICATION) || token.equals(DIVISION)) {
			result = MEDIUM_PRECEDENCE;
		} else {
			result = HIGH_PRECEDENCE;
		}

		return result;
	}

	private boolean isDouble(String token) {
		Scanner doubleScanner = new Scanner(token);
		boolean tokenIsDouble = doubleScanner.hasNextDouble();
		doubleScanner.close();

		return tokenIsDouble;
	}

	private boolean isOperator(String token) {
		return token.equals(ADDITION) ||
				token.equals(SUBSTRACTION) ||
				token.equals(MULTIPLICATION) ||
				token.equals(DIVISION) ||
				token.equals(POWER);
	}

	private boolean isParenthesis(String token) {
		return (token.equals(OPEN_PARENTHESIS) || token.equals(CLOSE_PARENTHESIS));
	}

	private Token configureToken(String token) throws APException {
		Token result = null;
		token = replaceDoubleOperators(token);

		if (isDouble(token)) {
			result = new TokenImplementation(token, Token.NUMBER_TYPE, NON_OPERATOR_PRECEDENCE);
		} else if (isOperator(token)) {
			int precedence = setPrecedence(token);
			result = new TokenImplementation(token, Token.OPERATOR_TYPE, precedence);
		} else if (isParenthesis(token)) {
			result = new TokenImplementation(token, Token.PARENTHESIS_TYPE, NON_OPERATOR_PRECEDENCE);
		} else {
			throw new APException(ERROR_MESSAGE);
		}

		return result;
	}

	@Override
	public TokenList readTokens(String input) throws APException {
		String[] tokenArray = input.split("\\s+"); // splits string in space and creates an array with  only the tokens
		TokenList result = new TokenListImplementation(tokenArray.length);

		for (int i = 0; i < tokenArray.length; i++) {
			Token token = configureToken(tokenArray[i]);
			result.add(token);
		}

		return result;
	}

	private double performOperation(double operand1, double operand2, String operation) throws APException {
		double result = 0;

		switch (operation) {
			case ADDITION:
				result = operand1 + operand2;
				break;
			case SUBSTRACTION:
				result = operand2 - operand1;
				break;
			case MULTIPLICATION:
				result = operand1 * operand2;
				break;
			case DIVISION:
				result = operand2 / operand1;
				break;
			case POWER:
				try{
					result = power(operand2, operand1);
				}catch(StackOverflowError e){
					throw new APException(ERROR_MESSAGE_5);
				}
				break;
		}

		return result;
	}

	private String replaceDoubleOperators(String operation) {

		if (operation.equals(ADDITION_SUBSTRACTION) || operation.equals(SUBSTRACTION_ADDITION)){
			operation = SUBSTRACTION;
		} else if (operation.equals(SUBSTRACTION_SUBSTRACTION) || operation.equals(ADDITION_ADDITION)){
			operation = ADDITION;
		}

		return operation;
	}

	@Override
	public Double rpn(TokenList tokens) throws APException {
		DoubleStack stack = new DoubleStackImplementation(tokens.size());
		Token token;

		for (int i = 0; i < tokens.size(); i++) {
			token = tokens.get(i);
			int type = token.getType();

			if (type == Token.NUMBER_TYPE) {
				stack.push(Double.parseDouble(token.getValue()));
			} else if (type == Token.OPERATOR_TYPE) {

				try {
					double operand1 = stack.pop();
					double operand2 = stack.pop();
					double operationResult = performOperation(operand1, operand2, token.getValue());
					stack.push(operationResult);

				} catch (ArrayIndexOutOfBoundsException e) {
					throw new APException(ERROR_MESSAGE_4 + token.getValue());
				}
			}
		}

		if (stack.size() != 1) {
			throw new APException(ERROR_MESSAGE_3);
		}

		return stack.top();
	}

	@Override
	public TokenList shuntingYard(TokenList tokens) throws APException {
		TokenList result = new TokenListImplementation(tokens.size());
		TokenStack operatorStack = new TokenStackImplementation(tokens.size());
		Token token;

		for (int i = 0; i < tokens.size(); i++) {
			token = tokens.get(i);
			int type = token.getType();
			int precedence = token.getPrecedence();
			String value = token.getValue();

			if (type == Token.NUMBER_TYPE) {
				result.add(token);
			} else if (type == Token.OPERATOR_TYPE) {

				while (operatorStack.size() > 0 && precedence <= operatorStack.top().getPrecedence()) {
					result.add(operatorStack.pop());
				}
				operatorStack.push(token);
			} else if (value.equals(OPEN_PARENTHESIS)) {
				operatorStack.push(token);
			} else if (value.equals(CLOSE_PARENTHESIS)) {

				while (!operatorStack.top().getValue().equals(OPEN_PARENTHESIS)) {
					result.add(operatorStack.pop());
				}

				if (operatorStack.top().getValue().equals(OPEN_PARENTHESIS)) {
					operatorStack.pop();
				} else {
					throw new APException(ERROR_MESSAGE_2);
				}
			}
		}

		while (operatorStack.size() > 0) {

			if (isParenthesis(operatorStack.top().getValue())) {
				throw new APException(ERROR_MESSAGE_2);
			}
			result.add(operatorStack.pop());
		}

		return result;
	}

	private void start() {
		Scanner in = new Scanner(System.in);
		String input;

		while (in.hasNext()) {

			try {
				input = in.nextLine();
				TokenList infix = readTokens(input);
				TokenList postfix = shuntingYard(infix);
				double result = rpn(postfix);
				out.printf("%f\n", result);
			} catch (APException e) {
				System.out.println(e);
			}
		}
		in.close();
	}

	public static void main(String[] argv) {
		new Main().start();
	}
}
