package fr.gouv.esante.pml.smt.rihn;



import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import org.eclipse.rdf4j.model.vocabulary.DC;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.XSDVocabulary;


import fr.gouv.esante.pml.smt.utils.ChargerMapping2023;

import fr.gouv.esante.pml.smt.utils.ChargerMappingChapitre;
import fr.gouv.esante.pml.smt.utils.DCTVocabulary;
import fr.gouv.esante.pml.smt.utils.DublinCoreVocabulary;
import fr.gouv.esante.pml.smt.utils.OWLRDFVocabulary;
import fr.gouv.esante.pml.smt.utils.PropertiesUtil;
import fr.gouv.esante.pml.smt.utils.SKOSVocabulary;
import fr.gouv.esante.pml.smt.utils.ADMSVocabulary;
import fr.gouv.esante.pml.smt.utils.AnsVocabulary;





public class XLSXOWLTransformer {
	
	
	private static String xlsxChapitreFileName = PropertiesUtil.getProperties("xlsxChapitreFileName");
	private static String owlRihnFFileName = PropertiesUtil.getProperties("owlRihnFFileName");
	
	private static OWLOntologyManager man = null;
	  private static OWLOntology onto = null;
	  private static OWLDataFactory fact = null;
	  
	  private static OWLAnnotationProperty skosNotation  = null;
	  private static OWLAnnotationProperty rdfsLabel  = null;
	  private static OWLAnnotationProperty skosNotes  = null;
	  private static OWLAnnotationProperty rihnPrixMax  = null;
	  private static OWLAnnotationProperty dcType  = null;
	  private static OWLAnnotationProperty ansVersionCreation  = null;
	  private static OWLAnnotationProperty ansVersMaj  = null;
	  private static OWLAnnotationProperty ansStatus  = null;
	  private static OWLAnnotationProperty ansVrsobsolescence   = null;
	  private static OWLAnnotationProperty skosaltLabel  = null;
	  private static OWLAnnotationProperty admsStatus  = null;
	  private static OWLAnnotationProperty dctCreated  = null;
	  private static OWLAnnotationProperty dctModified  = null;
	  private static OWLAnnotationProperty owlDeprecated  = null;
	  
	public static void main(String[] args) throws Exception {
		
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		final OutputStream fileoutputstream = new FileOutputStream(owlRihnFFileName);
		 man = OWLManager.createOWLOntologyManager();
		 onto = man.createOntology(IRI.create(PropertiesUtil.getProperties("terminologie_IRI")));
		 fact = onto.getOWLOntologyManager().getOWLDataFactory();
		
		 skosNotation =  fact.getOWLAnnotationProperty(SKOSVocabulary.NOTATION.getIRI());
		 rdfsLabel =  fact.getOWLAnnotationProperty(fr.gouv.esante.pml.smt.utils.OWLRDFVocabulary.RDFS_LABEL.getIRI());
		 rihnPrixMax =  fact.getOWLAnnotationProperty(fr.gouv.esante.pml.smt.utils.RIHNVocabulary.prixMaximal.getIRI());
		 dcType =  fact.getOWLAnnotationProperty(DublinCoreVocabulary .TYPE.getIRI());
		 skosNotes =  fact.getOWLAnnotationProperty(SKOSVocabulary.NOTE.getIRI());
		 
		 ansVersionCreation =  fact.getOWLAnnotationProperty(AnsVocabulary.version_creation.getIRI());
		 dctCreated =  fact.getOWLAnnotationProperty(DCTVocabulary.created.getIRI());
		 
		 ansVersMaj =  fact.getOWLAnnotationProperty(AnsVocabulary.version_derniereModification.getIRI());
		 dctModified =  fact.getOWLAnnotationProperty(DCTVocabulary.modified.getIRI());
		 
		// ansStatus =  fact.getOWLAnnotationProperty(AnsVocabulary.status_entité.getIRI());
		 admsStatus = fact.getOWLAnnotationProperty(ADMSVocabulary.status.getIRI());
		 
		 ansVrsobsolescence =  fact.getOWLAnnotationProperty(AnsVocabulary.version_obsolescence.getIRI());
		 skosaltLabel =  fact.getOWLAnnotationProperty(SKOSVocabulary.ALTLABEL.getIRI());
		 
		 owlDeprecated =  fact.getOWLAnnotationProperty(fact.getOWLDeprecated());
		 
		 
		    OWLClass owlClass = null;
		    
		    createPrincipalNoeud();
		
		
		
		  //ChargerMappingChapitre.chargeExcelConceptToList(xlsxChapitreFileName);
		  
		    ChargerMapping2023.chargeExcelConceptToList(xlsxChapitreFileName);

		    
		   for(String id: ChargerMapping2023.listConcepts2023.keySet()) {
			   
			   
			   final String about = PropertiesUtil.getProperties("terminologie_URI") + id;
		        owlClass = fact.getOWLClass(IRI.create(about));
		        OWLAxiom declare = fact.getOWLDeclarationAxiom(owlClass);
		        man.applyChange(new AddAxiom(onto, declare)); 
		        
		        
		        
		      //balise subClass
		        String aboutSubClass = null;
		        
		        if("noeud_racine".equals(ChargerMapping2023.listConcepts2023.get(id).get(0)))
		        { 
		        aboutSubClass = PropertiesUtil.getProperties("URI_parent") ;
		        }else {
		        	

		        	aboutSubClass = PropertiesUtil.getProperties("terminologie_URI") + ChargerMapping2023.listConcepts2023.get(id).get(0);
		        }
		        

		        OWLClass subClass = fact.getOWLClass(IRI.create(aboutSubClass));
		        OWLAxiom axiom = fact.getOWLSubClassOfAxiom(owlClass, subClass);
		        man.applyChange(new AddAxiom(onto, axiom));
		        

		        
		        addLateralAxioms(skosNotation, id, owlClass);
		        
		        addLateralAxioms(rdfsLabel, ChargerMapping2023.listConcepts2023.get(id).get(1), owlClass, "fr");
		        
		        addLateralAxioms(dcType, ChargerMapping2023.listConcepts2023.get(id).get(2), owlClass);
		        
		        if (!ChargerMapping2023.listConcepts2023.get(id).get(3).isEmpty()) 
		          addLateralAxioms(skosNotes, ChargerMapping2023.listConcepts2023.get(id).get(3), owlClass);
		        
			     
		        if (!ChargerMapping2023.listConcepts2023.get(id).get(4).isEmpty()) 
		        	addFloatAxioms(rihnPrixMax, ChargerMapping2023.listConcepts2023.get(id).get(4), owlClass);
		        
		        if (!ChargerMapping2023.listConcepts2023.get(id).get(5).isEmpty())
		         // addLateralAxioms(ansVersionCreation, ChargerMapping2022.listConcepts2022.get(id).get(5), owlClass);
		           // addDatelAxioms(dctCreated,  getDate(ChargerMapping2022.listConcepts2022.get(id).get(5)), owlClass);
		            addDatelAxioms(dctCreated,  formatter.format(returnDate(ChargerMapping2023.listConcepts2023.get(id).get(5))), owlClass);
		        
		        if (!ChargerMapping2023.listConcepts2023.get(id).get(6).isEmpty())
			       //addLateralAxioms(ansStatus, ChargerMapping2022.listConcepts2022.get(id).get(6), owlClass);
		           addURIAxioms(admsStatus, ChargerMapping2023.listConcepts2023.get(id).get(6), owlClass);
		        
		        if (!ChargerMapping2023.listConcepts2023.get(id).get(6).isEmpty() && "Inactif".equals(ChargerMapping2023.listConcepts2023.get(id).get(6)))
		        	addBooleanAxioms(owlDeprecated, "true", owlClass);
		        
			    if (!ChargerMapping2023.listConcepts2023.get(id).get(7).isEmpty()) 
			         // addLateralAxioms(ansVersMaj, ChargerMapping2022.listConcepts2022.get(id).get(7), owlClass);
			         // addDatelAxioms(dctModified,  getDate(ChargerMapping2022.listConcepts2022.get(id).get(7)), owlClass);
			         addDatelAxioms(dctModified,  formatter.format(returnDate(ChargerMapping2023.listConcepts2023.get(id).get(7))), owlClass);
		        
			    
			    if (!ChargerMapping2023.listConcepts2023.get(id).get(8).isEmpty()) {
			    	
			    	for(String altLabel : ChargerMapping2023.listConcepts2023.get(id).get(8).split("altLabel")) {
			          addLateralAxioms(skosaltLabel, altLabel, owlClass);
			    	}
			    	
			    }	
		       
			    
			    
			   // if (!ChargerMapping2022.listConcepts2022.get(id).get(9).isEmpty()) {
			    	//addLateralAxioms(ansVrsobsolescence, ChargerMapping2022.listConcepts2022.get(id).get(9), owlClass);
			          
			    //}
		        
		        
		    }
		    
		    
		   
		   final RDFXMLDocumentFormat ontologyFormat = new RDFXMLDocumentFormat();
		    ontologyFormat.setPrefix("dct", "http://purl.org/dc/terms/");
		    ontologyFormat.setPrefix("rihn", "http://www.data.esante.gouv.fr/DGOS/RIHN/");
		   // ontologyFormat.setPrefix("ans", "https://www.data.esante.gouv.fr/ANS-CGTS/MetaModel/");
		    
		    IRI iri = IRI.create(PropertiesUtil.getProperties("terminologie_IRI"));
		    man.applyChange(new SetOntologyID(onto,  new OWLOntologyID(iri)));
		   
		    addPropertiesOntology();
		    
		    man.saveOntology(onto, ontologyFormat, fileoutputstream);
		    fileoutputstream.close();
		    System.out.println("Done.");
		    
		
		

	}
	
	public static void addLateralAxioms(OWLAnnotationProperty prop, String val, OWLClass owlClass) {
	    final OWLAnnotation annotation =
	        fact.getOWLAnnotation(prop, fact.getOWLLiteral(val));
	    final OWLAxiom axiom = fact.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation);
	    man.applyChange(new AddAxiom(onto, axiom));
	  }
  
  public static void addLateralAxioms(OWLAnnotationProperty prop, String val, OWLClass owlClass, String lang) {
	    final OWLAnnotation annotation =
	        fact.getOWLAnnotation(prop, fact.getOWLLiteral(val, lang));
	    final OWLAxiom axiom = fact.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation);
	    man.applyChange(new AddAxiom(onto, axiom));
	  }
  
  
  public static void addDatelAxioms(OWLAnnotationProperty prop, String val, OWLClass owlClass) {
	    final OWLAnnotation annotation =
	        fact.getOWLAnnotation(prop, fact.getOWLLiteral(val,OWL2Datatype.XSD_DATE_TIME));
	    final OWLAxiom axiom = fact.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation);
	    man.applyChange(new AddAxiom(onto, axiom));
	  }
  
  public static void addFloatAxioms(OWLAnnotationProperty prop, String val, OWLClass owlClass) {
	    final OWLAnnotation annotation =
	        fact.getOWLAnnotation(prop, fact.getOWLLiteral(val,OWL2Datatype.XSD_FLOAT));
	    final OWLAxiom axiom = fact.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation);
	    man.applyChange(new AddAxiom(onto, axiom));
	  }
  
  public static void addBooleanAxioms(OWLAnnotationProperty prop, String val, OWLClass owlClass) {
	    final OWLAnnotation annotation =
	        fact.getOWLAnnotation(prop, fact.getOWLLiteral(val,OWL2Datatype.XSD_BOOLEAN));
	    final OWLAxiom axiom = fact.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation);
	    man.applyChange(new AddAxiom(onto, axiom));
	  }
  
  public static void addURIAxioms(OWLAnnotationProperty prop, String val, OWLClass owlClass) {

	    IRI iri_creator = IRI.create(PropertiesUtil.getProperties("terminologie_URI")+val);
		   
	    OWLAnnotationProperty prop_creator =fact.getOWLAnnotationProperty(prop.getIRI());
	    
	    OWLAnnotation annotation = fact.getOWLAnnotation(prop_creator, iri_creator);
	    final OWLAxiom axiom = fact.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), annotation);
	    man.applyChange(new AddAxiom(onto, axiom));
	    
	    
	  }
  
  
	public static void createPrincipalNoeud() {
		  
		   String noeud_parent = PropertiesUtil.getProperties("noeud_parent");
		   String noeud_parent_label=PropertiesUtil.getProperties("label_noeud_parent");
		   String noeud_parent_notation=PropertiesUtil.getProperties("notation_noeud_parent");
		    
		   final String aboutSubClass1 = PropertiesUtil.getProperties("URI_parent") ;
		   OWLClass subClass1 = fact.getOWLClass(IRI.create(aboutSubClass1));
	       addLateralAxioms(skosNotation, noeud_parent_notation, subClass1);
	       addLateralAxioms(rdfsLabel, noeud_parent_label, subClass1, "fr");
		  
	  }
	
	
	private static void addPropertiesOntology() {
		  
		  
		  OWLAnnotation anno_version = fact.getOWLAnnotation(fact.getOWLVersionInfo(), 
		    		fact.getOWLLiteral(PropertiesUtil.getProperties("ontology_version")));
		    
		    
		    OWLAnnotation anno_label = fact.getOWLAnnotation(fact.getOWLAnnotationProperty(fr.gouv.esante.pml.smt.utils.OWLRDFVocabulary.RDFS_LABEL.getIRI()), 
		    		fact.getOWLLiteral((PropertiesUtil.getProperties("ontology_label")), "fr"));
		    
		   // IRI iri_creator = IRI.create(PropertiesUtil.getProperties("ontology_creator"));
		    //OWLAnnotationProperty prop_creator =fact.getOWLAnnotationProperty(DCTVocabulary.creator.getIRI());
		    //OWLAnnotation anno_creator = fact.getOWLAnnotation(prop_creator, iri_creator);
		    
		    
		   // IRI iri_pub = IRI.create(PropertiesUtil.getProperties("ontology_publisher"));
		    //OWLAnnotationProperty prop_pub =fact.getOWLAnnotationProperty(DCTVocabulary.publisher.getIRI());
		    //OWLAnnotation anno_pub = fact.getOWLAnnotation(prop_pub, iri_pub );
		    
		    
		    //OWLAnnotationProperty prop_label =fact.getOWLAnnotationProperty(DCTVocabulary.issued.getIRI());
		    //OWLAnnotation anno_issued = fact.getOWLAnnotation(prop_label, fact.getOWLLiteral(PropertiesUtil.getProperties("ontology_issued"),
		    	//	OWL2Datatype.XSD_DATE_TIME));
		    
		    
		    man.applyChange(new AddOntologyAnnotation(onto, anno_version));
		    man.applyChange(new AddOntologyAnnotation(onto, anno_label));
		   // man.applyChange(new AddOntologyAnnotation(onto, anno_creator));
		   // man.applyChange(new AddOntologyAnnotation(onto, anno_pub));
		   // man.applyChange(new AddOntologyAnnotation(onto, anno_issued));
	  }
  
  
  
     
     
     private static Date returnDate(String annee) throws ParseException {
      	   
  	    String dateInString = "01/01/"+annee;
    	 
    	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Date date = formatter.parse(dateInString);
             
  	   return date;

         
      }

  

}
