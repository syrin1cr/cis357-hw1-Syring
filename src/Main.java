import java.io.*;
import java.text.Format;
import java.util.*;

// Homework 1: Sales Register Program
// Course: CIS357
// Due date: 7/5/2022
// Name: Cody Syring
// GitHub: xxx
// Instructor: Il-Hyung Cho
// Program description:

public class Main {
    private static double totalCostForDay = 0; //holds total expense on register

    public static void main(String[] args) throws IOException {
        //Controls if we are done with the program or not.
        Boolean newSale = true;
        String userData;
        char tempData;
        //Defines the List that we use for all items held in our store
        List<item> allItems =  new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        //Prints the welcome screen
        System.out.println("Welcome to Syring cash register system!\n");

        allItems = getData(); //Stores information in text file as a List of item

        //This is the start of the Register. Will continue till newSale is false
        //Only when the user types N/n
        do{
            System.out.print("Beginning a new sale (Y/N) ");
            userData = reader.readLine();
            tempData = userData.charAt(0);
            System.out.println("\n---------------------------");
            if(tempData == 'Y' || tempData == 'y'){
                addItemToCart(allItems);
            } else if (tempData == 'N' || tempData == 'n'){
                newSale = false;
            }
            resetShop(allItems);
        }while (newSale);

        System.out.println("The total sale for the day is $" + totalCostForDay);
        System.out.println("Thanks for using POST system. Goodbye.");
    }

    /*
    This function will get the list of all items on the register and reset the 'cart'
    portion of the register. This will not remove the ID, Price, or Name. Only the
    amount to buy
     */
    static List<item> resetShop(List<item> itemList){
        for (item x : itemList){
            x.resetCart();
        }
        return itemList;
    }
    /*
    This function gets every item on the register as itemsList. It handles all the
    tabbing for the output along with calling the required functions to output the
    end sale information. It will also add the end sale total to the total register
    sales.
     */
    static void printReceipt(List<item> itemsList) throws IOException {
        //These base formats what used from java Lanuage Tutorials. Slightly modified after.
        String column1Format = "%-3.3s";  // fixed size 3 characters, left aligned
        String column2Format = "%-8.8s";  // fixed size 8 characters, left aligned
        String column3Format = "%6.6s";   // fixed size 6 characters, right aligned
        String formatInfo = column1Format + " " + column2Format + " " + column3Format + " " + column1Format;


        column1Format = "%-9.9s";
        column2Format = "%13.2s";
        column3Format = "%-5.4s";
        String formatInfo1 = column1Format + " " + column2Format + " " + column3Format;

        column1Format = "%-9.9s";
        column2Format = "%14.3s";
        String formatInfo2 = column1Format + " " + column2Format;

        cash total = new cash();

        System.out.println("---------------------------");
        System.out.println("Items list:");
        for(item x : itemsList) {
            if(x.getAmount() != 0){
                //------------------------------------------
                //This section of output is for showing the list of items in the cust cart
                //It will also add the total coast of the cart into
                System.out.print("\t");
                System.out.format(formatInfo, x.getAmount(),x.getName(),"$",x.getPrice());
                System.out.println();
                total.addToTotal(x.getTotalPrice());
            }
        }

        //-------------------------------------------
        //This is to show the total expense of the cart
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String getCash = "";
        Double change = 0.0;
        System.out.format(formatInfo1,"Subtotal","$",total.getTotalCost());
        System.out.println();
        System.out.format(formatInfo1,"With Tax","$",total.getTax());
        System.out.println();
        System.out.format(formatInfo2,"Amount","$ ");

        getCash = reader.readLine();
        change = Double.parseDouble(getCash) - total.getTax();

        while (change < 0){
            System.out.println("Please enter more cash than total cost");
            System.out.format(formatInfo2,"Amount","$ ");
            getCash = reader.readLine();
            change = Double.parseDouble(getCash) - total.getTax();
        }
        System.out.format(formatInfo1,"Change","$",change);
        totalCostForDay = totalCostForDay + total.getTax();
        System.out.println("\n---------------------------\n");
    }
/*
This is to print a list of every item in the "store" if you will.
 */
    static void printList(List<item> itemsList) {
        System.out.println("ID  " + "Item Name  " + "Price");
        for(item x : itemsList) {
            System.out.println(x.getId() + "\t" + x.getName() + "\t" + (x.getPrice()*x.getAmount()));
        }
    }
/*
addItemToCart function gets the entire item list. It will get a variable from the user
will than ask the user how many of that item they want. It will call itself again
incase the user wants to buy another item.
 */
    static void addItemToCart(List<item> itemsList) throws IOException {
        String addToCart = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Enter product Code:\t");
            addToCart = reader.readLine();

            if (Integer.parseInt(addToCart) == -1) {
                printReceipt(itemsList);
            } else {
                for (item x : itemsList) {
                    if (Integer.parseInt(addToCart) == x.getId()) {
                        System.out.println("\t item name: " + x.getName());
                        System.out.print("Enter Quantity: \t");
                        addToCart = reader.readLine();
                        x.addToAmount(Integer.parseInt(addToCart));
                        System.out.println("\t" + "item total: \t" + (x.getAmount() * x.getPrice()));
                    }
                }
                addItemToCart(itemsList);
            }
        }catch (NumberFormatException nfe){
            System.out.println("Invalid input please try again");
        }
    }
    /*
    splitString gets a comma delimited string and will break it into a list.
    It will than create an item and place the information in its home, sending
    that single item back.
     */
    static item splitString(String tempList){
        item tempItem = new item();
        List<String> list = Arrays.asList(tempList.split(","));
        tempItem.setId(list.get(0));
        tempItem.setName(list.get(1));
        tempItem.setPrice(list.get(2));

        return tempItem;
    }
    /*
    getData will grab all the information from the text file and send it back
    as a list to the main function.
     */
   static List<item> getData(){
        List<item> allItems =  new ArrayList<>();
       try {
            File inFile = new File("src/data.txt");
            Scanner inReader = new Scanner(inFile);
            while (inReader.hasNextLine()) {
                String data = inReader.nextLine();
               allItems.add(splitString(data));
            }
            inReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return allItems;
    }
}
