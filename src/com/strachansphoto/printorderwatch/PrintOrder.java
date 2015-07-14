package com.strachansphoto.printorderwatch;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by admin on 10/7/2015.
 */
public class PrintOrder {
    String mOrderid;
    String mCustomerName;
    String mPaymentStaffName;
    String mDeliverBy;
    String mPhysicalAddress;
    String mCustomerNotes;
    String mPromoCode;
    String mPaymentType;
    String mPaymentRecieved;
    double mOrderTotal;
    double mPromoAmount;
    double mDeliverAmount;
    double  mCashTended;
    JSONArray mOrderlines;

    public void setOrderTotal(double orderTotal) {
        mOrderTotal = orderTotal;
    }

    public void setPaymentType(String paymentType) {
        mPaymentType = paymentType;
    }

    public void setCashTended(double cashTended) {
        mCashTended = cashTended;
    }

    public void setOrderid(String orderid) {
        mOrderid = orderid;
    }

    public void setCustomerName(String customerName) {
        mCustomerName = customerName;
    }

    public void setPaymentRecieved(String paymentRecieved) {
        mPaymentRecieved = paymentRecieved;
    }

    public void setPaymentStaffName(String paymentStaffName) {
        mPaymentStaffName = paymentStaffName;
    }

    public void setDeliverBy(String deliverBy) {
        mDeliverBy = deliverBy;
    }

    public void setPhysicalAddress(String physicalAddress) {
        mPhysicalAddress = physicalAddress;
    }

    public void setCustomerNotes(String customerNotes) {
        mCustomerNotes = customerNotes;
    }

    public void setPromoCode(String promoCode) {
        mPromoCode = promoCode;
    }

    public void setPromoAmount(double promoAmount) {
        mPromoAmount = promoAmount;
    }

    public void setDeliverAmount(double deliverAmount) {
        mDeliverAmount = deliverAmount;
    }

    public void addPrintLine(JSONArray jsonObjectLine) {
        mOrderlines = jsonObjectLine;
    }

    public PrintOrder() { }


    public boolean print() throws IOException {
        HashMap printsHashMap = new HashMap();
        HashMap framesHashMap = new HashMap();

        char LF = 10;   //Line Feed
        char ESC = 27;  //Escape
        char GS = 29;   //Other one

        char OFF = 0;
        char ON = 1;
        char Heading = 17;  //2x larger and 2x wider
        char Feed = 250;

        double printPrice = 0;
        double orderTotal = 0;
        int printTotal = 0;

        DecimalFormat df = new DecimalFormat("0.00");

        java.io.File currentDir = new java.io.File("");

        FileOutputStream fileOutputStream = new FileOutputStream("LPT1");  //currentDir+mOrderid+".txt"
        //System.out.println(currentDir+mOrderid+".txt");
        PrintStream printStream = new PrintStream(fileOutputStream);
        //Shop copy
        printStream.print(GS);
        printStream.print('!');
        printStream.print(Heading); //Heading mode

        printStream.print(GS);
        printStream.print('b');
        printStream.print(ON); //Smoothing on

        printStream.println("Order: "+mOrderid);
        printStream.println(mCustomerName);

        printStream.print(GS);
        printStream.print('!');
        printStream.print(OFF); //Heading off
        printStream.print(LF);

        printStream.print(ESC);
        printStream.print('E');
        printStream.print(ON); //Empasize On

        printStream.println(mPaymentRecieved);
        printStream.println("Staff: " + mPaymentStaffName);
        printStream.print(LF);

        printStream.println("Delivery: " + mDeliverBy);
        if (mDeliverBy.toLowerCase().contains("courier")) {
            printStream.println(mPhysicalAddress);
        }
        printStream.print(ESC);
        printStream.print('E');
        printStream.print(OFF); //Empasize off
        printStream.print(LF);

        //Loop though lines
        for(int x = 0; x < mOrderlines.size(); x = x+1) {
            JSONObject jsonObject = (JSONObject) mOrderlines.get(x);
            if (jsonObject.get("retail").toString().equals("0")) {
                printPrice = Double.parseDouble(jsonObject.get("printprice").toString());
            } else {
                printPrice = Double.parseDouble(jsonObject.get("retail").toString());
            }

            String printReference = jsonObject.get("printsize").toString() +
                    " "+ jsonObject.get("printsurface").toString() +
                    " @ US$" + df.format(printPrice);
            orderTotal += Integer.parseInt(jsonObject.get("qty").toString()) * printPrice;
            printTotal += Integer.parseInt(jsonObject.get("qty").toString());

            if (printsHashMap.containsKey(printReference)) {
                printsHashMap.put(printReference, (int)printsHashMap.get(printReference) + Integer.parseInt(jsonObject.get("qty").toString()));
            } else {
                printsHashMap.put(printReference, Integer.parseInt(jsonObject.get("qty").toString()));
            }

            if(mOrderlines.size() < 6) {
            //    printStream.println(jsonObject.get("originalname").toString());
            //    printStream.println(jsonObject.get("qty").toString() + "x " + printReference);
            }


            if(jsonObject.get("framecode") != null){
                String frameDescription = jsonObject.get("framecode").toString() +
                        " " + jsonObject.get("printsize").toString() +
                        " @ US$" + df.format(Double.parseDouble(jsonObject.get("frameprice").toString()));
                orderTotal += Integer.parseInt(jsonObject.get("qty").toString()) * Double.parseDouble(jsonObject.get("frameprice").toString());

                if (framesHashMap.containsKey(frameDescription)) {
                    framesHashMap.put(frameDescription, (int)framesHashMap.get(frameDescription) + Integer.parseInt(jsonObject.get("qty").toString()));
                } else {
                    framesHashMap.put(frameDescription, Integer.parseInt(jsonObject.get("qty").toString()));
                }

                if(mOrderlines.size() < 6) {
                //    printStream.println(frameDescription);
                }
            }
        }

        //if(mOrderlines.size() < 6) {
            printStream.print(LF);
            printStream.print(ESC);
            printStream.print('E');
            printStream.print(ON); //Empasize On

            printStream.println("Prints: ");

            printStream.print(ESC);
            printStream.print('E');
            printStream.print(OFF); //Empasize Off
        //}

        //sort printsizes
        Map<Integer, String> printMap = new TreeMap<Integer, String>(printsHashMap);
        Set printSet = printMap.entrySet();
        Iterator printIterator = printSet.iterator();
        while(printIterator.hasNext()) {
            Map.Entry entry = (Map.Entry)printIterator.next();
            printStream.println(entry.getValue() + "x " + entry.getKey());
        }

        if(framesHashMap.size() > 0) {
            printStream.print(LF);
            printStream.print(ESC);
            printStream.print('E');
            printStream.print(ON); //Empasize On

            printStream.println("Frames: ");

            printStream.print(ESC);
            printStream.print('E');
            printStream.print(OFF); //Empasize Off
        }

        //sort frames
        Map<Integer, String> frameMap = new TreeMap<Integer, String>(framesHashMap);
        Set frameSet = frameMap.entrySet();
        Iterator frameIterator = frameSet.iterator();
        while(frameIterator.hasNext()) {
            Map.Entry entry = (Map.Entry)frameIterator.next();
            printStream.println(entry.getValue() + "x " + entry.getKey());
        }

        printStream.print(LF);
        printStream.print(ESC);
        printStream.print('E');
        printStream.print(ON); //Empasize On

        printStream.println("Print Count: " + printTotal);
        if (mOrderTotal >  orderTotal) {
            orderTotal = mOrderTotal;
        }
        printStream.println("Order total:   US$" + df.format(orderTotal));
        if (mDeliverAmount > 0) {
            printStream.println("Delivery:   US$" + df.format(mDeliverAmount));
            orderTotal += mDeliverAmount;
        }
        if (mPromoAmount > 0) {
            printStream.println("Promotion:  (US$" + df.format(mPromoAmount) + ")");
            orderTotal -= mPromoAmount;
        }
        if (mCashTended > 0) {
            printStream.println("Amount Paid:   US$" + df.format(mCashTended) + " " + mPaymentType);
            printStream.println("Change tended: US$" + df.format(mCashTended - orderTotal));
        } else if(orderTotal >= 0){
            printStream.print(LF);
            printStream.println("Due on Collection: US$" + df.format(orderTotal));
        }

        printStream.print(ESC);
        printStream.print('E');
        printStream.print(OFF); //Empasize Off

        //Customer Notes
        if(!mCustomerNotes.trim().equals("")){printStream.print(LF);
            printStream.print(ESC);
            printStream.print('E');
            printStream.print(ON); //Empasize On
            printStream.println("Customer Notes:");
            printStream.print(ESC);
            printStream.print('E');
            printStream.print(OFF); //Empasize Off
            printStream.println(mCustomerNotes);
        }

        printStream.print(ESC);
        printStream.print('J');
        printStream.print(Feed); //Feed 1/2 in

        printStream.print(ESC); //Paper cut
        printStream.print('i');


        //Customer copy - not for online or on collect payments
        if(!"on collect".equals(mPaymentType.toLowerCase())
                && !"online".equals(mPaymentType.toLowerCase())
                && !"procredit".equals(mPaymentType.toLowerCase())) {
            printStream.print(GS);
            printStream.print('!');
            printStream.print(Heading); //Heading mode

            printStream.print(GS);
            printStream.print('b');
            printStream.print(ON); //Smoothing on

            printStream.println("Order: " + mOrderid);
            printStream.println(mCustomerName);

            printStream.print(GS);
            printStream.print('!');
            printStream.print(OFF); //Heading off
            printStream.print(LF);

            printStream.print(ESC);
            printStream.print('E');
            printStream.print(ON); //Empasize On

            printStream.println(mPaymentRecieved);
            printStream.println("Staff: " + mPaymentStaffName);
            printStream.print(LF);

            printStream.println("Delivery: " + mDeliverBy);
            if (mDeliverBy.toLowerCase().contains("courier")) {
                printStream.println(mPhysicalAddress);
            }
            printStream.print(ESC);
            printStream.print('E');
            printStream.print(OFF); //Empasize off

            printStream.print(LF);
            printStream.print(ESC);
            printStream.print('E');
            printStream.print(ON); //Empasize On

            printStream.println("Print Count: " + printTotal);
            printStream.println("Order total:   US$" + df.format(orderTotal));
            if (mDeliverAmount > 0) {
                printStream.println("Delivery:   US$" + df.format(mDeliverAmount));
                orderTotal += mDeliverAmount;
            }
            if (mPromoAmount > 0) {
                printStream.println("Promotion:  (US$" + df.format(mPromoAmount) + ")");
                orderTotal -= mPromoAmount;
            }
            if (mCashTended > 0) {
                printStream.println("Amount Paid:   US$" + df.format(mCashTended) + " " + mPaymentType);
                printStream.println("Change tended: US$" + df.format(mCashTended - orderTotal));
            } else if(orderTotal >= 0){
                printStream.print(LF);
                printStream.println("Due on Collection: US$" + df.format(orderTotal));
            }

            printStream.print(LF);
            printStream.println("*** Use this slip for collection. ***");

            printStream.print(ESC);
            printStream.print('E');
            printStream.print(OFF); //Empasize Off

            printStream.print(ESC);
            printStream.print('J');
            printStream.print(Feed); //Feed 1/2 in

            printStream.print(ESC); //Paper cut
            printStream.print('i');
        }

        printStream.close();
        fileOutputStream.close();

        return true;
    }
}