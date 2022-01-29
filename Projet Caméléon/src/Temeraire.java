
public class Temeraire extends Plateau {
	
	// Attributs
	protected Region R;
	
	
	
	// Constructeur
	public Temeraire(int n, boolean initVide, char caseVide, Joueur J1, Joueur J2) {
		super(n, initVide, caseVide, J1, J2);

		this.R = new Region (this);
	}
	

	
	// Méthodes
	// Retourner le nombre des cases qui peut J gagné s'il choisit la case c.
	public int EvalCase (Case c, Joueur J) {
		
		// Chercher l'id de l'autre joueur.
		char id_autre;
		if (J.id == this.J2.id) { id_autre = this.J1.id; } 
		else { id_autre = this.J2.id; }
		
		int nb1 = 0; // le nombre de points le joueur peut gagnés s'il choisit la case c.
		int nb2 = 0; // Le nombre des cases remplit autour de la case c.
		
		Region r1 = this.R.rechercheFeuille(c); // La région (feuille) de la case c.
		
		// Vérifier que la case c est bien vide.
		if (this.estCaseLibre(c)) {
			
			// Le point de la case séléctionnée.
			nb1++; 
			nb2++; 
			
			// On fait 9 itérations pour vérifier les cases autour de c.
			for (int i=-1; i<=1; i++) {
				for (int j=-1; j<=1; j++) {
					
					Case c_tmp = new Case (c.i+i, c.j+j);
					// Tester si la cases est bien dans le plateau.
					if (this.estCaseExiste(c_tmp)) {
						// Si oui, alors vérifier sa couleur (l'id).
						if (this.plateau[c_tmp.i][c_tmp.j] == id_autre) {
							
							// Chercher la rgion de la case temporaire (une des cases autour de c).
							Region r2 = this.R.rechercheFeuille(c_tmp);	
						
							if ((r1 != r2 && !r2.estAcquise(this)) || // Si hors région.
									(r1 == r2)) { // Si dans la même région.
								
								nb1++;
							}	
						}
						// Si la case temporaire est coloriée.
						if (this.plateau[c_tmp.i][c_tmp.j] != this.caseVide) {
							nb2++;
						}
					}
				}
			}
		}
		if (nb2 > nb1 && nb2 == 9) { return nb2; }
		return nb1;
	}
	
	// Chercher la case qui peut gagné le plus pour le joueur J en paramètre.
	public Case chercher_meilleur_case (Joueur J) {
		
		Case case_max = this.casesVides.get(this.casesVides.size()-1); 
		int score_max = this.EvalCase(case_max, J);
		int score_courant = score_max;
		
		// Vérifier tous les cases vides pour trouver la meilleur.
		for (int i=this.casesVides.size()-2; i>=0; i--) {
			// évaluer chaque case.
			score_courant = this.EvalCase(this.casesVides.get(i), J);
			// Chercher la meilleur case dans la liste des cases vides.
			if (score_courant > score_max) {
				score_max = score_courant;
				case_max = this.casesVides.get(i);
			}
		}
		System.out.println("Votre score sera: " + (J.Score+score_max));
		return case_max;
	}
	
	// Jouer Glouton avec Brave.
	public Case JouerGlouton (Case c, Joueur J) {

		Case meilleur = this.chercher_meilleur_case(J);
		this.supp_case(c);
		
		return meilleur;
	}
	
	
	// Retourner le nombre des cases que le joueur J peut gagner s'il choisit la case c
	// de manière plus intelligent de EvalCase().
	public int EvalCaseIA (Case c, Joueur J) {
		
		// Chercher l'id de l'autre joueur.
		char id_autre;
		if (J.id == this.J2.id) { id_autre = this.J1.id; } 
		else { id_autre = this.J2.id; }
		
		int nb1 = 0; // Le nombre de points le joueur peut gagnés s'il choisit la case c.
		int nb2 = 0; // Le nombre des cases remplit autour de la case c.
		
		// La région (feuille) de la case c.
		Region r1 = this.R.rechercheFeuille(c);
		
		// Vérifier que la case c est bien vide.
		if (this.estCaseLibre(c)) {
			
			// Le point de la case séléctionnée.
			nb1++; 
			nb2++; 
			
			// On fait 9 itérations pour vérifier les cases autour de c.
			for (int i=-1; i<=1; i++) {
				for (int j=-1; j<=1; j++) {
					
					Case c_tmp = new Case (c.i+i, c.j+j);
					// Tester si la cases est bien dans le plateau.
					if (this.estCaseExiste(c_tmp)) {
						// Si oui, alors vérifier sa couleur (l'id).
						if (this.plateau[c_tmp.i][c_tmp.j] == id_autre) {
							
							// Chercher la rgion de la case temporaire (une des cases autour de c).
							Region r2 = this.R.rechercheFeuille(c_tmp);	
						
							if ((r1 != r2 && !r2.estAcquise(this)) || // Si hors région.
									(r1 == r2)) { // Si dans la même région.
								
								nb1++;
							}	
						}
						// Si la case temporaire est coloriée.
						if (this.plateau[c_tmp.i][c_tmp.j] != this.caseVide) {
							nb2++;
						}
					}
				}
			}
			// Test pour vérifier si la case c est choisit par le joueur,
			// alors est-ce-que cette région sera acquise ou pas par le joueur? 
			boolean gagneRegion = true; 
			for (int i=r1.tete.i; i<r1.longueur_region; i++) {
				for (int j=r1.tete.j; j<r1.longueur_region; j++) {
					if (this.plateau[i][j] != J.id && i != c.i && j != c.j) {
						gagneRegion = false;
					}
				}
			}
			
			// Si le joueur a gagné la région par la case c,
			// alors vérifier les régiions voisines dans la région la plus grande.
			if (gagneRegion) {
				
				int nb_tmp = 0; // Nombre de régions acquises dans la région plus grande.
				int nb_tmp_autre = 0; // Nombre de régions acquises dans la région plus grande de l'autre joueur.

				do {
					r1 = this.R.recherche(c, r1.longueur_region*2);
					if (r1.haut_gauche.estAcquise(this)) {
						nb_tmp++;
						if (r1.haut_gauche.appatenant == id_autre) { nb_tmp_autre++; }
					}
					if (r1.haut_droite.estAcquise(this)) {
						nb_tmp++;
						if (r1.haut_droite.appatenant == id_autre) { nb_tmp_autre++; }
					}
					if (r1.bas_gauche.estAcquise(this)) {
						nb_tmp++;
						if (r1.bas_gauche.appatenant == id_autre) { nb_tmp_autre++; }
					}
					if (r1.bas_droite.estAcquise(this)) {
						nb_tmp++;
						if (r1.bas_droite.appatenant == id_autre) { nb_tmp_autre++; }
					}
					
					if (nb_tmp == 3) { // Acquise plus grande région.
						nb1 = nb1 + ((int) Math.pow(r1.haut_gauche.longueur_region, 2) * nb_tmp_autre);
					}

				} while (r1.longueur_region != this.R.longueur_region);	
			}
		}
		if (nb2 > nb1 && nb2 == 9) { return nb2; }
		return nb1;
	}
	
	// Chercher la meilleur cases en utilisant l'évaluation intelligent EvalCaseIA().
	public Case chercher_meilleur_case_IA(Joueur J) {
		
		Case case_max = this.casesVides.get(this.casesVides.size()-1); 
		int score_max = this.EvalCaseIA(case_max, J);
		int score_courant = score_max;
		
		// Vérifier tous les cases vides pour trouver la meilleur.
		for (int i=this.casesVides.size()-2; i>=0; i--) {
			
			score_courant = this.EvalCaseIA(this.casesVides.get(i), J);
			// Chercher la meilleur case dans la liste des cases vides.
			if (score_courant > score_max) {
				score_max = score_courant;
				case_max = this.casesVides.get(i);
			}
		}
		System.out.println("Votre score sera: " + (J.Score+score_max));
		return case_max;
	}
	
	// Jouer Temeraire avec la recherche intelligent.
	public Case JouerIATemeraire (Case c, Joueur J) {
		
		Case meilleur = this.chercher_meilleur_case_IA(J);
		this.supp_case(c);
		
		return meilleur;
	}

	 
	// Applique le coloriage de la case séléctionnée avec les changements.
	public boolean Colorier (Case c, Joueur J) {
		
		if (this.estCaseLibre(c)) {
			// Changer le couleur de la case.
			this.colorier_case(c, J);
			
			Region r1 = this.R.rechercheFeuille(c);
				
			// Changer les couleur autour de la case séléctionnée.
			for (int indice1=-1; indice1<=1; indice1++) {
				for (int indice2=-1; indice2<=1; indice2++) {
					
					Case c_tmp = new Case (c.i+indice1, c.j+indice2);
					// Faire les testes nécessaires.
					if (this.estCaseExiste(c_tmp)) {
						if (!this.estCaseLibre(c_tmp) && 
								this.plateau[c_tmp.i][c_tmp.j] != J.id) {
							
							Region r2 = this.R.rechercheFeuille(c_tmp);	
						
							if ((r1 != r2 && !r2.estAcquise(this)) || // Si hors région.
									(r1 == r2)) { // Si dans la même région.
								
								// Si tous les tests sont vrais alors,
								// colorier la case gagnée et supprimer un point de l'autre.
								this.colorier_case(c_tmp , J);
								this.suppAutrePoint(J);
							}
						}
					}
				}
			}
			// Si la case séléctionnée c fait sa région aqcuise alors vérifier les régions les plus grandes récursivement.
			this.R.RemplirRegion(this, J); 
			
			// Afficher le jeu dans le console.
			this.afficherPlateau();
			
			return true;
		} else {
			return false;
		}
	}




} // Fin Temeraire.
