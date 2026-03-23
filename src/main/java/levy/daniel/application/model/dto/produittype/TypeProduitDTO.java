package levy.daniel.application.model.dto.produittype;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">CLASSE TypeProduitDTO.java :</p>
 * 
 * <p>DTO pour l'objet métier 
 * <code style="font-weight:bold;">TypeProduit</code>.
 * </p>
 * 
 * <p>
 * Cette classe modélise : 
 * un <span style="font-weight:bold;">DTO</span> pour transférer 
 * les données d'un TypeProduit provenant de la couche 
 * PRESENTATION (VUE ) vers les CONTROLLERS.
 * </p>
 * 
 * </div>
 *
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 29 décembre 2025
 */
public final class TypeProduitDTO {

	// ************************ATTRIBUTS************************************/
	
	/**
	 * "null"
	 */
	public static final String NULL = "null";
	
	/**
	 * ", "
	 */
	public static final String VIRGULE_ESPACE = ", ";
	
	/**
	 * ']'
	 */
	public static final char CROCHET_FERMANT = ']';
	
	/**
	 * ';'
	 */
	public static final char POINT_VIRGULE = ';';
	
	/**
	 * "typeProduit="
	 */
	public static final String TYPEPRODUIT = "typeProduit=";
	
	
	/* ----------------------------------------------------------------- */
	/**
	 * <style>p, ul, li {line-height : 1em;}</style>
	 * <div>
	 * <p>LOG : Logger : </p>
	 * <p>Logger pour Log4j (utilisant org.apache.logging.log4j).</p>
	 * <p>dépendances : </p>
	 * <ul>
	 * <li><code>org.apache.logging.log4j.Logger</code></li>
	 * <li><code>org.apache.logging.log4j.LogManager</code></li>
	 * </ul>
	 * </div>
	 */
	private static final Logger LOG = LogManager
			.getLogger(TypeProduitDTO.class);

	// ***************************METHODES********************************/
	
	 /**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * <p>private pour bloquer l'instanciation (classe utilitaire).</p>
	 * </div>
	 */
	private TypeProduitDTO() {
	        super();
	    }

	
	
	/* ========================= INNER CLASSES ==========================*/
	
	
	
	/**
	 * <div>
	 * <p>CLASSE INTERNE 
	 * <code style="font-weight:bold;">InputDTO</code> 
	 * pour modéliser les DTO 
	 * qui proviennent des VUES et rentrent dans les CONTROLLERS.</p>
	 * 
	 * <p style="font-weight:bold;">DTO d'ENTREE
	 *  dans la logique métier de l'application (création/modification) 
	 * <code>InputDTO</code>.</p>
	 * 
	 * <p>Ce DTO - ne comportant que des String - provient de la VUE 
	 * de l'Utilisateur, puis du CONTROLLER pour solliciter 
	 * les SERVICES (logique métier).</p>
	 * <p>C'est un CONTENEUR des éléments transmis par la VUE 
	 * de l'Utilisateur à l'application lorsque l'Utilisateur 
	 * demande une création ou une modification d'un Objet métier.</p>
	 * </div>
	 */
	public static final class InputDTO {
		
		// ********************ATTRIBUTS****************************/
		
		/**
		 * <div>
		 * <p>Libellé du type de produit 
		 * (exemple : "vêtement", "outillage", ...).
		 * </p>
		 * </div>
		 */
		private String typeProduit;
				

		// ********************CONSTRUCTEURS************************/
		
		/**
		 * <div>
		 * <p>Constructeur par défaut d'arité nulle.</p>
		 * </div>
		 */
		public InputDTO() {
            this(null);
        }

		
		
		/**
		 * <div>
		 * <p>Constructeur Complet.</p>
		 * </div>
		 *
		 * @param pTypeProduit : String : type de produit 
		 * comme "vêtement", "outillage", ...
		 */
		public InputDTO(final String pTypeProduit) {

			super();
			this.typeProduit = pTypeProduit;

		}


		
		// ********************METHODES*****************************/
		
		/**
		* {@inheritDoc}
		*/
		@Override
		public int hashCode() {
			return Objects.hash(this.getTypeProduit());
		}



		/**
		* {@inheritDoc}
		*/
		@Override
		public boolean equals(final Object pObject) {

			if (this == pObject) {
				return true;
			}

			if (!(pObject instanceof InputDTO other)) {
				return false;
			}

			return Objects.equals(this.getTypeProduit()
					, other.getTypeProduit());
		}


		
		/**
		* {@inheritDoc}
		*/
		@Override
		public String toString() {

			final StringBuilder finalBuilder = new StringBuilder();
			
			finalBuilder.append("InputDTO [");
			
			finalBuilder.append(TYPEPRODUIT);
			
			if (this.getTypeProduit() != null) {				
				finalBuilder.append(this.getTypeProduit());
			} else {
				finalBuilder.append(NULL);
			}

			finalBuilder.append(CROCHET_FERMANT);
			
			return finalBuilder.toString();
		}

		
		
		// ********************GETTERS/SETTERS**********************/

		/**
		 * <div>
		 * <p>Getter du type de produit 
		 * (exemple : "Vêtement", "outillage", ...).</p>
		 * </div>
		 *
		 * @return <code>this.typeProduit</code> : String
		 */
		public String getTypeProduit() {		
			return this.typeProduit;		
		}


		
		/**
		 * <div>
		 * <p>Setter du type de produit 
		 * (exemple : "Vêtement", "outillage", ...).</p>
		 * </div>
		 *
		 * @param pTypeProduit : String :
		 * valeur à passer à <code>this.typeProduit</code>.
		 */
		public void setTypeProduit(final String pTypeProduit) {		
			this.typeProduit = pTypeProduit;		
		}
		
	} // fin InputDTO.-----------------------------------------------------
	

	
	/**
	 * <div>
	 * <p>CLASSE INTERNE 
	 * <code style="font-weight:bold;">OutputDTO</code> 
	 * pour modéliser les DTO 
	 * qui, - venant de l'application -, 
	 * proviennent des CONTROLLERS et rentrent dans les VUES.</p>
	 * 
	 * <p style="font-weight:bold;">DTO de SORTIE
	 * (lecture par le CONTROLLER et les VUES)
	 * <code>OutputDTO</code>.</p>
	 * 
	 * <p>Ce DTO - ne comportant que des String + un ID (Long) - 
	 * provient du CONTROLLER (après sollicitation de la logique métier) 
	 * pour alimenter les VUES côté Utilisateur.</p>
	 * 
	 * <p>C'est un CONTENEUR des éléments d'un Objet métier 
	 * transmis par le CONTROLLER 
	 * à la VUE de l'Utilisateur en SORTIE de l'application.</p>
	 * </div>
	 */
	public static final class OutputDTO {

		// ********************ATTRIBUTS****************************/
		
		/**
		 * <div>
		 * <p>ID technique dans le stockage du type de produit.</p>
		 * </div>
		 */
		private Long idTypeProduit;
				
		/**
		 * <div>
		 * <p>type de produit 
		 * (exemple : "vêtement", "outillage", ...).</p>
		 * </div>
		 */
		private String typeProduit;
		
		/**
		 * <div>
		 * <p>List&lt;String&gt; des noms (libellés) des 
		 * SousTypeProduits enfants du TypeProduit.</p>
		 * </div>
		 */
		private List<String> sousTypeProduits;

		
		
		// ********************CONSTRUCTEURS************************/

		/**
		 * <div>
		 * <p>Constructeur par défaut d'arité nulle.</p>
		 * </div>
		 */
		public OutputDTO() {
            this(null, null, null);
        }
		
		

		/**
		 * <div>
		 * <p>Constructeur Complet.</p>
		 * </div>
		 *
		 * @param pIdTypeProduit : Long : 
		 * ID technique dans le stockage du type de produit.
		 * @param pTypeProduit : String : type de produit 
		 *(exemple : "vêtement", "outillage", ...).
		 * @param pSousTypeProduits : List&lt;String&gt; 
		 * des noms (libellés) des 
		 * SousTypeProduits enfants du TypeProduit.
		 */
		public OutputDTO(
				final Long pIdTypeProduit
					, final String pTypeProduit
						, final List<String> pSousTypeProduits) {

			super();
			this.idTypeProduit = pIdTypeProduit;
			this.typeProduit = pTypeProduit;
			this.sousTypeProduits = pSousTypeProduits;
		}

		// ********************METHODES*****************************/

		/**
		* {@inheritDoc}
		*/
		@Override
		public int hashCode() {

			/* ID. */
			final Long id = this.getIdTypeProduit();

			if (id != null) {
				return Objects.hash(id);
			}

			/* Fallback “business key” vers le métier si pas d'ID. */
			/* TypeProduit. */
			final String tp = this.getTypeProduit();
			
			return Objects.hash(tp);
		}



		/**
		* {@inheritDoc}
		*/
		@Override
		public boolean equals(final Object pObject) {

			if (this == pObject) {
				return true;
			}

			if (!(pObject instanceof OutputDTO other)) {
				return false;
			}

			/* ID. */
			final Long id = this.getIdTypeProduit();
			final Long otherId = other.getIdTypeProduit();

			if ((id != null) && (otherId != null)) {
				return Objects.equals(id, otherId);
			}
			
			/* Fallback “business key” vers le métier si pas d'ID. */
			/* TypeProduit. */
			return Objects.equals(
					this.getTypeProduit()
						, other.getTypeProduit());

		}


		
		/**
		* {@inheritDoc}
		*/
		@Override
		public String toString() {

			final StringBuilder finalBuilder = new StringBuilder();
			
			finalBuilder.append("OutputDTO [");

			finalBuilder.append("idTypeProduit=");
			
			if (this.getIdTypeProduit() != null) {				
				finalBuilder.append(this.getIdTypeProduit());
			} else {
				finalBuilder.append(NULL);
			}
			
			finalBuilder.append(VIRGULE_ESPACE);
			
			finalBuilder.append(TYPEPRODUIT);
			
			if (this.getTypeProduit() != null) {				
				finalBuilder.append(this.getTypeProduit());
			} else {
				finalBuilder.append(NULL);
			}

			finalBuilder.append(CROCHET_FERMANT);
			
			return finalBuilder.toString();
		}

		
		
		// ********************GETTERS/SETTERS**********************/

		/**
		 * <div>
		 * <p>Getter de l'ID technique 
		 * dans le stockage du type de produit.</p>
		 * </div>
		 *
		 * @return <code>this.idTypeProduit</code> : Long
		 */
		public Long getIdTypeProduit() {		
			return this.idTypeProduit;		
		}


		
		/**
		 * <div>
		 * <p>Setter de l'ID technique 
		 * dans le stockage du type de produit.</p>
		 * </div>
		 *
		 * @param pIdTypeProduit : Long :
		 * valeur à passer à <code>this.idTypeProduit</code>.
		 */
		public void setIdTypeProduit(final Long pIdTypeProduit) {		
			this.idTypeProduit = pIdTypeProduit;		
		}


	
		/**
		 * <div>
		 * <p>Getter du type de produit 
		 * (exemple : "vêtement", "outillage", ...).</p>
		 * </div>
		 *
		 * @return <code>this.typeProduit</code> : String
		 */
		public String getTypeProduit() {		
			return this.typeProduit;		
		}


		
		/**
		 * <div>
		 * <p>Setter du type de produit 
		 * (exemple : "vêtement", "outillage", ...).</p>
		 * </div>
		 *
		 * @param pTypeProduit : String :
		 * valeur à passer à <code>this.typeProduit</code>.
		 */
		public void setTypeProduit(final String pTypeProduit) {		
			this.typeProduit = pTypeProduit;		
		}


		
		/**
		 * <div>
		 * <p>Getter de la List&lt;String&gt; des noms (libellés) des 
		 * SousTypeProduits enfants du TypeProduit.</p>
		 * </div>
		 *
		 * @return <code>this.sousTypeProduits</code> : List&lt;String&gt;
		 */
		public List<String> getSousTypeProduits() {		
			return this.sousTypeProduits;		
		}


		
		/**
		 * <div>
		 * <p>Setter de la List&lt;String&gt; des noms (libellés) des 
		 * SousTypeProduits enfants du TypeProduit.</p>
		 * </div>
		 *
		 * @param pSousTypeProduits : List&lt;String&gt; :
		 * valeur à passer à <code>this.sousTypeProduits</code>.
		 */
		public void setSousTypeProduits(
				final List<String> pSousTypeProduits) {		
			this.sousTypeProduits = pSousTypeProduits;		
		}
		
		
	} // fin OutputDTO.----------------------------------------------------
				
}
