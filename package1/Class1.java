package package1;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import au.com.bytecode.opencsv.*;
public class Class1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			CSVReader reader = new CSVReader(new FileReader("AssetsImportCompleteSample.csv"));
			String [] nextLine;
			System.out.println(reader.readNext());
			while ((nextLine = reader.readNext()) != null) {
			    // nextLine[] is an array of values from the line
			    System.out.println(nextLine[0]);
			}
			reader.close();
		}catch(Exception fe)
		{
			System.out.println(fe);
		}
	}

}
