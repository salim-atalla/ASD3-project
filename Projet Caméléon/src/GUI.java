import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;



@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener {
	
	// Attributs
	private JButton[][] boutons;
	private JButton meilleurCase;
	private Plateau p;
	
	private boolean clk;
	private JLabel ResultatJ1, ResultatJ2;
	private JPanel Res;
	private Container panel;
	private JPanel Jeu;
	private JPanel menu;
	
	private static Scanner scan = new Scanner (System.in);
	private static boolean unJoueur = false, TemeraireIA = false;
	
	// Constructeur
	public GUI (Plateau p) {	
		
		this.setTitle("Projet Caméléon");
		this.setSize(500, 550);
		this.setResizable(true);
		this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    
	    
	    // Initialiser les composants.
	    this.p = p;
	    
	    this.ResultatJ1 = new JLabel("Score player1:   " + this.p.J1.Score, JLabel.LEFT);
	    this.ResultatJ2 = new JLabel("Score player2:   " + this.p.J2.Score, JLabel.LEFT);
	    this.Res = new JPanel();
	    this.Res.setLayout (new GridLayout ());
	    this.Res.add(ResultatJ1);
	    this.Res.add(ResultatJ2);
	    
	    this.Jeu = new JPanel ();
	    this.Jeu.setLayout (new GridLayout (p.nb_cases, p.nb_cases));
	    
	    this.boutons = new JButton[p.nb_cases][p.nb_cases];
	    
	    for (int i=0; i<p.nb_cases; i++) {
	    	for (int j=0; j<p.nb_cases; j++) {
	    		
	    		this.boutons[i][j] = new JButton();
	    		this.boutons[i][j].setActionCommand(i + "," + j);
	    		Case c = new Case (i, j);
	    		this.colorier_case_gui (c);
	    		this.Jeu.add(this.boutons[i][j]);
	    		this.boutons[i][j].addActionListener(this);
	    	}
	    }
	    this.meilleurCase = new JButton ("Help");
	    this.meilleurCase.setActionCommand("help");
	    this.meilleurCase.addActionListener(this);
	    this.menu = new JPanel(new GridLayout (2, 1));
	    
	    this.menu.add(this.Res, BorderLayout.NORTH);
	    this.menu.add(this.meilleurCase, BorderLayout.SOUTH);
	    
	    this.panel = this.getContentPane();
	    this.panel.setLayout(new BorderLayout());
	    this.panel.add(this.Jeu, BorderLayout.CENTER);
	    this.panel.add(this.menu, BorderLayout.SOUTH);
	    
	    this.clk = true;
	    this.setVisible(true);
	}
	
	
	
	// Méthodes
	public void Jeu2Joueur (Case c_commande) {
		
		// Gérer le tour entre les joueurs.
		Joueur J;
		if (this.clk) { J = this.p.J1; } else { J = this.p.J2; }
		
		// Contôle du clickage sur des cases déjà occupées.
		if (p.estCaseLibre(c_commande)) {
			// Appliquer le coloriage.
			this.Colorier_gui(c_commande, J); // Choisir le Jeu.
			// Régler le tour.
			this.clk = !this.clk;
		}
	}

	public void Jeu1Joueur (Case c_commande) {

		// Contôle du clickage sur des cases déjà occupées.
		if (this.p.estCaseLibre(c_commande)) {
			Case c;
			// Appliquer le coloriage.
			this.Colorier_gui(c_commande, this.p.J1); // Joueur
			if (TemeraireIA) { c = this.p.JouerIATemeraire(c_commande, this.p.J2); }
			else { c = this.p.JouerGlouton(c_commande, this.p.J2); }
			this.Colorier_gui(c, this.p.J2); // Ordinateur
		}
	}
	
	// Changer la couleur d'une case sur la graphique.
	public void colorier_case_gui (Case c) {
		
		if (p.plateau[c.i][c.j] == this.p.caseVide) {
			this.boutons[c.i][c.j].setBackground(Color.white);
		} else if (p.plateau[c.i][c.j] == this.p.J1.id) {
			this.boutons[c.i][c.j].setBackground(Color.red);
		} else if (p.plateau[c.i][c.j] == this.p.J2.id) {
			this.boutons[c.i][c.j].setBackground(Color.blue);
		}
	}
	
	public void regler_plateau () {
		for (int i=0; i<this.p.nb_cases; i++) {
			for (int j=0; j<this.p.nb_cases; j++) {
				Case c = new Case (i, j);
				this.colorier_case_gui(c);
			}
		}
	}
	
	// Appliquer le coloriage sur la gui selon les règles.
	public void Colorier_gui (Case c, Joueur J) {
		
		// Appliquer le coloriage dans le tableau (back-end), 
		if (this.p.Colorier(c, J)) { // Si vrai alors appliquer aussi sur la gui.
			// Changer le couleur de la case.
			this.colorier_case_gui(c);
			
			// Changer les couleur autour de la case séléctionnée.
			for (int indice1=-1; indice1<=1; indice1++) {
				for (int indice2=-1; indice2<=1; indice2++) {
					Case c_tmp = new Case (c.i+indice1, c.j+indice2);
					if (this.p.estCaseExiste(c_tmp)) {
						this.colorier_case_gui(c_tmp);
					}
				}
			}
		}
		this.regler_plateau();
	}
	
	// Afficher un message à la fin du jeu.
	public void MessageGameOver() {
		 
		if (this.p.J1.Score + this.p.J2.Score == (int) Math.pow(this.p.nb_cases, 2)) {
			String message = " ";
			if (this.p.J1.Score > this.p.J2.Score) {
				message = "GAME OVER: Player1 win!";
			} else if (this.p.J2.Score > this.p.J1.Score) {
				message = "GAME OVER: Player2 win!";
			} else {
				message = "GAME OVER: Player1 and Player2 are equal.";
			}
			System.out.println(message);
			JOptionPane.showInternalMessageDialog(this.panel, message);
			this.meilleurCase.setEnabled(false);
		}
	}

	/**************************************ACTION LISTENER*******************************************/
	
	public void actionPerformed (ActionEvent e) {
		
		// Traduire les commandes.
		String eCommande = e.getActionCommand();
		
		if (eCommande == "help") {
			Joueur J = this.p.J1;
			Case c;
			
			if (TemeraireIA) { c = this.p.chercher_meilleur_case_IA(J); }
			else { c = this.p.chercher_meilleur_case(J); }

			this.boutons[c.i][c.j].setBackground(Color.green);
			
		} else {
			String i_str = "", j_str = "";
			boolean b = false;
			
			// Chercher i et j dans la commande. (ex: commande = "i,j" -> resultats: "i" et "j").
			for (int indice=0; indice<eCommande.length(); indice++) {
				// Chercher le virgule qui sépare i et j.
				if (eCommande.charAt(indice) == ',') { b = true; continue; }
				// Construire i et j.
				if (!b) { i_str = i_str + eCommande.charAt(indice); }
				else { j_str = j_str + eCommande.charAt(indice); }	
			}
			int i = Integer.parseInt(i_str);
			int j = Integer.parseInt(j_str);
			
			Case c_commande = new Case (i, j);
			
			if (unJoueur) { this.Jeu1Joueur (c_commande); } 
			else { this.Jeu2Joueur (c_commande); } 

			
			// Mise à jour les scores.
			this.ResultatJ1.setText("Score player1:   " + this.p.J1.Score);
			this.ResultatJ2.setText("Score player2:   " + this.p.J2.Score);
			
			// Message: GAME OVER
			this.MessageGameOver();
		}
	}

	/**************************************Méthodes Statiques*******************************************/
	
	public static void charInvalide () throws Exception {
		throw new Exception("Caractere invalide!");
	}
	
	public static boolean estBrave () throws Exception {
		
		System.out.println("Enter T to play Temeraire, or B to play Brave..");
		char choix = scan.next().charAt(0);
		if (choix != 'B' && choix != 'b' && choix != 'T' && choix != 't') { charInvalide(); }
		return choix == 'B' || choix == 'b';
	}
	
	public static boolean estTemeraireIA() throws Exception {

		System.out.println("Which version of the Temeraire game you prefer (G for glouton or I for IA)");
		char choix = scan.next().charAt(0);
		if (choix != 'G' && choix != 'g' && choix != 'I' && choix != 'i') { charInvalide(); }
		return choix == 'I' || choix == 'i';
	}
	
	public static boolean initVide () throws Exception {
		
		// Choisir de remplir le plateau partiellement ou pas.
		System.out.println("Fill the tray partially (Y/N)?");
		char choix = scan.next().charAt(0);
		if (choix != 'Y' && choix != 'y' && choix != 'N' && choix != 'n') { charInvalide(); }
		return choix == 'N' || choix == 'n';
	}
	
	public static boolean estUnJoueur () throws Exception {
		
		// Choisir le nombre de joueurs (1 ou 2).
		System.out.println("Enter 1 for one player or 2 for two players..");
		char choix = scan.next().charAt(0);
		if (choix != '1' && choix != '2') { charInvalide(); }
		return choix == '1';
	}
	
	public static int TailleDuJeu () {
		
		// Choisir la taille du jeu.
		System.out.println("Enter the size of the game (ex: 2)..");
		return scan.nextInt();
	}
	
	public static Plateau creerJeu (int n, boolean initVide, char caseVide, Joueur J1, Joueur J2) throws Exception {
		
		Plateau p;
		if(estUnJoueur()) { unJoueur = true; }
		if (estBrave()) {
			p = new Brave (n, initVide, caseVide, J1, J2);
		} else {
			p = new Temeraire (n, initVide, caseVide, J1, J2);
			if (unJoueur) {
				if (estTemeraireIA()) { TemeraireIA = true; }
			}
		}
		return p;
	}
	
	public static Plateau JeuDuFichier (char caseVide, Joueur J1, Joueur J2) throws Exception {
		
		Plateau p;
		System.out.println("Enter the name of the file (with the extension)");
		String nomFichier = scan.next();
		
		Fichier fichier = new Fichier (nomFichier, caseVide, J1, J2);
		int n = fichier.n;

		p = creerJeu(n, true, caseVide, J1, J2);
		p.plateau = fichier.plateau;
		p.casesVides = fichier.casesVides;
		
		return p;
	}
	
	public static Plateau InitialiserJeu (char caseVide, Joueur J1, Joueur J2) throws Exception {
		return creerJeu(TailleDuJeu(), initVide(), caseVide, J1, J2);
	}
	
	public static Plateau Menu(char caseVide, Joueur J1, Joueur J2) throws Exception {
		
		// Déclaration des paramètres du jeu.
		Plateau p;
		System.out.println("Do you want to initialize the tray from a file (Y/N)?");
		char choix = scan.next().charAt(0);
		if (choix != 'Y' && choix != 'y' && choix != 'N' && choix != 'n') { charInvalide(); }
		
		if (choix == 'Y' || choix == 'y') { p = JeuDuFichier (caseVide, J1, J2); }
		else { p = InitialiserJeu (caseVide, J1, J2); }

		return p;
	}

	/*******************************************MAIN*************************************************/
	
	public static void main(String[] args) throws Exception {
		
		char caseVide = 'O';
		Joueur J1 = new Joueur ('R');
		Joueur J2 = new Joueur ('B');
		
		if (J1.id == J2.id || J1.id == caseVide || J2.id == caseVide) {
			throw new Exception ("Les symboles de joueur1,  joueur2 et la case vide doivent être distincts");
		}
		
		
		// Commencer le jeu.
		@SuppressWarnings("unused")
		GUI gui = new GUI (Menu(caseVide, J1, J2));

	    
	} // Fin main.
	
} // Fin GUI.


