
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

public class PandemicGame {
    // Declaration of all Global variables used in the game.
    private static Scanner shellInput;
    private static boolean shellOpen = false;
    private static Random randomGenerator = new Random(1);
    private static int numberCities = -1;
    private static int numberConnections = -1;
    private static String[] cities;
    private static int[] diseaseCubes;
    private static int[][] connections;
    private static int[] userLocation;
    private static int currentUser = 0;
    private static int countCheck = 0;
    private static final int MAX_USERS = 4;
    private static final String cityMapFileName = "C:\\Users\\DELL\\fullMap.txt";
    private static String[] usernames;
    private static int NUMBER_USERS = 0;
    private static final int QUIT = 0;
    private static final int PRINT_LOCATION = 1;
    private static final int MOVE = 2;
    private static final int PRINT_ACTIONS = 3;
    private static final int PRINT_CITIES = 4;
    private static final int PRINT_CONNECTIONS = 5;
    private static final int PRINT_ADJACENT_CITIES = 6;
    private static final int PRINT_DISEASES = 7;
    private static final int REMOVE = 8;
    private static final int PRINT_CARDS = 9;
    private static final int GET_STATUS = 10;
    private static final int DIRECT_FLIGHT = 11;
    private static final int CHARTER_FLIGHT = 12;
    private static final int SHUTTLE_FLIGHT = 13;
    private static final int BUILD_RESEARCH = 14;
    private static final int PLAY_EVENT = 15;

    // Extra variables for the gameplay.
    private static int addAction = 0;
    private static int[] researchStation = new int[6];
    private static int[] diseaseCubeCities = new int[96];
    private static int[] remainingCubes = new int[4];
    private static int researchCt = 0;                          // variable to keep track of total research stations.
    private static final int BLUE_CUBE = 0;
    private static final int YELLOW_CUBE = 1;
    private static final int RED_CUBE = 2;
    private static final int BLACK_CUBE = 3;
    private static final int MAX_HAND_SIZE = 7;
    private static PandemicDeck playerDeck;                     // Player Deck of cards.
    private static PandemicDeck infectionDeck;
    private static PandemicHand[] userHand;
    private static PandemicHand infectionDiscardPile;
    private static int outbreak;
    private static int foundCure;
    private static int infectionRate = 1;
    private static boolean freeCure;
    private static boolean blueCure;
    private static boolean yellowCure;
    private static boolean redCure;
    private static boolean blackCure;
    private static boolean gameOver;
    private static final String[] color = new String[]{"blue", "yellow", "red", "black"};
    private static int blueCt;
    private static int yellowCt;
    private static int redCt;
    private static int blackCt;
    private static boolean addMove = false;  // variable for the add move special card.
    private static int addCount = 0;

    /**
     * Takes in a user input as a String and gives an integer equivalent output
     * This method first converts the user's input to lower case and trims any extra
     * white spaces after it.
     * @param inputString
     * @return
     */
    private static int processUserInput(String inputString) {
        inputString = inputString.toLowerCase().trim();
        return switch (inputString.toLowerCase().trim()) {
            case "quit" -> QUIT;
            case "location" -> PRINT_LOCATION;
            case "cities" -> PRINT_CITIES;
            case "connections" -> PRINT_CONNECTIONS;
            case "adjacent" -> PRINT_ADJACENT_CITIES;
            case "infections" -> PRINT_DISEASES;
            case "move" -> MOVE;
            case "remove" -> REMOVE;
            case "actions", "help" -> PRINT_ACTIONS;
            case "print cards" -> PRINT_CARDS;
            case "get status" -> GET_STATUS;
            case "direct flight" -> DIRECT_FLIGHT;
            case "charter flight" -> CHARTER_FLIGHT;
            case "shuttle flight" -> SHUTTLE_FLIGHT;
            case "build research" -> BUILD_RESEARCH;
            case "play event" -> PLAY_EVENT;
            default -> -1;
        };
    }

    private static int getUserInput() {
        boolean gotReasonableInput = false;
        int processedUserInput = -1;
        if (!shellOpen) {
            shellInput = new Scanner(System.in);
            shellOpen = true;
        }

        while(!gotReasonableInput) {
            String userInput = shellInput.nextLine();
            processedUserInput = processUserInput(userInput);
            if (processedUserInput >= 0) {
                gotReasonableInput = true;
            }
            else {
                System.out.println(userInput + " is not a good command. Try 'actions'.");
            }
        }

        return processedUserInput;
    }

    private static void printAdjacentCities() {
        for(int cityNumber = 0; cityNumber < numberCities; ++cityNumber) {
            if (citiesAdjacent(userLocation[currentUser], cityNumber)) {
                System.out.println(cities[cityNumber]);
            }
        }

    }

    private static void printActions() {
        System.out.println("Type in on the terminal with the following followed by no spaces finish with return.");
        System.out.println("quit");
        System.out.println("location");
        System.out.println("cities");
        System.out.println("connections");
        System.out.println("adjacent");
        System.out.println("infections");
        System.out.println("move");
        System.out.println("remove");
        System.out.println("actions");
        System.out.println("print cards -- to show cards in hand.");
        System.out.println("get status -- gets the info of the city you're in.");
        System.out.println("Direct Flight -- Discard a city card to fly to that city.");
        System.out.println("Charter Flight -- Discard card of current city to fly to any other city.");
        System.out.println("Shuttle Flight -- Move between research stations.");
        System.out.println("Build Research -- Discard the card matching a city you're in.");
        System.out.println("Play Event -- Play an event card if you have one. it is considered as an action.");
    }

    private static void printUserLocations() {
        System.out.println("The current user is " + usernames[currentUser]);

        for(int userNumber = 0; userNumber < NUMBER_USERS; ++userNumber) {
            int printUserLocation = userLocation[userNumber];
            String var10001 = usernames[userNumber];
            System.out.println(var10001 + " is in " + cities[printUserLocation]);
        }

    }

    private static boolean processUserCommand(int userInput) {
        switch (userInput) {
            case QUIT -> {
                return true;
            }
            case PRINT_LOCATION -> printUserLocations();
            case MOVE -> {
                moveUser();
                checkAndCountActions();
            }
            case PRINT_ACTIONS -> printActions();
            case PRINT_CITIES -> printCities();
            case PRINT_CONNECTIONS -> printConnections();
            case PRINT_ADJACENT_CITIES -> printAdjacentCities();
            case PRINT_DISEASES -> printInfectedCities();
            case REMOVE -> {
                if (removeCube())                                     // Check removeCube.
                    checkAndCountActions();
            }
            case PRINT_CARDS -> printAllCards();
            case GET_STATUS -> checkCityStatus(userLocation[currentUser]);
            case DIRECT_FLIGHT -> doDirectFlight();
            case CHARTER_FLIGHT -> doCharterFlight();
            case SHUTTLE_FLIGHT -> doShuttleFlight();
            case BUILD_RESEARCH -> buildResearchStation(userLocation[currentUser]);

            case PLAY_EVENT -> playEvent();
        }
        return false;
    }

    private static void moveUser() {
        boolean moved = false;

        while(!moved) {
            System.out.println("type where you'd like to move.");
            System.out.println("You can move to ");
            printAdjacentCities();
            String userInput = shellInput.nextLine();
            int cityToMoveTo = getCityOffset(userInput);
            if (cityToMoveTo == -1) {
                System.out.println(userInput + " is not a valid city. Try one of these.");
            } else if (citiesAdjacent(userLocation[currentUser], cityToMoveTo)) {
                String var10001 = usernames[currentUser];
                System.out.println(var10001 + " has moved from " + cities[userLocation[currentUser]] + " to " + cities[cityToMoveTo] + ".");
                userLocation[currentUser] = cityToMoveTo;
                moved = true;
            } else {
                System.out.println("You can't move to " + userInput + ". Try one of these.");
                printAdjacentCities();
            }
        }

    }

    private static boolean removeCube() {                                               //Check this subroutine.
        int currentUserLocation = userLocation[currentUser];
        if (diseaseCubes[currentUserLocation] > 0) {
            diseaseCubes[currentUserLocation]--;
            int cubesLeft = diseaseCubes[currentUserLocation];
            System.out.println("There are " + cubesLeft + " left");
            return true;
        }
        else {
            System.out.println("The space you're on has no disease cubes.");
            return false;
        }
    }

    private static void checkAndCountActions() {
        if (addMove) {
            System.out.println(usernames[currentUser] + " perform another action.");
            addMove = false;
            addCount = 2;   // Add two extra moves to the user that played the 'add move' special card.
            System.out.println("No. of moves left: " + addCount);
            return;
        }
        if(addCount > 0) {
            addCount--;
            System.out.println("No. of moves left: " + addCount);
            System.out.println("Perform another action.");
            return;
        }
        drawPlayerCard();
        drawInfectionCard();
        actionDone();

    }

    private static void actionDone() {
        currentUser++;
        currentUser %= NUMBER_USERS;
        System.out.println("It's now " + usernames[currentUser] + "'s turn.");
    }

    private static void readCities(int numCities, Scanner in) {
        for(int cityNumber = 0; cityNumber < numCities; cityNumber++) {
            String cityName = in.nextLine();
            cities[cityNumber] = cityName;
        }

    }

    private static void printCities() {
        System.out.println(numberCities + " Cities.");

        for(int i = 0; i < numberCities; ++i) {
            System.out.println(cities[i]);
        }

    }

    private static int getCityOffset(String cityName) {
        for(int cityNumber = 0; cityNumber < numberCities; ++cityNumber) {
            if (cityName.compareTo(cities[cityNumber]) == 0) {
                return cityNumber;
            }
        }

        return -1;
    }

    private static boolean citiesAdjacent(int city1, int city2) {
        for(int compareConnection = 0; compareConnection < numberConnections; ++compareConnection) {
            if (connections[0][compareConnection] == city1 && connections[1][compareConnection] == city2) {
                return true;
            }

            if (connections[0][compareConnection] == city2 && connections[1][compareConnection] == city1) {
                return true;
            }
        }

        return false;
    }

    private static void readConnections(int numConnections, Scanner scanner) {
        for(int connectionNumber = 0; connectionNumber < numConnections; ++connectionNumber) {
            String connectionName = scanner.nextLine();
            String[] cityName = connectionName.split(";");
            int firstCityOffset = getCityOffset(cityName[0]);
            int secondCityOffset = getCityOffset(cityName[1]);
            connections[0][connectionNumber] = firstCityOffset;
            connections[1][connectionNumber] = secondCityOffset;
        }

    }

    private static void printConnections() {
        System.out.println(numberConnections + " Connections.");

        for(int connectionNumber = 0; connectionNumber < numberConnections; ++connectionNumber) {
            String firstCity = cities[connections[0][connectionNumber]];
            String secondCity = cities[connections[1][connectionNumber]];
            System.out.println(firstCity + " " + secondCity);
        }

    }

    private static void readCityGraph() {
        try {
            File fileHandle = new File(cityMapFileName);
            Scanner mapFileReader = new Scanner(fileHandle);
            numberCities = mapFileReader.nextInt();
            mapFileReader.nextLine();
            cities = new String[numberCities];
            diseaseCubes = new int[numberCities];
            numberConnections = mapFileReader.nextInt();
            mapFileReader.nextLine();
            connections = new int[2][numberConnections];
            readCities(numberCities, mapFileReader);
            readConnections(numberConnections, mapFileReader);
            mapFileReader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("An error occurred reading the city graph.");
            e.printStackTrace();
        }

    }

    private static void printInfectedCities() {
        for(int cityNumber = 0; cityNumber < numberCities; ++cityNumber) {
            if (diseaseCubes[cityNumber] > 0) {
                String city = cities[cityNumber];
                System.out.println(city + " has " + diseaseCubes[cityNumber] + " cubes.");
            }
        }

    }

    public static void getUsers() {
        int i = 0;
        Scanner in = new Scanner(System.in);
        System.out.println("This game allows a maximum of 4 users.");
        System.out.print("Enter the number of users: ");

        while(true) {
            int noOfUsers;
            while(true) {
                try {
                    noOfUsers = Integer.parseInt(in.nextLine());
                    break;
                }
                catch (NumberFormatException var5) {
                    System.out.println("Please input a valid number.");
                }
            }

            if (noOfUsers >= 1 && noOfUsers <= 4) {
                NUMBER_USERS = noOfUsers;
                userLocation = new int[NUMBER_USERS];
                usernames = new String[noOfUsers];
                System.out.println("Type in your usernames. Press the 'Return' key to use default.");


                mainLoop: while(i < usernames.length) {
                    System.out.print("User " + (i + 1) + ": ");
                    String username = in.nextLine();
                    if (username.trim().length() == 0) {
                        username = "User" + (i + 1);
                    }

                    usernames[i] = username;
                    if (i > 0) {
                        for(int j = 0; j < i; ++j) {
                            if (usernames[i].equalsIgnoreCase(usernames[j])) {
                                System.out.println("Two users can not have the same username.");
                                continue mainLoop;
                            }
                        }
                    }
                    i++;
                }

                System.out.print("Welcome ");
                if (usernames.length > 1) {
                    for(int j = 0; j < usernames.length; ++j) {
                        String var10001;
                        if (j == usernames.length - 1) {
                            var10001 = usernames[j];
                            System.out.print("and " + var10001 + " to the game.");
                        } else {
                            var10001 = usernames[j];
                            System.out.print(var10001 + ", ");
                        }
                    }
                } else {
                    System.out.println(usernames[0] + " to the game.");
                }

                return;
            }

            System.out.println("Please input a valid number from 1 - 4.");
        }
    }

    public static void main(String[] args) {
        boolean gameDone = false;
        System.out.println("Hello Pandemic Tester");

        try {
            getUsers();
            readCityGraph();
            initializeGame();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        while(!gameDone || !gameOver) {
            try {
                int userInput = getUserInput();
                gameDone = processUserCommand(userInput);
            }
            catch (Exception var3) {
                System.out.println(var3.getMessage());
            }
        }

        System.out.println("Goodbye Pandemic Tester");
    }

    private static void buildResearchStation(int cityNumber) {
        if (researchCt == 6) {
            throw new IllegalStateException("Maximum number of Research Stations reached.");
        }
        else {
            for (int researchCity : researchStation) {
                if (researchCity == cityNumber) {
                    System.out.println("You can't build a research station here because it already has one.");
                    return;
                }
            }
            // Here, the research station is ready to be built
            researchStation[researchCt] = cityNumber;
            // Increment the number of research stations built so far
            researchCt++;
        }
    }

    private static void initializeGame() {
        initializeData();
        setUpBoard();
    }

    private static void checkCityStatus(int cityNumber) {
        getResearchInfo(cityNumber);
        printDiseaseInCity(cityNumber);
    }

    private static void initializeData() {
        Arrays.fill(diseaseCubeCities, -1);
        Arrays.fill(researchStation, -1);
        researchStation[0] = 0;                 // There is one research station in Atlanta at the start of the game.
        Arrays.fill(remainingCubes, 24);
        researchCt++;
    }

    private static void getResearchInfo(int cityNumber) {
        boolean research = false;

        for (int researchCity : researchStation) {
            if (researchCity == cityNumber) {
                research = true;
                break;
            }
        }

        if (research)
            System.out.println("There is a research station in " + cities[cityNumber]);
        else
            System.out.println("There is no research station in " + cities[cityNumber]);
    }

    private static void printDiseaseInCity(int cityNumber) {
        countDiseaseCubes(cityNumber);
        int total = blueCt + yellowCt + redCt + blackCt;
        String city = cities[cityNumber];
        System.out.print("The number of cubes in " + city + ": ");
        if (blueCt == 0 && yellowCt == 0 && redCt == 0 && blackCt == 0)
            System.out.print("0");
        else
            System.out.println("" + total + "\nBlue Disease: " + blueCt + ", Yellow Disease: "
                    + yellowCt + ",  Red Disease: " + redCt + ", Black Disease: " + blackCt);

    }

    private static void countDiseaseCubes(int cityNumber) {
        // Re-initialize all the values for counting the number of individual cubes.
        blueCt = 0;
        yellowCt = 0;
        redCt = 0;
        blackCt = 0;

        for(int i = 0; i < 96; ++i) {
            if (i < 24 && diseaseCubeCities[i] == cityNumber)
                blueCt++;
            else if (i < 48 && diseaseCubeCities[i] == cityNumber)
                yellowCt++;
            else if (i < 72 && diseaseCubeCities[i] == cityNumber)
                redCt++;
            else if (diseaseCubeCities[i] == cityNumber)
                blackCt++;
        }
    }

    private static void createInfection(int cityNumber, int noOfCubes, int cubeColor) {
        if (cubeColor >= 0 && cubeColor <= 3) {
            if (remainingCubes[cubeColor] == 0) {
                String info = "Out of disease cubes of a particular color.";
                gameEnded(false, info);
            }
            else {
                int cubeEnd = switch (cubeColor) {
                    case BLUE_CUBE -> 24;
                    case YELLOW_CUBE -> 48;
                    case RED_CUBE -> 72;
                    default -> 96;
                };

                for(int i = 0; i < noOfCubes; ++i) {
                    diseaseCubeCities[cubeEnd - remainingCubes[0]] = cityNumber;
                    remainingCubes[cubeColor]--;
                }
            }
        }
        else
            throw new IllegalArgumentException("Type in a number from 0 - 3");
    }

    private static boolean checkOutbreak(int cityNumber, int noOfCubes, int cubeEnd) {
        int count = 0;

        for(int i = cubeEnd - 24; i < cubeEnd; ++i)
            if (diseaseCubeCities[i] == cityNumber)
                count++;

        return (count + noOfCubes > 3);
    }

    private static void infectCities() {
        playerDeck = new PandemicDeck(true, 4);
        infectionDeck = new PandemicDeck();
        infectionDeck.shuffle();
        userHand = new PandemicHand[NUMBER_USERS];


        for(int userNumber = 0; userNumber < NUMBER_USERS; userNumber++)
            userHand[userNumber] = new PandemicHand();

        infectionDiscardPile = new PandemicHand();

        // Deal Nine cards from the infection deck. This is one of the things that are done
        // at the start of the game.
        for(int cardCount = 0; cardCount < 9; cardCount++)
            infectionDiscardPile.addCard(infectionDeck.dealCard());

        for(int cardCount = 0; cardCount < 6; cardCount++) {
            int cubeValue = cardCount < 3 ? 2 : 3;
            int value = infectionDiscardPile.getCard(cardCount).getValue();
            int color = infectionDiscardPile.getCard(cardCount).getAttribute();
            createInfection(value, cubeValue, color);
            ++cubeValue;
        }

        for(int cardCount = 6; cardCount < 9; cardCount++) {
            createInfection(infectionDiscardPile.getCard(cardCount).getValue(), 1,
                    infectionDiscardPile.getCard(cardCount).getAttribute());
        }

    }

    private static void gameEnded(boolean won, String info) {
        System.out.print("Game over. ");
        if (won)
            System.out.println("Game won!");
        else
            System.out.println("Game lost.");
        System.out.println(info);
        gameOver = true;
    }

    private static void setUpBoard() {
        infectCities();
        dealInitialCards();
    }

    private static void drawPlayerCard() {
        if (playerDeck.isEmpty()) {
            String info = "Can't draw cards -- Deck is empty.";
            gameEnded(false, info);
        }
        else {
            if (userHand[currentUser].getCardCount() == MAX_HAND_SIZE) {
                System.out.println("You've reached the number of cards limit in hand.\nYou'll have to discard a card." +
                        " Starting card number 1.");
                printAllCards();
                Scanner in = new Scanner(System.in);

                mainLoop: while(true) {
                    while(true) {
                        try {
                            System.out.print("? ");
                            int cardNumber = in.nextInt();
                            if (cardNumber >= 1 && cardNumber <= userHand[currentUser].getCardCount()) {
                                removeCard(cardNumber - 1);
                                break mainLoop;
                            }
                            System.out.println("Please input a value between 1 and " + userHand[currentUser].getCardCount());
                        }
                        catch (Exception e) {
                            System.out.println("Illegal input found. Please input a valid card number from 1 to "
                                    + userHand[currentUser].getCardCount());
                        }
                    }
                }
            }

            PandemicCard card = playerDeck.dealCard();
            if (card.getValue() >= 52 && card.getValue() <= 55)
                drawEpidemicCard(card);
            else
                userHand[currentUser].addCard(card);
        }
    }

    private static void removeCard(int position) {
        userHand[currentUser].removeCard(position);
    }

    private static void printAllCards() {
        for (PandemicCard card : userHand[currentUser].getCardArray()) {
            int cardValue = card.getValue();
            if (card.getAttribute() != PandemicCard.EVENT_CARD) {
                System.out.println("    " + cities[cardValue] + " " + card.getAttributeAsString());
            }
            else {
                switch (cardValue) {
                    case PandemicCard.FLY_ANYWHERE ->
                            System.out.println("    " + card.getAttributeAsString() + ": Fly Anywhere.");
                    case PandemicCard.BUILD_R_ANYWHERE ->
                            System.out.println("    " + card.getAttributeAsString() + ": Build Research station anywhere.");
                    case PandemicCard.SOLVE_DISEASE ->
                            System.out.println("    " + card.getAttributeAsString() + ": Solve Disease.");
                    default ->
                            System.out.println("    " + card.getAttributeAsString() + ": Add one move to number of moves.");
                }
            }
        }

        System.out.println("Cards are added and discarded.\nDo actions to play cards.");
    }

    private static void playEvent() {
        Scanner in = new Scanner(System.in);
        System.out.println("Card position: ");

        int cardPosition;
        while(true) {
            try {
                cardPosition = in.nextInt();
                if (cardPosition >= 1 && cardPosition <= userHand[currentUser].getCardCount()) {
                    break;
                }
                System.out.println("Please input a valid number from 1 to " + userHand[currentUser].getCardCount());
            }
            catch (Exception e) {
                System.out.print("Illegal input. Please put a valid input.\n? ");
            }
        }

        PandemicCard card = userHand[currentUser].getCard(cardPosition - 1);
        if (card.getAttribute() != PandemicCard.EVENT_CARD) {
            System.out.println("Card is not an event card.");
        }
        else {
            int cityNumber = -1;
            switch (card.getValue()) {
                case PandemicCard.FLY_ANYWHERE -> {
                    while (cityNumber < 0) {
                        cityNumber = doFlyAnywhere();
                        if (cityNumber < 0) {
                            System.out.println("Type in a valid city number.");
                        }
                    }
                    userLocation[currentUser] = cityNumber;
                }
                case PandemicCard.BUILD_R_ANYWHERE -> {
                    if (researchCt == 6) {
                        System.out.println("Research station has reached its limit. Card automatically discarded.");
                        return;
                    }
                    while (cityNumber < 0) {
                        cityNumber = doBuildResearch();
                        if (cityNumber < 0) {
                            System.out.println("Type in a valid city number.");
                        }
                    }
                    buildResearchStation(cityNumber);
                }
                case PandemicCard.SOLVE_DISEASE -> {
                    freeCure = true;
                    doSolveDisease(in);
                }
                default -> addMove = true;
            }
            checkAndCountActions();
        }
    }

    private static void dealInitialCards() {
        PandemicHand hand = new PandemicHand();

        for(int i = 0; i < 4; ++i)
            hand.addCard(playerDeck.removeBottom());

        playerDeck.shuffle();
        // Number of cards to be dealt based on the number of users.
        int noOfCards = switch (NUMBER_USERS) {
            case 1, 2 -> 4;
            case 3 -> 3;
            default -> 2;
        };

        for(int userNumber = 0; userNumber < NUMBER_USERS; userNumber++) {
            for(int cardCt = 0; cardCt < noOfCards; cardCt++) {
                userHand[userNumber].addCard(playerDeck.dealCard());
            }
        }
        createEpidemicDeck(hand);
    }

    private static void drawEpidemicCard(PandemicCard card) {
        PandemicHand hand = new PandemicHand();
        hand.addCard(card);
        PandemicCard infectionCard = infectionDeck.removeBottom();
        System.out.println("Epidemic card drawn.");
        PrintStream var10000 = System.out;
        String var10001 = cities[infectionCard.getValue()];
        var10000.println("Infection card drawn from deck bottom. Card: " + var10001 + " " + infectionCard.getAttributeAsString());
        doEpidemics(infectionCard.getValue(), 3, infectionCard.getAttribute());
        infectionDiscardPile.addCard(infectionCard);
        infectionDiscardPile.shuffle();

        for (PandemicCard cardToStack : infectionDiscardPile.getCardArray()) {
            infectionDeck.addToDeck(cardToStack);
        }

        infectionDiscardPile.clear();
        infectionRate++;
    }

    private static void doEpidemics(int cityNumber, int noOfCubes, int cubeColor) {
        if ((cubeColor != 0 || !blueCure) && (cubeColor != 1 || !yellowCure) && (cubeColor != 2 || !redCure)
                && (cubeColor != 3 || !blackCure)) {

            // For setting the end array for a particular cube.
            int cubeEnd = switch (cubeColor) {
                case 0 -> 24;
                case 1 -> 48;
                case 2 -> 72;
                default -> 96;
            };

            if (checkOutbreak(cityNumber, noOfCubes, cubeEnd)) {
                doOutbreak(cityNumber, cubeColor);
            }
            else {
                createInfection(cityNumber, noOfCubes, cubeEnd);
            }

        }
        else {
            System.out.println(color[cubeColor] + " has already been cured");
        }
    }

    private static void drawInfectionCard() {
        for(int cardCt = 0; cardCt < infectionRate; ++cardCt) {
            PandemicCard card = infectionDeck.removeBottom();
            doEpidemics(card.getValue(), 1, card.getAttribute());
            infectionDiscardPile.addCard(card);
        }
    }

    private static void doOutbreak(int outbreakCityNumber, int cubeColor) {
        outbreak++;
        String city = cities[outbreakCityNumber];
        if (outbreak == 8) {
            String info = "An eighth outbreak has occurred in " + city;
            gameEnded(false, info);
        }
        System.out.println("An outbreak has just occurred in " + city);


        for(int cityNumber = 0; cityNumber < numberCities; ++cityNumber) {
            if (citiesAdjacent(outbreakCityNumber, cityNumber)) {
                doEpidemics(cityNumber, 1, cubeColor);
                diseaseCubes[cubeColor]--;
            }
        }

        System.out.println("Total number of outbreaks: " + outbreak);
    }

    private static void createEpidemicDeck(PandemicHand hand) {
        playerDeck.shuffle();
        Random rand = new Random();

        for(int i = 0; i < 4; ++i) {
            playerDeck.addCard(rand.nextInt(24) + i * 24, hand.getCard(i));
        }

    }

    private static void doDirectFlight() {
        checkAndCountActions();
    }

    private static void doCharterFlight() {
        Scanner in = new Scanner(System.in);
        System.out.println("\nThese are the current cards in hand.");
        printAllCards();
        System.out.print("Please type in the card number: ");

        try {
            int cardNumber;
            do {
                do {
                    cardNumber = in.nextInt();
                    if (cardNumber < 1 || cardNumber > userHand[currentUser].getCardCount()) {
                        System.out.print("Please type a valid card Number: ");
                    }
                } while (cardNumber < 1);
            } while (cardNumber > userHand[currentUser].getCardCount());

            PandemicCard card = userHand[currentUser].getCard(cardNumber);
            if (card.getAttribute() == PandemicCard.EVENT_CARD) {
                System.out.println("Card is an event card. You need a city card to perform Charter Flight. Perform another action.");
                userHand[currentUser].addCard(cardNumber, card);
                return;
            }

            if (card.getValue() != userLocation[currentUser]) {
                System.out.println("Card does not match your current location. Please perform another action.");
                userHand[currentUser].addCard(cardNumber, card);
                return;
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Please perform another action.");
            return;
        }

        System.out.println("    Choose a city you wish to travel to:\n");
        printCities();
        System.out.println();

        while(true) {
            System.out.print("Where? ");
            int cityAsValue = searchForCity();
            if (cityAsValue > -1) {
                userLocation[currentUser] = cityAsValue;
                System.out.println(usernames[currentUser] + " is now in " + cities[userLocation[currentUser]] + ".");
                checkAndCountActions();
                return;
            }

            System.out.println("Please input a valid city.");
        }

    }

    private static void processActionCard(boolean doCharterFlight) {

    }

    /**
     * Move between research stations
     */
    private static void doShuttleFlight() {                                                                         // This method is incomplete
        printResearchCities();
        System.out.println("Which research station do you wish to fly to: ");
        int station = searchForCity();
        if (station == -1) {
            System.out.println("Research station city does not exist.");
        }
        else {
            for(int city = 0; city < researchStation.length; city++) {
                if (researchStation[city] == station) {
                    userLocation[currentUser] = station;
                    System.out.println("Shuttle flight to " + cities[userLocation[currentUser]] + " successful.");
                    checkAndCountActions();
                    return;
                }
            }
            System.out.println(cities[station] + " is not a city with a research station.");
        }
    }

    private static void printResearchCities() {
        System.out.println("Research stations available: ");
        int[] var0 = researchStation;
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            int station = var0[var2];
            if (station != -1) {
                System.out.println(cities[station]);
            }
        }
    }

    private static void doTreatDisease(Scanner in) {
    }

    private static int doFlyAnywhere() {
        System.out.print("Type in the city you wish to fly to: ");
        return searchForCity();
    }

    private static int doBuildResearch() {
        System.out.println("Type the city you wish to build your research station in: ");
        return searchForCity();
    }

    private static int searchForCity() {
        Scanner in = new Scanner(System.in);
        String userInput = in.nextLine();
        int cityNumber = 0;
        String[] var3 = cities;
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String city = var3[var5];
            if (userInput.equalsIgnoreCase(city)) {
                return cityNumber;
            }

            ++cityNumber;
        }

        return -1;
    }

    private static void doSolveDisease(Scanner in) {
        boolean rightLocation = false;          // variable for checking if the user is in the right location
        // Check if the user is in a research station. Diseases can only be solved in research stations
        for (int station : researchStation) {
            if (userLocation[currentUser] == station) {
                rightLocation = true;
                break;
            }
        }
        if(!rightLocation) {
            // User is in the wrong location and needs to get to the right location
            System.out.println("You're in " + cities[userLocation[currentUser]] + ", Which is not a research" +
                    " station.");
            return;
        }
        if (!freeCure) {
            PandemicHand cardArray = new PandemicHand();
            System.out.println("Type in all five card locations.");
            int userInput = 0;

            for(int cardCount = 0; cardCount < 5; ++cardCount) {
                do {
                    try {
                        System.out.print("? ");
                        userInput = in.nextInt();
                        if(userInput < 1 || userInput > userHand[currentUser].getCardCount())
                            System.out.println("Illegal card number. Please enter a value between 1 and " +
                                     userHand[currentUser].getCardCount());
                        cardArray.addCard(userHand[currentUser].getCard(userInput - 1));
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                } while (userInput < 1 || userInput > userHand[currentUser].getCardCount());
            }

            // Here compare all five cards drawn into the temporary card Array
            // Discard all five cards
        }
        else {
            System.out.println("Type in the color of disease you want to solve: ");
            switch (in.nextLine().toLowerCase()) {
                case "red" -> {
                    if (redCure) {
                        System.out.println("Red disease has already been cured.");
                        return;
                    }
                    redCure = true;
                }
                case "blue" -> {
                    if (blueCure) {
                        System.out.println("Blue disease has already been cured.");
                        return;
                    }
                    blueCure = true;
                }
                case "yellow" -> {
                    if (yellowCure) {
                        System.out.println("Yellow disease has already been cured.");
                        return;
                    }
                    yellowCure = true;
                }
                case "black" -> {
                    if (blackCure) {
                        System.out.println("Black disease has already been cured.");
                        return;
                    }
                    blackCure = true;
                }
            }
            freeCure = false;
        }
        foundCure++;

        if (foundCure == 4)
            gameEnded(true, "Cure of all four diseases have been found.");

    }
}
