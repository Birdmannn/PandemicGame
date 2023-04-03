/**
 * This is a very robust code for the console version of the Pandemic game. It is a cooperative game between users
 * by which users work together to win the game. The game is lost when either the player deck finishes, the disease
 * cubes finishes, or there is an eighth outbreak. This class makes use of fullMap.txt and PandemicGameInfo.txt
 */

// Import all files, File for reading from an external file,
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class PandemicGame {
    // Global declaration and initialization of variables used in the game.
    private static Scanner shellInput;                  // For the user's input (super), for actions.
    private static boolean shellOpen = false;
    private static boolean useAgent = false;
    private static SimpleAgent agentHandler;            // Won't start until initialized by user.
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
    private static final int AGENT = 16;
    private static final int SOLVE_DISEASE = 17;

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
    private static PandemicHand infectionDiscardPile = new PandemicHand();
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
        return switch (inputString) {
            case "quit", "exit", "out" -> QUIT;
            case "location", "where am i", "my location" -> PRINT_LOCATION;
            case "cities", "available cities", "get cities", "print cities" -> PRINT_CITIES;
            case "connections", "get connections", "print connections" -> PRINT_CONNECTIONS;
            case "adjacent", "city links", "links" -> PRINT_ADJACENT_CITIES;
            case "infections", "infected cities" -> PRINT_DISEASES;
            case "move", "travel" -> MOVE;
            case "treat disease", "cure disease" -> TREAT_DISEASE;
            case "actions", "help" -> PRINT_ACTIONS;
            case "print cards", "hand" -> PRINT_CARDS;
            case "get status", "status" -> GET_STATUS;
            case "direct flight" -> DIRECT_FLIGHT;
            case "charter flight" -> CHARTER_FLIGHT;
            case "shuttle flight" -> SHUTTLE_FLIGHT;
            case "build research" -> BUILD_RESEARCH;
            case "play event", "event" -> PLAY_EVENT;
            case "agent" -> AGENT;
            case "solve disease" -> SOLVE_DISEASE;
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

    // Print Adjacent cities associated with the users current location.
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
        System.out.println("Agent -- Use agent and get extra help and services.");
        System.out.println("Solve disease -- Turn in 5 cards of the same color in a research station to cure a disease of that color.");
        System.out.println();
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
                gameEnded(false,"Quit.");
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
            case AGENT -> useAgent();
            case SOLVE_DISEASE -> doSolveDisease();
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

        for(int connectionNumber = 0; connectionNumber < numberConnections; connectionNumber++) {
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

    // Print the list of infected cities for all 48 cities if the city is found in the diseased
    // cubes list. Print city if checkSearch is false.
    private static void printInfectedCities() {
        for (int cityNumber = 0; cityNumber < 48; cityNumber++) {
            if (checkSearch(cityNumber)) {
                System.out.println(cities[cityNumber] + " has been infected.");
                printDiseaseInCity(cityNumber);
            }
        }
    }

    // Check if the specified city number is amongst the disease cube list. Return false if city
    // is not found.
    private static boolean checkSearch(int cityNumber) {
        for (int diseaseCubeCity : diseaseCubeCities) {
            if (cityNumber == diseaseCubeCity) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initialize the number of users. Takes in from one to four users Handle all errors in this method.
     * Read comments.
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
                    System.out.println("Please input a valid number.");     // Catch error and loop
                }
            }

            if (noOfUsers >= 1 && noOfUsers <= MAX_USERS) {
                NUMBER_USERS = noOfUsers;
                userLocation = new int[NUMBER_USERS];
                usernames = new String[NUMBER_USERS];      // Create an array to store the user's usernames.
                System.out.println("Type in your usernames. Press the 'Return' key to use default.");

                // Start getting in usernames
                subLoop: while (position < usernames.length) {
                    System.out.print("User " + (position + 1) + ": ");
                    String username = in.nextLine();
                    // If users presses the return key, a default username is set.
                    if (username.trim().length() == 0) {
                        username = "User" + (position + 1);
                    }

                    // Store username in usernames
                    usernames[position] = username;
                    // Now iterate the whole loop if there is more than one user, to make sure there are no
                    // two users of the same username.
                    if (position > 0 && position < usernames.length) {
                        for(int iterator = 0; iterator < position; iterator++) {
                            if (usernames[position].equalsIgnoreCase(usernames[iterator])) {
                                System.out.println("Two users can not have the same username.");
                                continue subLoop;
                            }
                        }
                    }
                    // Now the user has passed the test. Increase the position by 1.
                    position++;
                }

                // Welcome all users to the game.
                System.out.print("Welcome ");
                if (usernames.length > 1) {
                    for(int iterator = 0; iterator < usernames.length; iterator++) {
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
            // Here, the user has inputted an integer that is not in the required range. Repeat
            // the initial loop.
            System.out.println("Please input a valid number from 1 - " + MAX_USERS);
        }
    }

    /**
     * The main routine which the game runs from.
     * @param args
     */
    public static void main(String[] args) {
        boolean gameDone = false;
        // Greetings "Hello!" (In Japanese) :)
        System.out.println("Konnichiwaa!");
        System.out.println();
        readAndPrintGameInfo();

        try {
            getUsers();
            readCityGraph();
            initializeGame();
        }
        catch (Exception e) {
            System.out.println("Error in city graph.");
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
                    System.out.println("No card matches your current city. Please perform another action.\n");
                    return;
                }
            }
            System.out.println();
            // Increment the number of research stations built so far
            researchCt++;
        }
    }

    // Start up the game and setting the data and setting up the board.
    private static void initializeGame() {
        initializeData();
        setUpBoard();
    }

    // Prints the current status if the city cityNumber
    private static void checkCityStatus(int cityNumber) {
        getResearchInfo(cityNumber);
        printDiseaseInCity(cityNumber);
    }

    // Set up data for playing the game and keeping track of activities.
    private static void initializeData() {
        Arrays.fill(diseaseCubeCities, -1);
        Arrays.fill(researchStation, -1);
        researchStation[0] = 0;                 // There is one research station in Atlanta at the start of the game.
        Arrays.fill(remainingCubes, 24);
        researchCt++;
        System.out.println("Data initialized.\n");
    }

    // Search if a city has a research station.
    private static void getResearchInfo(int cityNumber) {
        boolean research = false;

        for (int researchCity : researchStation) {
            if (researchCity == cityNumber) {
                research = true;    // Research station found in city.
                break;
            }
        }

        if (research)
            System.out.println("There is a research station in " + cities[cityNumber]);
        else
            System.out.println("There is no research station in " + cities[cityNumber]);
        System.out.println();
    }

    // Counts the disease cubes in a specified city and prints out the total and each total
    // of each cube color.
    private static void printDiseaseInCity(int cityNumber) {
        int totalDiseaseCubes = countDiseaseCubes(cityNumber);  // Count disease cubes.
        String city = cities[cityNumber];
        System.out.print("The number of cubes in " + city + ": ");
        if (blueCt == 0 && yellowCt == 0 && redCt == 0 && blackCt == 0)
            System.out.println("0");
        else
            System.out.println("" + totalDiseaseCubes + "\nBlue Disease: " + blueCt + ", Yellow Disease: "
                    + yellowCt + ",  Red Disease: " + redCt + ", Black Disease: " + blackCt);
    }

    // The Disease cube counting method.
    // initializes all cube color count, each to zero, then starts searching through the diseaseCubeCities
    // array.
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
        // Return total. Note: all count color variables can be accessed as global variables.
        return blueCt + yellowCt + redCt + blackCt;
    }

    /**
     * Infects city of cityNumber with noOfCubes of cubeColor
     * @param cityNumber
     * @param noOfCubes
     * @param cubeColor
     * @throws IllegalArgumentException
     */
    private static void createInfection(int cityNumber, int noOfCubes, int cubeColor) {
        // Cube color must be in the appropriate range, else throw an error
        if (cubeColor >= BLUE_CUBE && cubeColor <= BLACK_CUBE) {
            // Check remaining disease cubes of the specified color; If exhausted, end the game.
            if (remainingCubes[cubeColor] == 0) {
                String info = "Out of " + color[cubeColor] + " disease cubes.";
                gameEnded(false, info);
            }
            else {
                // A constant would be needed for each color for some calculation.
                int cubeEnd = switch (cubeColor) {
                    case BLUE_CUBE -> 24;
                    case YELLOW_CUBE -> 48;
                    case RED_CUBE -> 72;
                    default -> 96;
                };

                for(int iterator = 0; iterator < noOfCubes; iterator++) {
                    diseaseCubeCities[cubeEnd - remainingCubes[cubeColor]] = cityNumber;
                    remainingCubes[cubeColor]--;
                }
                String word = noOfCubes != 1 ? "cubes" : "cube";    // lol, Good English.
                System.out.println(cities[cityNumber] + " is infected with " + noOfCubes + " " +
                        color[cubeColor] + " " + word + ".");
            }
        }
        else
            throw new IllegalArgumentException("A problem occurred whilst creating infection.");
    }

    // Check if an outbreak will occur in a city if when noOfCubes, added to the already existing
    // cubes, will be greater than 3 cubes in total.
    private static boolean checkOutbreak(int cityNumber, int noOfCubes, int cubeEnd) {
        int count = 0;

        for(int iterator = cubeEnd - 24; iterator < cubeEnd; iterator++)
            if (diseaseCubeCities[iterator] == cityNumber)
                count++;

        return (count + noOfCubes > 3);
    }

    // Initialization stage. Infect cities drawn from the infectionDeck.
    private static void infectCities() {
        // Player deck includes epidemic cards and the number of epidemic cards.
        // The Deck class sees that only player decks include epidemics, thus it adds
        // the Special Event Cards only when includeEpidemic is true.
        playerDeck = new PandemicDeck(true, 4);
        // Create and Shuffle the infection deck.
        infectionDeck = new PandemicDeck();
        infectionDeck.shuffle();
        userHand = new PandemicHand[NUMBER_USERS];


        for(int userNumber = 0; userNumber < NUMBER_USERS; userNumber++)
            userHand[userNumber] = new PandemicHand();

        infectionDiscardPile = new PandemicHand();  // for keeping the dealt infection cards

        // Deal Nine cards from the infection deck. This is one of the things that are done
        // at the start of the game.
        for(int cardCount = 0; cardCount < 9; cardCount++)
            infectionDiscardPile.addCard(infectionDeck.dealCard());

        // Infect all nine cities shown on the nine infection cards
        for(int cardCount = 0; cardCount < 6; cardCount++) {
            // The first 3 cities with 3 cubes each, and the second 3 with 2 each.
            int cubeValue = cardCount < 3 ? 3 : 2;
            int value = infectionDiscardPile.getCard(cardCount).getValue();
            int color = infectionDiscardPile.getCard(cardCount).getAttribute();
            createInfection(value, cubeValue, color);
        }

        // Infect the last 3 with 1 cube each.
        for(int cardCount = 6; cardCount < 9; cardCount++) {
            createInfection(infectionDiscardPile.getCard(cardCount).getValue(), 1,
                    infectionDiscardPile.getCard(cardCount).getAttribute());
        }
        System.out.println("Cities infected successfully.");
    }

    /**
     * @param won end the game when the boolean value is false
     * @param info and print out the description on what occurred.
     */
    private static void gameEnded(boolean won, String info) {
        System.out.println("Game over.\n");
        if (won)
            System.out.print("Game won! ");
        else
            System.out.print("Game lost. ");
        System.out.println(info);
        gameOver = true;
    }

    // Set up the Board by infecting cities and dealing the initial cards.
    private static void setUpBoard() {
        // Debugging phase
        infectCities();
        dealInitialCards();
        System.out.println("Type \"actions\" to view actions.\n");
    }

    /**
     * Draw Player Card. The second to the last move in a given turn.
     * First check if the deck is empty. Could check last after every draw, but there is a thin chance the game can still
     * be won before the next drawing of cards.
     * End the game immediately if player deck is empty.
     */
    private static void drawPlayerCard() {
        if (playerDeck.isEmpty()) {
            String info = "Can't draw cards -- Deck is empty.";
            gameEnded(false, info);
        }
        else {
            // User has reached the maximum card limit
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
                                removeCard(cardNumber - 1);     // Discard card.
                                break mainLoop;
                            }
                            System.out.println("Please input a value between 1 and " + userHand[currentUser].getCardCount());
                        }
                        catch (Exception e) {
                            System.out.println("Illegal input found. Please input a valid card number from 1 to "
                                    + userHand[currentUser].getCardCount() + "\n");
                        }
                    }
                }
            }

            // Now, user hand size is less than 7 :)
            PandemicCard card = playerDeck.dealCard();
            // Check the type of card, whether Epidemic or a user playing card
            // execute epidemics and add card to the infection discard pile, or add
            // card to the user hand.
            if (card.getValue() >= 52 && card.getValue() <= 55) {
                // Card is an Epidemic card.
                System.out.println("Card Drawn: Epidemic.");
                drawEpidemicCard(card);
            }
            else {
                userHand[currentUser].addCard(card);
                if (card.getAttribute() == PandemicCard.EVENT_CARD) {
                    System.out.println("Card Drawn: Event.");
                }
                else {
                    System.out.println("Card Drawn: " + cities[card.getValue()] + " " + card.getAttributeAsString() + ".");
                }
            }
        }
    }

    // Method for Discarding cards from a specified position in the user's hand.
    private static void removeCard(int position) {
        userHand[currentUser].removeCard(position);
    }

    // Print all the cards available in the user's hand.
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

    /**
     * Play Event card. Take a card Number from the list of cards in the user's hand.
     * Check if it is an event card, if not, tell user to do another action and return.
     */
    private static void playEvent() {
        Scanner in = new Scanner(System.in);
        System.out.println("Card position: ");
        String cardName;        // Name of card. Can also use a Global String array.

        int cardPosition;
        // Catch errors and loop for non-integers and for integers that are out of range.
        while(true) {
            try {
                cardPosition = in.nextInt();
                if (cardPosition >= 1 && cardPosition <= userHand[currentUser].getCardCount())
                    break;

                System.out.println("Please input a valid number from 1 to " + userHand[currentUser].getCardCount());
            }
            catch (Exception e) {
                System.out.print("Illegal input. Please put a valid input.\n? ");
            }
        }

        PandemicCard card = userHand[currentUser].getCard(cardPosition - 1);
        if (card.getAttribute() != PandemicCard.EVENT_CARD) {
            System.out.println("Card is not an event card. Please perform another action.");
        }
        else {
            // Two loops for the ones that require a correct value from the user
            int cityNumber = -1;
            switch (card.getValue()) {
                case PandemicCard.FLY_ANYWHERE -> {
                    while (cityNumber < 0) {
                        cityNumber = doFlyAnywhere();  // returns the value of the city Number.
                        if (cityNumber < 0) {
                            System.out.println("Type in a valid city.");
                        }
                    }
                    cardName = "FLY ANYWHERE";
                    userLocation[currentUser] = cityNumber;     // Place user in a new city.
                }
                case PandemicCard.BUILD_R_ANYWHERE -> {
                    if (researchCt == 6) {
                        System.out.println("Research station has reached its limit.");
                        return;
                    }
                    while (cityNumber < 0) {
                        cityNumber = doBuildResearch();
                        if (cityNumber < 0) {
                            System.out.println("Invalid. Please type in a valid city.");
                        }
                    }
                    cardName = "BUILD RESEARCH ANYWHERE";
                    buildResearchStation(cityNumber, true);
                }
                case PandemicCard.SOLVE_DISEASE -> {
                    freeCure = true;
                    cardName = "SOLVE DISEASE";
                    doSolveDisease();
                }
                default -> {
                    addMove = true;
                    cardName = "ADD MOVE";
                }
            }
            System.out.println(card.getAttributeAsString() + ": " + cardName + " played successfully.\n");
            removeCard(cardPosition - 1);    // Discard the card.
            checkAndCountActions();
        }
    }

    // Part of the initialization. Deals the initial player cards to the user
    // The number of cards dealt to each user depends on the total number of users playing the game.
    private static void dealInitialCards() {
        PandemicHand hand = new PandemicHand();
        assert !playerDeck.isEmpty();

        //initialize userHand
        for (int user = 0; user < NUMBER_USERS; user++)
            userHand[user] = new PandemicHand();

        for(int iterator = 0; iterator < 4; iterator++)
            hand.addCard(playerDeck.removeBottom());

        playerDeck.shuffle();
        // Number of cards to be dealt based on the number of users.
        int noOfCards = switch (NUMBER_USERS) {
            case 1, 2 -> 4;
            case 3 -> 3;
            default -> 2;
        };

        for(int userNumber = 0; userNumber < NUMBER_USERS; userNumber++)
            for(int cardCt = 0; cardCt < noOfCards; cardCt++)
                userHand[userNumber].addCard(playerDeck.dealCard());
        // After dealing cards to the players, create the epidemic deck.
        System.out.println("Cards dealt to user(s).");

        createEpidemicDeck(hand);
    }

    /**
     * Draw an epidemic card. Epidemic cards are not added to the user's hand, thus they are handled,
     * executed -- Infection card(s) is/are drawn, put in the infection discard pile, shuffled and stacked back on top of the infection
     * deck.
     * @param card
     */
    private static void drawEpidemicCard(PandemicCard card) {
        PandemicHand hand = new PandemicHand();     // create a temporary hand to hold the epidemic card
        hand.addCard(card);     // add the card to the hand
        PandemicCard infectionCard = infectionDeck.removeBottom();

        int value = infectionCard.getValue();
        String city = cities[value];
        System.out.println("Infection card drawn from deck bottom. Card: " + city + " " + infectionCard.getAttributeAsString());
        doEpidemics(value, 3, infectionCard.getAttribute());
        infectionDiscardPile.addCard(infectionCard);
        infectionDiscardPile.shuffle();     // shuffle the pile after epidemics has been executed.

        // For each card in the pile, stack all back to the top of the deck.
        for (PandemicCard cardToStack : infectionDiscardPile.getCardArray()) {
            infectionDeck.addToDeck(cardToStack);
        }
        // Clear the pile for new drawn infection cards in the future
        infectionDiscardPile.clear();
        // Intensify! The number of infection cards drawn at the end of each turn is now (+1)
        infectionRate++;
    }

    /**
     * Do epidemics anytime an infection card is drawn
     * @param cityNumber in which the epidemic should occur
     * @param noOfCubes the number of cubes that should be added to the specified city
     * @param cubeColor the color of cube(s)
     */
    private static void doEpidemics(int cityNumber, int noOfCubes, int cubeColor) {
        // Crosscheck if really an epidemic should occur.
        if ((cubeColor != BLUE_CUBE && !blueCure) || (cubeColor != YELLOW_CUBE && !yellowCure) ||
                (cubeColor != RED_CUBE && !redCure) || (cubeColor != 3 && !blackCure)) {

            // For setting the end array for a particular cube.
            int cubeEnd = switch (cubeColor) {
                case BLUE_CUBE -> 24;
                case YELLOW_CUBE -> 48;
                case RED_CUBE -> 72;
                default -> 96;
            };

            // Check if an outbreak would occur. If yes, do the outbreak
            // If no, just add the required number of cubes to the city
            if (checkOutbreak(cityNumber, noOfCubes, cubeEnd))
                doOutbreak(cityNumber, cubeColor);
            else
                createInfection(cityNumber, noOfCubes, cubeColor);
        }
        else
            //Disease has already been cured. Return.
            System.out.println(color[cubeColor] + " disease/cube has already been cured.");
    }

    // The number of infection cards drawn depends on the infection rate
    private static void drawInfectionCard() {
        String word = infectionRate > 1 ? "cards" : "card";
        System.out.println("Infection " + word + " drawn.");
        for(int cardCt = 0; cardCt < infectionRate; cardCt++) {
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
        System.out.println();
    }

    /**
     * In real life, deck is cut into four "equal" halves, then the epidemic cards are added to the top
     * of each deck. All four decks are shuffled independently, and stacked together to form the player
     * deck. But here, we shuffle the deck, and add each epidemic card between four random slots at the
     * the current deck size divided by four, for 0 <= iterator < 4
     * @param hand
     */
    private static void createEpidemicDeck(PandemicHand hand) {
        playerDeck.shuffle();
        int currentDeckSize = playerDeck.size()/4;
        for (int iterator = 0; iterator < 4; iterator++) {
            int random = (int)(currentDeckSize*Math.random());
            playerDeck.addCard((iterator*currentDeckSize) + random,hand.getCard(iterator));
        }
        System.out.println("Epidemic Deck has been created.\n");
    }

    /**
     * Move to a city on a card by discarding the matching card from your hand.
     */
    private static void doDirectFlight(PandemicCard card, int cardNumber) {
        userLocation[currentUser] = card.getValue();

        System.out.println();
        System.out.println("Direct Flight Successful.");
        System.out.println("You're now in " + cities[userLocation[currentUser]]);
        removeCard(cardNumber - 1);     // discard
        checkAndCountActions();
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
                removeCard(cardNumber - 1);         // Discard card
                checkAndCountActions();         // Counts Action
                return;
            }

            System.out.println("Please input a valid city.");
        }
    }


    /**
     * Process the card to handle all user errors. Work only on charter flight and direct flight for true and false
     * respectively. Parameter below.
     * @param doCharterFlight
     */
    private static void processActionCard(boolean doCharterFlight) {
        Scanner in = new Scanner(System.in);
        System.out.println("\nThese are the current cards in hand.");
        // Show the user all the cards to pick from.
        // In more advanced systems, only playable cards for the operation are printed.
        printAllCards();
        System.out.print("Please type in the card number. type '99' to return.");
        PandemicCard card;
        int cardNumber;
        try {
            do {
                do {
                    cardNumber = in.nextInt();
                    if (cardNumber == 99)
                        return;
                    if (cardNumber < 1 || cardNumber > userHand[currentUser].getCardCount()) {
                        System.out.print("Please type a valid card Number: ");
                    }
                } while (cardNumber < 1);
            } while (cardNumber > userHand[currentUser].getCardCount());

            // Card is not a city card, return.
            card = userHand[currentUser].getCard(cardNumber - 1);
            if (card.getAttribute() == PandemicCard.EVENT_CARD) {
                System.out.println("Card is an event card. You need a city card. Perform another action.");
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

    // Checks if the current user is in a city that contains a research station. You can cure diseases only
    // in a research station.
    private static void doSolveDisease() {
        Scanner in = new Scanner(System.in);
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
            String city = cities[userLocation[currentUser]];
            System.out.println("You're in " + city + ". " + city + " does not have a research station.");
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

    // Check if a specified disease cube color has been cured, if not, cure them by setting all the
    // boolean variables corresponding to the color to true.
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

    // Makes a little advanced agent resources and abilities available to the user.
    // If user never uses agent, agent is never available.
    private static void useAgent() {
        Scanner in = new Scanner(System.in);
        System.out.print("Yes? ");
        if(! useAgent) {
            agentHandler = new SimpleAgent();
            String agentName;
            System.out.println("Initialize a name for the Agent: ");
            agentName = in.next();
            System.out.println("Successful @" + agentName.toUpperCase());
            agentHandler.setAgentName(agentName);
            useAgent = true;
        }
        // After the agent object has been created above, use the input feature here.
        agentHandler.inputCommand();
    }
    //----------------------------------------------------------- Nested Agent Class ------------------------------------------------------------------

    /**
     * Extends thread to run background calculations and give output. Works of an agent can be found in some
     * methods written above. But there are some in which the run() method has to keep running and updating the
     * values of the private parameters. Conversation is made simpler in this class.
     */
    public static class SimpleAgent extends Thread {

        Scanner in = new Scanner(System.in);        //For in-agent conversation activities.
        // Search through the city and user hand card and search for cubes in the cities. Print out
        // danger zones and safe zones and possibly advice on where to move next based on the cards
        // in the user's hand.

        // research station location
        // route to a particular city
        private LinkedList<Integer> dangerZones = new LinkedList<>();
        private LinkedList<Integer> safeZones = new LinkedList<>();
        private Set<Integer> removedCards = new HashSet<>();
        public String agentName;        // Check.
        private PandemicDeck deckCopy;        // A pointer to the player deck.
        private int zonesCount = 0;

        // Constructor
        public SimpleAgent() {
            this.start();       // Start thread immediately agent is initialized.
        }
        public void setAgentName(String name) {
            agentName = Objects.requireNonNullElse(name, "Lily");
            agentName = agentName.toUpperCase();
        }

        public String printCardStatus(int size) {
            String initialInfo = "    @" + agentName + " ";
            String outputInfo;
            switch (size) {
                case 5 -> {
                    this.interrupt();
                    outputInfo = "Critical condition!" + " Number of cards left: 5.";
                }
                case 10 -> {
                    this.interrupt();
                    outputInfo =  "Cards reducing. Cards left: 10";
                }
                case 30 -> {
                    this.interrupt();
                    outputInfo = "Half of Deck reached, play carefully.";
                }
                default -> outputInfo = "...";
            }
            return initialInfo + outputInfo;
        }


        /**
         * Input String from the user, Process all user's input.
         * This is a sample class method -- Won't give an accurate answer to all user questions. Just finds Keywords
         * in the user's input and responds appropriately to them.
         */
        public void inputCommand() {
            // Add more commands like asking
            // Make this recursive.
            String[] keyWords = {"probability", "percentage", "card", "danger", "status", "Where", "am", "I",
                    "location", "quit", "exit", "go", "out", "leave", "odds"};
            System.out.println("    " + agentName + " here. How may I be of help?");
            // go location,
            String userInput = in.nextLine();
            String words = userInput.toLowerCase();

            if (words.contains(keyWords[9]) || (words.contains(keyWords[10]) && words.contains(keyWords[11]))
             || words.contains(keyWords[12]) || words.contains(keyWords[13])) {
                System.out.println("    I hope you request next time :) @" + usernames[currentUser]);
                System.out.println("    Exiting Agent...");
            }
            else {
                if (words.contains(keyWords[0]) || (words.contains(keyWords[0]) && words.contains(keyWords[2])) ||
                 words.contains(keyWords[14])) {
                    // search if any of the strings contains a city
                    doProbability(words);

                }
                else if ((words.contains(keyWords[5]) && words.contains(keyWords[6]) && words.contains(keyWords[7]))
                        || words.contains(keyWords[8])) {
                    printUserLocations();
                }
                else if (words.contains("deck") && words.contains("size")) {
                    System.out.println("Cards in deck: " + playerDeck.size());
                }
                else {
                    System.out.println("    @" + agentName + " can't understand your input :(");
                    // Try something?
                }
                System.out.println("What else can I help you with? ");
                inputCommand();
            }
        }

        public void doProbability(String words) {
            boolean found = false; int cityCount = 0;
            System.out.println("Testing: Input only city cards.");
            for (String city : cities) {
                city = city.toLowerCase();
                if (words.contains(city)) {
                    found = true;
                    break;
                }
                cityCount++;
            }
            if (found)
                getProbability(cityCount);
            else {
                System.out.println("    @" + agentName + " It's either a city name can't be found on input.");
                System.out.println("    @" + agentName + " Hint: try \"odds or probability a Atlanta card shows up.\"");
            }
        }
        // Probability to get a given card
        public void getProbability(int cardNumber) {
            // Write probability definition here
            deckCopy = playerDeck;      // pointer to playerDeck.
            List<Integer> value = new ArrayList<>();

            for (int i = 0; i < deckCopy.size(); i++) {
                value.add(deckCopy.dealCard().getValue());
            }
            System.out.println("     Disclaimer: The Probability is not 100% correct.");
            System.out.println("Getting Probability...");

            int count = 0;
            for (int iterator = 0; iterator < 10000; iterator++) {
               Collections.shuffle(value, new Random());
               if (value.indexOf(cardNumber) == 0) {
                   count++;
               }
            }

            double probability = (double)count/10000.0;
            System.out.println("Probability of drawing card: " + probability);
        }

        public void run() {

            while (true) {
                if(playerDeck.size() == 5 || playerDeck.size() == 10 || playerDeck.size() == 30)
                    printCardStatus(playerDeck.size());
                checkZones();
                try {
                    Thread.sleep(8000);
                }
                catch (InterruptedException ignored) {}
            }
        }

        // Method to scan various arrays to locate disease and possible outbreaks, and advice on
        // cards to be played. Adds to the danger zones and safe zones, and prints them out, scans
        // The current user's card and tells the user the most preferable card to play, or if the
        // user should discard a card and draw for a lucky chance.
        public void checkZones() {
            int count = 0;
            int cityNumber = 0;
            while (cityNumber < 48) {
                subLoop: for (int iterator = 0; iterator < diseaseCubeCities.length; iterator++) {
                    if (diseaseCubeCities[iterator] == cityNumber)
                        count++;    // check for danger.
                    if (count == 3) {
                        // once it's up to three, then it is a danger zone
                        dangerZones.add(cityNumber);
                        break subLoop;
                    }
                    else
                        safeZones.add(cityNumber);
                }
                cityNumber++;
                count = 0;
                zonesCount++;
            }
            if (zonesCount > 25 && playerDeck.size() < 24)
                printStatusAndPreferableMove();
        }

        // Traverse all array positions in user hand, in danger zones and in adjacent cities.
        public void printStatusAndPreferableMove() {
            if(dangerZones.size() > safeZones.size()*3) {
                System.out.println("Disease spread in critical condition.");
            }
            // for all user cards, for all danger zones, and for all adjacent cities...
            mainLoop: for (PandemicCard card : userHand[currentUser].getCardArray()) {
                if(card.getAttribute() != PandemicCard.EVENT_CARD) {
                    for (int zone : dangerZones) {
                        if (card.getValue() != zone) {
                            System.out.println(cities[card.getValue()] + " " + card.getAttributeAsString() + ": recommended.");
                            break mainLoop;
                        }
                    }
                }
            }
        }

        // Create planning
    }
}
