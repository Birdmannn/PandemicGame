//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

public class PandemicGame {
    private static Scanner shellInput;
    private static boolean shellOpen = false;
    private static Random randomGenerator = new Random(1L);
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
    private static int addAction = 0;
    private static int[] researchStation = new int[6];
    private static int[] diseaseCubeCities = new int[96];
    private static int[] remainingCubes = new int[4];
    private static int researchCt = 0;
    private static final int BLUE_CUBE = 0;
    private static final int YELLOW_CUBE = 1;
    private static final int RED_CUBE = 2;
    private static final int BLACK_CUBE = 3;
    private static final int MAX_HAND_SIZE = 7;
    private static PandemicDeck playerDeck;
    private static PandemicDeck infectionDeck;
    private static PandemicHand[] userHand;
    private static PandemicHand temp;
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
    private static boolean addMove = false;

    public PandemicGame() {
    }

    private static int processUserInput(String inputString) {
        byte var10000;
        switch (inputString.toLowerCase().trim()) {
            case "quit":
                var10000 = 0;
                break;
            case "location":
                var10000 = 1;
                break;
            case "cities":
                var10000 = 4;
                break;
            case "connections":
                var10000 = 5;
                break;
            case "adjacent":
                var10000 = 6;
                break;
            case "infections":
                var10000 = 7;
                break;
            case "move":
                var10000 = 2;
                break;
            case "remove":
                var10000 = 8;
                break;
            case "actions":
            case "help":
                var10000 = 3;
                break;
            case "print cards":
                var10000 = 9;
                break;
            case "get status":
                var10000 = 10;
                break;
            case "direct flight":
                var10000 = 11;
                break;
            case "charter flight":
                var10000 = 12;
                break;
            case "shuttle flight":
                var10000 = 13;
                break;
            case "build research":
                var10000 = 14;
                break;
            case "play event":
                var10000 = 15;
                break;
            default:
                var10000 = -1;
        }

        return var10000;
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
            } else {
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
            case 0:
                return true;
            case 1:
                printUserLocations();
                break;
            case 2:
                moveUser();
                checkAndCountActions();
                break;
            case 3:
                printActions();
                break;
            case 4:
                printCities();
                break;
            case 5:
                printConnections();
                break;
            case 6:
                printAdjacentCities();
                break;
            case 7:
                printInfectedCities();
                break;
            case 8:
                if (removeCube()) {
                    checkAndCountActions();
                }
                break;
            case 9:
                printAllCards();
                break;
            case 10:
                checkCityStatus(userLocation[currentUser]);
                break;
            case 11:
                doDirectFlight();
                break;
            case 12:
                doCharterFlight();
                break;
            case 13:
                doShuttleFlight();
                break;
            case 14:
                buildResearchStation(userLocation[currentUser]);
                break;
            case 15:
                playEvent();
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

    private static boolean removeCube() {
        int currentUserLocation = userLocation[currentUser];
        if (diseaseCubes[currentUserLocation] > 0) {
            int var10002 = diseaseCubes[currentUserLocation]--;
            int var10001 = diseaseCubes[currentUserLocation];
            System.out.println("There are " + var10001 + " left");
            return true;
        } else {
            System.out.println("The space you're on has no disease cubes.");
            return false;
        }
    }

    private static void checkAndCountActions() {
        int maxCheck = addMove ? 4 : 3;
        ++countCheck;
        if (countCheck == maxCheck) {
            System.out.println("You've reached your maximum number of actions.");
            countCheck = 0;
            actionDone();
        } else {
            System.out.println("Note: You can perform a maximum of four actions.");
            System.out.println("Number of actions left: " + (maxCheck - countCheck));
            if (addMove) {
                System.out.println(usernames[currentUser] + " perform another action.");
                addMove = false;
            } else {
                drawPlayerCard();
                drawInfectionCard();
                actionDone();
            }
        }
    }

    private static void actionDone() {
        ++currentUser;
        currentUser %= NUMBER_USERS;
        System.out.println("It's now " + usernames[currentUser] + "'s turn.");
    }

    private static void readCities(int numCities, Scanner in) {
        for(int cityNumber = 0; cityNumber < numCities; ++cityNumber) {
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
            File fileHandle = new File("C:\\Users\\DELL\\fullMap.txt");
            Scanner mapFileReader = new Scanner(fileHandle);
            numberCities = mapFileReader.nextInt();
            String data = mapFileReader.nextLine();
            cities = new String[numberCities];
            diseaseCubes = new int[numberCities];
            numberConnections = mapFileReader.nextInt();
            data = mapFileReader.nextLine();
            connections = new int[2][numberConnections];
            readCities(numberCities, mapFileReader);
            readConnections(numberConnections, mapFileReader);
            mapFileReader.close();
        } catch (FileNotFoundException var3) {
            System.out.println("An error occurred reading the city graph.");
            var3.printStackTrace();
        }

    }

    private static void printInfectedCities() {
        for(int cityNumber = 0; cityNumber < numberCities; ++cityNumber) {
            if (diseaseCubes[cityNumber] > 0) {
                String var10001 = cities[cityNumber];
                System.out.println(var10001 + " has " + diseaseCubes[cityNumber] + " cubes.");
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
                } catch (NumberFormatException var5) {
                    System.out.println("Please input a valid number.");
                }
            }

            if (noOfUsers >= 1 && noOfUsers <= 4) {
                NUMBER_USERS = noOfUsers;
                userLocation = new int[NUMBER_USERS];
                usernames = new String[noOfUsers];
                System.out.println("Type in your usernames. Press the 'Return' key to use default.");

                label57:
                while(i < usernames.length) {
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
                                continue label57;
                            }
                        }
                    }

                    ++i;
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
        } catch (Exception var4) {
            System.out.println(var4.getMessage());
        }

        while(!gameDone || !gameOver) {
            try {
                int userInput = getUserInput();
                gameDone = processUserCommand(userInput);
            } catch (Exception var3) {
                System.out.println(var3.getMessage());
            }
        }

        System.out.println("Goodbye Pandemic Tester");
    }

    private static void buildResearchStation(int cityNumber) {
        if (researchCt == 6) {
            throw new IllegalStateException("Maximum number of Research Stations reached.");
        } else {
            int[] var1 = researchStation;
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                int i = var1[var3];
                if (i == cityNumber) {
                    System.out.println("You can't build a research station here because it already has one.");
                    return;
                }
            }

            researchStation[researchCt] = cityNumber;
            ++researchCt;
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
        researchStation[0] = 0;
        Arrays.fill(remainingCubes, 24);
        ++researchCt;
    }

    private static void getResearchInfo(int cityNumber) {
        boolean research = false;

        for(int i = 0; i < researchStation.length; ++i) {
            if (researchStation[i] == cityNumber) {
                research = true;
            }
        }

        String var10001;
        if (research) {
            var10001 = cities[cityNumber];
            System.out.println("There is a research station in " + var10001);
        } else {
            var10001 = cities[cityNumber];
            System.out.println("There is no research station in " + var10001);
        }

    }

    private static void printDiseaseInCity(int cityNumber) {
        countDiseaseCubes(cityNumber);
        int total = blueCt + yellowCt + redCt + blackCt;
        String var10001 = cities[cityNumber];
        System.out.print("The number of cubes in " + var10001 + ": ");
        if (blueCt == 0 && yellowCt == 0 && redCt == 0 && blackCt == 0) {
            System.out.print("0");
        } else {
            System.out.println("" + total + "\nBlue Disease: " + blueCt + ", Yellow Disease: " + yellowCt + ",  Red Disease: " + redCt + ", Black Disease: " + blackCt);
        }

    }

    private static void countDiseaseCubes(int cityNumber) {
        blueCt = 0;
        yellowCt = 0;
        redCt = 0;
        blackCt = 0;

        for(int i = 0; i < 96; ++i) {
            if (i < 24 && diseaseCubeCities[i] == cityNumber) {
                ++blueCt;
            } else if (i < 48 && diseaseCubeCities[i] == cityNumber) {
                ++yellowCt;
            } else if (i < 72 && diseaseCubeCities[i] == cityNumber) {
                ++redCt;
            } else if (diseaseCubeCities[i] == cityNumber) {
                ++blackCt;
            }
        }

    }

    private static void createInfection(int cityNumber, int noOfCubes, int cubeColor) {
        if (cubeColor >= 0 && cubeColor <= 3) {
            if (remainingCubes[cubeColor] == 0) {
                String info = "Out of disease cubes of a particular color.";
                gameEnded(false, info);
            } else {
                byte var10000;
                switch (cubeColor) {
                    case 0:
                        var10000 = 24;
                        break;
                    case 1:
                        var10000 = 48;
                        break;
                    case 2:
                        var10000 = 72;
                        break;
                    default:
                        var10000 = 96;
                }

                int cubeEnd = var10000;

                for(int i = 0; i < noOfCubes; ++i) {
                    diseaseCubeCities[cubeEnd - remainingCubes[0]] = cityNumber;
                    int var10002 = remainingCubes[cubeColor]--;
                }

            }
        } else {
            throw new IllegalArgumentException("Type in a number from 0 - 3");
        }
    }

    private static boolean checkOutbreak(int cityNumber, int noOfCubes, int cubeEnd) {
        int count = 0;

        for(int i = cubeEnd - 24; i < cubeEnd; ++i) {
            if (diseaseCubeCities[i] == cityNumber) {
                ++count;
            }
        }

        if (count + noOfCubes > 3) {
            return true;
        } else {
            return false;
        }
    }

    private static void infectCities() {
        playerDeck = new PandemicDeck(true, 4);
        infectionDeck = new PandemicDeck();
        infectionDeck.shuffle();
        userHand = new PandemicHand[NUMBER_USERS];

        int cubeValue;
        for(cubeValue = 0; cubeValue < NUMBER_USERS; ++cubeValue) {
            userHand[cubeValue] = new PandemicHand();
        }

        temp = new PandemicHand();

        for(cubeValue = 0; cubeValue < 9; ++cubeValue) {
            temp.addCard(infectionDeck.dealCard());
        }

        cubeValue = 0;

        int i;
        for(i = 0; i < 6; ++i) {
            int i1 = cubeValue < 3 ? 2 : 3;
            int value = temp.getCard(i).getValue();
            int color = temp.getCard(i).getAttribute();
            createInfection(value, i1, color);
            ++cubeValue;
        }

        for(i = 6; i < 9; ++i) {
            createInfection(temp.getCard(i).getValue(), 1, temp.getCard(i).getAttribute());
        }

    }

    private static void gameEnded(boolean won, String info) {
        System.out.print("Game over. ");
        if (won) {
            System.out.println("Game won!");
        } else {
            System.out.println("Game lost.");
        }

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
        } else {
            if (userHand[currentUser].getCardCount() == 7) {
                System.out.println("You've reached the number of cards limit in hand.\nYou'll have to discard a card. Starting card number 1.");
                printAllCards();
                Scanner in = new Scanner(System.in);

                label39:
                while(true) {
                    while(true) {
                        try {
                            System.out.print("? ");
                            int cardNumber = in.nextInt();
                            if (cardNumber >= 1 && cardNumber <= userHand[currentUser].getCardCount()) {
                                removeCard(cardNumber - 1);
                                break label39;
                            }

                            System.out.println("Please input a value between 1 and " + userHand[currentUser].getCardCount());
                        } catch (Exception var2) {
                            System.out.println("Illegal input found. Please input a valid nu");
                        }
                    }
                }
            }

            PandemicCard c = playerDeck.dealCard();
            if (c.getValue() >= 52 && c.getValue() <= 55) {
                drawEpidemicCard(c);
            } else {
                userHand[currentUser].addCard(c);
            }

        }
    }

    private static void removeCard(int position) {
        userHand[currentUser].removeCard(position);
    }

    private static void printAllCards() {
        Iterator var0 = userHand[currentUser].getCardArray().iterator();

        while(var0.hasNext()) {
            PandemicCard card = (PandemicCard)var0.next();
            if (card.getValue() < 48) {
                PrintStream var10000 = System.out;
                String var10001 = cities[card.getValue()];
                var10000.println("    " + var10001 + " " + card.getAttributeAsString());
            } else {
                switch (card.getValue()) {
                    case 48:
                        System.out.println("    " + card.getAttributeAsString() + ": Fly Anywhere.");
                        break;
                    case 49:
                        System.out.println("    " + card.getAttributeAsString() + ": Build Research station anywhere.");
                        break;
                    case 50:
                        System.out.println("    " + card.getAttributeAsString() + ": Solve Disease.");
                        break;
                    default:
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
            } catch (Exception var4) {
                System.out.print("Illegal input. Please put a valid input.\n? ");
            }
        }

        PandemicCard c = userHand[currentUser].getCard(cardPosition - 1);
        if (c.getAttribute() != 4) {
            System.out.println("Card is not an event card.");
        } else {
            int cityNumber = -1;
            switch (c.getValue()) {
                case 48:
                    while(cityNumber < 0) {
                        cityNumber = doFlyAnywhere();
                        if (cityNumber < 0) {
                            System.out.println("Type in a valid city number.");
                        }
                    }

                    userLocation[currentUser] = cityNumber;
                    break;
                case 49:
                    if (researchCt == 6) {
                        System.out.println("Research station has reached its limit. Card automatically discarded.");
                        return;
                    }

                    while(cityNumber < 0) {
                        cityNumber = doBuildResearch();
                        if (cityNumber < 0) {
                            System.out.println("Type in a valid city number.");
                        }
                    }

                    buildResearchStation(cityNumber);
                    break;
                case 50:
                    freeCure = true;
                    doSolveDisease(in);
                    break;
                default:
                    addMove = true;
            }

            checkAndCountActions();
        }
    }

    private static void dealInitialCards() {
        PandemicHand hand = new PandemicHand();

        for(int i = 0; i < 4; ++i) {
            hand.addCard(playerDeck.removeBottom());
        }

        playerDeck.shuffle();
        byte var10000;
        switch (NUMBER_USERS) {
            case 1:
                var10000 = 4;
                break;
            case 2:
                var10000 = 4;
                break;
            case 3:
                var10000 = 3;
                break;
            default:
                var10000 = 2;
        }

        int N = var10000;

        for(int i = 0; i < NUMBER_USERS; ++i) {
            for(int cardCt = 0; cardCt < N; ++cardCt) {
                userHand[i].addCard(playerDeck.dealCard());
            }
        }

        createEpidemicDeck(hand);
    }

    private static void drawEpidemicCard(PandemicCard c) {
        PandemicHand hand = new PandemicHand();
        hand.addCard(c);
        PandemicCard infectionCard = infectionDeck.removeBottom();
        System.out.println("Epidemic card drawn.");
        PrintStream var10000 = System.out;
        String var10001 = cities[infectionCard.getValue()];
        var10000.println("Infection card drawn from deck bottom. Card: " + var10001 + " " + infectionCard.getAttributeAsString());
        doEpidemics(infectionCard.getValue(), 3, infectionCard.getAttribute());
        temp.addCard(infectionCard);
        temp.shuffle();
        Iterator var3 = temp.getCardArray().iterator();

        while(var3.hasNext()) {
            PandemicCard card = (PandemicCard)var3.next();
            infectionDeck.addToDeck(card);
        }

        temp.clear();
        ++infectionRate;
    }

    private static void doEpidemics(int cityNumber, int noOfCubes, int cubeColor) {
        if ((cubeColor != 0 || !blueCure) && (cubeColor != 1 || !yellowCure) && (cubeColor != 2 || !redCure) && (cubeColor != 3 || !blackCure)) {
            byte var10000;
            switch (cubeColor) {
                case 0:
                    var10000 = 24;
                    break;
                case 1:
                    var10000 = 48;
                    break;
                case 2:
                    var10000 = 72;
                    break;
                default:
                    var10000 = 96;
            }

            int cubeEnd = var10000;
            if (checkOutbreak(cityNumber, noOfCubes, cubeEnd)) {
                doOutbreak(cityNumber, cubeColor);
            } else {
                createInfection(cityNumber, noOfCubes, cubeEnd);
            }

        } else {
            String var10001 = color[cubeColor];
            System.out.println(var10001 + " has already been cured");
        }
    }

    private static void drawInfectionCard() {
        for(int cardCt = 0; cardCt < infectionRate; ++cardCt) {
            PandemicCard c = infectionDeck.removeBottom();
            doEpidemics(c.getValue(), 1, c.getAttribute());
            temp.addCard(c);
        }

    }

    private static void doOutbreak(int outbreakCityNumber, int cubeColor) {
        ++outbreak;
        String var10001 = cities[outbreakCityNumber];
        System.out.println("An outbreak has just occurred in " + var10001);
        if (outbreak == 8) {
            String var10000 = cities[outbreakCityNumber];
            String info = "An eighth outbreak has occurred in " + var10000;
            gameEnded(false, info);
        }

        for(int cityNumber = 0; cityNumber < numberCities; ++cityNumber) {
            if (citiesAdjacent(outbreakCityNumber, cityNumber)) {
                doEpidemics(cityNumber, 1, cubeColor);
                int var10002 = diseaseCubes[cubeColor]--;
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
                } while(cardNumber < 1);
            } while(cardNumber > userHand[currentUser].getCardCount());

            PandemicCard card = userHand[currentUser].getCard(cardNumber);
            if (card.getAttribute() == 4) {
                System.out.println("Card is an event card. You need a city card to perform Charter Flight. Perform another action.");
                userHand[currentUser].addCard(cardNumber, card);
                return;
            }

            if (card.getValue() != userLocation[currentUser]) {
                System.out.println("Card does not match your current location. Please perform another action.");
                userHand[currentUser].addCard(cardNumber, card);
                return;
            }
        } catch (Exception var4) {
            System.out.println(var4.getMessage());
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
                String var10001 = usernames[currentUser];
                System.out.println(var10001 + " is now in " + cities[userLocation[currentUser]] + ".");
                checkAndCountActions();
                return;
            }

            System.out.println("Please input a valid city.");
        }
    }

    private static void doShuttleFlight() {
        printResearchCities();
        System.out.println("Which research station do you wish to fly to: ");
        int station = searchForCity();
        if (station == -1) {
            System.out.println("Research station city does not exist. ");
        } else {
            for(int city = 0; city < researchStation.length; ++city) {
                if (researchStation[city] == station) {
                    userLocation[currentUser] = station;
                    System.out.println("Shuttle flight to " + cities[userLocation[currentUser]] + " successful.");
                    checkAndCountActions();
                    return;
                }
            }

            String var10001 = cities[station];
            System.out.println(var10001 + " is not a city with a research station.");
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
        if (!freeCure) {
            PandemicHand cardArray = new PandemicHand();
            System.out.println("Type in all four card locations.");
            int userInput = 0;

            for(int cardCount = 0; cardCount < 5; ++cardCount) {
                do {
                    try {
                        System.out.print("? ");
                        userInput = in.nextInt();
                        cardArray.addCard(userHand[currentUser].getCard(userInput - 1));
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                } while(userInput < 1 || userInput > userHand[currentUser].getCardCount());
            }
        } else {
            System.out.println("Type in the color of disease you want to solve: ");
            switch (in.nextLine().toLowerCase()) {
                case "red":
                    if (redCure) {
                        System.out.println("Red disease has already been cured.");
                        return;
                    }

                    redCure = true;
                    break;
                case "blue":
                    if (blueCure) {
                        System.out.println("Blue disease has already been cured.");
                        return;
                    }

                    blueCure = true;
                    break;
                case "yellow":
                    if (yellowCure) {
                        System.out.println("Yellow disease has already been cured.");
                        return;
                    }

                    yellowCure = true;
                    break;
                case "black":
                    if (blackCure) {
                        System.out.println("Black disease has already been cured.");
                        return;
                    }

                    blackCure = true;
            }

            freeCure = false;
        }

        ++foundCure;
        if (foundCure == 4) {
            gameEnded(true, "Cure of all four diseases have been found.");
        }

    }
}
