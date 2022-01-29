import java.util.Random;
import java.util.ArrayList;

public abstract class Plateau {
	
	// Attribus
	protected char[][] plateau; // La surface du jeu. 
	protected int nb_cases; // Nombre de cases dans le plateau sur une seule dimension.
	protected Joueur J1, J2; // Les joueurs.
	protected char caseVide; // Le symbole de la case vide.
	protected ArrayList<Case> casesVides; // La liste des cases vides sur le plateau. 

	
	
	// Constructeur
	public Plateau (int n, boolean initVide, char caseVide, Joueur J1, Joueur J2) {
		
		if (n < 0) { // test n >= 0
			throw new NegativeArraySizeException();
		}
		
		this.nb_cases = (int) (3 * Math.pow(2, n)); // nb_cases = 3 x 2^n
		this.caseVide = caseVide;
		this.J1 = J1;
		this.J2 = J2;
		
		// Plateau vide par défaut.
		this.RemplirPlateau(initVide);
		
		this.casesVides = new ArrayList<Case>();
		// Ajouter les cases vides à la liste.
		for (int i=0; i<this.nb_cases; i++) {
			for (int j=0; j<this.nb_cases; j++) {
				if (this.plateau[i][j] == this.caseVide) {
					Case c = new Case (i, j);
					this.casesVides.add(c);
				}
			}
		}
	}
	
	
	
	// Méthodes
	 // Initialiser le plateau vide ou rempli partiellement.
	public void RemplirPlateau (boolean initVide) {
		
		this.plateau = new char[this.nb_cases][this.nb_cases];
		
		for (int i=0; i<nb_cases; i++) {
			for (int j=0; j<nb_cases; j++) {
				this.plateau[i][j] = this.caseVide;
			}
		}
		if (!initVide) {
			this.RemplirPlateauPartiel();
		}	
	}
	
	// Remplir le plateau partiellement.
	public void RemplirPlateauPartiel () { // TODO: à améliorer.
		
		int n = (int) Math.pow(this.nb_cases, 2) / 4; // 1/4 rouge, 1/4 bleu, et 1/2 vide. 
		int tmp = n;
		Case c = new Case (0, 0);
		Random r = new Random ();
		
		// Remplir les cases rouges par hasard.
		while (tmp != 0) {
			c.i = r.nextInt(this.nb_cases);
			c.j = r.nextInt(this.nb_cases);
			
			if (this.estCaseLibre(c)) {
				this.colorier_case(c, this.J1);
				tmp--;
			}
		}
		tmp = n;
		// Remplir les cases bleus par hasard.
		while (tmp != 0) {
			c.i = r.nextInt(this.nb_cases);
			c.j = r.nextInt(this.nb_cases);
			
			if (this.estCaseLibre(c)) {
				this.colorier_case(c, this.J2);
				tmp--;
			}
		}
	}
	
	// Si la case est valide (existe dans le plateau).
	public boolean estCaseExiste (Case c) { 
		return c.i >= 0 && c.i < this.nb_cases && c.j >= 0 && c.j < this.nb_cases; 
	}
	// Si la case vide.
	public boolean estCaseLibre (Case c) { 
		return this.plateau[c.i][c.j] == this.caseVide; 
	}
	
	// Ajouter un point au score du joueur passé en paramètre.
	public void addPoint (Joueur J) {
		if (J.id == this.J1.id) {
			this.J1.Score++;
		} else if (J.id == this.J2.id) {
			this.J2.Score++;
		}
	}
	// Enlever un point du score de l'autre joueur.
	public void suppAutrePoint (Joueur J) {
		if (J.id == this.J1.id) {
			this.J2.Score--;
		} else if (J.id == this.J2.id) {
			this.J1.Score--;
		}
	}
	
	// Retourner le score du joueur passéé en paramètre.
	public int CalculeScore (Joueur J) {
		
		if (this.J1.id == J.id) {
			return this.J1.Score;
		} else if (this.J2.id == J.id){
			return this.J2.Score;
		} else {
			return 0;
		}
	}
	
	// Colorier la case séléctionnée.
	public void colorier_case (Case c, Joueur J) {
		this.plateau[c.i][c.j] = J.id;
		this.addPoint(J);
	}
	
	// Supprimer la case choisit de la liste des cases vides.
	public void supp_case (Case c) {
		
		for (int i=0; i<this.casesVides.size(); i++) {
			if (this.casesVides.get(i).i == c.i && this.casesVides.get(i).j == c.j) {
				this.casesVides.remove(i);
				break;
			}
		}
	}
	
	// L'affichage du plateau sur le console.
	public void afficherPlateau () {
		
		int countL = 0;
		int countC = 0;
		
		for (int i=0; i<this.nb_cases; i++) {
			for (int j=0; j<this.nb_cases; j++) {
				System.out.print(this.plateau[i][j] + " ");
				countL++;
				if (countL%3 == 0) {
					System.out.print(" ");
				}
			}
			System.out.println("");
			countC++;
			if (countC%3 == 0) {
				System.out.println("");
			}
		}
		this.afficherScore();
		System.out.println("-------------------------------------------------");
	}
	
	// Affichage le score des joueurs à chaque coup.
	public void afficherScore () {
		System.out.println("J1 = " + this.J1.Score + ", J2 = " + this.J2.Score);
	}
	
	
	
	// Méthodes abstracts
	public abstract int EvalCase (Case c, Joueur J);
	public abstract Case chercher_meilleur_case (Joueur J);
	public abstract boolean Colorier (Case c, Joueur J);
	public abstract Case JouerGlouton (Case c, Joueur J);
	
	// méthode spécifique du jeu temeraire.
	public abstract Case chercher_meilleur_case_IA (Joueur J);
	public abstract Case JouerIATemeraire (Case c, Joueur J);
	
	
} // Fin Plateau.
