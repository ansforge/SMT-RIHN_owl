package fr.gouv.esante.pml.smt.utils;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class ChargerMapping2018 {
	
	
	public static HashMap<String, List<String>> listConcepts2018 = new HashMap<String, List<String>>();
	private static String ANNE_2018 = "2018";
	//private static String TYPE_ACTE = "Acte";
	private static String TYPE_ACTE = "code";
	private static String Status_ACTIF = "Actif";
	
	public static  HashMap<String, List<String>>  chargeExcelConceptToList(final String xlsFile) throws Exception {
		
		
		ChargerMappingChapitre ChargerMappingChapitre1 = new ChargerMappingChapitre();
		
		//Get data chapitre
		listConcepts2018 =  ChargerMappingChapitre1.chargeExcelConceptToList(xlsFile);
		
		// Traitement File 2018
		
		
		String xlsxRihnFileName = PropertiesUtil.getProperties("xlsxRihnFileName2018");
		
		FileInputStream file = new FileInputStream(new File(xlsxRihnFileName));
		
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		
		XSSFSheet sheet = workbook.getSheetAt(0);
		
		Iterator<Row> rowIterator = sheet.iterator();
		 
		rowIterator.next(); 
		rowIterator.next();
		
		 String codeParent="";
		 String nextParentNoeud="";
		 
		 String refCode = "";
		 
		 while (rowIterator.hasNext()) {
			 
			 
			 Row row = rowIterator.next();
	    	 Cell c1 = row.getCell(0); //get Ref Code
		     Cell c2 = row.getCell(1); // get Libelle
		     Cell c3 = row.getCell(3); // get Valorisation
		     Cell c4 = row.getCell(4); //get notes
		    
			 
		     String libelle = c2.getStringCellValue();
		     
		     if(c1 != null && c1.getCellType() != Cell.CELL_TYPE_BLANK) { // Acte
			    	
		    	 
		    	    List<String> listedonnees= new ArrayList<>();   
		    	 
			     	refCode = c1.getStringCellValue().trim();
			     	codeParent = nextParentNoeud;
			     	String notes = c4.getStringCellValue();
			     	double value = c3.getNumericCellValue();
			     	
			     	listedonnees.add(codeParent);//0
			     	listedonnees.add(libelle);//1
			     	listedonnees.add(TYPE_ACTE);//2
			     	listedonnees.add(notes);//3
			     	listedonnees.add(String.valueOf(value)); //4
			     	listedonnees.add(ANNE_2018);//5 date creation
			     	listedonnees.add(Status_ACTIF);//6
			     	listedonnees.add("");//7 date modif
			     	listedonnees.add("");//8
			     	listedonnees.add("");//9
			     	
			   
			     	
			     	listConcepts2018.put(refCode, listedonnees);
			     	
			     	
			    }else { //Chapitre ou Sous Chapitre
			    	
			    	char position3 = libelle.charAt(2);
			    	char position6 = libelle.charAt(6);
			    	char point = '.';
			    	char tiret = '-';
			    	
			    	if(position3==point) {refCode= libelle.substring(0,2);}//Chapitre
			    	else if(position6==tiret){// Chapitre Level 2
			    		
			    		refCode= libelle.substring(0,6);
			    	}else { //Chapitre Level 1
			    		
			    		refCode= libelle.substring(0,5);
			    		
			    		char test = refCode.charAt(4);
			    		char test2 = '-';
			    		//Cas 14-2 ; 14-3
			    		if(test==test2) {
			    			refCode = refCode.substring(0, 4);
			    		}
			    		
			    	}
			    	
			    	nextParentNoeud = refCode; //01
			    	
			    	
			    }
			 
			 
		 }
		
		file.close();
		
		
		
		return listConcepts2018;  
		
		
		
				
	   }

		

	
}
