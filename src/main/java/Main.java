import java.util.Scanner;

class Main implements CalculatorInterface {

	static final String OPERATORS = "+-*/^", 
						OPEN_PARENTHESIS = "(", 
						CLOSE_PARENTHESIS = ")", 
						ADDITION = "+",
						SUBSTRACTION = "-", 
						MULTIPLICATION = "*", 
						DIVISION = "/", POWER = "^",
						ADDITION_SUBSTRACTION = "+-",
						ADDITION_ADDITION= "++", 
						SUBSTRACTION_ADDITION = "-+", 
						SUBSTRACTION_SUBSTRACTION = "--",
						WELCOME_MESSAGE = "Welcome to Basic Calculator by Kostas Mountzakis and Ruben van der Ham\nType 'exit' to exit\n"
										+ "Use argument 'help' for more info\nType your input below:",
						ERROR_MESSAGE = "Invalid token in the arithmetic expression. Please fix your input. Try argument 'help' for more information",
						ERROR_MESSAGE_2 = "Mismatched parenthesis detected. Please check your input. Try argument 'help' for more information",
						ERROR_MESSAGE_3 = "Invalid input, remaining numbers on stack. Please fix your input. Try argument 'help' for more information",
						ERROR_MESSAGE_4 = "Invalid input, Try argument 'help' for more information\nOperation misses an operand for operation: ",
						ERROR_MESSAGE_5 = "Input results a calculation this calculator's power function can not handle, please use lower (or higher) operands",
						HELP_MESSAGE = "This basic calculator works with operators +,-,*,/,^ and parenthesis '(' and ')'.\n"
										+ "Basic Calculator REQUIRES you to put a space between each character.\nFor example:'93 * 38 / ( 1 * ( ( 72 ) ) ) * 23'\n\n"
										+ "Basic Calculator by Kostas Mountzakis & Ruben van der Ham";
	static final int NON_OPERATOR_PRECEDENCE = -1, 
						HIGH_PRECEDENCE = 3, 
						MEDIUM_PRECEDENCE = 2, 
						LOW_PRECEDENCE = 1;

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

	private Token configureToken(String token) {
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
			System.err.println(ERROR_MESSAGE);
			System.exit(0);
		}
		
		return result;
	}

	public TokenList readTokens(String input) {
		String[] tokenArray = input.split("\\s+"); // splits string in space and creates an array with  only the tokens
		TokenList result = new TokenListImplementation(tokenArray.length);

		for (int i = 0; i < tokenArray.length; i++) {
			Token token = configureToken(tokenArray[i]);
			result.add(token);
		}
		
		return result;
	}

	private double performOperation(double operand1, double operand2, String operation) {
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
				System.out.println(ERROR_MESSAGE_5);
				System.exit(1);
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

	public Double rpn(TokenList tokens) {
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
					System.out.printf("%s'%s'\n", ERROR_MESSAGE_4, token.getValue());
					System.exit(1);
				}
			}
		}

		if (stack.size() != 1) {
			System.err.println(ERROR_MESSAGE_3);
			System.exit(1);
		}
		
		return stack.top();
	}

	public TokenList shuntingYard(TokenList tokens) {
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
					System.err.println(ERROR_MESSAGE_2);
					System.exit(1);
				}
			}
		}
		
		while (operatorStack.size() > 0) {
			
			if (isParenthesis(operatorStack.top().getValue())) {
				System.out.println(ERROR_MESSAGE_2);
				System.exit(1);
			}
			result.add(operatorStack.pop());
		}
		
		return result;
	}

	private void start() {
		System.out.printf("%s\n", WELCOME_MESSAGE);
		
		Scanner in = new Scanner(System.in);
		String input;
		
		while (in.hasNext()) {
			input = in.nextLine();
			
			if (input.equals("exit")) {
				System.out.println("Basic Calculator closed by user");
				System.exit(0);
			}
			if (input.equals("help")) {
				System.out.println(HELP_MESSAGE);
				System.exit(0);
			}
			TokenList infix = readTokens(input);
			TokenList postfix = shuntingYard(infix);
			double result = rpn(postfix);
			System.out.println(result);
		}
		in.close();
		System.exit(0);
	}

	public static void main(String[] argv) {
		new Main().start();
	}
}
