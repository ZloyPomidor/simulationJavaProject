package org.example.utils.consoleRenderers;

public class ConsoleMessagesRenderer {
    public static final String INCORRECT_INPUT_MESSAGE = "Incorrect input. Please try again following the instructions.";
    public static final String STRING_MESSAGE = "Please select an action: \"S\" - (START) or \"E\" - (EXIT)";
    public static final String ENDING_MESSAGE = "The simulation is complete.";
    public static final String ENTITY_IS_NULL_MESSAGE ="Entity is null";
    public static final String COORDINATES_IS_NOT_VALID_MESSAGE ="Coordinates go beyond the boundaries of map.";

    public void output(String message){
        System.out.println(message);
    }
}
