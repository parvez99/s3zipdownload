import java.util.Map;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URL;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import au.com.bytecode.opencsv.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.MalformedURLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.GZIPInputStream;
import com.univocity.parsers.csv.*;
import com.univocity.parsers.common.processor.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.HashMap;
public class swrve {

	List csvfile;
	public URL url;
	public String gz_file;
	public String output_csv_file;
    public int count;
	
	swrve(URL input_url,String ofile,String ifile)
	{
		count = 0;
		url = input_url;
		gz_file = ofile;
		output_csv_file = ifile;
	}
	
	public void downloadFile() {
    /**
     * Download the zip file from remote server based on the URL provided.
     */
	try {
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.connect();
				BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
				FileOutputStream out = new FileOutputStream(gz_file);

				int i = 0;
				byte[] bytesIn = new byte[3000000];
				while ((i = in.read(bytesIn)) >= 0) {
					out.write(bytesIn, 0, i);
				}
				out.close();
				in.close();
		}catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	 public void unZipIt() {
	/**
     * GunZip the downloaded .gz file
     */
		byte[] buffer = new byte[1024];
		try{
			GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(gz_file));
 
			FileOutputStream fout = new FileOutputStream(output_csv_file);
 
			int len;
			while ((len = gzis.read(buffer)) > 0) {
				fout.write(buffer, 0, len);
			}
 
			gzis.close();
			fout.close();
 
			System.out.println(".......................File Unzipped........................");
    	
		}catch(IOException ex){
			ex.printStackTrace();   
		}
	}
	
	public int deviceResolution(Map<String, List<String>> columnValues){
	/**
     * Return the count of devices with resoultion 640*960
	 *This function accepts coloumn data where
	 *Map contains coloumn as key followed by list of values in that column
	 *Checks for device_width and device_height cols for given resolution
	 *Counts the number of resoultion found and returns it
     */
	
		int res_count = 0,i=0;
		
		try{
			for (Map.Entry<String, List<String>> key : columnValues.entrySet()) {
					String key1 = key.getKey();
					if( key1.equals("device_width") )
					{
						List<String> wd = key.getValue();
						for(i=0;i<wd.size();i++)
						{
							if(wd.get(i).equals("640"))		//if width found as 640, then look loop through device_height to look for 960
							{
								for (Map.Entry<String, List<String>> key2 : columnValues.entrySet()) {
									String key3= key2.getKey();
									if( key3.equals("device_height") )
									{
										List<String> ht = key2.getValue();
										if(ht.get(i).equals("960"))
											{
												res_count++;
											}
									}
								}
							}
						}
					}
				}				
		}catch(Exception e)
		{
			System.out.println(e);
		}
		return res_count; 
	}
	
	public int totalSpendInDollars(Map<String,List<String>> columnValues)
	{
	/**
     * Return the sum of dollars spent by users
	 *This function accepts coloumn data where
	 *Map contains coloumn as key followed by list of values in that column
	 *Checks for spend col to add the values
	 *returns the total dollars spent
     */
	
		int total_spend = 0;
		for (Map.Entry<String, List<String>> key : columnValues.entrySet()) {
				String key1 = key.getKey();
				if( key1.equals("spend") )
				{
					List<String> dollars = key.getValue();
					
					for(int i=0;i<dollars.size();i++)
					{
						total_spend = total_spend + Integer.parseInt(dollars.get(i));
					}
				}
		}
		return total_spend;
		
	}
	
	// This function returns the user_id of the user who joined first
	public int firstUser(Map<String,List<String>> columnValues)
	{
	/**
     *This function accepts coloumn data 
	 *Sorts the list first
	 *Selects the first date from the sorted list
	 *Reads the file again for selecting the date col
	 *Gets the id of the date and returns user_id
     */
	
		int date_count = 0;
		int flag = 1;
		try{
			for (Map.Entry<String, List<String>> key : columnValues.entrySet()) {
				String key1 = key.getKey();
				if( key1.equals("date_joined") )
				{
					List<String> date1 = key.getValue();
					Collections.sort(date1);
					
					String firstDate = date1.get(0);
			
			CSVReader reader = new CSVReader(new FileReader(output_csv_file));
			String [] nextLine;
			csvfile = reader.readAll();
			System.out.println((csvfile.size()-1));
			
			
			//CsvParserSettings settings = new CsvParserSettings();
			CsvParserSettings parserSettings = new CsvParserSettings();
			parserSettings.getFormat().setLineSeparator("\n");
			parserSettings.setHeaderExtractionEnabled(true);

			// To get the values of all columns, use a column processor
			ColumnProcessor rowProcessor = new ColumnProcessor();
			parserSettings.setRowProcessor(rowProcessor);

			CsvParser parser = new CsvParser(parserSettings);

			//This will kick in our column processor
			parser.parse(new FileReader(output_csv_file));

			//Finally, we can get the column values:
			Map<String, List<String>> columnValues1 = rowProcessor.getColumnValuesAsMapOfNames();
				for (Map.Entry<String, List<String>> key4 : columnValues1.entrySet()) {
					String key5 = key4.getKey();
						
						if( key5.equals("date_joined") )
						{
							List<String> temp_date = key4.getValue();
							for(int i=0;i<temp_date.size();i++)
							{
								date_count = i;
								if(temp_date.get(i).equals(firstDate))
								{
									flag = 0;
									break;
								}
							}

						}
					}
				
					if(flag == 0)
					{
						for (Map.Entry<String, List<String>> key6 : columnValues1.entrySet()) {
							String key7 = key6.getKey();
								if( key7.equals("user_id"))
								{
									List<String> users = key6.getValue();
									System.out.println(users.get(date_count));  //Prints the user_id with first joining date
								}
						}
					}
				}
			}
		
		
		}catch(Exception e)
		{
			System.out.println(e);
		}
		return date_count;
	}
	//This functions reads the downloaded CSV and performs various operations to get th desired output.
	public void readCSV()
	{
		int i = 0;
		try{
			CSVReader reader = new CSVReader(new FileReader(output_csv_file));
			String [] nextLine;
			csvfile = reader.readAll();
			System.out.println((csvfile.size()-1));
			
			
			//CsvParserSettings settings = new CsvParserSettings();
			CsvParserSettings parserSettings = new CsvParserSettings();
			parserSettings.getFormat().setLineSeparator("\n");
			parserSettings.setHeaderExtractionEnabled(true);

			// To get the values of all columns, use a column processor
			ColumnProcessor rowProcessor = new ColumnProcessor();
			parserSettings.setRowProcessor(rowProcessor);

			CsvParser parser = new CsvParser(parserSettings);

			//This will kick in our column processor
			parser.parse(new FileReader(output_csv_file));

			//Thus we get the column values:
			Map<String, List<String>> columnValues = rowProcessor.getColumnValuesAsMapOfNames();
			System.out.println(deviceResolution(columnValues));
			System.out.println(totalSpendInDollars(columnValues));
			System.out.println(firstUser(columnValues));
			
			
			
			
			
			
		}catch(IOException ex){
			ex.printStackTrace();   
		}
	}
	
	public static void main(String[] args) {
		try{
				URL url = new URL(args[0]);
				swrve obj = new swrve(url,"test_file1.gz","test_file1.csv"); //Parametrized constructor to initialise url,.gz and .csv file names.
				obj.downloadFile();
				obj.unZipIt();
				obj.readCSV();
		}catch(MalformedURLException me)
		{
			me.printStackTrace();
		}
	}
}