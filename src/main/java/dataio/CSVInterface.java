package dataio;

import com.csvreader.CsvReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import records.AnyRecord;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Util for managing reading from csv files
 * 
 * 	 * 1) Anything with 'time' or 'date' in the string will be a String later converted to a Temporal TimeEncoder
	 * 2) Anything with 'category_' will be categorical string value
	 * 3) All else will be a floating point value (float, double, integer)
 * 
 * @author lisztian
 *
 */
public class CSVInterface {

	private CsvReader marketDataFeed;
	private AnyRecord anyRecord;
	private String[] headers;
	private String label_name;
	private boolean indexed_headers = false;
	
	/**
	 * Instantiates a CSV reader and for a given file name and produces the 
	 * record template for the record encoder
	 * assertArrayEquals(expected, enc);
	 * @param file_name
	 * @throws IOException 
	 */
	public CSVInterface(String file_name) throws IOException {
		
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(file_name).getFile());

		marketDataFeed = new CsvReader(file.getAbsolutePath());		
		marketDataFeed.readHeaders();
		headers = marketDataFeed.getHeaders();

		anyRecord = createRecord();
	}

	public CSVInterface(String file_name, int label_column) throws IOException {

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(file_name).getFile());

		label_name = null;
		marketDataFeed = new CsvReader(file.getAbsolutePath());
		marketDataFeed.readHeaders();
		headers = marketDataFeed.getHeaders();

		String[] new_headers = new String[headers.length - 1];
		if(label_column < 0) {
			new_headers = new String[headers.length ];
		}

		//get subset of headers without the label_column index

		int j = 0;
		for(int i = 0; i < headers.length; i++) {
			if(i != label_column) {
				new_headers[j] = headers[i];
				j++;
			}
		}

		if(label_column >= 0) {
			label_name = headers[label_column];
		}

		headers = new_headers;

		//System.out.println("label_name " + label_name);

		anyRecord = createRecord();
	}


	
	/**
	 * Instantiates a CSV reader for the file name
	 * @param file_name
	 * @param label_col
	 * @throws IOException 
	 */
	public CSVInterface(String file_name, int[] meta_cols, boolean label_col, boolean header_names) throws IOException {
		
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(file_name).getFile());
		marketDataFeed = new CsvReader(file.getAbsolutePath());
		marketDataFeed.readHeaders();
		
		if(header_names) {
			headers = marketDataFeed.getHeaders();
		}
		else {
			int n_cols = marketDataFeed.getColumnCount();
			
			indexed_headers  = true;
			
			if(!label_col) headers = new String[n_cols - meta_cols.length];
			else {
				headers = new String[n_cols - meta_cols.length - 1];
			}
		}
		
	}


	public CSVInterface() {
		
	}
	
	
	/**
	 * Creates a record template given a string of headers. The default rules are as follow:
	 * 
	 * 1) Anything with 'time' or 'date' in the string will be a String later converted to a Temporal TimeEncoder
	 * 2) Anything with 'category_' will be categorical string value
	 * 3) All else will be a floating point value (float, double, integer)
	 * 
	 * @return
	 */
	public AnyRecord createRecord() {
		
		AnyRecord record = new AnyRecord();
		
		String[] fields = new String[headers.length];
		Object[] values = new Object[headers.length];
		
		for(int i = 0; i < headers.length; i++) {
			
			if(headers[i].contains("category_") || headers[i].contains("cat_")) {
				//fields[i] = new String(headers[i].split("[_]+")[1]);
				fields[i] = new String(headers[i]);
				values[i] = new String("categorical");
			}
			else if(headers[i].contains("time") || headers[i].contains("date")) {
				fields[i] = headers[i];
				values[i] = new String("time");
			}
			else {
				fields[i] = headers[i];
				values[i] = (Float)0f;		
			}
		}
		record.setField_names(fields);
		record.setValues(values);

		if(label_name != null) {
			record.setLabel_name(label_name);
		}

		
		return record;
			
	}
	
	/**
	 * Get the next n records in csv, if they exist
	 * If less than n exist the final records will be null
	 * @param n
	 * @return
	 * @throws IOException
	 */
	public AnyRecord[] getNextRecords(int n) throws IOException {
		
		AnyRecord[] records = new AnyRecord[n];
		
		int count = 0;
		while (marketDataFeed.readRecord()) {
		
			records[count] = getOneRecord();
			count++;
			
			if(count == n) break;
		}
		
		return records;			 		
	}
	
	/**
	 * Gets all the records in the csv file as an arraylist
	 * @return
	 * @throws IOException
	 */
	public ArrayList<AnyRecord> getAllRecords() throws IOException {
		
		ArrayList<AnyRecord> all = new ArrayList<AnyRecord>();
		while (marketDataFeed.readRecord()) {		
			all.add(getOneRecord());			
		}
		return all;	
	}
	

	/**
	 * Gets the next record in the file
	 * @return
	 * @throws IOException
	 */
	public AnyRecord getRecord() throws IOException {
		
		if(headers == null) {
			return null;
		}
		
		if(!marketDataFeed.readRecord()) {
			return null;
		}
		
		AnyRecord anyRecord = new AnyRecord();
		Object[] values = new Object[headers.length];
		

		for(int i = 0; i < headers.length; i++) {
			
			if(headers[i].contains("category_") || headers[i].contains("cat_")) {
				values[i] = marketDataFeed.get(headers[i]);
			}
			else if(headers[i].contains("time") || headers[i].contains("date")) {
				values[i] = marketDataFeed.get(headers[i]);
			}
			else {
				values[i] = Float.parseFloat(marketDataFeed.get(headers[i]));	
			}
		}
		anyRecord.setValues(values);
		
		
		return anyRecord;	
	}
	
	private AnyRecord getOneRecord() throws IOException {
		
		if(headers == null) {
			return null;
		}
				
		AnyRecord anyRecord = new AnyRecord();
		Object[] values = new Object[headers.length];
		

		for(int i = 0; i < headers.length; i++) {

			if(headers[i].contains("category_") || headers[i].contains("cat_")) {

				values[i] = marketDataFeed.get(headers[i]);
			}
			else if(headers[i].contains("time") || headers[i].contains("date")) {
				values[i] = marketDataFeed.get(headers[i]);
			}
			else {

				String myval = marketDataFeed.get(headers[i]);
				if(myval == null || myval == "") values[i] = null;
				else {
					values[i] = Float.parseFloat(marketDataFeed.get(headers[i]));	
				}
				
			}
		}

		anyRecord.setValues(values);

		if(label_name != null) {

			if (StringUtils.isNumeric(marketDataFeed.get(label_name))) {
				anyRecord.setLabel(Integer.parseInt(marketDataFeed.get(label_name)));
			}
			else {
				anyRecord.setLabel_name(marketDataFeed.get(label_name));
			}

		}


		
		return anyRecord;	
	}




	private AnyRecord grabOneRecord() throws IOException {
		
		if(headers == null) {
			return null;
		}
				
		AnyRecord anyRecord = new AnyRecord();
		Object[] values = new Object[headers.length];
		

		for(int i = 0; i < headers.length; i++) {
			
			if(headers[i].contains("category_") || headers[i].contains("cat_")) {
				values[i] = marketDataFeed.get(headers[i]);
			}
			else if(headers[i].contains("time") || headers[i].contains("date")) {
				values[i] = marketDataFeed.get(headers[i]);
			}
			else {
				String myval = marketDataFeed.get(headers[i]);
				if(myval == null || myval == "") values[i] = null;
				else {
					values[i] = Float.parseFloat(marketDataFeed.get(headers[i]));	
				}
				
			}
		}
		anyRecord.setValues(values);
		
		
		return anyRecord;	
	}
	
	
	
	public AnyRecord getAnyRecord() {
		return anyRecord;
	}

	public void setAnyRecord(AnyRecord anyRecord) {
		this.anyRecord = anyRecord;
	}

	public CsvReader getMarketDataFeed() {
		return marketDataFeed;
	}

	public void setMarketDataFeed(CsvReader marketDataFeed) {
		this.marketDataFeed = marketDataFeed;
	}
	
	public void close() {
		marketDataFeed.close();
	}

	
	public static void main(String[] args) throws IOException {
		
		
//		CSVInterface csv = new CSVInterface("data/test_data.csv");	
//		AnyRecord anyrecord = csv.createRecord();
//		
//		ArrayList<AnyRecord> records = csv.getAllRecords();
//		
//		for(AnyRecord record : records) {
//			System.out.println(record.toString());
//		}
//		
//		csv.close();
		
		int total_out = 0;
		int total_non = 0;
		CSVInterface csv = new CSVInterface();
		ClassLoader classLoader = csv.getClass().getClassLoader();
		File file = new File(classLoader.getResource("protein/expense_data_records.csv").getFile());

		CsvReader marketDataFeed = new CsvReader(file.getAbsolutePath());		
		marketDataFeed.readHeaders();
		

		while (marketDataFeed.readRecord()) {	
			
			String startdate = marketDataFeed.get("category_startdate"); 
			String enddate = marketDataFeed.get("category_enddate"); 
			
			if(startdate != null && enddate != null) {
				
				String rank = marketDataFeed.get("rank"); 
				String group = marketDataFeed.get("category_disclosure-group"); 
				String title = marketDataFeed.get("category_title_en");
				String air = NumberUtils.isCreatable(marketDataFeed.get("airfare")) ? marketDataFeed.get("airfare") : "0"; 
				String trans = NumberUtils.isCreatable(marketDataFeed.get("other_transport")) ? marketDataFeed.get("other_transport") : "0";  
				String lodging = NumberUtils.isCreatable(marketDataFeed.get("lodging")) ?  marketDataFeed.get("lodging") : "0";
				String meals = NumberUtils.isCreatable(marketDataFeed.get("meals")) ? marketDataFeed.get("meals") : "0";
				String other = NumberUtils.isCreatable(marketDataFeed.get("other_expenses")) ? marketDataFeed.get("other_expenses") : "0";
				String total = NumberUtils.isCreatable(marketDataFeed.get("total")) ? marketDataFeed.get("total"): "0";
				String ndays = NumberUtils.isCreatable(marketDataFeed.get("num_days")) ? marketDataFeed.get("num_days") : "0";
				
				int label = NumberUtils.isCreatable(rank) ? 1 : 0;
				if(label == 1 && total_out < 500) {
					System.out.println(title  + ";" + air + ";" + trans + ";" + lodging + ";" + meals + ";" + other + ";" + total + ";" + ndays + ";" + label);
					total_out++;
				}
				else if(label == 0 && total_non < 500) {
					System.out.println(title  + ";" + air + ";" + trans + ";" + lodging + ";" + meals + ";" + other + ";" + total + ";" + ndays + ";" + label);
					total_non++;
				}
				
			}
		}
		
		marketDataFeed.close();
	}
	
	
}
