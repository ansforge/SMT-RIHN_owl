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

public class ChargerMapping2023 {
	
	public static HashMap<String, List<String>> listConcepts2023 = null;
	public static HashMap<String, List<String>> listConcepts2022 = null;
	private static String ANNE_2023 = "2023";
	//private static String TYPE_ACTE = "Acte";
	private static String TYPE_ACTE = "code";
	private static String Status_ACTIF = "Actif";
	private static String Status_INACTIF = "Inactif";
	
	public static  HashMap<String, List<String>>  chargeExcelConceptToList(final String xlsFile) throws Exception {
		
		
		//Get Data 2022
				listConcepts2022 =  ChargerMapping2022.chargeExcelConceptToList(xlsFile);
				
		
		ChargerMappingChapitre ChargerMappingChapitre4 = new ChargerMappingChapitre();
		
		// GET Data Chapitre
		listConcepts2023 =  ChargerMappingChapitre4.chargeExcelConceptToList(xlsFile);
		
		
		
		// Traitement File 2023
		
		
		
		String xlsxRihnFileName = PropertiesUtil.getProperties("xlsxRihnFileName2023");
		
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
		     Cell c3 = row.getCell(2); // get Valorisation
		     Cell c4 = row.getCell(3); //get notes
		    
			 
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
			     	
			     	//Verfier pour chaque code de 2023 si existe ou non dans 2022
			     	
			     	if(listConcepts2022.containsKey(refCode)) {// Cas MAJ
			     		  
			     		 listedonnees.add(5,listConcepts2022.get(refCode).get(5));//date creation de N-1  //5
				     	 listedonnees.add(6, Status_ACTIF); // 6
		     		  
		     		  //verfication si il ya des modifs dans le notes
		     		  
				     	if(!notes.equals(listConcepts2022.get(refCode).get(3)) || !libelle.equals(listConcepts2022.get(refCode).get(1))
			     				  || !String.valueOf(value).equals(listConcepts2022.get(refCode).get(4))
			     				  ) { 
			     			 
			     			 if(!notes.equals(listConcepts2022.get(refCode).get(3))){ 
			     			   
			     			   listedonnees.add(7, ANNE_2023); // 7 : Date MAJ
			     			   listedonnees.add(8, listConcepts2022.get(refCode).get(8));//Ancien Label
			     			 }
			     			
			     			
			     			if(!String.valueOf(value).equals(listConcepts2022.get(refCode).get(4))){ 
				     			   
				     			   listedonnees.add(7, ANNE_2023); // 7 : Date MAJ
				     			   listedonnees.add(8, listConcepts2022.get(refCode).get(8));//Ancien Label
				     			 }
			     			
			     			if(!libelle.equals(listConcepts2022.get(refCode).get(1))){ 
				     			   
				     			   listedonnees.add(7, ANNE_2023); // 7 : Date MAJ
				     			   listedonnees.add(8, listConcepts2022.get(refCode).get(1)+ "altLabel"+
				     					  listConcepts2022.get(refCode).get(8));//Ancien Label
				     			 }
			     			 
			     		  }
				     	 
				     else {
		     			  //Aucun modif
		     			listedonnees.add(7, listConcepts2022.get(refCode).get(7));
		     			listedonnees.add(8, listConcepts2022.get(refCode).get(8));//Ancien Labe
		     		  }
		     		  
			     		  
				     	listedonnees.add(9,""); 
			     		  
			     		  
					}else {//cas  ajout
						
						 listedonnees.add(5, ANNE_2023);//date creation de N-1  //5
				     	 listedonnees.add(6, Status_ACTIF); // 6
				     	 listedonnees.add(7, "");// date Maj
				     	 listedonnees.add(8,"");
				     	 listedonnees.add(9, "");
					}
			     	
			     	
			     	listConcepts2023.put(refCode, listedonnees);
			     	
			     	
			     	
			     	
			     	
			     	//System.out.println("code "+refCode + "code Parent "+codeParent);
			     	
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
		
		
		for(String id : listConcepts2022.keySet()) {
			
			
			  if(!listConcepts2023.containsKey(id)) {
			  
			      
				  
				  List<String> list = new ArrayList<>();
				  
				  list.add(0, listConcepts2022.get(id).get(0)); 
				  list.add(1, listConcepts2022.get(id).get(1));
				  list.add(2,listConcepts2022.get(id).get(2)); 
				  list.add(3,listConcepts2022.get(id).get(3)); 
				  list.add(4,listConcepts2022.get(id).get(4)); 
				  list.add(5,listConcepts2022.get(id).get(5));
				  list.add(6, Status_INACTIF); 
				 // list.add(7, ANNE_2023);
				  if(Status_ACTIF.equals(listConcepts2022.get(id).get(6))) {
					  list.add(7,ANNE_2023);
				  }else {
					  list.add(7,listConcepts2022.get(id).get(7));
				  }
				  list.add(8, listConcepts2022.get(id).get(8));
				 
				  
				  
				  listConcepts2023.put(id, list); 
				  }
			 
		}
		
		
		
		
		return listConcepts2023;  
		

}
}
