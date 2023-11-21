package fr.gouv.esante.pml.smt.utils;

import java.io.File;
import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ChargerMappingChapitre {
	
	
	
	public  HashMap<String, List<String>> listConceptsChapitre = new HashMap<String, List<String>>();
	
	Map<String, String> test = new HashMap<>();
	
	
	public   HashMap<String, List<String>>  chargeExcelConceptToList(final String xlsFile) throws Exception {
		
		
		
		
		FileInputStream file = new FileInputStream(new File(xlsFile));
			
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		
		XSSFSheet sheet = workbook.getSheetAt(1);
		
		Iterator<Row> rowIterator = sheet.iterator();
		
		
		 rowIterator.next(); 
		
	
		 while (rowIterator.hasNext()) {
	    	 
	    	 Row row = rowIterator.next();
	    	 Cell c1 = row.getCell(0); //dc:type
		     Cell c2 = row.getCell(1); // skos:notation
		     Cell c3 = row.getCell(2); // rdfs:label
		     Cell c4 = row.getCell(3); //rdfs:subclassof
		    
		     
		    
		     
		     String type = c1.getStringCellValue();
		     String code = c2.getStringCellValue();
		     String label = c3.getStringCellValue();
		     String codeParent = c4.getStringCellValue();
		     
	        if (codeParent.isEmpty() ) {
	        	
	        	codeParent ="noeud_racine";
	        }
		    
  		      
				List<String> listedonnees= new ArrayList<>();
				
				
				listedonnees.add(codeParent);//0
				listedonnees.add(label);//1
				listedonnees.add(type);//2
				listedonnees.add("");//3 notes
				listedonnees.add("");//4 valorisation
				
				listedonnees.add("");//5
				listedonnees.add("");//6
				listedonnees.add("");//7
				listedonnees.add("");//8
				listedonnees.add("");//9
				
				listConceptsChapitre.put(code, listedonnees);
				
				
		       
				
				
	   }

		 file.close();
		return listConceptsChapitre;
		
		
	}
	
	

}
