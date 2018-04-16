
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.net.whois.WhoisClient;


public class Main {

  public static String getLocation(String address) {
    String toReturn = "";
    try {
      String dbLocation = "C:\\CitiesDatabase\\database.mmdb";
      InetAddress ipAddress = InetAddress.getByName(address);
      File database = new File(dbLocation);
      DatabaseReader dbReader = new DatabaseReader.Builder(database).build();
      CityResponse response = dbReader.city(ipAddress);
      if(response.getCity().getName() == null){
        toReturn = response.getCountry().getName();
      }else {
        toReturn = response.getCity().getName() + ", " + response.getCountry().getName();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return toReturn;
  }

  public static ArrayList<String> readInFileSection(int section){
    ArrayList<String> toReturn = new ArrayList<String>();
    try{
      BufferedReader reader = new BufferedReader(new FileReader("C:\\CitiesDatabase\\FormattedRoutes.txt"));
      int i = 0; String temp;
      while(i<section){
        temp = reader.readLine();
        if(temp.equals(";")){
          i++;
        }
      }
      temp = "";
      while(!temp.equals(";")){
        temp = reader.readLine();
        toReturn.add(temp);
      }
      reader.close();
    }catch (Exception err){
      err.printStackTrace();
    }
    return toReturn;
  }

  public static void outputFileSection(int section, ArrayList<String> toOutput){
    try {
      PrintStream writer = new PrintStream("C:\\CitiesDatabase\\" + section + ".txt");
      for (String string : toOutput) {
        writer.println(string);
      }
    }catch(Exception err){
      err.printStackTrace();
    }
  }

  public static void main(String[] args){
    Scanner input = new Scanner(System.in);
    ArrayList<String> inputs = new ArrayList<String>();
    String temp = input.nextLine();
    while(!temp.equals("GO")){
      inputs.add(temp);
      temp = input.nextLine();
    }
    System.out.println("RESULTS: ");

    for(int i = 0; i < inputs.size(); i++){
      Boolean result = false;
      for(int a = 0; a < i; a++){
        if(a != i && inputs.get(a).equals(inputs.get(i))){
          result = true;
        }
      }
      if(!result){
        System.out.println(" " + inputs.get(i) + " is not known.");
      }else{
        System.out.println(" " + inputs.get(i) + " is known.");
      }
    }
  }

  public static void Locate(){
    ArrayList<String> section;
    ArrayList<String> compilation = new ArrayList<String>();
    for(int i = 0; i < 2; i++){
      section = readInFileSection(i);
      for(int a = 0; a < section.size() - 1; a++){
        String temp = section.get(a);
        int ip = 0;
        ip = section.get(a).indexOf(";");
        ip++;
        String address = section.get(a).substring(ip);
        if(address.equals("172.16.0.1") || address.equals("127.0.0.1") || address.equals("")){
          temp = section.get(a) + ";" + "local network address" + ";";
        }else{
          //System.err.println("FINDING " + address);
          temp = section.get(a) + ";" + getLocation(address) + ";";
        }
        section.set(a,temp);
      }
      compilation.addAll(section);
      outputFileSection(i,section);
    }
    outputFileSection(21,compilation);
  }




  public static void Test(String[] args) {
    System.out.println(getWhois("ae1.brisub-rbr1.ja.net"));
    System.out.println("Done");
  }


  public static String getWhois(String domainName) {
    StringBuilder result = new StringBuilder("");
    WhoisClient whois = new WhoisClient();
    try {
      //default is internic.net
      whois.connect(WhoisClient.DEFAULT_HOST);
      String whoisData1 = whois.query("=" + domainName);
      result.append(whoisData1);
      whois.disconnect();
    } catch (SocketException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result.toString();
  }
}
