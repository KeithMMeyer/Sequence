
/**
 * Sequence Interpreter by Keith Meyer Copyright 2020
 * CSE 565 Honors Extension
 */

/**
 * This interpreter will run Sequence code in a given file, with given input and 
 * output. Proper syntax is:
 * <Program File> [Input File] [Output File]
 * Where the Program File is required and all others are optional. Input and Output
 * default to standard in and out when not specified.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Sequencer {

	static PrintStream output; // output stream
	static Scanner input; // input stream

	static int pointer = 2; // data pointer for tape
	static ArrayList<Integer> tape = new ArrayList<Integer>(5); // data tape

	static String previous; // previously executed block command

	static int lineNum; // current block number
	static String code = ""; // string of all code

	static boolean skippingLoop = false; // if currently skipping a loop

	/**
	 * Main method to start the interpreter.
	 * 
	 * @param args - a string array containing at least the program file, with an
	 *             optional input file and output file.
	 * @throws FileNotFoundException - if the program file does not exist
	 */
	public static void main(String[] args) {

		//Initializing tape to five elements
		for (int i = 0; i < 5; i++) {
			tape.add(0);
		}

		if (args.length < 1) { // checks for required program file
			System.out.println("Insufficient Params.");
			System.out.println("Correct Syntax: <Program File> [Input File] [Output File]");
			System.exit(-1); // exits as error
		}

		// program/source code file
		File programFile = new File(args[0]);
		Scanner program = null;
		try {
			program = new Scanner(programFile);
		} catch (FileNotFoundException e) { // if file does not exist
			System.out.println("Program file '" + args[0] + "' does not exist.");
			System.exit(-1); // exits as error
		}

		// input file
		if (args.length > 1) {
			File inFile = new File(args[1]);
			try {
				input = new Scanner(inFile);
			} catch (FileNotFoundException e) { // if file does not exist
				System.out.println("Input file '" + args[1] + "' does not exist.");
				System.exit(-1);
			}
		} else { // exits as error
			input = new Scanner(System.in);
		}

		// output file
		if (args.length > 2) {
			try {
				output = new PrintStream(args[2]);
			} catch (FileNotFoundException e) {
				System.out.println("Output file '" + args[2] + "' could not be created.");
				System.exit(-1);
			}
		} else {
			output = System.out;
		}

		lineReader(program); // begin processing code

		// close files
		program.close();
		input.close();
		output.close();

	}

	/**
	 * LineReader reads, and then executes, individual blocks of code. While
	 * instance static variable "skip"
	 * 
	 * @param program - A Scanner to read code from.
	 */
	public static void lineReader(Scanner program) {

		int bracketsFound = 0; // number of brackets found while skipping a loop
		while (program.hasNext()) { // reads in a line of code
			String line = program.nextLine();
			String[] parts = line.split("\\s"); // splits by whitespace
			for (int i = 0; i < parts.length; i++) {
				String command = sanitize(parts[i]); // standardizes input
				code += " " + command; // save command to string
				bracketsFound = execute(command, bracketsFound); // executes the command
				lineNum++;
			}
			code += " " + 0;
			previous = null; // sets previous to null after each line break
			lineNum++;
		}
	}

	/**
	 * Handles execution of code within a loop.
	 */
	public static void loopReader() {
		previous = null; // set previous command to null (for consistency)
		String[] parts = code.split(" "); // gets individual blocks
		int bracketsFound = 0; // number of brackets found (2 3)
		String nextCommand = "null";
		int i;
		for (i = lineNum; i >= 0; i--) { // loop backwards through the code, starding at current line
			String command = sanitize(parts[i]);
			if (command.equals("2") && nextCommand.equals("3")) { // checks for open brackets (2 3)
				if (bracketsFound == 0)
					break;
				bracketsFound--;
			} else {
				if (command.equals("2") && nextCommand.equals("4")) { // checks for close brackets (2 4)
					bracketsFound++;
				}
			}
			nextCommand = command;
		}
		int realNum = lineNum; // store the current line number
		lineNum = i; // set lineNumber to beginning of loop
		i += 2;
		int skips = 0;
		for (; i < parts.length; i++) { // executes the code in the loop
			if (parts[i - 1].equals("2") && parts[i].equals("4")) { // checks for close brackets (2 4)
				if (bracketsFound == 0) {
					endLoop();
					break;
				}
				bracketsFound--;
			} else {
				if (parts[i - 1].equals("2") && parts[i].equals("3")) // checks for open brackets (2 3)
					bracketsFound++;
			}

			skips = execute(parts[i], skips);
			lineNum++;
		}
		lineNum = realNum;
	}

	/**
	 * Handles execution of the command and block command.
	 * 
	 * @param command  - A string containing the command to be executed.
	 * @param brackets - An int counting the number of brackets (2 4) that need to
	 *                 be found before a loop is done being skipped.
	 * @return brackets - Same as above.
	 */
	public static int execute(String command, int brackets) {
		command = sanitize(command); // standardizes input
		if (skippingLoop) { // if presently skipping a loop
			if (previous != null && previous.equals("2") && command.equals("4")) { // checks for close brackets (2 4)
				if (brackets == 0) {
					previous = null;
					skippingLoop = false;
					return brackets;
				}
				brackets--;
			} else {
				if (previous != null && previous.equals("2") && command.equals("3")) { // checks for open brackets (2 3)
					brackets++;
				}
			}
			previous = command;
		} else {
			basic(command, false); // executes basic command
			block(command); // executes block command
			previous = command; // sets previous command to the current command
		}
		return brackets;
	}

	/**
	 * Executes a basic (inner-block) command.
	 * 
	 * @param command  - A string containing the command to run. Should be in
	 *                 ordinal format.
	 * @param onlyLast - Boolean indicating whether to run only the last command in
	 *                 the block.
	 */
	public static void basic(String command, boolean onlyLast) {
		if (command.equals("0"))
			return;
		int val = Integer.valueOf(command) % 4; // takes the modular form of the command
		command = String.valueOf(val == 0 ? 4 : val);

		switch (command) {
		case "1": // add one to pointer
			add(1);
			break;
		case "2": // subtract one to pointer
			if (onlyLast)
				add(-1);
			break;
		case "3": // shift pointer right
			shift(1);
			break;
		case "4": // shift pointer left
			if (onlyLast)
				shift(-1);
			break;
		}
	}

	/**
	 * Executes block command, considering the previous command. If the previous
	 * command is null, no command is executed.
	 * 
	 * @param command - A string containing the command to run. Should be in ordinal
	 *                format.
	 * 
	 */
	public static void block(String command) {
		if (previous == null || previous.equals("0") || command.equals("0")) {
			return; // if no previous command
		}
		int val = Integer.valueOf(previous) % 4; // takes the modular form of the command
		val = val == 0 ? 4 : val;

		switch (command) {
		case "0": // none
			previous = null;
			break;
		case "1": // repeat previous
			basic(previous, false);
			break;
		case "2": // repeat only last of previous
			basic(previous, true); //do last?
			break;
		case "3":
			switch (val) {
			case 1: // copy value from left
				copy(-1);
				break;
			case 2: // begin loop if nonzero
				loop();
				break;
			case 3: // add value from left
				if (pointer == 0) {
					tape.add(0, 0);
					pointer++;
				}
				add(tape.get(pointer - 1));
				break;
			case 4: // read input
				if (input.hasNextByte())
					tape.set(pointer, (int) input.nextByte());
				break;
			}
			break;
		case "4":
			switch (val) {
			case 1: // copy value from right
				copy(1);
				break;
			case 2: // end loop if zero
				endLoop();
				break;
			case 3: // add from right
				if (pointer == tape.size() - 1)
					tape.add(0);
				add(tape.get(pointer + 1));
				break;
			case 4: // output value
				output.print(Character.toChars(Integer.valueOf(tape.get(pointer))));
				break;
			}
			break;
		default: // for larger values
			for (int x = 0; x < Integer.valueOf(command); x++) { // repeat previous n times
				basic(previous, true);
			}

		}

		return;
	}

	/**
	 * Adds an int to the value currently under the pointer. If this will cause the
	 * value to go under zero or over 255, the value will wrap around again (tape
	 * contains unsigned 8 bit value).
	 * 
	 * @param addingValue - an int to add to the value under pointer
	 */
	private static void add(int addingValue) {
		int b = tape.get(pointer);
		if (b == 255 && addingValue > 0) { // if new value will exceed 255
			b = 0;
			addingValue--;
		} else {
			if (b == 0 && addingValue < 0) { // if new value will subceed zero
				b = 255;
				addingValue++;
			}
		}
		b += addingValue;
		tape.set(pointer, b);
	}

	/**
	 * Shifts the data pointer either to the right or to the left. If there is no
	 * cell to shift to, a new one is created.
	 * 
	 * @param shiftingDirection - an int of the direction to shift: -1 is one cell
	 *                          to the left, 1 is one cell to the right
	 */
	private static void shift(int shiftingDirection) {
		if (pointer == tape.size() - 1 && shiftingDirection > 0) { // if new cell doesn't exist
			tape.add(0);
		} else {
			if (pointer == 0 && shiftingDirection < 0) { // if new cell doesn't exist
				tape.add(0, 0);
				pointer += 1;
			}
		}
		pointer += shiftingDirection;
	}

	/**
	 * Copies a value from another cell. If there is no cell to copy from, a new one
	 * is created.
	 * 
	 * @param copyDirection - an int of the direction to copy from: -1 is one cell
	 *                      to the left, 1 is one cell to the right
	 */
	private static void copy(int copyDirection) {
		if (pointer == tape.size() - 1 && copyDirection > 0) {
			tape.add(0);
		} else {
			if (pointer == 0 && copyDirection < 0) {
				tape.add(0, 0);
				pointer += 1;
			}
		}

		int b = tape.get(pointer + copyDirection);
		tape.set(pointer, b);
	}

	/**
	 * Standardizes input to ordinal values. It also filters out comments.
	 * 
	 * @param rawCommand - A string containing a command to be formatted.
	 * @return formattedCommand - A string containing a formatted command.
	 */
	private static String sanitize(String rawCommand) {
		if (rawCommand.length() == 0)
			return "0";
		if (rawCommand.charAt(0) == '.') {
			int counter = 1;
			for (int i = 1; i < rawCommand.length(); i++) {
				counter += rawCommand.charAt(i) == '.' ? 1 : 0;
			}
			return String.valueOf(counter);
		}
		if (Character.isDigit(rawCommand.charAt(0)))
			return rawCommand;
		return "0";
	}

	/**
	 * Begins a new loop, or starts skipping the loop if the value under the pointer
	 * is zero.
	 */
	private static void loop() {
		if (tape.get(pointer) == 0) { // checks if value is zero
			previous = null;
			skippingLoop = true; // starts skipping loop
		}
	}

	/**
	 * Continues a loop (returns code pointer to start of loop), or move ahead if
	 * the value under the pointer is zero.
	 */
	private static void endLoop() {
		if (tape.get(pointer) == 0) { // checks if value is zero
			previous = null;
		} else {
			loopReader(); // return to start of loop
		}
	}
}
