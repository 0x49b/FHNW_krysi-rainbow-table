package ch.fhnw.krysi;

public class RainbowTableApp {

    public static void main(String[] args) {
        char[] reductionArray = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
        String hash           = "1d56a37fb6b08aa709fe90e12ca59e12";
        int    passwordLength = 7;
        int    chainLength    = 2000;

        RainbowTable rainbowTable = new RainbowTable(passwordLength, chainLength, reductionArray);
        rainbowTable.fillTable();
        System.out.println(rainbowTable.lookingForHashInTable(hash));
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

}
