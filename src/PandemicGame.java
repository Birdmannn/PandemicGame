/**
 * This is a very robust code for the console version of the Pandemic game. It is a cooperative game between users
 * by which users work together to win the game. The game is lost when either the player deck finishes, the disease
 * cubes finishes, or there is an eighth outbreak. This class makes use of fullMap.txt and PandemicGameInfo.txt
 */

// Import all files, File for reading from an external file,
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class PandemicGame {
    // Global declaration and initialization of variables used in the game.
    private static Scanner shellInput;                  // For the user's input (super), for actions.
    private static boolean shellOpen = false;
    private static int numberCities = -1;
    private static int numberConnections = -1;
    private static String[] cities;
    private static int[] diseaseCubes;
    private static int[][] connections;
    private static int[] userLocation;
    private static int currentUser = 0;
    private static final int MAX_USERS = 4;
    private static final String cityMapFileName = "C:\\Users\\DELL\\Pandemic\\fullMap.txt";  // Where the map is stored.
    private static final String gameInfoFileName = "C:\\Users\\DELL\\Pandemic\\PandemicGameInfo.txt"; // Description of the game is stored here.
    private static String[] usernames;                  // For storing the usernames of the players
    // Static final variables for some constants that will be used throughout the game.
    private static int NUMBER_USERS = 0;
    private static final int QUIT = 0;
    private static final int PRINT_LOCATION = 1;
    private static final int MOVE = 2;
    private static final int PRINT_ACTIONS = 3;
    private static final int PRINT_CITIES = 4;
    private static final int PRINT_CONNECTIONS = 5;
    private static final int PRINT_ADJACENT_CITIES = 6;
    private static final int PRINT_DISEASES = 7;
    private static final int TREAT_DISEASE = 8;
    private static final int PRINT_CARDS = 9;
    private static final int GET_STATUS = 10;
    private static final int DIRECT_FLIGHT = 11;
    private static final int CHARTER_FLIGHT = 12;
    private static final int SHUTTLE_FLIGHT = 13;
    private static final int BUILD_RESEARCH = 14;
    private static final int PLAY_EVENT = 15;

    // Extra variables for the gameplay.
    private static final int[] researchStation = new int[6];
    private static final int[] diseaseCubeCities = new int[96];
    private static final int[] remainingCubes = new int[4];
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
    private static int infectionRate = 1;  // Increments whenever an Epidemic card is drawn
    private static boolean freeCure;    // For special cad solve disease.
    private static boolean blueCure;    // This and the latter three to keep track of whether the diseases have been cured or not.
    private static boolean yellowCure;
    private static boolean redCure;
    private static boolean blackCure;
    private static final boolean[] cureFound = new boolean[4];  // So the curefound can be iterated through from 0 to 3.
    private static boolean gameOver;   // To change the game state based on some certain criteria
    private static final String[] color = {"blue", "yellow", "red", "black"};  // So the colors in String format can be iterated through
    private static int blueCt;   // Global variable for the number of blue cubes counted in a particular city. This also applies for the next three.
    private static int yellowCt;
    private static int redCt;
    private static int blackCt;
    private static boolean addMove = false;  // variable for the add move special card.
    private static int addCount = 0;  // if the add move special card is played, increment addCount by 2.

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
            case "treat disease" -> TREAT_DISEASE;
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

    /**
     * This should be the main method called during any root action time. Base action in the sense that
     * there are some actions that leads to other actions, each have there individual scanner class to take
     * in user input. Return or end each major method to loop back to this one.
     * @return
     */
    private static int getUserInput() {
        boolean gotReasonableInput = false;
        int processedUserInput = -1;
        if (!shellOpen) {
            shellInput = new Scanner(System.in);
            shellOpen = true;
        }

        // loop while the user's input is invalid/illegal
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

    // Print Adjacent cities associated with the users current location
    private static void printAdjacentCities() {
        for(int cityNumber = 0; cityNumber < numberCities; cityNumber++) {
            if (citiesAdjacent(userLocation[currentUser], cityNumber)) {
                System.out.println(cities[cityNumber]);
            }
        }

    }

    /**
     * Print the total list of actions the user can make, with its descriptions.
     */
    private static void printActions() {
        System.out.println("Type in on the terminal with the following followed by no spaces finish with return.");
        System.out.println("quit");
        System.out.println("location");
        System.out.println("cities");
        System.out.println("connections");
        System.out.println("adjacent");
        System.out.println("infections");
        System.out.println("move");
        System.out.println("treat disease -- to remove one cube of a color from your current location.");
        System.out.println("actions");
        System.out.println("print cards -- to show cards in hand.");
        System.out.println("get status -- gets the info of the city you're in.");
        System.out.println("Direct Flight -- Discard a city card to fly to that city.");
        System.out.println("Charter Flight -- Discard card of current city to fly to any other city.");
        System.out.println("Shuttle Flight -- Move between research stations.");
        System.out.println("Build Research -- Discard the card matching a city you're in.");
        System.out.println("Play Event -- Play an event card if you have one. it is considered as an action.");
    }

    // Prints out the location of each user
    private static void printUserLocations() {
        System.out.println("The current user is " + usernames[currentUser]);

        for(int userNumber = 0; userNumber < NUMBER_USERS; userNumber++) {
            int printUserLocation = userLocation[userNumber];
            System.out.println(usernames[userNumber] + " is in " + cities[printUserLocation]);
        }

    }

    // Handle user commands with its associated methods.
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
            case TREAT_DISEASE -> doTreatDisease();
            case PRINT_CARDS -> printAllCards();
            case GET_STATUS -> checkCityStatus(userLocation[currentUser]);
            case DIRECT_FLIGHT -> processActionCard(false);
            case CHARTER_FLIGHT -> processActionCard(true);
            case SHUTTLE_FLIGHT -> doShuttleFlight();
            case BUILD_RESEARCH -> buildResearchStation(userLocation[currentUser], false);

            case PLAY_EVENT -> playEvent();
        }
        return false;
    }

    // Move the user from one city to any adjacent city from that city
    private static void moveUser() {
        boolean moved = false;      // variable to check if the move was legal.

        System.out.println("type where you'd like to move.");
        System.out.println("You can move to ");
        while(!moved) {
            printAdjacentCities();
            String userInput = shellInput.nextLine();
            int cityToMoveTo = getCityOffset(userInput);
            if (cityToMoveTo == -1) {
                System.out.println(userInput + " is not a valid city. Try one of these.");
            }
            else if (citiesAdjacent(userLocation[currentUser], cityToMoveTo)) {
                System.out.println(usernames[currentUser] + " has moved from " + cities[userLocation[currentUser]] + " to " + cities[cityToMoveTo] + ".");
                userLocation[currentUser] = cityToMoveTo;
                moved = true;
            }
            else {
                System.out.println("You can't move to " + userInput + ". Try one of these.");
            }
        }

    }

    /**
     * After each player action, this checks, and counts actions if a special event card "add move" has been played
     * Set the addCount to two, and return from the method to let the player do another action
     * Play all compulsory moves -- Draw Player Card and Draw infectionCard for last. Switch to the next player if there
     * is more than one player.
     */
    private static void checkAndCountActions() {
        if (addMove) {
            System.out.println(usernames[currentUser] + " perform another action.");
            addMove = false;
            addCount = 2;   // Add two extra moves to the user that played the 'add move' special card.
            System.out.println("No. of moves left: " + addCount);
            return;
        }
        if(addCount > 0) {  // User still has some moves left.
            addCount--;     // Decrement addCount.
            System.out.println("No. of moves left: " + addCount);
            System.out.println("Perform another action.");
            return;
        }
        drawPlayerCard();
        drawInfectionCard();
        actionDone();

    }

    // Switch to the next user, modulo the number of users.
    private static void actionDone() {
        currentUser++;
        currentUser %= NUMBER_USERS;
        System.out.println("It's now " + usernames[currentUser] + "'s turn.");
    }

    // Read all cities using a scanner
    private static void readCities(int numCities, Scanner in) {
        for(int cityNumber = 0; cityNumber < numCities; cityNumber++) {
            String cityName = in.nextLine();
            cities[cityNumber] = cityName;
        }

    }

    // Read the game description from a file, catch IOException if it crashes.
    private static void readAndPrintGameInfo() {
        File gameInfoFile = new File(gameInfoFileName);

        try (Scanner fileInput = new Scanner(gameInfoFile)) {
            while (fileInput.hasNextLine())
                System.out.println("   " + fileInput.nextLine());
        }
        catch (IOException e) {
            System.out.println("An error occurred when reading from file.");
            e.printStackTrace();
        }
    }

    // Print all the cities present in the game.
    private static void printCities() {
        System.out.println(numberCities + " Cities.");

        for(int cityNumber = 0; cityNumber < numberCities; cityNumber++) {
            System.out.println(cities[cityNumber]);
        }
    }

    //Loop through the city array, and return the offset of the cityName parameter in that
    //array.  Return -1 if the cityName is not in the array.
    private static int getCityOffset(String cityName) {
        for(int cityNumber = 0; cityNumber < numberCities; cityNumber++) {
            if (cityName.compareToIgnoreCase(cities[cityNumber]) == 0) {
                return cityNumber;
            }
        }
        return -1;
    }

    //Look through the connections and see if the city numbers are in them.  If
    //Return whether they are in the list.
    private static boolean citiesAdjacent(int city1, int city2) {
        for(int compareConnection = 0; compareConnection < numberConnections; compareConnection++) {
            if (connections[0][compareConnection] == city1 && connections[1][compareConnection] == city2) {
                return true;
            }
            // Swap city1 and city 2 positions, because comparison goes both ways
            if (connections[0][compareConnection] == city2 && connections[1][compareConnection] == city1) {
                return true;
            }
        }
        // City is not adjacent.
        return false;
    }

    // Read the specified number of connections. If it throws an exception, it is caught by its calling catch call.
    private static void readConnections(int numConnections, Scanner scanner) {
        //A simple loop reading connections in.  It assumes the file is text with the last
        //character of the line being the last letter of the city name.  The two
        //cities are separated by a ; with no spaces
        for(int connectionNumber = 0; connectionNumber < numConnections; connectionNumber++) {
            String connectionName = scanner.nextLine();
            String[] cityName = connectionName.split(";");
            int firstCityOffset = getCityOffset(cityName[0]);
            int secondCityOffset = getCityOffset(cityName[1]);
            connections[0][connectionNumber] = firstCityOffset;
            connections[1][connectionNumber] = secondCityOffset;
        }

    }

    // Print out the full list of connections.
    private static void printConnections() {
        System.out.println(numberConnections + " Connections.");

        for(int connectionNumber = 0; connectionNumber < numberConnections; ++connectionNumber) {
            String firstCity = cities[connections[0][connectionNumber]];
            String secondCity = cities[connections[1][connectionNumber]];
            System.out.println(firstCity + " " + secondCity);
        }
    }

    // Open the city file, allocate the space for the cities, and connections, then read the
    // cities, and then read the connections.  It uses those class variables.
    private static void readCityGraph() {
        try {
            File fileHandle = new File(cityMapFileName);
            Scanner mapFileReader = new Scanner(fileHandle);

            numberCities = mapFileReader.nextInt();
            mapFileReader.nextLine();   // read and discard the next line after the int.

            cities = new String[numberCities];
            diseaseCubes = new int[numberCities];

            numberConnections = mapFileReader.nextInt();
            mapFileReader.nextLine();

            connections = new int[2][numberConnections];
            readCities(numberCities, mapFileReader);        // read cities
            readConnections(numberConnections, mapFileReader);  // read connections
            mapFileReader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("An error occurred reading the city graph.");
            e.printStackTrace();
        }

    }

    // Prints the cities already infected by a disease.
    private static void printInfectedCities() {
        for(int cityNumber = 0; cityNumber < diseaseCubeCities.length; cityNumber++) {
            if (diseaseCubeCities[cityNumber] > 0) {
                String city = cities[cityNumber];
                System.out.println(city + " has been infected.");
                // Print a detailed account of the disease found and the color
                printDiseaseInCity(cityNumber);
            }
        }
    }

    /**
     * Initialize the number of users.
     */
    public static void getUsers() {
        int position = 0;
        Scanner in = new Scanner(System.in);
        System.out.println("This game allows a maximum of " + MAX_USERS + " users.");
        System.out.print("Enter the number of users: ");

        while(true) {
            int noOfUsers;
            while(true) {
                try {
                    System.out.print("? ");
                    noOfUsers = Integer.parseInt(in.nextLine());
                    break;
                }
                catch (NumberFormatException e) {
                    System.out.println("Please input a valid number.");
                }
            }

            if (noOfUsers >= 1 && noOfUsers <= MAX_USERS) {
                NUMBER_USERS = noOfUsers;
                userLocation = new int[NUMBER_USERS];
                usernames = new String[noOfUsers];
                System.out.println("Type in your usernames. Press the 'Return' key to use default.");


                mainLoop: while (position < usernames.length) {
                    System.out.print("User " + (position + 1) + ": ");
                    String username = in.nextLine();
                    if (username.trim().length() == 0) {
                        username = "User" + (position + 1);
                    }

                    usernames[position] = username;
                    if (position > 0) {
                        for(int iterator = 0; iterator < position; iterator++) {
                            if (usernames[position].equalsIgnoreCase(usernames[iterator])) {
                                System.out.println("Two users can not have the same username.");
                                continue mainLoop;
                            }
                        }
                    }
                    position++;
                }

                System.out.print("Welcome ");
                if (usernames.length > 1) {
                    for(int iterator = 0; iterator < usernames.length; ++iterator) {
                        if (iterator == usernames.length - 1)
                            System.out.print("and " + usernames[iterator] + " to the game.");
                        else
                            System.out.print(usernames[iterator] + ", ");
                    }
                }
                else {
                    System.out.println(usernames[0] + " to the game.");
                }
                return;
            }
            System.out.println("Please input a valid number from 1 - " + MAX_USERS);
        }
    }

    public static void main(String[] args) {
        boolean gameDone = false;
        System.out.println("Konnichiwaa!");
        System.out.println();

        readAndPrintGameInfo();

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
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("Goodbye Pandemic Players.");
    }

    /**
     * A research station can be built by discarding the card matching the city you are in.
     * @param cityNumber the cityNumber in which the research station should be built, if
     * @param anywhere is true. if false, users will have to perform the rituals by discarding
     * current city card.
     */
    private static void buildResearchStation(int cityNumber, boolean anywhere) {
        if (researchCt == 6) {
            throw new IllegalStateException("Maximum number of Research Stations reached.");
        }
        else {
            if (anywhere) {
                for (int researchCity : researchStation) {
                    if (researchCity == cityNumber) {
                        System.out.println("You can't build a research station here because it already has one.");
                        return;
                    }
                }
                // Here, the research station is ready to be built
                researchStation[researchCt] = cityNumber;
                System.out.println("Research station successfully built in " + cities[cityNumber]);
            }
            else {
                // Automatically check for card and discard if the required card is found.
                int cardPosition = 0;  // to start.
                boolean cardFound = false;
                for (PandemicCard card : userHand[currentUser].getCardArray()) {
                    if (card.getValue() == userLocation[currentUser]) {
                        cardFound = true;
                        break;
                    }
                    cardPosition++;
                }

                if (cardFound) {
                    // Well, build the research station in the city you are currently in.
                    researchStation[researchCt] = userLocation[currentUser];
                    System.out.println("Research station successfully built in your current city " +
                             cities[userLocation[currentUser]]);
                    removeCard(cardPosition);  // Discard the card afterwards.
                    checkAndCountActions();
                }
                else {
                    System.out.println("No card matches your current city. Please perform another action.");
                    return;
                }
            }
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
        int totalDiseaseCubes = countDiseaseCubes(cityNumber);
        String city = cities[cityNumber];
        System.out.print("The number of cubes in " + city + ": ");
        if (blueCt == 0 && yellowCt == 0 && redCt == 0 && blackCt == 0)
            System.out.println("0");
        else
            System.out.println("" + totalDiseaseCubes + "\nBlue Disease: " + blueCt + ", Yellow Disease: "
                    + yellowCt + ",  Red Disease: " + redCt + ", Black Disease: " + blackCt);
    }

    private static int countDiseaseCubes(int cityNumber) {
        // Re-initialize all the values for counting the number of individual cubes.
        blueCt = 0;
        yellowCt = 0;
        redCt = 0;
        blackCt = 0;

        for(int position = 0; position < 96; position++) {
            if (position < 24 && diseaseCubeCities[position] == cityNumber)
                blueCt++;
            else if (position < 48 && diseaseCubeCities[position] == cityNumber)
                yellowCt++;
            else if (position < 72 && diseaseCubeCities[position] == cityNumber)
                redCt++;
            else if (diseaseCubeCities[position] == cityNumber)
                blackCt++;
        }
        return blueCt + yellowCt + redCt + blackCt;
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
        String cardName = null;

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
                    cardName = "FLY ANYWHERE";
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
                            System.out.println("Type in a valid city.");
                        }
                    }
                    cardName = "BUILD RESEARCH ANYWHERE";
                    buildResearchStation(cityNumber, true);
                }
                case PandemicCard.SOLVE_DISEASE -> {
                    freeCure = true;
                    cardName = "SOLVE DISEASE";
                    doSolveDisease(in);
                }
                default -> {
                    addMove = true;
                    cardName = "ADD MOVE";
                }
            }
            System.out.println(card.getAttribute() + ": " + cardName + " played successfully");
            removeCard(cardPosition - 1);    // Discard the card.
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
        int value = infectionCard.getValue();
        String city = cities[value];
        System.out.println("Infection card drawn from deck bottom. Card: " + city + " " + infectionCard.getAttributeAsString());
        doEpidemics(value, 3, infectionCard.getAttribute());
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
        System.out.println("Infection card drawn.");
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

        for(int iterator = 0; iterator < 4; iterator++) {
            playerDeck.addCard(rand.nextInt(24) + iterator * 24, hand.getCard(iterator));
        }
        System.out.println("Epidemic Deck has been created.");
    }

    /**
     * Move to a city on a card by discarding the matching card from your hand.
     */
    private static void doDirectFlight(PandemicCard card, int cardNumber) {
        userLocation[currentUser] = card.getValue();

        System.out.println("");
        checkAndCountActions();
        System.out.println("Direct Flight Successful.");
        System.out.println("You're now in " + cities[userLocation[currentUser]]);
    }

    /**
     * Discard the card of the city you are currently in to travel to any other city on the map.
     */
    private static void doCharterFlight(PandemicCard card, int cardNumber) {

        if (card.getValue() != userLocation[currentUser]) {
            System.out.println("Card does not match your current location. Please perform another action.");
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
                removeCard(cardNumber);         // Discard card
                checkAndCountActions();         // Counts Action
                return;
            }

            System.out.println("Please input a valid city.");
        }
    }

    private static void processActionCard(boolean doCharterFlight) {
        // Write something here that probably explains Charter flight?
        Scanner in = new Scanner(System.in);
        System.out.println("\nThese are the current cards in hand.");
        printAllCards();
        System.out.print("Please type in the card number: ");
        PandemicCard card;
        int cardNumber;
        try {
            do {
                do {
                    cardNumber = in.nextInt();
                    if (cardNumber < 1 || cardNumber > userHand[currentUser].getCardCount()) {
                        System.out.print("Please type a valid card Number: ");
                    }
                } while (cardNumber < 1);
            } while (cardNumber > userHand[currentUser].getCardCount());

            card = userHand[currentUser].getCard(cardNumber);
            if (card.getAttribute() == PandemicCard.EVENT_CARD) {
                System.out.println("Card is an event card. You need a city card to perform Charter Flight. Perform another action.");
                return;
            }


        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Please perform another action.");
            return;
        }

        if(doCharterFlight)
            doCharterFlight(card,cardNumber);
        else
            doDirectFlight(card,cardNumber);
    }

    /**
     * Move between research stations
     */
    private static void doShuttleFlight() {
        printResearchCities();
        System.out.println("Which research station do you wish to fly to: ");
        int station = searchForCity();
        if (station == -1) {
            System.out.println("Research station city does not exist.");
        }
        else {
            for (int city : researchStation) {
                if (city == station) {
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

        for(int station = 0; station < researchStation.length; station++) {
            if (researchStation[station] != -1) {
                System.out.println(cities[station]);
            }
        }
    }

    /**
     * Prints out the number of disease cubes and their colors in the users current location
     * then asks the user for the color to be treated. If a cure has been discovered, remove all
     * the cubes of that color from the city as a single action.
     */
    private static void doTreatDisease() {
        int total = countDiseaseCubes(userLocation[currentUser]);   // The total number of disease cubes
        if(total == 0) {
            System.out.println("The city you are has no disease cubes.");
            return;
        }
        printDiseaseInCity(userLocation[currentUser]);
        System.out.println("type in the color of the disease you want to treat (Or the cube you want to remove).");
        Scanner in = new Scanner(System.in);
        System.out.print("? ");
        String userInput = in.nextLine();
        userInput = userInput.toLowerCase().trim();

        int cubeValue = switch (userInput) {
            case "blue cube", "blue", "blue disease" -> BLUE_CUBE;
            case "yellow cube", "yellow", "yellow disease" -> YELLOW_CUBE;
            case "red cube", "red", "red disease" -> RED_CUBE;
            case "black cube", "black", "black disease" -> BLACK_CUBE;
            default -> -1;
        };

        if (cubeValue < 0) {
            System.out.println("Couldn't decipher user input. Try another action.");
            return;
        }
        removeCube(cubeValue);
    }

    private static void removeCube(int cubeColor) {
        int cubeCount = 0;
        // To initialize the start and end of each disease cube color array.
        int cubeEnd = switch (cubeColor) {
            case BLUE_CUBE -> 24;
            case YELLOW_CUBE -> 48;
            case RED_CUBE -> 72;
            default -> 96;
        };

        int currentUserLocation = userLocation[currentUser];

        // Search the array range and find out if the city is in that range
        for (int cityNumber = cubeEnd - 24; cityNumber < cubeEnd; cityNumber++) {
            if(diseaseCubeCities[cityNumber] == currentUserLocation) {
                // Now check if the disease has been cured. If yes, remove all cubes in one action.
                diseaseCubeCities[cityNumber] = -1;
                cubeCount++;
                // If cure is not found, just break the loop.
                if(! cureFound[cubeColor])
                    break;
            }
        }

        //If cube was not found, return.
        if(cubeCount == 0) {
            System.out.println("No cube of " + color[cubeColor] + " found in your current location.");
            return;
        }
        System.out.println(cubeCount + " cube(s) of " + color[cubeColor] + " removed from your location " +
                userLocation[currentUser]);
        checkAndCountActions();
    }

    private static int doFlyAnywhere() {
        System.out.print("Type in the city you wish to fly to: ");
        return searchForCity();
    }

    /**
     * Build a research station by discarding the card matching the city the user is in
     * @return
     */
    private static int doBuildResearch() {
        System.out.println("Type the city you wish to build your research station in: ");
        return searchForCity();
    }

    private static int searchForCity() {
        Scanner in = new Scanner(System.in);
        String userInput = in.nextLine();
        int cityNumber = 0;

        for (String city : cities) {
            if (userInput.equalsIgnoreCase(city)) {
                return cityNumber;
            }
            cityNumber++;
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
           validateCardsAndCheckCure(in);
        }
        else {
            System.out.println("Type in the color of disease you want to solve: ");
            checkCure(in.nextLine());
            freeCure = false;
        }

        if (foundCure == 4)
            gameEnded(true, "Cure of all four diseases have been found.");
    }

    /**
     * This subroutine ensures that the five cards picked by the user to turn in are valid.
     * This a robust subroutine. Please read the inside comments. If disease of that color has been
     * cured, cards are still retained by the user and are allowed to perform another action.
     * @param in for the scanner input.
     */
    private static void validateCardsAndCheckCure(Scanner in) {
        PandemicHand cardArray = new PandemicHand();
        System.out.println("Type in all five card locations.");
        int userInput = 0;
        int[] cardPositions = new int[5];   // For storing the five user input

        for(int cardCount = 0; cardCount < 5; cardCount++) {
            do {
                try {
                    System.out.print("? ");
                    userInput = in.nextInt();
                    cardPositions[cardCount] = userInput;
                    //Tests if the user's input was in the correct range of the number of cards in hand.
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
        // Discard all five cards if they have reached the last step of validation.
        // First check if their attributes match before discarding from the player
        for (int cardPosition = 0; cardPosition < cardArray.getCardCount(); cardPosition++) {
            // Check if card is an event card
            if (cardArray.getCard(cardPosition).getAttribute() == PandemicCard.EVENT_CARD) {
                System.out.println("Event card found in selected number of cards. Only city cards can be used.");
                System.out.println("Perform another action.");
                return;
            }
            if (cardPosition > 0) {
                if (cardArray.getCard(cardPosition).getAttribute()
                        != cardArray.getCard(cardPosition - 1).getAttribute()) {
                    System.out.println("Found a card of different color. Please you need five cards of the same color.");
                    System.out.println("Perform another action.");
                    return;
                }
                //The robustness is here. Checks if the user's cards are made the same numerical input twice.
                for(int iterator = 0; iterator < cardPosition; iterator++) {
                    if (cardPositions[cardPosition] == cardPositions[iterator]) {
                        System.out.println("You typed in a card location twice. Perform another action.");
                        return;
                    }
                }
            }
        }
        // Here all cards are confirmed to be the same.
        PandemicCard card = cardArray.getCard(0);
        if (! checkCure(card.getAttributeAsString())) {
            // Cure has already been found, User still retains the cards
            System.out.println("Perform another action.");
        }
        else {
            // Discard the users cards
            for (int position : cardPositions)
                removeCard(position);
        }
    }

    private static boolean checkCure(String color) {
        switch (color.toLowerCase()) {
            case "red" -> {
                if (redCure) {
                    System.out.println("Red disease has already been cured.");
                    return false;
                }
                redCure = true;
                cureFound[RED_CUBE]= true;
            }
            case "blue" -> {
                if (blueCure) {
                    System.out.println("Blue disease has already been cured.");
                    return false;
                }
                blueCure = true;
                cureFound[BLUE_CUBE] = true;
            }
            case "yellow" -> {
                if (yellowCure) {
                    System.out.println("Yellow disease has already been cured.");
                    return false;
                }
                yellowCure = true;
                cureFound[YELLOW_CUBE] = true;
            }
            case "black" -> {
                if (blackCure) {
                    System.out.println("Black disease has already been cured.");
                    return false;
                }
                blackCure = true;
                cureFound[BLACK_CUBE] = true;
            }
            default -> {
                System.out.println("Please type in a valid color: Blue, yellow, red or black.\nTry another action.");
                return false;
            }
        }
        System.out.println("Disease successfully solved.");
        foundCure++;
        return true;
    }


    //----------------------------------------------------------- Nested Agent Class ------------------------------------------------------------------

    /**
     * Class for the Agent, runs background calculations and gives desired output.
     */
    public class AgentAssist extends Thread {

    }
}
