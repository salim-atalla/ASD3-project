
import java.io.*;
import java.util.ArrayList;


public class Fichier {
	
	// Attribus
	protected int n;
 	protected char[][] plateau; 
	protected ArrayList<Case> casesVides;
	 
	
	
	// Constructeur
	public Fichier (String nomFichier, char caseVide, Joueur J1, Joueur J2) throws Exception {
	 
        String chemin = System.getProperty("user.dir");
        chemin = chemin + "/data/" + nomFichier;
        
        
        File fichier = new File (chemin);
        
        // Tester l'existence du fichier.
        if (!fichier.exists()) { throw new FileNotFoundException(); }
        
        @SuppressWarnings("resource")
		BufferedReader bufferedReader = new BufferedReader(new FileReader(fichier));
        String ligne = bufferedReader.readLine();
        
        int nb_cases = Integer.parseInt(ligne);
        
        
        // test: pour tester nb_cases = 3 x 2^n
        double test = (double) nb_cases / 3;
        test =  Math.log(test) / Math.log(2);
        this.n = (int) test;
        test = test % 1;
        if (test != 0) { throw new Exception ("Le nombre de cases n'est pas correct!"); }
        
        int i = 0;
        
        this.casesVides = new ArrayList<Case>();
        this.plateau = new char[nb_cases][nb_cases];
        
        while ((ligne = bufferedReader.readLine()) != null) {
        	for (int j=0; j<nb_cases; j++) {
        		char id_courant = ligne.charAt(j);
        		
        		if (id_courant == 'A' || id_courant == caseVide) { 
        			this.casesVides.add(new Case (i, j));
        			this.plateau[i][j] = caseVide; 
        		} 
        		else if (id_courant == 'R' || id_courant == J1.id) { 
        			this.plateau[i][j] = J1.id; 
        			J1.Score++; 
        		} 
        		else if (id_courant == 'B' || id_courant == J2.id) {
        			this.plateau[i][j] = J2.id; 
        			J2.Score++;
        		} else {
        			if (nb_cases != i) { throw new Exception ("ID inconnu dans le fichier"); }
        		}
	        }
        	i++;
        } 
        bufferedReader.close(); 
        if (nb_cases != i) { throw new Exception ("Le nombre de lignes et le nombre de colonnes ne sont pas égaux!"); }
	 }

	
	 
	 // Méthodes

}




