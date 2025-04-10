package org.example;

import org.example.actions.Actions;
import org.example.actions.EntitiesSpawner;
import org.example.map.WorldMap;
import org.example.utils.consoleRenderers.ConsoleMessagesRenderer;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;



public class Simulation {
    private final WorldMap worldMap = new WorldMap();
    private final Actions actions = new Actions(worldMap);
    private final EntitiesSpawner entitiesSpawner = new EntitiesSpawner(worldMap);
    private final Scanner scanner = new Scanner(System.in);
    private final ConsoleMessagesRenderer messagesRenderer = new ConsoleMessagesRenderer();
    private static final String EXIT = "E";
    private static final String NEXT = "N";
    private static final String START_STOP = "S";
    private static final int VALID_STRING_LENGTH = 1;
    private static final int FIRST_CHARACTER_POSITION = 0;
    private static final int ITERATION_TIME = 1500;
    private String userPressed;
    private boolean running= false;
    private boolean finished = false;
    public synchronized void start(){
        this.notifyAll();
    }
    public synchronized void stop(){
        try {
            this.wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void startSimulation() {
        entitiesSpawner.spawnEntities();
        Thread controlThread = new Thread(() -> {
            startingInteractionWithUser();
            defaultInteractionWithUser();
        });
        controlThread.start();
        endlessSimulation();

    }

    public void nextTurn(){
        try {
            actions.turnActions();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void endlessSimulation() {
        while (!finished) {
            try {Thread.sleep(ITERATION_TIME);}
            catch (InterruptedException e){
                throw new RuntimeException(e);}

            if (running) {
                nextTurn();
            }else
                stop();
        }
        messagesRenderer.output(ConsoleMessagesRenderer.ENDING_MESSAGE);
    }


    private boolean inputIsValid(String userPressed) {
        if (userPressed.length()==VALID_STRING_LENGTH){
            char input = userPressed.charAt(FIRST_CHARACTER_POSITION);
            if(Character.isLetter(input)){
                return isInputAvailable(userPressed);
            }
        }
        return false;
    }

    private boolean isInputAvailable(String userPressed) {
        List<String> actions = List.of(START_STOP,NEXT,EXIT);
        userPressed = userPressed.toUpperCase();
        return actions.contains(userPressed);
    }

    private void exit() {
        running=false;
        finished=true;
        start();
        Thread.currentThread().interrupt();
    }

    private void defaultInteractionWithUser(){
        while (!Thread.currentThread().isInterrupted()) {
            userPressed = scanner.nextLine();
            if (userPressed.equalsIgnoreCase(START_STOP)) {
                running = false;
                while (!running&&!Thread.currentThread().isInterrupted()){
                    chooseAnAction();
                }
            }else
                messagesRenderer.output(ConsoleMessagesRenderer.INCORRECT_INPUT_MESSAGE);
        }
    }

    private void chooseAnAction() {
        userPressed = scanner.nextLine();
        if(inputIsValid(userPressed)){
            userPressed = userPressed.toUpperCase(Locale.ROOT);
            switch (userPressed){
                case START_STOP ->running();
                case NEXT -> nextTurn();
                case EXIT -> exit();
            }
        }else
            messagesRenderer.output(ConsoleMessagesRenderer.INCORRECT_INPUT_MESSAGE);
    }

    private void startingInteractionWithUser(){
        while (!finished&&!running){
            messagesRenderer.output(ConsoleMessagesRenderer.STRING_MESSAGE);
            userPressed = scanner.nextLine().toUpperCase();

            switch (userPressed){
                case START_STOP ->running();
                case EXIT -> exit();
                default -> messagesRenderer.output(ConsoleMessagesRenderer.INCORRECT_INPUT_MESSAGE);
            }
        }
    }

    private void running() {
        running = true;
        start();
    }
}
