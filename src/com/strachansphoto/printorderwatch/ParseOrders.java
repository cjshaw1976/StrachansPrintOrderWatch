package com.strachansphoto.printorderwatch;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Created by admin on 10/7/2015.
 */
public class ParseOrders {
    LogFile logFile = new LogFile();

    public ParseOrders() { }

    public void parseForOrders(String jsonInput, PollForOrders pollForOrders){
        Object obj= JSONValue.parse(jsonInput);
        JSONArray array=(JSONArray)obj;

        Iterator<JSONObject> iterator = array.iterator();
        while (iterator.hasNext()) {
            JSONObject jsonObject=iterator.next();
            if(Objects.equals(jsonObject.get("receiptprinted").toString(), "0")){
                PrintOrder printOrder = new PrintOrder();
                printOrder.setCustomerName(jsonObject.get("customername").toString());
                printOrder.setCustomerNotes(jsonObject.get("customernotes").toString());
                printOrder.setOrderid(jsonObject.get("orderid").toString());
                printOrder.setPaymentRecieved(jsonObject.get("paymentrecieved").toString());
                printOrder.setDeliverBy(jsonObject.get("deliverby").toString());
                printOrder.setPaymentStaffName(jsonObject.get("paymentstaffname").toString());
                printOrder.setPhysicalAddress(jsonObject.get("physicaladdress").toString());
                printOrder.setDeliverAmount(Double.parseDouble(jsonObject.get("deliveramount").toString()));
                printOrder.setOrderTotal(Double.parseDouble(jsonObject.get("ordertotal").toString()));
                printOrder.setPromoAmount(Double.parseDouble(jsonObject.get("promoamount").toString()));
                printOrder.setPromoCode(jsonObject.get("promocode").toString());
                printOrder.setCashTended(Double.parseDouble(jsonObject.get("cashtended").toString()));
                printOrder.setPaymentType(jsonObject.get("paymenttype").toString());

                //Order Lines
                JSONArray arrayLines = (JSONArray) jsonObject.get("lines");
                printOrder.addPrintLine(arrayLines);


                //send to print
                boolean printSuccess = false;
                try {
                    printSuccess = printOrder.print();
                    if(printSuccess){
                        logFile.log(Level.INFO, "Printed: " + jsonObject.get("orderid").toString());
                        pollForOrders.mark(jsonObject.get("orderid").toString());
                    } else {
                        //todo: popup?
                        //halt processing and pop up message
                    }
                } catch (FileNotFoundException e) {
                    logFile.log(Level.WARNING, "Print error: " + e.getMessage());
                    //System.out.println("Print error: " + e.getMessage());
                    //halt processing and pop up message
                } catch (IOException e) {
                    logFile.log(Level.WARNING, "Print error: " + e.getMessage());
                    //System.out.println("Print error: " + e.getMessage());
                } catch (NoSuchAlgorithmException e) {
                    logFile.log(Level.WARNING, "Print error: " + e.getMessage());
                    //System.out.println("Print error: " + e.getMessage());
                }

            }
        }
    }

}
